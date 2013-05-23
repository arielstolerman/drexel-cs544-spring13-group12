package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import protocol.DFA;
import protocol.Message;

import common.Util;

public class ClientCLI implements ClientComm {

	private String host;
	private int port;
	private String user;
	private String pass;
	private DFA dfa;
	private boolean shutdown = false;
	
	public ClientCLI(String host, int port, String user, String pass) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.dfa = new DFA();
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
			
			write(dfa.clientProcess(null), bw); //POKE
			
			while (true) {
				String line = read(socket, br);
				if (line == null) return;
				
				Message inMsg = Message.fromHexString(line);
				inMsg.prettyPrint("S");
				Message outMsg = dfa.clientProcess(inMsg);
				
				if (outMsg == null) {
					throw new RuntimeException("Invalid Out Message.");
				} else {
					write(outMsg, bw);
				}					
			}
				
		} catch (Exception e) {
			e.printStackTrace();
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
}