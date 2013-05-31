package client;

public class Client {
	
	// client configuration
	public static final int LISTEN_TIMEOUT_MS = 1000;
	public static final String VERSION = "RSHC 0001";
	private static final String DEFAULT_HOST = "127.0.0.1";
	private static final int DEFAULT_PORT = 7070;
	
	public static void main(String args[]) {
		ClientComm cc = null;
		
		// configuration
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
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equalsIgnoreCase("-test")) {
				test = true;
				break;
			}
		}
		if (!test && (user == null || pass == null))
			printUsageAndExit("user and password not given");
		
		// start test mode
		if (test) {
			System.out.println(">>> TEST MODE");
			cc = new ClientCommTester(host, port);
		}
		// start standard mode
		else {
			cc = new ClientCommCLI(host, port, user, pass);
		}
		Thread T = new Thread(cc);
		T.start();
	}
	
	private static void printUsageAndExit(String message) {
		System.out.println(message);
		System.out.println("Expected arguments:");
		System.out.println("[-host <host>] [-port <port>] -login <user>:<pass>");
		System.out.println("E.g.: -host 127.0.0.1 -port 7070 -login myname:mypassword");
		System.out.println("Default host: 127.0.0.1");
		System.out.println("Default port: 7070");
		System.exit(-1);
	}
}
