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

package protocol;

import devices.*;

public abstract class DFA {
	
	// common server and client fields
	protected ProtocolState state = ProtocolState.IDLE;
	protected House house;
	protected String version = "RSHC 0000";
	
	// constructors
	
	public DFA(House house) {
		this.house = house;
	}
	
	// common procedures
	
	/**
	 * Main DFA message processing procedure. Processes the given message with
	 * respect to the current state of the DFA, changes the state of the DFA
	 * accordingly and returns the message to be sent to the other side.
	 * @param m incoming message
	 * @return the response message after the DFA state is changed, corresponding
	 * to the input message.
	 */
	public Message process(Message m) {
		if (m == Message.SHUTDOWN) return m;
		switch (state) {
		case IDLE:					return processIdle(m);
		case C_AWAITS_VERSION:		return processClientAwaitsVersion(m);
		case S_AWAITS_VERSION:		return processServerAwaitsVersion(m);
		case C_AWAITS_CHALLENGE:	return processClientAwaitsChallenge(m);
		case S_AWAITS_RESPONSE:		return processServerAwaitsResponse(m);
		case C_AWAITS_INIT:			return processClientAwaitsInit(m);
		case S_AWAITS_ACTION:		return processServerAwaitsAction(m);
		case C_AWAITS_CONFIRM:		return processClientAwaitsConfirm(m);
		default:					return Message.ERROR_GENERAL;
		}
	}
	
	// abstract methods
	// a method for each state of the DFA
	
	protected abstract Message processIdle(Message m);
	protected abstract Message processClientAwaitsVersion(Message m);
	protected abstract Message processServerAwaitsVersion(Message m);
	protected abstract Message processClientAwaitsChallenge(Message m);
	protected abstract Message processServerAwaitsResponse(Message m);
	protected abstract Message processClientAwaitsInit(Message m);
	protected abstract Message processServerAwaitsAction(Message m);
	protected abstract Message processClientAwaitsConfirm(Message m);
	
	// getters
	
	public ProtocolState state() {
		return state;
	}
	
	public House house() {
		return this.house;
	}
	
	public String version() {
		return version;
	}
	
	// setters
	
	public void setHouse(House house) {
		this.house = house;
	}
}

/**
 * Enumerator for DFA states
 */
enum ProtocolState {
	IDLE				("Idle"),
	C_AWAITS_VERSION	("Client awaits version"),
	S_AWAITS_VERSION	("Server awaits version selection"),
	C_AWAITS_CHALLENGE	("Client awaits challenge"),
	S_AWAITS_RESPONSE	("Server awaits response"),
	C_AWAITS_INIT		("Client awaits init"),
	S_AWAITS_ACTION		("Server awaits action"), 
	C_AWAITS_CONFIRM	("Client awaits confirmation");
	
	private String desc;
	
	private ProtocolState(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		return desc;
	}
}
