package protocol;

import java.io.*;
import java.net.*;

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
	
	private static ClientComm createTest1() {
		return new ClientComm() {
			public void run() {
				try {
					Socket S = new Socket("127.0.0.1", 7070);
					System.out.println("client connected.");
					BufferedReader br = new BufferedReader(new InputStreamReader(S.getInputStream()));
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(S.getOutputStream()));
					bw.write("ping\n");
					bw.flush();					
					System.out.println("response: " + br.readLine());
					S.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

	}
	
	private static ClientComm createTest2() {
		return new ClientComm() {
			public void run() {
				try {
					Socket S = new Socket("127.0.0.1", 7070);
					System.out.println("client connected.");
					BufferedReader br = new BufferedReader(new InputStreamReader(S.getInputStream()));
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(S.getOutputStream()));
					
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
						S.close();
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
					outMsg = new Message(DESAuth.genUserSemiResponse("ariel",
							"stolerman", inMsg.contentBytes()),
							Message.OP_RESPONSE);
					outMsg.prettyPrint("C");
					outMsg.write(bw);
					
					// get init from server or failure
					inBuff = br.readLine();
					inMsg = Message.fromHexString(inBuff);
					inMsg.prettyPrint("S");
					if (inMsg.opcode() == Message.OP_ERROR) {
						S.close();
						throw new Exception("authentication error! exiting");
					}
					
					// shutdown
					outMsg = Message.SHUTDOWN;
					outMsg.prettyPrint("C");
					outMsg.write(bw);
					
					S.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

	}
}
