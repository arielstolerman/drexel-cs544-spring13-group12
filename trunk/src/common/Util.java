package common;

import java.util.ArrayList;
import java.util.Arrays;
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
	
//	public static void print(String sender, byte[] stream, String streamStr) {
//		String prefix = sender + " > ";
//		String indent = indent(prefix);
//		int wrap = 96;
//		String raw = new String(stream).replaceAll("\n", "\\n")
//				.replaceAll("\r", "\\r");
//		System.out.println(indentedWrapped(prefix + "raw:  ", raw, wrap));
//		System.out.println(indentedWrapped(indent + "byte: ", streamStr, wrap));
//		System.out.println();
//	}
//	
//	private static String indent(String s) {
//		String res = "";
//		int size = s.length();
//		for (int i = 0; i < size; i++)
//			res += " ";
//		return res;
//	}
//	
//	private static String indentedWrapped(String prefix, String s, int wrap) {
//		String res = prefix;
//		String ind = indent(prefix);
//		int lim = s.length() / wrap;
//		for (int i = 0; i < lim; i++) {
//			res += s.substring(0, wrap) + "\n" + ind;
//			s = s.substring(wrap, s.length());
//		}
//		res += s;
//		return res;
//	}
}









