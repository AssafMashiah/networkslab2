public class HttpHeaderParsingException extends Exception {

	/**
	 * generated UID.. man, man! i'm on a horse 
	 */
	private static final long serialVersionUID = -4512081361784050840L;

	/**
	 * Should be thrown when an error occurred while handling the headers
	 * @param msg Exception details for the puny human
	 */
	public HttpHeaderParsingException(String msg) {
		super(msg);
	}
	
	/**
	 * Should be thrown when an error occurred while handling the headers
	 * @param msg Exception details for the puny human
	 * @param inner The inner exception
	 */
	public HttpHeaderParsingException(String msg, Exception inner) {
		super(msg, inner);
	}
}
