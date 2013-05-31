package client;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

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
				m.prettyPrint("TEST CLIENT");
				write(m, bw);
				
				// shutdown if necessary
				if (m.opcode() == Message.OP_SHUTDOWN)
					break;
				
				// read message from server
				System.out.println(">>> reading response from server:");
				String line = read(socket, br);
				if (line == null) return;
				m = Message.fromHexString(line);
				m.prettyPrint("SERVER");
			}
			
			// shutdown
			socket.close();
			System.out.println(Util.dateTime() + " -- TEST CLIENT DISCONNECTED");
			
		} catch (Exception e) {
			e.printStackTrace();
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
					//server closed connection
					socket.close();
					return null;
				}
			} catch (SocketTimeoutException ste) {}
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
}