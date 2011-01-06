import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class Lab2Utils
{
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
