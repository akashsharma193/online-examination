package com.online.examination.utility;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtility {

	public static String encryptOrHashPassword(String userId, String input, String mobile) {

		return generateSHA512Hash(userId + input + mobile);
	}
	
	private static String generateSHA512Hash(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");

			byte[] messageDigest = md.digest(value.getBytes());

			BigInteger no = new BigInteger(1, messageDigest);
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		}
		catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("INTERNAL_SERVER_ERROR");
		}
	}

}
