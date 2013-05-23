package protocol;

import server.ConnectionListener;
import server.ServerComm;
import devices.Action;
import devices.House;

public class DFA {
	private ProtocolState state = ProtocolState.IDLE;
	private House house;
	private ConnectionListener connectionListener;
	private ServerComm serverComm;
	private byte[] challenge;
	private String version = "RSHC 0000";
	
	public DFA() {
		
	}
	
	public DFA(House house, ConnectionListener cl) {
		this.connectionListener = cl;
		this.house = house;
	}
	
	public DFA(House house, ConnectionListener cl, String version) {
		this.connectionListener = cl;
		this.house = house;
		this.version = version;
	}
	
	public void setServerComm(ServerComm serverComm) {
		this.serverComm = serverComm;
	}
	
	public Message serverProcess(Message message) {
		switch (state) {
			case IDLE: {
				return processIdle(message);
			}
			case S_AWAITS_VERSION: {
				return processSAwaitsVersion(message);
			}
			case S_AWAITS_RESPONSE: {
				return processSAwaitsResponse(message);
			}
			case CONNECTED: {
				return processConnected(message);
			}
			default:
				return Message.ERROR_GENERAL;
		}
	}

	public Message clientProcess(Message m) {
		switch (state) {
			case IDLE: {
				return processClientIdle(m);
			}
			case C_AWAITS_INIT: {
				return processClientAwaitsInit(m);
			}
			default: {
				throw new RuntimeException("Client in unsupported state.");
			}
		}
	}
	
	private Message processClientAwaitsInit(Message m) {
		if ("RSHC 0000".equals(m.content())) {
			state = ProtocolState.C_AWAITS_CHALLENGE;
			return Message.CLIENT_VERSION;
		} else {
			throw new RuntimeException("Server does not support client's version.");
		}
	}

	private Message processClientIdle(Message m) {
		state = ProtocolState.C_AWAITS_INIT;
		return Message.POKE;
	}

	private Message processIdle(Message inMsg) {
		if (inMsg.length() == 1 && inMsg.opcode() == Message.OP_POKE) {
			this.state = ProtocolState.S_AWAITS_VERSION;
			return Message.SERVER_VERSION;
		} else {
			return Message.ERROR_INIT;
		}
	}
	
	private Message processSAwaitsVersion(Message inMsg) {
		if (inMsg.content().equals(version)) {
			this.state = ProtocolState.S_AWAITS_RESPONSE;
			challenge = DESAuth.genChallenge();
			return new Message(challenge, Message.OP_CHALLENGE);
		}
		return Message.ERROR_VERSION;
	}
	
	private Message processSAwaitsResponse(Message inMsg) {
		if (DESAuth.checkResponse(challenge, inMsg.contentBytes())) {
			this.state = ProtocolState.CONNECTED;
			return Message.createInit(this.house);
		}
		return Message.ERROR_AUTH;
	}
	
	private Message processConnected(Message inMsg) {
		try {
			if (inMsg.length() == 1 && inMsg.opcode() == Message.OP_SHUTDOWN) {
				return Message.SHUTDOWN;
			} else if (inMsg.length() > 0 && inMsg.opcode() == Message.OP_ACTION) {
				Action action = new Action(inMsg);
				house.doAction(action);
				house.prettyPrint();
				broadcastStateChange(inMsg);
				return Message.createConfirm(action.sequenceNumber());
			} else {
				return Message.ERROR_GENERAL;
			}
		} catch (Exception e) {
			return Message.ERROR_GENERAL;
		}
	}

	private void broadcastStateChange(Message actionMsg) {
		Message updateMsg = Message.createUpdate(actionMsg);
		this.connectionListener.broadcast(updateMsg, serverComm);		
	}
	
}

enum ProtocolState {
	IDLE(""),
	S_AWAITS_VERSION(""),
	C_AWAITS_CHALLENGE(""),
	S_AWAITS_RESPONSE(""),
	C_AWAITS_INIT(""),
	CONNECTED(""),
	CONNECTED_PENDING("");
	
	private String desc;
	
	private ProtocolState(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		return desc;
	}
}











