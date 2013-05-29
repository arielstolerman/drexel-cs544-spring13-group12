package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import devices.DeviceType;
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
			
			DeviceType dT = DeviceType.typeFromCode(Byte.parseByte(deviceType));
			
			if (dT == DeviceType.NO_SUCH_DEVICE) {
				throw new RuntimeException("Invalid device type.");
			}			
			
			System.out.println("Modify which device Num?");
			String deviceNum = br.readLine();
			if (killInput) return;
			
			System.out.println("What state?");
			String state = br.readLine();
			if (killInput) return;
			
			String parameters = null;
			if (dT.parmCount() > 0) {
				System.out.println("What parameters?");
				parameters = br.readLine();				
			}
			
			if (killInput) return;
			
			postAction(dT, deviceNum, state, parameters);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	private void postAction(DeviceType deviceType, String deviceNum, String state, String parameters) {
		byte n = (byte) Byte.parseByte(deviceNum);
		byte s = (byte) Byte.parseByte(state);
		
		String[] parms = parameters.split(" ");
		
		if (parms.length != deviceType.parmCount()) {
			throw new RuntimeException("Invalid number of parameters, expected: " + deviceType.parmCount() + " got: " + parms.length);
		}
		
		byte[] p = new byte[parms.length];
		
		for (int i = 0; i < parms.length; i++) {
			p[i] = Byte.parseByte(parms[i]);
		}
		
		clientComm.postAction(house.createActionMessage(deviceType.type(), n, s, p));
	}
	
	public void killInput() {
		this.killInput = true;		
	}
}
