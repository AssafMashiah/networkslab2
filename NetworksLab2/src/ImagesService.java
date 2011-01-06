import java.io.IOException;

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
		
        return Lab2Utils.ReadFile(imageName);
	}
}
