/* =============================================================================
 * CS544 - Computer Networks
 * Drexel University, Spring 2013
 * Protocol Implementation: Remote Smart House Control
 * Group 12:
 * - Ryan Corcoran
 * - Amber Heilman
 * - Michael Mersic
 * - Ariel Stolerman
 * 
 * -----------------------------------------------------------------------------
 * File name: RandomHouseFactory.java
 * 
 * Purpose:
 * Implements HouseFactory and provides functionality for generating a random
 * house with devices. Used for demonstration and testing purposes.
 * 
 * Relevant requirements:
 * - SERVICE - house generation and representation are part of the functionality
 *   required by the protocol implementation.
 * 
 * =============================================================================
 */

package devices;

import java.util.Random;

public class RandomHouseFactory implements HouseFactory {
	
	// fields
	
	/**
	 * Random room names for device name generation
	 */
	private static final String[] ROOMS = new String[]{
		"bedroom",
		"kitchen",
		"bathroom",
		"dining",
		"living",
		"patio",
		"closet",
		"hallway",
		"basement",
		"laundry"
	};
	
	/**
	 * Maximum number of devices to allow per device type
	 */
	private int maxDevicesPerType = 5;
	/**
	 * Random number generator
	 */
	private final Random rand;
	
	// constructors
	
	/**
	 * Default constructor for random house factory, using the current time as
	 * random seed.
	 */
	public RandomHouseFactory() {
		this.rand = new Random(System.currentTimeMillis());
	}
	
	/**
	 * Constructs a new random house factory from the given seed.
	 */
	public RandomHouseFactory(long seed) {
		this.rand = new Random(seed);
	}
	
	/**
	 * Constructs a new random house factory from the given seed and number of
	 * devices limit.
	 */
	public RandomHouseFactory(long seed, int maxDevicesPerType) {
		this(seed);
		this.maxDevicesPerType = maxDevicesPerType;
	}
	
	// interface methods
	
	/**
	 * @return a randomly generated house.
	 */
	public House createHouse() {
		// initialize an empty house
		House house = new House();
		
		// add lights
		int numLights = 1 + rand.nextInt(maxDevicesPerType);
		for (int i = 0; i < numLights; i++) {
			LightState s = (rand.nextInt(2) == 0 ? LightState.OFF : LightState.ON);
			Light d = new Light(room() + " light", (byte) i, s);
			try {
				d.dim((byte) rand.nextInt(256));
			} catch (Exception e) {}
			house.addDevice(d);
		}
		// add shades
		int numShades = 1 + rand.nextInt(maxDevicesPerType);
		for (int i = 0; i < numShades; i++) {
			ShadeState s = (rand.nextInt(2) == 0 ? ShadeState.UP
					: ShadeState.DOWN);
			Shade d = new Shade(room() + " shade", (byte) i, s);
			try {
				d.dim((byte) rand.nextInt(256));
			} catch (Exception e) {}
			house.addDevice(d);
		}
		// add AirCons
		int numAirCons = 1 + rand.nextInt(maxDevicesPerType);
		for (int i = 0; i < numAirCons; i++) {
			AirConState s = (rand.nextInt(2) == 0 ? AirConState.OFF
					: AirConState.ON);
			AirCon d = new AirCon(room() + " aircon", (byte) i, s);
			try {
				d.setTemp((byte) rand.nextInt(256));
			} catch (Exception e) {}
			house.addDevice(d);
		}
		// add TVs
		int numTVs = 1 + rand.nextInt(maxDevicesPerType);
		for (int i = 0; i < numTVs; i++) {
			TVState s = (rand.nextInt(2) == 0 ? TVState.OFF
					: TVState.ON);
			TV d = new TV(room() + " tv", (byte) i, s);
			try {
				d.setChannel((byte) rand.nextInt(256));
				d.setVolume((byte) rand.nextInt(256));
			} catch (Exception e) {}
			house.addDevice(d);
		}
		// add alarms
		int numAlarms = 1 + rand.nextInt(maxDevicesPerType);
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
			Alarm d = new Alarm(room() + " alarm", (byte) i, s);
			house.addDevice(d);
		}
		
		return house;
	}
	
	/**
	 * @return a randomly chosen room, using the random generator.
	 */
	private String room() {
		return ROOMS[rand.nextInt(ROOMS.length)];
	}
	
	/**
	 * Main method for testing purposes
	 * @param args
	 */
	public static void main(String[] args) {
		House h = new RandomHouseFactory().createHouse();
		h.prettyPrint();
	}
}
