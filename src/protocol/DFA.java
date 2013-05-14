package protocol;

import devices.House;

public class DFA {
	private ProtocolState state = ProtocolState.IDLE;
	private House house;
	
	public DFA(House house) {
		this.house = house;
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
		}
		return Message.ERROR;
	}


	private Message processIdle(Message M) {
		if ("0".equals(M.toString())) {
			this.state = ProtocolState.S_AWAITS_VERSION;
			return Message.SERVER_VERSION;
		} else {
			return Message.ERROR;
		}
	}
	
	private Message processSAwaitsVersion(Message M) {
		if ("RSHC 0000".equals(M.toString())) {
			this.state = ProtocolState.S_AWAITS_RESPONSE;
			return Message.createChallenge();
		}
		return Message.ERROR;
	}
	
	private Message processSAwaitsResponse(Message M) {
		if ("CLIENT RESPONSE".equals(M.toString())) {
			this.state = ProtocolState.CONNECTED;
			return Message.createINIT(this.house);
		}
		return Message.ERROR;
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










