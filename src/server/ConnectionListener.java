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
 * 
 * =============================================================================
 */

package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;

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
	 * List of active connections
	 */
	private List<ServerComm> sList = new ArrayList<ServerComm>();
	/**
	 * Flag to mark shutdown
	 */
	private boolean shutdown = false;
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
	
	public void run() {
		try {
			ServerSocket servSocket = new ServerSocket(Server.PORT);
			servSocket.setSoTimeout(Server.LISTEN_TIMEOUT_MS);
			System.out.println(Util.dateTime() + " -- Server started\n");
			while (true) {
				Socket commSocket = null;
				while (commSocket == null) {
					try {
						commSocket = servSocket.accept();
					} catch (SocketTimeoutException e) {
						if (shutdown) {
							servSocket.close();
							return;
						}
					}
				}
				ServerComm serverComm = new ServerComm(
						ID_COUNTER++,
						this,
						commSocket,
						new ServerDFA(house, this));
				sList.add(serverComm);
				Thread thread = new Thread(serverComm);
				thread.start();
			}
		} catch (Exception e) {
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
}
