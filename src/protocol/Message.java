package protocol;

import common.Util;

import devices.House;

/**
 * Representation of a PDU - protocol messages.
 */
public class Message {
	
	// message types
	public static final byte OP_POKE =		0;
	public static final byte OP_VERSION =	1;
	public static final byte OP_ERROR =		2;
	public static final byte OP_CHALLENGE =	3;
	public static final byte OP_RESPONSE =	4;
	public static final byte OP_INIT =		5;
	public static final byte OP_ACTION =	6;
	public static final byte OP_CONFIRM =	7;
	public static final byte OP_UPDATE =	8;
	public static final byte OP_SHUTDOWN =	9;
	
	
	// predefined messages
	
	// other messages
	public final static Message SHUTDOWN = new Message(new byte[]{7});
	public final static Message SERVER_VERSION = new Message(("RSHC "
			+ Server.version).getBytes());
	
	// error messages
	public final static Message ERROR_GENERAL = createError("General error");
	public final static Message ERROR_INIT = createError("Poke error");
	public final static Message ERROR_VERSION = createError("Unsupported version");
	public final static Message ERROR_AUTH = createError("Failed authentication");
	
	// fields
	
	/**
	 * Raw representation of the message byte stream.
	 */
	private final byte[] b;
	
	
	// constructors and factory methods
	
	/**
	 * Constructs a new message from the given stream of bytes.
	 * @param b
	 */
	public Message(byte[] b) {
		this.b = b;
	}
	
	/**
	 * @param seqNum sequence number to confirm.
	 * @return a new confirm message for the given sequence number.
	 */
	public static Message createConfirm(byte seqNum) {
		return new Message(new byte[] {5,seqNum});
	}
	
	/**
	 * @param msg error message content.
	 * @return a new error message for the given message content.
	 */
	private static Message createError(String msg) {
		byte m[] = msg.getBytes();
		byte[] data = new byte[1 + m.length];
		int i = 0;
		data[i] = 1;
		i++;
		for (int j = 0; j < m.length; j++) {
			data[i] = m[j];
		}
		return new Message(data);
	}
	
	/**
	 * @param house the house to generate an init message for.
	 * @return a new init message for the given house.
	 */
	public static Message createInit(House house) {
		return new Message(house.getInit());
	}
	
	// getters
	
	/**
	 * @return the raw stream of bytes of the message.
	 */
	public byte[] bytes() {
		return this.b;
	}
	
	/**
	 * @return a hexadecimal string representation of the bytes of the message.
	 */
	public String toHexString() {
		return Util.toHexString(b);
	}
	
	@Override
	public String toString() {
		return new String(b);
	}
}

/**
 * Enumerator for message types.
 */
enum MessageType {
	
	POKE(0, "client poke"),
	VERSION(1, "version number"),
	ERROR(2, "error"),
	CHALLENGE(3, "server DES challenge"),
	RESPONSE(4, "client response"),
	INIT(5, "server init"),
	ACTION(6, "client action"),
	CONFIRM(7, "server confirm client's action"),
	UPDATE(8, "server non-client-invoked update"),
	SHUTDOWN(9, "shutdown connection");
	
	private MessageType(int opcode, String desc) {
		this.opcode = (byte) opcode;
		this.desc = desc;
	}
	
	// fields
	
	private byte opcode;
	private String desc;
	
	/**
	 * @return the opcode of the message
	 */
	public byte opcode() {
		return opcode;
	}
	
	/**
	 * @return the description of the message
	 */
	public String description() {
		return desc;
	}
}