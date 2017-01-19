package hisaab.services.transaction.dao;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.config.morphia.MorphiaDatastoreTrasaction;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.notification.NotificationHelper;
import hisaab.services.notification.PushNotificationControler;
import hisaab.services.notification.TransactionNotification;
import hisaab.services.notification.TransactionReadNotification;
import hisaab.services.pull.modal.ReadTransaction;
import hisaab.services.sms.SMSHelper;
import hisaab.services.sms.dao.SmsDao;
import hisaab.services.sms.modal.SmsTable;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.user.dao.UserDao;
import hisaab.services.transaction.modal.TransactionSql;
import hisaab.services.transaction.openingbalance.modal.OpeningBalRequest;
import hisaab.services.transaction.request.dao.ModificationRequestDao;
import hisaab.services.transaction.request.modal.ModificationRequest;
import hisaab.services.transaction.staff_transaction.modal.StaffTransactionDoc;
import hisaab.services.transaction.webservices.bean.TransDocBean;
import hisaab.services.transaction.webservices.bean.TransactionDisputeBean;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.MorphiaIterator;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import com.google.gson.Gson;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;



public class TransactionDao {

	public static TransactionDoc getTransactionDoc(TransactionDoc transactionDoc ){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
		query.or(
				query.and(query.criteria("user1").equal(transactionDoc.getUser1()),query.criteria("user2").equal(transactionDoc.getUser2())),
				query.and(query.criteria("user1").equal(transactionDoc.getUser2()),query.criteria("user2").equal(transactionDoc.getUser1()))
				);
		query.field("docType").equal(transactionDoc.getDocType());
		if(query.get() != null){
			
			transactionDoc = query.get();
		}
		else{
			transactionDoc.setCreatedTime(System.currentTimeMillis());
			datastore.save(transactionDoc);
		}
		return transactionDoc;
	}
	
	
	public static TransactionDoc getTransactionDocByDocId(String transactionDocId){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		TransactionDoc transactionDoc = null;
		Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
		query.field("_id").equal(new ObjectId(transactionDocId));
		if(query.get() != null){
		
			transactionDoc = query.get();
		}
		
		return transactionDoc;
	}

	
	public static TransactionDoc getTransactionDocForUnmanagedUser(TransactionDoc transactionDoc ){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
		query.and(query.criteria("user1").equal(transactionDoc.getUser1()),query.criteria("user2").equal(transactionDoc.getUser2()));
		query.field("docType").equal(transactionDoc.getDocType());
		if(query.get() != null){
			
			transactionDoc = query.get();
		}
		else{
			transactionDoc.setCreatedTime(System.currentTimeMillis());
			datastore.save(transactionDoc);
		}
		return transactionDoc;
	}
	

	public static int addTransactions(TransactionDoc transactionDoc , UserMaster user ){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
//		query.field("_id").equals(androidId);
		query.field("_id").equal(transactionDoc.get_id());
		UpdateOperations<TransactionDoc> op = datastore.createUpdateOperations(TransactionDoc.class);
//		op.disableValidation();
//		op.add("idCount", transactionDoc.getIdCount());
		op.addAll("transactions", transactionDoc.getTransactions(),false);
		UpdateResults ur = datastore.update(query,op );
		int stat = 0;
		if(ur != null)
			stat = ur.getUpdatedCount();
		/**
		 * calculation part*/
		double user1AmtToTake = 0;
		double user1Amt = 0 ;
		double user2Amt = 0;
		double del = 0;
		double del2 = 0;
		double total = 0;
		/*for(Transaction t : transactionDoc.getTransactions()){
			if(t.getFrom().equals(transactionDoc.getUser1()) ){
				user1AmtToTake += t.getAmount();
				user1Amt += t.getAmount();
			}
			else if(t.getFrom().equals(transactionDoc.getUser2())){
				user2Amt += t.getAmount();
			}
		}
		del = user1Amt - user2Amt;
		del2 = del;
		if(del > 0 ){
			// user1 amt to take from user 2
			if(transactionDoc.getPaymentStatus()== Constants.TO_GIVE){
				transactionDoc.setAmount(transactionDoc.getAmount()-del);
				if(transactionDoc.getAmount()<0){
					transactionDoc.setAmount(transactionDoc.getAmount()*(-1));
					transactionDoc.setPaymentStatus(Constants.TO_TAKE);
				}
			}else {
				transactionDoc.setAmount(transactionDoc.getAmount() + del);
				transactionDoc.setPaymentStatus(Constants.TO_TAKE);
			}
		}else if(del < 0){
			del = del * (-1);
			if(transactionDoc.getPaymentStatus() == Constants.TO_TAKE){
				transactionDoc.setAmount(transactionDoc.getAmount() - del);
				if(transactionDoc.getAmount()<0){
					transactionDoc.setAmount(transactionDoc.getAmount()*(-1));
					transactionDoc.setPaymentStatus(Constants.TO_GIVE);
				}
			}else {
				transactionDoc.setAmount(transactionDoc.getAmount() + del);
				transactionDoc.setPaymentStatus(Constants.TO_GIVE);
			}
		}*/
		
		del = calculateAmt(transactionDoc.getIdString());
		total = del + transactionDoc.getOpeningBalAmt();
		if(total>=0){
			transactionDoc.setAmount(total);
			transactionDoc.setPaymentStatus(Constants.TO_TAKE);
		}
		else{
			transactionDoc.setAmount(total*(-1));
			transactionDoc.setPaymentStatus(Constants.TO_GIVE);
		}
		
		
		
		UpdateOperations<TransactionDoc> op1 = datastore.createUpdateOperations(TransactionDoc.class);
//		op.disableValidation();
		op1.set("idCount", transactionDoc.getIdCount());
		op1.set("amount",transactionDoc.getAmount());
		op1.set("paymentStatus", transactionDoc.getPaymentStatus());
		op1.set("updatedTime", System.currentTimeMillis());
		datastore.update(query,op1 );
		
		del2 = user1Amt - user2Amt;
		FriendsDao.updateAmount(transactionDoc.getUser1(), transactionDoc.getUser2(), transactionDoc);
		
		del2 = user2Amt - user1Amt;
		FriendsDao.updateAmount(transactionDoc.getUser2(), transactionDoc.getUser1(), transactionDoc);
		
		/***
		 * Notification part 
		 ***/
		boolean flag = false;
		UserMaster userB = null;
		if(transactionDoc.getDocType() == 0){
			if(transactionDoc.getUser1().equals(user.getUserId()+"")){
				userB = UserDao.getUserForWeb(Long.parseLong(transactionDoc.getUser2()));
				flag = false;
			}
			else{
				userB = UserDao.getUserForWeb(Long.parseLong(transactionDoc.getUser1()));
				flag = true;
			}
		
			if(userB != null && userB.getUserId()!=0){
		/*		TransactionNotification tn = new TransactionNotification();
				tn.setNotificationType(Constants.NOTIFICATION_TRANS_NEW);
				tn.setTransaction(transactionDoc.getTransactions().get(0));
				tn.setAmount(transactionDoc.getAmount());
				tn.setUserId(""+userB.getUserId());
				if(flag)
					tn.setPaymentStatus(transactionDoc.getPaymentStatus());
				else{
					if(transactionDoc.getPaymentStatus() == Constants.TO_GIVE)
						tn.setPaymentStatus(Constants.TO_TAKE);
					else
						tn.setPaymentStatus(Constants.TO_GIVE);
				}
				if(transactionDoc.getTransactions().size()>1)
					tn.setPullFlag(1);
*/				/**
				 * Notification message
				 **/
				FriendContact frnd = null; 
				frnd = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, userB);
				String msg = "";
				if(frnd != null)
				{
					if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
						msg = frnd.getContactName();
					else if(user.getUserProfile().getUserName() != null && 
							!user.getUserProfile().getUserName().isEmpty())
						msg = user.getUserProfile().getUserName();
					else
						msg = user.getContactNo();
				}
		
				msg +=" has added transactions with you.";
				System.out.println("==///=="+msg);
				/**
				 * end
				 */
				
				for(Transaction transaction : transactionDoc.getTransactions()){
					transaction.setSyncFlag(0);
					NotificationHelper.buildAndSendTransactionNotification(user, userB, transaction, transactionDoc, flag, msg, Constants.NOTIFICATION_TRANS_NEW);
				}
				
				
				
//				String msg = user.getUserProfile().getUserName()+" has added transactions with you.";
					 
/*				PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB.getUserId()), null, tn, msg);
*/				}
		}
		/***
		 * Sending promotional text message to unregistered user.
		 ***/
		else if(transactionDoc.getDocType() == Constants.NOT_REGISTERED_USER){
			UserMaster tempUser =  null;
			if(transactionDoc.getUser1().equals(user.getUserId()+"")){
				tempUser = UserDao.getUserForWeb(Long.parseLong(transactionDoc.getUser2()));
			}
			else{
				tempUser = UserDao.getUserForWeb(Long.parseLong(transactionDoc.getUser1()));
			}
			if(tempUser != null && tempUser.getSmsCount()< Constants.PROMOTIONAL_SMS_LIMIT && Constants.SMS_PACK_ACTIVE){	
				String strMsg = SMSHelper.generatePromotionalTransactionMessage(user, tempUser.getContactNo());
				
				List<Long> userli = tempUser.getValueMsgBy();
				if(userli.isEmpty()){

					String id =  SMSHelper.sendSms(tempUser.getContactNo(), strMsg, Constants.SMS_TYPE_PROMOTIONAL);
					UserDao.updateSmsCount(tempUser,user);
					SmsTable sms = new SmsTable();
					
					sms.setContactNo(tempUser.getContactNo());
					sms.setMsgId(id);
					sms.setType(Constants.SMS_TYPE_PROMOTIONAL);
					sms.setSenderId(user.getUserId());
					sms.setReceiverId(tempUser.getUserId());
					sms.setStatus("");
					System.out.println("FIRst Time sending");
					SmsDao.addNewUserRequest(sms);
					
				}else{
					System.out.println(" --->"+userli.contains(user.getUserId()));
				
				if( !tempUser.getValueMsgBy().contains(user.getUserId())){

					String id =  SMSHelper.sendSms(tempUser.getContactNo(), strMsg, Constants.SMS_TYPE_PROMOTIONAL);

					UserDao.updateSmsCount(tempUser,user);
					SmsTable sms = new SmsTable();
					
					sms.setContactNo(tempUser.getContactNo());
					sms.setMsgId(id);
					sms.setType(Constants.SMS_TYPE_PROMOTIONAL);
					sms.setSenderId(user.getUserId());
					sms.setReceiverId(tempUser.getUserId());
					sms.setStatus("");
					System.out.println("SECond Time sending");
					SmsDao.addNewUserRequest(sms);
				}
				}
			}



		}
		return stat;
	}
		
	
