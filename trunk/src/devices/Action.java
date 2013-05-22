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
	
	/**
	 * Constructs an action from the given action message.
	 * @param inActionMsg the action message to construct an action from.
	 * @throws Exception if the input message is not an action message.
	 */
	public Action(Message inActionMsg) throws Exception {
		if (inActionMsg.opcode() != Message.OP_ACTION)
			throw new Exception("Unexpected message when initializing Action: "
					+ inActionMsg.toPrettyString());
		this.stream = inActionMsg.bytes();
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
}
