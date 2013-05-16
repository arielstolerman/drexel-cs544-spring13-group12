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
	
	/**
	 * @return the byte code of this device type.
	 */
	abstract public byte deviceType();
	
	/**
	 * Applies the given action on this device.
	 * @param action the action to perform.
	 * @throws Exception if this action is illegal or invalid for this device
	 * type or state.
	 */
	abstract public void doAction(Action action) throws Exception;
	
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
