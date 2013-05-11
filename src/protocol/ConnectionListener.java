package protocol;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ConnectionListener implements Runnable {
	private boolean shutdown = false;
	
	public void run() {
		try {
			ServerSocket servSocket = new ServerSocket(7777);
			servSocket.setSoTimeout(1000);
			while (true) {
				Socket commSocket = null;
				while (commSocket == null) {
					try {
						commSocket = servSocket.accept();
					} catch (SocketTimeoutException e) {
						if (shutdown) {
							servSocket.close();
							return;
						}
					}
				}
				Thread T = new Thread(new ServerComm(commSocket));
				T.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		shutdown = true;
	}
}
