import java.io.IOException;
import java.net.UnknownHostException;

public class FriendServiceProxy extends ProxyBase implements IFriendService
{

	public FriendServiceProxy(String friendIP, int friendPort) 
	{
		super(friendIP, friendPort);
	}

	public String AddMeAsAFriendRequest(String friends) throws UnknownHostException, IOException, HttpHeaderParsingException, HttpResponseParsingException, HttpProxyException
	{
		HttpRequestParser request = GetRequest(HttpRequestMethod.GET, "/friends_service/add_me_as_friend_request", friends);
		
		HttpResponseParser response = SendData(request);
		
		// validate response
		if (!response.GetHttpResponseCodeObject().equals(HttpResponseCode.OK))
		{
			throw new HttpProxyException(String.format("Destination reported an error during add_me_as_friend_request: %s", response.GetHttpResponseCode()));
		}
		
		return new String(response.GetContent());
	}

	public String AckFriendRequest(String friends) throws UnknownHostException, IOException, HttpHeaderParsingException, HttpResponseParsingException, HttpProxyException
	{
		HttpRequestParser request = GetRequest(HttpRequestMethod.GET, "/friends_service/ack_friend_request", friends);
		
		HttpResponseParser response = SendData(request);
		
		// validate response
		if (!response.GetHttpResponseCodeObject().equals(HttpResponseCode.OK))
		{
			throw new HttpProxyException(String.format("Destination reported an error during ack_friend_request: %s", response.GetHttpResponseCode()));
		}
		
		return new String(response.GetContent());
	}

	
}
