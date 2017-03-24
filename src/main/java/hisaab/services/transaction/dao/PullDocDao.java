package hisaab.services.transaction.dao;

import java.util.Arrays;

import hisaab.config.morphia.MorphiaDatastoreTrasaction;
import hisaab.services.pull.modal.PullDoc;
import hisaab.services.staff.modal.StaffUserRequest;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.transaction.openingbalance.dao.OpeningBalDao;
import hisaab.services.transaction.openingbalance.modal.OpeningBalRequest;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import com.mongodb.BasicDBObject;

public class PullDocDao {

	public static PullDoc getPullDoc(PullDoc pullDoc ){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(PullDoc.class);
		Query<PullDoc> query = datastore.createQuery(PullDoc.class);
		query.field("userId").equal(pullDoc.getUserId());
		if(query.get() != null){
			
			pullDoc = query.get();
		}
		else{
			
			datastore.save(pullDoc);
		}
		return pullDoc;
	}
	
	public static void addUpdateApproval(TransactionDoc transactionDoc, PullDoc pullDoc, UserMaster user) {
		Transaction tempTrans = transactionDoc.getTransactions().get(0);	
		int stat = 0;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(PullDoc.class);
		long epoch = System.currentTimeMillis();
		Query<PullDoc> query = datastore.createQuery(PullDoc.class);
		query.field("userId").equal(pullDoc.getUserId());
		query.filter("transactionList.transactionId",tempTrans.getTransactionId());
		
        if(!query.asList().isEmpty()){
        	UpdateOperations<PullDoc> op = datastore.createUpdateOperations(PullDoc.class);
        	op.disableValidation();
        	op.set("transactionList.$.updatedTime", epoch);
			op.set("transactionList.$.amount", tempTrans.getAmount());
			op.set("transactionList.$.comment", tempTrans.getComment());
			op.set("transactionList.$.transactionDate",tempTrans.getTransactionDate());
			op.set("transactionList.$.transactionStatus",Constants.TRANS_APROOVED);
			op.set("transactionList.$.lastEditedBy", tempTrans.getLastEditedBy());
			op.enableValidation();
			
			UpdateResults ur = datastore.update(query,op );
			
			if(ur != null){
				stat = ur.getUpdatedCount();
			}
        
			
			

        }else{
        	Query<PullDoc> query1 = datastore.createQuery(PullDoc.class);
        	query1.field("userId").equal(pullDoc.getUserId());
        	UpdateOperations<PullDoc> op1 = datastore.createUpdateOperations(PullDoc.class);
        	op1.addAll("transactionList", transactionDoc.getTransactions(),false);
        	UpdateResults ur = datastore.update(query1,op1 );
        	if(ur != null)
    			stat = ur.getUpdatedCount();
    		
    		}
        	
	        Query<PullDoc> query3 = datastore.createQuery(PullDoc.class);
			query3.field("userId").equal(""+user.getUserId());
			query3.filter("transactionList.transactionId",tempTrans.getTransactionId());
			UpdateOperations<PullDoc> op3 = datastore.createUpdateOperations(PullDoc.class);
		
	    	op3.disableValidation();
	    	op3.set("transactionList.$.updatedTime", epoch);
			op3.set("transactionList.$.amount", tempTrans.getAmount());
			op3.set("transactionList.$.comment", tempTrans.getComment());
			op3.set("transactionList.$.transactionDate",tempTrans.getTransactionDate());
			op3.set("transactionList.$.transactionStatus",Constants.TRANS_APROOVED);
			op3.set("transactionList.$.lastEditedBy", tempTrans.getLastEditedBy());
			op3.enableValidation();
			
			UpdateResults ur3 = datastore.update(query3,op3 );
    	
			Query<PullDoc> quer = datastore.createQuery(PullDoc.class);
			quer.field("_id").equal(""+user.getUserId());
			quer.filter("modifiedTransactionList.transactionId",tempTrans.getTransactionId());
			UpdateOperations<PullDoc> updateOp= datastore.createUpdateOperations(PullDoc.class).removeAll("modifiedTransactionList", new BasicDBObject("transactionId", tempTrans.getTransactionId()));
			UpdateResults up = datastore.update(quer, updateOp);
   
		
        }
		
