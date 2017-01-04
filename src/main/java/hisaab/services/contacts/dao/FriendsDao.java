package hisaab.services.contacts.dao;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import hisaab.config.morphia.DataTransaction;
import hisaab.config.morphia.MongoConnection;
import hisaab.config.morphia.MorphiaDatastoreTrasaction;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.user.dao.PrivateUserDao;
import hisaab.services.user.modal.PrivateUser;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;

import org.bson.NewBSONDecoder;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import com.google.gson.Gson;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

public class FriendsDao {

	
	public static FriendList getAssociatedUserDoc(UserMaster user){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		FriendList frndList = null;
		Query<FriendList> query = datastore.createQuery(FriendList.class);
		if(user.getUserType() == Constants.STAFF_USER)
			query.field("_id").equal(""+user.getOwnerId());
		else
			query.field("_id").equal(""+user.getUserId());
		if(query.get() != null){
			ObjectMapper mapper = new ObjectMapper();
			frndList = query.get();
		}
		else{
			frndList = new FriendList();
			frndList.setUserId(""+user.getUserId());
			frndList.setCreatedTime(System.currentTimeMillis());
			frndList.setUpdatedTime(System.currentTimeMillis());
			datastore.save(frndList);
		}
		return frndList;
	}
	
	public static FriendList getAssociatedUserDocForPull(UserMaster user){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		FriendList frndList = null;
		Query<FriendList> query = datastore.createQuery(FriendList.class);
		query.field("_id").equal(""+user.getUserId());
		if(query.get() != null){
			ObjectMapper mapper = new ObjectMapper();
			frndList = query.get();
		}
		else{
			frndList = null;
		}
		return frndList;
	}
	
	public static List<FriendContact> pullAssociatedUserDoc(UserMaster user,Long pullTime){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		FriendList frndList = null;
		List<FriendContact> friendContacts = new ArrayList<FriendContact>();
		
		Query<FriendList> query = datastore.createQuery(FriendList.class);
//		.field("_id").equal(user.getUserId()).field("updatedTime").lessThanOrEq(pullTime);
		query.and(
			    query.criteria("_id").equal(""+user.getUserId()),
			    query.criteria("updatedTime").greaterThan(pullTime)
			);

		if(query.get() != null){
			ObjectMapper mapper = new ObjectMapper();
			frndList = query.get();
			friendContacts = frndList.friends;
		}
		
		return friendContacts;
	}
	
	public static boolean addFriends(FriendList friendList){
		boolean flag = false;
		
			
			
			Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
			Query<FriendList> query = datastore.createQuery(FriendList.class);

			query.field("_id").equal(friendList.getUserId());
			UpdateOperations<FriendList> op = datastore.createUpdateOperations(FriendList.class);
			op.set("updatedTime", System.currentTimeMillis());
			op.set("idCount", friendList.getIdCount());
			op.addAll("friends", friendList.getFriends(),false);
			UpdateResults ur = datastore.update(query,op );
			if(ur.getUpdatedCount()>0){
				flag = true;
			}
			
	
//		}
		return flag;
	}
	
