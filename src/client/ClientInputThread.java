package client;

import java.io.*;
import java.util.*;

import protocol.Message;

import devices.*;

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
			msg = "Select device type or press S to shutdown:\n";
			for (DeviceType type: types)
				msg += "[" + type.type() + "] " + type + "  ";
			// read until legal
			while (!legalInput) {
				System.out.println(msg);
				input = br.readLine();
				if (input.trim().equalsIgnoreCase("s")) {
					clientComm.postAction(Message.SHUTDOWN);
					return;
				}
				try {
					byte code = Byte.parseByte(input);
					if (code < legalMin || code > legalMax)
						throw new Exception("selected device code not in range");
					selectedType = DeviceType.typeFromCode(code);
					if (house.devices().get(selectedType.type()).isEmpty())
						throw new Exception("no devices of selected type");
				} catch (Exception e) {
					System.out.println("Illegal selection, try again: " +
							e.getMessage());
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
			
			// get device number
			// -----------------
			byte selectedDeviceIndex = -1;
			// set message for user
			msg = "Select device or press S to shutdown:";
			for (Device d: selectedDevices)
				msg += "\n[" + d.deviceNumber() + "] " + d.name().trim();
			// read user input until legal
			while (!legalInput) {
				System.out.println(msg);
				try {
					input = br.readLine();
					if (input.trim().equalsIgnoreCase("s")) {
						clientComm.postAction(Message.SHUTDOWN);
						return;
					}
					selectedDeviceIndex = Byte.parseByte(input);
					if (selectedDeviceIndex < 0
							|| selectedDeviceIndex >= selectedDevices.size()) {
						throw new Exception("selected device number not in range");
					}
				} catch (Exception e) {
					System.out.println("Illegal selection, try again: " +
							e.getMessage());
					continue;
				}
				// mark input is legal
				legalInput = true;
				if (killInput) return;
			}
			// reset
			input = null;
			legalInput = false;
			
			// selected device
			Device selectedDevice = selectedDevices.get(selectedDeviceIndex);
			System.out.println("selected device: " + selectedDevice.toPrettyString());
			
			// operation
			// ---------
			byte selectedOpcode = -1;
			// set message for user
			msg = "Select operation or press S to shutdown:";
			Map<Byte,String> opCodesMap = selectedDevice.opCodesMap();
			for (byte key: opCodesMap.keySet())
				msg += "\n[" + key + "] " + opCodesMap.get(key);
			// read user input until legal
			while (!legalInput) {
				System.out.println(msg);
				try {
					input = br.readLine();
					if (input.trim().equalsIgnoreCase("s")) {
						clientComm.postAction(Message.SHUTDOWN);
						return;
					}
					selectedOpcode = Byte.parseByte(input);
					if (selectedOpcode < 0 || selectedOpcode >= opCodesMap.size()) {
						throw new Exception("selected opcode not in range");
					}
				} catch (Exception e) {
					System.out.println("Illegal selection, try again: " +
							e.getMessage());
					continue;
				}
				// mark input is legal
				legalInput = true;
				if (killInput) return;
			}
			// reset
			input = null;
			legalInput = false;
			
			// operation parameters
			// --------------------
			String[] paramNames = selectedDevice.opCodesParamMap().get(
					selectedOpcode);
			byte[] params;
			
			if (paramNames == null) {
				System.out.println("No parameters for operation: "
						+ selectedOpcode);
				params = new byte[]{};
			} else {
				params = new byte[paramNames.length];
				String[] inputArr;
				// set message for user
				msg = "Input " +
						Arrays.toString(paramNames).replace("[", "").replace("]", "") +
						(params.length > 1 ? " (separated by commas)" : "") +
						" or press S to shutdown:";
				// read user input until legal
				while (!legalInput) {
					System.out.println(msg);
					try {
						inputArr = br.readLine().split(",");
						if (inputArr[0].trim().equalsIgnoreCase("s")) {
							clientComm.postAction(Message.SHUTDOWN);
							return;
						}
						// check number of parameters
						if (inputArr.length != paramNames.length)
							throw new Exception(
									"unexpected number of parameters");
						// parse and check parameters
						for (int i = 0; i < inputArr.length; i++) {
							params[i] = Byte.parseByte(inputArr[i].trim());
						}
					} catch (Exception e) {
						System.out.println("Illegal selection, try again: "
								+ e.getMessage());
						continue;
					}
					// mark input is legal
					legalInput = true;
					if (killInput) return;
				}
			}
			
			// finally, post action 
			clientComm.postAction(house.createActionMessage(
					selectedType.type(),
					selectedDeviceIndex,
					selectedOpcode,
					params));
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public void killInput() {
		this.killInput = true;		
	}
}
