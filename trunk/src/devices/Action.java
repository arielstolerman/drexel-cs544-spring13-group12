/* =============================================================================
 * CS544 - Computer Networks
 * Drexel University, Spring 2013
 * Protocol Implementation: Remote Smart House Control
 * Group 12:
 * - Ryan Corcoran
 * - Amber Heilman
 * - Michael Mersic
 * - Ariel Stolerman
 * 
 * -----------------------------------------------------------------------------
 * File name: 
 * 
 * Purpose:
 * 
 * 
 * Relevant requirements (details in the file):
 * - 
 * 
 * =============================================================================
 */

package devices;

import protocol.Message;

public class Action {
	
	// fields
	
	public static final byte ILLEGAL_PARAM = -1;
	
	/**
	 * The byte stream for both action (first byte) and parameters.
	 */
	private byte[] stream;
	
	// constructors
	
	public Action(byte sequenceNumber, byte deviceType, byte deviceNumber, byte opcode, byte params[]) {
		this.stream = new byte[5 + params.length];
		stream[0] = Message.OP_ACTION;
		stream[1] = sequenceNumber;
		stream[2] = deviceType;
		stream[3] = deviceNumber;
		stream[4] = opcode;
		for (int i = 0; i < params.length; i++) {
			stream[5+i] = params[i];
		}		
	}
	
	/**
	 * Constructs an action from the given action message, without making an
	 * input check.
	 * @param inActionMsg the action message to construct an action from.
	 */
	public Action(Message inActionMsg) {
		this.stream = inActionMsg.bytes();
	}
	
	/**
	 * Constructs an action from the given stream. Does not check validity.
	 * @param stream
	 */
	public Action(byte[] stream) {
		this.stream = stream;
	}
	
	// methods
	
	/**
	 * @return the action sequence number.
	 */
	public byte sequenceNumber() {
		return stream[1];
	}
	
	/**
	 * @return the device type.
	 */
	public byte deviceType() {
		return stream[2];
	}
	
	/**
	 * @return the device number.
	 */
	public byte deviceNumber() {
		return stream[3];
	}
	
	/**
	 * @return the opcode (byte) of this action.
	 */
	public byte opcode() {
		return stream[4];
	}
	
	/**
	 * @param index index of the desired parameter
	 * @return the parameter at the given index, or ILLEGAL_PARAM if no 
	 * parameter exists at that index.
	 */
	public byte getParam(int index) {
		if (index < 0 || stream.length < index + 5)
			return ILLEGAL_PARAM;
		return stream[index + 5];
	}
	
	/**
	 * @return number of parameters in the action.
	 */
	public int numParams() {
		return stream.length - 5;
	}

	public Message toMessage() {
		return new Message(this.stream);
	}
}
