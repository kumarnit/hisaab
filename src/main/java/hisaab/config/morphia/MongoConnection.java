package hisaab.config.morphia;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoConnection {
  private MongoClient mongoClient;
  private DB db;
  
  MongoConnection(MongoClient mongoClient, DB db){
	  this.mongoClient = mongoClient;
	  this.db = db;
  }
  
	public MongoClient getMongoClient() {
		return mongoClient;
	}
	public void setMongoClient(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}
	public DB getDb() {
		return db;
	}
	public void setDb(DB db) {
		this.db = db;
	}
  
	public void closeConnection(){
		if(mongoClient!=null){
			mongoClient.close();
		}
	}
  
}
