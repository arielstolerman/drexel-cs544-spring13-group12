package devices;

public class Light extends Device {
	
	public Light(String name, int deviceNumber) {
		super(name, deviceNumber);
	}

}

/**
 * Enumeration of Light states.
 */
enum LightState {
	OFF,
	ON
}
