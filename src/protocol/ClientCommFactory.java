package protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientCommFactory {
	public static ClientComm createTest(int i) {
		switch (i) {
		case 1:
		{
			return createTest1();
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
					Socket S = new Socket("127.0.0.1", 7777);
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
}
