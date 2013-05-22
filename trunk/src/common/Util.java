package common;

import java.text.SimpleDateFormat;
import java.util.*;

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
	
	/**
	 * @param hexStr hexadecimal space-delimited representation of a byte stream.
	 * @return the actual byte stream.
	 */
	public static byte[] toByteStream(String hexStr) {
		String[] bytes = hexStr.split(" ");
		byte[] res = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			res[i] = (byte) Integer.parseInt(bytes[i], 16);
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
	
	// date and time
	
	private static SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
	private static SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	private static Calendar cal;

	/**
	 * @return The current date.
	 */
	public static String date() {
		cal = Calendar.getInstance();
		return df.format(cal.getTime());
	}
	
	/**
	 * @return The current time.
	 */
	public static String time() {
		cal = Calendar.getInstance();
		return tf.format(cal.getTime());
	}
	
	/**
	 * @return The current date and time.
	 */
	public static String dateTime() {
		return date() + " " + time();
	}
}









