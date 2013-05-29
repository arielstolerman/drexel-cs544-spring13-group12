package devices;

import protocol.Message;

public abstract class Device {

	// device information
	// common to all device types
	
	protected final String name;
	protected int deviceNumber;
	
	Device() {
		this.name = "Not a real device.";
	}
	
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
	
	public abstract String toPrettyString();
	
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

	public static Device createDeviceFromBytes(DeviceType deviceType, int deviceNum, byte[] d) {
		String desc = new String(d, 0, 16);
		byte state = d[16];
		
		switch (deviceType) {
			case LIGHT: {			
				return new Light(desc, deviceNum, LightState.typeFromCode(state));
			}
			case SHADE: {
				return new Shade(desc, deviceNum, ShadeState.typeFromCode(state));
			}
			case AIRCON: {
				return new AirCon(desc, deviceNum, AirConState.typeFromCode(state));
			}
			case TV: {
				return new TV(desc, deviceNum, TVState.typeFromCode(state));
			}
			case ALARM: {
				return new Alarm(desc, deviceNum, AlarmState.typeFromCode(state));
			}
			default: {
				throw new RuntimeException("Invalid device type.");
			}
		}
	}

	public Message getActionMessage(byte sequenceNumber, byte opcode, byte[] parameters) {
		throw new RuntimeException("getActionMessage not implemented");		
	}
	
	public void printOpCodes() { }
	public void printParms(byte opcode) { }
	public int parmCount() { return 0; } 
}
