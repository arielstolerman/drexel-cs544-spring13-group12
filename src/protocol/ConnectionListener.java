package protocol;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import devices.House;

public class ConnectionListener implements Runnable {
	private boolean shutdown = false;
	private House house;
	
	public ConnectionListener(House house) {
		this.house = house;
	}
	
	public void run() {
		try {
			ServerSocket servSocket = new ServerSocket(7070);
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
				Thread T = new Thread(new ServerComm(commSocket, new DFA(house)));
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
