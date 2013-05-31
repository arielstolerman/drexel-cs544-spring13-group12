package client;

import java.io.*;
import java.net.*;

import protocol.*;

import common.Util;

public class ClientCommTester implements ClientComm {
	
	// fields
	
	private String host;
	private int port;
	private BufferedReader userInputReader;
	
	// constructors
	
	/**
	 * Constructor for a client communication tester with CLI to process user
	 * input.
	 * @param host the host to connect to.
	 * @param port the port to connect to.
	 */
	public ClientCommTester(String host, int port) {
		this.host = host;
		this.port = port;
		userInputReader = new BufferedReader(new InputStreamReader(System.in));
	}
	
	// methods
	
	@Override
	public void run() {
		boolean connect = true;
		while (connect) {
			try {
				Socket socket = new Socket(host, port);
				socket.setSoTimeout(Client.LISTEN_TIMEOUT_MS);

				System.out.println(Util.dateTime() + " -- TEST CLIENT CONNECTED\n");

				BufferedReader br = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				BufferedWriter bw = new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream()));

				Message m;

				// collect user input, send to server and process response
				while (true) {
					// read message from user and send to server
					m = getUserInputMessage();

					// send message to server
					System.out.println(">>> sending message to server:");
					write(m, bw);

					// shutdown if necessary
					if (m.opcode() == Message.OP_SHUTDOWN) {
						connect = false;
						break;
					}

					// read message from server
					System.out.println(">>> reading response from server:");
					String line = read(socket, br);
					if (line == null) return;
					m = Message.fromHexString(line);
					m.prettyPrint("S");
					
					if (m.opcode() == Message.OP_SHUTDOWN ||
							m.opcode() == Message.OP_ERROR) {
						break;
					}
					if (m.opcode() == Message.OP_CHALLENGE) {
						System.out.println(">>> valid response message would be:");
						System.out.println(Util.toHexString(
								new Message(DESAuth.genUserSemiResponse(
										"ariel",
										"ariel123",
										m.contentBytes()),
										Message.OP_RESPONSE).bytes()));
					}
				}

				// shutdown
				socket.close();
				System.out.println(Util.dateTime() + " -- TEST CLIENT DISCONNECTED");
				
				if (connect) {
					System.out.println();
					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" +
							"~~~~~~~~~~~~~~~~~~");
					System.out.println("shutdown / error received from server");
					System.out.println("reconnecting");
					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" +
							"~~~~~~~~~~~~~~~~~~");
					System.out.println();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Utility method to read user input byte stream and parse it into a message
	 * to be sent to the server.
	 */
	private Message getUserInputMessage() throws Exception {
		String input;
		byte[] b;
		Message m = null;
		while (m == null) {
			System.out.println("Enter message in hex bytes to send the server, e.g: '01 A6 8B'");
			input = userInputReader.readLine();
			input = input.replaceAll("\\s+"," ").trim();
			try {
				b = Util.toByteStream(input);
			} catch (NumberFormatException e) {
				System.out.println("invalid input, try again");
				continue;
			}
			m = new Message(b);
		}
		return m;
	}
	
	/**
	 * Utility method to read from the input buffer.
	 */
	private String read(Socket socket, BufferedReader br) throws IOException {
		String line = null;
		while (line == null) {
			try {
				line = br.readLine();
				if (line == null) {
					socket.close();
					return null;
				}
			} catch (SocketException e) {
				System.err.println("socket exception thrown:");
				e.printStackTrace();
				return null;
			}
		}
		return line;
	}

	/**
	 * Utility method to write to the output buffer.
	 */
	private void write(Message m, BufferedWriter bw) throws IOException {
		m.prettyPrint("C");
		m.write(bw);
	}
	
	// getters
	
	public String host() {
		return host;
	}
	
	public int port() {
		return port;
	}
		
	// overriding
	
	@Override
	public void postAction(Message actionMessage) {}
	
	@Override
	public Message getPostedActionAndReset() { return null; }
	
	@Override
	public void killInput() {}
	
	// run Client in test mode
	public static void main(String[] args) {
		Client.main(new String[]{"-test"});
	}
}