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
 * File name: House.java
 * 
 * Purpose:
 * Class for representing a house controlled by the protocol. The house
 * maintains all device instances. The server holds a house object for the house
 * it controls, and the clients receive encoding of the house, from which they
 * generate a local image of the house they control remotely.
 * 
 * Relevant requirements (details in the file):
 * - SERVICE - the house is the core of the service, since the entire purpose of
 *   the protocol is to remotely control the house.
 * 
 * =============================================================================
 */

package devices;

import java.util.ArrayList;
import java.util.List;

import protocol.Message;

import common.Util;

public class House {
	
	// fields
	
	/**
	 * List of devices available in the house
	 */
	private List<List<Device>> devices = new ArrayList<List<Device>>();
	/**
	 * Sequence number field, used for tracking actions generated to be applied
	 * on the house devices.
	 */
	private byte sequenceNumber = 0;
	
	// constructors
	
	/**
	 * constructs a new house with no devices.
	 */
	public House() {
		devices = new ArrayList<List<Device>>();
		devices.add(new ArrayList<Device>());
		devices.add(new ArrayList<Device>());
		devices.add(new ArrayList<Device>());
		devices.add(new ArrayList<Device>());
		devices.add(new ArrayList<Device>());
	}
	
	/**
	 * Adds the given device to the list of devices of the same type, with the
	 * index of the last element as the device number.
	 * @param device the device to add.
	 * @return the added device.
	 */
	public Device addDevice(Device device) {
		List<Device> l = devices.get(device.deviceType());
		device.setDeviceNumber((byte)l.size());
		l.add(device);
		return device;
	}
	
	/**
	 * Applies the given action on the respective device.
	 * @param action
	 */
	public synchronized void doAction(Action action) throws Exception {
		devices.get(action.deviceType()).get(action.deviceNumber())
				.doAction(action);
	}
	
	/**
	 * @return the init message for this house.
	 */
	public byte[] getInit() {
		// initialize byte stream and init opcode
		List<Byte> bytes = new ArrayList<Byte>();
		bytes.add((byte) Message.OP_INIT);
		// iterate over devices and accumulate their encoding
		for (List<Device> deviceList : devices) {			
			bytes.add((byte) deviceList.size());
			for (Device device : deviceList) {
				bytes.addAll(Util.toByteList(device.getBytes()));
			}
		}
		bytes.addAll(Util.toByteList("\n".getBytes()));
		// convert to array before return
		byte bytesArr[] = new byte[bytes.size()];
		for (int i = 0; i < bytesArr.length; i++) {
			bytesArr[i] = bytes.get(i);
		}
		return bytesArr;
	}
	
	// used for pretty printing the state of the house
	private static final String THIN_SEP =
			"----------------------------------------" +
			"------------------------------";
	private static final String THICK_SEP =
			"========================================" +
			"==============================";
	
	/**
	 * Prints the current state of the house - the list of all contained devices
	 * and their states.
	 */
	public void prettyPrint() {
		String ind = "       ";
		String pre;
		int devTypes = devices.size();
		List<Device> devs;
		
		// print header
		System.out.println(THICK_SEP);
		System.out.println("House current state:");
		System.out.println(String.format("%-7s%-4s %-16s %-10s %s",
				"Type", "Num", "Name", "State", "Params"));
		
		// iterate over device types and print all devices
		for (int devType = 0; devType < devTypes; devType++) {
			System.out.println(THIN_SEP);
			pre = String.format("%-7s",
					DeviceType.typeFromCodeSafe((byte) devType));
			devs = devices.get(devType);
			for (Device d: devs) {
				System.out.println(
						(pre == null ? ind : pre) +
						d.toPrettyString());
				pre = null;
			}
		}	
		System.out.println(THICK_SEP);
	}
	
	/**
	 * @return the house object generated from the given INIT message, used by
	 * the client to construct the image of the house locally from the encoding
	 * sent from the server.
	 */
	public static House createHouseFromInit(Message m) {
		// initialize an empty house
		House house = new House();
		byte[] b = m.bytes();
		int index = 1;
		// iterate over device types and construct devices
		for (byte deviceType = 0; deviceType < 5; deviceType++) {
			int deviceCount = b[index++];
			// construct device instances
			for (byte deviceNum = 0; deviceNum < deviceCount; deviceNum++) {
				int numParms = DeviceType.typeFromCodeSafe(deviceType)
						.numParams();
				byte[] d = new byte[17+numParms];
				for (int k = 0; k < d.length; k++) {
					d[k] = b[index++];
				}
				// create device and add to house
				Device device = Device.createDeviceFromBytes(
						DeviceType.typeFromCodeSafe(deviceType), deviceNum, d);
				house.addDevice(device);
			}
		}
		return house;
	}

	/**
	 * @return the action message generated from the given device type, number,
	 * sequence number, opcode and parameters.
	 */
	public Message createActionMessage(byte deviceType, byte deviceNumber,
			byte opcode, byte[] params) {
		try {
			return devices.get(deviceType).get(deviceNumber)
					.getActionMessage(sequenceNumber++, opcode, params);
		} catch (Exception e) {
			e.printStackTrace();
			return Message.ERROR_GENERAL;
		}
	}

	/**
	 * Apply the update from the given update message on the house.
	 */
	public void doUpdate(Message updateMessage) throws Exception {
		// disguise the update as an action
		byte[] b = updateMessage.bytes();
		byte[] actionBytes = new byte[b.length + 1];
		actionBytes[0] = Message.OP_ACTION;
		actionBytes[1] = 0x00;	// dummy sequence number
		for (int i = 1; i < b.length; i++)
			actionBytes[i + 1] = b[i];
		Action a = new Action(actionBytes);
		// apply the action
		doAction(a);
	}
	
	// getters
	
	/**
	 * @return all devices in the house.
	 */
	public List<List<Device>> devices() {
		return devices;
	}
}
