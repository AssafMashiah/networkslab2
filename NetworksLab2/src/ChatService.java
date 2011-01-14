import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ChatService implements IChatService
{
	private static ChatService m_Instance = null;
	
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	private ArrayList<ChatBuddyData> m_ChatData;
	
	private ChatService()
	{
		m_ChatData = new ArrayList<ChatBuddyData>();
//		m_ChatData.add(new ChatBuddyData("jan", "1.1.1.1", "The enterprise is sinking"));
//		m_ChatData.add(new ChatBuddyData("pekard", "1.1.1.2", "no, it aint"));
//		m_ChatData.add(new ChatBuddyData("numberOne", "1.1.1.3", "going to the infarmary hit on doc"));
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
			
			switch (functionName) 
			{
			case get_chat_data:
				retVal.append(getChatData());
				break;
			case get_chat_page:
				retVal.append(getChatPage());
				break;
			case send_message:
				retVal.append(sendMessage(params[0]));
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
		t.AddValueToTemplate("CHAT_DATA", getChatData());
		
		tracer.TraceToConsole("Chat page was generated!");
		return t.CompileTemplate();
	}
	
	/**
	 * generates the chat data
	 * @return
	 */
	private String getChatData()
	{
		tracer.TraceToConsole("Genrating the chat data");
		
		StringBuilder sb = new StringBuilder();
		
		for (ChatBuddyData data : m_ChatData)
		{
			sb.append("<div class=\"timestamp chatdata\">[");
			sb.append(data.TimeStamp);
			sb.append("]&nbsp;</div>");
			sb.append("<div class=\"fromWho chatdata\">");
			sb.append(data.TitleName);
			sb.append("</div>");
			sb.append("<div class=\"chatdata\">&nbsp;");
			sb.append(data.Message);
			sb.append("</div><div class=\"padder\"></div><br>\n");
		}
		
		return sb.toString();
	}
	
	/**
	 * Sends the given msg parameter to all friends by calling the friend’s function
	 * @param message
	 * @throws HttpResponseParsingException 
	 * @throws HttpHeaderParsingException 
	 * @throws IOException 
	 * @throws HttpProxyException 
	 * @throws UnknownHostException 
	 * @return (new) Chat data 
	 */
	private String sendMessage(String message) throws UnknownHostException, HttpProxyException, IOException, HttpHeaderParsingException, HttpResponseParsingException
	{
		// add my message to log
		newMessage(message, "me, dummy!");
		
		// scan all friend and send to their proxy newMessage
		for (FriendInfo friend : FriendService.get_instance().GetFriends())
		{
			tracer.TraceToConsole(String.format("Sending message to %s:%s", friend.IP, friend.Port));
			
			ChatServiceProxy prox = new ChatServiceProxy(friend.IP, friend.Port);
			prox.newMessage(message);
		}
		
		return getChatData();
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
