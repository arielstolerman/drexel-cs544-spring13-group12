package server;

import common.Util;

import devices.House;
import devices.RandomHouseFactory;

public class Server {
	
	// server static configuration
	public static final String VERSION = "RSHC 0001";
	private static final long HOUSE_GEN_SEED = 4;
	public static final int LISTEN_TIMEOUT_MS = 1000;
	public static final int PORT = 7070;
	
	/**
	 * Main method to initialize server.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// generate house
		RandomHouseFactory rhf = new RandomHouseFactory(HOUSE_GEN_SEED);
		House house = rhf.createHouse();
		house.prettyPrint();
		System.out.println();
		
		// initialize server
		Thread connectionListener = new Thread(new ConnectionListener(house));
		connectionListener.start();
		System.out.println(Util.dateTime() + " -- Server started\n");
	}
}
