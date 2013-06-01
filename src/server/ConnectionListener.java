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
 * File name: ConnectionListener.java
 * 
 * Purpose:
 * Handles multiple connections to clients, by maintaining a list of ServerComm
 * and listening to incoming connections from clients.
 * 
 * Relevant requirements (details in the file):
 * - CONCURRENT
 * - UI
 * 
 * =============================================================================
 */

package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.ConcurrentSkipListSet;

import common.Util;

import protocol.*;


import devices.House;

public class ConnectionListener implements Runnable {
	
	// fields
	
	/**
	 * Counter to assign unique numeric identifiers to incoming connections
	 */
	private static int ID_COUNTER = 0;
	/**
	 * Set of active connections
	 */
	private ConcurrentSkipListSet<ServerComm> sList =
			new ConcurrentSkipListSet<ServerComm>();
	/**
	 * Flag to mark shutdown
	 */
	private volatile boolean shutdown = false;
	/**
	 * House maintained by the server
	 */
	private House house;
	
	// constructors
	
	/**
	 * Constructs a new connection listener with the given attached house.
	 * @param house
	 */
	public ConnectionListener(House house) {
		this.house = house;
	}
	
	@Override
	public void run() {
		try {
			// initialize listen socket
			ServerSocket servSocket = new ServerSocket(Server.DEFAULT_PORT);
			servSocket.setSoTimeout(Server.LISTEN_TIMEOUT_MS);
			System.out.println(Util.dateTime() + " -- Server started\n");
			
			/*
			 * CONCURRENT
			 * UI
			 * start shutdown listener thread
			 */
			startShutdownListener();
			
			// loop and listen to incoming connections
			
			/*
			 * CONCURRENT
			 * the following loop listens to incoming connections, and when it
			 * receives one it launches a server communication handler for it
			 * and continues listening, to handle multiple clients in parallel.
			 */
			
			while (true) {
				Socket commSocket = null;
				while (commSocket == null) {
					try {
						commSocket = servSocket.accept();
					} catch (SocketTimeoutException e) {
						// process shutdown
						if (shutdown) {
							servSocket.close();
							// close all open connections
							for (ServerComm sc: sList)
								sc.markShutdown();
							while (!sList.isEmpty()) {}
							return;
						}
					}
				}
				// initialize server communication handler from accepted
				// connection and launch it
				ServerComm serverComm = new ServerComm(
						ID_COUNTER++,
						this,
						commSocket,
						new ServerDFA(house, this));
				sList.add(serverComm);
				Thread thread = new Thread(serverComm);
				thread.start();
			}
		}
		catch (BindException e) {
			System.out.println("Port " + Server.DEFAULT_PORT + " already bound");
			System.out.println("Cannot start RSHC server");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Raises the shutdown flag, such that on the next shutdown check the
	 * connection will terminate.
	 */
	public void shutdown() {
		shutdown = true;
	}

	/**
	 * Removes the given server communication from the list of communications.
	 * @param serverComm
	 */
	public void remove(ServerComm serverComm) {
		this.sList.remove(serverComm);		
	}
	
	/**
	 * Appends the given update message to the pending messages to send on each
	 * server communication except the given one (which is connected to the
	 * client that generated the action and caused the update; that client will
	 * receive a confirm message).
	 * @param updateMsg
	 * @param serverComm
	 */
	public void broadcast(Message updateMsg, ServerComm serverComm) {
		for (ServerComm s : sList) {
			if (s == serverComm) continue;
			s.appendToSendQueue(updateMsg);
		}
	}
	
	/**
	 * Initiate server shutdown command listener. Listens to standard input for
	 * server user shutdown command.
	 */
	private void startShutdownListener() {
		/*
		 * CONCURRENT
		 * UI
		 * handles server shutdown concurrently with listening to new client
		 * connections
		 * presents the server user an interface for shutting down the server
		 */
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// initialize reader
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				boolean shut = false;
				String line;
				// read until received shutdown command
				while (!shut) {
					System.out.println(">>> Press S at anytime to shutdown server");
					try {
						line = br.readLine();
						// received shutdown
						if (line.trim().equalsIgnoreCase("s")) {
							shut = true;
						}
						// retry
						else {
							System.out.println(">>> Unrecognized command: " +
									line);
						}
					} catch (IOException e) {
						System.out.println("unable to read input, retrying");
					}
				}
				// finally shutdown
				shutdown();
			}
		}).start();
	}
}
