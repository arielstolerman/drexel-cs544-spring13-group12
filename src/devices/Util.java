package devices;

public class Util {

	public static String bufferLeft(char c, int i, String name) {
		while (name.length() < i) {
			name = c + name;
		}
		return name;
	}

}
