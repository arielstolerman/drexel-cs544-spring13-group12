package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import devices.House;

public class ClientInputThread extends Thread {
	
	private ClientComm clientComm;
	private House house;
	private boolean killInput = false;
	
	public ClientInputThread(ClientComm clientComm, House house) {
		this.clientComm = clientComm;
		this.house = house;
	}
	
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Modify which device Type?");
			String deviceType = br.readLine();
			if (killInput) return;
			System.out.println("Modify which device Num?");
			String deviceNum = br.readLine();
			if (killInput) return;
			System.out.println("What state?");
			String state = br.readLine();
			if (killInput) return;
			postAction(deviceType, deviceNum, state);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	private void postAction(String deviceType, String deviceNum, String state) {
		byte t = (byte) Byte.parseByte(deviceType);
		byte n = (byte) Byte.parseByte(deviceNum);
		byte s = (byte) Byte.parseByte(state);
		clientComm.postAction(house.createActionMessage(t, n, s));
	}
	
	public void killInput() {
		this.killInput = true;		
	}
}
