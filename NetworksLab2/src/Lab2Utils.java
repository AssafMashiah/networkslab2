import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Lab2Utils
{
	private final static String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * Get the timestamp with the default format (yyyy-MM-dd HH:mm:ss)
	 * @return
	 */
	public static String GetTimeStamp()
	{
		return GetTimeStamp(DATE_FORMAT_NOW);
	}
	
	/**
	 * Return timestamp with the given format
	 * @param format The format of the timestamp
	 * @return
	 */
	public static String GetTimeStamp(String format)
	{
		DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
		return dateFormat.format(date);
	}
	
	/**
	 * Generates a cool button
	 * @param text The text in the button
	 * @param onClick The on-click action
	 * @return The button HTML code
	 */
	public static String GenerateStyledButton(String text, String onClick)
	{
		return GenerateStyledButton(text, onClick, false);
	}
	
	/**
	 * Generates a cool button which is centered (relative)
	 * @param text The text in the button
	 * @param onClick The on-click action
	 * @param centered should it be centered?
	 * @return The button HTML code
	 */
	public static String GenerateStyledButton(String text, String onClick, boolean centered)
	{
		String centDataPerfix = "";
		String centDataPostfix = "";
		if (centered)
		{
			centDataPerfix = "<div class=\"centerwrap\">";
			centDataPostfix = "</div>";
		}
		
		return String.format("%s<a class=\"button\" href=\"#\" onClick='this.blur();%s'><span>%s</span></a>%s", centDataPerfix, onClick, text, centDataPostfix);
	}
	
	public static byte[] ReadFile(String fileName) throws IOException
	{
		File file = new File(fileName);
		InputStream is = new FileInputStream(file);
	    
        // Get the size of the file
        long length = file.length();
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException(String.format("Could not completely read file %s", file.getName()));
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
	}
	
	public static void WriteFile(byte[] data, String fileName) throws IOException
	{
		BufferedOutputStream bos = null;
 
		try
		{
			//create an object of FileOutputStream
			FileOutputStream fos = new FileOutputStream(new File(fileName));
 
			//create an object of BufferedOutputStream
			bos = new BufferedOutputStream(fos);
 
			bos.write(data);
		}
		finally
		{
			if(bos != null)
			{
				//flush the BufferedOutputStream
				bos.flush();
 
				//close the BufferedOutputStream
				bos.close();
			}
		}
	}
	
	public static String URLDecodingString(String in)
	{
		String out = null;
		try 
		{
			out = URLDecoder.decode(in, "ASCII");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		
		return out;
	}
	
	public static String URLEncodeString(String in)
	{
		String out = null;
		try
		{
			out = URLEncoder.encode(in, "ASCII");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		
		return out;
	}
}
