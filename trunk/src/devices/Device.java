package devices;

public abstract class Device {

	// device information
	// common to all device types
	
	protected final String name;
	protected int deviceNumber;
	
	/**
	 * Constructs a new device with the given name and device number.
	 * @param name
	 * @param deviceNumber
	 */
	public Device(String name, int deviceNumber) {
		this.name = name;
		this.deviceNumber = deviceNumber;
	}
	
	// methods
	
	// abstract
	
	/**
	 * @return the byte code of this device type.
	 */
	public abstract byte deviceType();
	
	/**
	 * Applies the given action on this device.
	 * @param action the action to perform.
	 * @throws Exception if this action is illegal or invalid for this device
	 * type or state.
	 */
	public abstract void doAction(Action action) throws Exception;
	
	public abstract byte[] getBytes();
	
	// common
	
	/**
	 * @return the device name.
	 */
	public String name() {
		return name;
	}
	
	/**
	 * @return the device unique id.
	 */
	public int deviceNumber() {
		return deviceNumber;
	}
	
	protected void setDeviceNumber(int deviceNumber) {
		this.deviceNumber = deviceNumber;
	}
}

/**
 * Enumerator for known device types.
 */
enum DeviceType {
	LIGHT((byte) 0),
	SHADE((byte) 1),
	AIRCON((byte) 2),
	TV((byte) 3),
	ALARM((byte) 4);
	
	private DeviceType(byte type) {
		this.type = type;
	}
	
	private byte type;
	
	public byte type() {
		return type;
	}
}
