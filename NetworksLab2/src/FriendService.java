import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class FriendService implements IFriendService
{
	// used for trace messages
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	private ArrayList<FriendInfo> m_FriendInfoList;
	private FriendInfo m_MyInfo;
	
	private static FriendService m_Instance = null;
	
	private FriendService() 
	{
	}

	public static synchronized FriendService get_instance() 
	{
		if (m_Instance == null) {
			m_Instance = new FriendService();
		}

		return m_Instance;
	}
	
	private FriendService(FriendInfo myInfo)
	{
		m_MyInfo = new FriendInfo(myInfo);
		m_FriendInfoList = new ArrayList<FriendInfo>();
	}
	
	private FriendService(String nickname, String IP, int port)
	{
		m_MyInfo = new FriendInfo(nickname, IP, port);
		m_FriendInfoList = new ArrayList<FriendInfo>();
	}
	
	@Override
	public String callFunction(Functions functionName, String[] params)
			throws HttpServiceException 
	{
		StringBuilder retVal;
		try
		{
			retVal = new StringBuilder();
			
			switch (functionName) {
			case ack_friend_request:
				AckFriendRequest(params[0]);
				break;
			case add_friend_source:
				AddFriendSource(params[0], Integer.parseInt(params[1]));
				break;
			case add_me_as_friend_request:
				AddMeAsFriendRequest(params[0]);
				break;
			case get_friends:
				retVal.append(GetFriendsInOneLine());
				break;
			case get_Friends_list_page:
				break;
			case look_for_friends:
				break;
			case remove_friend:
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

	
	private void AddMeAsFriendRequest(String friendsList) 
	{
		// Split string on ;
		String[] tempfriends = friendsList.split(";");
		
		FriendInfo firstOne = new FriendInfo(tempfriends[0]);
		tracer.TraceToConsole(String.format("%s is asking to Be a friend!", firstOne));
		
		// Add the first one if not already in the list
		if (!m_FriendInfoList.contains(firstOne) && !m_MyInfo.equals(firstOne))
		{
			m_FriendInfoList.add(firstOne);
			
			// build my own friend list
			String myFriendList = GetFriendsInOneLine();
			String addedIP = firstOne.IP;
			int addedPort = firstOne.Port;
			
			// the ack will be sent via proxy (we activate the ack on the friend
			tracer.TraceToConsole(String.format("Friend added, sending ACK to %s:%d", addedIP, addedPort));
			
			// TODO: add proxy call with myFriendList (ACK!!)
			
		}
		
		ArrayList<FriendInfo> newFriends = new ArrayList<FriendInfo>();
		for (int i = 1; i < tempfriends.length; i++)
		{
			newFriends.add(new FriendInfo(tempfriends[i]));
		}
		
		// Send friend requests to all the rest of the list, if required (via add friend source)
		for(FriendInfo newFriend : newFriends)
		{
			if (!m_FriendInfoList.contains(newFriend) && !m_MyInfo.equals(firstOne))
			{
				// proxy call of add friend info to this friend
			}
		}
	}

	private void AckFriendRequest(String friends) throws HttpServiceException 
	{
		// Split string on ;
		String[] tempfriends = friends.split(";");
		
		// builds the new friend list (without the first one)
		ArrayList<FriendInfo> newFriends = new ArrayList<FriendInfo>();
		for (int i = 1; i < tempfriends.length; i++)
		{
			newFriends.add(new FriendInfo(tempfriends[i]));
		}
		
		FriendInfo firstOne = new FriendInfo(tempfriends[0]);
		
		if (m_FriendInfoList.contains(firstOne))
		{
			throw new HttpServiceException("Invalid logic encountered");
		}
		
		m_FriendInfoList.add(firstOne);

		// we got an ACK, we need to start adding a new friend (in a new thread)
		
		// we need to OK the ACK - this happens automatically via the service
	}
	
	private String GetFriendsListPage() 
	{
		tracer.TraceToConsole("Genrating the friends list page");
		
		// need to load the template
		
		// iterate and build a friend row for each friend
		
		// replace place holders in template
		
		// compile template
		
		tracer.TraceToConsole("Friend list page was generated!");
		return null;
	}

	public String AddFriendSource(String friendIP, int friendPort) 
	{
		String friendsList = GetFriendsInOneLine();
		
		// use proxy to call add_me_as_a_friend on the target IP
		
		return null;
	}

	public void RemoveFriend(String IP, int port) 
	{
		for(FriendInfo info : m_FriendInfoList)
		{
			if(info.IP.equals(IP) && info.Port == port)
			{
				tracer.TraceToConsole(String.format("Friend %s was removed", info.NickName));
				m_FriendInfoList.remove(info);
			}
		}
		
	}

	public void LookForFriends() throws UnknownHostException 
	{
		
		String payload = String.format("Be my friend? %s:%d", InetAddress.getLocalHost().getHostAddress(), 1);
		
		// create a UDP datagram and broadcast the payload
		
	}

//	private String[] GetFriends() 
//	{	
//		// get the number of known friends
//		int length = m_FriendInfoList.size();
//		
//		// add them to a string array
//		String[] retVal = new String[length];
//		
//		for(int i = 0 ; i < length ; i++)
//		{
//			String friend = m_FriendInfoList.get(i).toString();
//			retVal[i] = friend;
//		}
//		
//		return retVal;
//	}
	
	private String GetFriendsInOneLine() 
	{
		StringBuilder sb = new StringBuilder();
		
		int length = m_FriendInfoList.size();
		
		for(int i = 0 ; i < length ; i++)
		{
			String friend = m_FriendInfoList.get(i).toString();
			sb.append(friend);
			
			if(i != length - 2)
			{
				sb.append(";");
			}
		}
		
		return sb.toString();
	}
}
