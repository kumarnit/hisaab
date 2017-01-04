package hisaab.services.user.token;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RandomStringGenerator {
	
	public static void main(String[] args) {
		System.out.println(RandomStringGenerator.createRandomString());
	}
	
    private static String toHex(byte[] array)
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0) 
            return String.format("%0" + paddingLength + "d", 0) + hex;
        else
            return hex;
    }
    
    public static String createRandomString(){
    	SecureRandom random = new SecureRandom();
    	 byte[] salt = new byte[15];
    	 random.nextBytes(salt);
    	 return toHex(salt);
    }

}
