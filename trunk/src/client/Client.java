package client;

public class Client {
	public static final int LISTEN_TIMEOUT_MS = 1000;
	public static final String VERSION = "0000";

	public static void main(String args[]) {
		ClientComm cc = null;
		if (args.length == 2) {
			cc = ClientCommFactory.createClientCLI(args[0], Integer.parseInt(args[1]));
		} else {
			cc = ClientCommFactory.createTest(2);
		}
		
		Thread T = new Thread(cc);
		T.start();
	}
}
