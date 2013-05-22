package devices;

import java.util.ArrayList;
import java.util.List;

import protocol.Message;

import common.Util;

public class House {
	
	// fields
	// list of devices available in the house
	
	private List<List<Device>> devices = new ArrayList<List<Device>>();
		
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
	
	public Device addDevice(Device d) {
		List<Device> l = devices.get(d.deviceType());
		d.setDeviceNumber(l.size());
		l.add(d);
		return d;
	}
	
	/**
	 * Applies the given action on the respective device.
	 * @param action
	 */
	public void doAction(Action action) throws Exception {
		devices.get(action.deviceType()).get(action.deviceNumber())
				.doAction(action);
	}
	
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
	
}
