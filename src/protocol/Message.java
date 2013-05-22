package protocol;

import java.util.Arrays;

import devices.House;
import devices.Util;

public class Message {
	private final byte[] b;
	
	// error messages
	public final static Message ERROR_GENERAL = createError("General error");
	public final static Message ERROR_INIT = createError("Poke error");
	public final static Message ERROR_VERSION = createError("Unsupported version");
	public final static Message ERROR_AUTH = createError("Failed authentication");
	
	// other messages
	public final static Message SHUTDOWN = new Message(new byte[]{7});
	public final static Message SERVER_VERSION = new Message(("RSHC "
			+ Server.version).getBytes());
		
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
		
	public Message(byte[] b) {
		this.b = b;
	}
	
//	public Message(String line) {
//		this.b = Util.toByteStream(line);	
//	}
	
	public byte[] bytes() {
		return this.b;
	}
	
	public String toString() {
		return new String(b);
	}
	
	public String toHexString() {
		return Util.toHexString(b);
	}
	
	public static Message createINIT(House H) {
		return new Message(H.getINIT());
	}
}
