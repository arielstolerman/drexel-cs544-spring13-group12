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
 * File name: Server.java
 * 
 * Purpose:
 * Main class for starting a server. The server is hardcoded to port 7070. This
 * implementation is for demonstration purposes only, therefore it initializes a
 * random house which it maintains throughout the entire run.
 * The server initializes a connection listener which handles incoming
 * connections.
 * 
 * Relevant requirements (details in the file):
 * - SERVICE
 * 
 * =============================================================================
 */

package server;

import devices.House;
import devices.RandomHouseFactory;

public class Server {
	
	// server static configuration
	
	/**
	 * Server protocol version
	 */
	public static final String VERSION = "RSHC 0001";
	/**
	 * Random house generation seed
	 */
	private static final long HOUSE_GEN_SEED = 4;
	/**
	 * Maximum number of devices to generate a random house with
	 */
	private static final int MAX_DEVICES_PER_TYPE = 3;
	/**
	 * Socket listen timeout
	 */
	public static final int LISTEN_TIMEOUT_MS = 1000;
	/**
	 * Default server port
	 */
	public static final int DEFAULT_PORT = 7070;
	
	/**
	 * Main method to initialize server.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// generate house
		RandomHouseFactory rhf =
				new RandomHouseFactory(HOUSE_GEN_SEED, MAX_DEVICES_PER_TYPE);
		House house = rhf.createHouse();
		house.prettyPrint();
		System.out.println();
		
		/*
		 * SERVICE
		 * initialize server
		 */
		Thread connectionListener = new Thread(new ConnectionListener(house));
		connectionListener.start();
	}
}
