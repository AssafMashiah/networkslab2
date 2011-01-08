import java.io.IOException;

public class CSSService 
{
	private static CSSService m_Instance = null;
	
	private static final Tracer tracer = Tracer.getTracerForThisClass();

	private CSSService() 
	{
	}

	public static synchronized CSSService get_instance() 
	{
		if (m_Instance == null) 
		{
			m_Instance = new CSSService();
		}

		return m_Instance;
	}
	
	/**
	 * returns an image data in a byte array
	 * @param imageName the image filename
	 * @return The image data
	 * @throws IOException if image it not found or cannot read the image file
	 */
	public byte[] GetCSS(String cssName) throws IOException
	{
		tracer.TraceToConsole("Loading css: " + cssName);
		
        return Lab2Utils.ReadFile(cssName);
	}
}
