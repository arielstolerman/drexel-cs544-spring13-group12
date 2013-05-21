package protocol;

import devices.Action;
import devices.House;

public class DFA {
	private ProtocolState state = ProtocolState.IDLE;
	private House house;
	private ConnectionListener connectionListener;
	private ServerComm serverComm;
	
	public DFA(House house, ConnectionListener cl) {
		this.connectionListener = cl;
		this.house = house;
	}
	
	public void setServerComm(ServerComm serverComm) {
		this.serverComm = serverComm;
	}
	
	public Message process(Message message) {
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
		}
		return Message.ERROR_GENERAL;
	}


	private Message processIdle(Message M) {
		byte[] b = M.bytes(); 
		if (b.length == 1 && b[0] == 0) {
			this.state = ProtocolState.S_AWAITS_VERSION;
			return Message.SERVER_VERSION;
		} else {
			return Message.ERROR_INIT;
		}
	}
	
	private Message processSAwaitsVersion(Message M) {
		if ("RSHC 0000".equals(M.toString())) {
			this.state = ProtocolState.S_AWAITS_RESPONSE;
			return Message.createChallenge();
		}
		return Message.ERROR_GENERAL;
	}
	
	private Message processSAwaitsResponse(Message M) {
		if ("CLIENT RESPONSE".equals(M.toString())) {
			this.state = ProtocolState.CONNECTED;
			return Message.createINIT(this.house);
		}
		return Message.ERROR_GENERAL;
	}
	
	private Message processConnected(Message M) {
		try {
			byte[] b = M.bytes();
			if (b.length == 1 && b[0] == 7) {
				return Message.SHUTDOWN;
			} else if (b.length > 0 && b[0] == 4) {
				Action action = new Action(b);
				house.doAction(action);
				broadcastStateChange(b);
				return Message.createConfirm(b[1]);
			} else {
				return Message.ERROR_GENERAL;
			}
		} catch (Exception e) {
			return Message.ERROR_GENERAL;
		}
	}

	private void broadcastStateChange(byte[] b) {
		byte b1[] = new byte[b.length-1];
		b1[0] = 6;      //message type
		b1[1] = b[2];   //device type
		b1[2] = b[3];   //device num
		for (int i = 4; i < b.length; i++) { //action
			b1[i-i] = b[i];
		}
		this.connectionListener.broadcast(b1, serverComm);		
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

enum ProtocolMessage {
	
	POKE(""),
	;
	
	private String desc;
	
	private ProtocolMessage(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		return desc;
	}
}










