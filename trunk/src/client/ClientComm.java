package client;

import protocol.Message;

public interface ClientComm extends Runnable {

	void postAction(Message createUpdateMessage);

	void killInput();

}
