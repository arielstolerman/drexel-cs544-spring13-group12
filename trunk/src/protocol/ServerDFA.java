package protocol;

import server.*;
import devices.*;

public class ServerDFA extends DFA {
	
	// fields
	private ConnectionListener connectionListener;
	private ServerComm serverComm;
	private byte[] challenge;
	private Message confirm;
	
	// constructors
	
	public ServerDFA(House house, ConnectionListener cl) {
		super(house);
		this.connectionListener = cl;
	}
	
	public ServerDFA(House house, ConnectionListener cl, String version) {
		this(house, cl);
		this.version = version;
	}
	
	// setters
	
	public void setServerComm(ServerComm serverComm) {
		this.serverComm = serverComm;
	}

	private void broadcastStateChange(Message actionMsg) {
		Message updateMsg = Message.createUpdate(actionMsg);
		this.connectionListener.broadcast(updateMsg, serverComm);		
	}
	
	
	// process procedures
	
	@Override
	protected Message processIdle(Message m) {
		if (m.length() == 1 && m.opcode() == Message.OP_POKE) {
			this.state = ProtocolState.C_AWAITS_VERSION;
			return process(Message.INTERNAL);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_INIT;
	}

	@Override
	protected Message processClientAwaitsVersion(Message m) {
		if (m.opcode() == Message.OP_INTERNAL) {
			this.state = ProtocolState.S_AWAITS_VERSION;
			return Message.SERVER_VERSION;
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	@Override
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

	@Override
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

	@Override
	protected Message processServerAwaitsResponse(Message m) {
		if (DESAuth.checkResponse(challenge, m.contentBytes())) {
			this.state = ProtocolState.C_AWAITS_INIT;
			return process(Message.INTERNAL);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_AUTH;
	}

	@Override
	protected Message processClientAwaitsInit(Message m) {
		if (m.opcode() == Message.OP_INTERNAL) {
			this.state = ProtocolState.S_AWAITS_ACTION;
			return Message.createInit(this.house);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	@Override
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
			broadcastStateChange(m);
			confirm = Message.createConfirm(action.sequenceNumber(), true);
			return process(Message.INTERNAL);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	@Override
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
