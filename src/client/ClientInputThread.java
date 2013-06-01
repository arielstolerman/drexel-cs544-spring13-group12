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
 * File name: ClientInputThread.java
 * 
 * Purpose:
 * Process and handle action input from the user, to be posted as action to send
 * the server for processing. Presents the user only valid options - selecting
 * device type, number, action and parameters, or shutting down the connection.
 * User input is handled in a separate thread in order to allow update messages
 * to be accepted from server at the same time as the user inputs commands.
 * 
 * Relevant requirements (details in the file):
 * - CLIENT
 * - UI
 * 
 * =============================================================================
 */

package client;

import java.io.*;
import java.util.*;

import protocol.Message;

import devices.*;

public class ClientInputThread extends Thread {
	
	/**
	 * Client communication handler that uses this client input thread
	 */
	private ClientComm clientComm;
	/**
	 * The house image on the client side on which all actions are to be
	 * performed
	 */
	private House house;
	/**
	 * A flag that indicates whether the user input read should be terminated,
	 * to be used when a message is received from the server while reading user
	 * input
	 */
	private volatile boolean killInput = false;
	
	// constructors
	
	/**
	 * Constructs a new client input thread, attached to the client communication
	 * handler and the house image at the client side.
	 * @param clientComm the client communication handler.
	 * @param house the house.
	 */
	public ClientInputThread(ClientComm clientComm, House house) {
		this.clientComm = clientComm;
		this.house = house;
	}
	
	/*
	 * UI
	 * The main run method of the client input thread handles user I/O.
	 * The user is presented with valid options, and the thread collects the
	 * user selections and in charge of posting the constructed action to the
	 * communication handler.
	 */
	
	@Override
	public void run() {
		try {
			// initialize local variables for handling user input
			boolean legalInput = false;
			byte legalMin, legalMax;
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
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
				// process shutdown
				if (input.trim().equalsIgnoreCase("s")) {
					clientComm.postAction(Message.SHUTDOWN);
					return;
				}
				try {
					// check legal range
					byte code = Byte.parseByte(input);
					if (code < legalMin || code > legalMax)
						throw new Exception("selected device code not in range");
					selectedType = DeviceType.typeFromCode(code);
					// check legal type selected
					if (house.devices().get(selectedType.type()).isEmpty())
						throw new Exception("no devices of selected type");
				} catch (Exception e) {
					if (killInput) return;
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
					// process shutdown
					if (input.trim().equalsIgnoreCase("s")) {
						clientComm.postAction(Message.SHUTDOWN);
						return;
					}
					// check selected device number is in range
					selectedDeviceIndex = Byte.parseByte(input);
					if (selectedDeviceIndex < 0
							|| selectedDeviceIndex >= selectedDevices.size()) {
						throw new Exception("selected device number not in range");
					}
				} catch (Exception e) {
					if (killInput) return;
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
					// process shutdown
					if (input.trim().equalsIgnoreCase("s")) {
						clientComm.postAction(Message.SHUTDOWN);
						return;
					}
					// check opcode in range
					selectedOpcode = Byte.parseByte(input);
					if (selectedOpcode < 0 || selectedOpcode >= opCodesMap.size()) {
						throw new Exception("selected opcode not in range");
					}
				} catch (Exception e) {
					if (killInput) return;
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
			
			// no parameters
			if (paramNames == null) {
				System.out.println("No parameters for operation: "
						+ selectedOpcode);
				params = new byte[]{};
			}
			// expected parameters - process
			else {
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
						// process shutdown
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
						if (killInput) return;
						System.out.println("Illegal selection, try again: "
								+ e.getMessage());
						continue;
					}
					// mark input is legal
					legalInput = true;
					if (killInput) return;
				}
			}
			
			// finally, post action to be processed by client communication
			// handler
			clientComm.postAction(house.createActionMessage(
					selectedType.type(),
					selectedDeviceIndex,
					selectedOpcode,
					params));
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * Mark to kill the user input thread. Called by the communication handler
	 * whenever a message is received by the server during user input collection. 
	 */
	public void killInput() {
		this.killInput = true;
	}
}
