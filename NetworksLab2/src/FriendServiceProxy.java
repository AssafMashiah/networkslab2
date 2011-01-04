import java.io.IOException;
import java.net.UnknownHostException;



public class FriendServiceProxy extends ProxyBase implements IFriendService{

	public FriendServiceProxy()
	{
		
	}
	
	public FriendServiceProxy(String dest, int port)
	{
		this.DestinationIP = dest;
		this.DestinationPort = port;
	}
	
	public String AddMeAsAFriendRequest(String friends) throws UnknownHostException, IOException, HttpHeaderParsingException, HttpResponseParsingException, HttpProxyException
	{
		HttpRequestParser request = GetRequest(HttpRequestMethod.GET, "/friends_server/add_me_as_a_friend", friends);
		
		HttpResponseParser response = SendData(request);
		
		// validate response
		if (!response.GetHttpResponseCodeObject().equals(HttpResponseCode.OK))
		{
			throw new HttpProxyException(String.format("Destination reported an error during add_me_as_a_friend: %s", response.GetHttpResponseCode()));
		}
		
		return new String(response.GetContent());
	}
	
}
