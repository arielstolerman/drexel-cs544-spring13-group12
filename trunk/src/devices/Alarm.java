package devices;

import common.Util;

public class Alarm extends Device {
	
	// legal opcodes
	private static final byte TURN_ON = 0;
	private static final byte TURN_OFF = 1;
	private static final byte ARM = 2;
	
	// fields
	private AlarmState state;
	
	// constructors
	
	public Alarm(String name, int deviceNumber) {
		super(name, deviceNumber);
	}
	
	public Alarm(String name, int deviceNumber, AlarmState state) {
		super(name, deviceNumber);
		this.state = state;
	}
	
	// methods
	
	public byte deviceType() {
		return DeviceType.ALARM.type();
	}

	@Override
	public void doAction(Action action) throws Exception {
		byte opcode = action.opcode();
		// turn on
		if (opcode == TURN_ON) {
			if (action.numParams() != 0) throw new Exception("Turn on Alarm " +
					"expected 0 parameters, given: " + action.numParams());
			turnOn();
		}
		// turn off
		else if (opcode == TURN_OFF) {
			if (action.numParams() != 0) throw new Exception("Turn off Alarm " +
					"expected 0 parameters, given: " + action.numParams());
			turnOff();
		}
		// dim
		else if (opcode == ARM) {
			if (action.numParams() != 0) throw new Exception("Arm Alarm " +
					"expected 0 parameters, given: " + action.numParams());
			arm();
		}
		// error
		else {
			throw new Exception("Illegal opcode for Alarm: " + opcode);
		}
	}
	
	// local setters
	
	/**
	 * Turns on the alarm.
	 * @throws Exception if the alarm is already on.
	 */
	protected void turnOn() throws Exception {
		if (state == AlarmState.ON)
			throw new Exception("Cannot turn on Alarm " +
					deviceNumber + " (" + name + ") when already on");
		state = AlarmState.ON;
	}
	
	/**
	 * Turns off the alarm.
	 * @throws Exception if the alarm is already off.
	 */
	protected void turnOff() throws Exception {
		if (state == AlarmState.OFF)
			throw new Exception("Cannot turn off Alarm " +
					deviceNumber + " (" + name + ") when already off");
		state = AlarmState.OFF;
	}
	
	/**
	 * Arms the alarm.
	 * @throws Exception if the alarm is already armed.
	 */
	protected void arm() throws Exception {
		if (state == AlarmState.ARMED)
			throw new Exception("Cannot arm Alarm " +
					deviceNumber + " (" + name + ") when already armed");
		state = AlarmState.ARMED;
	}
	
	public String toString() {
		return Util.bufferLeft(' ', 16, name) + state.ordinal();
	}
	
	public byte[] getBytes() {
		return Util.cat(Util.bufferLeft(' ', 16, name).getBytes(), (byte)state.ordinal());
	}
}

/**
 * Enumeration of Alarm states.
 */
enum AlarmState {
	OFF,
	ON,
	ARMED
}
