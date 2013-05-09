package devices;

public abstract class Device {

	// device information
	// common to all device types
	
	protected String name;
	protected int deviceNumber;
	
	// private default constructor to disable
	// "empty" abstract devices
	private Device() {}
	
	/**
	 * Constructs a new device with the given name and id.
	 * @param name
	 * @param deviceNumber
	 */
	public Device(String name, int deviceNumber) {
		this();
		this.name = name;
		this.deviceNumber = deviceNumber;
	}
	
	// methods
	
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
}











