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
 * File name: TV.java
 * 
 * Purpose:
 * Class for representation of a TV device, that can be part of a house
 * controlled by the protocol.
 * 
 * Relevant requirements:
 * - SERVICE - device representation, state maintenance and functionality to
 *   apply actions on it are part of the protocol service.
 * 
 * =============================================================================
 */

package devices;

import java.util.HashMap;
import java.util.Map;

import common.Util;

public class TV extends Device {
	
	// legal opcodes
	private static final byte TURN_ON = 0;
	private static final byte TURN_OFF = 1;
	private static final byte SET_CHANNEL = 2;
	private static final byte SET_VOLUME = 3;
	protected static Map<Byte,String> opcodeMap;
	protected static Map<Byte,String[]> opcodeParamMap;
	static {
		// opcode map
		opcodeMap = new HashMap<>();
		opcodeMap.put(TURN_ON, "Turn ON");
		opcodeMap.put(TURN_OFF, "Turn OFF");
		opcodeMap.put(SET_CHANNEL, "Set channel");
		opcodeMap.put(SET_VOLUME, "Set volume");
		
		// opcode parameters map
		opcodeParamMap = new HashMap<>();
		opcodeParamMap.put(TURN_ON, null);
		opcodeParamMap.put(TURN_OFF, null);
		opcodeParamMap.put(SET_CHANNEL, new String[]{"Channel"});
		opcodeParamMap.put(SET_VOLUME, new String[]{"Volume"});
	}
	
	// fields
	private TVState state;
	private byte channel;
	private byte volume;
	
	// constructors
	
	/**
	 * Default constructor for TV.
	 */
	TV() {}
	
	/**
	 * Constructs TV with the given name and device number.
	 */
	public TV(String name, byte deviceNumber) {
		super(name, deviceNumber);
	}
	
	/**
	 * Constructs TV with the given name, device number and initial state.
	 */
	public TV(String name, byte deviceNumber, TVState state) {
		super(name, deviceNumber);
		this.state = state;
	}
	
	/**
	 * Constructs TV with the given name, device number, initial state and
	 * parameters (should contain only dim level).
	 */
	public TV(String name, byte deviceNumber, TVState state, byte[] params) {
		super(name, deviceNumber);
		this.state = state;
		this.channel = params[0];
		this.volume = params[1];
	}

	// overriding methods
	
	@Override
	public byte deviceType() {
		return DeviceType.TV.type();
	}

	@Override
	public void doAction(Action action) throws Exception {
		byte opcode = action.opcode();
		// turn on
		if (opcode == TURN_ON) {
			if (action.numParams() != 0) throw new Exception("Turn on TV " +
					"expected 0 parameters, given: " + action.numParams());
			turnOn();
		}
		// turn off
		else if (opcode == TURN_OFF) {
			if (action.numParams() != 0) throw new Exception("Turn off TV " +
					"expected 0 parameters, given: " + action.numParams());
			turnOff();
		}
		// set channel
		else if (opcode == SET_CHANNEL) {
			if (action.numParams() != 1) throw new Exception("Set TV channel " +
					"expected 1 parameters, given: " + action.numParams());
			setChannel(action.getParam(0));
		}
		// set channel
		else if (opcode == SET_VOLUME) {
			if (action.numParams() != 1) throw new Exception("Set TV volume " +
					"expected 1 parameters, given: " + action.numParams());
			setVolume(action.getParam(0));
		}
		// error
		else {
			throw new Exception("Illegal opcode for TV: " + opcode);
		}
	}
	
	@Override
	public String toString() {
		return Util.bufferLeft(' ', 16, name) + state.ordinal();
	}
	
	@Override
	public byte[] getBytes() {
		return Util.cat(
				Util.bufferLeft(' ', 16, name).getBytes(),	// name
				(byte)state.ordinal(),						// state
				new byte[]{channel, volume});				// params
	}
	
	@Override
	public String toPrettyString() {
		return String.format("#%03d %-16s %-10s channel: %-4d volume: %d",
				deviceNumber, name, state, channel, volume);
	}
	
	@Override
	public Map<Byte,String> opCodesMap() {
		return opcodeMap;
	}
	
	@Override
	public Map<Byte,String[]> opCodesParamMap() {
		return opcodeParamMap;
	}
	
	// local setters
	
	/**
	 * Turns on the TV.
	 * @throws Exception if the TV is already on.
	 */
	protected void turnOn() throws Exception {
		if (state == TVState.ON)
			throw new Exception("Cannot turn on TV " +
					deviceNumber + " (" + name + ") when already on");
		state = TVState.ON;
	}
	
	/**
	 * Turns off the TV.
	 * @throws Exception if the TV is already off.
	 */
	protected void turnOff() throws Exception {
		if (state == TVState.OFF)
			throw new Exception("Cannot turn off TV " +
					deviceNumber + " (" + name + ") when already off");
		state = TVState.OFF;
	}
	
	/**
	 * Sets the TV channel.
	 * @param channel the channel to set.
	 * @throws Exception if the TV is off.
	 */
	protected void setChannel(byte channel) throws Exception {
		if (state == TVState.OFF)
			throw new Exception("Cannot set channel for TV " +
					deviceNumber + " (" + name + ") when off");
		this.channel = channel;
	}
	
	/**
	 * Sets the TV volume.
	 * @param volume the volume to set.
	 * @throws Exception if the TV is off.
	 */
	protected void setVolume(byte volume) throws Exception {
		if (state == TVState.OFF)
			throw new Exception("Cannot set volume for TV " +
					deviceNumber + " (" + name + ") when off");
		this.volume = volume;
	}
	
	// getters
	
	public byte channel() {
		return channel;
	}
	
	public byte volume() {
		return volume;
	}	
}

/**
 * Enumeration of TV states.
 */
enum TVState {
	OFF((byte)0),
	ON((byte)1);
	
	private TVState(byte type) {
		this.type = type;
	}
	
	private byte type;
	
	public byte type() {
		return type;
	}
	
	public static TVState typeFromCode(byte code) {
		switch (code) {
			case 0: return OFF;
			case 1: return ON;
			default: {
				throw new RuntimeException("Invalid TVState given: " + code);
			}
		}
	}		
}
