package devices;

import common.Util;

public class Shade extends Device {
	
	// legal opcodes
	private static final byte PUT_DOWN = 0;
	private static final byte PULL_UP = 1;
	private static final byte DIM = 2;
	
	// fields
	private ShadeState state;
	private byte dimLevel;
	
	// constructors
	
	Shade() {}
	
	public Shade(String name, byte deviceNumber) {
		super(name, deviceNumber);
	}
	
	public Shade(String name, byte deviceNumber, ShadeState state) {
		super(name, deviceNumber);
		this.state = state;
	}
	
	// methods
	
	public Shade(String desc, byte deviceNum, ShadeState state, byte[] parms) {
		super(desc, deviceNum);
		this.state = state;
		this.dimLevel = parms[0];
	}

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
	
	public byte dimLevel() {
		return dimLevel;
	}
	
	public String toPrettyString() {
		return String.format("#%03d %-16s %-10s dim-level: %d",
				deviceNumber, name, state, dimLevel);
	}
	
	
	public int parmCount(byte opcode) {
		if (opcode == DIM)
			return 1;
		else 
			return 0;
	}
	
	public void printOpCodes() {
		System.out.println("Pull Up: 0");
		System.out.println("Pull Down: 1");
		System.out.println("Dim: 2");
	}
	
	public void printParms(byte opcode) {
		if (opcode == DIM) {
			System.out.println("Parm 1: dim level");
		}
	}
	
	public int maxParms() {
		return 1;
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
