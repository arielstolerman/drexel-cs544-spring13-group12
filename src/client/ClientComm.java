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
 * File name: ClientComm.java
 * 
 * Purpose:
 * An interface for a client communication handler object. Enforces client
 * communication handlers to be threads with functionality to post actions to
 * be sent to the server, disrupt client input and be runnable (i.e. can be
 * ran as a thread).
 * 
 * Relevant requirements:
 * - CONCURRENT
 * - CLIENT
 * 
 * =============================================================================
 */

package client;

import protocol.Message;

public interface ClientComm extends Runnable {
	
	/**
	 * Post an action to be sent to the server.
	 * @param actionMessage action message to be sent to the server.
	 */
	public void postAction(Message actionMessage);
	
	/**
	 * Get the posted action to be sent to the server.
	 * @return the action message to be sent to the server.
	 */
	public Message getPostedActionAndReset();

	/**
	 * Disrupt and terminate any current user input handling. Used to allow
	 * incoming server messages update the state of the house at the client side.
	 */
	public void killInput();

}
