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
			
			switch (functionName) {
			case get_chat_page:
				break;
			case send_message:
				break;
			case new_message:
				break;
			default:
				// this is a 404 error, it should not happen (right now no code
				// because it cannot happen at this place as we get enum as input)
			}
		}
		catch (Exception e)
		{
			throw new HttpServiceException("Internal error in commands service", e);
		}
		
		return retVal.toString();
	}
	
	/**
	 * Sends the given msg parameter to all friends by calling the friend’s function
	 * @param massage
	 */
	private void sendMassage(String massage)
	{
		
	}
	
	private String newMassage(String massage)
	{
		String retVal = null;
		
		
		
		return retVal;
	}
}
