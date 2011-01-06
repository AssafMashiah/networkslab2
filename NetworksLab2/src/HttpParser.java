import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;


public abstract class HttpParser 
{
	public final static String CRLF = "\r\n";
	
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	private ArrayList<HttpHeader> m_Headers;
	private byte[] m_Content;
	private HttpVersion m_HttpVersion;
	
	/**
	 * Creates a new Parser object
	 */
	public HttpParser()
	{
		tracer.TraceToConsole("Starting HTTP Parser");
		
		m_Headers = new ArrayList<HttpHeader>();
	}
	
	/**
	 * Adds an header to this parser
	 * @param header The header to add
	 */
	public void AddHeader(HttpHeader header)
	{
		if (header != null)
		{
			m_Headers.add(header);
		}
	}

	/**
	 * Returns the headers associated to this parser
	 * @return
	 */
	public ArrayList<HttpHeader> GetHeaders()
	{
		return new ArrayList<HttpHeader>(m_Headers);
	}
	
	/**
	 * Sets the content to this parser
	 * @param data
	 */
	public void SetContent(byte[] data)
	{
		m_Content = data;
	}
	
	/**
	 * If not content, an empty byte array is returned
	 * @return The content of the current parser or null if empty
	 */
	public byte[] GetContent()
	{
		if (GetContentSize() > 0)
		{
			return m_Content;
		}
		else
		{
			return new byte[0];
		}
	}
	
	/**
	 * 
	 * @return The content size
	 */
	public int GetContentSize()
	{
		int contentSize = 0;
		
		if (m_Content != null)
		{
			contentSize = m_Content.length;
		}
		
		return contentSize;
	}
	
	/**
	 * 
	 * @return The HTTP version for this request (1.0 or 1.1)
	 */
	public HttpVersion GetHttpVersion()
	{
		return m_HttpVersion;
	}
	
	/**
	 * Sets the HTTP version for this request
	 * @param version the version
	 */
	public void SetHttpVersion(HttpVersion version)
	{
		m_HttpVersion = version;
	}
	
	/**
	 * Parses the headers from raw text to their objects for easier access
	 * Headers start from the second line (lines[1..n] , not [0..n]).
	 * @param lines raw headers
	 * @return The size of the content-length header or 0 if it does not exists
	 * @throws HttpRequestParsingException
	 */
	public int HttpHeadersParser(String[] lines) throws HttpHeaderParsingException 
	{
		int contentLength = 0;

		// the Request-Line is finished, lets do the headers
		for (int i = 1; i < lines.length; i++) {
			int colonLocation = lines[i].indexOf(':');
			if (colonLocation == -1) {
				throw new HttpHeaderParsingException("Invalid Header Format!");
			}
			String headerName = lines[i].substring(0, colonLocation);

			// header name cannot be empty
			if (headerName.equals("")) {
				throw new HttpHeaderParsingException("Invalid Header Format!");
			}

			String headerValue = lines[i].substring(colonLocation + 2, lines[i]
					.length());

			this.AddHeader(new HttpHeader(headerName, headerValue));

			// if this is the Content Length, get the value (which we eventually
			// return)
			if (headerName.equals("Content-Length")) {
				contentLength = Integer.parseInt(headerValue);
				if(contentLength < 0){
					throw new HttpHeaderParsingException("Content-Lenght must be positive!");
				}
			}
		}

		return contentLength;
	}
	
	/**
	 * Gets the header text from the input stream, header information ends with
	 * a CRLF CRLF
	 * 
	 * @param in
	 *            a buffered reader!
	 * @return A string containing the Request/Response-Line and headers
	 * @throws IOException
	 *             If there was a problem reading from the input stream
	 */
	public String GetHeaderTextFromStream(BufferedReader in) throws IOException {
		String retVal = "";
		
		if (in != null) {
			StringBuilder output = new StringBuilder();

			String line;
			while ((line = in.readLine()) != null) {
				output.append(line + HttpParser.CRLF);
				// if we have CRLF CRLF, stop
				if (output.indexOf(String.format("%s%s", HttpParser.CRLF,
						HttpParser.CRLF)) != -1) {
					break;
				}
			}
			
			retVal = output.toString();
		}
		
		return retVal;
	}
	
	/**
	 * Reads the content off the request
	 * 
	 * @param contentLength
	 *            The content length
	 * @param in
	 *            The buffered reader
	 * @return The content data
	 * @throws IOException
	 *             If there was a problem while reading from the stream
	 */
	protected void GetContentFromStream(int contentLength, BufferedReader in)
			throws IOException {
		char[] ccontent = new char[contentLength];

        int offset = 0;
        int numRead = 0;
		while (offset < ccontent.length
	               && (numRead=in.read(ccontent, offset, ccontent.length-offset)) >= 0) 
		{
            offset += numRead;
	    }
		
		byte[] bytes = new byte[ccontent.length];
		
		for (int i = 0; i < ccontent.length; i++)
		{
			bytes[i] = (byte)ccontent[i];
		}
		
		// we want to ensure the content is preserved with its encoding (which should be ASCII, but may not be?)
		SetContent(bytes);
	}

	/**
	 * 
	 * @return The parser header as a string
	 */
	abstract String HeaderString();
}
