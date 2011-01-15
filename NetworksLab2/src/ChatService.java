import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChatService implements IChatService
{
	private static ChatService m_Instance = null;
	
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	private ArrayList<ChatBuddyData> m_ChatData;
	
	private LinkedHashMap<String, String> m_MessageConvertMap;
	
	private ChatService()
	{
		m_ChatData = new ArrayList<ChatBuddyData>();
		m_MessageConvertMap = new LinkedHashMap<String, String>();
		
		// bold
		m_MessageConvertMap.put("\\*([<?/?\\w+>?\\s?]*)\\*", "<b>$1</b>");
		// italic
		m_MessageConvertMap.put("_([<?/?\\w+>?\\s?]*)_", "<i>$1</i>");
		// strike
		m_MessageConvertMap.put("-([<?/?\\w+>?\\s?]*)-", "<strike>$1</strike>");
		// :p
		m_MessageConvertMap.put(":p|:P", "<img src=\"/images/tongue.gif\">");
		// ;-)
		m_MessageConvertMap.put(";-\\)", "<img src=\"/images/wink_nose.gif\">");
		// ;^)
		m_MessageConvertMap.put(";\\^\\)", "<img src=\"/images/wink_big_nose.gif\">");
		// :-|
		m_MessageConvertMap.put(":-\\|", "<img src=\"/images/straightface.gif\">");
		// ;)
		m_MessageConvertMap.put(";\\)", "<img src=\"/images/wink.gif\">");
		// x-(
		m_MessageConvertMap.put("x-\\(", "<img src=\"/images/angry.gif\">");
		// B-)
		m_MessageConvertMap.put("B-\\)", "<img src=\"/images/cool.gif\">");
		// =)
		m_MessageConvertMap.put("=\\)", "<img src=\"/images/equal_smile.gif\">");
		// :)
		m_MessageConvertMap.put(":\\)", "<img src=\"/images/smile.gif\">");
		// :D
		m_MessageConvertMap.put(":D", "<img src=\"/images/grin.gif\">");
		// ~@~
		m_MessageConvertMap.put("~@~", "<img src=\"/images/poop.gif\">");
		// :(|)
		m_MessageConvertMap.put(":\\(\\|\\)", "<img src=\"/images/monkey.gif\">");
		// :(
		m_MessageConvertMap.put(":\\(", "<img src=\"/images/frown.gif\">");		
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
				if (params.length > 2)
				{
					showTS = false;
				}
				
				retVal.append(getChatData(showTS, Long.parseLong(params[0])));
				break;
			case get_chat_page:
				retVal.append(getChatPage());
				break;
			case send_message:
				// if we have a paramater, we remove the timestamps
				if (params.length > 3)
				{
					showTS = false;
				}
				
				retVal.append(sendMessage(params[0], Long.parseLong(params[1]), showTS));
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
		t.AddValueToTemplate("CHAT_DATA", getChatData(true, 0));
		
		tracer.TraceToConsole("Chat page was generated!");
		return t.CompileTemplate();
	}
	
	/**
	 * generates the chat data
	 * @param showTimestamp Show the time stamps?
	 * @param lastUpdate will get changes after that time
	 * @return
	 */
	private String getChatData(boolean showTimestamp, long lastUpdate)
	{
		tracer.TraceToConsole("Genrating the chat data");
		
		StringBuilder sb = new StringBuilder();
		
		for (ChatBuddyData data : m_ChatData)
		{
			if (data.IsMessageNew(lastUpdate))
			{
				String leftMost = " leftMost";
				if (showTimestamp)
				{
					sb.append(String.format("<div class=\"timestamp chatdata%s\">[%s]</div>", leftMost, data.TimeStamp));
					leftMost = "";
				}
				
				sb.append(String.format("<div class=\"fromWho chatdata%s\">%s</div>", leftMost, data.TitleName));
				sb.append("<div class=\"chatdata\">");
				sb.append(processMessage(data.Message));
				sb.append("</div><div class=\"padder\"></div><br>\n");
			}
		}

		tracer.TraceToConsole("Show timestamp is: " + showTimestamp);
		
		if (!showTimestamp && m_ChatData.size() > 0 && sb.length() == 0)
		{
			ChatBuddyData lastMessage = m_ChatData.get(m_ChatData.size() - 1);
			if (lastMessage.IsMessageOld())
			{
				sb.append("_L<div class=\"timestamp chatdata leftMost\">Last message received on ");
				sb.append(lastMessage.TimeStamp);
				sb.append("</div>\n");
			}
		}
		
		return sb.toString();
	}
	
	private String processMessage(String message)
	{
		String pMessage = message;
		
		for (Map.Entry<String, String> entry : m_MessageConvertMap.entrySet())
		{
			pMessage = pMessage.replaceAll(entry.getKey(), entry.getValue());
		}
		
		return pMessage;
	}
	
	/**
	 * Sends the given msg parameter to all friends by calling the friend’s function
	 * @param message the message to send
	 * @param showTS should we show the timestamp at the returned data?
	 * @param lastUpdate The last update time of the page (will return all changes since)
	 * @throws HttpResponseParsingException 
	 * @throws HttpHeaderParsingException 
	 * @throws IOException 
	 * @throws HttpProxyException 
	 * @throws UnknownHostException 
	 * @return (new) Chat data 
	 */
	private String sendMessage(String message, long lastUpdate, boolean showTS)
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
		
		return getChatData(showTS, lastUpdate);
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
