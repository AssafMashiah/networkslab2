/**
 * 
 * Represents all supported HTTP versions by this Parser
 *
 */
public enum HttpVersion {
	One("1.0"),
	OneDotOne("1.1");
	
	private String m_Version;
	
	private HttpVersion(String version)
	{
		m_Version = version;
	}
	
	/**
	 * 
	 * @return HTTP/1.x (according to the actual version)
	 */
	public String GetHTTPVersion()
	{
		return String.format("HTTP/%s", m_Version); 
	}
	
	@Override
	public String toString() {
		return m_Version;
	}
}

