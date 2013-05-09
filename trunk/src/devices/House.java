package devices;

import java.util.*;

public class House {
	
	// fields
	// list of devices available in the house
	
	private List<Light> lights;
	private List<Shade> shades;
	private List<AirCon> airCons;
	private List<TV> tvs;
	private List<Alarm> alarms;
	
	// constructors
	
	/**
	 * constructs a new house with no devices.
	 */
	public House() {
		lights = new ArrayList<>();
		shades = new ArrayList<>();
		airCons = new ArrayList<>();
		tvs = new ArrayList<>();
		alarms = new ArrayList<>();
	}
	
	// methods
	
	public Light addLight(String name) {
		Light l = new Light(name, lights.size());
		lights.add(l);
		return l;
	}
	
	public Shade addShade(String name) {
		Shade s = new Shade(name, shades.size());
		shades.add(s);
		return s;
	}
	
	public AirCon addAirCon(String name) {
		AirCon a = new AirCon(name, airCons.size());
		airCons.add(a);
		return a;
	}
	
	public TV addTV(String name) {
		TV t = new TV(name, tvs.size());
		tvs.add(t);
		return t;
	}
	
	public Alarm addAlarm(String name) {
		Alarm a = new Alarm(name, alarms.size());
		alarms.add(a);
		return a;
	}
	
	// TODO
	// - add getters
}