	public static void updateAmount(String id, String frndId, TransactionDoc transDoc){
		
		
		ObjectMapper mapper = new ObjectMapper();
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		Query<FriendList> query = datastore.createQuery(FriendList.class);
		query.field("_id").equal(id);
		query.filter("friends.frndId", frndId );
		
		if(!query.asList().isEmpty()){
		
			FriendContact frndCon = null;
			for(FriendContact fc : query.asList().get(0).friends){
				if(fc.getFrndId().equals(frndId)){
					frndCon = fc;
					break;
				}
			}
				frndCon.setTransactionDocId(transDoc.getIdString());
			if(transDoc.getUser1() == id){
				frndCon.setAmount(transDoc.getAmount());
				frndCon.setPaymentStatus(transDoc.getPaymentStatus());
			}
			else{
				frndCon.setAmount(transDoc.getAmount());
				if(transDoc.getPaymentStatus() == Constants.TO_GIVE)
					frndCon.setPaymentStatus(Constants.TO_TAKE);
				else
					frndCon.setPaymentStatus(Constants.TO_GIVE);
			}
				UpdateOperations<FriendList> op = datastore.createUpdateOperations(FriendList.class);
				op.disableValidation();
				op.set("friends.$.transactionDocId", transDoc.get_id().toString());
				op.set("friends.$.amount", frndCon.getAmount());
				op.set("friends.$.paymentStatus", frndCon.getPaymentStatus());
				op.set("friends.$.updatedTime", System.currentTimeMillis());
				op.enableValidation();
				
				UpdateResults ur = datastore.update(query,op );
		}
		
	}
	
	
	/***
	 * Update Opening Balance amount and date also update latest amount against user
	 * must be called after updating opening Balance amount.
	 ***/
	public static void updateOpeningBalAmount(String id, String frndId, TransactionDoc transDoc){
	
		ObjectMapper mapper = new ObjectMapper();
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		Query<FriendList> query = datastore.createQuery(FriendList.class);
		query.field("_id").equal(id);
		query.filter("friends.frndId", frndId );
		
		if(!query.asList().isEmpty()){
		
			FriendContact frndCon = null;
			for(FriendContact fc : query.asList().get(0).friends){
				if(fc.getFrndId().equals(frndId)){
					frndCon = fc;
					break;
				}
			}
				frndCon.setTransactionDocId(transDoc.getIdString());
				frndCon.setOpeningBalDate(transDoc.getOpeningBalDate());
			if(transDoc.getUser1() == id){
				frndCon.setOpeningBalAmt(transDoc.getOpeningBalAmt());
				frndCon.setAmount(transDoc.getAmount());
				frndCon.setPaymentStatus(transDoc.getPaymentStatus());
			}
			else{
				frndCon.setOpeningBalAmt(transDoc.getOpeningBalAmt()*(-1));
				frndCon.setAmount(transDoc.getAmount());
				if(transDoc.getPaymentStatus() == Constants.TO_GIVE)
					frndCon.setPaymentStatus(Constants.TO_TAKE);
				else
					frndCon.setPaymentStatus(Constants.TO_GIVE);
			}
				UpdateOperations<FriendList> op = datastore.createUpdateOperations(FriendList.class);
				op.disableValidation();
				op.set("friends.$.transactionDocId", transDoc.get_id().toString());
				op.set("friends.$.amount", frndCon.getAmount());
				op.set("friends.$.paymentStatus", frndCon.getPaymentStatus());
				op.set("friends.$.updatedTime", System.currentTimeMillis());
				op.set("friends.$.openingBalAmt", frndCon.getOpeningBalAmt());
				op.set("friends.$.openingBalDate", frndCon.getOpeningBalDate());
				
				op.enableValidation();
				
				UpdateResults ur = datastore.update(query,op );
		}
		
	}
	
