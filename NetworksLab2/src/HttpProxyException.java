/**
 * The exception that comes from the 
 *
 */
public class HttpProxyException extends Exception {

	/**
	 * lucky number!
	 */
	private static final long serialVersionUID = -5133016161140101159L;

	public HttpProxyException(String msg) {
		super(msg);
	}
	
	public HttpProxyException(String msg, Exception inner) {
		super(msg, inner);
	}
}
