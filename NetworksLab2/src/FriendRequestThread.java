

public class FriendRequestThread implements Runnable 
{
	private final String EXPECTED_PREFIX = "Be my friend? ";
	
	// used for trace messages
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	private String m_Data; 
	
	public FriendRequestThread(byte[] data)
	{
		int i = 0;
		for (; i < data.length; i++)
		{
			if (data[i] == 0)
			{
				break;
			}
		}
		
		m_Data = new String(data, 0, i);;
	}
	
	@Override
	public void run() 
	{
		// trace
		tracer.TraceToConsole("Friend Request started");
		
		if (m_Data.startsWith(EXPECTED_PREFIX))
		{
			String secondHalf = m_Data.substring(EXPECTED_PREFIX.length());
			// bob's IP and Port
			String[] IPAndPort = secondHalf.split(":");
			
			tracer.TraceToConsole(String.format("Start friendship protocol with %s:%s", IPAndPort[0], IPAndPort[1]));
			
			// call bob's proxy for add_me_as_a_friend
			FriendServiceProxy proxy = new FriendServiceProxy(IPAndPort[0], Integer.parseInt(IPAndPort[1]));
			try 
			{
				proxy.AddMeAsAFriendRequest(FriendService.get_instance().GetFriendsInOneLine(true));
			} 
			catch (Exception e) 
			{
				tracer.TraceToConsole("Something bad happened while adding a friend :(");
			}
		}
		else
		{
			tracer.TraceToConsole("Invalid friend request prefix");
		}
	}
}
