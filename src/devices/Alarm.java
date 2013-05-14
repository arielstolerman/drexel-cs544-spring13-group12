package devices;

public class Alarm extends Device {

	public Alarm(String name) {
		super(name);
	}

	public byte deviceType() {
		return 4;
	}
	
	public boolean doAction(Action A) {
		return false;
	}

}

/**
 * Enumeration of Alarm states.
 */
enum AlarmState {
	OFF,
	ON,
	ARMED
}
