import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;


public class HttpRequestParser extends HttpParser {

	private HttpRequestMethod m_RequestMethod;
	private String m_RequestedURI;
	
	// We want to be able to get the GET and the POST separately 
	// This is because maybe the client will want to handle them differently (this is us, begging for a bonus, we have puppy eyes.. honestly!)
	// This data structure might be an overkill, but it feels like the most readable way of doing so
	// Memory wise it is probably possible to do this at a lower footprint, but its more simple to understand and extend this way
	private LinkedHashMap<HttpQueryStringType, LinkedHashMap<String, String>> m_QueryString;
	
	public HttpRequestParser()
	{
		super();
	}
	
	/**
	 * Returns the HttpRequestMethod for this request object
	 * @return the HttpRequestMethod for this request object
	 */
	public HttpRequestMethod GetMethod()
	{
		return m_RequestMethod;
	}
	
	/**
	 * Set the HTTP Request method
	 * @param type The method to set to
	 */
	public void SetMethod(HttpRequestMethod type)
	{
		m_RequestMethod = type;
	}
	
	/**
	 * If request is �GET /mysite/index.html HTTP/1.0�, than the requested resource is /mysite/index.html
	 * @param resource The resource string
	 */
	public void SetRequestURI(String resource)
	{
		m_RequestedURI = resource;
	}
	
	/**
	 * If request is �GET /mysite/index.html HTTP/1.0�, than the requested resource is /mysite/index.html
	 * @return The resource string
	 */
	public String GetRequestedURI()
	{
		return m_RequestedURI;
	}
	
	/**
	 * Generates the query-string objects
	 * @throws HttpRequestParsingException 
	 */
	public void GenerateQueryString() throws HttpRequestParsingException
	{
		try
		{
			m_QueryString = new LinkedHashMap<HttpQueryStringType, LinkedHashMap<String, String>>();
			
			
			// did we get any params from the URI?
			int paramSeperator = m_RequestedURI.indexOf('?');
			if (paramSeperator != -1)
			{
				// yes we can!
				String params = m_RequestedURI.substring(paramSeperator + 1, m_RequestedURI.length());
				
				m_QueryString.put(HttpQueryStringType.GET, parseQueryString(params));
			}
			
			// did we get any in the content (POST?)
			if (GetContentSize() > 0 && GetMethod().equals(HttpRequestMethod.POST))
			{
				String params = new String(GetContent());
				
				m_QueryString.put(HttpQueryStringType.POST, parseQueryString(params));
			}
		}
		catch (Exception e)
		{
			// something bad happen, we do not have the query string
			m_QueryString = null;
			
			throw new HttpRequestParsingException("Error parsing Query String", e);
		}
	}
	
	
	/**
	 * Gets the request parameters (both GET and POST)
	 * It will try to parse the query string if it does not exists
	 * @return A dictionary with key=param name, value=param value 
	 * @throws HttpRequestParsingException If there was a problem parsing the query string
	 */
	public Map<String, String> GetQueryStringParams(HttpQueryStringType type) throws HttpRequestParsingException
	{
		// check to see if we need to generate the params.
		if (m_QueryString == null)
		{
			GenerateQueryString();
		}
		
		// create a copy of the data and return it to the user for farther abuse
		LinkedHashMap<String, String> retVal = new LinkedHashMap<String, String>();
		
		LinkedHashMap<String, String> tempMap;
		switch (type)
		{
		case GET:
			tempMap = m_QueryString.get(HttpQueryStringType.GET);
			if (tempMap != null)
			{
				retVal.putAll(tempMap);
			}
			break;
		case POST:
			tempMap = m_QueryString.get(HttpQueryStringType.POST);
			if (tempMap != null)
			{
				retVal.putAll(tempMap);
			}
			break;
		case BOTH:
			tempMap = m_QueryString.get(HttpQueryStringType.GET);
			if (tempMap != null)
			{
				retVal.putAll(tempMap);
			}
			
			tempMap = m_QueryString.get(HttpQueryStringType.POST);
			if (tempMap != null)
			{
				retVal.putAll(tempMap);
			}
			break;
		}

		return retVal;
	}
	
	/**
	 * Parses a string with params
	 * @param params The param string
	 * @return A key-value collection of the keys and value from the param string
	 */
	private LinkedHashMap<String, String> parseQueryString(String params)
	{
		LinkedHashMap<String, String> currentParams = new LinkedHashMap<String, String>();
		
		boolean finishedAllParams = false;
		while (!finishedAllParams)
		{
			// the parameter we will work on
			String param;
			
			// get amp loc or end of line
			int ampLoc = params.indexOf('&');
			// single param?
			if (ampLoc == -1)
			{
				param = params.substring(0, params.length());
				
				// ugly patch..
				finishedAllParams = true;
			}
			else
			{
				param = params.substring(0, ampLoc);
			}
			
			// we support '=' in the values (not sure if that is a requirement)
			int currentParamSeperator = param.indexOf('=');
			
			String paramKey = param.substring(0, currentParamSeperator);
			String paramValue = param.substring(currentParamSeperator + 1, param.length());
			
			// handle decoding (in case we got spaces or whatnot
			paramValue = Lab2Utils.URLDecodingString(paramValue);
			
			currentParams.put(paramKey, paramValue);
			
			params = params.substring(ampLoc + 1, params.length());
		}
		
		return currentParams;
	}
	
	/**
	 * Parses the Request-Line
	 * 
	 * @param requestLine
	 *            The request line to parse
	 * @throws HttpRequestParsingException
	 *             If an error occurs while parsing a request
	 * @throws HttpNotImplementedException
	 *             If the request method is not supported
	 */
	public void HttpRequestLineParser(String requestLine) 
		throws 	HttpRequestParsingException,
				HttpNotImplementedException {
		// tokenize the Request-Line
		StringTokenizer toky = new StringTokenizer(requestLine, " ");

		// set method...
		if (toky.hasMoreTokens()) {
			try {
				this.SetMethod(HttpRequestMethod.valueOf(toky.nextToken()));
			} catch (Exception e) {
				throw new HttpNotImplementedException("Invalid Request Method!");
			}
		} else {
			throw new HttpRequestParsingException("Invalid Request-Line!");
		}

		// set requested URI / resource
		if (toky.hasMoreTokens()) {
			this.SetRequestURI(toky.nextToken());
		} else {
			throw new HttpRequestParsingException("Invalid Request-Line!");
		}

		// set HTTP version token (bad implementation, should use the enum.valueOf() )
		if (toky.hasMoreTokens()) {
			String httpVersion = toky.nextToken();
			if (httpVersion.equals(HttpVersion.One.GetHTTPVersion())) {
				this.SetHttpVersion(HttpVersion.One);
			} else if (httpVersion.equals(HttpVersion.OneDotOne
					.GetHTTPVersion())) {
				this.SetHttpVersion(HttpVersion.OneDotOne);
			} else {
				throw new HttpRequestParsingException("Invalid HTTP Version!");
			}
		} else {
			throw new HttpRequestParsingException("Invalid Request-Line!");
		}
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		// output the status line
		// Request-Line   = Method SP Request-URI SP HTTP-Version CRLF
		sb.append(m_RequestMethod);
		sb.append(" ");
		sb.append(m_RequestedURI);
		sb.append(" ");
		sb.append(GetHttpVersion().GetHTTPVersion());
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
