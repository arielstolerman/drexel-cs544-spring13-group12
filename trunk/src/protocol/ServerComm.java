package protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import devices.House;

public class ServerComm implements Runnable {
	private boolean shutdown = false;
	private Socket socket;
	private DFA dfa;

	public ServerComm(Socket s, DFA dfa) {
		this.socket = s;
		this.dfa = dfa;
	}
	
	public void run() {
		try {
			OutputStream os = this.socket.getOutputStream();
			InputStream  is = this.socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
			socket.setSoTimeout(1000);  //throw SocketTimeoutException every 1s if nothing to read.  Use this to check for shutdown.
			while (true) {
				String line = null;
				while (line == null) {
					try {
						line = br.readLine();
						if (line == null) {
							//client closed connection.
							socket.close();
							return;
						}
					} catch (SocketTimeoutException e) {
						if (shutdown) {
							socket.close();
							return;
						}
					}
				}
				Message msg = new Message(line);
				Message response = dfa.process(msg);
				bw.write(response.toString());
				bw.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		this.shutdown = true;
	}
}
