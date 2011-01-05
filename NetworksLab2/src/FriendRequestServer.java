import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class FriendRequestServer implements Runnable {

	private final static String CONFIGURATION_FILE = "config.ini";
	
	private static final Tracer tracer = Tracer.getTracerForThisClass();
	
	private ConfigParser m_Configuration;
	private int m_portNumber;
	private boolean m_IsStopped;
	
	private DatagramSocket m_Server;

	public FriendRequestServer() throws ConfigException
	{
		this(null);
	}
	
	public FriendRequestServer(String configFile) throws ConfigException
	{
		try {
			if (configFile == null) {
				configFile = CONFIGURATION_FILE;
			}

			// initialize configuration
			m_Configuration = new ConfigParser(configFile);

			// get the port number
			String port = m_Configuration.GetValue("friends_group_port");
			port = port.split(":")[0];

			// set the port number
			m_portNumber = Integer.parseInt(port);
			
			FriendService.get_instance().SetFriendServerPort(m_portNumber);
			
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
		if (this.m_Server != null)
		{
			this.m_Server.close();
		}
		
		this.m_IsStopped = true;
	}
	
	@Override
	public synchronized void run() {
		
		tracer.TraceToConsole("******* FriendRequestServer starting up!! *******");
		
		// try to bind the server to the socket
		try {
			m_Server = new DatagramSocket(m_portNumber);
			tracer.TraceToConsole("Datagram socket is now opened");
		} catch (IOException e) {
			System.err.println(String.format(
					"Error opening the socket on port %d!", m_portNumber));
		}
		
		byte[] receiveData = new byte[256];
		
		// run while not stopped
		while (m_Server != null && !isStopped()) {
			
			try {

				tracer.TraceToConsole("Server going into listening mode!");
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				// next line blocks
				m_Server.receive(receivePacket);

				// trace
				tracer.TraceToConsole("A new friend request has arrived!");
				
				// are we talking to ourselves?
//				if (receivePacket.getAddress().getHostAddress().equals(InetAddress.getLocalHost().getHostAddress()) ||
//						receivePacket.getAddress().getHostAddress().equals("127.0.0.1"))
				if (receivePacket.getAddress().getHostAddress().equals(InetAddress.getLocalHost().getHostAddress()))
				{
					tracer.TraceToConsole("Got a friend request from ourselves");
				}
				else
				{
					// process request in a new thread
					new Thread(new FriendRequestThread(receivePacket.getData())).start();
				}
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
