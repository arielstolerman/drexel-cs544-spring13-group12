package devices;

public class Light extends Device {
	
	// legal opcodes
	private static final byte TURN_ON = 0;
	private static final byte TURN_OFF = 1;
	private static final byte DIM = 2;
	
	// fields
	
	private LightState state;
	private byte dimLevel;
	
	public Light(String name, int deviceNumber) {
		super(name, deviceNumber);
	}
	
	public Light(String name, int deviceNumber, LightState state) {
		super(name, deviceNumber);
		this.state = state;
	}
	
	public byte deviceType() {
		return 0;
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
	
	// methods
	
	/**
	 * TODO
	 * @return
	 */
	private void turnOn() throws Exception {
		if (state == LightState.ON)
			throw new Exception("Cannot turn on Light " +
					deviceNumber + " (" + name + ") when already on");
		state = LightState.ON;
	}
	
	/**
	 * TODO
	 * @return
	 */
	private void turnOff() throws Exception {
		if (state == LightState.OFF)
			throw new Exception("Cannot turn off Light " +
					deviceNumber + " (" + name + ") when already off");
		state = LightState.OFF;
	}
	
	/**
	 * TODO
	 * @param dimLevel
	 * @return
	 */
	private void dim(byte dimLevel) throws Exception {
		if (state == LightState.OFF)
			throw new Exception("Cannot dim Light " +
					deviceNumber + " (" + name + ") when off");
		this.dimLevel = dimLevel;
	}
	
	public String toString() {
		return Util.bufferLeft(' ', 16, name) + state.ordinal();
	}
}

/**
 * Enumeration of Light states.
 */
enum LightState {
	OFF,
	ON
}
