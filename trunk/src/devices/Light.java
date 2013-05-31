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

import protocol.Message;
import common.Util;

public class Light extends Device {
	
	// legal opcodes
	private static final byte TURN_ON = 0;
	private static final byte TURN_OFF = 1;
	private static final byte DIM = 2;
	protected static Map<Byte,String> opcodeMap;
	protected static Map<Byte,String[]> opcodeParamMap;
	static {
		// opcode map
		opcodeMap = new HashMap<>();
		opcodeMap.put(TURN_ON, "Turn ON");
		opcodeMap.put(TURN_OFF, "Turn OFF");
		opcodeMap.put(DIM, "Dim");
		
		// opcode parameters map
		opcodeParamMap = new HashMap<>();
		opcodeParamMap.put(TURN_ON, null);
		opcodeParamMap.put(TURN_OFF, null);
		opcodeParamMap.put(DIM, new String[]{"Dim level"});
	}
	
	// fields
	private LightState state;
	private byte dimLevel;
	
	// constructors
	
	Light() {}
	
	public Light(String name, byte deviceNumber) {
		super(name, deviceNumber);
	}
	
	public Light(String name, byte deviceNumber, LightState state) {
		super(name, deviceNumber);
		this.state = state;
	}

	public Light(String name, byte deviceNumber, LightState state, byte[] parms) {
		super(name, deviceNumber);
		this.state = state;
		this.dimLevel = parms[0];
	}

	
	// methods
	
	public byte deviceType() {
		return DeviceType.LIGHT.type();
	}

	@Override
	public void doAction(Action action) throws Exception {
		byte opcode = action.opcode();
		// turn on
		if (opcode == TURN_ON) {
			if (action.numParams() != 0) throw new Exception("Turn on Light " +
					"expected 0 parameters, given: " + action.numParams());
			turnOn();
		}
		// turn off
		else if (opcode == TURN_OFF) {
			if (action.numParams() != 0) throw new Exception("Turn off Light " +
					"expected 0 parameters, given: " + action.numParams());
			turnOff();
		}
		// dim
		else if (opcode == DIM) {
			if (action.numParams() != 1) throw new Exception("Dim Light " +
					"expected 1 parameters, given: " + action.numParams());
			dim(action.getParam(0));
		}
		// error
		else {
			throw new Exception("Illegal opcode for Light: " + opcode);
		}
	}
	
	// local setters
	
	/**
	 * Turns on the light.
	 * @throws Exception if the light is already on.
	 */
	protected void turnOn() throws Exception {
		if (state == LightState.ON)
			throw new Exception("Cannot turn on Light " +
					deviceNumber + " (" + name + ") when already on");
		state = LightState.ON;
	}
	
	/**
	 * Turns off the light.
	 * @throws Exception if the light is already off.
	 */
	protected void turnOff() throws Exception {
		if (state == LightState.OFF)
			throw new Exception("Cannot turn off Light " +
					deviceNumber + " (" + name + ") when already off");
		state = LightState.OFF;
	}
	
	/**
	 * Sets the dim level of the light.
	 * @param dimLevel the dim level to set.
	 * @throws Exception if the light is off.
	 */
	protected void dim(byte dimLevel) throws Exception {
		if (state == LightState.OFF)
			throw new Exception("Cannot dim Light " +
					deviceNumber + " (" + name + ") when off");
		this.dimLevel = dimLevel;
	}
	
	public String toString() {
		return Util.bufferLeft(' ', 16, name) + state.ordinal();
	}
	
	public byte[] getBytes() {
		return Util.cat(
				Util.bufferLeft(' ', 16, name).getBytes(),	// name
				(byte)state.ordinal(),						// state
				new byte[]{dimLevel});						// params
	}
	
	// getters
	
	public LightState state() {
		return state;
	}
	
	public byte dimLevel() {
		return dimLevel;
	}
	
	public String toPrettyString() {
		return String.format("#%03d %-16s %-10s dim-level: %d",
				deviceNumber, name, state, dimLevel);
	}
	
	public Message getActionMessage(byte sequenceNumber, byte opcode, byte[] parameters) {
		Action action = new Action(sequenceNumber, this.deviceType(), (byte) this.deviceNumber, opcode, parameters);
		return action.toMessage();
	}
	
	public Map<Byte,String> opCodesMap() {
		return opcodeMap;
	}
	
	public Map<Byte,String[]> opCodesParamMap() {
		return opcodeParamMap;
	}
}

/**
 * Enumeration of Light states.
 */
enum LightState {
	OFF((byte)0),
	ON((byte)1);
	
	private LightState(byte type) {
		this.type = type;
	}
	
	private byte type;
	
	public byte type() {
		return type;
	}
	
	public static LightState typeFromCode(byte code) {
		switch (code) {
			case 0: return OFF;
			case 1: return ON;
			default: {
				throw new RuntimeException("Invalid LightState given: " + code);
			}
		}
	}	
}
