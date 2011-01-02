/**
 * The exception that comes from the 
 *
 */
public class HttpServiceException extends Exception {

	/**
	 * lucky number!
	 */
	private static final long serialVersionUID = -5233016161140101159L;

	public HttpServiceException(String msg, Exception inner) {
		super(msg, inner);
	}
}
