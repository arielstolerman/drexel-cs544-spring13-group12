package protocol;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import devices.House;

public class ConnectionListener implements Runnable {
	
	// fields
	private List<ServerComm> sList = new ArrayList<ServerComm>();
	private boolean shutdown = false;
	private House house;
	
	public ConnectionListener(House house) {
		this.house = house;
	}
	
	public void run() {
		try {
			ServerSocket servSocket = new ServerSocket(Server.PORT);
			servSocket.setSoTimeout(Server.LISTEN_TIMEOUT_MS);
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
				ServerComm serverComm = new ServerComm(this, commSocket,
						new DFA(house, this));
				sList.add(serverComm);
				Thread thread = new Thread(serverComm);
				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Raises the shutdown flag, such that on the next shutdown check the
	 * connection will terminate.
	 */
	public void shutdown() {
		shutdown = true;
	}

	/**
	 * Removes the given server communication from the list of communications.
	 * @param serverComm
	 */
	public void remove(ServerComm serverComm) {
		this.sList.remove(serverComm);		
	}
	
	/**
	 * Appends the given update message to the pending messages to send on each
	 * server communication except the given one (which is connected to the
	 * client that generated the action and caused the update; that client will
	 * receive a confirm message).
	 * @param updateMsg
	 * @param serverComm
	 */
	public void broadcast(Message updateMsg, ServerComm serverComm) {
		for (ServerComm s : sList) {
			if (s == serverComm) continue;
			s.appendToSendQueue(updateMsg);
		}
	}
}
