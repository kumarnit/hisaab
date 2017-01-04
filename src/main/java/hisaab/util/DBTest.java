package hisaab.util;

import java.net.UnknownHostException;

import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class DBTest {

	public static void main(String[] args) {
		DB db ;
		MongoClient mongoClient ;
		
		
		try {
			mongoClient = new MongoClient( "139.59.26.175" , 27017 );
			db = mongoClient.getDB( "hisaab" );
			System.out.println("db....");
			ObjectMapper mapper = new ObjectMapper();
			System.out.println(mapper.writeValueAsString(db.authenticateCommand("tacktile", "tacktile2014".toCharArray())));
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
