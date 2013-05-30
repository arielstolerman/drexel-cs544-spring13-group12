package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import protocol.DESAuth;
import protocol.Message;

import common.Util;

public class ClientCommFactory {
	public static ClientComm createTest(int i) {
		switch (i) {
		case 1:
		{
			return createTest1();
		}
		case 2:
		{
			return createTest2();
		}
		default: 
		{
			return createTest1();
		}
		}
	}
	
	public static ClientComm createClientCLI(String host, int port, String user,
			String pass) {
		return new ClientCommCLI(host, port, user, pass);
	}
	
	private static ClientComm createTest1() {
		return new ClientComm() {
			public void run() {
				try {
					Socket socket = new Socket("127.0.0.1", 7070);
					System.out.println(Util.dateTime() + " -- Client connected\n");
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					bw.write("ping\n");
					bw.flush();					
					System.out.println("response: " + br.readLine());
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void postAction(Message createUpdateMessage) {}
			
			@Override
			public Message getPostedActionAndReset() { return null; }

			@Override
			public void killInput() {}
		};

	}
	
	private static ClientComm createTest2() {
		return new ClientComm() {
			public void run() {
				try {
					Socket socket = new Socket("127.0.0.1", 7070);
					System.out.println(Util.dateTime() + " -- Client connected\n");
					BufferedReader br = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					BufferedWriter bw = new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream()));

					String inBuff;
					Message inMsg, outMsg;
					
					// poke
					outMsg = Message.POKE;
					outMsg.prettyPrint("C");
					outMsg.write(bw);
					
					// get version from server
					inBuff = br.readLine();
					inMsg = Message.fromHexString(inBuff);
					inMsg.prettyPrint("S");
					if (!inMsg.content().equals("RSHC 0000")) {
						socket.close();
						throw new Exception("Response was: " + inMsg.content() +
								" expected: 'RSHC 0000'");
					}
					
					// send version to server
					outMsg = new Message("RSHC 0000".getBytes(),
							Message.OP_VERSION);
					outMsg.prettyPrint("C");
					outMsg.write(bw);
					
					// get challenge from server
					inBuff = br.readLine();
					inMsg = Message.fromHexString(inBuff);
					inMsg.prettyPrint("S");
					
					// generate response
					outMsg = new Message(DESAuth.genUserSemiResponse(
							"ariel",
							"ariel123", inMsg.contentBytes()),
							Message.OP_RESPONSE);
					outMsg.prettyPrint("C");
					outMsg.write(bw);
					
					// get init from server or failure
					inBuff = br.readLine();
					inMsg = Message.fromHexString(inBuff);
					inMsg.prettyPrint("S");
					if (inMsg.opcode() == Message.OP_ERROR) {
						socket.close();
						throw new Exception("authentication error! exiting");
					}
					
					// do action - set volume of TV #2 to 78 with seq. #6A
					outMsg = new Message(new byte[]{
							Message.OP_ACTION,
							(byte) 0x6A,
							(byte) 0x03,
							(byte) 0x02,
							(byte) 0x03,
							(byte) 0x4E
					});
					outMsg.prettyPrint("C");
					outMsg.write(bw);
					
					// get confirm / deny from server
					inBuff = br.readLine();
					inMsg = Message.fromHexString(inBuff);
					inMsg.prettyPrint("S");
					if (inMsg.opcode() == Message.OP_CONFIRM) {
						System.out.println("action confirmed");
					}
					else {
						System.out.println("action denied");
					}
					
					// shutdown
					outMsg = Message.SHUTDOWN;
					outMsg.prettyPrint("C");
					outMsg.write(bw);
					
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void postAction(Message createUpdateMessage) {}
			
			@Override
			public Message getPostedActionAndReset() { return null; }

			@Override
			public void killInput() {}
		};

	}
}
