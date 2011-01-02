
public interface ICommandsService {
	
	public enum Functions {
		echo,
		get_main_page
	}
	
	public String callFunction(ICommandsService.Functions functionName, String[] params) throws HttpServiceException;
}
