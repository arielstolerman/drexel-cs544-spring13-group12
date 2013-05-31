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
 * File name: 
 * 
 * Purpose:
 * 
 * 
 * Relevant requirements (details in the file):
 * - 
 * 
 * =============================================================================
 */

package server;

import devices.House;
import devices.RandomHouseFactory;

public class Server {
	
	// server static configuration
	public static final String VERSION = "RSHC 0001";
	private static final long HOUSE_GEN_SEED = 4;
	private static final int MAX_DEVICES_PER_TYPE = 3;
	public static final int LISTEN_TIMEOUT_MS = 1000;
	public static final int PORT = 7070;
	
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
		
		// initialize server
		Thread connectionListener = new Thread(new ConnectionListener(house));
		connectionListener.start();
	}
}
