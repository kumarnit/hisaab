package hisaab.services.user.token;

        
public class TokenModal {

	private static final long SECONDS = 10800;
	public static String generateToken(){
//		long t1 = System.currentTimeMillis();
		long epoch = (System.currentTimeMillis()/1000)+SECONDS;
		String token = RandomStringGenerator.createRandomString();
		token += "-"+epoch;
		 
		return token;
	}
	
	 public static void main(String[] args) {
//		System.out.println(System.currentTimeMillis());
		System.out.println(new TokenModal().generateToken());
		 
		 
	}
}
