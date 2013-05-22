package protocol;

import java.net.Socket;

import common.Util;

import devices.House;
import devices.RandomHouseFactory;

public class Server {
	
	// server static configuration
	private static Thread connectionListener;
	public static final String VERSION = "0000";
	private static final long HOUSE_GEN_SEED = 4;
	public static final int LISTEN_TIMEOUT_MS = 1000;
	public static final String IP = "127.0.0.1";
	public static final int PORT = 7070;
	private static House HOUSE;
	
	/**
	 * Main method to initialize server.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// generate house
		RandomHouseFactory rhf = new RandomHouseFactory(HOUSE_GEN_SEED);
		HOUSE = rhf.createHouse();
		HOUSE.prettyPrint();
		System.out.println();
		
		// initialize server
		connectionListener = new Thread(new ConnectionListener(HOUSE));
		connectionListener.start();
		System.out.println(Util.dateTime() + " -- Server started\n");
	}
	
	/**
	 * @return a socket for a client to connect to the server with.
	 * @throws Exception
	 */
	public static Socket getRSHCSocket() throws Exception {
		return new Socket(IP, PORT);
	}
}
