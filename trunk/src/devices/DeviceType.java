package devices;

/**
 * Enumerator for known device types.
 */
public enum DeviceType {
	LIGHT	((byte) 0, 1),
	SHADE	((byte) 1, 1),
	AIRCON	((byte) 2, 1),
	TV		((byte) 3, 1),
	ALARM	((byte) 4, 0);
	//NO_SUCH_DEVICE((byte) -1, null);
	
	private DeviceType(byte type, int numParams) {
		this.type = type;
		this.numParams = numParams;
	}
	
	// fields
	private byte type;
	private int numParams;
	
	// getters
	
	/**
	 * @return the type byte code.
	 */
	public byte type() {
		return type;
	}
	
	/**
	 * @return the number of parameters related to the device.
	 */
	public int numParams() {
		return numParams;
	}
	
	public static DeviceType[] legalValues() {
		return new DeviceType[]{
			LIGHT,SHADE,AIRCON,TV,ALARM	
		};
	}
	
	/**
	 * @param code byte code for the device type.
	 * @return the device type with the given code.
	 */
	public static DeviceType typeFromCode(byte code) throws Exception {
		switch (code) {
		case 0: return LIGHT;
		case 1: return SHADE;
		case 2: return AIRCON;
		case 3: return TV;
		case 4: return ALARM;
		default: throw new Exception("Illegal device type code: " + code);
		}
	}
	
	/**
	 * Used internally, identical to typeFromCode but does not throw exceptions.
	 * If an illegal code is given, returns LIGHT.
	 * @param code byte code for the device type.
	 * @return the device type with the given code.
	 */
	protected static DeviceType typeFromCodeSafe(byte code) {
		try {
			return typeFromCode(code);
		} catch (Exception e) {
			return LIGHT;
		}
	}


//	public int parmCount(byte opcode) {
//		return this.device.parmCount(opcode);
//	}
//		
//	public void printOpCode() {
//		this.device.printOpCodes();
//	}
//	
//	public void printParms(byte opcode) {
//		this.device.printParms(opcode);
//	}
//
}