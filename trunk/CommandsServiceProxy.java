import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class CommandsServiceProxy implements ICommandsService, IProxy {

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
				for (String s : params)
				{
					retVal.append(echo(s));
					retVal.append("\r\n");
				}
				
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

	
	private String echo(String s) throws HttpResponseParsingException, UnknownHostException, IOException
	{
		tracer.TraceToConsole(String.format("Starting echo proxy, Destination: %s", DestinationIP));
		
		// define the request
		HttpRequestParser request = new HttpRequestParser();
		request.SetMethod(HttpRequestMethod.GET);
		request.SetHttpVersion(HttpVersion.One);
		request.SetRequestURI(String.format("/commands_service/echo?string=%s", s));
		
		tracer.TraceToConsole(String.format("Sending:\n%s\nTo: %s", request.toString(), DestinationIP));
		
		// open tcp socket to destination
		Socket sock = new Socket(DestinationIP, DestinationPort);
		
		tracer.TraceToConsole("Proxy Connected");
		
		PrintWriter toServer = new PrintWriter(sock.getOutputStream(), true);
		
		toServer.println(request.toString());
		
		
		
		// process the response
		HttpResponseParser response = new HttpResponseParser("");
		return new String(response.GetContent());
	}
	
	
	
}
