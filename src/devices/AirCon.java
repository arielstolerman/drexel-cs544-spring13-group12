package devices;

public class AirCon extends Device {

	public AirCon(String name) {
		super(name);
	}
	
	public byte deviceType() {
		return 2;
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
