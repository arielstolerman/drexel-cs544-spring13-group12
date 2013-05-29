package protocol;

import server.ConnectionListener;
import server.ServerComm;
import client.ClientComm;
import devices.Action;
import devices.House;
import devices.Update;

public class DFA {
	private ProtocolState state = ProtocolState.IDLE;
	private House house;
	private ConnectionListener connectionListener;
	private ServerComm serverComm;
	private ClientComm clientComm;
	private byte[] challenge;
	private String version = "RSHC 0000";
	private String user = null;
	private String pass = null;
	
	
	public DFA() {
		
	}
	
	public DFA(ClientComm clientComm, String user, String pass) {
		this.user = user;
		this.pass = pass;
		this.clientComm = clientComm;
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
			case C_AWAITS_VERSION: {
				return processServerCAwaitsVersion(message);
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
			case C_AWAITS_VERSION: {
				return processClientAwaitsVersion(m);
			}
			case S_AWAITS_VERSION: {
				return processClientServerAwaitsVersion(m);
			}
			case C_AWAITS_CHALLENGE: {
				return processClientAwaitsChallenge(m);
			}
			case S_AWAITS_RESPONSE: {
				return processClientServerAwaitsResponse(m);
			}
			case C_AWAITS_INIT: {
				return processClientAwaitsInit(m);
			}
			case S_AWAITS_ACTION: {
				return processClientServerAwaitsAction(m);
			}
			default: {
				throw new RuntimeException("Client in unsupported state.");
			}
		}
	}
	
	private Message processClientAwaitsInit(Message m) {
		if (m.opcode() == 5) {
			this.house = House.createHouseFromInit(m);
			System.out.println("client's house:::::::::::::::::::");
			this.house.prettyPrint();
			this.state = ProtocolState.S_AWAITS_ACTION;
			return clientProcess(null);
		} else {
			throw new RuntimeException("Bad message");
		}
	}

	private Message processClientServerAwaitsAction(Message m) {
		if (m == null) {
			return Message.AWAITING_CLIENT_INPUT;
		} else if (m.opcode() == Message.OP_ACTION) {
			this.state = ProtocolState.C_AWAITS_CONFIRM;
			return m;
		} else if (m.opcode() == Message.OP_UPDATE) {
			this.house.doUpdate(new Update(m.bytes()));
			this.clientComm.killInput();
			this.house.prettyPrint();
			return Message.AWAITING_CLIENT_INPUT;
		} else {
			throw new RuntimeException("clientServerAwaitsAction doesn't know what to do.");
		}
	}
	
	private Message processClientAwaitsChallenge(Message m) {
		Message outMsg = new Message(DESAuth.genUserSemiResponse(user,
				pass, m.contentBytes()),
				Message.OP_RESPONSE);
		state = ProtocolState.S_AWAITS_RESPONSE;
		return outMsg;
	}
	
	private Message processClientServerAwaitsResponse(Message m) {
		this.state = ProtocolState.C_AWAITS_INIT;
		return clientProcess(m);
	}

	private Message processClientAwaitsVersion(Message m) {
		if ("RSHC 0000".equals(m.content())) {
			state = ProtocolState.S_AWAITS_VERSION;
			return Message.CLIENT_VERSION;
		} else {
			throw new RuntimeException("Server does not support client's version.");
		}
	}

	private Message processClientServerAwaitsVersion(Message m) {
		this.state = ProtocolState.C_AWAITS_CHALLENGE;
		return clientProcess(m);
	}
	
	private Message processClientIdle(Message m) {
		state = ProtocolState.C_AWAITS_VERSION;
		return Message.POKE;
	}

	private Message processIdle(Message inMsg) {
		if (inMsg.length() == 1 && inMsg.opcode() == Message.OP_POKE) {
			this.state = ProtocolState.C_AWAITS_VERSION;
			return serverProcess(inMsg); 
		} else {
			return Message.ERROR_INIT;
		}
	}
	
	private Message processServerCAwaitsVersion(Message inMsg) {
		this.state = ProtocolState.S_AWAITS_VERSION;
		return Message.SERVER_VERSION;
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
	
	public House getHouse() {
		return this.house;
	}
	
}

enum ProtocolState {
	IDLE(""),
	S_AWAITS_VERSION(""),
	C_AWAITS_CHALLENGE(""),
	S_AWAITS_RESPONSE(""),
	C_AWAITS_INIT(""),
	CONNECTED(""),
	CONNECTED_PENDING(""), C_AWAITS_VERSION(""), 
	S_AWAITS_ACTION(""), 
	C_AWAITS_CONFIRM("");
	
	private String desc;
	
	private ProtocolState(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		return desc;
	}
}











