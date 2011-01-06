import java.io.BufferedReader;
import java.io.IOException;
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
	
	/**
	 * Creates a new response directly from a incoming data stream
	 * @param reader The reader with the data
	 * @throws HttpHeaderParsingException
	 * @throws HttpResponseParsingException
	 * @throws IOException
	 */
	public HttpResponseParser(BufferedReader reader) throws HttpHeaderParsingException, HttpResponseParsingException, IOException
	{
		String[] responseHeaders = GetHeaderTextFromStream(reader).split(HttpParser.CRLF);
		HttpResponseLineParser(responseHeaders[0]);
		int contentLength = HttpHeadersParser(responseHeaders);
		if (contentLength > 0)
		{
			GetContentFromStream(contentLength, reader);
		}
	}
	
	public static HttpResponseParser GetRedirectResponse(String location, HttpVersion version)
	{
		// 302 to the get_main_page
		HttpResponseParser response = new HttpResponseParser(HttpResponseCode.MOVED_PERMANENTLY);
		response.SetHttpVersion(version);
		response.AddHeader(new HttpHeader("Location", location));
		
		return response;
	}
	
	/**
	 * Gets the response code object
	 * @return The response code object!
	 */
	public HttpResponseCode GetHttpResponseCodeObject()
	{
		return m_ResponseCode;
	}
	
	/**
	 * If response is “HTTP/1.0 200 OK”, the response code is 200
	 * @return Returns the response code of this response 
	 */
	public String GetHttpResponseCode()
	{
		return m_ResponseCode.GetCode();
	}
	
	/**
	 * If response is “HTTP/1.0 200 OK”, the response description is OK
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
				String httpVersion = toky.nextToken();
				if (httpVersion.equals(HttpVersion.One.GetHTTPVersion())) {
					this.SetHttpVersion(HttpVersion.One);
				} else if (httpVersion.equals(HttpVersion.OneDotOne
						.GetHTTPVersion())) {
					this.SetHttpVersion(HttpVersion.OneDotOne);
				} else {
					throw new HttpRequestParsingException("Invalid HTTP Version!");
				}
			} catch (Exception e) {
				throw new HttpResponseParsingException("Invalid HTTP Version!");
			}
		} else {
			throw new HttpResponseParsingException("Invalid Response-Line!");
		}
	
		// set requested URI / resource
		if (toky.hasMoreTokens()) {
			try {
				m_ResponseCode = HttpResponseCode.GetResponseCode(Integer.parseInt(toky.nextToken()));
			} catch (Exception e) {
				throw new HttpResponseParsingException("Invalid HTTP Response Code!");
			}
		} else {
			throw new HttpResponseParsingException("Invalid HTTP Version!");
		}
	}
	
	@Override
	public String toString() {
		
		return toString(false);
	}
	
	/**
	 * 
	 * @param onlyHeaders if true, will return only the headers, if false, will return the headers with the data (ASCII encoded to string)
	 * @return The response in human readable format* (some humans)
	 */
	public String toString(boolean onlyHeaders)
	{
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
		
		if (!onlyHeaders)
		{
			sb.append(new String(GetContent()));
		}
		
		return sb.toString();
	}
}
