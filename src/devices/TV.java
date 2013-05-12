package devices;

public class TV extends Device {

	public TV(String name) {
		super(name);
	}
	
	public byte deviceType() {
		return 3;
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