	public static void addTransaction(TransactionDoc transactionDoc, PullDoc pullDoc) {
		Transaction tempTrans = transactionDoc.getTransactions().get(0);	
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(PullDoc.class);
		Query<PullDoc> query = datastore.createQuery(PullDoc.class);
		query.field("userId").equal(pullDoc.getUserId());
		UpdateOperations<PullDoc> op = datastore.createUpdateOperations(PullDoc.class);
		op.addAll("transactionList", transactionDoc.getTransactions(),false);
    	UpdateResults ur = datastore.update(query,op );
    	int stat = 0;
    	if(ur != null)
			stat = ur.getUpdatedCount();
		
		
        }
		

	public static void addModifiedTransaction(TransactionDoc transactionDoc,
			PullDoc pullDoc) {
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(PullDoc.class);
		Query<PullDoc> query = datastore.createQuery(PullDoc.class);
		query.field("userId").equal(pullDoc.getUserId());
		UpdateOperations<PullDoc> op = datastore.createUpdateOperations(PullDoc.class);
        op.addAll("modifiedTransactionList", transactionDoc.getTransactions(),false);
		UpdateResults ur = datastore.update(query,op );
		int stat = 0;
		if(ur != null)
			stat = ur.getUpdatedCount();
		
	}
	
	public static void addUpdateReject(TransactionDoc transactionDoc, PullDoc pullDoc, UserMaster user) {
		Transaction tempTrans = transactionDoc.getTransactions().get(0);	
		
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(PullDoc.class);
			
			Query<PullDoc> quer = datastore.createQuery(PullDoc.class);
			quer.field("_id").equal(""+user.getUserId());
			quer.filter("modifiedTransactionList.transactionId",tempTrans.getTransactionId());
			UpdateOperations<PullDoc> updateOp= datastore.createUpdateOperations(PullDoc.class).removeAll("modifiedTransactionList", new BasicDBObject("transactionId", tempTrans.getTransactionId()));
			UpdateResults up = datastore.update(quer, updateOp);
   
		
        }

	public static void addDeleteTransaction(TransactionDoc pullTransDoc,
			PullDoc pullDoc, UserMaster user, int userResponse) {
		Transaction tempTrans = pullTransDoc.getTransactions().get(0);	
		
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(PullDoc.class);
		
		if(userResponse == Constants.ACTION_APPROVED){
		
		Query<PullDoc> query = datastore.createQuery(PullDoc.class);
		query.field("userId").equal(""+user.getUserId());
		query.filter("transactionList.transactionId",tempTrans.getTransactionId());
		
		UpdateOperations<PullDoc> updateOp= datastore.createUpdateOperations(PullDoc.class).removeAll("transactionList", new BasicDBObject("transactionId", tempTrans.getTransactionId()));
		UpdateResults up = datastore.update(query, updateOp);
        
			
		Query<PullDoc> query1 = datastore.createQuery(PullDoc.class);
		query1.field("userId").equal(pullDoc.getUserId());
		query1.filter("transactionList.transactionId",tempTrans.getTransactionId());
		
		UpdateOperations<PullDoc> updateOp1= datastore.createUpdateOperations(PullDoc.class).removeAll("transactionList", new BasicDBObject("transactionId", tempTrans.getTransactionId()));
		UpdateResults up1 = datastore.update(query1, updateOp1);

		}else{
			
		}
			Query<PullDoc> quer = datastore.createQuery(PullDoc.class);
			quer.field("_id").equal(""+user.getUserId());
			quer.filter("modifiedTransactionList.transactionId",tempTrans.getTransactionId());
			UpdateOperations<PullDoc> updateOp= datastore.createUpdateOperations(PullDoc.class).removeAll("modifiedTransactionList", new BasicDBObject("transactionId", tempTrans.getTransactionId()));
			UpdateResults up = datastore.update(quer, updateOp);
		
	}

