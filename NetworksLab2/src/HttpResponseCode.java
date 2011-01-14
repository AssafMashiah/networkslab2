/**
 * This class implements the response Codes supported by the parsers
 */
public enum HttpResponseCode {
	OK("200", "OK"),
	MOVED_PERMANENTLY("301", "Moved Permanently"), 
	BAD_REQUEST("400", "Bad Request"), 
	NOT_FOUND("404", "Not Found"), 
	REQUEST_TIMEOUT("408", "Request Timeout"),
	INTERNAL_SERVER_ERROR("500", "Internal Server Error"), 
	NOT_IMPLEMENTED("501", "Not Implemented");

	// used later for the
	private String m_Code;
	private String m_Description;

	private HttpResponseCode(String code, String description) {
		m_Code = code;
		m_Description = description;
	}

	/**
	 * 
	 * @return The code for this status code (for example, 200)
	 */
	public String GetCode() {
		return m_Code;
	}

	/**
	 * 
	 * @return The description for this status code (for example, OK)
	 */
	public String GetDescription() {
		return m_Description;
	}

	/**
	 * Translate numeric code to enum value
	 * @param code
	 * @return
	 */
	public static HttpResponseCode GetResponseCode(int code)
	{
		HttpResponseCode retVal = null;
		switch (code)
		{
		case 200:
			retVal = HttpResponseCode.OK;
			break;
		case 301:
			retVal = HttpResponseCode.MOVED_PERMANENTLY;
			break;
		case 400:
			retVal = HttpResponseCode.BAD_REQUEST;
			break;
		case 404:
			retVal = HttpResponseCode.NOT_FOUND;
			break;
		case 408:
			retVal = HttpResponseCode.REQUEST_TIMEOUT;
			break;
		case 500:
			retVal = HttpResponseCode.INTERNAL_SERVER_ERROR;
			break;
		case 501:
			retVal = HttpResponseCode.NOT_IMPLEMENTED;
			break;
		}
		
		return retVal;
	}
	
	@Override
	public String toString() {

		return String.format("%s %s", m_Code, m_Description);
	}

}
