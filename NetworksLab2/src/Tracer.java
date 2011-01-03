import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This is part of the bonus, it lets you write formatted trace messages from any place in the solution.
 * To use it, you will need to create a new tracer object, and generate it using 'getTracer(String name)' or using 'getTracerForThisClass()' .
 * getTracer(String name) will create a new tracer object with a prefix of the name you give it as a parameter
 * getTracerForThisClass() will create a new tracer object with a prefix of the calling class name
 * 
 * After you have a tracer instance, you can use 'TraceToConsole(String message)' to write a formatted message to the trace, which includes the prefix and a timestamp
 * 
 * NOTE: setting TRACE_ON to false will turn off all trace messages!
 */
public class Tracer {

	// Same comment from Starter about ifdef 
	private static final boolean TRACE_ON = true;
	
	// the name that will be written in the trace messages
	private String m_Name;
	private final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	
	private Tracer(String name)
	{
		m_Name = name;
	}
	
	/**
	 * Gets a new named tracer
	 * @param name The name of this tracer
	 * @return A Tracer object
	 */
	public synchronized static Tracer getTracer(String name)
	{
		return new Tracer(name);
	}
	
	/**
	 * Gets a new tracer for this class (named for this class)
	 * @return A Tracer object
	 */
	public synchronized static Tracer getTracerForThisClass()
	{
		// get the calling class name from the stack trace
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		StackTraceElement callingElement = st[2];
		return Tracer.getTracer(callingElement.getClassName());
	}
	
	/**
	 * Gets the current time
	 * @return the time
	 */
	private String getTime()
	{
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_NOW);
        Date date = new Date();
        return dateFormat.format(date);
	}
	
	/**
	 * Outputs a trace message to the console
	 * @param message The message to write to the console
	 */
	public synchronized void TraceToConsole(String message)
	{
		if (TRACE_ON)
		{
			System.out.println(String.format("[%s] %s: %s", getTime(), m_Name, message));
		}
	}
	
	/**
	 * Outputs a trace messages to the console, each in a new line
	 * @param messages The messages to write to the console
	 */
	public synchronized void TraceToConsole(String[] messages)
	{
		if (TRACE_ON)
		{
			StringBuilder sb = new StringBuilder();
			
			for (String s : messages)
			{
				sb.append(s);
				sb.append("\r\n");
			}
			
			this.TraceToConsole(sb.toString());
		}
	}
}
