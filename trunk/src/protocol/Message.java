package protocol;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;

import server.Server;
import client.Client;

import common.Util;

import devices.House;

/**
 * Representation of a PDU - protocol messages.
 */
public class Message {
	
	// message types
	public static final byte OP_INTERNAL =	-2;
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
	// internal message - used only by client / server for internal processing
	public static final Message INTERNAL = new Message(OP_INTERNAL);
	// poke
	public static final Message POKE = new Message(OP_POKE);
	// server version
	public static final Message SERVER_VERSION =
			new Message((Server.VERSION).getBytes(), OP_VERSION);
	// client version
	public static final Message CLIENT_VERSION =
			new Message((Client.VERSION).getBytes(), OP_VERSION);
	// shutdown
		public static final Message SHUTDOWN = new Message(OP_SHUTDOWN);
		
	public static final Message AWAITING_CLIENT_INPUT = new Message((byte)-1);
	
		
	// ERROR MESSAGES
	// General catch-all error
	public static final Message ERROR_GENERAL =
			createError("General error");
	// Error on the poke message
	public static final Message ERROR_INIT =
			createError("Poke error");
	// An unsupported version message was sent
	public static final Message ERROR_VERSION =
			createError("Unsupported version");
	// Client/server authentication failed.  
	public static final Message ERROR_AUTH =
			createError("Failed authentication");
	
	
	// GENERAL CONFIG
	private static final int WRAP_SIZE = 60;
	
	// FIELDS
	
	/**
	 * Message opcode.
	 */
	private byte opcode = -1; // to catch erroneous initialization
	/**
	 * Raw representation of the message byte stream.
	 */
	private final byte[] bytes;
	
	
	// CONSTRUCTORS
	
	/**
	 * Constructs a new message from the given stream of bytes, which should
	 * already include the opcode as the first byte.
	 * @param fullMessage
	 */
	public Message(byte[] fullMessage) {
		opcode = fullMessage[0];
		bytes = fullMessage;
	}
	
	/**
	 * Constructs a new message from the given message content stream of bytes
	 * (should NOT contain the opcode at the beginning) and the given opcode.
	 * @param messageContent
	 * @param opcode
	 */
	public Message(byte[] messageContent, byte opcode) {
		this.opcode= opcode;  
		bytes = new byte[messageContent.length + 1];
		bytes[0] = opcode;
		for (int i = 0; i < messageContent.length; i++)
			bytes[i + 1] = messageContent[i];
	}
	
	/**
	 * Constructs a message that has only an opcode, from the given one.
	 * @param opcode
	 */
	public Message(byte opcode) {
		this.opcode = opcode;
		bytes = new byte[]{opcode};
	}
	
	// METHODS
	
	/**
	 * @param seqNum sequence number to confirm.
	 * @param accept whether the action was accepted and applied or denied by
	 * the server.
	 * @return a new confirm message for the given sequence number.
	 */
	public static Message createConfirm(byte seqNum, boolean accept) {
		return new Message(new byte[] {OP_CONFIRM, seqNum,
				(accept? (byte) 1 : (byte) 0)});
	}
	
	/**
	 * @param msg error message content.
	 * @return a new error message for the given message content.
	 */
	private static Message createError(String msg) {
		return new Message(msg.getBytes(), OP_ERROR);
	}
	
	/**
	 * @param house the house to generate an init message for.
	 * @return a new init message for the given house.
	 */
	public static Message createInit(House house) {
		return new Message(house.getInit());
	}
	
	/**
	 * @param hexStr hexadecimal string representation of a message (with opcode
	 * and content). Should be used when message is read from input stream.
	 * @return the corresponding message.
	 */
	public static Message fromHexString(String hexStr) {
		return new Message(Util.toByteStream(hexStr));
	}
	
	/**
	 * @param actionMsg the action message to generate an update message from.
	 * @return an update message corresponding to the given action message. To
	 * be used when an action is performed to a client request, and an update
	 * should be sent to all other clients.
	 */
	public static Message createUpdate(Message actionMsg) {
		byte[] actionStream = actionMsg.bytes;
		byte[] updateStream = new byte[actionStream.length - 1];
		updateStream[0] = OP_UPDATE;
		for (int i = 2; i < actionStream.length; i++)
			updateStream[i - 1] = actionStream[i];
		return new Message(updateStream);
	}
	
	// GETTERS
	
	/**
	 * @return number of bytes in the message stream.
	 */
	public int length() {
		return bytes.length;
	}
	
	/**
	 * @return the opcode of the message.
	 */
	public byte opcode() {
		return opcode;
	}
	
	/**
	 * @return the raw stream of bytes of the message.
	 */
	public byte[] bytes() {
		return this.bytes;
	}
	
	/**
	 * @return the string representation of the message content (without the
	 * opcode).
	 */
	public String content() {
		return new String(contentBytes());
	}
	
	/**
	 * @return the raw stream of bytes of the content part of the message (i.e.
	 * without the first byte - the opcode).
	 */
	public byte[] contentBytes() {
		return Arrays.copyOfRange(bytes, 1, bytes.length);
	}
	
	/**
	 * To be used when writing to output stream.
	 * @return a hexadecimal string representation of the bytes of the message,
	 * terminated with a newline.
	 */
	public String toHexString() {
		return Util.toHexString(bytes) + "\n";
	}
	
	@Override
	public String toString() {
		return new String(bytes);
	}
	
	/**
	 * @return a pretty string representation of the message, with separation
	 * between the opcode and the message content.
	 */
	public String toPrettyString() {
		return "OP: " + opcode + " | MESSAGE: " + content();
	}
	
	// ACTIONS
	
	/**
	 * Writes the message (its hexadecimal + '\n' representation) to the given
	 * buffered writer and flushes.
	 * @param bw
	 * @throws IOException
	 */
	public void write(BufferedWriter bw) throws IOException {
		bw.write(toHexString());
		bw.flush();
	}
	
	/**
	 * Pretty prints this message with prefix set to the given sender.
	 * @param sender
	 */
	public void prettyPrint(String sender) {
		System.out.println(Util.time() + " " + sender + " >");
		String raw = toPrettyString().replaceAll("\n", "\\n").replaceAll("\r", "\\r");
		String bytecode = toHexString();
		System.out.println(indentedWrapped(
				"raw:  ", raw, WRAP_SIZE));
		System.out.println(indentedWrapped(
				"byte: ", bytecode, WRAP_SIZE));
	}
	
	// UTILITY METHODS
		
	private static String indent(String s) {
		String res = "";
		int size = s.length();
		for (int i = 0; i < size; i++)
			res += " ";
		return res;
	}
	
	private static String indentedWrapped(String prefix, String s, int wrap) {
		String res = prefix;
		String ind = indent(prefix);
		int lim = s.length() / wrap;
		for (int i = 0; i < lim; i++) {
			res += s.substring(0, wrap) + "\n" + ind;
			s = s.substring(wrap, s.length());
		}
		res += s;
		return res;
	}
}
