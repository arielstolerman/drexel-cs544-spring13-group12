package devices;

import java.util.ArrayList;
import java.util.List;

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
		List<Byte> B = new ArrayList<Byte>();
		B.add((byte)3);
		for (List<Device> L : devices) {			
			B.add((byte)L.size());
			for (Device D : L) {
				B.addAll(Util.toByteArray(D.getBytes()));
			}
		}
		B.addAll(Util.toByteArray("\n".getBytes()));
		
		byte b[] = new byte[B.size()];
		for (int i = 0; i < b.length; i++) {
			b[i] = B.get(i);
		}
		
		return b;
	}
	
}








