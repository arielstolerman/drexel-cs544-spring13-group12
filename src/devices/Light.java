package devices;

public class Light extends Device {
	
	// fields
	
	private LightState state;
	private byte dimLevel;
	
	public Light(String name) {
		super(name);
	}
	
	public Light(String name, LightState state) {
		super(name);
		this.state = state;
	}
	
	public byte deviceType() {
		return 0;
	}

	public boolean doAction(Action A) {
		return false;
	}
	
	// methods
	
	/**
	 * TODO
	 * @return
	 */
	public boolean turnOn() {
		if (state == LightState.ON)
			return false;
		state = LightState.ON;
		return true;
	}
	
	/**
	 * TODO
	 * @return
	 */
	public boolean turnOff() {
		if (state == LightState.OFF)
			return false;
		state = LightState.OFF;
		return true;
	}
	
	/**
	 * TODO
	 * @param dimLevel
	 * @return
	 */
	public boolean dim(byte dimLevel) {
		if (state == LightState.OFF)
			return false;
		this.dimLevel = dimLevel;
		return true;
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