	public static void addAndUpadteOpeningBalanceRequest(OpeningBalRequest obr,
			PullDoc pullDoc) {
			
		int stat = 0;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(PullDoc.class);
		long epoch = System.currentTimeMillis();
		Query<PullDoc> query = datastore.createQuery(PullDoc.class);
		query.field("userId").equal(pullDoc.getUserId());
		query.filter("openingBalance.id",obr.getId());
		
        if(!query.asList().isEmpty()){
        	UpdateOperations<PullDoc> op = datastore.createUpdateOperations(PullDoc.class);
        	op.disableValidation();
        	op.set("openingBalance.$.requesterUserId", obr.getRequesterUserId());
			op.set("openingBalance.$.updatedDate", obr.getUpdatedTime());
			op.set("openingBalance.$.forUserId", obr.getForUserId());
			op.set("openingBalance.$.transactionDate", obr.getOpeningBalDate());
			op.set("openingBalance.$.openingBalAmt", obr.getOpeningBalAmt());
			op.set("openingBalance.$.paymentStatus", obr.getPaymentStatus());
			op.set("openingBalance.$.createdTime", obr.getCreatedTime());
			op.set("openingBalance.$.status", obr.getStatus());
			op.enableValidation();
			
			UpdateResults ur = datastore.update(query,op );
			
			if(ur != null){
				stat = ur.getUpdatedCount();
			}
        

        }else{
        	Query<PullDoc> query1 = datastore.createQuery(PullDoc.class);
        	query1.field("userId").equal(pullDoc.getUserId());
        	UpdateOperations<PullDoc> op1 = datastore.createUpdateOperations(PullDoc.class);
        	op1.addAll("openingBalance", Arrays.asList(obr),false);
        	UpdateResults ur = datastore.update(query1,op1 );
        	if(ur != null)
    			stat = ur.getUpdatedCount();
    		}
		
	}

	public static void UpadteOpeningBalanceResponse(OpeningBalRequest req,
			PullDoc pullDoc, int userResponse) {
		int stat = 0;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(PullDoc.class);
		long epoch = System.currentTimeMillis();
		Query<PullDoc> query = datastore.createQuery(PullDoc.class);
		query.field("userId").equal(pullDoc.getUserId());
		query.filter("openingBalance.id",req.getId());
		
        if(!query.asList().isEmpty()){
        	UpdateOperations<PullDoc> op = datastore.createUpdateOperations(PullDoc.class);
        	op.disableValidation();
			op.set("openingBalance.$.updatedDate", epoch);
			op.set("openingBalance.$.status", userResponse);
			op.enableValidation();
			
			UpdateResults ur = datastore.update(query,op );
			
			if(ur != null){
				stat = ur.getUpdatedCount();
			}
        

        }else{
        	OpeningBalRequest obr = new OpeningBalRequest();
        	UserMaster user = new UserMaster();
        	user.setUserId(Long.parseLong(req.getForUserId()));
        	obr = OpeningBalDao.getOpeningBalRequest(req.getId(), 0, user);
        	Query<PullDoc> query1 = datastore.createQuery(PullDoc.class);
        	query1.field("userId").equal(pullDoc.getUserId());
        	UpdateOperations<PullDoc> op1 = datastore.createUpdateOperations(PullDoc.class);
        	op1.addAll("openingBalance", Arrays.asList(obr),false);
        	UpdateResults ur = datastore.update(query1,op1 );
        	if(ur != null)
    			stat = ur.getUpdatedCount();
    		}
        
        
		
	}

	public static void addStaffRequest(PullDoc pullDoc,
			StaffUserRequest staffUser) {
		
		/*Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(PullDoc.class);
		Query<PullDoc> query = datastore.createQuery(PullDoc.class);
		query.field("userId").equal(pullDoc.getUserId());
		query.filter("openingBalance.id",req.getId());
		UpdateOperations<PullDoc> op = datastore.createUpdateOperations(PullDoc.class);
		op.addAll("staffRequestsForYou", Arrays.asList(staffUser),false);
    	UpdateResults ur = datastore.update(query,op );
    	int stat = 0;
    	if(ur != null)
			stat = ur.getUpdatedCount();*/
		
		
		
	}

