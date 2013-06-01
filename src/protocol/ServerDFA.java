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
 * File name: ServerDFA.java
 * 
 * Purpose:
 * Extends the DFA class and provides functionality for server actions in the
 * different states of the protocol (DFA).
 * 
 * Relevant requirements:
 * - STATEFUL - the entire file defines the functionality to use to keep the
 *   correct state and state transitions in the server side.
 * - SERVICE - the entire file defines server functionality.
 * - CONCURRENT - details in the file
 * 
 * =============================================================================
 */

package protocol;

import server.*;
import devices.*;

public class ServerDFA extends DFA {
	
	// fields
	
	/**
	 * The parent connection listener
	 */
	private ConnectionListener connectionListener;
	/**
	 * The parent server communication handler
	 */
	private ServerComm serverComm;
	/**
	 * Challenge for the authentication phase
	 */
	private byte[] challenge;
	/**
	 * Confirm message for client actions
	 */
	private Message confirm;
	
	// constructors
	
	/**
	 * Constructs a ServerDFA with the given house and connection listener.
	 */
	public ServerDFA(House house, ConnectionListener cl) {
		super(house);
		this.connectionListener = cl;
	}
	
	// setters
	
	/**
	 * Sets the server communication handler to the given one.
	 * @param serverComm server communication handler to set.
	 */
	public void setServerComm(ServerComm serverComm) {
		this.serverComm = serverComm;
	}

	/**
	 * Broadcast an update to all open connections for an action performed by
	 * one of the connected clients.
	 * Called whenever an action is confirmed by the server and applied on the
	 * house, in order to update the house state at every one of the connected
	 * clients.
	 * @param actionMsg action to broadcast.
	 */
	private void broadcastStateChange(Message actionMsg) {
		
		/*
		 * CONCURRENT
		 * the broadcast handles concurrent clients by making sure all clients
		 * are updated on each of the other clients confirmed actions
		 */
		
		Message updateMsg = Message.createUpdate(actionMsg);
		this.connectionListener.broadcast(updateMsg, serverComm);		
	}
	
	
	// process procedures
	
	/**
	 * Transitions the protocol state to "client awaits version" and immediately
	 * calls the next process phase to prepare a version message to be sent to
	 * the client.
	 * If given an invalid message for the current state, returns an init
	 * error message.
	 */
	protected Message processIdle(Message m) {
		if (m.length() == 1 && m.opcode() == Message.OP_POKE) {
			this.state = ProtocolState.C_AWAITS_VERSION;
			return process(Message.INTERNAL);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_INIT;
	}

	/**
	 * Transitions the protocol state to "server awaits version" and returns
	 * the server supported protocol version.
	 * If given an invalid message for the current state, returns a general
	 * error message.
	 */
	protected Message processClientAwaitsVersion(Message m) {
		if (m.opcode() == Message.OP_INTERNAL) {
			this.state = ProtocolState.S_AWAITS_VERSION;
			return Message.SERVER_VERSION;
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	/**
	 * Transitions the protocol state to "client awaits challenge" and immediately
	 * calls the next process phase to prepare a challenge message to be sent to
	 * the client.
	 * If given an invalid message for the current state or an unsupported
	 * version, returns a version error message.
	 */
	protected Message processServerAwaitsVersion(Message m) {
		if (m.opcode() == Message.OP_VERSION
				&& Server.VERSION.equals(m.content())) {
			this.state = ProtocolState.C_AWAITS_CHALLENGE;
			return process(Message.INTERNAL);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_VERSION;
	}

	/**
	 * Transitions the protocol state to "server awaits response" and returns
	 * the challenge message to be sent to the client.
	 * If given an invalid message for the current state, returns a general
	 * error message.
	 */
	protected Message processClientAwaitsChallenge(Message m) {
		if (m.opcode() == Message.OP_INTERNAL) {
			this.state = ProtocolState.S_AWAITS_RESPONSE;
			challenge = DESAuth.genChallenge();
			return new Message(challenge, Message.OP_CHALLENGE);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	/**
	 * Transitions the protocol state to "client awaits init" and immediately
	 * calls the next process phase to prepare an init message to be sent to
	 * the client.
	 * If given an invalid message for the current state or the client failed
	 * the challenge, returns an authentication error message.
	 */
	protected Message processServerAwaitsResponse(Message m) {
		if (DESAuth.checkResponse(challenge, m.contentBytes())) {
			this.state = ProtocolState.C_AWAITS_INIT;
			return process(Message.INTERNAL);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_AUTH;
	}

	/**
	 * Transitions the protocol state to "server awaits action" and returns
	 * the init message to be sent to the client.
	 * If given an invalid message for the current state, returns a general
	 * error message.
	 */
	protected Message processClientAwaitsInit(Message m) {
		if (m.opcode() == Message.OP_INTERNAL) {
			this.state = ProtocolState.S_AWAITS_ACTION;
			return Message.createInit(house);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	/**
	 * If given a shutdown message, returns it immediately to signal shutdown.
	 * If given an action message from the client, applies the action on the
	 * house, transitions the protocol state to "client awaits confirm" and
	 * immediately calls the next process phase to prepare a confirm message
	 * to be sent to the client.
	 * If the action is confirmed and applied, also broadcasts the action to
	 * all other active clients.
	 * If given an invalid message for the current state, returns a general
	 * error message.
	 */
	protected Message processServerAwaitsAction(Message m) {
		// shutdown
		if (m.length() == 1 && m.opcode() == Message.OP_SHUTDOWN) {
			return Message.SHUTDOWN;
		}
		// process action
		else if (m.length() > 0 && m.opcode() == Message.OP_ACTION) {
			Action action = new Action(m);
			this.state = ProtocolState.C_AWAITS_CONFIRM;
			try {
				house.doAction(action);
			} catch (Exception e) {
				// action failed
				System.err.println("Action failed: " + e.getMessage());
				confirm = Message.createConfirm(action.sequenceNumber(),false);
				return process(Message.INTERNAL);
			}
			// action succeeded
			house.prettyPrint();
			/*
			 * CONCURRENT
			 * broadcast confirmed action to all other active clients
			 */
			broadcastStateChange(m);
			confirm = Message.createConfirm(action.sequenceNumber(), true);
			return process(Message.INTERNAL);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	/**
	 * Transitions the protocol state to "server awaits action" and returns
	 * the confirm message to be sent to the client.
	 * If given an invalid message for the current state, returns a general
	 * error message.
	 */
	protected Message processClientAwaitsConfirm(Message m) {
		if (m.opcode() == Message.OP_INTERNAL && confirm != null) {
			this.state = ProtocolState.S_AWAITS_ACTION;
			Message res = confirm;
			confirm = null;
			return res;
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}
}
