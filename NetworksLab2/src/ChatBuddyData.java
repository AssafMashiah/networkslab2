public class ChatBuddyData
{
	public static final long OLD_LIMIT = 10000L;
	
	private long m_LoggedAt;
	public String TimeStamp;
	public String TitleName;
	public String Message;
	
	public ChatBuddyData(String nickname, String IP, String message)
	{
		m_LoggedAt = Lab2Utils.GetEpochFromUTC();
		TimeStamp = Lab2Utils.GetTimeStamp("HH:mm:ss");
		TitleName = String.format("%s (%s): ", nickname,IP);
		Message = message;
	}
	
	/**
	 * True if the message has arrived [OLD_LIMIT] ago
	 * OLD_LIMIT is in ms, constant defined above
	 * @return
	 */
	public boolean IsMessageOld()
	{
		return IsMessageOld(OLD_LIMIT);
	}
	
	public boolean IsMessageOld(long timeOut)
	{
		boolean oldMessage = false;
		
		if (System.currentTimeMillis() - m_LoggedAt > timeOut)
		{
			oldMessage = true;
		}
		
		return oldMessage;
	}
	
	/**
	 * If the message is older then the given timestamp, we get true
	 * @param timeStamp The timestamp to check against
	 * @return
	 */
	public boolean IsMessageNew(long timeStamp)
	{
		boolean newMessage = false;
		
		if (m_LoggedAt > timeStamp)
		{
			newMessage = true;
		}
		
		return newMessage;
	}
	
	public String toString()
	{
		return String.format("[%s] %s %s",TimeStamp, TitleName, Message);
	}
}
