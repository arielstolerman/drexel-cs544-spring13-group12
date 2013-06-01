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
 * File name: ServerComm.java
 * 
 * Purpose:
 * A class for a server communication handler object. A ServerComm object is
 * created and assigned by ConnectionListener to every incoming client
 * connection; it then listens to the client commands and in charge of handling
 * the communication, similar to ClientComm for the client.
 * 
 * Relevant requirements (details in the file):
 * - SERVICE
 * 
 * =============================================================================
 */

package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import common.Util;

import protocol.*;


public class ServerComm implements Runnable {
	
	// fields
	
	/**
	 * The unique numeric identifier of the client handled by this server
	 * communication handler
	 */
	private int id;
	/**
	 * The parent connection listener
	 */
	private ConnectionListener connectionListener;
	/**
	 * Flag for shutdown
	 */
	private boolean shutdown = false;
	/**
	 * The connection socket
	 */
	private Socket socket;
	/**
	 * DFA to be used to track protocol states and process messages
	 */
	private DFA dfa;
	
	/**
	 * A queue to maintain update messages generated by other server communication
	 * handlers in response to their client's actions
	 */
	private ConcurrentLinkedQueue<Message> sendQueue = new ConcurrentLinkedQueue<>();
	
	// constructors
	
	/**
	 * Constructs a new server communication handler with the given client id,
	 * parent connection listener, connection socket and server DFA.
	 * @param id the unique numeric identifier of the handled client
	 * @param cl the parent connection listener
	 * @param s the connection socket
	 * @param dfa the server protocol DFA
	 */
	public ServerComm(int id, ConnectionListener cl, Socket s, ServerDFA dfa) {
		this.id = id;
		this.connectionListener = cl;
		this.socket = s;
		this.dfa = dfa;
		// attach this server communication handler to the DFA
		dfa.setServerComm(this);
	}
	
	/*
	 * SERVICE
	 * main thread to handle server communication to the client, parse and
	 * respond to the client messages etc.
	 */
	
	@Override
	public void run() {
		try {
			// initialize readers and writers
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			socket.setSoTimeout(Server.LISTEN_TIMEOUT_MS);
			
			System.out.println(Util.dateTime() + " -- Connection with C" + id +
					" initiated");
			
			// read messages
			while (true) {
				// read user input
				String inBuff = null;
				while (inBuff == null) {
					try {
						inBuff = br.readLine();
						if (inBuff == null) {
							//client closed connection.
							socket.close();
							return;
						}
					} catch (SocketTimeoutException e) {
						// on timeout, before attemptin to read user input again,
						// send any pending update messages to the client
						while (!sendQueue.isEmpty()) {
							Message outMsg = sendQueue.remove();
							outMsg.prettyPrint("S ");
							outMsg.write(bw);
						}
						// handle shutdown
						if (shutdown) {
							shutdown();
							return;
						}
					}
				}
				// process client message and generate response
				Message inMsg = Message.fromHexString(inBuff);
				        inMsg.prettyPrint("C" + id);
				Message outMsg = dfa.process(inMsg);
				
				// check for shutdown
				if (outMsg.opcode() == Message.OP_SHUTDOWN) {
					shutdown = true;
					shutdown();
					return;
				}
				
				// send response to client
				outMsg.prettyPrint("S ");
				outMsg.write(bw);
				
				// check if last sent message was error
				// if so - shutdown
				if (outMsg.opcode() == Message.OP_ERROR) {
					shutdown = true;
					shutdown();
					return;
				}
			}
		} catch (Exception e) {
			connectionListener.remove(this);
			e.printStackTrace();
		}
	}
	
	/**
	 * Shuts down the current server communication handler and removes it from
	 * the list of handlers maintained by the parent connection listener.
	 */
	public void shutdown() throws Exception {
		connectionListener.remove(this);
		socket.close();
		System.out.println(Util.dateTime() + " -- Connection with C"
				+ id + " terminated");
	}

	/**
	 * Adds the input update message to the queue of pending updates to be sent
	 * to the client. Called by other server communication handlers that
	 * confirmed an action, via broadcast to all other handlers.
	 * @param msg the update message to add.
	 */
	public void appendToSendQueue(Message msg) {
		sendQueue.add(msg);
	}
}
