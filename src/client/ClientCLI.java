package client;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

import protocol.*;

import common.Util;

public class ClientCLI implements ClientComm {

	private static final String POSTED_MESSAGE = "POSTED_MESSAGE";
	private String host;
	private int port;
	private String user;
	private String pass;
	private DFA dfa;
	private boolean shutdown = false;
	private ClientInputThread clientInputThread;
	private Message postedMessage;
	
	public ClientCLI(String host, int port, String user, String pass) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.dfa = new ClientDFA(this, user, pass);
	}
	
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
			
			write(dfa.process(null), bw); //POKE
			
			while (true) {
				String line = read(socket, br);
				if (line == null) return;

				
				Message inMsg = null;
				if (line == POSTED_MESSAGE) {
					inMsg = postedMessage;
					postedMessage = null;
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

	private void createClientInputThread() {
		if (this.clientInputThread == null) {
			this.clientInputThread = new ClientInputThread(this, this.dfa.house());
			this.clientInputThread.start();
		}		
	}
	
	

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
				if (this.postedMessage != null) {
					return POSTED_MESSAGE;
				}
			}
		}
		return line;
	}

	private void write(Message M, BufferedWriter bw) throws IOException {
		M.prettyPrint("C");
		M.write(bw);
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
	
	public Message postedActionMessage() {
		return postedMessage;
	}

	public void postAction(Message actionMessage) {
		this.postedMessage = actionMessage;
	}
	
	public void killInput() {
		this.clientInputThread.killInput();
	}
}