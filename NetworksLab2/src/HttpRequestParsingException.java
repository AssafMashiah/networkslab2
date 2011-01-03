public class HttpRequestParsingException extends Exception {

	/**
	 * generated UID.. man, man! i'm on a horse 
	 */
	private static final long serialVersionUID = -4514081361784050840L;

	/**
	 * Should be thrown when an error occurred while handling the request 
	 * @param msg Exception details for the puny human
	 */
	public HttpRequestParsingException(String msg) {
		super(msg);
	}
	
	/**
	 * Should be thrown when an error occurred while handling the request 
	 * @param msg Exception details for the puny human
	 * @param inner The inner exception
	 */
	public HttpRequestParsingException(String msg, Exception inner) {
		super(msg, inner);
	}

}
