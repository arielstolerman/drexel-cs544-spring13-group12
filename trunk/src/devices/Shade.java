package devices;

public class Shade extends Device {

	public Shade(String name, int deviceNumber) {
		super(name);
	}
	
	public byte deviceType() {
		return 1;
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