	public static void addAndUpdateStaffRequestForYou(PullDoc pullDoc,
			StaffUserRequest staffUser,int status) {
		int stat = 0;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(PullDoc.class);
		Query<PullDoc> query = datastore.createQuery(PullDoc.class);
		query.field("userId").equal(pullDoc.getUserId());
		query.filter("staffRequestsForYou.id",staffUser.getId());
		
        if(!query.asList().isEmpty()){
        	UpdateOperations<PullDoc> op = datastore.createUpdateOperations(PullDoc.class);
        	op.disableValidation();
        	op.set("staffRequestsForYou.$.ownerId",staffUser.getOwnerId());
        	op.set("staffRequestsForYou.$.contactNo",staffUser.getContactNo());
			op.set("staffRequestsForYou.$.staffUserId", staffUser.getStaffUserId());
			op.set("staffRequestsForYou.$.createdTime", staffUser.getCreatedTime());
			op.set("staffRequestsForYou.$.updatedTime", staffUser.getUpdatedTime());
			op.set("staffRequestsForYou.$.Status", status);
			op.set("staffRequestsForYou.$.securityCode", staffUser.getSecurityCode());
			op.set("staffRequestsForYou.$.displayName", staffUser.getDisplayName());
			op.set("staffRequestsForYou.$.ownerName", staffUser.getOwnerName());
			op.set("staffRequestsForYou.$.ownerContactNo", staffUser.getOwnerContactNo());
						op.enableValidation();
			
			UpdateResults ur = datastore.update(query,op );
			
			if(ur != null){
				stat = ur.getUpdatedCount();
			}
        

        }else{
        	Query<PullDoc> query1 = datastore.createQuery(PullDoc.class);
        	query1.field("userId").equal(pullDoc.getUserId());
        	UpdateOperations<PullDoc> op1 = datastore.createUpdateOperations(PullDoc.class);
        	staffUser.setStatus(status);
        	op1.addAll("staffRequestsForYou", Arrays.asList(staffUser),false);
        	UpdateResults ur = datastore.update(query1,op1 );
        	if(ur != null)
    			stat = ur.getUpdatedCount();
    		}
		
	}

	public static void setStatusToStaffUserRequest(StaffUserRequest st,
			PullDoc pullDoc, int status) {
		int stat = 0;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(PullDoc.class);
		Query<PullDoc> query = datastore.createQuery(PullDoc.class);
		query.field("userId").equal(pullDoc.getUserId());
		query.filter("staffRequests.id",st.getId());
		st.setStatus(status);
        if(!query.asList().isEmpty()){
        	UpdateOperations<PullDoc> op = datastore.createUpdateOperations(PullDoc.class);
        	op.disableValidation();
        	op.set("staffRequests.$.ownerId",st.getOwnerId());
        	op.set("staffRequests.$.contactNo",st.getContactNo());
			op.set("staffRequests.$.staffUserId", st.getStaffUserId());
			op.set("staffRequests.$.createdTime", st.getCreatedTime());
			op.set("staffRequests.$.updatedTime", st.getUpdatedTime());
			op.set("staffRequests.$.Status", st.getStatus());
			op.set("staffRequests.$.securityCode", st.getSecurityCode());
			op.set("staffRequests.$.displayName", st.getDisplayName());
			op.set("staffRequests.$.ownerName", st.getOwnerName());
			op.set("staffRequests.$.ownerContactNo", st.getOwnerContactNo());
						op.enableValidation();
			
			UpdateResults ur = datastore.update(query,op );
			
			if(ur != null){
				stat = ur.getUpdatedCount();
			}
        }else{
        	Query<PullDoc> query1 = datastore.createQuery(PullDoc.class);
        	query1.field("userId").equal(pullDoc.getUserId());
        	UpdateOperations<PullDoc> op1 = datastore.createUpdateOperations(PullDoc.class);
        	op1.addAll("staffRequests", Arrays.asList(st),false);
        	UpdateResults ur = datastore.update(query1,op1 );
        	if(ur != null)
    			stat = ur.getUpdatedCount();
    		}

        
		
	}
	
}
