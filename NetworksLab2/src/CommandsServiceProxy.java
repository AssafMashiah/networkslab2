import java.io.IOException;

public class CommandsServiceProxy extends ProxyBase implements ICommandsService {

	// used for trace messages
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	public CommandsServiceProxy()
	{
		
	}
	
	public CommandsServiceProxy(String dest, int port)
	{
		this.DestinationIP = dest;
		this.DestinationPort = port;
	}

	public String echo(String s) 
		throws 	IOException, HttpProxyException, 
				HttpHeaderParsingException, HttpResponseParsingException
	{
		tracer.TraceToConsole(String.format("Starting echo proxy, Destination: %s", DestinationIP));
		
		HttpRequestParser request = GetRequest(HttpRequestMethod.GET, "/commands_service/echo", s);
		
		tracer.TraceToConsole(String.format("Sending:\n%s\nTo: %s", request.toString(), DestinationIP));
		
		HttpResponseParser response = SendData(request);
		
		tracer.TraceToConsole("echo proxy data received");
		
		// validate response
		if (!response.GetHttpResponseCodeObject().equals(HttpResponseCode.OK))
		{
			throw new HttpProxyException(String.format("Destination reported an error during echo: %s", response.GetHttpResponseCode()));
		}
		
		return new String(response.GetContent());
	}
}
