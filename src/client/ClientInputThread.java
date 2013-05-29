package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import devices.Device;
import devices.DeviceType;
import devices.House;

public class ClientInputThread extends Thread {
	
	private ClientComm clientComm;
	private House house;
	private volatile boolean killInput = false;
	
	public ClientInputThread(ClientComm clientComm, House house) {
		this.clientComm = clientComm;
		this.house = house;
	}
	
	public void run() {
		try {
			boolean legalInput = false;
			byte legalMin, legalMax;
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input = null;
			String msg;
			
			// get device type
			// ---------------
			// set legal configuration
			DeviceType[] types = DeviceType.values();
			legalMin = types[0].type();
			legalMax = types[types.length - 1].type();
			DeviceType selectedType = null;
			// set message for user
			msg = "Select device type:\n";
			for (DeviceType type: types)
				msg += "[" + type.type() + "] " + type + "  ";
			// read until legal
			while (!legalInput) {
				System.out.println(msg);
				input = br.readLine();
				try {
					byte code = Byte.parseByte(input);
					if (code < legalMin || code > legalMax)
						throw new Exception();
					selectedType = DeviceType.typeFromCode(code);
				} catch (Exception e) {
					System.err.println("Illegal selection, try again");
					continue;
				}
				// mark input is legal
				legalInput = true;
				if (killInput) return;
			}
			// reset
			input = null;
			legalInput = false;
			
			// selected devices
			List<Device> selectedDevices = house.devices().get(
					selectedType.type());
			Set<Byte> selectedDeviceNums = new HashSet<>();
			for (Device d: selectedDevices)
				selectedDeviceNums.add(d.deviceNumber());
			
			// get device number
			// -----------------
			int selectedDevice = -1;
			// set message for user
			msg = "Select device:";
			for (Device d: selectedDevices)
				msg += "\n[" + d.deviceNumber() + "] " + d.name().trim();
			// read user input until legal
			while (!legalInput) {
				System.out.println(msg);
				try {
					input = br.readLine();
					selectedDevice = Byte.parseByte(input);
					if (!selectedDeviceNums.contains(selectedDevice))
						throw new Exception();
				} catch (Exception e) {
					System.err.println("Illegal selection, try again");
					continue;
				}
				// mark input is legal
				legalInput = true;
				if (killInput) return;
			}
			// reset
			input = null;
			legalInput = false;
			
			// operation
			// ---------
			
			System.out.println("What operation?");
			selectedType.printOpCode();
			String opcode = br.readLine();
			if (killInput) return;
			
			byte opcode_b = Byte.parseByte(opcode);
			
			String parameters = null;
			if (selectedType.parmCount(opcode_b) > 0) {
				System.out.println("What parameters?");
				selectedType.printParms(opcode_b);
				parameters = br.readLine();				
			}
			
			if (killInput) return;
			
			postAction(selectedType, selectedDevice + "", opcode_b, parameters);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	private void postAction(DeviceType deviceType, String deviceNum, byte opcode, String parameters) {
		byte n = (byte) Byte.parseByte(deviceNum);
		
		String[] parms = new String[0];
		
		if (parameters != null)
			parms = parameters.split(" ");
		
		if (parms.length != deviceType.parmCount(opcode)) {
			throw new RuntimeException("Invalid number of parameters, expected: " + deviceType.parmCount(opcode) + " got: " + parms.length);
		}
		
		byte[] p = new byte[parms.length];
		
		for (int i = 0; i < parms.length; i++) {
			p[i] = Byte.parseByte(parms[i]);
		}
		
		clientComm.postAction(house.createActionMessage(deviceType.type(), n, opcode, p));
	}
	
	public void killInput() {
		this.killInput = true;		
	}
}
