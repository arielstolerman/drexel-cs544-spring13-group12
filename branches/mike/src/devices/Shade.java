package devices;

public class Shade extends Device {

	public Shade(String name, int deviceNumber) {
		super(name, deviceNumber);
	}
	
	public byte deviceType() {
		return 2;
	}
	
	public boolean doAction(Action A) {
		return false;
	}

}

/**
 * Enumeration of Shade states.
 */
enum ShadeState {
	UP,
	DOWN
}
