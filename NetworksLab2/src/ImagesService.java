import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImagesService {

	private static ImagesService m_Instance = null;
	
	private static final Tracer tracer = Tracer.getTracerForThisClass();

	private ImagesService() 
	{
	}

	public static synchronized ImagesService get_instance() 
	{
		if (m_Instance == null) 
		{
			m_Instance = new ImagesService();
		}

		return m_Instance;
	}
	
	/**
	 * returns an image data in a byte array
	 * @param imageName the image filename
	 * @return The image data
	 * @throws IOException if image it not found or cannot read the image file
	 */
	public byte[] GetImage(String imageName) throws IOException
	{
		tracer.TraceToConsole("Reading image: " + imageName);
		
		File file = new File(imageName);
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
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
	}
}
