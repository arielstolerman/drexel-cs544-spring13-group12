package devices;

import common.Util;

public class TV extends Device {
	
	// legal opcodes
	private static final byte TURN_ON = 0;
	private static final byte TURN_OFF = 1;
	private static final byte SET_CHANNEL = 2;
	private static final byte SET_VOLUME = 3;
	
	// fields
	private TVState state;
	private byte channel;
	private byte volume;
	
	// constructors
	
	TV() {}
	
	public TV(String name, byte deviceNumber) {
		super(name, deviceNumber);
	}
	
	public TV(String name, byte deviceNumber, TVState state) {
		super(name, deviceNumber);
		this.state = state;
	}
	
	// methods
	
	public TV(String name, byte deviceNumber, TVState state, byte[] parms) {
		super(name, deviceNumber);
		this.state = state;
		this.channel = parms[0];
		this.volume = parms[1];
	}

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
	
	public String toString() {
		return Util.bufferLeft(' ', 16, name) + state.ordinal();
	}
	
	public byte[] getBytes() {
		return Util.cat(
				Util.bufferLeft(' ', 16, name).getBytes(),	// name
				(byte)state.ordinal(),						// state
				new byte[]{channel, volume});				// params
	}
	
	// getters
	
	public byte channel() {
		return channel;
	}
	
	public byte volume() {
		return volume;
	}
	
	public String toPrettyString() {
		return String.format("#%03d %-16s %-10s channel: %-4d volume: %d",
				deviceNumber, name, state, channel, volume);
	}
	
	public int parmCount(byte opcode) {
		if (opcode == SET_CHANNEL || opcode == SET_VOLUME) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public void printOpCodes() {
		System.out.println("Turn on: 0");
		System.out.println("Turn off: 1");
		System.out.println("Set channel: 2");
		System.out.println("Set volume: 3");
	}
	
	public void printParms(byte opcode) {
		if (opcode == SET_CHANNEL) {
			System.out.println("Parm 1: channel");
		} else if (opcode == SET_VOLUME) {
			System.out.println("Parm 1: set volume");
		}
	}
	
	public int maxParms() {
		return 2;
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
