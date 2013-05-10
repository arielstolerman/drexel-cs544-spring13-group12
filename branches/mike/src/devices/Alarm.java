package devices;

public class Alarm extends Device {

	public Alarm(String name, int deviceNumber) {
		super(name, deviceNumber);
	}

	public byte deviceType() {
		return 5;
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
