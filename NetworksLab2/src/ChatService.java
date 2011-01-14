import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;

public class ChatService implements IChatService
{
	private static ChatService m_Instance = null;
	
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	private ArrayList<ChatBuddyData> m_ChatData;
	
	private ChatService()
	{
		m_ChatData = new ArrayList<ChatBuddyData>();
	}

	public static synchronized ChatService get_instance()
	{
		if (m_Instance == null) {
			m_Instance = new ChatService();
		}

		return m_Instance;
	}
	
	public String callFunction(Functions functionName, String[] params) throws HttpServiceException 
	{
		StringBuilder retVal;
		
		tracer.TraceToConsole(String.format("Now calling: %s", functionName));
		try
		{
			retVal = new StringBuilder();
			boolean showTS = true;
			
			switch (functionName) 
			{
			case get_chat_data:
				// if we have a paramater, we remove the timestamps
				if (params.length > 0)
				{
					showTS = false;
				}
				
				retVal.append(getChatData(showTS));
				break;
			case get_chat_page:
				retVal.append(getChatPage());
				break;
			case send_message:
				// if we have a paramater, we remove the timestamps
				if (params.length > 2)
				{
					showTS = false;
				}
				
				retVal.append(sendMessage(params[0], showTS));
				break;
			case new_message:
				newMessage(params[0], params[1]);
				break;
			default:
				// this is a 404 error, it should not happen (right now no code
				// because it cannot happen at this place as we get enum as input)
			}
		}
		catch (Exception e)
		{
			throw new HttpServiceException("Internal error in chat service", e);
		}
		
		return retVal.toString();
	}
	
	private String getChatPage() throws IOException
	{
		tracer.TraceToConsole("Genrating the chat page");
		
		// replace place holders in template
		HTMLTemplate t = new HTMLTemplate("chat_page.html");
		t.AddValueToTemplate("CHAT_DATA", getChatData(true));
		
		tracer.TraceToConsole("Chat page was generated!");
		return t.CompileTemplate();
	}
	
	/**
	 * generates the chat data
	 * @return
	 * @throws ParseException 
	 */
	private String getChatData(boolean showTimestamp)
	{
		tracer.TraceToConsole("Genrating the chat data");
		
		StringBuilder sb = new StringBuilder();
		
		for (ChatBuddyData data : m_ChatData)
		{
			String leftMost = " leftMost";
			if (showTimestamp)
			{
				sb.append(String.format("<div class=\"timestamp chatdata%s\">[%s]</div>", leftMost, data.TimeStamp));
				leftMost = "";
			}
			
			sb.append(String.format("<div class=\"fromWho chatdata%s\">%s</div>", leftMost, data.TitleName));
			sb.append("<div class=\"chatdata\">");
			sb.append(data.Message);
			sb.append("</div><div class=\"padder\"></div><br>\n");
		}

		if (!showTimestamp && sb.length() > 0)
		{
			ChatBuddyData lastMessage = m_ChatData.get(m_ChatData.size() - 1);
			if (lastMessage.IsMessageOld())
			{
				sb.append("<div class=\"timestamp chatdata leftMost\">Last message received on ");
				sb.append(lastMessage.TimeStamp);
				sb.append("</div>\n");
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Sends the given msg parameter to all friends by calling the friend’s function
	 * @param message
	 * @param showTS should we show the timestamp at the returned data?
	 * @throws HttpResponseParsingException 
	 * @throws HttpHeaderParsingException 
	 * @throws IOException 
	 * @throws HttpProxyException 
	 * @throws UnknownHostException 
	 * @return (new) Chat data 
	 */
	private String sendMessage(String message, boolean showTS)
	{
		// add my message to log
		newMessage(message, "me, dummy!");
		
		// scan all friend and send to their proxy newMessage
		FriendInfo[] friends = FriendService.get_instance().GetFriends();
		for (int i = 0; i < friends.length; i++)
		{
			FriendInfo friend = friends[i];
			
			tracer.TraceToConsole(String.format("Sending message to %s:%s", friend.IP, friend.Port));
			
			ChatServiceProxy prox = new ChatServiceProxy(friend.IP, friend.Port);
			try
			{
				prox.newMessage(message);
			}
			catch (Exception e)
			{
				tracer.TraceToConsole("Failed to send message!! removing friend");
				FriendService.get_instance().RemoveFriend(friend.IP, friend.Port);
			}
		}
		
		return getChatData(showTS);
	}
	
	/**
	 * Adds a new message
	 * @param message The message
	 * @param fromWho The IP of sender
	 */
	private void newMessage(String message, String fromWho)
	{
		tracer.TraceToConsole(String.format("\nA new message has arrived\nMessage data: %s\nMessage Sender: %s", message, fromWho));
		
		// if the message is from ourselves
		if (fromWho.equals("me, dummy!"))
		{
			FriendInfo me = FriendService.get_instance().GetMyInfo();
			m_ChatData.add(new ChatBuddyData(me.NickName, me.IP, message));
		}
		else
		{
			// scan all friend and enter new message
			for (FriendInfo friend : FriendService.get_instance().GetFriends())
			{
				if (friend.IP.equals(fromWho))
				{
					m_ChatData.add(new ChatBuddyData(friend.NickName, fromWho, message));
					break;
				}
			}
		}
	}
}
