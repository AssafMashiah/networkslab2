import java.util.ArrayList;


public class FriendService implements IFriendService
{
	// used for trace messages
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	private ArrayList<FriendInfo> m_FriendInfoList;
	private FriendInfo m_MyInfo;
	
	public FriendService()
	{
		
	}
	
	public FriendService(FriendInfo myInfo)
	{
		m_MyInfo = new FriendInfo(myInfo);
		m_FriendInfoList = new ArrayList<FriendInfo>();
	}
	
	public FriendService(String nickname, String IP, int port)
	{
		m_MyInfo = new FriendInfo(nickname, IP, port);
		m_FriendInfoList = new ArrayList<FriendInfo>();
	}
	
	@Override
	public void AddMeAsFriendRequest(FriendInfo[] friendsInfo) 
	{
		// TODO: Need Proxy
		tracer.TraceToConsole("Asking to Be a friend!");
	}

	@Override
	public String GetFriendsListPage() 
	{
		tracer.TraceToConsole("Retreaving the friends list page");
		// TODO Auto-generated method stub
		
		tracer.TraceToConsole("Friend list page was recived!");
		return null;
	}

	@Override
	public String AddFriendSource(String friendIP) 
	{
		String friendInLine = GetFriendsInLine();
		
		// add me as a friend using the friendIP I got in this function as an argument
		return null;
	}

	@Override
	public void RemoveFriend(String IP) 
	{
		for(FriendInfo info : m_FriendInfoList)
		{
			if(info.IP.equals(IP))
			{
				tracer.TraceToConsole(String.format("Friend %s was removed", info.NickName));
				m_FriendInfoList.remove(info);
			}
		}
		
	}

	@Override
	public String LookForFriends() 
	{
		// TODO Need Proxy
		return null;
	}

	@Override
	public String[] GetFriends() 
	{	
		int lenght = m_FriendInfoList.size();
		
		String[] retVal = new String[lenght];
		
		for(int i = 0 ; i < lenght ; i++)
		{
			String friend = m_FriendInfoList.get(i).toString();
			retVal[i] = friend;
		}
		
		return retVal;
	}
	
	public String GetFriendsInLine() 
	{
		StringBuilder sb = new StringBuilder();
		
		int lenght = m_FriendInfoList.size();
		
		for(int i = 0 ; i < lenght ; i++)
		{
			String friend = m_FriendInfoList.get(i).toString();
			sb.append(friend);
			
			if(i != lenght - 2)
			{
				sb.append(";");
			}
		}
		
		return sb.toString();
	}

	@Override
	public void AckFriendRequest(String friends) 
	{		
		// TODO: Need the Proxy
		tracer.TraceToConsole("Ack sent!");
	}
}
