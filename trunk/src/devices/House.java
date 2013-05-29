package devices;

import java.util.ArrayList;
import java.util.List;

import protocol.Message;

import common.Util;

public class House {
	
	// fields
	// list of devices available in the house
	
	private List<List<Device>> devices = new ArrayList<List<Device>>();
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
	public void doAction(Action action) throws Exception {
		devices.get(action.deviceType()).get(action.deviceNumber())
				.doAction(action);
	}
	
	/**
	 * @return the init message for this house.
	 */
	public byte[] getInit() {
		List<Byte> bytes = new ArrayList<Byte>();
		bytes.add((byte) Message.OP_INIT);
		for (List<Device> deviceList : devices) {			
			bytes.add((byte) deviceList.size());
			for (Device device : deviceList) {
				bytes.addAll(Util.toByteArray(device.getBytes()));
			}
		}
		bytes.addAll(Util.toByteArray("\n".getBytes()));
		
		byte bytesArr[] = new byte[bytes.size()];
		for (int i = 0; i < bytesArr.length; i++) {
			bytesArr[i] = bytes.get(i);
		}
		
		return bytesArr;
	}
	
	private static final String THIN_SEP =
			"----------------------------------------" +
			"----------------------------------------";
	private static final String THICK_SEP =
			"========================================" +
			"========================================";
	
	public void prettyPrint() {
		String ind = "       ";
		String pre;
		int devTypes = devices.size();
		List<Device> devs;
		
		System.out.println(THICK_SEP);
		System.out.println("House current state:");
		System.out.println(String.format("%-7s%-4s %-16s %-10s %s",
				"Type", "Num", "Name", "State", "Params"));
		
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

	public static House createHouseFromInit(Message m) {
		House house = new House();
		
		byte[] b = m.bytes();
		int index = 1;
		
		for (byte deviceType = 0; deviceType < 5; deviceType++) {
			int deviceCount = b[index++];
			for (byte deviceNum = 0; deviceNum < deviceCount; deviceNum++) {
				int maxParms = DeviceType.typeFromCodeSafe(deviceType)
						.maxParms();
				byte[] d = new byte[17+maxParms];
				for (int k = 0; k < d.length; k++) {
					d[k] = b[index++];
				}
				Device device = Device.createDeviceFromBytes(
						DeviceType.typeFromCodeSafe(deviceType), deviceNum, d);
				house.addDevice(device);
			}
		}
		
		return house;
	}

	public Message createActionMessage(byte t, byte n, byte s, byte[] parms) {
		try {			
			return devices.get(t).get(n).getActionMessage(sequenceNumber++, s, parms);
		} catch (Exception e) {
			e.printStackTrace();
			return Message.ERROR_GENERAL;
		}
	}

	public void doUpdate(Update update) {
		// TODO IMPLEMENT doUPDATE();
	}
	
	// getters
	
	/**
	 * @return all devices in the house.
	 */
	public List<List<Device>> devices() {
		return devices;
	}
	
}
