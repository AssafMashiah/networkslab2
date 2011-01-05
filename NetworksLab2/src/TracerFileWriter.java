import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is a one file writer - meaning that only one file will be created
 * every class using the file out put is using
 */
public class TracerFileWriter
{
	private final static String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	
	private static FileOutputStream m_fstream;
	private static String LOG_LOCATION = "c:\\Server.log";
	
	// Make this a shared resource to all classes that use the file
	private static TracerFileWriter m_Instance = null;
	
	/**
	 * The singelton of the file reader
	 * @return - I'm all alone!
	 */
	public static synchronized TracerFileWriter get_instance()
	{
		if (m_Instance == null) 
		{
			try
			{
				// Create file 
				m_fstream = new FileOutputStream(LOG_LOCATION,true);
			}
			catch (Exception e)
			{
				//Catch exception if any
		    	System.err.println("Error: " + e.getMessage());
			}
			m_Instance = new TracerFileWriter();
		}

		return m_Instance;
	}
	
	/**
	 * Writing the data to a file
	 * @param message - the massage to add
	 * @param name - class the massage came from
	 */
	public static void Write(String message, String name)
	{
		try 
	   	{
			m_fstream.write(String.format("[%s] %s: %s", getTime(), message, name).getBytes());
			m_fstream.write("\r\n".getBytes());
	   	}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the current time
	 * @return the time
	 */
	private static String getTime()
	{
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_NOW);
        Date date = new Date();
        return dateFormat.format(date);
	}

	/**
	 * Stops the file - closing it.
	 */
	public static void Stop()
	{
		try
		{
			m_fstream.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}