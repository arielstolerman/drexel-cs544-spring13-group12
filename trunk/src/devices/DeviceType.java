package devices;

/**
 * Enumerator for known device types.
 */
public enum DeviceType {
	LIGHT((byte) 0, new Light()),
	SHADE((byte) 1, new Shade()),
	AIRCON((byte) 2, new AirCon()),
	TV((byte) 3, new TV()),
	ALARM((byte) 4, new Alarm());
	//NO_SUCH_DEVICE((byte) -1, null);
	
	private DeviceType(byte type, Device device) {
		this.type = type;
		this.device = device;
	}
	
	private byte type;
	private Device device; 
	
	public byte type() {
		return type;
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

	public int parmCount(byte opcode) {
		return this.device.parmCount(opcode);
	}
		
	public void printOpCode() {
		this.device.printOpCodes();
	}
	
	public void printParms(byte opcode) {
		this.device.printParms(opcode);
	}

	public int maxParms() {
		return this.device.maxParms();
	}
}