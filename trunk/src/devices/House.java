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
	
	/**
	 * Applies the given action on the respective device.
	 * @param action
	 */
	public void doAction(Action action) throws Exception {
		devices.get(action.deviceType()).get(action.deviceNumber())
				.doAction(action);
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








