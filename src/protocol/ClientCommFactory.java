package protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;

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
					
					byte[] outBuff, inBuff;
					String outBuffStr, inBuffStr;
					
					// poke
					outBuff = new byte[]{0};
					outBuffStr = Util.toHexString(outBuff);
					Util.print("C", outBuff, outBuffStr);
					outBuffStr += "\n";
					bw.write(outBuffStr);
					bw.flush();
					
					// get version from server
					inBuffStr = br.readLine();
					inBuff = Util.toByteStream(inBuffStr);
					Util.print("S", inBuff, inBuffStr);
					inBuffStr = new String(inBuff);
					if (!inBuffStr.equals("RSHC 0000")) {
						Exception e = new Exception("Response was: "
								+ inBuffStr + " expected: " + "RSHC 0000");
						S.close();
						throw e;
					}
					
					// send version to server
					outBuffStr = "RSHC 0000";
					outBuff = outBuffStr.getBytes();
					outBuffStr = Util.toHexString(outBuff);
					Util.print("C", outBuff, outBuffStr);
					outBuffStr += "\n";
					bw.write(outBuffStr);
					bw.flush();
					
					// get challenge from server
					inBuffStr = br.readLine();
					inBuff = Util.toByteStream(inBuffStr);
					Util.print("S", inBuff, inBuffStr);
					inBuffStr = new String(inBuff);
					
					// generate response
					outBuff = DESAuth.genUserSemiResponse("ariel", "stolerman", inBuff);
					outBuffStr = Util.toHexString(outBuff);
					Util.print("C", outBuff, outBuffStr);
					outBuffStr += "\n";
					bw.write(outBuffStr);
					bw.flush();
					
					// get init from server or failure
					inBuffStr = br.readLine();
					inBuff = Util.toByteStream(inBuffStr);
					Util.print("S", inBuff, inBuffStr);
					inBuffStr = new String(inBuff);
					if (inBuff[0] == 1) {
						S.close();
						throw new Exception("authentication error! exiting");
					}
					
					// shutdown
					outBuff = new byte[]{7};
					outBuffStr = Util.toHexString(outBuff);
					Util.print("C", outBuff, outBuffStr);
					outBuffStr += "\n";
					bw.write(outBuffStr);
					bw.flush();
					
					S.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

	}
}