//	@SuppressWarnings("finally")
	public static TransactionDoc update(Transaction transaction, UserMaster user){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
		List<TransactionDoc> transDocs = null;
		ObjectMapper mapper = new ObjectMapper();
		UpdateResults ur =null;
		TransactionDoc transDoc = null;
		long epoch;
		int status=0;
		
		Transaction trnsact = getTransactionForWeb(transaction.getTransactionId(),transaction.getTransactionDocId(),user);
			query.or(
					query.criteria("user1").equal(""+user.getUserId()),
					query.criteria("user2").equal(""+user.getUserId())
					);
			query.field("_id").equal(new ObjectId(transaction.getTransactionDocId()));
			query.filter("transactions.transactionId",""+transaction.getTransactionId());
//			query.filter("transactions.createdBy",""+user.getUserId());
//			System.out.println("000:"+query.get());
			if(query.get() != null){
					   
					   transDocs = query.asList();
					 
				   epoch = System.currentTimeMillis();
				UpdateOperations<TransactionDoc> op = datastore.createUpdateOperations(TransactionDoc.class);
				
				ModificationRequest modReq = new ModificationRequest();
				modReq.setAction(Constants.TRANSACTION_UPDATE);
				modReq.setTransactionId(transaction.getTransactionId());
				String user1 = query.asList().get(0).getUser1();
				String user2 = query.asList().get(0).getUser2();
				if (user1.equals("" + user.getUserId())) {
					modReq.setForUser(user2);
					modReq.setEditedBy(""+user.getUserId());
				} else {
					modReq.setForUser(user1);
					modReq.setEditedBy(""+user.getUserId());
				}
				
				op.disableValidation();
				if (trnsact != null && !trnsact.getLastEditedBy().equals(""+modReq.getForUser()) ) {   //&& trnsact.getCreatedBy().equals("" + user.getUserId())
					
					transDoc = query.get();
					if(transDoc.getDocType() == 0)
						ModificationRequestDao.addModificationRequest(modReq, user);
					
					if(transDoc.getDocType() == 0){
						 List<Transaction> backed = TransactionDao.getTransactionForBackedUpTransaction(transaction.getTransactionId(),transaction.getTransactionDocId(),user);		
		//					System.out.println("==> "+mapper.writeValueAsString(backed));
			                if(backed == null){
			                	op.add("backedUptransactions", trnsact);
			                }
						// Transaction oldTransaction
		                transaction.setAction(Constants.TRANSACTION_UPDATE);    
		                op.set("transactions.$.action", Constants.TRANSACTION_UPDATE);
		                transaction.setTransactionStatus(Constants.TRANS_NEED_TO_APROOVE);
						op.set("transactions.$.transactionStatus",Constants.TRANS_NEED_TO_APROOVE);
						transaction.setLastEditedBy(""+user.getUserId());
						op.set("transactions.$.lastEditedBy", ""+user.getUserId());
						transaction.setModReqId(modReq.getId());
						op.set("transactions.$.modReqId", modReq.getId());
					}
					
					op.set("transactions.$.updatedTime", epoch);
					op.set("transactions.$.amount", transaction.getAmount());
					op.set("transactions.$.comment", transaction.getComment());
					op.set("transactions.$.transactionDate",transaction.getTransactionDate());
					op.enableValidation();
						
					ur = datastore.update(query, op);
					
					try {
						System.out.println("respone : "+mapper.writeValueAsString(ur));
					} catch (Exception e) {
						e.printStackTrace();
					}
					status = ur.getUpdatedCount();
					if(status > 0){
						transaction.setSyncFlag(0);
					//// reset transaction doc amount and pay status.
					try {
					
						double amount = calculateAmt(transDoc.getIdString()) + transDocs.get(0).getOpeningBalAmt();
						if(amount >=0){
							transDoc.setAmount(amount);
							transDoc.setPaymentStatus(Constants.TO_TAKE);
						}else{
							transDoc.setAmount(amount * (-1));
							transDoc.setPaymentStatus(Constants.TO_GIVE);
						}
							
						UpdateOperations<TransactionDoc> opAmt = datastore.createUpdateOperations(TransactionDoc.class);
						opAmt.disableValidation();
						opAmt.set("amount", transDoc.getAmount());
						opAmt.set("paymentStatus", transDoc.getPaymentStatus());
						opAmt.set("updatedTime", epoch);
						
						UpdateResults ures = datastore.update(query,opAmt );
						if(ures != null && ures.getUpdatedCount()>0){
							FriendsDao.updateAmount(transDoc.getUser1(), transDoc.getUser2(), transDoc);
							FriendsDao.updateAmount(transDoc.getUser2(), transDoc.getUser1(), transDoc);
						}

					} catch (Exception e) {
						System.out.println("EXCEPTION : "+e.getMessage());
					}
						
						transDoc.setTransactions(Arrays.asList(transaction));
						transDoc.setBackedUptransactions(null);
	//					TransactionDao.updateTransactionSql(transaction,epoch,Constants.TRANS_NEED_TO_APROOVE);
						if(transDoc.getDocType() == 0)
							TransactionDao.addNewUpdatedTransaction(transaction,epoch);
						
						
						try {
							System.out.println("==>" + mapper.writeValueAsString(ur));
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
		
						
						if(transDoc.getDocType() == 0){
						/**
						 * add Notification code.
						 * **/
		
	
						boolean flag = false;
						UserMaster userB = null;
						
							
								if (transDoc.getUser1().equals(user.getUserId() + "")) {
									userB = UserDao.getUserForWeb(Long.parseLong(transDoc.getUser2()));
									flag = false;
								} else {
									userB = UserDao.getUserForWeb(Long.parseLong(transDoc.getUser1()));
									flag = true;
								}
							
							if (userB != null && userB.getUserId() != 0) {
						/*		TransactionNotification tn = new TransactionNotification();
								tn.setNotificationType(Constants.NOTIFICATION_TRANS_UPDATE);
								tn.setTransaction(transaction);
								tn.setAmount(transactionDoc.getAmount());
								tn.setUserId(""+userB.getUserId());
								if (flag)
									tn.setPaymentStatus(transactionDoc.getPaymentStatus());
								else {
									if (transactionDoc.getPaymentStatus() == Constants.TO_GIVE)
										tn.setPaymentStatus(Constants.TO_TAKE);
									else
										tn.setPaymentStatus(Constants.TO_GIVE);
	
									}
	*/								/**
									 * Notification message
									 **/
									FriendContact frnd = null; 
									frnd = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, userB);
									String msg = "";
									if(frnd != null)
									{
										if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
											msg = frnd.getContactName();
										else if(user.getUserProfile().getUserName() != null && 
												!user.getUserProfile().getUserName().isEmpty())
											msg = user.getUserProfile().getUserName();
										else
											msg = user.getContactNo();
									}
							
									msg +=" has updated transactions.";
									System.out.println("==///=="+msg);
									/**
									 * end
									 * */
								/*	String msg = user.getUserProfile().getUserName()+ " has updated transactions.";
									*/
	/*								PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB.getUserId()), null,tn, msg);*/
									
									NotificationHelper.buildAndSendTransactionNotification(user, userB, transaction, transDoc, flag, msg, Constants.NOTIFICATION_TRANS_UPDATE);
								
							}
						}
					}
				}
				
			
			}
		return transDoc;
	}

	
	
	public static void syncUpdate(List<Transaction> transactionList,
			UserMaster user) {
		System.out.println("in sync update ... ");
		Datastore datastore = MorphiaDatastoreTrasaction
				.getDatastore(TransactionDoc.class);
		for (Transaction transaction : transactionList) {
			if(ObjectId.isValid(transaction.getTransactionDocId())){
				
			Query<TransactionDoc> query = datastore
					.createQuery(TransactionDoc.class);
			List<TransactionDoc> transDocs = null;
			ObjectMapper mapper = new ObjectMapper();
			UpdateResults ur = null;
			TransactionDoc transDoc = new TransactionDoc();
			long epoch;
			int status = 0;

			Transaction trnsact = getTransactionForWeb(
					transaction.getTransactionId(),
					transaction.getTransactionDocId(), user);
			query.or(
					query.criteria("user1").equal(""+user.getUserId()),
					query.criteria("user2").equal(""+user.getUserId())
					);
			
			query.field("_id").equal(
					new ObjectId(transaction.getTransactionDocId()));
			query.filter("transactions.transactionId",
					"" + transaction.getTransactionId());
			
//			query.filter("transactions.createdBy", "" + user.getUserId());
			 System.out.println("44:"+query.asList());
			if (query.get() != null) {
				transDoc = query.get();
				transDocs = query.asList();
			
			epoch = System.currentTimeMillis();
			
			
			ModificationRequest modReq = new ModificationRequest();
			ModificationRequest modReqValidator = null;
			modReq.setAction(Constants.TRANSACTION_UPDATE);
			modReq.setTransactionId(transaction.getTransactionId());
			String user1 = query.asList().get(0).getUser1();
			String user2 = query.asList().get(0).getUser2();
			if (user1.equals("" + user.getUserId())) {
				modReq.setForUser(user2);
				modReq.setEditedBy(""+user.getUserId());
			} else {
				modReq.setForUser(user1);
                modReq.setEditedBy(""+user.getUserId());			
			}
			modReqValidator = ModificationRequestDao.getModificationRequestFor(transaction.getTransactionId());
			
			if ((modReqValidator != null
					&& modReqValidator.getStatus() != 0 ) || modReqValidator == null) {

				if (transDoc.getDocType() == 0)
					ModificationRequestDao.addModificationRequest(modReq, user);

				if (transDoc.getDocType() == 0) {
					/*List<Transaction> backed = TransactionDao
							.getTransactionForBackedUpTransaction(
									transaction.getTransactionId(),
									transaction.getTransactionDocId(), user);
					// System.out.println("==> "+mapper.writeValueAsString(backed));
					if (backed == null) {
						op.add("backedUptransactions", trnsact);
					}*/
					// Transaction oldTransaction
					UpdateOperations<TransactionDoc> op = datastore
							.createUpdateOperations(TransactionDoc.class);
					op.disableValidation();
					transaction.setAction(Constants.TRANSACTION_UPDATE);    
	                transaction.setTransactionStatus(Constants.TRANS_NEED_TO_APROOVE);
					transaction.setLastEditedBy(""+user.getUserId());
					transaction.setModReqId(modReq.getId());
					transaction.setUpdatedTime(epoch);
					transaction.setCreatedTime(epoch);
					op.add("modifiedTransactions", transaction);
					op.enableValidation();

					ur = datastore.update(query, op);
					
					
				}
				else if(transDoc.getDocType() == 5){
					
				}else{
					UpdateOperations<TransactionDoc> op = datastore
							.createUpdateOperations(TransactionDoc.class);
					op.disableValidation();
					op.set("transactions.$.updatedTime", epoch);
					op.set("transactions.$.amount", transaction.getAmount());
					op.set("transactions.$.comment", transaction.getComment());
					op.set("transactions.$.transactionDate",transaction.getTransactionDate());
					op.enableValidation();

					ur = datastore.update(query, op);
				}
				

				try {
					System.out.println(mapper.writeValueAsString(ur));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(ur != null)
					status = ur.getUpdatedCount();
				if (status > 0) {
					transaction.setSyncFlag(0);
					// // reset transaction doc amount and pay status.
					try {

						double amount = calculateAmt(transDoc.getIdString()) + transDoc.getOpeningBalAmt();
						if (amount >= 0) {
							transDoc.setAmount(amount);
							transDoc.setPaymentStatus(Constants.TO_TAKE);
						} else {
							transDoc.setAmount(amount * (-1));
							transDoc.setPaymentStatus(Constants.TO_GIVE);
						}

						UpdateOperations<TransactionDoc> opAmt = datastore
								.createUpdateOperations(TransactionDoc.class);
						opAmt.disableValidation();
						opAmt.set("amount", transDoc.getAmount());
						opAmt.set("paymentStatus", transDoc.getPaymentStatus());
						opAmt.set("updatedTime", epoch);

						UpdateResults ures = datastore.update(query, opAmt);
						if (ures != null && ures.getUpdatedCount() > 0) {
							FriendsDao.updateAmount(transDoc.getUser1(),
									transDoc.getUser2(), transDoc);
							FriendsDao.updateAmount(transDoc.getUser2(),
									transDoc.getUser1(), transDoc);
						}
					} catch (Exception e) {
						System.out.println("EXCEPTION : " + e.getMessage());
					}
					// end of transaction doc amount calculation

					transaction.setSyncFlag(0);
					transDoc.setTransactions(Arrays.asList(transaction));
					transDoc.setBackedUptransactions(null);
					// TransactionDao.updateTransactionSql(transaction,epoch,Constants.TRANS_NEED_TO_APROOVE);
					if (transDoc.getDocType() == 0)
						TransactionDao.addNewUpdatedTransaction(transaction,epoch);

					try {
						System.out.println("==>"
								+ mapper.writeValueAsString(ur));
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}

					/**
					 * add Notification code.
					 * **/

					boolean flag = false;
					UserMaster userB = null;

					if (transDocs != null && !transDocs.isEmpty()) {
						TransactionDoc transactionDoc = transDocs.get(0);
						if (transactionDoc.getDocType() == 0) {
							if (transactionDoc.getUser1().equals(
									user.getUserId() + "")) {
								userB = UserDao.getUserForWeb(Long
										.parseLong(transactionDoc.getUser2()));
								flag = false;
							} else {
								userB = UserDao.getUserForWeb(Long
										.parseLong(transactionDoc.getUser1()));
								flag = true;
							}
						}
						if (userB != null && userB.getUserId() != 0) {
				/*			TransactionNotification tn = new TransactionNotification();
							tn.setNotificationType(Constants.NOTIFICATION_TRANS_UPDATE);
							tn.setTransaction(transaction);
							tn.setAmount(transactionDoc.getAmount());
							tn.setUserId("" + userB.getUserId());
							if (flag)
								tn.setPaymentStatus(transactionDoc
										.getPaymentStatus());
							else {
								if (transactionDoc.getPaymentStatus() == Constants.TO_GIVE)
									tn.setPaymentStatus(Constants.TO_TAKE);
								else
									tn.setPaymentStatus(Constants.TO_GIVE);
							}*/
							/**
							 * Notification message
							 **/
							FriendContact frnd = null;
							frnd = FriendsDao.getFriendForWeb(
									"" + user.getUserId(), 0, userB);
							String msg = "";
							if (frnd != null) {
								if (frnd.getContactName() != null
										&& !frnd.getContactName().isEmpty())
									msg = frnd.getContactName();
								else if (user.getUserProfile().getUserName() != null
										&& !user.getUserProfile().getUserName()
												.isEmpty())
									msg = user.getUserProfile().getUserName();
								else
									msg = user.getContactNo();
							}

							msg += " has updated transactions.";
							System.out.println("==///==" + msg);
							/**
							 * end
							 * */

							/*
							 * String msg = user.getUserProfile().getUserName()+
							 * " has updated transactions.";
							 */
							/*PushNotificationControler.sendNotificationsToUser(
									Arrays.asList(userB.getUserId()), null, tn,
									msg);*/
							
							NotificationHelper.buildAndSendTransactionNotification(user, userB, transaction, transactionDoc, flag, msg, Constants.NOTIFICATION_STAFF_TRANS_UPDATE);
								}
							}
						}
					}
				
				}
			}
		}	
	}

	
	
	public static TransactionDoc updateTransaction2(Transaction transaction, UserMaster user){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
		List<TransactionDoc> transDocs = null;
		ObjectMapper mapper = new ObjectMapper();
		UpdateResults ur =null;
		TransactionDoc transDoc = null;
		long epoch;
		int status=0;
		
		Transaction trnsact = getTransactionForWeb(transaction.getTransactionId(),transaction.getTransactionDocId(),user);
			query.or(
					query.criteria("user1").equal(""+user.getUserId()),
					query.criteria("user2").equal(""+user.getUserId())
					);
			query.field("_id").equal(new ObjectId(transaction.getTransactionDocId()));
			query.filter("transactions.transactionId",""+transaction.getTransactionId());
//			query.filter("transactions.createdBy",""+user.getUserId());
//			System.out.println("000:"+query.get());
			if(query.get() != null){
					   
					   transDocs = query.asList();
					 
				   epoch = System.currentTimeMillis();
				
				
				ModificationRequest modReq = new ModificationRequest();
				ModificationRequest modReqValidator = new ModificationRequest();
				modReq.setAction(Constants.TRANSACTION_UPDATE);
				modReq.setTransactionId(transaction.getTransactionId());
				String user1 = query.asList().get(0).getUser1();
				String user2 = query.asList().get(0).getUser2();
				if (user1.equals("" + user.getUserId())) {
					modReq.setForUser(user2);
					modReq.setEditedBy(""+user.getUserId());
				} else {
					modReq.setForUser(user1);
					modReq.setEditedBy(""+user.getUserId());
				}
				
				
				modReqValidator = ModificationRequestDao.getModificationRequestFor(transaction.getTransactionId());
				if ((modReqValidator != null
						&& modReqValidator.getStatus() != 0 ) || modReqValidator == null) {   //&& trnsact.getCreatedBy().equals("" + user.getUserId())
					
					transDoc = query.get();
						
					
					if(transDoc.getDocType() == 0){
						UpdateOperations<TransactionDoc> op = datastore.createUpdateOperations(TransactionDoc.class);
						op.disableValidation();
						ModificationRequestDao.addModificationRequest(modReq, user);
						List<Transaction> backed = TransactionDao.getTransactionForBackedUpTransaction(transaction.getTransactionId(),transaction.getTransactionDocId(),user);		

						transaction.setAction(Constants.TRANSACTION_UPDATE);    
		                transaction.setTransactionStatus(Constants.TRANS_NEED_TO_APROOVE);
						transaction.setLastEditedBy(""+user.getUserId());
						transaction.setModReqId(modReq.getId());
						transaction.setUpdatedTime(epoch);
						transaction.setCreatedTime(epoch);
						
						op.add("modifiedTransactions", transaction);
						op.enableValidation();
						
						ur = datastore.update(query, op);
				
					}
					else if(transDoc.getDocType() == 5){
						
					}else{
						UpdateOperations<TransactionDoc> op = datastore.createUpdateOperations(TransactionDoc.class);
						op.disableValidation();
						op.set("transactions.$.updatedTime", epoch);
						op.set("transactions.$.amount", transaction.getAmount());
						op.set("transactions.$.comment", transaction.getComment());
						op.set("transactions.$.transactionDate",transaction.getTransactionDate());
						op.enableValidation();
						
						ur = datastore.update(query, op);
					}
					
					
					try {
						System.out.println("respone : "+mapper.writeValueAsString(ur));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(ur != null)
					status = ur.getUpdatedCount();
					if(status > 0){
						transaction.setSyncFlag(0);
					//// reset transaction doc amount and pay status.
						
					if(transDoc.getDocType() == 0)
						TransactionDao.addNewUpdatedTransaction(transaction,epoch);
					else{
						try {
						
							double amount = calculateAmt(transDoc.getIdString()) + transDocs.get(0).getOpeningBalAmt();
							if(amount >=0){
								transDoc.setAmount(amount);
								transDoc.setPaymentStatus(Constants.TO_TAKE);
							}else{
								transDoc.setAmount(amount * (-1));
								transDoc.setPaymentStatus(Constants.TO_GIVE);
							}
								
							UpdateOperations<TransactionDoc> opAmt = datastore.createUpdateOperations(TransactionDoc.class);
							opAmt.disableValidation();
							opAmt.set("amount", transDoc.getAmount());
							opAmt.set("paymentStatus", transDoc.getPaymentStatus());
							opAmt.set("updatedTime", epoch);
							
							UpdateResults ures = datastore.update(query,opAmt );
							if(ures != null && ures.getUpdatedCount()>0){
								FriendsDao.updateAmount(transDoc.getUser1(), transDoc.getUser2(), transDoc);
								FriendsDao.updateAmount(transDoc.getUser2(), transDoc.getUser1(), transDoc);
							}
	
						} catch (Exception e) {
							System.out.println("EXCEPTION : "+e.getMessage());
						}
					}						
						
	//					TransactionDao.updateTransactionSql(transaction,epoch,Constants.TRANS_NEED_TO_APROOVE);
												
						
						try {
							System.out.println("==>" + mapper.writeValueAsString(ur));
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
		
						
						if(transDoc.getDocType() == 0){
						/**
						 * add Notification code.
						 * **/
		
	
						boolean flag = false;
						UserMaster userB = null;
						
							
								if (transDoc.getUser1().equals(user.getUserId() + "")) {
									userB = UserDao.getUserForWeb(Long.parseLong(transDoc.getUser2()));
									flag = false;
								} else {
									userB = UserDao.getUserForWeb(Long.parseLong(transDoc.getUser1()));
									flag = true;
								}
							
							if (userB != null && userB.getUserId() != 0) {
						/*		TransactionNotification tn = new TransactionNotification();
								tn.setNotificationType(Constants.NOTIFICATION_TRANS_UPDATE);
								tn.setTransaction(transaction);
								tn.setAmount(transactionDoc.getAmount());
								tn.setUserId(""+userB.getUserId());
								if (flag)
									tn.setPaymentStatus(transactionDoc.getPaymentStatus());
								else {
									if (transactionDoc.getPaymentStatus() == Constants.TO_GIVE)
										tn.setPaymentStatus(Constants.TO_TAKE);
									else
										tn.setPaymentStatus(Constants.TO_GIVE);
	
									}
	*/								/**
									 * Notification message
									 **/
									FriendContact frnd = null; 
									frnd = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, userB);
									String msg = "";
									if(frnd != null)
									{
										if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
											msg = frnd.getContactName();
										else if(user.getUserProfile().getUserName() != null && 
												!user.getUserProfile().getUserName().isEmpty())
											msg = user.getUserProfile().getUserName();
										else
											msg = user.getContactNo();
									}
							
									msg +=" has updated transactions.";
									System.out.println("==///=="+msg);
									/**
									 * end
									 * */
								/*	String msg = user.getUserProfile().getUserName()+ " has updated transactions.";
									*/
	/*								PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB.getUserId()), null,tn, msg);*/
									
									NotificationHelper.buildAndSendTransactionNotification(user, userB, transaction, transDoc, flag, msg, Constants.NOTIFICATION_TRANS_UPDATE);
								
							}
						}
					}
					transDoc.setTransactions(null);
					transDoc.setModifiedTransactions(Arrays.asList(transaction));
					transDoc.setBackedUptransactions(null);
				}
				
			
			}
		return transDoc;
	}

	
	
	
	
	
	
	public static void addNewUpdatedTransaction(Transaction transaction,long epoch) {
		Session session = null;
		TransactionSql trnsql = new TransactionSql();
		TransactionSql trnssql = new TransactionSql();
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx=null;
			Criteria criteria = session.createCriteria(TransactionSql.class);
			criteria.add(Restrictions.eq("transactionId", transaction.getTransactionId()));
			criteria.add(Restrictions.eq("transactionStatus", Constants.TRANS_NEED_TO_APROOVE));
			if(criteria.list().size()>0){
				trnssql = (TransactionSql) criteria.list().get(0);
				tx = session.beginTransaction();
				String updatetransac = "update TransactionSql set from1 = :from1, to = :to"
						+ ", amount = :amount, type = :type"
						+ ", comment = :comment, createdTime = :createdTime"
						+ ", updatedTime = :updatedTime, createdBy = :createdBy"
						+ ", readFlag = :readFlag, transactionStatus = :transactionStatus"
						+ ", action = :action, receivedTime = :receivedTime"
						+ ", readTime = :readTime, modReqId = :modReqId"
						+ ", readStatus = :readStatus, transactionDocId = :transactionDocId"
						+ ",lastEditedBy = :lastEditedBy"
						+ " where id = :id ";
				org.hibernate.Query upq = session.createQuery(updatetransac);
				upq.setParameter("from1", transaction.getFrom());
				upq.setParameter("to", transaction.getTo());
				upq.setParameter("amount", transaction.getAmount());
				upq.setParameter("type", transaction.getType());
				upq.setParameter("comment", transaction.getComment());
				upq.setParameter("createdTime", transaction.getCreatedTime());
				upq.setParameter("updatedTime", epoch);
				upq.setParameter("createdBy", transaction.getCreatedBy());
				upq.setParameter("readFlag", transaction.getReadFlag());
				upq.setParameter("transactionStatus", transaction.getTransactionStatus());
				upq.setParameter("action", transaction.getAction());
				upq.setParameter("receivedTime", transaction.getReceivedTime());
				upq.setParameter("readTime", transaction.getReadTime());
				upq.setParameter("modReqId", transaction.getModReqId());
				upq.setParameter("readStatus", transaction.getReadStatus());
				upq.setParameter("transactionDocId", transaction.getTransactionDocId());
				upq.setParameter("id", trnssql.getId());
				upq.setParameter("lastEditedBy", transaction.getLastEditedBy());
				int status = upq.executeUpdate();
				System.out.println("-- : "+status);
				tx.commit();
			}else
			{
				tx = session.beginTransaction();
		       
				if(transaction.getFrom().equals(transaction.getCreatedBy())){
					trnsql.setUserId(transaction.getTo());
				}else{
					trnsql.setUserId(transaction.getFrom());
				}
				trnsql.setAmount(transaction.getAmount());
				trnsql.setComment(transaction.getComment());
				trnsql.setCreatedBy(transaction.getCreatedBy());
				trnsql.setCreatedTime(transaction.getCreatedTime());
				trnsql.setDisputeAmount(transaction.getDisputeAmount());
				trnsql.setDisputeBy(transaction.getDisputeBy());
				trnsql.setDisputeFlag(transaction.getDisputeFlag());
				trnsql.setFrom(transaction.getFrom());
				trnsql.setTo(transaction.getTo());
				trnsql.setTransactionDate(transaction.getTransactionDate());
				trnsql.setTransactionId(transaction.getTransactionId());
				trnsql.setType(transaction.getType());
				trnsql.setAction(transaction.getAction());
				trnsql.setTransactionStatus(transaction.getTransactionStatus());
				trnsql.setUpdatedTime(transaction.getUpdatedTime());
				trnsql.setTransactionDocId(transaction.getTransactionDocId());
				trnsql.setStaffUser(transaction.getStaffUser());;
				trnsql.setRefTransacId(transaction.getRefTransacId());
				trnsql.setReadStatus(transaction.getReadStatus());
				trnsql.setReadTime(transaction.getReadTime());
				trnsql.setReceivedTime(transaction.getReceivedTime());
				trnsql.setReadFlag(transaction.getReadFlag());
				trnsql.setModReqId(transaction.getModReqId());
				trnsql.setLastEditedBy(transaction.getLastEditedBy());
				trnsql.setSrNo(transaction.getSrNo());
				session.save(trnsql);
			
			tx.commit();
			}
						
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			
			e.printStackTrace();
			
		} finally {
			session.close();
		}
	}


	/* Update transcation sql
	 */  
	  /* private static void updateTransactionSql(Transaction transaction,long epoch,
	
			int transNeedToAproove) {
		
		Session session = null;
		TransactionSql trnsql = new TransactionSql();
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx=null;
			String updateHql = "update TransactionSql set  updatedTime = :epoch1, comment = :comment1,"
					+ " transactionDate = :transactionDate1,amount = :amount1,transactionStatus = :transNeedToAproove "+
					"WHERE transactionId = :transactionId1";
			tx = session.beginTransaction();
			org.hibernate.Query query =session.createQuery(updateHql); 
			query.setParameter("epoch1",epoch );
			query.setParameter("comment1", transaction.getComment());
			query.setParameter("transactionDate1", transaction.getTransactionDate());
			query.setParameter("amount1", transaction.getAmount());
			query.setParameter("transNeedToAproove", transNeedToAproove);
			query.setParameter("transactionId1", transaction.getTransactionId());
			
			query.executeUpdate();
			tx.commit();
						
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		
		// TODO Auto-generated method stub
		
	}*/


	public static List<Transaction> pullTransactions(UserMaster user, long pullTime){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
		List<TransactionDoc> transactionDocs = new ArrayList<TransactionDoc>();
		List<Transaction> transactions = new ArrayList<Transaction>();
		if(pullTime > 0){
			query.and(
					query.or(query.criteria("user1").equal(""+user.getUserId()),query.criteria("user2").equal(""+user.getUserId())),
				    query.criteria("updatedTime").greaterThan(pullTime)
			);    
		}else{
			query.or(query.criteria("user1").equal(""+user.getUserId()),query.criteria("user2").equal(""+user.getUserId()));
		}
				
		if(!query.asList().isEmpty()){
			
			transactionDocs = query.asList();
			
			for(TransactionDoc trans : transactionDocs){
				transactions.addAll(trans.getTransactions());
			}
		}
		
		return transactions;
	}
	
	
	
	
		public static List<Transaction> pullTransactions2(UserMaster user, long pullTime){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 List<Transaction> flist = new ArrayList<Transaction>();
			//	 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
				  
				 BasicDBList idList = new BasicDBList();
				 idList.add(new BasicDBObject("user1",""+user.getUserId()));
				 idList.add(new BasicDBObject("user2",""+user.getUserId()));
				 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("$or", idList));
				  
				  BasicDBList paramList = new BasicDBList();
//				  paramList.add(new BasicDBObject("transactions.createdTime", new BasicDBObject("$gt",pullTime)));
//				  paramList.add(new BasicDBObject("transactions.transactionStatus", Constants.TRANS_APROOVED));
//				 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("$and", paramList));
				 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("transactions.updatedTime", new BasicDBObject("$gt",pullTime)));
				  DBObject gdb1 = new BasicDBObject();
				  gdb1.put("_id","$_id");
				  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
				  DBObject group = new BasicDBObject("$group", gdb1);
				  DBObject project = new BasicDBObject("$unwind", "$transactions");
				  AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,project,match1, group);
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
	
		public static List<Transaction> pullModifiedTransactions(UserMaster user, long pullTime){
			 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
			 List<Transaction> flist = new ArrayList<Transaction>();
				//	 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
					  
					 BasicDBList idList = new BasicDBList();
					 idList.add(new BasicDBObject("user1",""+user.getUserId()));
					 idList.add(new BasicDBObject("user2",""+user.getUserId()));
					 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("$or", idList));
					  
					  BasicDBList paramList = new BasicDBList();
