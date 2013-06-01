/* =============================================================================
 * CS544 - Computer Networks
 * Drexel University, Spring 2013
 * Protocol Implementation: Remote Smart House Control
 * Group 12:
 * - Ryan Corcoran
 * - Amber Heilman
 * - Michael Mersic
 * - Ariel Stolerman
 * 
 * -----------------------------------------------------------------------------
 * File name: DESAuth.java
 * 
 * Purpose:
 * Provides cryptographic utility methods for DES encryption for the client
 * authentication phase of the protocol.
 * Provides utilities for initializing a sample approved user/password pairs, to
 * be used as reference by the server when authenticating clients.
 * 
 * Relevant requirements (details in the file):
 * - SERVICE - the entire file is related to the authentication scheme supported
 *   by the protocol, therefore it is considered part of the service.
 * 
 * =============================================================================
 */

package protocol;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.io.*;

import javax.crypto.*;
import javax.crypto.spec.*;

import common.Util;


/**
 * Class for DES authentication procedures.
 */
public class DESAuth {
	
	// constants
	
	/**
	 * Mapping of users to hashes of their passwords.
	 */
	public static HashMap<String,String> DES_STORE;
	/**
	 * DES storage path
	 */
	public static String DES_STORE_PATH = "./DES_STORE";
	/**
	 * DES storage initialization
	 */
	static {
		// initialize DES storage
		DES_STORE = new HashMap<>();
		try {
			Scanner scan = new Scanner(new File(DES_STORE_PATH));
			scan.nextLine(); // skip header
			// make sure storage contains some user/password pairs
			if (!scan.hasNext()) {
				scan.close();
				throw new Exception("DES storage empty; must have at least " +
						"one user/password pair");
			}
			// read pairs
			String[] line;
			while (scan.hasNext()) {
				line = scan.nextLine().split(",");
				DES_STORE.put(line[0], line[1]);
			}
			scan.close();
		} catch (Exception e) {
			System.err.println("error reading DES storage; exiting.");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	/**
	 * Default challenge length
	 */
	public static final int CHALLENGE_LENGTH_BYTES = 16;
	private static final byte[] SEMI = ";".getBytes();
	
	// challenge-response generation and check procedures
	
	/**
	 * Generates a random challenge as a stream of bytes and returns it.
	 */
	public static byte[] genChallenge() {
		byte[] challenge;
		do {
			challenge = new byte[CHALLENGE_LENGTH_BYTES];
		} while (new String(challenge).contains("\n"));
		new Random().nextBytes(challenge); 
		return challenge;
	}
	
	/**
	 * Generates a response from the given challenge and password - DES
	 * encryption of the input challenge using the given password.
	 * @return the generated response.
	 */
	private static byte[] genResponse(byte[] challenge, String password) {
		try {
			// initialize key
			SecretKey secret = new SecretKeySpec(
					Arrays.copyOf(password.getBytes(), 8), "DES");	
			// encrypt
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			return cipher.doFinal(challenge);
		}
		catch (Exception e) {
			System.err.println("Error generating response to challenge " +
					Util.toHexString(challenge));
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Generates a response message in the form <code>username;response</code>
	 * where the response is the DES encryption of the given challenge with the
	 * given password.
	 */
	public static byte[] genUserSemiResponse(String username, String password, byte[] challenge) {
		byte[] user = username.getBytes();
		byte[] response = genResponse(challenge, password);
		byte[] ans = new byte[user.length + SEMI.length + response.length];
		int i = 0;
		for (int j = 0; j < user.length; j++) {
			ans[i] = user[j];
			i++;
		}
		for (int j = 0; j < SEMI.length; j++) {
			ans[i] = SEMI[j];
			i++;
		}
		for (int j = 0; j < response.length; j++) {
			ans[i] = response[j];
			i++;
		}
		return ans;
	}

	/**
	 * Receives a user response in the form <code>username;response</code> and
	 * checks if the response matches the challenge for the given username.
	 * Returns true if and only if the response is correct for that user.
	 */
	public static boolean checkResponse(byte[] challenge,
			byte[] userSemiResponse) {
		String[] split = new String(userSemiResponse).split(";");
		// make sure length is as expected
		if (split.length != 2)
			return false;
		// extract response
		String username = split[0];
		byte[] response = Arrays.copyOfRange(userSemiResponse,
				username.getBytes().length + SEMI.length,
				userSemiResponse.length);
		// calculate expected response
		byte[] expectedResponse = genResponse(challenge,
				DES_STORE.get(username));
		return Arrays.equals(response, expectedResponse);
	}
	
	// initial DES generation
	
	/**
	 * For initial population of the DES storage.
	 * @throws NoSuchAlgorithmException 
	 */
	@SuppressWarnings("unused")
	private static void populateDESStroe() throws Exception {
		// generate user-password pairs
		String[][] userPassPairs = new String[][]{
				new String[]{"john","smith123"},
				new String[]{"amber","amber123"},
				new String[]{"mike","mike1234"},
				new String[]{"ryan","ryan1234"},
				new String[]{"ariel","ariel123"}
		};
		// populate DES storage
		PrintWriter pw = new PrintWriter(DES_STORE_PATH);
		pw.println("user,password");
		for (String[] pair: userPassPairs) {
			pw.println(pair[0] + "," + pair[1]);
		}
		pw.flush();
		pw.close();
	}
	
	/**
	 * Main for testing
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//populateDESStroe();
		byte[] c = genChallenge();
		System.out.println("challenge:");
		System.out.println(Util.toHexString(c));
		System.out.println();
		
		byte[] rGood = genUserSemiResponse("ariel", "ariel123", c);
		System.out.println("good response:");
		System.out.println(Util.toHexString(rGood));
		System.out.println();
				
		byte[] rBad = genUserSemiResponse("ariel", "blahblah", c);
		System.out.println("bad response:");
		System.out.println(Util.toHexString(rBad));
		System.out.println();
		
		System.out.println("check good: " + checkResponse(c, rGood));
		System.out.println("check good: " + checkResponse(c, rBad));
	}
}
