import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class DispatcherServer implements Runnable {

	private final static String DEFAULT_SERVER_NAME = "DispatcherServer/0.0";
	// in msec
	private final static int DEFAULT_TIMEOUT = 30000;

	private final static String CONFIGURATION_FILE = "config.ini";
	
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	private String m_ServerName;

	private ConfigParser m_Configuration;
	private int m_portNumber;
	private boolean m_IsStopped;

	private int m_timeout;
	
	private ServerSocket m_Server;

	/**
	 * Creates a new dispatcher server
	 */
	public DispatcherServer() throws ConfigException {
		this(null);
	}
	
	/**
	 * Creates a new dispatcher server
	 * 
	 * @param configFile
	 *            The configuration file for this server, if null will default
	 *            to "config.ini"
	 */
	public DispatcherServer(String configFile) throws ConfigException {
		try {
			if (configFile == null) {
				configFile = CONFIGURATION_FILE;
			}

			// initialize configuration
			m_Configuration = new ConfigParser(configFile);

			// get the port number
			String port = m_Configuration.GetValue("port");
			port = port.split(":")[0];

			// set the port number
			m_portNumber = Integer.parseInt(port);
			
			// optional configuration
			m_ServerName = m_Configuration.GetValue("servername");
			
			String timeout = m_Configuration.GetValue("requesttimeout");
			
			// handle default server name
			if (m_ServerName == null)
			{
				m_ServerName = DEFAULT_SERVER_NAME;
			}
			
			// handle default / timeout assignment (this must run!!, we do string->int here)
			if (timeout == null)
			{
				m_timeout = DEFAULT_TIMEOUT;
			}
			else
			{
				m_timeout = Integer.parseInt(timeout);
			}
			tracer.TraceToConsole("Timeout was set to: " + m_timeout);
			
			// set my info
			String nickname = m_Configuration.GetValue("nickname");
			String IP = InetAddress.getLocalHost().getHostAddress();
			FriendService.get_instance().SetMyInfo(new FriendInfo(nickname, IP, m_portNumber));
			FriendService.get_instance().SetDispatcherPort(m_portNumber);
			
			// set p2p root dir
			FilesService.get_instance().SetRootDir(m_Configuration.GetValue("file_sharing_dir"));
		} catch (Exception e) {
			String errMessage = String.format(
					"Error loading Configuration from %s!", configFile);
			tracer.TraceToConsole(errMessage);
			throw new ConfigException(errMessage, e);
		}
	}

	/**
	 * 
	 * @return The current server state (running/stopped => false/true)
	 */
	private boolean isStopped() {
		return this.m_IsStopped;
	}

	/**
	 * Stop the current dispatcher
	 */
	public void stop() {
		this.m_IsStopped = true;
		tracer.TraceToConsole("Closing server. BYE BYE");
		tracer.stop();
		try {
			if (this.m_Server != null)
			{
				this.m_Server.close();
			}
		} catch (IOException e) {
			if (this.m_Server != null)
			{
				this.m_IsStopped = false;
			}
			throw new RuntimeException("Error closing server", e);
		}
	}
	
	@Override
	public synchronized void run() {
		
		tracer.TraceToConsole(String.format("******* %s operational!! *******", m_ServerName));
		
		// try to bind the server to the socket
		try {
			m_Server = new ServerSocket(m_portNumber);
			tracer.TraceToConsole("Server socket is now opened");
		} catch (IOException e) {
			System.err.println(String.format(
					"Error opening the socket on port %d!", m_portNumber));
		}
		
		// run while not stopped
		while (m_Server != null && !isStopped()) {
			Socket clientSocket = null;
			try {

				// trace
				tracer.TraceToConsole("Server going into listening mode!");

				// start listening
				clientSocket = this.m_Server.accept();
				clientSocket.setSoTimeout(m_timeout);

				// trace
				tracer.TraceToConsole("A new request has arrived!");
				
				// process request in a new thread
				new Thread(new DispatcherThread(clientSocket, m_ServerName)).start();
			} catch (IOException e) {
				if (isStopped()) {
					tracer.TraceToConsole("Server Stopped.");
					return;
				}
				throw new RuntimeException("Error accepting client connection",
						e);
			}
		}
	}
}