//					  paramList.add(new BasicDBObject("transactions.createdTime", new BasicDBObject("$gt",pullTime)));
//					  paramList.add(new BasicDBObject("transactions.transactionStatus", Constants.TRANS_APROOVED));
//					 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("$and", paramList));
					 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("modifiedTransactions.updatedTime", new BasicDBObject("$gt",pullTime)));
					  DBObject gdb1 = new BasicDBObject();
					  gdb1.put("_id","$_id");
					  gdb1.put("transactions",new BasicDBObject("$push","$modifiedTransactions"));
					  DBObject group = new BasicDBObject("$group", gdb1);
					  DBObject project = new BasicDBObject("$unwind", "$modifiedTransactions");
					  AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,project,match1, group);
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
		
		
		
		
	/**
	 * Used for fetching all docs of a user,
	 * being used for setting frndlist for user who was previously
	 * added as unmanaged user.
	 * */
	public static List<TransactionDoc> updateTransactionDocsForUnmangedUser(UserMaster user){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
		List<TransactionDoc> transactionDocs = new ArrayList<TransactionDoc>();
	
		query.or(query.criteria("user1").equal(""+user.getUserId()),query.criteria("user2").equal(""+user.getUserId()));
		query.filter("docType", Constants.NOT_REGISTERED_USER);		
		if(!query.asList().isEmpty()){
			
			transactionDocs = query.asList();
			
			UpdateOperations<TransactionDoc> op = datastore.createUpdateOperations(TransactionDoc.class);
			
			op.set("docType", 0);
			op.set("updatedTime", System.currentTimeMillis());
			
			datastore.update(query,op );
			
			
		}
		
		return transactionDocs;
	}
	
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
//		disputeTransaction(null, new UserMaster());
		UserMaster user = new UserMaster();
		user.setUserId(4);
		user.setOwnerId(0);
		pullReadTransactions(user,0);
