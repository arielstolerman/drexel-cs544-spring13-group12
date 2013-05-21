package protocol;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class ServerComm implements Runnable {
	private ConnectionListener connectionListener;
	private boolean shutdown = false;
	private Socket socket;
	private DFA dfa;
	
	private List<byte[]> sendList = new ArrayList<byte[]>();

	public ServerComm(ConnectionListener cl, Socket s, DFA dfa) {
		this.connectionListener = cl;
		this.socket = s;
		this.dfa = dfa;
		dfa.setServerComm(this);
	}
	
	public void run() {
		try {
			OutputStream os = this.socket.getOutputStream();
			InputStream  is = this.socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
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
						while (!sendList.isEmpty()) {
							byte[] toSend = sendList.remove(0);
							os.write(toSend);
							os.write("\n".getBytes());
							os.flush();
						}
						
						if (shutdown) {
							//TODO check if in the meanwhile house state is changed
							// send message to client accordingly
							socket.close();
							return;
						}
					}
				}
				System.out.println("Server recieved: " + line);
				Message msg = new Message(line);
				Message response = dfa.process(msg);
				
				if (response == Message.SHUTDOWN) {
					this.shutdown = true;
					connectionListener.remove(this);
					socket.close();
					return;
				}
				String s = new String(response.bytes());
				System.out.println("Server sending to client: " + response + " as string: " + s);
				
				os.write(response.bytes());
				os.flush();
			}
		} catch (Exception e) {
			connectionListener.remove(this);
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		this.shutdown = true;
	}

	public void appendToSendList(byte[] b) {
		sendList.add(b);
	}
}
