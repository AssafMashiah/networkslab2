import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * This is a dispatcher thread for the dispatcher server. It handles the
 * connection once received from the client
 * All the logic be stored here...
 */
public class DispatcherThread implements Runnable {

	// socket should be visible if someone wants to extend the dispatcher-thread
	protected Socket m_ClientSocket;
	
	// for output of the 'Server: ' header
	private String m_ServerName;

	// used for trace messages
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	/**
	 * Starts a new dispatcher thread
	 * 
	 * @param clientSocket
	 *            The socket the request is coming on.
	 */
	public DispatcherThread(Socket clientSocket, String serverName) {
		this.m_ClientSocket = clientSocket;
		this.m_ServerName = serverName;
	}

	@Override
	public synchronized void run() {
		
		OutputStream output = null;
		HttpRequestParser request = null;
		HttpResponseParser response = null;
		try {
			BufferedReader input = getReaderFromSocket();
			output = m_ClientSocket.getOutputStream();

			// parse the request
			request = parseClientRequestHeader(input);
			request.GenerateQueryString();

			// trace the request
			tracer.TraceToConsole("The Request data is:\r\n" + request.toString());
			
			// If this is a TRACE request, the dispatcher should not do anything but echo back
			if (request.GetMethod().equals(HttpRequestMethod.TRACE))
			{
				response = generateTraceResponse(request);
			}
			else
			{
				response = generateNormalResponse(request);
			}

		} catch (SocketTimeoutException e) {
			tracer.TraceToConsole("Request timed out\n" + e.getMessage());
			response = generateHttpErrorResponse(HttpResponseCode.REQUEST_TIMEOUT, HttpVersion.One);
		} catch (SocketException e) {
			tracer.TraceToConsole("Socket error encountered\n" + e.getMessage());
		} catch (IOException e) {
			tracer.TraceToConsole("Socket error encountered\n" + e.getMessage());
		} catch (HttpNotImplementedException e) {
			// We got a non-supported method
			tracer.TraceToConsole("Error parsing the request");
			tracer.TraceToConsole(e.getMessage());

			response = generateHttpErrorResponse(HttpResponseCode.NOT_IMPLEMENTED, HttpVersion.One);
		} catch (HttpRequestParsingException e) {
			// We got a non-supported method
			tracer.TraceToConsole("Error parsing the request");
			tracer.TraceToConsole(e.getMessage());

			response = generateHttpErrorResponse(HttpResponseCode.BAD_REQUEST, HttpVersion.One);
		} catch (HttpHeaderParsingException e) {
			// We got a non-supported method
			tracer.TraceToConsole("Error parsing the request");
			tracer.TraceToConsole(e.getMessage());

			response = generateHttpErrorResponse(HttpResponseCode.BAD_REQUEST, HttpVersion.One);
		}

		// close the connection
		try {
			// if we could not get to a state where we generate a response...
			if (response == null)
			{
				response = generateHttpErrorResponse(HttpResponseCode.INTERNAL_SERVER_ERROR, HttpVersion.One);
			}
			
			// trace the response header
			tracer.TraceToConsole("Response headers are:\r\n" + response.HeaderString());
			
			output.write(response.toString().getBytes());
			output.flush();
			output.close();
		} catch (IOException e) {
			// this can happen if the connection is already dead, in which case
			// we do not really care aside from logging it happened
			tracer.TraceToConsole("Error writing or closing the http output stream");
		}
	}

	/**
	 * Gets the buffered reader from the socket
	 * @return A buffered reader to be used in the thread
	 * @throws UnsupportedEncodingException If the ASCII encoding is not supported in this system (won't happen..)
	 * @throws IOException If there is a problem getting the input stream from the socket
	 */
	private BufferedReader getReaderFromSocket() throws UnsupportedEncodingException, IOException
	{
		return new BufferedReader(new InputStreamReader(m_ClientSocket.getInputStream(), "ASCII"));
	}
	
	/**
	 * The request appears to be valid we will try to process it into a service
	 * @param request The request object
	 * @return The valid response, or an error response if there was a processing error
	 */
	private HttpResponseParser generateNormalResponse(HttpRequestParser request)
	{
		HttpResponseParser response;
		
		try
		{
			HttpURIData uriData = getServiceAndFunctionName(request.GetRequestedURI());
			
			// trace
			tracer.TraceToConsole(String.format("Service Name is: %s\nFunction Name is: %s", uriData.ServiceName, uriData.FunctionName));		
			
			// this is the response from the service
			String content = null;
			
			if (uriData.ServiceName == null)
			{
				response = new HttpResponseParser(HttpResponseCode.MOVED_PERMANENTLY);
				response.SetHttpVersion(request.GetHttpVersion());
				response.AddHeader(new HttpHeader("Location", "/commands_service/get_main_page"));
			}
			else
			{
				String[] params = null;
				params = request.GetQueryStringParams(HttpQueryStringType.BOTH).values().toArray(new String[0]);

				switch(HttpServices.valueOf(uriData.ServiceName))
				{
				case commands_service:
					content = CommandsService.get_instance().callFunction(CommandsService.Functions.valueOf(uriData.FunctionName), params);
					break;
				case commnads_service_proxy:
					CommandsServiceProxy proxy = new CommandsServiceProxy();
					proxy.DestinationIP = "127.0.0.1";
					proxy.DestinationPort = 10000;
					content = proxy.echo(params[0]);
					default:
						// 404 (cannot happen...)
				}
				
				// everything worked if we got here
				response = new HttpResponseParser(HttpResponseCode.OK);
				response.SetHttpVersion(request.GetHttpVersion());
				response.SetContent(content.getBytes());
				response.AddHeader(new HttpHeader("Content-Length", String.valueOf(response.GetContentSize())));
				response.AddHeader(new HttpHeader("Server", m_ServerName));
			}
			
		}
		catch (HttpRequestParsingException e)
		{
			tracer.TraceToConsole("server has encountered a 500 error");
			response = generateHttpErrorResponse(HttpResponseCode.INTERNAL_SERVER_ERROR, request.GetHttpVersion());
		}
		catch (HttpServiceException e)
		{
			tracer.TraceToConsole("server has encountered a 500 error");
			response = generateHttpErrorResponse(HttpResponseCode.INTERNAL_SERVER_ERROR, request.GetHttpVersion());
		}
		catch (HttpProxyException e)
		{
			tracer.TraceToConsole("Proxy has encountered an error from server: " + e.getMessage());
			response = generateHttpErrorResponse(HttpResponseCode.INTERNAL_SERVER_ERROR, request.GetHttpVersion());
		}
		catch (Exception e)
		{
			// we catch a general exception as everything we do is logic processing on the request data
			// any error means the resource was not valid in a way, which means a 404 error
			tracer.TraceToConsole("server has encountered a 404 error");
			
			response = generateHttpErrorResponse(HttpResponseCode.NOT_FOUND, request.GetHttpVersion());
		}
		
		return response;
	}
	
