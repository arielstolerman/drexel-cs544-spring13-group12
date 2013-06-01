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
 * File name: Shade.java
 * 
 * Purpose:
 * Class for representation of a shade device, that can be part of a house
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

public class Shade extends Device {
	
	// legal opcodes
	private static final byte PUT_DOWN = 0;
	private static final byte PULL_UP = 1;
	private static final byte DIM = 2;
	protected static Map<Byte,String> opcodeMap;
	protected static Map<Byte,String[]> opcodeParamMap;
	static {
		// opcode map
		opcodeMap = new HashMap<>();
		opcodeMap.put(PUT_DOWN, "Put down");
		opcodeMap.put(PULL_UP, "Pull up");
		opcodeMap.put(DIM, "Dim");
		
		// opcode parameters map
		opcodeParamMap = new HashMap<>();
		opcodeParamMap.put(PUT_DOWN, null);
		opcodeParamMap.put(PULL_UP, null);
		opcodeParamMap.put(DIM, new String[]{"Dim level"});
	}
	
	// fields
	private ShadeState state;
	private byte dimLevel;
	
	// constructors
	
	/**
	 * Default constructor for Shade.
	 */
	Shade() {}
	
	/**
	 * Constructs Shade with the given name and device number.
	 */
	public Shade(String name, byte deviceNumber) {
		super(name, deviceNumber);
	}

	/**
	 * Constructs Shade with the given name, device number and initial state.
	 */
	public Shade(String name, byte deviceNumber, ShadeState state) {
		super(name, deviceNumber);
		this.state = state;
	}
	
	/**
	 * Constructs Shade with the given name, device number, initial state and
	 * parameters (should contain only dim level).
	 */
	public Shade(String desc, byte deviceNum, ShadeState state, byte[] parms) {
		this(desc, deviceNum, state);
		this.dimLevel = parms[0];
	}

	// overriding methods
	
	@Override
	public byte deviceType() {
		return DeviceType.SHADE.type();
	}

	@Override
	public void doAction(Action action) throws Exception {
		byte opcode = action.opcode();
		// turn on
		if (opcode == PUT_DOWN) {
			if (action.numParams() != 0) throw new Exception("Put down Shade " +
					"expected 0 parameters, given: " + action.numParams());
			putDown();
		}
		// turn off
		else if (opcode == PULL_UP) {
			if (action.numParams() != 0) throw new Exception("Pull up Shade " +
					"expected 0 parameters, given: " + action.numParams());
			pullUp();
		}
		// dim
		else if (opcode == DIM) {
			if (action.numParams() != 1) throw new Exception("Dim Shade " +
					"expected 1 parameters, given: " + action.numParams());
			dim(action.getParam(0));
		}
		// error
		else {
			throw new Exception("Illegal opcode for Shade: " + opcode);
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
				new byte[]{dimLevel});						// params
	}
	
	@Override
	public String toPrettyString() {
		return String.format("#%03d %-16s %-10s dim-level: %d",
				deviceNumber, name, state, dimLevel);
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
	 * Puts down the shade.
	 * @throws Exception if the shade is already down.
	 */
	protected void putDown() throws Exception {
		if (state == ShadeState.DOWN)
			throw new Exception("Cannot put down Shade " +
					deviceNumber + " (" + name + ") when already down");
		state = ShadeState.DOWN;
	}
	
	/**
	 * Pulls up the shade.
	 * @throws Exception if the shade is already up.
	 */
	protected void pullUp() throws Exception {
		if (state == ShadeState.UP)
			throw new Exception("Cannot pull up Shade " +
					deviceNumber + " (" + name + ") when already up");
		state = ShadeState.UP;
	}
	
	/**
	 * Sets the dim level of the shade.
	 * @param dimLevel the dim level to set.
	 * @throws Exception if the shade is up.
	 */
	protected void dim(byte dimLevel) throws Exception {
		if (state == ShadeState.UP)
			throw new Exception("Cannot dim Shade " +
					deviceNumber + " (" + name + ") when up");
		this.dimLevel = dimLevel;
	}
	
	// getters
	
	public byte dimLevel() {
		return dimLevel;
	}	
}

/**
 * Enumeration of Shade states.
 */
enum ShadeState {
	UP((byte) 0),
	DOWN((byte) 1);
	
	private ShadeState(byte type) {
		this.type = type;
	}
	
	private byte type;
	
	public byte type() {
		return type;
	}
	
	public static ShadeState typeFromCode(byte code) {
		switch (code) {
			case 0: return UP;
			case 1: return DOWN;
			default: {
				throw new RuntimeException("Invalid ShadeState given: " + code);
			}
		}
	}		
}
