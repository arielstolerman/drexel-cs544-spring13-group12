package devices;

import common.Util;

public class AirCon extends Device {
	
	// legal opcodes
	private static final byte TURN_ON = 0;
	private static final byte TURN_OFF = 1;
	private static final byte SET_TEMP = 2;
	
	// fields
	private AirConState state;
	private byte temp;
	
	// constructors
	
	public AirCon(String name, int deviceNumber) {
		super(name, deviceNumber);
	}
	
	public AirCon(String name, int deviceNumber, AirConState state) {
		super(name, deviceNumber);
		this.state = state;
	}
	
	// methods
	
	public byte deviceType() {
		return DeviceType.AIRCON.type();
	}

	@Override
	public void doAction(Action action) throws Exception {
		byte opcode = action.opcode();
		// turn on
		if (opcode == TURN_ON) {
			if (action.numParams() != 0) throw new Exception("Turn on AirCon " +
					"expected 0 parameters, given: " + action.numParams());
			turnOn();
		}
		// turn off
		else if (opcode == TURN_OFF) {
			if (action.numParams() != 0) throw new Exception("Turn off AirCon " +
					"expected 0 parameters, given: " + action.numParams());
			turnOff();
		}
		// set temp
		else if (opcode == SET_TEMP) {
			if (action.numParams() != 1) throw new Exception("Set AirCon temp " +
					"expected 1 parameters, given: " + action.numParams());
			setTemp(action.getParam(0));
		}
		// error
		else {
			throw new Exception("Illegal opcode for AirCon: " + opcode);
		}
	}
	
	// local setters
	
	/**
	 * Turns on the AirCon.
	 * @throws Exception if the AirCon is already on.
	 */
	protected void turnOn() throws Exception {
		if (state == AirConState.ON)
			throw new Exception("Cannot turn on AirCon " +
					deviceNumber + " (" + name + ") when already on");
		state = AirConState.ON;
	}
	
	/**
	 * Turns off the AirCon.
	 * @throws Exception if the AirCon is already off.
	 */
	protected void turnOff() throws Exception {
		if (state == AirConState.OFF)
			throw new Exception("Cannot turn off AirCon " +
					deviceNumber + " (" + name + ") when already off");
		state = AirConState.OFF;
	}
	
	/**
	 * Sets the temperature of the AirCon.
	 * @param temp temperature to set.
	 * @throws Exception if the AirCon is off.
	 */
	protected void setTemp(byte temp) throws Exception {
		if (state == AirConState.OFF)
			throw new Exception("Cannot set temp for AirCon " +
					deviceNumber + " (" + name + ") when off");
		this.temp = temp;
	}
	
	public String toString() {
		return Util.bufferLeft(' ', 16, name) + state.ordinal();
	}
	
	public byte[] getBytes() {
		return Util.cat(Util.bufferLeft(' ', 16, name).getBytes(), (byte)state.ordinal());
	}
	
	// getters
	
	public byte temp() {
		return temp;
	}
	
	public String toPrettyString() {
		return String.format("#%03d %-16s %-10s temp: %d",
				deviceNumber, name, state, temp);
	}
}

/**
 * Enumeration of AirCon states.
 */
enum AirConState {
	OFF,
	ON
}
