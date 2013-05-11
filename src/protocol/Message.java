package protocol;

public class Message {
	private final String line;
	public final static Message ERROR = new Message("ERROR\n");
	public Message(String line) {
		this.line = line;
	}
	
	public String toString() {
		return this.line;
	}
}
