package devices;

public class Alarm extends Device {

	public Alarm(String name, int deviceNumber) {
		super(name, deviceNumber);
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
