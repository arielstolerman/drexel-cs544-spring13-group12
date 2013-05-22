package protocol;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import devices.House;

public class ConnectionListener implements Runnable {
	private List<ServerComm> sList = new ArrayList<ServerComm>();
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
				ServerComm serverComm = new ServerComm(this, commSocket, new DFA(house, this));
				sList.add(serverComm);
				Thread T = new Thread(serverComm);
				T.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		shutdown = true;
	}

	public void remove(ServerComm serverComm) {
		this.sList.remove(serverComm);		
	}

	public void broadcast(Message updateMsg, ServerComm serverComm) {
		for (ServerComm s : sList) {
			if (s == serverComm) continue;
			s.appendToSendQueue(updateMsg);
		}
		
	}
}
