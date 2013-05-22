package protocol;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ServerComm implements Runnable {
	
	private ConnectionListener connectionListener;
	private boolean shutdown = false;
	private Socket socket;
	private DFA dfa;
	
	private ConcurrentLinkedQueue<Message> sendQueue =
			new ConcurrentLinkedQueue<>();

	public ServerComm(ConnectionListener cl, Socket s, DFA dfa) {
		this.connectionListener = cl;
		this.socket = s;
		this.dfa = dfa;
		dfa.setServerComm(this);
	}
	
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			String inBuff;
			Message inMsg, outMsg;
			
			socket.setSoTimeout(Server.LISTEN_TIMEOUT_MS);  //throw SocketTimeoutException every 1s if nothing to read.  Use this to check for shutdown.
			while (true) {
				inBuff = null;
				inMsg = null;
				while (inBuff == null) {
					try {
						inBuff = br.readLine();
						if (inBuff == null) {
							//client closed connection.
							socket.close();
							return;
						}
						inMsg = Message.fromHexString(inBuff);
					} catch (SocketTimeoutException e) {
						while (!sendQueue.isEmpty()) {
							outMsg = sendQueue.remove();
							outMsg.prettyPrint("S");
							outMsg.write(bw);
						}
						if (shutdown) {
							socket.close();
							return;
						}
					}
				}
				inMsg.prettyPrint("C");
				outMsg = dfa.serverProcess(inMsg);
				
				// check for shutdown
				if (outMsg.opcode() == Message.OP_SHUTDOWN) {
					this.shutdown = true;
					connectionListener.remove(this);
					socket.close();
					return;
				}
				outMsg.prettyPrint("S");
				outMsg.write(bw);
			}
		} catch (Exception e) {
			connectionListener.remove(this);
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		this.shutdown = true;
	}

	public void appendToSendQueue(Message msg) {
		sendQueue.add(msg);
	}
}
