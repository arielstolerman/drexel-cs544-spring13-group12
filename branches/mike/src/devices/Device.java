package devices;

public abstract class Device {

	// device information
	// common to all device types
	
	protected final String name;
	private int deviceNumber;
	
	/**
	 * Constructs a new device with the given name and id.
	 * @param name
	 * @param deviceNumber
	 */
	public Device(String name) {
		this.name = name;
	}
	
	// methods
	
	abstract public byte deviceType();
	abstract public boolean doAction(Action A);
	
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











