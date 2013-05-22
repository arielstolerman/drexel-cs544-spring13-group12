package protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import devices.Util;

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
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
			byte[] outBuff, inBuff;
			String outBuffStr, inBuffStr;
			
			socket.setSoTimeout(1000);  //throw SocketTimeoutException every 1s if nothing to read.  Use this to check for shutdown.
			while (true) {
				inBuffStr = null;
				inBuff = null;
				while (inBuffStr == null) {
					try {
						inBuffStr = br.readLine();
						if (inBuffStr == null) {
							//client closed connection.
							socket.close();
							return;
						}
						inBuff = Util.toByteStream(inBuffStr);
					} catch (SocketTimeoutException e) {
						while (!sendList.isEmpty()) {
							outBuff = sendList.remove(0);
							outBuffStr = Util.toHexString(outBuff);
							Util.print("S", outBuff, outBuffStr);
							outBuffStr += "\n";
							bw.write(outBuffStr);
							bw.flush();
						}
						if (shutdown) {
							socket.close();
							return;
						}
					}
				}
				Util.print("C", inBuff, inBuffStr);
				Message msg = new Message(inBuff);
				Message response = dfa.serverProcess(msg);
				
				if (response == Message.SHUTDOWN) {
					this.shutdown = true;
					connectionListener.remove(this);
					socket.close();
					return;
				}
				outBuff = response.bytes();
				outBuffStr = response.toHexString();
				Util.print("S", outBuff, outBuffStr);
				outBuffStr += "\n";
				bw.write(outBuffStr);
				bw.flush();
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
