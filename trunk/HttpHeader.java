
public class HttpHeader {
	
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	private String m_Name;
	private String m_Value;

	/**
	 * Construct a new header
	 * @param name  The name of the header
	 * @param value The value for this header
	 */
	public HttpHeader(String name, String value)
	{
		m_Name = name;
		m_Value = value;
	}
	
	/**
	 * 
	 * @return The name of the header
	 */
	public String GetName()
	{
		return m_Name;
	}
	
	/**
	 * 
	 * @return The value of the header
	 */
	public String GetValue()
	{
		// trace
		tracer.TraceToConsole(String.format("Reading value of header '%s'", m_Name));
		
		return m_Value;
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s", m_Name, m_Value);
	}
}
