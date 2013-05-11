package protocol;

public class DFA {

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










