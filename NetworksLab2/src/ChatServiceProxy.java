import java.io.IOException;
import java.net.UnknownHostException;


public class ChatServiceProxy extends ProxyBase implements IChatService
{
	
	
	public ChatServiceProxy(String targetIP, int targetPort) 
	{
		super(targetIP, targetPort);
	}
	
	public void newMessage(String message) throws HttpProxyException, UnknownHostException, IOException, HttpHeaderParsingException, HttpResponseParsingException
	{
		HttpRequestParser request = GetRequest(HttpRequestMethod.GET, String.format("/chat_service/new_message?p1=%s", message));
		
		HttpResponseParser response = SendData(request);
		
		// validate response
		if (!response.GetHttpResponseCodeObject().equals(HttpResponseCode.OK))
		{
			throw new HttpProxyException(String.format("Destination reported an error during add_me_as_a_friend: %s", response.GetHttpResponseCode()));
		}
	}
}
