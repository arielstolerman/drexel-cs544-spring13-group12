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
 * File name: DFA.java
 * 
 * Purpose:
 * Defines the states of the protocol and abstract methods to be implemented by
 * the client and server for the operations to apply at any given state of th
 * protocol.
 * Extended by ServerDFA and ClientDFA - each applying the procedures, state
 * transitions and message generation for the respetive state the DFA is at.
 * Helps in enforcing the protocol during active sessions.
 * 
 * Relevant requirements:
 * - STATEFUL - the entire file defines the functionality to use to keep the
 *   correct state and state transitions.
 * 
 * =============================================================================
 */

package protocol;

import devices.*;

public abstract class DFA {
	
	// common server and client fields
	
	/**
	 * The state of the protocol
	 */
	protected ProtocolState state = ProtocolState.IDLE;
	/**
	 * The house
	 */
	protected House house;
	
	// constructors
	
	/**
	 * Initializes the DFA with the given house.
	 */
	public DFA(House house) {
		this.house = house;
	}
	
	// common procedures
	
	/**
	 * Main DFA message processing procedure. Processes the given message with
	 * respect to the current state of the DFA, changes the state of the DFA
	 * accordingly and returns the message to be sent to the other side.
	 * @param m incoming message to process at the current state.
	 * @return the response message after the DFA state is changed, corresponding
	 * to the input message.
	 */
	public Message process(Message m) {
		// if the incoming message is a shutdown request, return immediately
		if (m == Message.SHUTDOWN) return m;
		// otherwise, process the message in the respective state of the protocol
		switch (state) {
		case IDLE:					return processIdle(m);
		case C_AWAITS_VERSION:		return processClientAwaitsVersion(m);
		case S_AWAITS_VERSION:		return processServerAwaitsVersion(m);
		case C_AWAITS_CHALLENGE:	return processClientAwaitsChallenge(m);
		case S_AWAITS_RESPONSE:		return processServerAwaitsResponse(m);
		case C_AWAITS_INIT:			return processClientAwaitsInit(m);
		case S_AWAITS_ACTION:		return processServerAwaitsAction(m);
		case C_AWAITS_CONFIRM:		return processClientAwaitsConfirm(m);
		default:					return Message.ERROR_GENERAL; // should not get here
		}
	}
	
	// abstract methods
	// a method for each state of the DFA
	
	/**
	 * Processes the given message when the protocol is in idle state.
	 * @param m the message to process.
	 * @return the response message to pass to the other end.
	 */
	protected abstract Message processIdle					(Message m);
	
	/**
	 * Processes the given message when the protocol is in client awaits version
	 * state.
	 * @param m the message to process.
	 * @return the response message to pass to the other end.
	 */
	protected abstract Message processClientAwaitsVersion	(Message m);
	
	/**
	 * Processes the given message when the protocol is in server awaits version
	 * selection state.
	 * @param m the message to process.
	 * @return the response message to pass to the other end.
	 */
	protected abstract Message processServerAwaitsVersion	(Message m);
	
	/**
	 * Processes the given message when the protocol is in client awaits
	 * authentication challenge state.
	 * @param m the message to process.
	 * @return the response message to pass to the other end.
	 */
	protected abstract Message processClientAwaitsChallenge	(Message m);
	
	/**
	 * Processes the given message when the protocol is in server awaits client
	 * authentication challenge response state.
	 * @param m the message to process.
	 * @return the response message to pass to the other end.
	 */
	protected abstract Message processServerAwaitsResponse	(Message m);
	
	/**
	 * Processes the given message when the protocol is in client awaits init
	 * state.
	 * @param m the message to process.
	 * @return the response message to pass to the other end.
	 */
	protected abstract Message processClientAwaitsInit		(Message m);
	
	/**
	 * Processes the given message when the protocol is in server awaits action
	 * state.
	 * @param m the message to process.
	 * @return the response message to pass to the other end.
	 */
	protected abstract Message processServerAwaitsAction	(Message m);
	
	/**
	 * Processes the given message when the protocol is in client awaits
	 * action confirmation / denial state.
	 * @param m the message to process.
	 * @return the response message to pass to the other end.
	 */
	protected abstract Message processClientAwaitsConfirm	(Message m);
	
	// getters
	
	/**
	 * @return the current state of the protocol (the DFA).
	 */
	public ProtocolState state() {
		return state;
	}
	
	/**
	 * @return the house attached to the DFA.
	 */
	public House house() {
		return this.house;
	}
		
	// setters
	
	/**
	 * Sets the house attached to the DFA to the given one.
	 * @param house the house to attach.
	 */
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
