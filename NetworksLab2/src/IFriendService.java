
public interface IFriendService {
	
	// All legal functions the friend server can get
	public enum Functions {
		add_me_as_friend_request,
		get_Friends_list_page,
		add_friend_source,
		remove_friend,
		look_for_friends,
		get_friends,
		ack_friend_request
	}
	
	public String callFunction(IFriendService.Functions functionName, String[] params) throws HttpServiceException;
}
