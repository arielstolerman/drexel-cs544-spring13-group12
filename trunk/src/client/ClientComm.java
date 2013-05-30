package client;

import protocol.Message;

public interface ClientComm extends Runnable {

	public void postAction(Message actionMessage);
	
	public Message getPostedActionAndReset();

	public void killInput();

}
