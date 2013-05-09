package devices;

public class Shade extends Device {

	public Shade(String name, int deviceNumber) {
		super(name, deviceNumber);
	}
}

/**
 * Enumeration of Shade states.
 */
enum ShadeState {
	UP,
	DOWN
}
