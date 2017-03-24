package hisaab.util;


import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceTest {


	
	 public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
	        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	        keyGen.initialize(512);
	        byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
	        StringBuffer retString = new StringBuffer();
	        for (int i = 0; i < publicKey.length; ++i) {
	            retString.append(Integer.toHexString(0x0100 + (publicKey[i] & 0x00FF)).substring(1));
	        }
	        System.out.println(retString);
	    }
}

