import java.util.StringTokenizer;


public class HttpResponseParser extends HttpParser {

	private HttpResponseCode m_ResponseCode;
	
	/**
	 * Create a HTTP response
	 * @param code The response code
	 */
	public HttpResponseParser(HttpResponseCode code)
	{
		m_ResponseCode = code;
	}
	
	/**
	 * Create a HTTP response from a 
	 * @param responseData
	 * @throws HttpResponseParsingException 
	 */
	public HttpResponseParser(String responseData) throws HttpResponseParsingException
	{
		String[] lines = responseData.split(CRLF);
		
		String[] firstLine = lines[0].split(" ");
		
		// get http version
		HttpVersion version = HttpVersion.valueOf(firstLine[0]);
		if (version == null)
		{
			throw new HttpResponseParsingException("Invalid HTTP Version");
		}
		
		this.SetHttpVersion(version);
		
		// get the http status code
		HttpResponseCode code = HttpResponseCode.valueOf(firstLine[1]);
		if (code == null)
		{
			throw new HttpResponseParsingException("Invalid HTTP Status Code");
		}
		
		this.m_ResponseCode = code;
		
		// check for headers
		int i = 1;
		for (; i < lines.length; i++)
		{
			if (lines[i] == "")
			{
				break;
			}
			
			String[] header = lines[i].split(": ", 2);
			this.AddHeader(new HttpHeader(header[0], header[1]));
		}
		
		// skip to the next line
		i++;
		
		// handle content
		if (i < lines.length)
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append(lines[i]);
			while (i < lines.length)
			{
				sb.append(CRLF);
				sb.append(lines[i]);
				i++;
			}
			
			this.SetContent(sb.toString().getBytes());
		}
	}
	
	public HttpResponseParser()
	{
		
	}
	
	/**
	 * If response is �HTTP/1.0 200 OK�, the response code is 200
	 * @return Returns the response code of this response 
	 */
	public String GetHttpResponseCode()
	{
		return m_ResponseCode.GetCode();
	}
	
	/**
	 * If response is �HTTP/1.0 200 OK�, the response description is OK
	 * @return Returns the descriptions of the current response code
	 */
	public String GetHttpResponseDescription()
	{
		return m_ResponseCode.GetDescription();
	}

	@Override
	String HeaderString() {
		StringBuilder sb = new StringBuilder();

		for (HttpHeader currentHeader : GetHeaders())
		{
			sb.append(currentHeader.toString());
			sb.append(CRLF);
		}
		return sb.toString();
	}
	
	/**
	 * Sets the response line into the object (from raw string data
	 * @param requestLine The raw data that came from the socket (or anywhere else)
	 * @throws HttpResponseParsingException If the HTTP version or the response code is not supported
	 */
	public void HttpResponseLineParser(String requestLine) throws HttpResponseParsingException 
	{
		// tokenize the Request-Line
		StringTokenizer toky = new StringTokenizer(requestLine, " ");
	
		// set method...
		if (toky.hasMoreTokens()) {
			try {
				SetHttpVersion(HttpVersion.valueOf(toky.nextToken()));
			} catch (Exception e) {
				throw new HttpResponseParsingException("Invalid HTTP Version!");
			}
		} else {
			throw new HttpResponseParsingException("Invalid Response-Line!");
		}
	
		// set requested URI / resource
		if (toky.hasMoreTokens()) {
			try {
				m_ResponseCode = HttpResponseCode.valueOf(toky.nextToken());
			} catch (Exception e) {
				throw new HttpResponseParsingException("Invalid HTTP Response Code!");
			}
		} else {
			throw new HttpResponseParsingException("Invalid HTTP Version!");
		}
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		// output the status line
		// Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
		sb.append(GetHttpVersion().GetHTTPVersion());
		sb.append(" ");
		sb.append(m_ResponseCode);
		sb.append(CRLF);
		
		// output the headers
		sb.append(HeaderString());
		sb.append(CRLF);
		
		if (GetContentSize() > 0)
		{
			sb.append(new String(GetContent()));
		}
		
		return sb.toString();
	}
}
