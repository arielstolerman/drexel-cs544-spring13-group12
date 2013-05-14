package protocol;

public class Client {
	public static void main(String args[]) {
		Thread T = new Thread(ClientCommFactory.createTest(2));
		T.start();
	}
}