//		deleteTransactionSqlOnBlock("1",user) ;
//		calculateAmt("57ea2153438db5d9b3b6e1ef");
//		getTransactionListForWeb(Arrays.asList("1_2_1", "2_3_1","1_2_2"));
		
//		getTransactionForWeb("4_14_3", "57f342b0fe1db01b503840fw", user);
		
		/*OpeningBalRequest ob = new OpeningBalRequest();
		ob.setOpeningBalDate(Long.parseLong("13213213213"));
		ob.setRequesterUserId("12");
		ob.setForUserId("1");
		System.out.println(checkForOpeninigBalDate(ob, 0));*/
//		getModifiedTransactionById("2_4_2", user);
//		System.out.println("00 : "+checkForTransactionDateForInsert(Long.parseLong("1567246572"),"585bbdd614d4c66e05d965d9"));
//		pullPrivateUserTransaction(user,Long.parseLong("1482406531851"));

//		getBackedupTransactionListForUser(Arrays.asList("3_4_2","1_4_2","1_2_1"), "4");
		
//		updateReadStatusForTransactionTest(Arrays.asList("1_2_1", "2_3_1","1_2_2"), user, 1, new ArrayList<String>());
/*		long time = Long.parseLong("1474961746564");
		List<Transaction> transList = getTransactionForStaff(user,time);
		System.out.println(transList.size());
*///		getTransactionForBackedUpTransaction("4_3_2","57ea345484d1445b217fe8ad",null);
//		deleteTransaction(null,null,null);
//		deleteTransactionInSql();
//		pullModifiedTransactions(user, Long.parseLong("1482408242512"));
		
//		System.out.println(checkForTransactionDateForInsert("", ""));
		
	}

	public static void addTransactiontoSql(List<Transaction> transactionslist) {
		Session session = null;
		TransactionSql trnsql = new TransactionSql();
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx=null;
			tx = session.beginTransaction();
			for(Transaction trnc : transactionslist){
				if(trnc.getFrom().equals(trnc.getCreatedBy())){
					trnsql.setUserId(trnc.getTo());
				}else{
					trnsql.setUserId(trnc.getFrom());
				}
				trnsql.setAmount(trnc.getAmount());
				trnsql.setComment(trnc.getComment());
				trnsql.setCreatedBy(trnc.getCreatedBy());
				trnsql.setCreatedTime(trnc.getCreatedTime());
				trnsql.setDisputeAmount(trnc.getDisputeAmount());
				trnsql.setDisputeBy(trnc.getDisputeBy());
				trnsql.setDisputeFlag(trnc.getDisputeFlag());
				trnsql.setFrom(trnc.getFrom());
				trnsql.setTo(trnc.getTo());
				trnsql.setTransactionDate(trnc.getTransactionDate());
				trnsql.setTransactionId(trnc.getTransactionId());
				trnsql.setType(trnc.getType());
				trnsql.setUpdatedTime(trnc.getUpdatedTime());
				trnsql.setAction(trnc.getAction());
				trnsql.setReadFlag(trnc.getReadFlag());
				trnsql.setTransactionStatus(trnc.getTransactionStatus());
				trnsql.setTransactionDocId(trnc.getTransactionDocId());
				trnsql.setStaffUser(trnc.getStaffUser());;
				trnsql.setRefTransacId(trnc.getRefTransacId());
				trnsql.setReadStatus(trnc.getReadStatus());
				trnsql.setReadTime(trnc.getReadTime());
				trnsql.setReceivedTime(trnc.getReceivedTime());
				trnsql.setLastEditedBy(trnc.getLastEditedBy());
				trnsql.setModReqId(trnc.getModReqId());
				trnsql.setSrNo(trnc.getSrNo());
				session.save(trnsql);
			}
			tx.commit();
						
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		
	}
	
	
	
	public static boolean markTransactionsAsReadInSql(List<String> transIds, UserMaster user, int readStatus ){
		Session session = null;
		org.hibernate.Transaction tx = null;
		long epoch = System.currentTimeMillis();
		boolean flag = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = "Update TransactionSql set readStatus = :readFlag, updatedTime = :updatedDate "
					        + " where transactionId IN (:transId) and userId = :userId ";
			org.hibernate.Query query = session.createQuery(hql);
			query.setParameterList("transId", transIds);
			query.setParameter("readFlag", readStatus);
			query.setParameter("updatedDate", epoch);
			query.setParameter("userId", ""+user.getUserId());
			int i = query.executeUpdate();
			if(i>0){
				flag = true;
			}
			tx.commit();	
		}
		catch (Exception e) {
			System.out.println("Exception = "+e.getMessage());
			if(tx != null)
				tx.rollback();
			e.printStackTrace();
		}finally{
			session.close();
		}
		return flag;
	}
	public static List<Transaction> pullTransactionsSql(UserMaster user,Long pullTime, int transStatus){
		Session session = null;
		List<Transaction> listtrans = new ArrayList<Transaction>();
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx=null;
			tx = session.beginTransaction();
			Criteria q=session.createCriteria(TransactionSql.class);
			q.add(Restrictions.or(Restrictions.eq("from1", ""+user.getUserId()),Restrictions.eq("to", ""+user.getUserId())));
			q.add(Restrictions.eq("readFlag", 0));
			q.add(Restrictions.eq("transactionStatus", transStatus));
			q.add(Restrictions.gt("updatedTime", pullTime));
			if(q.list().size()>0){
				listtrans=(ArrayList<Transaction>)q.list();
			}
			tx.commit();
						
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return listtrans;
	}
	
	
	
	
	
    //delete transaction from TransactionSql whose created time is more than 5 days
	public static void deleteTransactionInSql(){
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx=null;
			tx = session.beginTransaction();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -5);
			System.out.println(""+cal.getTimeInMillis());
			org.hibernate.Query query=session.createQuery("DELETE FROM TransactionSql WHERE createdTime < :time3");
			query.setParameter("time3", cal.getTimeInMillis());
			query.executeUpdate();
			tx.commit();
						
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			
			e.printStackTrace();
			
		} finally {
			session.close();
		}
	}

	
	public static TransactionDoc disputeTransaction(TransactionDisputeBean transacDispBean, UserMaster user){
		ObjectMapper mapper = new ObjectMapper();
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
//		user.setUserId();
		Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
		TransactionSql trnsql = new TransactionSql();
		TransactionDoc transDoc = null;
		query.field("_id").equal(new ObjectId(transacDispBean.getTransactionDocId()));
		query.or(query.criteria("user1").equal(""+user.getUserId()),query.criteria("user2").equal(""+user.getUserId()));
		query.field("transactions.transactionId").equal(transacDispBean.getTransactionId());
		if(!query.asList().isEmpty()){
			transDoc = query.get();

			Transaction transac = getTransactionForWeb(transacDispBean.getTransactionId(), transacDispBean.getTransactionDocId(), user);
			int disputeFlag = 1;
			
			long epoch = System.currentTimeMillis();
			if(transac.getCreatedBy().equals(""+user.getUserId())){
			if(transacDispBean.getDisputeAmount() == transac.getDisputeAmount()){
				disputeFlag = 0;
			}else{
				disputeFlag = 1;
			}		
			}else if(!transac.getCreatedBy().equals(""+user.getUserId())){
				if(transacDispBean.getDisputeAmount() == transac.getAmount() ){
					disputeFlag = 0;
				}else{
					disputeFlag = 1;
				}
			}
			transac.setDisputeFlag(disputeFlag);
			
			UpdateOperations<TransactionDoc> op = datastore.createUpdateOperations(TransactionDoc.class);
			op.disableValidation();
			
			if(transac.getCreatedBy().equals(""+user.getUserId())){
				if(disputeFlag == 0){
					op.set("transactions.$.disputeAmount", 0);
					transac.setAmount(transacDispBean.getDisputeAmount());
					transac.setDisputeAmount(0);
				}
				else
				transac.setAmount(transacDispBean.getDisputeAmount());
				op.set("transactions.$.amount", transacDispBean.getDisputeAmount());
				op.set("transactions.$.disputeFlag", disputeFlag);
				op.set("transactions.$.updatedTime", epoch);
				
			}
			else{
				if(disputeFlag == 0){
				
					op.set("transactions.$.disputeAmount", 0);
					transac.setAmount(transacDispBean.getDisputeAmount());
					transac.setDisputeAmount(0);
				}
				else{
					op.set("transactions.$.disputeAmount", transacDispBean.getDisputeAmount());
					transac.setDisputeAmount(transacDispBean.getDisputeAmount());
				}
				op.set("transactions.$.disputeFlag", disputeFlag);
				op.set("transactions.$.updatedTime", epoch);
				op.set("transactions.$.disputeBy", user.getUserId());
				transac.setDisputeBy(""+user.getUserId());
			}
			transac.setUpdatedTime(epoch);
			
			op.enableValidation();
				UpdateResults ur = datastore.update(query,op );
				
				if(ur.getUpdatedCount()>0){
					
				//// reset transaction doc amount and pay status.
					double total = 0;
					double amount = calculateAmt(transacDispBean.getTransactionDocId());
					total = amount + transDoc.getOpeningBalAmt();
					if(total >=0){
						transDoc.setAmount(total);
						transDoc.setPaymentStatus(Constants.TO_TAKE);
					}else{
						transDoc.setAmount(total * (-1));
						transDoc.setPaymentStatus(Constants.TO_GIVE);
					}
						
					UpdateOperations<TransactionDoc> opAmt = datastore.createUpdateOperations(TransactionDoc.class);
					opAmt.disableValidation();
					opAmt.set("amount", transDoc.getAmount());
					opAmt.set("paymentStatus", transDoc.getPaymentStatus());
					opAmt.set("updatedTime", epoch);
					
					UpdateResults ures = datastore.update(query,opAmt );
					if(ures != null && ures.getUpdatedCount()>0){
						FriendsDao.updateAmount(transDoc.getUser1(), transDoc.getUser2(), transDoc);
						FriendsDao.updateAmount(transDoc.getUser2(), transDoc.getUser1(), transDoc);
					}

					
					
					/**
					 * adding transaction to sql
					 * */
					TransactionDao.addTransactiontoSql(Arrays.asList(transac));
					transDoc.setTransactions(Arrays.asList(transac));

					
					/*TransactionNotification transNotif = new TransactionNotification();
					transNotif.setTransaction(transac);
					transNotif.setNotificationType(Constants.NOTIFICATION_TRANS_DISPUTE);
					transNotif.setAmount(transacDispBean.getDisputeAmount());
					*/
					boolean flag = false;
					UserMaster um = null;
					FriendContact frnd = null; 
					if(transDoc.getUser1().equals(""+user.getUserId())){
						um = UserDao.getUserForWeb(Long.parseLong(transDoc.getUser2()));
//						transNotif.setUserId(""+um.getUserId());
						frnd = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, um);
						flag = false;
					}else {
						um = UserDao.getUserForWeb(Long.parseLong(transDoc.getUser1()));
//						transNotif.setUserId(""+um.getUserId());
						frnd = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, um);
						flag = true;
					}
					String msg = "";
					if(frnd != null)
					{
						if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
							msg = frnd.getContactName();
						else if(user.getUserProfile().getUserName() != null && 
								!user.getUserProfile().getUserName().isEmpty())
							msg = user.getUserProfile().getUserName();
						else
							msg = user.getContactNo();
					}
					if(transac.getDisputeFlag() == 0)
						msg +=" has resolved the transaction dispute : ";
					else
						msg +=" has disputed transaction : ";

					
					NotificationHelper.buildAndSendTransactionNotification(user, um, transac, transDoc, flag, msg, Constants.NOTIFICATION_TRANS_DISPUTE);
					
//					PushNotificationControler.sendNotificationsToUser(Arrays.asList(um.getUserId()), null, transNotif, msg);
					transDoc.setBackedUptransactions(null);
					transacDispBean.setMsg("SuccessFully Disputed");
					transacDispBean.setStatus(Constants.SUCCESS_RESPONSE);
				}else{
					transacDispBean.setMsg("Dispute Failed");
					transacDispBean.setStatus(Constants.FAILURE);
				}

		}else{
			transacDispBean.setMsg("Incorrect Transaction ids");
			transacDispBean.setStatus(Constants.FAILURE);

		}
		return transDoc;
	}
	
	
	public static Transaction getTransactionForWeb(String transactionId, String transactionDocId, UserMaster user){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 Transaction trans = null;
		 DBObject match2  ;
		 boolean cflag = true;
		 if(transactionDocId!=null){
			 try {
				 BasicDBObject sdb = new BasicDBObject("_id",new ObjectId(transactionDocId));
			} catch (Exception e) {
				e.printStackTrace();
				cflag = false;
			}
		 }
		 
		 if(transactionDocId!= null && cflag){
				 match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId(transactionDocId)));
			  
		 }
		 else{
			 BasicDBList exprList = new BasicDBList();
			 exprList.add(new BasicDBObject("user1",""+user.getUserId()));
			 exprList.add(new BasicDBObject("user2", ""+user.getUserId()));
			 DBObject userCond = new BasicDBObject("$or",exprList);
			 match2 = new BasicDBObject("$match", userCond);
		 }
		 
