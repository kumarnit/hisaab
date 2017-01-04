package hisaab.config.morphia;






import hisaab.util.Constants;

import java.sql.Connection;






import com.mongodb.DB;
import com.mongodb.MongoClient;


public class DataTransaction {
	
	 public MongoConnection mongoConnection;
	 public Connection con;
	 
	 
	 public MongoConnection getMongoConnection(){
		
		/*try {
//			MongoClient mongoClient = new MongoClient(System.getenv("OPENSHIFT_MONGODB_DB_HOST"), 27017 );
			
			MongoClient mongoClient = null;
			DB db = null;
			if(Constants.devMode){
				mongoClient = new MongoClient( "localhost" , 27017 );
				db = mongoClient.getDB( "productionwebapp" );
				System.out.println("db....");
				mongoConnection = new MongoConnection(mongoClient, db);
			}else{
				 mongoClient = new MongoClient(System.getenv("OPENSHIFT_MONGODB_DB_HOST"),Integer.parseInt(System.getenv("OPENSHIFT_MONGODB_DB_PORT")));
				 db = mongoClient.getDB( "productionapp" );
				 String pass = "MWwtdu9Aazrs";
				 char[] password = pass.toCharArray();
				
				 System.out.println(db);
					if (db.authenticate("admin", password)) {
//						    System.out.println("Successfully logged in to MongoDB!");
						mongoConnection = new MongoConnection(mongoClient, db);
					} else {
						
					}
				 
			}
			
			
//			MongoClient mongoClient = new MongoClient( "mongodb://"+System.getenv("OPENSHIFT_MONGODB_DB_HOST")+":"+System.getenv("OPENSHIFT_MONGODB_DB_PORT")+"/");
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		 String Dbname = "";
		 if(Constants.DEV_MODE){
			 Dbname = "hisaab_local";
//			 Dbname = "hisaab";

		 }else{
			 Dbname = "hisaab";
		 }
		 MongoResource resource = MongoResource.INSTANCE;
		 System.out.println("Mongo connection in DataTransaction");
		 mongoConnection = resource.getMongoConnection(Dbname);
		return mongoConnection;
	}
	
}
