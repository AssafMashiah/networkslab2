import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class FriendService implements IFriendService
{
	// used for trace messages
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	private ArrayList<FriendInfo> m_FriendInfoList;
	private FriendInfo m_MyInfo;
	private int m_DispatcherPort;
	private int m_FriendServerPort;
	
	private static FriendService m_Instance = null;
	
	private FriendService() 
	{
		m_FriendInfoList = new ArrayList<FriendInfo>();
	}

	public static synchronized FriendService get_instance() 
	{
		if (m_Instance == null) {
			m_Instance = new FriendService();
		}

		return m_Instance;
	}
	
	public void SetMyInfo(FriendInfo me)
	{
		m_MyInfo = new FriendInfo(me);
	}
	
	public FriendInfo GetMyInfo()
	{
		return new FriendInfo(m_MyInfo);
	}
	
	public void SetDispatcherPort(int port)
	{
		m_DispatcherPort = port;
	}
	
	public void SetFriendServerPort(int port)
	{
		m_FriendServerPort = port;
	}
	
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
				AddFriendSource(params[1], Integer.parseInt(params[0]));
				break;
			case add_me_as_friend_request:
				AddMeAsFriendRequest(params[0]);
				break;
			case get_friends:
				retVal.append(GetFriendsInOneLine(true));
				break;
			case get_friends_list_page:
				retVal.append(GetFriendsListPage());
				break;
			case look_for_friends:
				LookForFriends();
				break;
			case remove_friend:
				RemoveFriend(params[1], Integer.parseInt(params[0]));
				break;
			default:
				// this is a 404 error, it should not happen (right now no code
				// because it cannot happen at this place as we get enum as input)
			}
		}
		catch (Exception e)
		{
			throw new HttpServiceException("Internal error in friends service", e);
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
			String myFriendList = GetFriendsInOneLine(true);
			String addedIP = firstOne.IP;
			int addedPort = firstOne.Port;
			
			// the ack will be sent via proxy (we activate the ack on the friend
			tracer.TraceToConsole(String.format("Friend added, sending ACK to %s:%d", addedIP, addedPort));
			
			// Ack the request
			FriendServiceProxy proxy = new FriendServiceProxy(addedIP, addedPort);
			try
			{
				proxy.AckFriendRequest(myFriendList);
			}
			catch (Exception e)
			{
				// if Ack failed, remove the friend we just added
				tracer.TraceToConsole(String.format("Ack new friend request failed -> %s\nError was: %s", firstOne, e.toString()));
				this.RemoveFriend(firstOne);
			}
		}
		
		// now we add the rest of the friend
		
		ArrayList<FriendInfo> newFriends = new ArrayList<FriendInfo>();
		for (int i = 1; i < tempfriends.length; i++)
		{
			newFriends.add(new FriendInfo(tempfriends[i]));
		}
		
		// Send friend requests to all the rest of the list, if required (via add friend source)
		for(FriendInfo newFriend : newFriends)
		{
			String myFriendList = GetFriendsInOneLine(true);
			
			if (!m_FriendInfoList.contains(newFriend) && !m_MyInfo.equals(newFriend))
			{
				// proxy call of add friend info to this friend
				FriendServiceProxy proxy = new FriendServiceProxy(newFriend.IP, newFriend.Port);
				try {
					proxy.AddMeAsAFriendRequest(myFriendList);
				} catch (Exception e) {
					tracer.TraceToConsole(String.format("Failed to add new friend -> %s\nError was: %s", newFriend, e.toString()));
				}
			}
		}
	}

	private void AckFriendRequest(String friends) throws HttpServiceException 
	{
		// Split string on ;
		String[] tempfriends = friends.split(";");
		
		// get the first friend
		FriendInfo firstOne = new FriendInfo(tempfriends[0]);
		
		// builds the new friend list (without the first one)
		final ArrayList<FriendInfo> newFriends = new ArrayList<FriendInfo>();
		for (int i = 1; i < tempfriends.length; i++)
		{
			newFriends.add(new FriendInfo(tempfriends[i]));
		}
		
		// we should not be getting an ack from a friend we already have, this is an error
		if (m_FriendInfoList.contains(firstOne))
		{
			throw new HttpServiceException("Invalid logic encountered");
		}
		
		// add the friend
		m_FriendInfoList.add(firstOne);

		// we got an ACK, we need to start adding a new friends (in a new thread)
		Thread babaGump = new Thread() {
			
			@Override
			public void run() {
				// Send friend requests to all the rest of the list, if required (via add friend source)
				for(FriendInfo newFriend : newFriends)
				{
					String myFriendList = GetFriendsInOneLine(true);
					
					if (!m_FriendInfoList.contains(newFriend) && !m_MyInfo.equals(newFriend))
					{
						// proxy call of add friend info to this friend
						FriendServiceProxy proxy = new FriendServiceProxy(newFriend.IP, newFriend.Port);
						try {
							proxy.AddMeAsAFriendRequest(myFriendList);
						} catch (Exception e) {
							tracer.TraceToConsole(String.format("Failed to add new friend -> %s\nError was: %s", newFriend, e.toString()));
						}
					}
				}
			}
		};
		
		babaGump.start();
	}
	
	private String GetFriendsListPage() throws IOException 
	{
		tracer.TraceToConsole("Genrating the friends list page");
		
		StringBuilder sb = new StringBuilder();
		

		if (m_FriendInfoList.size() == 0)
		{
			tracer.TraceToConsole("no friends!!");
			sb.append("You have no friends :(<br>Try the 'look for friends' maybe you will get lucky!!");
		}
		else
		{
			tracer.TraceToConsole("found friends!!");
			sb.append("<table id=\"generated\">");
			sb.append("<tr><th>Nickname</th><th>IP</th><th>Port#</th><th>Remove?</th><th>Get Files?</th></tr>\n");
			// iterate and build a friend row for each friend
			for (int i = 0; i < m_FriendInfoList.size(); i++)
			{
				FriendInfo friend = m_FriendInfoList.get(i);
				
				String alt = "";
				
				if (i % 2 == 1)
				{
					alt = " class=\"alt\"";
				}
				
				tracer.TraceToConsole("Processing friend: " + friend.toString());
				sb.append(String.format("<tr%s>\n<td>%s</td>\n<td>%s</td>\n<td>%d</td>\n<td>", alt, friend.NickName, friend.IP, friend.Port));
				String userIpPort = String.format("\"%s\", \"%d\"", friend.IP, friend.Port);
				sb.append("<input type=\"button\" value=\"Remove Friend\" onClick='doRemoveAction(" + userIpPort + ")'></td>\n");
				sb.append("<td><input type=\"button\" value=\"Go To Files Page\" onClick='doFilesPage(" + userIpPort + ")'></td></tr>\n");
			}
			
//			for (FriendInfo friend : m_FriendInfoList)
//			{
//				tracer.TraceToConsole("Processing friend: " + friend.toString());
//				sb.append(String.format("<tr><td>%s</td><td>%s</td><td>%d</td>\n<td>", friend.NickName, friend.IP, friend.Port));
//				String userIpPort = String.format("\"%s\", \"%d\"", friend.IP, friend.Port);
//				sb.append("<input type=\"button\" value=\"Remove Friend\" onClick='doRemoveAction(" + userIpPort + ")'></td>\n");
//				sb.append("<td><input type=\"button\" value=\"Go To Files Page\" onClick='doFilesPage(" + userIpPort + ")'></td></tr>\n");
//			}
			
			sb.append("</table>");
		}
		
		// replace place holders in template
		HTMLTemplate t = new HTMLTemplate("friends_page.html");
		t.AddValueToTemplate("FREINDS_TABLE", sb.toString());
		t.AddValueToTemplate("MY_PORT", String.valueOf(m_DispatcherPort));
		
		tracer.TraceToConsole("Friend list page was generated!");
		return t.CompileTemplate();
	}

	private void AddFriendSource(String friendIP, int friendPort) 
	{
		String friendsList = GetFriendsInOneLine(true);
		
		// use proxy to call add_me_as_a_friend on the target IP
		FriendServiceProxy proxy = new FriendServiceProxy(friendIP, friendPort);
		try
		{
			proxy.AddMeAsAFriendRequest(friendsList);
		}
		catch (Exception e)
		{
			tracer.TraceToConsole("failed to add friend source");
		}
	}

	private void RemoveFriend(FriendInfo friend) 
	{
		m_FriendInfoList.remove(friend);
	}
	
	public void RemoveFriend(String IP, int port) 
	{
		for(int i = 0; i < m_FriendInfoList.size(); i++)
		{
			if(m_FriendInfoList.get(i).IP.equals(IP) && m_FriendInfoList.get(i).Port == port)
			{
				m_FriendInfoList.remove(i);
			}
		}
		
		tracer.TraceToConsole(String.format("Friend %s:%d was removed", IP, port));
	}

	public void LookForFriends() throws IOException 
	{
		DatagramSocket socket = new DatagramSocket();
		
		FriendInfo me = FriendService.get_instance().GetMyInfo();
		
		// payload is "Be my friend? [ip]:[dispatcher port]
		String payload = String.format("Be my friend? %s:%d", me.IP, m_DispatcherPort);
		
		// create a UDP Datagram and broadcast the payload
		String[] IPchunks = me.IP.split("[.]");
		
		// change last octet to 255
		String brodcastIP = String.format("%s.%s.%s.%d", IPchunks[0], IPchunks[1], IPchunks[2], 255);
		
		// prepare the payload packet
		DatagramPacket payloadPacket = new DatagramPacket(payload.getBytes(), payload.getBytes().length, InetAddress.getByName(brodcastIP) , m_FriendServerPort);
		
		tracer.TraceToConsole("UDP Brodcast sent");
		
		socket.send(payloadPacket);
	}

	public FriendInfo[] GetFriends() 
	{	
		return m_FriendInfoList.toArray(new FriendInfo[0]);
	}
	
	public String GetFriendsInOneLine(boolean withMe) 
	{
		StringBuilder sb = new StringBuilder();
		
		// should we add ourselves?
		if (withMe)
		{
			sb.append(m_MyInfo.toString());
			if (m_FriendInfoList.size() > 0)
			{
				sb.append(";");
			}
		}
		
		// append first friend
		if (m_FriendInfoList.size() > 0)
		{
			sb.append(m_FriendInfoList.get(0).toString());
			
			// append rest of them with ';' delim
			for(int i = 1 ; i < m_FriendInfoList.size() ; i++)
			{
				sb.append(";");
				String friend = m_FriendInfoList.get(i).toString();
				sb.append(friend);
			}
		}
		
		return sb.toString();
	}
}
