package devices;

import java.util.Random;

public class RandomHouseFactory implements HouseFactory {
	
	private static final int MAX_DEVICES_PER_TYPE = 5;
	
	private final Random rand;
	
	public RandomHouseFactory(long seed) {
		this.rand = new Random(seed);
	}
	
	public RandomHouseFactory() {
		this.rand = new Random(System.currentTimeMillis());
	}
	
	public House createHouse() {
		House house = new House();
		// add lights
		int numLights = 1 + rand.nextInt(MAX_DEVICES_PER_TYPE);
		for (int i = 0; i < numLights; i++) {
			LightState s = (rand.nextInt(2) == 0 ? LightState.OFF : LightState.ON);
			Light l = new Light("light_" + i, i, s);
			try {
				l.dim((byte) rand.nextInt(256));
			} catch (Exception e) {}
			house.addDevice(l);
		}
		// add shades
		
		// add AirCons
		
		// add TVs
		
		// add alarms
		
		return house;
	}
}
