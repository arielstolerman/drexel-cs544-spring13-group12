package devices;

public class Util {

	public static String bufferLeft(char c, int i, String name) {
		while (name.length() < i) {
			name = c + name;
		}
		return name;
	}

	/**
	 * @param stream a stream of bytes
	 * @return the hexadecimal representation of the stream of bytes.
	 */
	public static String toHexString(byte[] stream) {
		String res = "";
		for (byte b: stream) {
			res += String.format("%02x ", b);
		}
		res = res.substring(0, res.length() - 1);
		return res;
	}
}
