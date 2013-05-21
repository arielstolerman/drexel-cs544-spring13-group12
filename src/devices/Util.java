package devices;

import java.util.ArrayList;
import java.util.List;

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
	
	public static List<Byte> toByteArray(byte[] bytes) {
		List<Byte> l = new ArrayList<Byte>();
		for (int i = 0; i < bytes.length; i++) {
			l.add(bytes[i]);
		}
		
		return l;
	}

	public static byte[] cat(byte[] bytes, byte ordinal) {
		byte b[] = new byte[bytes.length + 1];
		for (int i = 0; i < bytes.length; i++) {
			b[i] = bytes[i];
		}
		b[b.length-1] = ordinal;
		return b;
	}

}
