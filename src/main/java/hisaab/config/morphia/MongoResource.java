package hisaab.config.morphia;




import hisaab.util.Constants;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;






import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.sun.istack.Nullable;


public enum MongoResource {
	INSTANCE;
    private MongoClient mongoClient;
    private DB db;
    public MongoConnection mongoConnection;
    
    /**
     * Constructor 
     */
    private MongoResource() {
        try {
            if (mongoClient == null)
                getClient();
            System.out.println("mongoClient..."+mongoClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    @Nullable
    /**
     * Gives the mongodb client object
     */
    private void getClient() {
    	try {
//			MongoClient mongoClient = new MongoClient(System.getenv("OPENSHIFT_MONGODB_DB_HOST"), 27017 );

    		if(Constants.DEV_MODE){
				mongoClient = new MongoClient( "localhost" , 27017 );
				db = mongoClient.getDB( "hisaab_local" );

				System.out.println("db....");
    		
			/*if(Constants.DEV_MODE){
				mongoClient = new MongoClient( "139.59.26.175" , 27017 );
				db = mongoClient.getDB( "hisaab" );
				System.out.println("db....");
				
*//***for digital ocean
 * **//*
				String pass = "tacktile2014";
				 char[] password = pass.toCharArray();
				
				 System.out.println(db);
					if (db.authenticate("tacktile", password)) {
						this.db = db;
//						   System.out.println("Successfully logged in to MongoDB!");
//						mongoConnection = new MongoConnection(mongoClient, db);
					} else {
						
					}

				
				
				

				
				

				this.db = db;*/

				
//				mongoConnection = new MongoConnection(mongoClient, db);
			}else{
				 mongoClient = new MongoClient(System.getenv("OPENSHIFT_MONGODB_DB_HOST"),Integer.parseInt(System.getenv("OPENSHIFT_MONGODB_DB_PORT")));
				 db = mongoClient.getDB( "hisaab" );
				 String pass = "1iNHMtL1mjVG";
				 char[] password = pass.toCharArray();
				
				 System.out.println(db);
					if (db.authenticate("admin", password)) {
						this.db = db;
//						   System.out.println("Successfully logged in to MongoDB!");
//						mongoConnection = new MongoConnection(mongoClient, db);
					} else {
						
					}
				 
			}
			
//			mongoConnection = new  MongoConnection(getMongoClientSeted(), getDB());
//			MongoClient mongoClient = new MongoClient( "mongodb://"+System.getenv("OPENSHIFT_MONGODB_DB_HOST")+":"+System.getenv("OPENSHIFT_MONGODB_DB_PORT")+"/");
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
       
    }
    
    public DB getDB() {
        return this.db;
    }
    public MongoConnection getMongoConnection(String dbname){
    	
    	return new MongoConnection(getMongoClientSeted(), getDB());
    	
    }
 
    public MongoClient getMongoClientSeted(){
    	 
    	return mongoClient;
    }
    
    /**
     * The method is used for creating a datastore.
     * @param classOfT
     * @return datastore.
     */
    public <T> Datastore getDatastore(Class<T> classOfT) {
    	Datastore datastore = null;
      
    	Morphia morphia = new Morphia();
		morphia.map(classOfT);
		
		if(Constants.DEV_MODE){
//			mongoClient = new MongoClient( "localhost" , 27017 );
			 String pass = "tacktile2014";
			 char[] password = pass.toCharArray();
			 
			 getDB().authenticate("tacktile", password);
				System.out.println(getMongoClientSeted()+getDB().getName());
				if(getDB().isAuthenticated()){
					System.out.println("yes data base is authenticate");
					datastore = morphia.createDatastore(getMongoClientSeted(), getDB().getName());
					datastore.ensureIndexes();
				}else{
					datastore = morphia.createDatastore(getMongoClientSeted(), getDB().getName());
					datastore.ensureIndexes();
				}
//			datastore = morphia.createDatastore(mongoClient, "productionwebapp");
					
		}else{
//			 mongoClient = new MongoClient(System.getenv("OPENSHIFT_MONGODB_DB_HOST"),Integer.parseInt(System.getenv("OPENSHIFT_MONGODB_DB_PORT")));
			 
			 
//			 DB db = Mongo.connect(new DBAddress(System.getenv("OPENSHIFT_MONGODB_DB_HOST"),
//					 Integer.parseInt(System.getenv("OPENSHIFT_MONGODB_DB_PORT")),"productionapp"));
			DB db = this.db;
			 String pass = "1iNHMtL1mjVG";
			 char[] password = pass.toCharArray();
			 
//			System.out.println(db);
			if (this.db.authenticate("admin", password)) {
				System.out.println("Successfully logged in to MongoDB!");
//				mongoClient.getDB("productionapp").authenticate("admin", password);
				
				if(this.db.isAuthenticated()){
					System.out.println("yes data base is authenticate..."+mongoClient);
					datastore = morphia.createDatastore(getMongoClientSeted(), getDB().getName());
					datastore.ensureIndexes();
				}
				else{
					System.out.println("no data base is authenticate");
				}
				
			} else {
				
			}
		}
		
		
        return datastore;
    }

}
