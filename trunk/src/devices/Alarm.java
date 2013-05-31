/* =============================================================================
 * CS544 - Computer Networks
 * Drexel University, Spring 2013
 * Protocol Implementation: Remote Smart House Control
 * Group 12:
 * - Ryan Corcoran
 * - Amber Heilman
 * - Michael Mersic
 * - Ariel Stolerman
 * 
 * -----------------------------------------------------------------------------
 * File name: 
 * 
 * Purpose:
 * 
 * 
 * Relevant requirements (details in the file):
 * - 
 * 
 * =============================================================================
 */

package devices;

import java.util.HashMap;
import java.util.Map;

import common.Util;

public class Alarm extends Device {
	
	// legal opcodes
	private static final byte TURN_ON = 0;
	private static final byte TURN_OFF = 1;
	private static final byte ARM = 2;
	protected static Map<Byte,String> opcodeMap;
	protected static Map<Byte,String[]> opcodeParamMap;
	static {
		// opcode map
		opcodeMap = new HashMap<>();
		opcodeMap.put(TURN_ON, "Turn ON");
		opcodeMap.put(TURN_OFF, "Turn OFF");
		opcodeMap.put(ARM, "Arm");
		
		// opcode parameters map
		opcodeParamMap = new HashMap<>();
		opcodeParamMap.put(TURN_ON, null);
		opcodeParamMap.put(TURN_OFF, null);
		opcodeParamMap.put(ARM, null);
	}
	
	// fields
	private AlarmState state;
	
	// constructors
	
	Alarm() {}
	
	public Alarm(String name, byte deviceNumber) {
		super(name, deviceNumber);
	}
	
	public Alarm(String name, byte deviceNumber, AlarmState state) {
		super(name, deviceNumber);
		this.state = state;
	}
	
	// methods
	
	public Alarm(String name, byte deviceNumber, AlarmState state, byte[] parms) {
		super(name, deviceNumber);
		this.state = state;
	}

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
		return Util.cat(
				Util.bufferLeft(' ', 16, name).getBytes(),	// name
				(byte)state.ordinal(),						// state
				new byte[]{});								// params
	}
	
	public String toPrettyString() {
		return String.format("#%03d %-16s %-10s",
				deviceNumber, name, state);
	}
	
	public Map<Byte,String> opCodesMap() {
		return opcodeMap;
	}
	
	public Map<Byte,String[]> opCodesParamMap() {
		return opcodeParamMap;
	}			
}

/**
 * Enumeration of Alarm states.
 */
enum AlarmState {
	OFF((byte)0),
	ON((byte)1),
	ARMED((byte)2);
	
	private AlarmState(byte type) {
		this.type = type;
	}
	
	private byte type;
	
	public byte type() {
		return type;
	}
	
	public static AlarmState typeFromCode(byte code) {
		switch (code) {
			case 0: return OFF;
			case 1: return ON;
			case 2: return ARMED;
			default: {
				throw new RuntimeException("Invalid AlarmState given: " + code);
			}
		}
	}		
}
