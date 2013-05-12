package devices;

import java.util.ArrayList;
import java.util.List;

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
	
	public boolean doAction(Action A) {
		return devices.get(A.getDeviceType()).get(A.getDeviceNumber()).doAction(A);
	}
	
	public String getINIT() {
		StringBuffer sb = new StringBuffer(1000);
		for (List<Device> L : devices) {
			sb.append(L.size());
			for (Device D : L) {
				sb.append(D.toString());
			}
		}
		return sb.toString();
	}
}








