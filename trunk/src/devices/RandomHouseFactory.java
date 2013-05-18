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
			Light d = new Light("light_" + i, i, s);
			try {
				d.dim((byte) rand.nextInt(256));
			} catch (Exception e) {}
			house.addDevice(d);
		}
		// add shades
		int numShades = 1 + rand.nextInt(MAX_DEVICES_PER_TYPE);
		for (int i = 0; i < numShades; i++) {
			ShadeState s = (rand.nextInt(2) == 0 ? ShadeState.UP
					: ShadeState.DOWN);
			Shade d = new Shade("shade_" + i, i, s);
			try {
				d.dim((byte) rand.nextInt(256));
			} catch (Exception e) {}
			house.addDevice(d);
		}
		// add AirCons
		int numAirCons = 1 + rand.nextInt(MAX_DEVICES_PER_TYPE);
		for (int i = 0; i < numAirCons; i++) {
			AirConState s = (rand.nextInt(2) == 0 ? AirConState.OFF
					: AirConState.ON);
			AirCon d = new AirCon("aircon_" + i, i, s);
			try {
				d.setTemp((byte) rand.nextInt(256));
			} catch (Exception e) {}
			house.addDevice(d);
		}
		// add TVs
		int numTVs = 1 + rand.nextInt(MAX_DEVICES_PER_TYPE);
		for (int i = 0; i < numTVs; i++) {
			TVState s = (rand.nextInt(2) == 0 ? TVState.OFF
					: TVState.ON);
			TV d = new TV("tv_" + i, i, s);
			try {
				d.setChannel((byte) rand.nextInt(256));
				d.setVolume((byte) rand.nextInt(256));
			} catch (Exception e) {}
			house.addDevice(d);
		}
		// add alarms
		int numAlarms = 1 + rand.nextInt(MAX_DEVICES_PER_TYPE);
		for (int i = 0; i < numAlarms; i++) {
			int stateInt = rand.nextInt(3);
			AlarmState s = AlarmState.OFF;
			switch (stateInt) {
			case 1:
				s = AlarmState.ON;
				break;
			case 2:
				s = AlarmState.ARMED;
				break;
			}
			Alarm d = new Alarm("alarm_" + i, i, s);
			house.addDevice(d);
		}
		
		return house;
	}
}
