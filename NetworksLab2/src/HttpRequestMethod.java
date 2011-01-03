/**
 * The Request method ENUM
 * 
 */
public enum HttpRequestMethod {
	/**
	 * The GET method means retrieve whatever information (in the form of an
	 * entity) is identified by the Request-URI. If the Request-URI refers to a
	 * data-producing process, it is the produced data which shall be returned
	 * as the entity in the response and not the source text of the process,
	 * unless that text happens to be the output of the process.
	 */
	GET,

	/**
	 * The POST method is used to request that the origin server accept the
	 * entity enclosed in the request as a new subordinate of the resource
	 * identified by the Request-URI in the Request-Line.
	 */
	POST,

	/**
	 * The TRACE method is used to invoke a remote, application-layer loop- back
	 * of the request message. The final recipient of the request SHOULD reflect
	 * the message received back to the client as the entity-body of a 200 (OK)
	 * response.
	 */
	TRACE
}
