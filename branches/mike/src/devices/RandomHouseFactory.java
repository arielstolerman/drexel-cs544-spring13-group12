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
		int numLights = R.nextInt(5);
		for (int i = 0; i < numLights; i++) {
			Device d = new Light("light_" + i);
			H.addDevice(d);
		}
		
		return H;
	}
	
	
}
