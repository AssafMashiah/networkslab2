import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class ProxyBase 
{
	public String DestinationIP   = "";
	public int    DestinationPort = 10000;
	
	// used for trace messages
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	public ProxyBase()
	{
		
	}
	
	public ProxyBase(String dest, int port)
	{
		this.DestinationIP = dest;
		this.DestinationPort = port;
	}
	
	/**
	 * Gets a request object for this parser (HTTP Version 1.0)
	 * @param method Request Method
	 * @param uri The resource required
	 * @return 
	 */
	protected HttpRequestParser GetRequest(HttpRequestMethod method, String uri)
	{
		return GetRequest(method, uri, HttpVersion.One, new String[] {});
	}
	
	/**
	 * Gets a request object for this parser (HTTP Version 1.0)
	 * @param method Request Method
	 * @param uri The resource required
	 * @param param A single param
	 * @return 
	 */
	protected HttpRequestParser GetRequest(HttpRequestMethod method, String uri, String param)
	{
		return GetRequest(method, uri, HttpVersion.One, new String[] {param});
		
	}
	
	/**
	 * Gets a request object for this parser (HTTP Version 1.0)
	 * @param method Request Method
	 * @param uri The resource required
	 * @param params A string array of params
	 * @return 
	 */
	protected HttpRequestParser GetRequest(HttpRequestMethod method, String uri, String[] params)
	{
		return GetRequest(method, uri, HttpVersion.One, params);
		
	}
	
	/**
	 * Gets a request object for this parser
	 * @param method Request Method
	 * @param uri The resource required
	 * @param version The HTTP version for this request
	 * @param params A string array of params
	 * @return
	 */
	protected HttpRequestParser GetRequest(HttpRequestMethod method, String uri, HttpVersion version, String[] params)
	{
		HttpRequestParser request = new HttpRequestParser();
		request.SetMethod(method);
		request.SetHttpVersion(version);
		
		StringBuilder builder = new StringBuilder();
		
		if (params.length > 0)
		{
			builder.append(String.format("?param1=%s", params[0]));
		
			for (int i = 1; i < params.length; i++)
			{
				builder.append(String.format("&param%d=%s", i + 1, params[i]));
			}
		}
		
		request.SetRequestURI(String.format("%s%s", uri, builder.toString()));
		
		return request;
	}
	
	/**
	 * Sends a request to the destination ip:port
	 * @param request The request to send
	 * @return The response of that request
	 * @throws UnknownHostException If there is a problem connecting
	 * @throws IOException If there is a problem reading the data off the socket
	 * @throws HttpHeaderParsingException If there is a problem parsing the response
	 * @throws HttpResponseParsingException If there is a problem parsing the response
	 */
	protected HttpResponseParser SendData(HttpRequestParser request) throws UnknownHostException, IOException, HttpHeaderParsingException, HttpResponseParsingException
	{
		// open tcp socket to destination
		Socket sock = new Socket(DestinationIP, DestinationPort);
		
		tracer.TraceToConsole("Proxy Connected");
		
		PrintWriter toServer = new PrintWriter(sock.getOutputStream(), true);
		BufferedReader fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream(), "ASCII"));
		// send the request
		toServer.println(request.toString());
		
		tracer.TraceToConsole(String.format("Data for '%s' sent", request.GetRequestedURI()));
		
		// receive data
		HttpResponseParser response = new HttpResponseParser(fromServer);
		
		return response;
	}
}
