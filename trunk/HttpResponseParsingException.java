public class HttpResponseParsingException extends Exception {

	/**
	 * generated UID.. man, man! i'm on a horse 
	 */
	private static final long serialVersionUID = -4514071361784050840L;

	/**
	 * Should be thrown when an error occurred while handling the response 
	 * @param msg Exception details for the puny human
	 */
	public HttpResponseParsingException(String msg) {
		super(msg);
	}
	
	/**
	 * Should be thrown when an error occurred while handling the response 
	 * @param msg Exception details for the puny human
	 * @param inner The inner exception
	 */
	public HttpResponseParsingException(String msg, Exception inner) {
		super(msg, inner);
	}

}
