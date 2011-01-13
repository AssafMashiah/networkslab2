public class ChatBuddyData
{
	public String TimeStamp;
	public String TitleName;
	public String Message;
	
	public ChatBuddyData(String nickname, String IP, String message)
	{
		TimeStamp = Lab2Utils.GetTimeStamp("HH:mm:ss");
		TitleName = String.format("%s (%s): ", nickname,IP);
		Message = message;
	}
	
	public String toString()
	{
		return String.format("[%s] %s %s",TimeStamp, TitleName, Message);
	}
}
