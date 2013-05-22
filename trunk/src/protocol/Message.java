package protocol;

import java.util.Arrays;

import devices.House;

public class Message {
	private final byte[] b;
	public final static Message ERROR_INIT;
	public final static Message ERROR_GENERAL;
	public final static Message SHUTDOWN;
	public final static Message SERVER_VERSION = new Message("RSHC " + Server.version + '\n');
	
	static {
		ERROR_INIT = createError("Poke error");
		ERROR_GENERAL = createError("General error");
		SHUTDOWN = new Message("");
		
	}
	
	public static Message createConfirm(byte seqNum) {
		return new Message(new byte[] {5,seqNum});
	}
	
	private static Message createError(String msg) {
		byte m1[] = new byte[] {1};
		byte m2[] = msg.getBytes();
		
		byte m3[] = new byte[m1.length + m2.length];
		for (int i = 0; i < m1.length; i++) {
			m3[i] = m1[i];
		}
		for (int i = 0; i < m2.length; i++) {
			m3[m1.length+i] = m2[i];
		}
		return new Message(m3);
	}
	
	public static Message genWithNewline(byte[] stream) {
		byte[] n = "\n".getBytes();
		int len = stream.length + n.length;
		byte[] msg = Arrays.copyOf(stream, len);
		int tmp = stream.length;
		for (int i = 0; i < n.length; i++) {
			msg[tmp] = n[i];
			tmp++;
		}
		return new Message(msg);
	}
	
	
	public Message(byte[] b) {
		this.b = b;
	}
	
	public Message(String line) {
		this.b = line.getBytes();	
	}
	
	public byte[] bytes() {
		return this.b;
	}
	
	public String toString() {
		return new String(b);
	}
	
	public static Message createINIT(House H) {
		return new Message(H.getINIT());
	}
}
