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
 * File name: Device.java
 * 
 * Purpose:
 * Abstract class for a Device that can be part of a House controlled by the
 * protocol.
 * 
 * Relevant requirements:
 * - SERVICE - device representation, state maintenance and functionality to
 *   apply actions on it are part of the protocol service.
 * 
 * =============================================================================
 */

package devices;

import java.util.Map;

import protocol.Message;

public abstract class Device {

	// device information
	// common to all device types
	
	// fields
	
	/**
	 * The device name
	 */
	protected final String name;
	/**
	 * The device id number
	 */
	protected byte deviceNumber;
	
	// constructors
	
	/**
	 * Default constructor, should never be called.
	 */
	Device() {
		this.name = "Not a real device.";
	}
	
	/**
	 * Constructs a new device with the given name and device number.
	 */
	public Device(String name, byte deviceNumber) {
		this.name = name.trim();
		this.deviceNumber = deviceNumber;
	}
	
	// abstract methods
	
	/**
	 * @return the byte code of this device type.
	 */
	public abstract byte deviceType();
	
	/**
	 * Applies the given action on this device.
	 * @param action the action to perform.
	 * @throws Exception if this action is illegal or invalid for this device
	 * type or state.
	 */
	public abstract void doAction(Action action) throws Exception;
	
	/**
	 * @return the byte stream representing the device.
	 */
	public abstract byte[] getBytes();
	
	/**
	 * @return a structured string representation of the device.
	 */
	public abstract String toPrettyString();
	
	/**
	 * @return a mapping of the device's opcodes to their names.
	 */
	public abstract Map<Byte,String> opCodesMap();
	
	/**
	 * @return a mapping of the device's opcodes to the names of the opcodes
	 * parameters, or null if the opcode has none.
	 */
	public abstract Map<Byte,String[]> opCodesParamMap();
	
	// common
	
	/**
	 * @return the device name.
	 */
	public String name() {
		return name;
	}
	
	/**
	 * @return the device unique id.
	 */
	public byte deviceNumber() {
		return deviceNumber;
	}
	
	/**
	 * Sets the device number to the given one.
	 * @param deviceNumber the device number to set.
	 */
	protected void setDeviceNumber(byte deviceNumber) {
		this.deviceNumber = deviceNumber;
	}
	
	/**
	 * @return the device generated from the given byte code, without input
	 * check.
	 */
	public static Device createDeviceFromBytes(DeviceType deviceType,
			byte deviceNum, byte[] d) {
		// initialize fields
		String desc = new String(d, 0, 16);
		byte state = d[16];
		byte[] parms = new byte[d.length - 17];
		for (int i = 0; i < parms.length; i++) {
			parms[i] = d[17 + i];
		}
		// handle different devices
		switch (deviceType) {
		case LIGHT: {
			return new Light(desc, deviceNum, LightState.typeFromCode(state),
					parms);
		}
		case SHADE: {
			return new Shade(desc, deviceNum, ShadeState.typeFromCode(state),
					parms);
		}
		case AIRCON: {
			return new AirCon(desc, deviceNum, AirConState.typeFromCode(state),
					parms);
		}
		case TV: {
			return new TV(desc, deviceNum, TVState.typeFromCode(state), parms);
		}
		case ALARM: {
			return new Alarm(desc, deviceNum, AlarmState.typeFromCode(state),
					parms);
		}
		default: {
			throw new RuntimeException("Invalid device type.");
		}
		}
	}
	
	/**
	 * @return the action message generated from the given sequence number,
	 * opcode and parameters.
	 */
	public Message getActionMessage(byte sequenceNumber, byte opcode, byte[] parameters) {
		byte[] b = new byte[5 + parameters.length];
		int i = 0;
		b[i++] = Message.OP_ACTION;
		b[i++] = sequenceNumber;
		b[i++] = deviceType();
		b[i++] = deviceNumber;
		b[i++] = opcode;
		for (byte param: parameters)
			b[i++] = param;
		return new Message(b);		
	}
}
