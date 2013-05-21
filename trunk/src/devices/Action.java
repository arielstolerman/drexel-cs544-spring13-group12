package devices;

public class Action {
	
	// fields
	
	public static final byte ILLEGAL_PARAM = -1;
	
	/**
	 * The byte stream for both action (first byte) and parameters.
	 */
	private byte[] stream;
	
	// constructors
	
	/**
	 * Constructs an action from the given stream of bytes, where the first byte
	 * is the the device type, second is device number, third is operation code
	 * and the rest (if any) are the parameters.
	 * @param stream the stream to construct the action from.
	 * @throws Exception if the input stream is empty or null.
	 */
	public Action(byte[] stream) throws Exception {
		if (stream == null || stream.length < 3)
			throw new Exception("action byte stream must contain at least " +
					"three bytes");
		this.stream = stream;
	}
	
	// methods
	
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
		if (index < 0 || stream.length < index + 4)
			return ILLEGAL_PARAM;
		return stream[index + 1];
	}
	
	/**
	 * @return number of parameters in the action.
	 */
	public int numParams() {
		return stream.length - 3;
	}
}
