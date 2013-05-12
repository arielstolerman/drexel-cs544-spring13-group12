package protocol;

import devices.RandomHouseFactory;

public class Server {
	private static Thread connectionListener;
	public static final String version = "0000";
	
	public static void main(String[] args) throws Exception {
		RandomHouseFactory rhf = new RandomHouseFactory(2);
		connectionListener = new Thread(new ConnectionListener(rhf.createHouse()));
		connectionListener.start();
	}
}