	/**
	 * Gets the service and function name from the URI
	 * @param requestURI The requested resource
	 * @return the Service name and the Function name object
	 */
	private HttpURIData getServiceAndFunctionName(String requestURI)
	{
		HttpURIData serviceFunctionNames = new HttpURIData();
				
		// The request appears to be valid.. lets check the request service
		int secondSlashLocation = requestURI.indexOf('/', 1);
		
		if (secondSlashLocation != -1)
		{	
			serviceFunctionNames.ServiceName = requestURI.substring(1, secondSlashLocation);
			
			// get the function name
			int questionMarkLocation = requestURI.indexOf('?');
			
			if (questionMarkLocation == -1)
			{
				serviceFunctionNames.FunctionName = requestURI.substring(secondSlashLocation + 1, requestURI.length());
			}
			else
			{
				serviceFunctionNames.FunctionName = requestURI.substring(secondSlashLocation + 1, questionMarkLocation);
			}
		}
		
		return serviceFunctionNames;
	}
	
	/**
	 * Generate a trace response from a trace request
	 * @param request the trace request
	 * @return the trace response
	 */
	private HttpResponseParser generateTraceResponse(HttpRequestParser request)
	{
		HttpResponseParser response = new HttpResponseParser(HttpResponseCode.OK);
		response.SetHttpVersion(request.GetHttpVersion());
		
		// set the data of the request to the response
		byte[] requestData = request.toString().getBytes();
		response.SetContent(requestData);
		
		// set the headers for the response 
		response.AddHeader(new HttpHeader("Content-Length", String.valueOf(requestData.length)));
		response.AddHeader(new HttpHeader("Content-type", "text/plain"));
		response.AddHeader(new HttpHeader("Server", m_ServerName));
		
		return response;
	}
	
	/**
	 * Generates an error response
	 * @param code The error code
	 * @param version The HTTP version
	 * @return A response object
	 */
	private HttpResponseParser generateHttpErrorResponse(HttpResponseCode code,
			HttpVersion version) {
		HttpResponseParser generatedResponse = new HttpResponseParser(code);

		generatedResponse.AddHeader(new HttpHeader("Content-Length", "0"));
		generatedResponse.AddHeader(new HttpHeader("Server",
				m_ServerName));
		generatedResponse.SetHttpVersion(version);

		return generatedResponse;
	}

	/**
	 * Creates a HTTP client request from a socket
	 * @param in The input stream from the socket
	 * @return The client request object
	 * @throws HttpRequestParsingException If the request header / request-line is malformed
	 * @throws IOException If there is a problem reading from the socket
	 * @throws HttpNotImplementedException If its a not implemented command (e.g, KUKU)
	 * @throws HttpHeaderParsingException If there was a problem with the headers
	 */
	private HttpRequestParser parseClientRequestHeader(BufferedReader in)
			throws HttpRequestParsingException, IOException,
			HttpNotImplementedException, HttpHeaderParsingException {
		HttpRequestParser newRequest = new HttpRequestParser();

		String clientRequest = newRequest.GetHeaderTextFromStream(in);
		
		// get lines again
		String[] lines = clientRequest.split(HttpParser.CRLF);

		// parse the Request-Line
		newRequest.HttpRequestLineParser(lines[0]);

		// the Request-Line is finished, lets do the headers
		int contentLength = newRequest.HttpHeadersParser(lines);

		if (contentLength > 0) {
			newRequest.SetContent(getContentFromRequestStream(contentLength, in));
		}

		return newRequest;
	}

	/**
	 * Reads the content off the request
	 * 
	 * @param contentLength
	 *            The content length
	 * @param in
	 *            The buffered reader
	 * @return The content data
	 * @throws IOException
	 *             If there was a problem while reading from the stream
	 */
	private byte[] getContentFromRequestStream(int contentLength, BufferedReader in)
			throws IOException {
		char[] ccontent = new char[contentLength];

		// we know exactly how much to read now
		if (in != null) {
			in.read(ccontent);
		}
		
		// we want to ensure the content is preserved with its encoding (which should be ASCII, but may not be?)
		byte[] content = new String(ccontent).getBytes();
		

		return content;
	}
}
