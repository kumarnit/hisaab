package hisaab.services.transaction.staff_transaction.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import hisaab.config.morphia.MorphiaDatastoreTrasaction;
import hisaab.services.contacts.ContactHelper;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.notification.PushNotificationControler;
import hisaab.services.notification.TransactionNotification;
import hisaab.services.pull.helper.PullDocDao;
import hisaab.services.pull.modal.PullDoc;
import hisaab.services.staff.dao.StaffUserDao;
import hisaab.services.staff.modal.StaffUser;
import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.transaction.request.dao.ModificationRequestDao;
import hisaab.services.transaction.request.modal.ModificationRequest;
import hisaab.services.transaction.staff_transaction.modal.DeletedStaffTransaction;
import hisaab.services.transaction.staff_transaction.modal.StaffTransactionDoc;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import com.google.gson.Gson;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class StaffTransactionDao {
	
	/**
	 * get staff user and owner's shared transaction document document
	 **/
	public static StaffTransactionDoc getTransactionDoc(StaffTransactionDoc staffTransDoc ){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(StaffTransactionDoc.class);
		Query<StaffTransactionDoc> query = datastore.createQuery(StaffTransactionDoc.class);
		query.and(query.criteria("staffId").equal(staffTransDoc.getStaffId()),query.criteria("ownerId").equal(staffTransDoc.getOwnerId()));
		if(query.get() != null){
			ObjectMapper mapper = new ObjectMapper();
			staffTransDoc = query.get();
		}
		else{
			staffTransDoc.setCreatedTime(System.currentTimeMillis());
			datastore.save(staffTransDoc);
		}
		return staffTransDoc;
	}

	
	
	public static void addStaffTransactions(StaffTransactionDoc staffTransDoc , StaffUser user ){
		
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(StaffTransactionDoc.class);
		Query<StaffTransactionDoc> query = datastore.createQuery(StaffTransactionDoc.class);
//		query.field("_id").equals(androidId);
		query.field("_id").equal(staffTransDoc.get_id());
		UpdateOperations<StaffTransactionDoc> op = datastore.createUpdateOperations(StaffTransactionDoc.class);
//		op.disableValidation();
		op.set("idCount", staffTransDoc.getIdCount());
		op.addAll("transactions", staffTransDoc.getTransactions(),false);
		datastore.update(query,op );
			
		/***
		 * Notification part 
		 ***/
		boolean flag = false;
		UserMaster userB = null;
		
				userB = UserDao.getUserForWeb(Long.parseLong(staffTransDoc.getOwnerId()));
				flag = true;
		
		if(userB != null && userB.getUserId()!=0){
			
			for(Transaction t : staffTransDoc.getTransactions()){
				TransactionNotification tn = new TransactionNotification();
				tn.setNotificationType(Constants.NOTIFICATION_TRANS_BY_STAFF);
				tn.setTransaction(t);
				tn.setUserId(""+userB.getUserId());
				if(staffTransDoc.getTransactions().size()>1)
					tn.setPullFlag(1);
				/***
				 * Need to work on notification.
				 * **/
				PullDoc pullDoc = new PullDoc();
				pullDoc.setUserId(""+userB.getUserId());
//				pullDoc = PullDocDao.getPullDoc(pullDoc);
//				TransactionDoc transactionDoc = new TransactionDoc();
//				transactionDoc.setTransactions(Arrays.asList(t));
//				PullDocDao.addTransaction(transactionDoc,pullDoc);
//				
				FriendContact frnd = null; 
				frnd = FriendsDao.getFriendForWeb(""+user.getStaffId(), 0, userB);
				String msg = "Staff ";
				if(frnd != null)
				{
					if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
						msg = frnd.getContactName();
					else if(user.getStaffProfile().getUserName() != null && 
							!user.getStaffProfile().getUserName().isEmpty())
						msg = user.getStaffProfile().getUserName();
					else
						msg = user.getContactNo();
				}
				
				msg += " has added transactions for you.";
				System.out.println("==///=="+msg);
				PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB.getUserId()), null, tn, msg);
			}
		}
	}


	/**
	 * pull transactions for staff users
	 **/
	public static List<Transaction> getTransactionForStaff(StaffUser user, long pullTime){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(StaffTransactionDoc.class);
		 List<Transaction> flist = new ArrayList<Transaction>();
	//	 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
		  
		 BasicDBList idList = new BasicDBList();
		 idList.add(new BasicDBObject("staffId",""+user.getStaffId()));
		 idList.add(new BasicDBObject("ownerId",""+user.getOwnerId()));
		 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("$and", idList));
		  
