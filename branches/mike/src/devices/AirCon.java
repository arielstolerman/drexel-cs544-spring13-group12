package devices;

public class AirCon extends Device {

	public AirCon(String name, int deviceNumber) {
		super(name, deviceNumber);
	}
	
	public byte deviceType() {
		return 3;
	}
	
	public boolean doAction(Action A) {
		return false;
	}

}

/**
 * Enumeration of AirCon states.
 */
enum AirConState {
	OFF,
	ON
}
