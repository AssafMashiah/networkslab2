
public class FriendInfo {
	
	// The Friends nickmane
	public String NickName;
	
	// IP of the friend
	public String IP;
	
	// Port Number the friend is listening to
	public int Port;
	
	public FriendInfo(String nickname, String IP, int port)
	{
		this.NickName = nickname;
		this.IP = IP;
		this.Port = port;
	}
	
	public FriendInfo(FriendInfo other)
	{
		this.NickName = other.NickName;
		this.IP = other.IP;
		this.Port = other.Port;
	}
	
	public String toString()
	{
		return String.format("%s:%s:%s", this.NickName, this.IP, this.Port);
	}
}