//		  DBObject match = new BasicDBObject("$match", BasicDBObjectBuilder.start("user1", user.getUserId()).push("|").add("user2", user.getUserId()));
		  DBObject match1 = new BasicDBObject("$match", new BasicDBObject("transactions.transactionId", transactionId));
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$transactions");
		  AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,project,match1, group);
		  System.out.println("i m in");
		  try {
		   ObjectMapper mapper = new ObjectMapper();
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
			   List<Transaction> flist = (List<Transaction>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")), List.class);
			   trans =  mapper.readValue(mapper.writeValueAsString(flist.get(0)), Transaction.class);
		   }
		   
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 
		 return trans;
	}
	
	
	
	
	public static boolean deleteTransaction(String transacId, String transacDocId, UserMaster user){

		boolean flag = false;
		ModificationRequest modReqValidator = null;
		UpdateResults ur = null;
		ObjectMapper mapper = new ObjectMapper();
		long epoch = System.currentTimeMillis();
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		TransactionDoc transactionDoc = null;
		Query<TransactionDoc> quer = datastore.createQuery(TransactionDoc.class);
		quer.field("_id").equal(new ObjectId(transacDocId));
//		quer.field("transactions.createdBy").equal(""+user.getUserId());
		quer.filter("transactions.transactionId",transacId);
		

		if(!quer.asList().isEmpty())
			transactionDoc = quer.asList().get(0);
		Transaction transaction = TransactionDao.getTransactionForWeb(transacId, transacDocId, user);
		UpdateOperations<TransactionDoc> op = null;
		modReqValidator = ModificationRequestDao.getModificationRequestFor(transacId);
		if((modReqValidator != null && modReqValidator.getStatus() !=0) || modReqValidator == null )
		{
		if(transactionDoc != null && transactionDoc.getDocType() == 0){
			
			ModificationRequest modReq = new ModificationRequest();
			modReq.setAction(Constants.TRANSACTION_DELETE);
			modReq.setTransactionId(transaction.getTransactionId());
			String user1 = quer.asList().get(0).getUser1();
			String user2 = quer.asList().get(0).getUser2();
			if (user1.equals("" + user.getUserId())) {
				modReq.setForUser(user2);
			} else {
				modReq.setForUser(user1);
			}
			
				ModificationRequestDao.addModificationRequest(modReq, user);
			
		
			op = datastore.createUpdateOperations(TransactionDoc.class);
			op.disableValidation();
			transaction.setAction(Constants.TRANSACTION_DELETE);
			transaction.setModReqId(modReq.getId());
			transaction.setUpdatedTime(epoch);
			transaction.setLastEditedBy(""+user.getUserId());
			transaction.setTransactionStatus(Constants.TRANS_NEED_TO_APROOVE);

			op.add("modifiedTransactions", transaction);
			
			op.enableValidation();
			ur = datastore.update(quer, op);
			
		}else if (transactionDoc != null && transactionDoc.getDocType() == 5){
		
			}else{
			op= datastore.createUpdateOperations(TransactionDoc.class).removeAll("transactions", new BasicDBObject("transactionId", transacId));
			ur = datastore.update(quer, op);
		}
		System.out.println("---===>>"+quer.toString());
		
		if(ur != null)
		if(ur.getUpdatedCount() > 0){
			
//			TransactionDao.addNewUpdatedTransaction(transaction, epoch);
			flag = true;
			
			/**
			 * Updating total amount of user and unmanaged_user transaction
			 * */
			if(transactionDoc != null && transactionDoc.getDocType() != 0)
			{
				double total = 0;
				double amount = calculateAmt(transacDocId);
				total = amount + transactionDoc.getOpeningBalAmt();
				if(total >=0){
					transactionDoc.setAmount(total);
					transactionDoc.setPaymentStatus(Constants.TO_TAKE);
				}else{
					transactionDoc.setAmount(total * (-1));
					transactionDoc.setPaymentStatus(Constants.TO_GIVE);
				}
				
				Query<TransactionDoc> querUpdate1 = datastore.createQuery(TransactionDoc.class);
				querUpdate1.field("_id").equal(new ObjectId(transacDocId));	
				UpdateOperations<TransactionDoc> opAmt = datastore.createUpdateOperations(TransactionDoc.class);
				opAmt.disableValidation();
				opAmt.set("amount", transactionDoc.getAmount());
				opAmt.set("paymentStatus", transactionDoc.getPaymentStatus());
				opAmt.set("updatedTime", epoch);
				
				UpdateResults ures = datastore.update(querUpdate1,opAmt );
				if(ures != null && ures.getUpdatedCount()>0){
					FriendsDao.updateAmount(transactionDoc.getUser1(), transactionDoc.getUser2(), transactionDoc);
	//				FriendsDao.updateAmount(transactionDoc.getUser2(), transactionDoc.getUser1(), transactionDoc);
				}
				
				}
				
				if(transactionDoc.getDocType() == 0){
					TransactionDao.addNewUpdatedTransaction(transaction, epoch);
					
				}
	
				try {
					System.out.println("==>" + mapper.writeValueAsString(ur));
				} catch (Exception e) {
					System.out.println(e.getMessage());
			}
		
			
			/***
			 * Notification part 
			 ***/
			
			UserMaster userB = null;
			if(transactionDoc.getDocType() == 0){
				if(transactionDoc.getUser1().equals(user.getUserId()+"")){
					userB = UserDao.getUserForWeb(Long.parseLong(transactionDoc.getUser2()));
				}
				else{
					userB = UserDao.getUserForWeb(Long.parseLong(transactionDoc.getUser1()));
					flag = true;
				}
			}
			if(userB != null && userB.getUserId()!=0){
				/*TransactionNotification tn = new TransactionNotification();
				tn.setNotificationType(Constants.NOTIFICATION_TRANS_DELETE);
				tn.setTransaction(transaction);
				tn.setAmount(transactionDoc.getAmount());
				tn.setUserId(""+userB.getUserId());
				
				if(flag)
					tn.setPaymentStatus(transactionDoc.getPaymentStatus());
				else{
					if(transactionDoc.getPaymentStatus() == Constants.TO_GIVE)
						tn.setPaymentStatus(Constants.TO_TAKE);
					else
						tn.setPaymentStatus(Constants.TO_GIVE);
				}
				if(transactionDoc.getTransactions().size()>1)
					tn.setPullFlag(1);
*/				/**
				 * Notification message
				 **/
				FriendContact frnd = null; 
				frnd = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, userB);
				String msg = "";
				if(frnd != null)
				{
					if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
						msg = frnd.getContactName();
					else if(user.getUserProfile().getUserName() != null && 
							!user.getUserProfile().getUserName().isEmpty())
						msg = user.getUserProfile().getUserName();
					else
						msg = user.getContactNo();
				}
		
				msg +=" has requested to delete a transaction with you.";
				System.out.println("==///=="+msg);
				/**
				 * end
				 **/
				/*String msg = user.getUserProfile().getUserName()+" has requested to delete a transaction with you.";
				 */
				
				NotificationHelper.buildAndSendTransactionNotification(user, userB, transaction, transactionDoc, flag, msg, Constants.NOTIFICATION_TRANS_DELETE);
//				PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB.getUserId()), null, tn, msg);
			}
		}
		
		try {
			System.out.println("== > "+mapper.writeValueAsString(ur));
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
		return flag;
	}

    /**
     * for verification of old backed_up transaction record
     * */
	    
	public static List<Transaction> getTransactionForBackedUpTransaction(String transactionId, String transactionDocId, UserMaster user){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 List<Transaction> trans = null;
		 ObjectMapper mapper = new ObjectMapper();
		 Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class) ;
			query.filter("_id",new ObjectId(transactionDocId));
			
			query.filter("backedUptransactions.transactionId",transactionId);
		 	if(query.asList().isEmpty())
				return null;
		 	else
		 return query.asList().get(0).getBackedUptransactions();
	}
	
	
	/**
     * for verification of  modified transaction record
     * */
	    
	public static List<Transaction> getTransactionForModifiedTransaction(String transactionId, String transactionDocId, UserMaster user){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 List<Transaction> trans = null;
		 ObjectMapper mapper = new ObjectMapper();
		 Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class) ;
			query.filter("_id",new ObjectId(transactionDocId));
			
			query.filter("modifiedTransactions.transactionId",transactionId);
		 	if(query.asList().isEmpty())
				return null;
		 	else
		 return query.asList().get(0).getBackedUptransactions();
	}
	
	/**
	 * for processing 
	 * user Response on transaction update approval
	 **/
	public static int processResponseForTransactionUpdate(String transactionId, String transactionDocId, UserMaster user, 
					int userResponse, ModificationRequest req){
		ObjectMapper mapper = new ObjectMapper();
		int res = 0;
		Transaction trans = null;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		TransactionDoc transDoc = null;
		long epoch = System.currentTimeMillis();
		Query<TransactionDoc> querUpdate = datastore.createQuery(TransactionDoc.class);
		querUpdate.field("_id").equal(new ObjectId(transactionDocId));
		querUpdate.filter("transactions.transactionId",transactionId);
		if(querUpdate.get() != null){
			
			transDoc = querUpdate.get();
		}
		if(transDoc != null){
			UpdateResults ur = null;
			trans = getModifiedTransactionById(transactionId, user);
			if(userResponse == Constants.ACTION_APPROVED){
				
				
				
				if(trans != null){
					UpdateOperations<TransactionDoc> op = datastore.createUpdateOperations(TransactionDoc.class);
					op.set("transactions.$.updatedTime", epoch);
					op.set("transactions.$.amount", trans.getAmount());
					op.set("transactions.$.comment", trans.getComment());
					op.set("transactions.$.transactionDate",trans.getTransactionDate());
					op.set("transactions.$.transactionStatus",Constants.TRANS_APROOVED);
					op.set("transactions.$.lastEditedBy", trans.getLastEditedBy());
					ur = datastore.update(querUpdate,op);
					if(ur.getUpdatedCount() == 0)
					{
						res = 2;
					}
					else{
						trans.setTransactionStatus(Constants.TRANS_APROOVED);
						TransactionDao.updateAddNewTransaction(trans, epoch,true);
					}
				}
				
			}
			else{
				UpdateOperations<TransactionDoc> op = datastore.createUpdateOperations(TransactionDoc.class);
				op.disableValidation();
				op.set("transactions.$.transactionStatus", Constants.TRANS_APROOVED);
				op.set("transactions.$.updatedTime", epoch);
				op.set("transactions.$.lastEditedBy",trans.getLastEditedBy());
				ur = datastore.update(querUpdate,op );
				if(ur.getUpdatedCount() == 0)
				{
					res = 2;
				}
				else
				{
					trans.setTransactionStatus(Constants.TRANS_APROOVED);
					TransactionDao.updateAddNewTransaction(trans, epoch,false);
				}	

			}

			if(ur!=null && ur.getUpdatedCount() > 0 ){
				
				//// reset transaction doc amount and pay status.
			
				double amount = calculateAmt(transactionDocId) + transDoc.getOpeningBalAmt();
				if(amount >=0){
					transDoc.setAmount(amount);
					transDoc.setPaymentStatus(Constants.TO_TAKE);
				}else{
					transDoc.setAmount(amount * (-1));
					transDoc.setPaymentStatus(Constants.TO_GIVE);
				}
					
				UpdateOperations<TransactionDoc> opAmt = datastore.createUpdateOperations(TransactionDoc.class);
				opAmt.disableValidation();
				opAmt.set("amount", transDoc.getAmount());
				opAmt.set("paymentStatus", transDoc.getPaymentStatus());
				opAmt.set("updatedTime", epoch);
				
				UpdateResults ures = datastore.update(querUpdate,opAmt );
				if(ures != null && ures.getUpdatedCount()>0){
					FriendsDao.updateAmount(transDoc.getUser1(), transDoc.getUser2(), transDoc);
					FriendsDao.updateAmount(transDoc.getUser2(), transDoc.getUser1(), transDoc);
				}
				
				
				// delete backed-up transactions.
				Query<TransactionDoc> quer = datastore.createQuery(TransactionDoc.class);
				quer.field("_id").equal(new ObjectId(transactionDocId));
				quer.filter("modifiedTransactions.transactionId",transactionId);

				UpdateOperations<TransactionDoc> updateOp= datastore.createUpdateOperations(TransactionDoc.class).removeAll("modifiedTransactions", new BasicDBObject("transactionId", transactionId));

				UpdateResults up = datastore.update(quer, updateOp);
				if(up.getUpdatedCount()>0){
					
					TransactionDao.deleteSqlTransaction(transactionId,Constants.TRANS_NEED_TO_APROOVE);
					req.setStatus(userResponse);
					
					ModificationRequestDao.updateModificationRequest(req, user);
					
					boolean flag = false;
					UserMaster userB = null;
					if (transDoc.getDocType() == 0) {
						if (transDoc.getUser1().equals(user.getUserId() + "")) {
								userB = UserDao.getUserForWeb(Long
										.parseLong(transDoc.getUser2()));
								flag = false;
							} else {
								userB = UserDao.getUserForWeb(Long
										.parseLong(transDoc.getUser1()));
								flag = true;
							}
						}
						
						if (userB != null && userB.getUserId() != 0) {
							Transaction t = null;
							if(userResponse == Constants.ACTION_APPROVED)
								t = getTransactionForWeb(transactionId, transactionDocId, userB);
							else{
								t = getTransactionForWeb(transactionId, transactionDocId, userB);
								/*t  = getModifiedTransactionById(transactionId, user);
								t.setTransactionStatus(userResponse);*/
							}
/*							TransactionNotification tn = new TransactionNotification();
							tn.setNotificationType(Constants.NOTIFICATION_TRANS_UPDATE);
							tn.setTransaction(t);
							tn.setAmount(transDoc.getAmount());
							tn.setUserId(""+userB.getUserId());
							if (flag)
								tn.setPaymentStatus(transDoc
										.getPaymentStatus());
							else {
								if (transDoc.getPaymentStatus() == Constants.TO_GIVE)
									tn.setPaymentStatus(Constants.TO_TAKE);
								else
									tn.setPaymentStatus(Constants.TO_GIVE);
							}
*/							

							/**
							 * Notification message
							 **/
							FriendContact frnd = null; 
							frnd = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, userB);
							String msg = "";
							if(frnd != null)
							{
								if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
									msg = frnd.getContactName();
								else if(user.getUserProfile().getUserName() != null && 
										!user.getUserProfile().getUserName().isEmpty())
									msg = user.getUserProfile().getUserName();
								else
									msg = user.getContactNo();
							}
					        if(userResponse == Constants.ACTION_APPROVED )
							       msg +=" has approved your transaction update.";
					        else 
					        	msg +=" has rejected your transaction update.";
					        System.out.println("==///=="+msg);
							/**
							 * end
							 * */
							
							/*String msg = user.getUserProfile().getUserName()+ " has approved your transaction update.";*/
					        
					        NotificationHelper.buildAndSendTransactionNotification(user, userB, t, transDoc, flag, msg, Constants.NOTIFICATION_TRANS_UPDATE);
/*							PushNotificationControler
									.sendNotificationsToUser(
											Arrays.asList(userB.getUserId()), null,
											tn, msg);
*/						}
					}
				}

			
					
					
			/*	}
				
			}*/
		
		}else{
			res = 1;
		}
		
		return res;
	}

	
	
	private static void deleteSqlTransaction(String transactionId,
			int transNeedToAproove) {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx=null;
			tx = session.beginTransaction();
			org.hibernate.Query query=session.createQuery("DELETE FROM TransactionSql WHERE transactionId =:transactionId AND transactionStatus =:transactionStatus");
			query.setParameter("transactionId", transactionId);
			query.setParameter("transactionStatus", transNeedToAproove);
			query.executeUpdate();
			tx.commit();
			System.out.println("sql transaction : " +transactionId+" deleted");			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		
	}


	/**
	 * for processing 
	 * user Response on transaction update approval
	 **/
	public static int processResponseForTransactionDelete(String transactionId, String transactionDocId, UserMaster user, 
					int userResponse, ModificationRequest req){
		ObjectMapper mapper = new ObjectMapper();
		int res = 0;
		
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		TransactionDoc transDoc = null;
		
		long epoch = System.currentTimeMillis();
		Query<TransactionDoc> querUpdate = datastore.createQuery(TransactionDoc.class);
		Transaction trans = getTransactionForWeb(transactionId, transactionDocId, user);
		querUpdate.field("_id").equal(new ObjectId(transactionDocId));
		querUpdate.filter("transactions.transactionId",transactionId);
		if(querUpdate.get() != null){
			
			transDoc = querUpdate.get();
		}
		if(transDoc != null){
			UpdateResults ur = null;
			Transaction tranmod = TransactionDao.getModifiedTransactionById(transactionId, user);
			if(tranmod != null)
				trans.setLastEditedBy(tranmod.getLastEditedBy());
			if(userResponse == Constants.ACTION_APPROVED){
				if(trans != null){
					
					trans.setTransactionStatus(0);
					
				}
				UpdateOperations<TransactionDoc> updateOp= datastore.createUpdateOperations(TransactionDoc.class).removeAll("transactions", new BasicDBObject("transactionId", transactionId));
				ur = datastore.update(querUpdate, updateOp);
				TransactionDao.deleteSqlTransaction(trans.getTransactionId(), Constants.TRANS_APROOVED);
			}
			else{
				if(trans != null)
					trans.setTransactionStatus(userResponse);
				
				UpdateOperations<TransactionDoc> op = datastore.createUpdateOperations(TransactionDoc.class);
				op.disableValidation();
				op.set("transactions.$.transactionStatus", Constants.TRANS_APROOVED);
				op.set("transactions.$.updatedTime", epoch);
				op.set("transactions.$.lastEditedBy", trans.getLastEditedBy());
				ur = datastore.update(querUpdate,op );
				if(ur.getUpdatedCount() == 0){
					res = 2;
				}else{
					trans.setTransactionStatus(Constants.TRANS_APROOVED);
					TransactionDao.updateAddNewTransaction(trans, epoch,false);
				}
			}
			
			if(ur.getUpdatedCount() > 0){

				// reset transaction doc amount
				double total = 0;
				double amount = calculateAmt(transactionDocId);
				total = amount + transDoc.getOpeningBalAmt();
				if(total >=0){
					transDoc.setAmount(total);
					transDoc.setPaymentStatus(Constants.TO_TAKE);
				}else{
					transDoc.setAmount(total * (-1));
					transDoc.setPaymentStatus(Constants.TO_GIVE);
				}
				
				Query<TransactionDoc> querUpdate1 = datastore.createQuery(TransactionDoc.class);
				querUpdate1.field("_id").equal(new ObjectId(transactionDocId));	
				UpdateOperations<TransactionDoc> opAmt = datastore.createUpdateOperations(TransactionDoc.class);
				opAmt.disableValidation();
				opAmt.set("amount", transDoc.getAmount());
				opAmt.set("paymentStatus", transDoc.getPaymentStatus());
				opAmt.set("updatedTime", epoch);
				
				UpdateResults ures = datastore.update(querUpdate1,opAmt );
				if(ures != null && ures.getUpdatedCount()>0){
					FriendsDao.updateAmount(transDoc.getUser1(), transDoc.getUser2(), transDoc);
					FriendsDao.updateAmount(transDoc.getUser2(), transDoc.getUser1(), transDoc);
				}

				// delete backed-up transactions.
				Query<TransactionDoc> quer = datastore.createQuery(TransactionDoc.class);
				quer.field("_id").equal(new ObjectId(transactionDocId));
				quer.filter("modifiedTransactions.transactionId",transactionId);

				UpdateOperations<TransactionDoc> updateOp= datastore.createUpdateOperations(TransactionDoc.class).removeAll("modifiedTransactions", new BasicDBObject("transactionId", transactionId));

				UpdateResults up = datastore.update(quer, updateOp);
                if(up != null && up.getUpdatedCount()>0){
                	TransactionDao.deleteSqlTransaction(transactionId, Constants.TRANS_NEED_TO_APROOVE);
                }
				
				req.setStatus(userResponse);
				ModificationRequestDao.updateModificationRequest(req, user);
				boolean flag = false;
				UserMaster userB = null;
				if (transDoc.getDocType() == 0) {
					if (transDoc.getUser1().equals(user.getUserId() + "")) {
							userB = UserDao.getUserForWeb(Long
									.parseLong(transDoc.getUser2()));
							flag = false;
						} else {
							userB = UserDao.getUserForWeb(Long
									.parseLong(transDoc.getUser1()));
							flag = true;
						}
					}
					
					if (userB != null && userB.getUserId() != 0) {
						Transaction t = null;
						if(userResponse == Constants.ACTION_APPROVED)
							t = trans;
						else{
							t = trans;
						}
						/*TransactionNotification tn = new TransactionNotification();
						tn.setNotificationType(Constants.NOTIFICATION_TRANS_DELETE);
						tn.setTransaction(t);
						tn.setUserId(""+userB.getUserId());
						tn.setAmount(transDoc.getAmount());
						if (flag)
							tn.setPaymentStatus(transDoc.getPaymentStatus());
						else {
							if (transDoc.getPaymentStatus() == Constants.TO_GIVE)
								tn.setPaymentStatus(Constants.TO_TAKE);
							else
								tn.setPaymentStatus(Constants.TO_GIVE);
						}*/
						/**
						 * Notification message
						 **/
						FriendContact frnd = null; 
						frnd = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, userB);
						String msg = "";
						if(frnd != null)
						{
							if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
								msg = frnd.getContactName();
							else if(user.getUserProfile().getUserName() != null && 
									!user.getUserProfile().getUserName().isEmpty())
								msg = user.getUserProfile().getUserName();
							else
								msg = user.getContactNo();
						}
				
						if(userResponse == Constants.ACTION_APPROVED)
						       msg +=" has approved your transaction delete.";
				        else 
				        	msg +=" has rejected your transaction delete.";
						System.out.println("==///=="+msg);
						/**
						 * end
						 * */
						/*String msg = user.getUserProfile().getUserName()+ " has approved your transaction update.";
						*/
						
						NotificationHelper.buildAndSendTransactionNotification(user, userB, t, transDoc, flag, msg, Constants.NOTIFICATION_TRANS_DELETE);
						/*PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB.getUserId()), null,
										tn, msg);*/
					}
				}
			
				
		}else{
			res = 1;
		}
		
		return res;
	}


	
	/**
	 * pull transactions for staff users
	 **/
	public static List<Transaction> getTransactionForStaff(UserMaster user, long pullTime){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 Transaction trans = null;
		 List<Transaction> flist = new ArrayList<Transaction>();
//		 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
		  
		 BasicDBList idList = new BasicDBList();
		 idList.add(new BasicDBObject("user1",""+user.getOwnerId()));
		 idList.add(new BasicDBObject("user2",""+user.getOwnerId()));
		  DBObject match2 = new BasicDBObject("$match", new BasicDBObject("$or", idList));
		  
		  DBObject match1 = new BasicDBObject("$match", new BasicDBObject("transactions.updatedTime", new BasicDBObject("$gt",pullTime)));
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$transactions");
		  AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,project,match1, group);
		  System.out.println("i m in");
		  try {
		   ObjectMapper mapper = new ObjectMapper();
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
//			  System.out.println(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")));
			  Gson gson = new Gson();
			  for(BasicDBObject objArr : (List<BasicDBObject>) objList){
				  List<Transaction> tempList =  gson.fromJson(gson.toJson(objArr.get("transactions")),List.class);
				  
				  Iterator<Transaction> iterator = tempList.iterator();
				  while(iterator.hasNext()){
					 flist.add(gson.fromJson(gson.toJson(iterator.next()), Transaction.class));
				  }
			  }
//			  flist = (List<Transaction>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")), List.class);
		   }
		   
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 
		 return flist;
	}

