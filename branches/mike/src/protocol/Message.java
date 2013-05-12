package protocol;

import devices.House;

public class Message {
	private final String line;
	public final static Message ERROR = new Message("ERROR\n");
	public final static Message SERVER_VERSION = new Message("RSHC " + Server.version + '\n');
	public Message(String line) {
		this.line = line;
	}
	
	public String toString() {
		return this.line;
	}

	public static Message createChallenge() {
		return new Message("SERVER CHALLENGE\n");
	}

	public static Message createINIT(House H) {
		return new Message('3' + H.getINIT() + '\n');
	}
}
