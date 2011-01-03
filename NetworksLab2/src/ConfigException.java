
public class ConfigException extends Exception {

	/**
	 * this is the default ID
	 */
	private static final long serialVersionUID = 1L;

	public ConfigException(String msg)
	{
		super(msg);
	}
	
	public ConfigException(String msg, Exception inner)
	{
		super(msg, inner);
	}
}
