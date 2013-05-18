package devices;

public class Shade extends Device {
	
	// legal opcodes
	private static final byte PUT_DOWN = 0;
	private static final byte PULL_UP = 1;
	private static final byte DIM = 2;
	
	// fields
	private ShadeState state;
	private byte dimLevel;
	
	// constructors
	
	public Shade(String name, int deviceNumber) {
		super(name, deviceNumber);
	}
	
	public Shade(String name, int deviceNumber, ShadeState state) {
		super(name, deviceNumber);
		this.state = state;
	}
	
	// methods
	
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
	
	// getters
	
	public byte dimLevel() {
		return dimLevel;
	}
}

/**
 * Enumeration of Shade states.
 */
enum ShadeState {
	UP,
	DOWN
}
