package protocol;

public class DFA {
	private ProtocolState state = ProtocolState.IDLE;
	
	public DFA() {
		
	}
	
	public Message process(Message message) {
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










