import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatBuddyData
{
	private final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	private String m_TimeStamp;
	private String m_TitleName;
	private String m_OldMassage;
	
	public ChatBuddyData(String nickname, String IP, String massage)
	{
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_NOW);
        Date date = new Date();
		m_TimeStamp = dateFormat.format(date);
		m_TitleName = String.format("%s (%s): ", nickname,IP);
		m_OldMassage = massage;
	}
	
	public String toString()
	{
		return String.format("[%s]%s%s",m_TimeStamp, m_TitleName, m_OldMassage);
	}
}
