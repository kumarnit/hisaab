package hisaab.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class RandomStringHelper {

	public static  String getCodeRandomString() {
		String allowedChars = "abcdefghijklmnopqrstuvwxyz";
		int length = 6;
		if (allowedChars == null || allowedChars.trim().length() == 0
				|| length <= 0) {
			throw new IllegalArgumentException("Please provide valid input");
		}
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(allowedChars.charAt(rand.nextInt(allowedChars.length())));
		}
		return sb.toString();
	}
	
	public static  String getCodeRandomAlphaNumeric() {
		String allowedChars = "abcdefghijklmnopqrstuvwxyz0123456789";
		int length = 8;
		if (allowedChars == null || allowedChars.trim().length() == 0
				|| length <= 0) {
			throw new IllegalArgumentException("Please provide valid input");
		}
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(allowedChars.charAt(rand.nextInt(allowedChars.length())));
		}
		return sb.toString();
	}
	
	public static void main(String [] r){
		System.out.println(getCodeRandomAlphaNumeric());
	}
	
}
