package devices;

/**
 * Enumerator for known device types.
 */
public enum DeviceType {
	LIGHT((byte) 0, new Light()),
	SHADE((byte) 1, new Shade()),
	AIRCON((byte) 2, new AirCon()),
	TV((byte) 3, new TV()),
	ALARM((byte) 4, new Alarm()),
	NO_SUCH_DEVICE((byte) -1, null);
	
	private DeviceType(byte type, Device device) {
		this.type = type;
		this.device = device;
	}
	
	private byte type;
	private Device device; 
	
	public byte type() {
		return type;
	}
	
	public static DeviceType typeFromCode(byte code) {
		switch (code) {
		case 0: return LIGHT;
		case 1: return SHADE;
		case 2: return AIRCON;
		case 3: return TV;
		case 4: return ALARM;
		default: return NO_SUCH_DEVICE;
		}
	}

	public int parmCount() {
		return this.device.parmCount();
	}
	
	public void printOpCode() {
		this.device.printOpCodes();
	}
	
	public void printParms(byte opcode) {
		this.device.printParms(opcode);
	}
}