	/*public static void main(String[] args) {
//		updateAmount(3, 2, 0);
		UserMaster us = new UserMaster();
		us.setUserId(8);
//		getFriendForBlocked("3", 0, us);
//		updateFriendsDocsForUnmangedUser(us);
		List<FriendContact> frnds = pullFriendsOfOwner("4", 0);
		PrivateUser privateUser = new PrivateUser();
		privateUser.setContactNo("1232312136546");
		privateUser.setDisplayName("Ansh");
		privateUser.setOwnerId(us.getUserId());
		privateUser.setCreatedTime(System.currentTimeMillis());
		PrivateUserDao.addPrivateUserForBlocked(us.getUserId(), privateUser);
System.out.println(privateUser.getPrivateUserId());
//		 blockFriend("3", us, 5, "3");
		FriendContact frnd = getFriendForWeb(1, 1,us );
		if(frnd != null){
			System.out.println("==> "+frnd.getFrndId());
		}else
			System.out.println("no such friends ..");
	
	}*/
	//method to change the name of friend's contact name
	public static void updateFriend(List<FriendContact> frndcont,UserMaster user){
			String id=""+user.getUserId();
			ObjectMapper mapper = new ObjectMapper();
			Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
			Query<FriendList> query = datastore.createQuery(FriendList.class);
			query.field("_id").equal(id);
			
			UpdateOperations<FriendList> op = datastore.createUpdateOperations(FriendList.class);
			op.disableValidation();
			if(!query.asList().isEmpty()){
			   try{
				List<FriendContact> frndconta = query.get().getFriends();
				for(FriendContact fc : frndconta){
					for(FriendContact fcon : frndcont){
					if(fc.getFrndId()==fcon.getFrndId()){
						query.filter("friends.frndId", fc.getFrndId() );
						op.set("friends.$.contactName", fcon.getContactName());
						UpdateResults ur = datastore.update(query,op );
						op.enableValidation();
					}
					}
				}	
			   }catch(Exception e){
				   e.printStackTrace();
			   }

			}
		}
		
	
	public static Object pullFriendsById(UserMaster user, List<String> ids){
		  Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		  BasicDBList objlist = null;
		  DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",""+user.getUserId())); 
		  ObjectMapper mapper = new ObjectMapper();
		  Object res = null;
		  DBObject match = new BasicDBObject("$match", new BasicDBObject("friends.frndId", 
		     new BasicDBObject("$in",ids ))); 
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("friend",new BasicDBObject("$push","$friends"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$friends");
		  AggregationOutput output = datastore.getCollection(FriendList.class).aggregate(match2,project,match,group);
		  System.out.println("i m in");
		  objlist = (BasicDBList)output.getCommandResult().get("result") ;
		  try {
			  		   
			  System.out.println("== : "+mapper.writeValueAsString((output.getCommandResult().get("result") )));
			  		    
			  } catch (Exception e) {
			   e.printStackTrace();
			  }
		  if(!objlist.isEmpty())
			  res = objlist.get(0);
		 
			  return res;
		 }
		
	public static FriendContact getFriendForWeb(String frndId, int type, UserMaster  user){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		 FriendContact frnd = null;
		 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",""+user.getUserId())); 
		 /* BasicDBObject frndStatus = null;
		  if(type == Constants.NOT_REGISTERED_USER)
			  frndStatus =  new BasicDBObject("friends.frndStatus", type);
		  	else
		  		frndStatus =  new BasicDBObject("friends.frndStatus", new BasicDBObject("$ne",Constants.NOT_REGISTERED_USER));
		*/  
		  
/*		  DBObject match1 =  null ;
		  	if(type == Constants.UNMANAGED_USER)
		  		match1 = new BasicDBObject("$match", new BasicDBObject("friends.frndStatus", type));
		  	else
		  		match1 = new BasicDBObject("$match", new BasicDBObject("friends.frndStatus", new BasicDBObject("$ne",Constants.UNMANAGED_USER)));
*/	/*	 BasicDBList query = new BasicDBList();
		query.add(new BasicDBObject(frndStatus));		
		System.out.println("frndId : "+frndId);
		query.add(new BasicDBObject("friends.frndId",new BasicDBObject("$eq", frndId)));
	*/
//		 DBObject match = new BasicDBObject("$match", new BasicDBObject("$and", query));
		 DBObject match = new BasicDBObject("$match", new BasicDBObject("friends.frndId", frndId));
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("friends",new BasicDBObject("$push","$friends"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$friends");
		  try {
		  AggregationOutput output = datastore.getCollection(FriendList.class).aggregate(match2,project,match, group);
		  System.out.println("i m in");
		  
		   ObjectMapper mapper = new ObjectMapper();
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
//			   System.out.println(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("friends")));
			   List<FriendContact> flist = (List<FriendContact>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("friends")), List.class);
			   frnd =  mapper.readValue(mapper.writeValueAsString(flist.get(0)), FriendContact.class);
			   System.out.println("=>"+frnd.getFrndStatus());
		   }
		   
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 
		 return frnd;
		 
		 
/*		 Query<FriendList> query = datastore.createQuery(FriendList.class);
		 
		 
		 JSONObject obj = new JSONObject();
		 obj.put("friends.frndId", frndId);
		 obj.put("friends.frndStatus", 0);
		
		 FriendList flist = datastore.createQuery(FriendList.class)
				 
				 .field("_id").equal(user.getUserId())
				
                .field("friends.frndId").equal(4)
                .field("friends.frndStatus").equal(1)
                .retrievedFields(true, "friends.$")
                .get();
		 ObjectMapper mapper = new ObjectMapper();
		 try {
			System.out.println(mapper.writeValueAsString(flist));
		} catch (Exception e) {
			e.printStackTrace();
		}
*/	}
	public static HashMap<String,FriendContact> getFriendContactbyUserList(UserMaster user,List<Long> userlist) {
		HashMap<String,FriendContact> hashfriend = new HashMap<String,FriendContact>() ;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		FriendList frndList = null;
		List<FriendContact> friendContacts = new ArrayList<FriendContact>();
		
		org.mongodb.morphia.query.Query<FriendList> query = datastore.createQuery(FriendList.class);
		query.field("_id").equal(""+user.getUserId());
		if(query.get() != null){
			frndList = query.get();
			friendContacts = frndList.friends;
		}
		for(FriendContact frndcontact : friendContacts){
			hashfriend.put(frndcontact.getFrndId(), frndcontact);
		}
		return hashfriend;
	}
	

	public static boolean deleteFrndContFromList(String frndId, UserMaster user){
		boolean flag = false;
		
		
		ObjectMapper mapper = new ObjectMapper();
			Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
			Query<FriendList> query = datastore.createQuery(FriendList.class);

			query.field("_id").equal(""+user.getUserId());
			query.filter("friends.frndId",frndId );
			try {
				System.out.println("---==="+mapper.writeValueAsString(query.get()));
			} catch (JsonGenerationException e) {

				e.printStackTrace();
			} catch (JsonMappingException e) {

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			UpdateOperations<FriendList> delete= datastore.createUpdateOperations(FriendList.class).removeAll("friends", new BasicDBObject("frndId", frndId));
			UpdateResults ur = datastore.update(query,delete);
			
			
			if(ur.getUpdatedCount()>0){
				flag = true;
			}
		return flag;
	}
	

	public static int blockFriend(String frndId, UserMaster user, int status, String blockedBy ){
		String id=""+user.getUserId();
		int i = -1;
		ObjectMapper mapper = new ObjectMapper();
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		Query<FriendList> query = datastore.createQuery(FriendList.class);
		query.field("_id").equal(id);
		query.filter("friends.frndId", frndId );
		UpdateOperations<FriendList> op = datastore.createUpdateOperations(FriendList.class);
		op.disableValidation();
		
		if(!query.asList().isEmpty()){
			i = 0;
			op.set("friends.$.frndStatus", status);
			op.set("friends.$.updatedTime", System.currentTimeMillis());
			op.add("friends.$.blockedByList", blockedBy);
			op.enableValidation();
			
			UpdateResults ur = datastore.update(query,op);
			
			if(ur.getUpdatedCount()>0)
				i = 1;
		}
		return i;
	}

	
	
	public static FriendContact getFriendForBlocked(String frndId, int type, UserMaster  user){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		 FriendContact frnd = null;
		 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",""+user.getUserId()));
		 
		 
		 	 BasicDBList query = new BasicDBList();
			query.add(new BasicDBObject("friends.frndStatus",Constants.PRIVATE_USER));		
			System.out.println("frndId : "+frndId);
			query.add(new BasicDBObject("friends.reffId", frndId));
			DBObject frndCond = new BasicDBObject("$and",query);
		 
		 DBObject match = new BasicDBObject("$match", frndCond);
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("friends",new BasicDBObject("$push","$friends"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$friends");
		  try {
		  AggregationOutput output = datastore.getCollection(FriendList.class).aggregate(match2,project,match, group);
		  System.out.println("i m in");
		  
		   ObjectMapper mapper = new ObjectMapper();
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
//			   System.out.println(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("friends")));
			   List<FriendContact> flist = (List<FriendContact>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("friends")), List.class);
			   frnd =  mapper.readValue(mapper.writeValueAsString(flist.get(0)), FriendContact.class);
			   System.out.println("=>"+frnd.getFrndStatus());
		   }
		   
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 
		 return frnd;
		 
		 
	}
	
	
	public static boolean updateFriendsDocsForUnmangedUser(UserMaster user){
		boolean statusFlag = false;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		Query<FriendList> query = datastore.createQuery(FriendList.class);
		List<FriendList> frndListDocs = new ArrayList<FriendList>();
	
		query.and(query.criteria("friends.frndId").equal(""+user.getUserId()),query.criteria("friends.frndStatus").equal(Constants.NOT_REGISTERED_USER));
//		query.filter("friends.frndId", ""+user.getUserId());
//		query.filter("friends.frndStatus", Constants.NOT_REGISTERED_USER);
//		query.filter("docType", Constants.NOT_REGISTERED_USER);		
		if(!query.asList().isEmpty()){
			
			frndListDocs = query.asList();
//			System.out.println("query : "+query.toString());
			
			long epoch = System.currentTimeMillis();
			for(FriendList fl : frndListDocs){
				
				Query<FriendList> upQuer = datastore.createQuery(FriendList.class);
				upQuer.field("_id").equal(fl.getUserId());
				upQuer.filter("friends.frndId", ""+user.getUserId());
				
				ObjectMapper mapper = new ObjectMapper();
				UpdateOperations<FriendList> op = datastore.createUpdateOperations(FriendList.class);
				op.disableValidation();
				op.set("friends.$.frndStatus", 0);
				op.set("friends.$.updatedTime", epoch);
				System.out.println("in frnd status update.");
				op.enableValidation();
			
				UpdateResults ur = datastore.update(upQuer,op);
				if(ur.getUpdatedCount()>0){
					System.out.println("frndStatus Updated.");
					
					try {
						System.out.println("output : "+mapper.writeValueAsString(ur));
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					statusFlag = true;
				}
			
			}
			
		}
		return statusFlag;
//		return frndListDocs;
	}
	
	/***
	 * Fetch associated user without staff
	 * Used By Staff Users
	 ***/
	public static List<FriendContact> pullFriendsOfOwner(String userId, long pullTime){
		  Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		  BasicDBList objlist = null;
		  DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",userId)); 
		  ObjectMapper mapper = new ObjectMapper();
		  Object res = null;
		  List<FriendContact> flist  = null;
		  BasicDBList query = new BasicDBList();
			query.add(new BasicDBObject("friends.frndStatus",new BasicDBObject("$ne", Constants.STAFF_USER)));		
			
			query.add(new BasicDBObject("friends.updatedTime", new BasicDBObject("$gt", pullTime )));
			DBObject frndCond = new BasicDBObject("$and",query);
		 
		 DBObject match = new BasicDBObject("$match", frndCond);
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("friend",new BasicDBObject("$push","$friends"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$friends");
		  AggregationOutput output = datastore.getCollection(FriendList.class).aggregate(match2,project,match,group);
		  System.out.println("i m in");
		  objlist = (BasicDBList)output.getCommandResult().get("result") ;
		  try {
			  		   
			  System.out.println("== : "+mapper.writeValueAsString((output.getCommandResult().get("result") )));
			  if (!objlist.isEmpty()){
				  flist= (List<FriendContact>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objlist.get(0)).get("friend")), List.class);
			  }
			  		    
			  } catch (Exception e) {
			   e.printStackTrace();
			  }
		  if(!objlist.isEmpty())
			  res = objlist.get(0);
		 
			  return flist;
		 }

	public static List<FriendContact> pullAssociatedUserDocUpdated(
			UserMaster user, long pullTime) {
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		 List<FriendContact> flist = new ArrayList<FriendContact>();
			//	 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
				  
				/* BasicDBList idList = new BasicDBList();
				 idList.add(new BasicDBObject("user1",""+user.getUserId()));
				 idList.add(new BasicDBObject("user2",""+user.getUserId()));*/
				 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",""+user.getUserId()));
				  
//				  BasicDBList paramList = new BasicDBList();
//				  paramList.add(new BasicDBObject("transactions.createdTime", new BasicDBObject("$gt",pullTime)));
//				  paramList.add(new BasicDBObject("transactions.transactionStatus", Constants.TRANS_APROOVED));
//				 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("$and", paramList));
				 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("friends.updatedTime", new BasicDBObject("$gt",pullTime)));
				  DBObject gdb1 = new BasicDBObject();
				  gdb1.put("_id","$_id");
				  gdb1.put("friend",new BasicDBObject("$push","$friends"));
				  DBObject group = new BasicDBObject("$group", gdb1);
				  DBObject project = new BasicDBObject("$unwind", "$friends");
				  AggregationOutput output = datastore.getCollection(FriendList.class).aggregate(match2,project,match1, group);
				  System.out.println("i m in");
				  try {
				   ObjectMapper mapper = new ObjectMapper();
				   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
				   List objList =  (List) output.getCommandResult().get("result") ;
				   if(!objList.isEmpty()){
			//		  System.out.println(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")));
					  Gson gson = new Gson();
					  for(BasicDBObject objArr : (List<BasicDBObject>) objList){
						  List<FriendContact> tempList =  gson.fromJson(gson.toJson(objArr.get("friend")),List.class);

						  Iterator<FriendContact> iterator = tempList.iterator();
						  while(iterator.hasNext()){
							 flist.add(gson.fromJson(gson.toJson(iterator.next()), FriendContact.class));
						  }
					  }
			//		  flist = (List<Transaction>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")), List.class);
				   }
				   
				  } catch (Exception e) {
				   e.printStackTrace();
				  }
				 
				 return flist;
	}

	public static void main(String[] arg) throws UnknownHostException{
		UserMaster user = new UserMaster();

		user.setUserId(Long.parseLong("51"));
		
//		List<String> friend = getBlockedUserIdForSaff(user.getUserId());
//		System.out.println(friend.toString());
//		pullAssociatedUserDocUpdated(user,Long.parseLong("1481893519224"));
	}

	public static List<String> getBlockedUserIdForSaff(long ownerId) {
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		 List<String> flist = new ArrayList<String>();
			//	 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
				  
				/* BasicDBList idList = new BasicDBList();
				 idList.add(new BasicDBObject("user1",""+user.getUserId()));
				 idList.add(new BasicDBObject("user2",""+user.getUserId()));*/
				 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",""+ownerId));
				  
//				  BasicDBList paramList = new BasicDBList();
//				  paramList.add(new BasicDBObject("transactions.createdTime", new BasicDBObject("$gt",pullTime)));
//				  paramList.add(new BasicDBObject("transactions.transactionStatus", Constants.TRANS_APROOVED));
//				 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("$and", paramList));
				 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("friends.frndStatus", Constants.BLOCKED_USER));
				  DBObject gdb1 = new BasicDBObject();
				  gdb1.put("_id","$_id");
				  gdb1.put("friendId",new BasicDBObject("$push","$friends.frndId"));
				  DBObject group = new BasicDBObject("$group", gdb1);
				  DBObject project = new BasicDBObject("$unwind", "$friends");
				  AggregationOutput output = datastore.getCollection(FriendList.class).aggregate(match2,project,match1, group);
				  System.out.println("i m in");
				  try {
				   ObjectMapper mapper = new ObjectMapper();
				   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
				   List objList =  (List) output.getCommandResult().get("result") ;
				   if(!objList.isEmpty()){
			//		  System.out.println(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")));
					  Gson gson = new Gson();
					  for(BasicDBObject objArr : (List<BasicDBObject>) objList){
						  List<String> tempList =  gson.fromJson(gson.toJson(objArr.get("friendId")),List.class);

						  Iterator<String> iterator = tempList.iterator();
						  while(iterator.hasNext()){
							 flist.add(gson.fromJson(gson.toJson(iterator.next()), String.class));
						  }
					  }
			//		  flist = (List<Transaction>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")), List.class);
				   }
				   
				  } catch (Exception e) {
				   e.printStackTrace();
				  }
				 
				 return flist;
	}
	
}
