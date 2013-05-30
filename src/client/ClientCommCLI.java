package client;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

import protocol.*;

import common.Util;

public class ClientCommCLI implements ClientComm {

	// indicator to flag the client to process user input
	private static final String POSTED_MESSAGE = "POSTED_MESSAGE";
	
	// fields
	
	private String host;
	private int port;
	private String user;
	private String pass;
	private DFA dfa;
	private volatile boolean shutdown = false;
	private ClientInputThread clientInputThread;
	private Message postedAction;
	
	// constructors
	
	/**
	 * Constructor for a client communication handler with a CLI for processing
	 * client input.
	 * @param host the host to connect to.
	 * @param port the port to connect to.
	 * @param user client username.
	 * @param pass client password.
	 */
	public ClientCommCLI(String host, int port, String user, String pass) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.dfa = new ClientDFA(this, user, pass);
	}
	
	// methods
	
	@Override
	public void run() {
		try {
			Socket socket = new Socket(host, port);
			socket.setSoTimeout(Client.LISTEN_TIMEOUT_MS);

			System.out.println(Util.dateTime() + " -- Client " + user
					+ " connected\n");
			
			BufferedReader br = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));
			
			// poke
			write(dfa.process(Message.INTERNAL), bw);
			
			while (true) {
				// read next message from input
				String line = read(socket, br);
				if (line == null) return;
				
				Message inMsg = null;
				if (line == POSTED_MESSAGE) {
					inMsg = postedAction;
					postedAction = null;
				} else {
					inMsg = Message.fromHexString(line);
				}
				inMsg.prettyPrint("S");
				Message outMsg = dfa.process(inMsg);
				
				if (outMsg == null) {
					throw new RuntimeException("Invalid Out Message.");
				} else if (outMsg == Message.AWAITING_CLIENT_INPUT) {
					createClientInputThread();
				} else {
					write(outMsg, bw);
				}					
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the client input thread, that runs in parallel to the client
	 * listening on server updates.
	 */
	private void createClientInputThread() {
		if (this.clientInputThread == null) {
			this.clientInputThread = new ClientInputThread(this, this.dfa.house());
			this.clientInputThread.start();
		}		
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
			} catch (SocketTimeoutException ste) {
				if (shutdown) {
					socket.close();
					return null;
				}
				if (this.postedAction != null) {
					return POSTED_MESSAGE;
				}
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
	
	public String user() {
		return user;
	}
	
	public String pass() {
		return pass;
	}
	
	// overriding
	
	public void postAction(Message actionMessage) {
		this.postedAction = actionMessage;
	}
	
	public Message getPostedAction() {
		return postedAction;
	}
	
	public void killInput() {
		this.clientInputThread.killInput();
	}
}