/*
	public static Transaction getTransactionByTransactionId(String transIds) {
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 Transaction trans = null;
		 
		  DBObject match2 = new BasicDBObject("$unwind","$transactions"); 
		  DBObject match1 = new BasicDBObject("$match", new BasicDBObject("transactions.transactionId", transIds));
		  
		  AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,match1);
		  System.out.println("i m in");
		  try {
		   ObjectMapper mapper = new ObjectMapper();
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
//			   System.out.println(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("friends")));
			   List<Transaction> flist = (List<Transaction>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")), List.class);
			   trans =  mapper.readValue(mapper.writeValueAsString(flist.get(0)), Transaction.class);
		   }
		   
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 
		 return trans;
		
	}


	public static void markTransactionDocAsRead(List<String> transIds) {
		for(String tranId : transIds){
			getTransactionByTransactionId(tranId);
		}
		
		
		
	}*/
	
	
	/***
	 * Calculate Transaction Amount
	 ***/
	public static double calculateAmt(String transDocId){
		double amt = 0;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		
		//match transactionDocId
		DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId(transDocId))); 
		
		//calculate amt and project result
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
		
		//Group and sum amount
		DBObject group = new BasicDBObject("$group", gdb1);
	
		// Unwind array on which operation to perform
		DBObject unwind = new BasicDBObject("$unwind", "$transactions");
		
		
		AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,unwind,group,project);
		System.out.println("i m in");
		try {
			ObjectMapper mapper = new ObjectMapper();
//			System.out.println("== : "+mapper.writeValueAsString(output));
//			System.out.println("== : "+mapper.writeValueAsString(output.getCommandResult()));
			BasicDBList bso = (BasicDBList) output.getCommandResult().get("result");
			if(!bso.isEmpty()){
				amt = Double.parseDouble(mapper.writeValueAsString(((BasicDBObject) bso.get(0)).get("amt")));
				System.out.println("Amount : "+amt);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return amt;
	}

	
	
	public static boolean updateReadStatusForTransaction(List<String> transIds, UserMaster user, int userResponse, List<String> failed){
		boolean flag = false;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		long epoch = System.currentTimeMillis();
		HashMap<String, List<String>> transMap = new HashMap<String, List<String>>();
		List<Transaction> transactionlist = new ArrayList<Transaction>();
		transactionlist = getTransactionListForWeb(transIds);
		for(Transaction transaction : transactionlist){
			if(transaction != null){
				Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
		//		query.field("_id").equals(androidId);
				query.disableValidation();
				query.or(query.criteria("user1").equal(""+user.getUserId()),query.criteria("user2").equal(""+user.getUserId()));
				query.filter("transactions.transactionId", transaction.getTransactionId());
				query.field("docType").notEqual(Constants.BLOCKED_USER);
		//		query.criteria("transactions.transactionId").in(transIds);
		//		query.enableValidation();
				if(query.asList().size()>0){
					
					UpdateOperations<TransactionDoc> op = datastore.createUpdateOperations(TransactionDoc.class);
					op.disableValidation();
					
					if(userResponse == Constants.TRANSACTION_READ){
						op.set("transactions.$.readStatus", Constants.TRANSACTION_READ);
						op.set("transactions.$.readTime", epoch);
						
					}
					else{
						op.set("transactions.$.readStatus", Constants.TRANSACTION_RECIEVED);
						op.set("transactions.$.receivedTime", epoch);
						op.set("transactions.$.readTime", epoch);
					}
//					op.set("transactions.$.updatedTime", epoch);
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
								
						if(transaction.getFrom().equals(""+user.getUserId())){
							List<String> tempTrans = transMap.get(transaction.getTo());
							if(tempTrans != null){
								tempTrans.add(transaction.getTransactionId());
							}else{
								tempTrans = new ArrayList<String>();
								tempTrans.add(transaction.getTransactionId());
							}
							transMap.put(transaction.getTo(), tempTrans);
						}
						else{
							List<String> tempTrans = transMap.get(transaction.getFrom());
							if(tempTrans != null){
								tempTrans.add(transaction.getTransactionId());
							}else{
								tempTrans = new ArrayList<String>();
								tempTrans.add(transaction.getTransactionId());
							}
							transMap.put(transaction.getFrom(), tempTrans);
						}

		
					}	
				
				}
			}
			else 
				failed.add(transaction.getTransactionId());
		}
		/***
		 * Notification part 
		 ***/
		FriendContact frnd = null; 
		for (String key : transMap.keySet()) {
			UserMaster userB = null;
			try {
				userB = UserDao.getUserForWeb(Long.parseLong(key));
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("Exception in mark read service.");
			}
			
			if(userB != null && userB.getUserId()!=0){
				/*TransactionReadNotification tn = new TransactionReadNotification();
				tn.setReadStatus(userResponse);
				tn.setTransactionIds(transMap.get(key));
				tn.setUserId(userB.getUserId()+"");
				tn.setServerTime(epoch);
				tn.setNotificationType(Constants.NOTIFICATION_TRANS_READ_STAT);
*/				
				/**
				 * Notification message
				 **/
				
			
				frnd = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, userB);
					
			
				String msg = "";
				if(frnd != null)
				{
					if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
						msg = frnd.getContactName();
					else if(user.getUserProfile().getUserName() != null && 
							!user.getUserProfile().getUserName().isEmpty())
						msg = user.getUserProfile().getUserName();
					else
						msg = user.getContactNo();
				}
		
				msg +=" has read transactions with you.";
				System.out.println("==///=="+msg);
				/**
				 * end
				 * */
				/*String msg = user.getUserProfile().getUserName()+" has read transactions with you.";
				 */
				
				NotificationHelper.buildAndSendTransactionReadNotification(userB, transMap.get(key), userResponse, msg, epoch);
//				PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB.getUserId()), null, tn, msg);
			}
			frnd = null;
		}

		
		return flag;
	}

	
	
	/**
	 * update docType to block state.
	 * 
	 * */
	public static TransDocBean updateTransactionDocsAsBlocked(UserMaster user, String frndId){
		int result = -1;
		TransDocBean transBean = new TransDocBean();
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
		List<TransactionDoc> transactionDocs = new ArrayList<TransactionDoc>();
	
		query.or(
				query.and(query.criteria("user1").equal(""+user.getUserId()),query.criteria("user2").equal(frndId)),
				query.and(query.criteria("user1").equal(frndId),query.criteria("user2").equal(""+user.getUserId()))
				);
		query.filter("docType", 0);
				
		if(!query.asList().isEmpty()){
			result = 0;
			transactionDocs = query.asList();
			transBean.setTransDoc(transactionDocs.get(0));
			
			UpdateOperations<TransactionDoc> op = datastore.createUpdateOperations(TransactionDoc.class);
			
			op.set("docType", Constants.BLOCKED_USER);
			op.set("updatedTime", System.currentTimeMillis());
			
			UpdateResults ur = datastore.update(query,op );
			
			if(ur.getUpdatedCount() >0){
			result = 1;
			transBean.getTransDoc().setDocType(Constants.BLOCKED_USER);
			}
			
		}
		
		transBean.setStatus(result);
		
		return transBean;
	}
	
	public static List<Transaction> getTransactionListForWeb(List<String> transactionIds){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 Transaction trans = null;
		 List<Transaction> reultlist = new ArrayList<Transaction>();
		 ObjectMapper mapper = new ObjectMapper();

		 DBObject list = new BasicDBObject("$in",transactionIds );  
		 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("transactions.transactionId", list));
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$transactions");
		  AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(project,match1, group);
		  System.out.println("i m in");
		  try {
		   
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   List<Transaction> flist = null;
		   
		   for(int i=0; i<objList.size(); i++ ){
			   flist = (List<Transaction>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(i)).get("transactions")), List.class);
			   for(int j=0; j<flist.size() ; j++ ){
				   trans =  mapper.readValue(mapper.writeValueAsString(flist.get(j)), Transaction.class);
				   reultlist.add(trans);
				   System.out.println("33"+mapper.writeValueAsString(trans));
			   }
		   }
		   
		   
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 
		
		 return reultlist;
	}
	public static boolean updateReadStatusForTransactionTest(List<String> transIds, UserMaster user, int userResponse, List<String> failed){
		boolean flag = false;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		long epoch = System.currentTimeMillis();
		HashMap<String, List<String>> transMap = new HashMap<String, List<String>>();
		List<Transaction> transactionlist = new ArrayList<Transaction>();
		ObjectMapper mapper = new ObjectMapper();
		transactionlist = getTransactionListForWeb(transIds);
		
				Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
		//		query.field("_id").equals(androidId);
				query.disableValidation();
				query.or(query.criteria("user1").equal(""+user.getUserId()),query.criteria("user2").equal(""+user.getUserId()));
				query.filter("transactions.transactionId in",transIds);
				MorphiaIterator<TransactionDoc,TransactionDoc> moitr = query.fetch();
		        while(moitr.hasNext()){
		        	try {
						System.out.println("-- :"+mapper.writeValueAsString(moitr.next()));
					} catch (
							IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		//		query.criteria("transactions.transactionId").in(transIds);
		//		query.enableValidation();
				if(query.asList().size()>0){
					
					UpdateOperations<TransactionDoc> op = datastore.createUpdateOperations(TransactionDoc.class);
					op.disableValidation();
					
					if(userResponse == Constants.TRANSACTION_READ){
						op.set("transactions.$.readStatus", Constants.TRANSACTION_READ);
						op.set("transactions.$.readTime", epoch);
					}
					else{
						op.set("transactions.$.readStatus", Constants.TRANSACTION_RECIEVED);
						op.set("transactions.$.receivedTime", epoch);
					}
					op.set("transactions.$.updatedTime", epoch);
					op.enableValidation();
		//			       datastore.getCollection(StaffTransactionDoc.class).updateMulti(query, op);
					UpdateResults ur = datastore.update(query,op);
					for(Transaction transaction : transactionlist){
						
						if(transaction != null){
					
					try {
						System.out.println("update respo : "+mapper.writeValueAsString(ur));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if(ur.getUpdatedCount()>0){
						flag = true;
								
						if(transaction.getFrom().equals(""+user.getUserId())){
							List<String> tempTrans = transMap.get(transaction.getTo());
							if(tempTrans != null){
								tempTrans.add(transaction.getTransactionId());
							}else{
								tempTrans = new ArrayList<String>();
								tempTrans.add(transaction.getTransactionId());
							}
							transMap.put(transaction.getTo(), tempTrans);
						}
						else{
							List<String> tempTrans = transMap.get(transaction.getFrom());
							if(tempTrans != null){
								tempTrans.add(transaction.getTransactionId());
							}else{
								tempTrans = new ArrayList<String>();
								tempTrans.add(transaction.getTransactionId());
							}
							transMap.put(transaction.getFrom(), tempTrans);
						}

		
					}	
				}}
				
				}
			
		/***
		 * Notification part 
		 ***/
		FriendContact frnd = null; 
		for (String key : transMap.keySet()) {
			UserMaster userB = null;
			try {
				userB = UserDao.getUserForWeb(Long.parseLong(key));
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("Exception in mark read service.");
			}
			
			if(userB != null && userB.getUserId()!=0){
				/*TransactionReadNotification tn = new TransactionReadNotification();
				tn.setReadStatus(userResponse);
				tn.setTransactionIds(transMap.get(key));
				tn.setUserId(userB.getUserId()+"");
				tn.setServerTime(epoch);
				tn.setNotificationType(Constants.NOTIFICATION_TRANS_READ_STAT);
*/				
				/**
				 * Notification message
				 **/
				
			
				frnd = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, userB);
					
			
				String msg = "";
				if(frnd != null)
				{
					if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
						msg = frnd.getContactName();
					else if(user.getUserProfile().getUserName() != null && 
							!user.getUserProfile().getUserName().isEmpty())
						msg = user.getUserProfile().getUserName();
					else
						msg = user.getContactNo();
				}
		
				msg +=" has read transactions with you.";
				System.out.println("==///=="+msg);
				/**
				 * end
				 * */
				/*String msg = user.getUserProfile().getUserName()+" has read transactions with you.";
				 */
				
				NotificationHelper.buildAndSendTransactionReadNotification(userB, transMap.get(key), userResponse, msg, epoch);
//				PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB.getUserId()), null, tn, msg);
			}
			frnd = null;
		}

		
		return flag;
	}
	
	
	
	public static boolean checkForOpeninigBalDate(OpeningBalRequest obr, int docType){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
		boolean flag = false;
		query.or(
				query.and(query.criteria("user1").equal(obr.getRequesterUserId()),query.criteria("user2").equal(obr.getForUserId())),
				query.and(query.criteria("user1").equal(obr.getForUserId()),query.criteria("user2").equal(obr.getRequesterUserId()))
				);
		query.filter("docType", docType);
		query.field("transactions.transactionDate").lessThan(obr.getOpeningBalDate());
		if(!query.asList().isEmpty()){
			flag = true;
		}
		
		return flag;
	}
	
	
	
	
	
	/***
	 * for fetching the minimum date in transactions
	 ***/
	public static long getTransactionListMinimumDate(OpeningBalRequest obr, int docType){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 Transaction trans = null;
		
//		 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
		 long date = 0;
		 
		 BasicDBList andList1 = new BasicDBList();
		 andList1.add(new BasicDBObject("user1",obr.getForUserId()));
		 andList1.add(new BasicDBObject("user2",obr.getRequesterUserId()));
		 
		 BasicDBList andList2 = new BasicDBList();
		 andList2.add(new BasicDBObject("user1",obr.getRequesterUserId()));
		 andList2.add(new BasicDBObject("user2",obr.getForUserId()));
		 
		 
		 BasicDBList idList = new BasicDBList();
		 idList.add(new BasicDBObject("$and",andList1));
		 idList.add(new BasicDBObject("$and",andList2));
		 
		 
		  DBObject match2 = new BasicDBObject("$match", new BasicDBObject("$or", idList));
		  
		  DBObject match1 = new BasicDBObject("$match", new BasicDBObject("transactions.transactionDate", new BasicDBObject("$lt",obr.getOpeningBalDate())));
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$transactions");
		  AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,project,match1, group);
		  System.out.println("i m in");
		  try {
		   ObjectMapper mapper = new ObjectMapper();
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
//			  System.out.println(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")));
			  Gson gson = new Gson();
			  for(BasicDBObject objArr : (List<BasicDBObject>) objList){
				  List<Transaction> tempList =  gson.fromJson(gson.toJson(objArr.get("transactions")),List.class);
				  
				  Iterator<Transaction> iterator = tempList.iterator();
				  while(iterator.hasNext()){
					 
				  }
			  }
//			  flist = (List<Transaction>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")), List.class);
		   }
		   
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 
		 return date;
	}

	/***
	 * This method updates the opening balance amount in transaction doc
	 ***/
	
	public static boolean updateOpeningBalTransactionDoc(UserMaster user, OpeningBalRequest obr, int docType){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
		boolean updateResultFlag = false;
		TransactionDoc transDoc = new TransactionDoc();
		transDoc.setUser1(obr.getRequesterUserId());
		transDoc.setUser2(obr.getForUserId());
		transDoc.setDocType(docType);
		transDoc = TransactionDao.getTransactionDoc(transDoc);
		/*query.or(
				query.and(query.criteria("user1").equal(obr.getRequesterUserId()),query.criteria("user2").equal(obr.getForUserId())),
				query.and(query.criteria("user1").equal(obr.getForUserId()),query.criteria("user2").equal(obr.getRequesterUserId()))
				);*/
		query.field("_id").equal(new ObjectId(transDoc.getIdString()));
		query.filter("docType", docType);
		if(!query.asList().isEmpty()){
			transDoc = query.get();
			
			if(transDoc.getUser1().equals(obr.getRequesterUserId())){
				if(obr.getPaymentStatus() == Constants.TO_TAKE)
					transDoc.setOpeningBalAmt(obr.getOpeningBalAmt());
				else{
					transDoc.setOpeningBalAmt(obr.getOpeningBalAmt()*(-1));
				}
			}else{
				if(obr.getPaymentStatus() == Constants.TO_TAKE)
					transDoc.setOpeningBalAmt(obr.getOpeningBalAmt()*(-1));
				else{
					transDoc.setOpeningBalAmt(obr.getOpeningBalAmt());
				}
			}
			transDoc.setOpeningBalDate(obr.getOpeningBalDate());

			transDoc.setAmount(transDoc.getAmount()+transDoc.getOpeningBalAmt());
			if(transDoc.getAmount() > 0){
				transDoc.setPaymentStatus(Constants.TO_TAKE);
			}else{
				transDoc.setAmount(transDoc.getAmount()*(-1));
				transDoc.setPaymentStatus(Constants.TO_GIVE);
			}
			
			UpdateOperations<TransactionDoc> op = datastore.createUpdateOperations(TransactionDoc.class);
			op.set("openingBalAmt", transDoc.getOpeningBalAmt());
			op.set("openingBalDate", transDoc.getOpeningBalDate());
			op.set("updatedTime", System.currentTimeMillis());
			op.set("amount", transDoc.getAmount());
			op.set("paymentStatus",transDoc.getPaymentStatus());
			op.set("openingBalBy", obr.getRequesterUserId());
			UpdateResults ur =  datastore.update(query,op);
			if(ur.getUpdatedCount()>0){
				updateResultFlag = true;
				
				FriendsDao.updateOpeningBalAmount(transDoc.getUser1(), transDoc.getUser2(), transDoc);
				
				if(transDoc.getDocType() == 0)
					FriendsDao.updateOpeningBalAmount(transDoc.getUser2(), transDoc.getUser1(), transDoc);
			}
		}
		
		return updateResultFlag;
	}

	
	
	
	
	
	public static List<Transaction> getBackedupTransactionListForUser(List<String> transactionIds, String userId){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 Transaction trans = null;
		 List<Transaction> reultlist = new ArrayList<Transaction>();
		 ObjectMapper mapper = new ObjectMapper();

		 
		 BasicDBList andList1 = new BasicDBList();
		 andList1.add(new BasicDBObject("user1",userId));
		 andList1.add(new BasicDBObject("user2",userId));
		 
		 
		 
		  DBObject match2 = new BasicDBObject("$match", new BasicDBObject("$or", andList1));
		 
		 DBObject list = new BasicDBObject("$in",transactionIds );  
		 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("backedUptransactions.transactionId", list));
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("backedUptransactions",new BasicDBObject("$push","$backedUptransactions"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$backedUptransactions");
		  AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,project,match1, group);
		  System.out.println("i m in");
		  try {
		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   List<Transaction> flist = null;
		   Gson gson = new Gson();
		   for(int i=0; i<objList.size(); i++ ){
			   flist = (List<Transaction>) gson.fromJson(gson.toJson(((BasicDBObject)objList.get(i)).get("backedUptransactions")), List.class);
			   for(int j=0; j<flist.size() ; j++ ){
				   trans =  gson.fromJson(gson.toJson(flist.get(j)), Transaction.class);
				   reultlist.add(trans);
//				   System.out.println("33"+mapper.writeValueAsString(trans));
			   }
		   }
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 return reultlist;
	}
	
	
	
/***
 * Get Transaction from modified transaction list
 ***/	
	public static Transaction getModifiedTransactionById(String transactionId, UserMaster user){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 Transaction trans = null;
		 DBObject match2  ;
		
		 BasicDBList exprList = new BasicDBList();
		 exprList.add(new BasicDBObject("user1",""+user.getUserId()));
		 exprList.add(new BasicDBObject("user2", ""+user.getUserId()));
		 DBObject userCond = new BasicDBObject("$or",exprList);
		 match2 = new BasicDBObject("$match", userCond);
		 
//		  DBObject match = new BasicDBObject("$match", BasicDBObjectBuilder.start("user1", user.getUserId()).push("|").add("user2", user.getUserId()));
		  DBObject match1 = new BasicDBObject("$match", new BasicDBObject("modifiedTransactions.transactionId", transactionId));
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("transactions",new BasicDBObject("$push","$modifiedTransactions"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$modifiedTransactions");
		  AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,project,match1, group);
		  System.out.println("i m in");
		  try {
		  Gson gson = new Gson();
		   System.out.println("== : "+gson.toJson(((BasicDBList)(BasicDBList)output.getCommandResult().get("result") )));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
			   List<Transaction> flist = (List<Transaction>) gson.fromJson(gson.toJson(((BasicDBObject)objList.get(0)).get("transactions")), List.class);
			   trans =  gson.fromJson(gson.toJson(flist.get(0)), Transaction.class);
		   }
		   
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 
		 return trans;
	}
	
	public static void updateAddNewTransaction(Transaction transaction,long epoch, boolean forApproval) {
		Session session = null;
		TransactionSql trnsql = new TransactionSql();
		TransactionSql trnssql = new TransactionSql();
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx=null;
			org.hibernate.Query upq;
			Criteria criteria = session.createCriteria(TransactionSql.class);
			criteria.add(Restrictions.eq("transactionId", transaction.getTransactionId()));
			criteria.add(Restrictions.eq("transactionStatus", Constants.TRANS_APROOVED));
			if(criteria.list().size()>0){
				trnssql = (TransactionSql) criteria.list().get(0);
				tx = session.beginTransaction();
				String updatetransac = null;
				if(forApproval){
					updatetransac = "update TransactionSql set amount = :amount, comment = :comment "
							+ ", updatedTime = :updatedTime , transactionStatus = :transactionStatus"
							+ ",lastEditedBy = :lastEditedBy,transactionDate = :transactionDate"
							+ " where id = :id ";	
					upq = session.createQuery(updatetransac);
					
					upq.setParameter("amount", transaction.getAmount());
					
					upq.setParameter("comment", transaction.getComment());
					
					upq.setParameter("updatedTime", epoch);
					
					upq.setParameter("transactionStatus", transaction.getTransactionStatus());
					upq.setParameter("transactionDate", transaction.getTransactionDate());
					upq.setParameter("id", trnssql.getId());
					upq.setParameter("lastEditedBy", transaction.getLastEditedBy());
					
				}else{
					updatetransac = "update TransactionSql set updatedTime = :updatedTime , transactionStatus = :transactionStatus"
							+ ",lastEditedBy = :lastEditedBy"
							+ " where id = :id ";
					upq = session.createQuery(updatetransac);
					upq.setParameter("updatedTime", epoch);
					
					upq.setParameter("transactionStatus", transaction.getTransactionStatus());
					
					upq.setParameter("id", trnssql.getId());
					upq.setParameter("lastEditedBy", transaction.getLastEditedBy());
				}
				
				int status = upq.executeUpdate();
				System.out.println("-- : "+status);
				tx.commit();
			}else
			{
				tx = session.beginTransaction();
		       
				if(transaction.getFrom().equals(transaction.getCreatedBy())){
					trnsql.setUserId(transaction.getTo());
				}else{
					trnsql.setUserId(transaction.getFrom());
				}
				trnsql.setAmount(transaction.getAmount());
				trnsql.setComment(transaction.getComment());
				trnsql.setCreatedBy(transaction.getCreatedBy());
				trnsql.setCreatedTime(transaction.getCreatedTime());
				trnsql.setDisputeAmount(transaction.getDisputeAmount());
				trnsql.setDisputeBy(transaction.getDisputeBy());
				trnsql.setDisputeFlag(transaction.getDisputeFlag());
				trnsql.setFrom(transaction.getFrom());
				trnsql.setTo(transaction.getTo());
				trnsql.setTransactionDate(transaction.getTransactionDate());
				trnsql.setTransactionId(transaction.getTransactionId());
				trnsql.setType(transaction.getType());
				trnsql.setAction(transaction.getAction());
				trnsql.setTransactionStatus(transaction.getTransactionStatus());
				trnsql.setUpdatedTime(epoch);
				trnsql.setTransactionDocId(transaction.getTransactionDocId());
				trnsql.setStaffUser(transaction.getStaffUser());;
				trnsql.setRefTransacId(transaction.getRefTransacId());
				trnsql.setReadStatus(transaction.getReadStatus());
				trnsql.setReadTime(transaction.getReadTime());
				trnsql.setReceivedTime(transaction.getReceivedTime());
				trnsql.setReadFlag(transaction.getReadFlag());
				trnsql.setModReqId(transaction.getModReqId());
				trnsql.setLastEditedBy(transaction.getLastEditedBy());
				trnsql.setSrNo(transaction.getSrNo());
				session.save(trnsql);
			
			tx.commit();
			}
						
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			
			e.printStackTrace();
			
		} finally {
			session.close();
		}
	}

	/**
	  * for checking uniqueness of transaction date
	  */
	 
	 public static List<Transaction> checkForTransactionDateForInsert(Long transactionDate, String transactionDocId){
	  /*Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
	  Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
	  boolean flag = false;
	  query.field("_id").equal(new ObjectId(transactionDocId));
	  query.filter("transactions.transactionDate",transactionDate);
	  
	  
	  if(query.asList().isEmpty()){
	   flag = true;
	  }
	  
	  return flag;*/
	  
	  Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 List<Transaction> flist = new ArrayList<Transaction>();
			//	 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
				  
				
				 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id", new ObjectId(transactionDocId)));
				  
				  BasicDBList paramList = new BasicDBList();
//				  paramList.add(new BasicDBObject("transactions.createdTime", new BasicDBObject("$gt",pullTime)));
//				  paramList.add(new BasicDBObject("transactions.transactionStatus", Constants.TRANS_APROOVED));
//				 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("$and", paramList));
				  DBObject match1 = new BasicDBObject("$match", new BasicDBObject("transactions.transactionDate", transactionDate));
				  DBObject gdb1 = new BasicDBObject();
				  gdb1.put("_id","$_id");
				  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
				  DBObject group = new BasicDBObject("$group", gdb1);
				  DBObject project = new BasicDBObject("$unwind", "$transactions");
				  AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,project,match1, group);
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
	 
	 
	 public static List<Transaction> pullPrivateUserTransaction(UserMaster user, long pullTime){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 List<Transaction> flist = new ArrayList<Transaction>();
			//	 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
				  
				 BasicDBList idList = new BasicDBList();
				 idList.add(new BasicDBObject("user1",""+user.getUserId()));
				 idList.add(new BasicDBObject("user2",""+user.getUserId()));
				 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("$or", idList));
				  
				  BasicDBList paramList = new BasicDBList();
//				  paramList.add(new BasicDBObject("transactions.createdTime", new BasicDBObject("$gt",pullTime)));
//				  paramList.add(new BasicDBObject("transactions.transactionStatus", Constants.TRANS_APROOVED));
//				 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("$and", paramList));
				  DBObject match1 = new BasicDBObject("$match", new BasicDBObject("transactions.updatedTime", new BasicDBObject("$gt",pullTime)));
				  DBObject match3 = new BasicDBObject("$match", new BasicDBObject("docType", Constants.PRIVATE_USER));
				  DBObject gdb1 = new BasicDBObject();
				  gdb1.put("_id","$_id");
				  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
				  DBObject group = new BasicDBObject("$group", gdb1);
				  DBObject project = new BasicDBObject("$unwind", "$transactions");
				  AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,project,match1,match3, group);
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
	 
	 public static List<Transaction> pullTransactions1(UserMaster user, long pullTime){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 List<Transaction> flist = new ArrayList<Transaction>();
			//	 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
				  
				 BasicDBList idList = new BasicDBList();
				 idList.add(new BasicDBObject("user1",""+user.getUserId()));
				 idList.add(new BasicDBObject("user2",""+user.getUserId()));
				 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("$or", idList));
				  
				  BasicDBList paramList = new BasicDBList();
//				  paramList.add(new BasicDBObject("transactions.createdTime", new BasicDBObject("$gt",pullTime)));
//				  paramList.add(new BasicDBObject("transactions.transactionStatus", Constants.TRANS_APROOVED));
//				 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("$and", paramList));
				 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("transactions.updatedTime", new BasicDBObject("$gt",pullTime)));
				  DBObject gdb1 = new BasicDBObject();
				  gdb1.put("_id","$_id");
				  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
				  DBObject group = new BasicDBObject("$group", gdb1);
				  DBObject project = new BasicDBObject("$unwind", "$transactions");
				  AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,project,match1, group);
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

	 
	 
	 public static List<ReadTransaction> pullReadTransactions(UserMaster user, long pullTime){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		 List<ReadTransaction> flist = new ArrayList<ReadTransaction>();
			//	 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",new ObjectId())); 
				  
				 BasicDBList idList = new BasicDBList();
				 idList.add(new BasicDBObject("user1",""+user.getUserId()));
				 idList.add(new BasicDBObject("user2",""+user.getUserId()));
				 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("$or", idList));
				 ReadTransaction readTransaction = null; 
				 
				 
				 DBObject match1 = new BasicDBObject("$match", new BasicDBObject("transactions.readTime",new BasicDBObject("$gt",pullTime)));
				 
				  DBObject gdb1 = new BasicDBObject();
				  gdb1.put("_id","$_id");
				  gdb1.put("transactions",new BasicDBObject("$push","$transactions"));
				  DBObject group = new BasicDBObject("$group", gdb1);
				  DBObject project = new BasicDBObject("$unwind", "$transactions");
				  AggregationOutput output = datastore.getCollection(TransactionDoc.class).aggregate(match2,project,match1, group);
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
							Transaction transaction = gson.fromJson(gson.toJson(iterator.next()), Transaction.class);
							System.out.println(transaction.getTransactionId());
							readTransaction = new ReadTransaction();
							readTransaction.setReadStatus(transaction.getReadStatus());
							readTransaction.setReadTime(transaction.getReadTime());
							readTransaction.setReceivedTime(transaction.getReceivedTime());
							readTransaction.setTransactionId(transaction.getTransactionId());
							flist.add(readTransaction);
						  }
					  }
			//		  flist = (List<Transaction>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("transactions")), List.class);
				   }
				   
				  } catch (Exception e) {
				   e.printStackTrace();
				  }
				 
				 return flist;
	}

	 

	public static void deleteTransactionSqlOnBlock(String frndId,
			UserMaster usermaster) {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx=null;
			tx = session.beginTransaction();
			org.hibernate.Query query=session.createQuery("DELETE FROM TransactionSql WHERE (from1 =:from1 AND to =:to) OR (from1 =:from12 AND to =:to1) ");
			query.setParameter("from1", frndId);
			query.setParameter("to", ""+usermaster.getUserId());
			query.setParameter("from12", ""+usermaster.getUserId());
			query.setParameter("to1", frndId);
			query.executeUpdate();
			tx.commit();
			System.out.println("sql transaction : deleted");			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		
	}


	public static TransactionDoc getTransactionDocForUser(
			TransactionDoc transactionDoc) {
		TransactionDoc transDoc = null;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
		query.or(
				query.and(query.criteria("user1").equal(transactionDoc.getUser1()),query.criteria("user2").equal(transactionDoc.getUser2())),
				query.and(query.criteria("user1").equal(transactionDoc.getUser2()),query.criteria("user2").equal(transactionDoc.getUser1()))
				);
		query.field("docType").equal(transactionDoc.getDocType());
		if(query.get() != null){
			
			transDoc = query.get();
		}
		return transDoc;
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
