package protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;

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
					OutputStream os = S.getOutputStream();
					
					byte[] endOfLine = "\n".getBytes();
					byte[] poke = new byte[]{0};
					
					//String clientMessage = "0\n";
					System.out.print("C: " + Arrays.toString(poke));//clientMessage);
					//bw.write(clientMessage);
					os.write(poke);
					os.write(endOfLine);
					os.flush();
					//bw.flush();			
					String resp = br.readLine();
					if (!resp.equals("RSHC 0000")) {
						Exception e = new Exception("Response was: " + resp + " expected: " + "RSHC 0000");
						throw e;
					}
					System.out.println("S: " + resp);
					
					
					String clientMessage = "RSHC 0000\n";
					System.out.print("C: " + clientMessage);
					
					os.write(clientMessage.getBytes());
					os.flush();
					resp = br.readLine();
					System.out.println("S: " + resp);
					
					clientMessage = "CLIENT RESPONSE\n";
					System.out.print("C: " + clientMessage);
					os.write(clientMessage.getBytes());
					os.flush();
					resp = br.readLine();
					System.out.println("S: " + resp);

					byte[] shutdown = new byte[] {7};
					os.write(shutdown);
					os.write(endOfLine);
					os.flush();
					
					S.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

	}
}
