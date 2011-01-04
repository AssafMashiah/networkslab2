import java.io.IOException;

public class CommandsServiceProxy extends ProxyBase implements ICommandsService {

	// used for trace messages
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	public String callFunction(Functions functionName, String[] params)
			throws HttpServiceException {
		StringBuilder retVal;
		try
		{
			retVal = new StringBuilder();
			
			switch (functionName) {
			case echo:
				retVal.append(echo(params[0]));
				break;
			case get_main_page:
				HTMLTemplate t = new HTMLTemplate("mainpage.html");
				t.AddValueToTemplate("SERVER", "http://localhost/");
				retVal.append(t.CompileTemplate());
				break;
			default:
				// this is a 404 error, it should not happen (right now no code
				// because it cannot happen at this place as we get enum as input)
			}
		}
		catch (Exception e)
		{
			throw new HttpServiceException("Internal error in commands service", e);
		}
		
		return retVal.toString();
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
