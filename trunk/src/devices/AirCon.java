package devices;

public class AirCon extends Device {

	public AirCon(String name, int deviceNumber) {
		super(name, deviceNumber);
	}
}

/**
 * Enumeration of AirCon states.
 */
enum AirConState {
	OFF,
	ON
}
