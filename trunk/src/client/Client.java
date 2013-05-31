/* =============================================================================
 * CS544 - Computer Networks
 * Drexel University, Spring 2013
 * Protocol Implementation: Remote Smart House Control
 * Group 12:
 * - Ryan Corcoran
 * - Amber Heilman
 * - Michael Mersic
 * - Ariel Stolerman
 * 
 * -----------------------------------------------------------------------------
 * File name: Client.java
 * 
 * Purpose:
 * Main class for starting a client. Provides the main method to initialize a
 * connection to the server, either with a client CLI or in test mode (allows
 * sending raw messages to the server).
 * Behavior is controlled with command line arguments; please run with no
 * arguments to receive description of expected arguments.
 * 
 * Relevant requirements (details in the file):
 * - CLIENT
 * 
 * =============================================================================
 */

package client;

public class Client {
	
	// client configuration
	/**
	 * default timeout for client socket listener
	 */
	public static final int LISTEN_TIMEOUT_MS = 1000;
	/**
	 * default client protocol version
	 */
	public static final String VERSION = "RSHC 0001";
	/**
	 * default host to connect to
	 */
	private static final String DEFAULT_HOST = "127.0.0.1";
	/**
	 * default port to connect to
	 */
	private static final int DEFAULT_PORT = 7070;
	
	/**
	 * Main method to startup a client connection to the server. Arguments:
	 * <ul>
	 * 	<li>
	 * 	<code>[-host &lt;host&gt;]</code>: optional; specify the host to connect to.
	 * 	default value: 127.0.0.1
	 * 	</li>
	 * 	<li>
	 * 	<code>[-port &lt;port&gt;]</code>: optional; specify the port to connect to.
	 * 	default value: 7070
	 * 	</li>
	 * 	<li>
	 * 	<code>-login &lt;user&gt;:&lt;password&gt;</code>: specify client username
	 * 	and password. MUST be specified if not in test mode.
	 * 	</li>
	 * 	<li>
	 * 	<code>-test</code>: run in a client in test mode, which allows sending the
	 * 	server raw messages for testing purposes. If given, does not have to specify
	 *  username and password.
	 * 	</li>
	 * </ul>
	 * @param args client command line arguments.
	 */
	public static void main(String args[]) {
		ClientComm cc = null;
		
		// -------------
		// configuration
		// -------------
		
		// host
		String host = DEFAULT_HOST;
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equalsIgnoreCase("-host")) {
				host = args[i + 1];
				break;
			}
		}
		// port
		int port = DEFAULT_PORT;
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equalsIgnoreCase("-port")) {
				try {
					port = Integer.parseInt(args[i + 1]);
				} catch (NumberFormatException e) {
					printUsageAndExit("Illegal port given: " + args[i + 1]);
				}
				break;
			}
		}
		// user and password
		String user = null, pass = null;
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equalsIgnoreCase("-login")) {
				String[] s = args[i + 1].split(":");
				if (s.length != 2) {
					printUsageAndExit(
							"Illegal format of user and password arguments");
				}
				user = s[0].trim();
				pass = s[1].trim();
				break;
			}
		}
		// test client
		boolean test = false;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("-test")) {
				test = true;
				break;
			}
		}
		if (!test && (user == null || pass == null))
			printUsageAndExit("user and password not given");
		
		
		// ------------
		// start client
		// ------------
		
		// start in test mode
		if (test) {
			System.out.println(">>> TEST MODE");
			cc = new ClientCommTester(host, port);
		}
		// start in standard mode
		else {
			cc = new ClientCommCLI(host, port, user, pass);
		}
		Thread T = new Thread(cc);
		T.start();
	}
	
	/**
	 * Prints the given error message along with client run usage, and exits the
	 * program.
	 * @param message error message.
	 */
	private static void printUsageAndExit(String message) {
		System.out.println(message);
		System.out.println("Expected arguments:");
		System.out.println("[-host <host>] [-port <port>] -login <user>:<pass>");
		System.out.println("E.g.: -host 127.0.0.1 -port 7070 -login myname:mypassword");
		System.out.println("*  Default host: 127.0.0.1");
		System.out.println("*  Default port: 7070");
		System.out.println("Or to run in test mode (send raw messages to the server):");
		System.out.println("-test");
		System.exit(-1);
	}
}
