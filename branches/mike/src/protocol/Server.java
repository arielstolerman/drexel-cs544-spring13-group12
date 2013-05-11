package protocol;

public class Server {
	private static Thread connectionListener;
	
	public static void main(String[] args) throws Exception {
		connectionListener = new Thread(new ConnectionListener());
		connectionListener.start();
	}
}
