package devices;

public class TV extends Device {

	public TV(String name, int deviceNumber) {
		super(name, deviceNumber);
	}
	
	public byte deviceType() {
		return 4;
	}

	public boolean doAction(Action A) {
		return false;
	}

}

/**
 * Enumeration of TV states.
 */
enum TVState {
	OFF,
	ON
}
