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
 * File name: ClientDFA.java
 * 
 * Purpose:
 * Extends the DFA class and provides functionality for client actions in the
 * different states of the protocol (DFA).
 * 
 * Relevant requirements:
 * - STATEFUL - the entire file defines the functionality to use to keep the
 *   correct state and state transitions in the client side.
 * - CLIENT - the entire file defines client functionality.
 * 
 * =============================================================================
 */

package protocol;

import client.*;
import devices.*;

public class ClientDFA extends DFA {

	// fields
	
	/**
	 * The parent client communication handler
	 */
	private ClientComm clientComm;
	/**
	 * The client username
	 */
	private String user = null;
	/**
	 * The client password
	 */
	private String pass = null;
	/**
	 * Response for the authentication phase
	 */
	private Message response;

	// constructors
	
	/**
	 * Constructs a ClientDFA with the given client communication handler,
	 * username and password.
	 */
	public ClientDFA(ClientComm clientComm, String user, String pass) {
		super(null);
		this.clientComm = clientComm;
		this.user = user;
		this.pass = pass;
	}

	// process procedures

	/**
	 * Transitions the protocol state to "client awaits version" and returns a
	 * poke message to initiate communication with the server.
	 * If given an invalid message for the current state, returns a general
	 * error message.
	 */
	protected Message processIdle(Message m) {
		if (m.opcode() == Message.OP_INTERNAL) {
			state = ProtocolState.C_AWAITS_VERSION;
			return Message.POKE;
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	/**
	 * Transitions the protocol state to "server awaits version" and immediately
	 * calls the next process phase to prepare a version message to be sent to
	 * the server.
	 * If given an invalid message for the current state, returns a version
	 * error message.
	 */
	protected Message processClientAwaitsVersion(Message m) {
		if (m.opcode() == Message.OP_VERSION
				&& Client.VERSION.equals(m.content())) {
			state = ProtocolState.S_AWAITS_VERSION;
			return process(Message.INTERNAL);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_VERSION;
	}

	/**
	 * Transitions the protocol state to "client awaits challenge" and returns
	 * the client selected protocol version.
	 * If given an invalid message for the current state, returns a general
	 * error message.
	 */
	protected Message processServerAwaitsVersion(Message m) {
		if (m.opcode() == Message.OP_INTERNAL) {
			this.state = ProtocolState.C_AWAITS_CHALLENGE;
			return Message.CLIENT_VERSION;
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	/**
	 * Transitions the protocol state to "server awaits response" and immediately
	 * calls the next process phase to send the response to the server.
	 * If given an invalid message for the current state, returns a general
	 * error message.
	 */
	protected Message processClientAwaitsChallenge(Message m) {
		if (m.opcode() == Message.OP_CHALLENGE) {
			response = new Message(DESAuth.genUserSemiResponse(
					user,
					pass,
					m.contentBytes()),
					Message.OP_RESPONSE);
			state = ProtocolState.S_AWAITS_RESPONSE;
			return process(Message.INTERNAL);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	/**
	 * Transitions the protocol state to "client awaits init" and returns the
	 * challenge response message to be sent to the server.
	 * If given an invalid message for the current state, returns a general
	 * error message.
	 */
	protected Message processServerAwaitsResponse(Message m) {
		if (m.opcode() == Message.OP_INTERNAL && response != null) {
			this.state = ProtocolState.C_AWAITS_INIT;
			Message res = response;
			response = null;
			return res;
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	/**
	 * Transitions the protocol state to "server awaits action" and immediately
	 * calls the next process phase to send the action to the server.
	 * If given an invalid message for the current state, returns a general
	 * error message.
	 */
	protected Message processClientAwaitsInit(Message m) {
		if (m.opcode() == Message.OP_INIT) {
			this.house = House.createHouseFromInit(m);
			System.out.println("::: Server house image at client side :::");
			this.house.prettyPrint();
			this.state = ProtocolState.S_AWAITS_ACTION;
			return process(Message.INTERNAL);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	/**
	 * If given an action message, transitions the protocol state to "client
	 * awaits confirm" and returns action message to be sent to the server.
	 * If given an update message (sent from the server), applies the update on
	 * the local house image.
	 * If given an invalid message for the current state, returns a general
	 * error message.
	 */
	protected Message processServerAwaitsAction(Message m) {
		// immediately after init message, signal client that no message
		// is to be sent to server until given user input
		if (m.opcode() == Message.OP_INTERNAL) {
			return Message.AWAITING_CLIENT_INPUT;
		}
		// process user action
		else if (m.opcode() == Message.OP_ACTION) {
			state = ProtocolState.C_AWAITS_CONFIRM;
			return m;
		}
		// process server update
		else if (m.opcode() == Message.OP_UPDATE) {
			return processUpdate(m);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	/**
	 * If given ac confirm message, transitions the protocol state to "server
	 * awaits action" and signals to await user input action.
	 * If given an update message (sent from the server), applies the update on
	 * the local house image.
	 * If given an invalid message for the current state, returns a general
	 * error message.
	 */
	protected Message processClientAwaitsConfirm(Message m) {
		if (m.opcode() == Message.OP_CONFIRM) {
			byte[] b = m.bytes();
			boolean confirmed = b[2] == 1;
			Action action = new Action(clientComm.getPostedActionAndReset());
			if (confirmed) {
				// apply confirmed message internally
				try {
					house.doAction(action);
				} catch (Exception e) {
					System.out.println("Internal error applying posted message on house");
					state = ProtocolState.IDLE;
					return Message.ERROR_GENERAL;
				}
				System.out.println("::: Action " + b[1]
						+ " confirmed, new state of the house :::");
				house.prettyPrint();
			}
			else {
				System.out.println("::: Action " + b[1] + " denied :::");
			}
			state = ProtocolState.S_AWAITS_ACTION;
			return Message.AWAITING_CLIENT_INPUT;
		}
		// process server update
		else if (m.opcode() == Message.OP_UPDATE) {
			return processUpdate(m);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}
	
	/**
	 * Should be called to process a server update (response to actions
	 * performed by some other client).
	 * @param m update message.
	 */
	private Message processUpdate(Message m) {
		try {
			house.doUpdate(m);
		} catch (Exception e) {
			System.out.println("Internal error applying update on house");
			state = ProtocolState.IDLE;
			return Message.ERROR_GENERAL;
		}
		clientComm.killInput();
		System.out.println("::: Update received from server :::");
		house.prettyPrint();
		return Message.AWAITING_CLIENT_INPUT;
	}
}
