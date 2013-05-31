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

public class AirCon extends Device {
	
	// legal opcodes
	private static final byte TURN_ON = 0;
	private static final byte TURN_OFF = 1;
	private static final byte SET_TEMP = 2;
	protected static Map<Byte,String> opcodeMap;
	protected static Map<Byte,String[]> opcodeParamMap;
	static {
		// opcode map
		opcodeMap = new HashMap<>();
		opcodeMap.put(TURN_ON, "Turn ON");
		opcodeMap.put(TURN_OFF, "Turn OFF");
		opcodeMap.put(SET_TEMP, "Set temperature");
		
		// opcode parameters map
		opcodeParamMap = new HashMap<>();
		opcodeParamMap.put(TURN_ON, null);
		opcodeParamMap.put(TURN_OFF, null);
		opcodeParamMap.put(SET_TEMP, new String[]{"Temperature"});
	}
	
	// fields
	private AirConState state;
	private byte temp;
	
	// constructors
	
	AirCon() {}
	
	public AirCon(String name, byte deviceNumber) {
		super(name, deviceNumber);
	}
	
	public AirCon(String name, byte deviceNumber, AirConState state) {
		super(name, deviceNumber);
		this.state = state;
	}
	
	// methods
	
	public AirCon(String name, byte deviceNumber, AirConState state, byte[] parms) {
		super(name, deviceNumber);
		this.state = state;
		this.temp = parms[0];
	}

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
		return Util.cat(
				Util.bufferLeft(' ', 16, name).getBytes(),	// name
				(byte)state.ordinal(),						// state
				new byte[]{temp});							// params
	}
	
	// getters
	
	public byte temp() {
		return temp;
	}
	
	public String toPrettyString() {
		return String.format("#%03d %-16s %-10s temp: %d",
				deviceNumber, name, state, temp);
	}
	
	public Map<Byte,String> opCodesMap() {
		return opcodeMap;
	}
	
	public Map<Byte,String[]> opCodesParamMap() {
		return opcodeParamMap;
	}
}

/**
 * Enumeration of AirCon states.
 */
enum AirConState {
	OFF((byte)0),
	ON((byte)1);
	
	private AirConState(byte type) {
		this.type = type;
	}
	
	private byte type;
	
	public byte type() {
		return type;
	}
	
	public static AirConState typeFromCode(byte code) {
		switch (code) {
			case 0: return OFF;
			case 1: return ON;
			default: {
				throw new RuntimeException("Invalid AirConState given: " + code);
			}
		}
	}	
}
