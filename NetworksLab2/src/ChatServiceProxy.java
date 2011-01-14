import java.io.IOException;
import java.net.UnknownHostException;


public class ChatServiceProxy extends ProxyBase implements IChatService
{
	
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	public ChatServiceProxy(String targetIP, int targetPort) 
	{
		super(targetIP, targetPort);
	}
	
	public void newMessage(String message) throws HttpProxyException, UnknownHostException, IOException, HttpHeaderParsingException, HttpResponseParsingException
	{
		HttpRequestParser request = GetRequest(HttpRequestMethod.GET, String.format("/chat_service/new_message?p1=%s", Lab2Utils.URLEncodeString(message)));
		
		HttpResponseParser response = SendData(request);
		
		tracer.TraceToConsole(String.format("Message '%s' was sent to %s", message, this.DestinationIP));
		tracer.TraceToConsole(String.format("Response was:\n%s", response.toString()));
		
		// validate response
		if (!response.GetHttpResponseCodeObject().equals(HttpResponseCode.OK))
		{
			throw new HttpProxyException(String.format("Destination reported an error during add_me_as_a_friend: %s", response.GetHttpResponseCode()));
		}
	}
}
