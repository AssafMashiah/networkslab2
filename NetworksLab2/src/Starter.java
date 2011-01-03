/**
 * This is our main, it does nothing but start a single server with the default configuration time
 */
public class Starter {
	
	// no ifdef in java :( we wanted to show the stack trace only if we are in debug mode
	// the only option to do that in java is to define the below, and java will not compile the code if its set to false
	// the variable name can be anything you want it to be
	private static final boolean DEBUG = false;
	
	public static void main(String[] args) {
		DispatcherServer server = null;
		FriendRequestServer friend_server = null;
		
		try {
			server = new DispatcherServer();
			new Thread(server).start();
			
			friend_server = new FriendRequestServer();
			new Thread(friend_server).start();
			
			System.in.read();
		} catch (Exception e) {
			// #ifdef :p
			if (DEBUG)
			{
				e.printStackTrace();
			}
			// #endif
		}
		finally
		{
			if (server != null)
			{
				server.stop();
			}
		}
	}
}
