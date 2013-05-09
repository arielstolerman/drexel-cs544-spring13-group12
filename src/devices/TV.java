package devices;

public class TV extends Device {

	public TV(String name, int deviceNumber) {
		super(name, deviceNumber);
	}

}

/**
 * Enumeration of TV states.
 */
enum TVState {
	OFF,
	ON
}
