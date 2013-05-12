package devices;

import java.util.Random;

public class RandomHouseFactory implements HouseFactory {
	private final Random R;
	
	public RandomHouseFactory(int seed) {
		this.R = new Random(seed);
	}
	
	public RandomHouseFactory() {
		this.R = new Random(System.currentTimeMillis());
	}
	
	public House createHouse() {
		House H = new House();
		int numLights = 1 + R.nextInt(4);
		for (int i = 0; i < numLights; i++) {
			LightState ls = (R.nextInt(2) == 0 ? LightState.OFF : LightState.ON);
			H.addDevice(new Light("light_" + i, ls));
		}
		
		return H;
	}
	
	
}