//		  BasicDBList paramList = new BasicDBList();
//		  paramList.add(new BasicDBObject("transactions.createdTime", new BasicDBObject("$gt",pullTime)));
//		  paramList.add(new BasicDBObject("transactions.transactionStatus", Constants.TRANS_ADDED_BY_STAFF));
//		 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("$and", paramList));
		 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("transactions.updatedTime", new BasicDBObject("$gt",pullTime)));
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$transactions");
		  AggregationOutput output = datastore.getCollection(StaffTransactionDoc.class).aggregate(match2,project,match1, group);
		  System.out.println("i m in");
		  try {
		   ObjectMapper mapper = new ObjectMapper();
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
	//		  System.out.println(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")));
			  Gson gson = new Gson();
			  for(BasicDBObject objArr : (List<BasicDBObject>) objList){
				  List<Transaction> tempList =  gson.fromJson(gson.toJson(objArr.get("transactions")),List.class);

				  Iterator<Transaction> iterator = tempList.iterator();
				  while(iterator.hasNext()){
					 flist.add(gson.fromJson(gson.toJson(iterator.next()), Transaction.class));
				  }
			  }
	//		  flist = (List<Transaction>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")), List.class);
		   }
		   
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 
		 return flist;
	}

	
	public static List<Transaction> getStaffTransactionsByIds(UserMaster user, List<String> transIds){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(StaffTransactionDoc.class);
		 Transaction trans = null;
		 List<Transaction> flist = new ArrayList<Transaction>();
	//	 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
		  
/*		 BasicDBList idList = new BasicDBList();
		 idList.add(new BasicDBObject("staffId",""+user.getUserId()));
		 idList.add(new BasicDBObject("ownerId",""+user.getOwnerId()));*/
		 
		  DBObject match2 = new BasicDBObject("$match", new BasicDBObject("ownerId", ""+user.getUserId()));
		  
		  BasicDBList paramList = new BasicDBList();
		  paramList.add(new BasicDBObject("transactions.transactionId", new BasicDBObject("$in", transIds)));
		  paramList.add(new BasicDBObject("transactions.transactionStatus", Constants.TRANS_ADDED_BY_STAFF));
		  DBObject match1 = new BasicDBObject("$match", new BasicDBObject("$and", paramList));
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$transactions");
		  AggregationOutput output = datastore.getCollection(StaffTransactionDoc.class).aggregate(match2,project,match1, group);
		  System.out.println("i m in");
		  try {
		   ObjectMapper mapper = new ObjectMapper();
		   System.out.println("== : "+mapper.writeValueAsString((output.getCommandResult() )));
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
			  System.out.println(mapper.writeValueAsString((objList)));
			  Gson gson = new Gson();
			  for(BasicDBObject objArr : (List<BasicDBObject>) objList){
				  List<Transaction> tempList =  gson.fromJson(gson.toJson(objArr.get("transactions")),List.class);

				  Iterator<Transaction> iterator = tempList.iterator();
				  while(iterator.hasNext()){
					 flist.add(gson.fromJson(gson.toJson(iterator.next()), Transaction.class));
				  }
			  }
	//		  flist = (List<Transaction>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")), List.class);
		   }
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 return flist;
	}
	
	
	/***
	 * approval of transactions added by user
	 ***/
	public static boolean updateUserResponseForStaffTransaction(List<String> transIds, UserMaster user, int userResponse, List<String> failed, List<Transaction> approvedtransaction){
		boolean flag = false;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(StaffTransactionDoc.class);
		long epoch = System.currentTimeMillis();
		for(String transId : transIds){
			Transaction transaction = getStaffTransactionForWeb(transId, ""+user.getUserId());
			if(transaction != null){
				Query<StaffTransactionDoc> query = datastore.createQuery(StaffTransactionDoc.class);
		//		query.field("_id").equals(androidId);
				query.disableValidation();
				query.filter("ownerId",""+user.getUserId());
				query.filter("transactions.transactionId", transId);
		//		query.criteria("transactions.transactionId").in(transIds);
		//		query.enableValidation();
				if(query.asList().size()>0){
					
							
					UpdateOperations<StaffTransactionDoc> op = datastore.createUpdateOperations(StaffTransactionDoc.class);
					op.disableValidation();
					
					if(userResponse == Constants.ACTION_APPROVED)
						op.set("transactions.$.transactionStatus", 0);
					else
						op.set("transactions.$.transactionStatus", Constants.ACTION_REJECTED);
					op.set("transactions.$.updatedTime", epoch);
					op.enableValidation();
		//			       datastore.getCollection(StaffTransactionDoc.class).updateMulti(query, op);
					UpdateResults ur = datastore.update(query,op);
					ObjectMapper mapper = new ObjectMapper();
					try {
						System.out.println("update respo : "+mapper.writeValueAsString(ur));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if(ur.getUpdatedCount()>0){
						flag = true;
					if(userResponse == Constants.ACTION_APPROVED){
						
						/*HashMap<String, List<Transaction>> transMap = new HashMap<String, List<Transaction>>();
						
						for(Transaction t : transactions){
							
							if(t.getFrom().equals(""+user.getUserId())){
								List<Transaction> tempTrans = transMap.get(t.getTo());
								if(tempTrans != null){
									tempTrans.add(t);
								}else{
									tempTrans = new ArrayList<Transaction>();
									tempTrans.add(t);
								}
								transMap.put(t.getTo(), tempTrans);
							}
							else{
								List<Transaction> tempTrans = transMap.get(t.getFrom());
								if(tempTrans != null){
									tempTrans.add(t);
								}else{
									tempTrans = new ArrayList<Transaction>();
									tempTrans.add(t);
								}
								transMap.put(t.getFrom(), tempTrans);
							}
						}
						*/
						/*for (String key : transMap.keySet()) {
							List<Transaction> transList = transMap.get(key);
	//						Transaction t = transList.get(0);*/
							TransactionDoc transDoc = new TransactionDoc();
							String key = "";
							if(transaction.getTo().equals(""+user.getUserId()))
								key = transaction.getFrom();
							else if(transaction.getFrom().equals(""+user.getUserId()))
								key = transaction.getTo();
							FriendContact frnd = FriendsDao.getFriendForWeb(key, 0, user);
							transDoc.setUser1(user.getUserId()+"");
							transDoc.setUser2(key);
							if(frnd!=null)
								transDoc.setDocType(frnd.getFrndStatus());
							transDoc = TransactionDao.getTransactionDoc(transDoc);
							
							if(frnd.getFrndStatus() == 0)
								ContactHelper.checkAndAddAssociate(frnd, user, frnd.getFrndId(), transDoc);
							
							
							long count = transDoc.getIdCount();
							transaction.setRefTransacId(transaction.getTransactionId());
							transaction.setTransactionId(transDoc.getUser1()+"_"+transDoc.getUser2()+"_"+(++count));
							transaction.setSrNo(count);
							transaction.setTransactionDocId(transDoc.getIdString());
							transaction.setTransactionStatus(0);
							
							transDoc.setTransactions(Arrays.asList(transaction));
							transDoc.setIdCount(count);
							if(TransactionDao.addTransactions(transDoc, user)>0)
							{
								TransactionDao.addTransactiontoSql(Arrays.asList(transaction));
								approvedtransaction.add(transaction);
							}
							
	//					}
			
					}
				
				}	
				/***
				 * Notification part 
				 ***/
				/*boolean flag = false;
				UserMaster userB = null;
				
						userB = UserDao.getUserForWeb(Long.parseLong(staffTransDoc.getOwnerId()));
						flag = true;
				
				if(userB != null && userB.getUserId()!=0){
					TransactionNotification tn = new TransactionNotification();
					tn.setNotificationType(Constants.NOTIFICATION_TRANS_BY_STAFF);
					tn.setTransaction(staffTransDoc.getTransactions().get(0));
					if(staffTransDoc.getTransactions().size()>1)
						tn.setPullFlag(1);
					String msg = user.getUserProfile().getUserName()+" has added transactions with you.";
					PushNotificationControler.sendNotifications(Arrays.asList(userB.getUserId()), null, tn, msg);
				}
		*/
				}
			}
			else 
				failed.add(transId);
		}
		return flag;
	}
	
	
	public static Transaction getStaffTransactionForWeb(String transId, String ownerId){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(StaffTransactionDoc.class);
		 Transaction trans = null;
		 List<Transaction> flist = new ArrayList<Transaction>();
	//	 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
		  
/*		 BasicDBList idList = new BasicDBList();
		 idList.add(new BasicDBObject("staffId",""+user.getUserId()));
		 idList.add(new BasicDBObject("ownerId",""+user.getOwnerId()));*/
		 
		 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("ownerId", ownerId));
		  
		  BasicDBList paramList = new BasicDBList();
		  paramList.add(new BasicDBObject("transactions.transactionId", transId));
		  paramList.add(new BasicDBObject("transactions.transactionStatus", new BasicDBObject("$ne", 0)));
		  DBObject match1 = new BasicDBObject("$match", new BasicDBObject("$and", paramList));
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$transactions");
		  AggregationOutput output = datastore.getCollection(StaffTransactionDoc.class).aggregate(match2,project,match1, group);
		  System.out.println("i m in");
		  try {
		   ObjectMapper mapper = new ObjectMapper();
		   System.out.println("== : "+mapper.writeValueAsString((output.getCommandResult() )));
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
			  System.out.println(mapper.writeValueAsString((objList)));
			  Gson gson = new Gson();
			  for(BasicDBObject objArr : (List<BasicDBObject>) objList){
				  List<Transaction> tempList =  gson.fromJson(gson.toJson(objArr.get("transactions")),List.class);

				  Iterator<Transaction> iterator = tempList.iterator();
				  
				  if(iterator.hasNext()){
					  trans = gson.fromJson(gson.toJson(iterator.next()), Transaction.class);
				  }
					  
				 /* while(iterator.hasNext()){
					 flist.add(gson.fromJson(gson.toJson(iterator.next()), Transaction.class));
				  }*/
			  }
	//		  flist = (List<Transaction>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")), List.class);
		   }
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 return trans;
	}

	
	
	/***
	 *for updating staff user transaction
	 ***/
	public static void update(List<Transaction> transactionList, StaffUser user){
		System.out.println("in staff update ... ");
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(StaffTransactionDoc.class);
		for(Transaction transaction : transactionList){
			
			Query<StaffTransactionDoc> query = datastore.createQuery(StaffTransactionDoc.class);
			List<StaffTransactionDoc> transDocs = null;
			ObjectMapper mapper = new ObjectMapper();
			UpdateResults ur =null;
			StaffTransactionDoc transDoc = new StaffTransactionDoc();
			long epoch;
			int status=0;
			
			
			Transaction trnsact = getStaffTransactionForWeb(transaction.getTransactionId(),""+user.getOwnerId());
				
				query.field("ownerId").equal(user.getOwnerId()+"");
				query.field("staffId").equal(user.getStaffId());
				query.filter("transactions.transactionId",""+transaction.getTransactionId());
				
	//			System.out.println(mapper.writeValueAsString(query.asList()));
				if(query.get() != null){
					   transDoc = query.get();
					   transDocs = query.asList();
					  }
			
				UpdateOperations<StaffTransactionDoc> op = datastore.createUpdateOperations(StaffTransactionDoc.class);
				op.disableValidation();
				if (trnsact != null ) {
					
				        
	                epoch = System.currentTimeMillis();
					op.set("transactions.$.updatedTime", epoch);
					
					op.set("transactions.$.amount", transaction.getAmount());
					transaction.setAction(Constants.TRANSACTION_UPDATE);
					op.set("transactions.$.action", Constants.TRANSACTION_UPDATE);
					op.set("transactions.$.comment", transaction.getComment());
					op.set("transactions.$.transactionDate",transaction.getTransactionDate());
					transaction.setTransactionStatus(Constants.TRANS_NEED_TO_APROOVE);
					op.set("transactions.$.transactionStatus",Constants.TRANS_NEED_TO_APROOVE);
	    
					op.enableValidation();
	
					ur = datastore.update(query, op);
					
					try {
						System.out.println(mapper.writeValueAsString(ur));
					} catch (Exception e) {
						e.printStackTrace();
					}
					status = ur.getUpdatedCount();
					if(status > 0){
				
						/**
						 * add Notification code.
						 * **/
		
						boolean flag = false;
						UserMaster userB = UserDao.getUserForWeb(user.getOwnerId());
						
						
							if (userB != null && userB.getUserId() != 0) {
								TransactionNotification tn = new TransactionNotification();
								tn.setNotificationType(Constants.NOTIFICATION_STAFF_TRANS_UPDATE);
								tn.setTransaction(transaction);
								tn.setUserId(""+userB.getUserId());
								/**
								 * Notification message
								 **/
								
								FriendContact frnd = null; 
									frnd = FriendsDao.getFriendForWeb(""+user.getStaffId(), 0, userB);
								String msg = "";
								if(frnd != null)
								{
									if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
										msg = frnd.getContactName();
									else if(user.getStaffProfile().getUserName() != null && 
											!user.getStaffProfile().getUserName().isEmpty())
										msg = user.getStaffProfile().getUserName();
									else
										msg = user.getContactNo();
								}
						
								msg +=" has updated transactions.";
								/**
								 * end
								 * */
								
							/*	String msg = user.getStaffProfile().getUserName()
										+ " has updated transaction.";
							*/			
								PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB.getUserId()), null,tn, msg);
							}
						}
					
				}
			}
			
		
	}



	public static void main(String[] args) {
		UserMaster user = new UserMaster();
		user.setUserId(Long.parseLong("4"));
//		getStaffTransactionforUser(user, Long.parseLong("0"));
		StaffUser us = new StaffUser();
		us.setStaffId("ST-6");
		us.setOwnerId(1);
//		getStaffTransactionforStaffByIds(us, Arrays.asList("ST-6_2","ST-6_3"));

		getStaffTransactionforBlockedUser("1", "12");
		
//		deleteTransactionsOfBlockedUser("12", "1");

//		deleteTransactionsOfBlockedUser("1", "2");
		System.out.println(getStaffTransactionforBlockedUser("1", "2"));

//		deleteTransaction("ST-5_4", "ST-5", "4", "ST-5", true);
/*		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date("Oct 4, 2016 2:43:56 AM"));
		cal.add(Calendar.HOUR_OF_DAY, 12);
		cal.add(Calendar.MINUTE, 30);
		System.out.println(cal.getTime());
		System.out.println(cal.getTimeInMillis());*/
//		StaffUser us = new StaffUser();
//		us.setStaffId("ST-5");
//		us.setOwnerId(4);
//		Transaction t = getStaffTransactionForWeb("ST-5_1", ""+user.getOwnerId());
//		System.out.println( "---"+t);
//		getStaffTransactionsByIds(user, Arrays.asList("ST-5_3") );
//		updateUserResponseForStaffTransaction(Arrays.asList("ST-5_3","ST-5_4"), user, 112 );
//		getTransactionForStaff(user, -1);
	}

	
	public static boolean deleteTransaction(String transacId, String staffId, String ownerId, String deletedBy, boolean isStaff){
		boolean flag = false;
		ObjectMapper mapper = new ObjectMapper();
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(StaffTransactionDoc.class);
		StaffTransactionDoc transactionDoc = new StaffTransactionDoc();
		
		
		Query<StaffTransactionDoc> quer = datastore.createQuery(StaffTransactionDoc.class);
		quer.and(quer.criteria("staffId").equal(staffId),quer.criteria("ownerId").equal(ownerId));
		quer.filter("transactions.createdBy",ownerId);
		quer.filter("transactions.transactionId",transacId);
		
		if(!quer.asList().isEmpty()){
			Transaction trans = getStaffTransactionForWeb(transacId, ownerId);
			if(trans != null){
					UpdateOperations<StaffTransactionDoc> updateOp= datastore.createUpdateOperations(StaffTransactionDoc.class).removeAll("transactions", new BasicDBObject("transactionId", transacId));
					UpdateResults up = datastore.update(quer, updateOp);
				try {
					System.out.println("==>" + mapper.writeValueAsString(up));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
		
				if(up.getUpdatedCount() > 0){
					flag = true;
		
					DeletedStaffTransaction dst = new DeletedStaffTransaction();
					
					dst.setStaffId(staffId);
					dst.setDeletedBy(deletedBy);
					dst.setOwnerId(ownerId);
					dst.setTransactionId(transacId);
					dst.setCreatedTime(System.currentTimeMillis());
					DeletedStaffTransactionDao.addDeleteStaffTransId(dst);
					
				if(!isStaff){
					StaffUser userB = null;
					userB = StaffUserDao.getStaffUserByStaffIdForWeb(staffId);
					if(userB != null && userB.getsId()!=0){
						TransactionNotification tn = new TransactionNotification();
						tn.setNotificationType(Constants.NOTIFICATION_STAFF_TRANS_DELETE);
						tn.setTransaction(trans);
						
						tn.setUserId(""+userB.getStaffId());
						
						/**
						 * Notification message
						 **/
						String msg = "";
						UserMaster owner = UserDao.getUserForWeb(Long.parseLong(ownerId));
						if(owner != null)
						{
							if(owner.getUserProfile().getUserName() != null && !owner.getUserProfile().getUserName().isEmpty())
								msg = owner.getUserProfile().getUserName();
							else
								msg = owner.getContactNo();
						}
				
						msg +=" has deleted a transaction with you.";
						System.out.println("==///=="+msg);
						/**
						 * end
						 * */
						/*String msg = user.getUserProfile().getUserName()+" has requested to delete a transaction with you.";
						 */
						PushNotificationControler.sendNotificationsToStaff(Arrays.asList(userB.getStaffId()), null, tn, msg);
						
					}
				}
					else
					{
						UserMaster userB = null;
								userB = UserDao.getUserForWeb(Long.parseLong(ownerId));
						if(userB != null && userB.getUserId()!=0){
							TransactionNotification tn = new TransactionNotification();
							tn.setNotificationType(Constants.NOTIFICATION_TRANS_DELETE);
							tn.setTransaction(trans);
							
							tn.setUserId(""+userB.getUserId());
							
							/**
							 * Notification message
							 **/
							FriendContact frnd = null; 
							frnd = FriendsDao.getFriendForWeb(staffId, 0, userB);
							String msg = "";
							if(frnd != null)
							{
								if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
									msg = frnd.getContactName();
							}
					
							msg +=" has deleted a transaction with you.";
							System.out.println("==///=="+msg);
							/**
							 * end
							 * */
							/*String msg = user.getUserProfile().getUserName()+" has requested to delete a transaction with you.";
							 */
							PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB.getUserId()), null, tn, msg);
					}
					
					
				}
				}
			}			
		}
		
		return flag;
	}

	
	
	public static boolean deleteTransactionsOfBlockedUser( String frndId, String ownerId ){
		boolean flag = false;
		ObjectMapper mapper = new ObjectMapper();
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(StaffTransactionDoc.class);
		StaffTransactionDoc transactionDoc = new StaffTransactionDoc();
		
		List<String> transactionList = getStaffTransactionforBlockedUser(ownerId, frndId);
		System.out.println("get Staff Transaction :"+transactionList);
		if(transactionList != null){
			Query<StaffTransactionDoc> quer = datastore.createQuery(StaffTransactionDoc.class);
			quer.criteria("ownerId").equal(ownerId);
			quer.filter("transactions.transactionId in ",transactionList);
			
			if(!quer.asList().isEmpty()){
				Transaction trans = getStaffTransactionForWeb(transactionList.get(0), ownerId);
				if(trans != null){
					
						UpdateOperations<StaffTransactionDoc> updateOp= datastore.createUpdateOperations(StaffTransactionDoc.class).removeAll("transactions", new BasicDBObject("transactionId", new BasicDBObject("$in",transactionList)));
						UpdateResults up = datastore.update(quer, updateOp);
					try {
						System.out.println("==>" + mapper.writeValueAsString(up));
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
			
					if(up.getUpdatedCount() > 0){
						flag = true;
						
					}
				}			
			}
		}
		
		return flag;
	}


	
	public static List<String> getStaffTransactionforBlockedUser(
			String ownerId, String blockedUserId) {
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(StaffTransactionDoc.class);
		 Transaction trans = null;
		 List<String> transIds = null;
		 List<Transaction> flist = new ArrayList<Transaction>();
	//	 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
		  
		 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("ownerId",ownerId));
		 BasicDBList userList = new BasicDBList();
		  userList.add(new BasicDBObject("transactions.from", blockedUserId));
		  userList.add(new BasicDBObject("transactions.to", blockedUserId));

//		  BasicDBList paramList = new BasicDBList();
//		  paramList.add(new BasicDBObject("$or", userList));
//		  paramList.add(new BasicDBObject("transactions.transactionStatus", Constants.TRANS_ADDED_BY_STAFF));
//		 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("$and", paramList));
		 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("$or", userList));
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$transactions");
		  AggregationOutput output = datastore.getCollection(StaffTransactionDoc.class).aggregate(match2,project,match1, group);
		  System.out.println("i m in");
		  try {
		   ObjectMapper mapper = new ObjectMapper();
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
	//		  System.out.println(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")));
			  Gson gson = new Gson();
			  transIds = new ArrayList<String>();
			  for(BasicDBObject objArr : (List<BasicDBObject>) objList){
				  List<Transaction> tempList =  gson.fromJson(gson.toJson(objArr.get("transactions")),List.class);
				  
				  Iterator<Transaction> iterator = tempList.iterator();
				  while(iterator.hasNext()){
//					 flist.add(gson.fromJson(gson.toJson(iterator.next()), Transaction.class));
					 trans =  gson.fromJson(gson.toJson(iterator.next()), Transaction.class);
					 transIds.add(trans.getTransactionId());
				  }
			  }
	//		  flist = (List<Transaction>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")), List.class);
		   }
		   
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 
		 return transIds;
	}

	
	
	
	

	public static List<Transaction> getStaffTransactionforUser(
			UserMaster user, long pullTime) {
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(StaffTransactionDoc.class);
		 Transaction trans = null;
		 List<Transaction> flist = new ArrayList<Transaction>();
	//	 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
		  
		 BasicDBList idList = new BasicDBList();
		 idList.add(new BasicDBObject());
		 idList.add(new BasicDBObject("ownerId",""+user.getUserId()));
		 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("$and", idList));
		  BasicDBList paramList = new BasicDBList();
		  paramList.add(new BasicDBObject("transactions.createdTime", new BasicDBObject("$gt",pullTime)));
		  paramList.add(new BasicDBObject("transactions.transactionStatus", Constants.TRANS_ADDED_BY_STAFF));
		 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("$and", paramList));
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$transactions");
		  AggregationOutput output = datastore.getCollection(StaffTransactionDoc.class).aggregate(match2,project,match1, group);
		  System.out.println("i m in");
		  try {
		   ObjectMapper mapper = new ObjectMapper();
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
	//		  System.out.println(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")));
			  Gson gson = new Gson();
			  for(BasicDBObject objArr : (List<BasicDBObject>) objList){
				  List<Transaction> tempList =  gson.fromJson(gson.toJson(objArr.get("transactions")),List.class);

				  Iterator<Transaction> iterator = tempList.iterator();
				  while(iterator.hasNext()){
					 flist.add(gson.fromJson(gson.toJson(iterator.next()), Transaction.class));
				  }
			  }
	//		  flist = (List<Transaction>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")), List.class);
		   }
		   
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 
		 return flist;
	}

	
	
	
	
	public static List<Transaction> getStaffTransactionforStaffByIds(
			StaffUser user, List<String> transIds) {
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(StaffTransactionDoc.class);
		 List<Transaction> flist = new ArrayList<Transaction>();
	//	 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
		  
		 BasicDBList idList = new BasicDBList();
		 idList.add(new BasicDBObject("staffId",""+user.getStaffId()));
		 idList.add(new BasicDBObject("ownerId",""+user.getOwnerId()));
		 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("$and", idList));
		  
		 DBObject list = new BasicDBObject("$in",transIds );  
		 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("transactions.transactionId", list));
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$transactions");
		  AggregationOutput output = datastore.getCollection(StaffTransactionDoc.class).aggregate(match2,project,match1, group);
		  System.out.println("i m in");
		  try {
		   ObjectMapper mapper = new ObjectMapper();
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
			  System.out.println(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")));
			  Gson gson = new Gson();
			  for(BasicDBObject objArr : (List<BasicDBObject>) objList){
				  List<Transaction> tempList =  gson.fromJson(gson.toJson(objArr.get("transactions")),List.class);

				  Iterator<Transaction> iterator = tempList.iterator();
				  while(iterator.hasNext()){
					 flist.add(gson.fromJson(gson.toJson(iterator.next()), Transaction.class));
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
