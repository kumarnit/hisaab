package hisaab.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hisaab.config.morphia.MorphiaDatastoreTrasaction;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.user.modal.UserMaster;

import org.bson.BasicBSONObject;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.mongodb.morphia.Datastore;

import com.google.gson.Gson;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Test {

	
	public static void pullFriendsById(UserMaster user, List<Long> ids){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		DBObject match2 = new BasicDBObject("$match", new BasicDBObject("friends.frndId",3)); 
		
		DBObject match = new BasicDBObject("$match", new BasicDBObject("friends.frndId", 
						new BasicDBObject("$in",ids ))); 
		DBObject gdb1 = new BasicDBObject();
		gdb1.put("_id","$_id");
		gdb1.put("friend",new BasicDBObject("$push","$friends"));
		DBObject group = new BasicDBObject("$group", gdb1);
		DBObject project = new BasicDBObject("$unwind", "$friends");
		AggregationOutput output = datastore.getCollection(FriendList.class).aggregate(match2,project,match,group);
		System.out.println("i m in");
		try {
			ObjectMapper mapper = new ObjectMapper();
			System.out.println("== : "+mapper.writeValueAsString(output.getCommandResult()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void calculateAmt(String transDocId){
		double amt = 0;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId(transDocId))); 
		
		DBObject project = new BasicDBObject("$project", new BasicDBObject("amt", 
						new BasicDBObject("$subtract",Arrays.asList("$sum1", "$sum2") ))); 
		
		DBObject gdb1 = new BasicDBObject();
	
		BasicDBList sum1Condition = new BasicDBList();
		sum1Condition.add(new BasicDBObject("$eq",Arrays.asList("$user1", "$transactions.from")));
		sum1Condition.add("$transactions.amount");
		sum1Condition.add(0);
		
		BasicDBList sum2Condition = new BasicDBList();
		sum2Condition.add(new BasicDBObject("$eq",Arrays.asList("$user1", "$transactions.to")));
		sum2Condition.add("$transactions.amount");
		sum2Condition.add(0);
		
		gdb1.put("_id","$_id");
		gdb1.put("sum1",new BasicDBObject("$sum",new BasicDBObject("$cond",sum1Condition)));
		gdb1.put("sum2",new BasicDBObject("$sum",new BasicDBObject("$cond",sum2Condition)));
		
		DBObject group = new BasicDBObject("$group", gdb1);
	
		DBObject unwind = new BasicDBObject("$unwind", "$transactions");
		
		
		AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,unwind,group,project);
		System.out.println("i m in");
		try {
			ObjectMapper mapper = new ObjectMapper();
//			System.out.println("== : "+mapper.writeValueAsString(output));
//			System.out.println("== : "+mapper.writeValueAsString(output.getCommandResult()));
			BasicDBList bso = (BasicDBList) output.getCommandResult().get("result");
			if(!bso.isEmpty()){
				System.out.println("data aahe");
				amt = Double.parseDouble(mapper.writeValueAsString(((BasicDBObject) bso.get(0)).get("amt")));
				System.out.println("Amount : "+amt);
			}
			else{
				System.out.println("data nahi aahe...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static Transaction getTransactionForWebByTransactionList(List<String> transactionIds, String transactionDocId, UserMaster user){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 Transaction trans = null;
		 BasicDBList exprList = new BasicDBList();
		 exprList.add(new BasicDBObject("user1",new BasicDBObject("$eq", ""+user.getUserId())));
		 exprList.add(new BasicDBObject("user2",new BasicDBObject("$eq", ""+user.getUserId())));
//		 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId(transactionDocId))); 
		  DBObject userCond = new BasicDBObject("$or",exprList);
		  DBObject match2 = new BasicDBObject("$match", userCond);
//		  DBObject match1 = new BasicDBObject("$match", new BasicDBObject("transactions.transactionId", transactionId));
		  DBObject match1 = new BasicDBObject("$match", new BasicDBObject("transactions.transactionId", 
					new BasicDBObject("$in",transactionIds ))); 
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$transactions");
		  AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,project,match1, group);
		  System.out.println("i m in");
		  try {
		   ObjectMapper mapper = new ObjectMapper();
		   System.out.println("== : "+mapper.writeValueAsString(output));
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
			   List<Transaction> flist = new ArrayList<Transaction>();// = (List<Transaction>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")), List.class);
//			   trans =  mapper.readValue(mapper.writeValueAsString(flist.get(0)), Transaction.class);
			   
			   List li = objList;
	            for(Object liItem:li){
	                
		             if (liItem instanceof Transaction) {
//		            	 flist.add(liItem);
		             }
	            }
		   }
		   
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 
		 return trans;
	}
	
	
	
	public static void main(String[] args) {
//		long[] ids ={};
//		pullFriendsById(null,Arrays.asList(Long.parseLong("2")) );
//		calculateAmt("57ece38767ff843582787383");
//		UserMaster u = new UserMaster();
//		u.setUserId(1);
//		getTransactionForWebByTransactionList(Arrays.asList("1_2_2","1_2_5","1_4_2","4_8_4"), "", u);
		
		getTransactionCount();
		
		
	}
	
	public static Object getTransactionCount(){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		
		BasicDBList ifno = new BasicDBList();
	
		DBObject gdb1 = new BasicDBObject();
		gdb1.put("_id",Integer.parseInt("1"));
		gdb1.put("user1",Integer.parseInt("1") );
		gdb1.put("user2", Integer.parseInt("1"));
		gdb1.put("count",new BasicDBObject("$size",new BasicDBObject("$ifNull",Arrays.asList("$transactions", new ArrayList()))));
		DBObject project = new BasicDBObject("$project", gdb1);
		
		AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(project);
		System.out.println("i m in");
		try {
			ObjectMapper mapper = new ObjectMapper();
			System.out.println("== : "+mapper.writeValueAsString(output.getCommandResult().get("result")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Object result = null;
		result = output.getCommandResult().get("result");
		return result;
	}
	
}
