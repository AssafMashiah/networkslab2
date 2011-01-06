import java.io.IOException;

/**
 * 
 * There is only one of me!!! <br>
 * The Echo Service will echo the input it receives<br>
 * The get_main_page will get the main page!<br>
 * 
 */
public class CommandsService implements ICommandsService {
	private static CommandsService m_Instance = null;
	
	private static final Tracer tracer = Tracer.getTracerForThisClass();

	private final String FROM_WHO = "from server";

	private CommandsService() {
	}

	public static synchronized CommandsService get_instance() {
		if (m_Instance == null) {
			m_Instance = new CommandsService();
		}

		return m_Instance;
	}

	/**
	 * Calls a function in our commands service
	 * @param functionName The function name to call
	 * @param params The params for this function
	 * @return The output of the function processing
	 * @throws HttpServiceException If there was an internal processing error
	 */
	public String callFunction(Functions functionName, String[] params) throws HttpServiceException {
		StringBuilder retVal;
		try
		{
			retVal = new StringBuilder();
			
			switch (functionName) {
			case echo:
				retVal.append(echo(params[0]));
				break;
			case get_main_page:
				retVal.append(getMainPage());
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

	/**
	 * Intelligent echo algorithm (its called the knock knock algorithm) The
	 * theory is very simple: 
	 * 1. client to server -> knock knock 
	 * 2. server to client -> who's there? 
	 * 3. client to server -> me! 
	 * 4. server to client -> from server: me!
	 * 
	 * @param param
	 *            This will be echoed
	 * @return return (echo, get it?)
	 */
	private String echo(String param) 
	{
		tracer.TraceToConsole("Echo() called with param value: " + param);
		return String.format("%s: %s", FROM_WHO, param);
	}
	
	/**
	 * Gets the main page
	 * @return A HTML string of the main page to display
	 * @throws IOException If there was a problem accessing the template
	 */
	private String getMainPage() throws IOException
	{
		tracer.TraceToConsole("GetMainPage() called");
		HTMLTemplate t = new HTMLTemplate("mainpage.html");
		return t.CompileTemplate();
	}
}
