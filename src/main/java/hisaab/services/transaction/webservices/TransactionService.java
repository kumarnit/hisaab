package hisaab.services.transaction.webservices;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import hisaab.services.Logs.LogHelper;
import hisaab.services.Logs.LogModel;
import hisaab.services.contacts.ContactHelper;
import hisaab.services.contacts.dao.ContactsDao;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.Contact;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.contacts.services.bean.FriendListBean;
import hisaab.services.contacts.services.bean.PrivateUserBean;
import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.transaction.request.dao.ModificationRequestDao;
import hisaab.services.transaction.request.modal.ModificationRequest;
import hisaab.services.transaction.request.webservices.ModificaltionBean;
import hisaab.services.transaction.webservices.bean.ReadBean;
import hisaab.services.transaction.webservices.bean.TransDocBean;
import hisaab.services.transaction.webservices.bean.TransactionBean;
import hisaab.services.transaction.webservices.bean.TransactionBean1;
import hisaab.services.transaction.webservices.bean.TransactionDisputeBean;
import hisaab.services.user.dao.PrivateUserDao;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.PrivateUser;
import hisaab.services.user.modal.UserMaster;
import hisaab.services.viewlog.helper.TransactionLogHelper;
import hisaab.util.Constants;
import hisaab.util.DeleteDB;
import hisaab.util.ExcecutorHelper;
import hisaab.util.ExecutionTimeLog;
import hisaab.util.ServiceResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Path("v1/transaction")
public class TransactionService {

	
	@POST
	@Path("/add/{userId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response addTransaction(@HeaderParam("authToken") String authToken, 
	 	         TransactionBean transBean, @PathParam("userId") String uid,
	 	         @HeaderParam("authId") String authId){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		Object result = null;
		List<Transaction> rejectedTransaction = null;
		TransDocBean trnsDocBean = new TransDocBean();
//		try{
			ObjectMapper mapper = new ObjectMapper();
			
			String req = "token : "+authToken+", userId : "+uid+", authId :"+authId;
			try {
				req += mapper.writeValueAsString(transBean);
			} catch (Exception e) {
			
				e.printStackTrace();
			}
			String res = "";
			LogModel logModel = new LogModel();
			logModel.setUserToken(authToken);
			
			TransactionDoc transDoc = new TransactionDoc();
			long epoch = System.currentTimeMillis();
			UserMaster user = null;
			if(Constants.AUTH_USERID){
				user = UserDao.getUserFromAuthToken1(authToken,authId);
			}
			else{
				user = UserDao.getUserFromAuthToken(authToken);
			}
			if(user.getUserId()>0){
				UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
				logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
				FriendList frndList = FriendsDao.getAssociatedUserDoc(user);
				FriendContact frnd = null;
				for(FriendContact fn : frndList.getFriends()){
					if(fn.getFrndId().equals(""+uid))
						frnd = fn;
				}
			
				if(frnd != null){
					if(frnd.getFrndStatus() != Constants.BLOCKED_USER){
						transDoc.setUser1(""+user.getUserId());
						transDoc.setUser2(""+uid);
						if(frnd.getFrndStatus() == Constants.NOT_REGISTERED_USER || 
								frnd.getFrndStatus() == Constants.PRIVATE_USER || frnd.getFrndStatus() == Constants.STAFF_USER)
							transDoc.setDocType(frnd.getFrndStatus());
						transDoc = TransactionDao.getTransactionDoc(transDoc);
						if(frnd.getFrndStatus() == 0)
							ContactHelper.checkAndAddAssociate(frnd, user, uid, transDoc);
						Iterator<Transaction> itr = transBean.getTransactions().iterator();
						while(itr.hasNext()){
							Transaction trans =itr.next();
							List<Transaction> transresponse =TransactionDao.checkForTransactionDateForInsert(trans.getTransactionDate(), transDoc.get_id().toString());
							if(transresponse.isEmpty())
							{
//								transDoc.getTransactions().add(trans);
								
							}
							else{
								trnsDocBean.getRejectedTransaction().add(transresponse.get(0));
								itr.remove();
							}
						}
						transDoc.setTransactions(transBean.getTransactions());
						long lastCount = transDoc.getIdCount();
						for(Transaction t : transBean.getTransactions()){
							t.setTransactionId(transDoc.getUser1()+"_"+transDoc.getUser2()+"_"+(++lastCount));
							t.setSrNo(lastCount);
							t.setTransactionDocId(transDoc.getIdString());
							t.setCreatedTime(epoch);
							t.setUpdatedTime(epoch);
							t.setCreatedBy(""+user.getUserId());
							t.setSyncFlag(0);
							t.setReadStatus(0);
						}
						transDoc.setUpdatedTime(epoch);
						transDoc.setIdCount(lastCount);
						if(transDoc.getTransactions().size()>0)
						{
							TransactionDao.addTransactions(transDoc, user);
								if(transDoc.getDocType() == 0)
									TransactionDao.addTransactiontoSql(transDoc.getTransactions());
							try{
								TransactionLogHelper.saveTransLog(user, transDoc.getTransactions());
								}catch(Exception e){
									e.printStackTrace();
									System.out.print("Exception in add Transaction save Log :"+e);
								}
						}
						
						transDoc.setModifiedTransactions(null);
						trnsDocBean.setTransDoc(transDoc);
						trnsDocBean.setMsg("Success");
						trnsDocBean.setStatus(200);
						result = trnsDocBean;
		           
					}else{
						
						/***
						 *get private user for blocked if exist.
						 ***/
						PrivateUser prUser = null;
						FriendContact pr = FriendsDao.getFriendForBlocked(uid, 0, user);
						if(pr != null)
							prUser = PrivateUserDao.getPrivateUserByIdFor(pr.getFrndId());
						PrivateUserBean flb = new PrivateUserBean();
						flb.setMsg("Specified user is blocked. To add transaction use this private user.");
						flb.setStatus(406);
						flb.setFriendContact(pr);
						if(prUser != null)
							flb.setPrivateUser(prUser);
						result = flb;
					}
						
				}else{
					result = ServiceResponse.getResponse(405, "Specified user is not associated with You");
				}
			}else{
				result = ServiceResponse.getResponse(401, "Invalid Auth Token");
			}
			
			
			try {
				res = mapper.writeValueAsString(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
	try{
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName("Add transaction");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
	}catch(Exception e){
		System.out.println("Exception in Add Log Record , in add Transaction: "+e);
	}
		/*}catch(Exception e){
			System.out.println("Exception in add Transaction : \n"+e.getMessage());
			   result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		*/
		timer.stop();
		timer.setMethodName("add_transaction");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

	
	@POST
	@Path("/markRead")
	@Consumes("application/json")
	@Produces("application/json")
	public Response updateTransactionAsRead(@HeaderParam("authToken") String authToken,
			    @HeaderParam("authId")String authId, ReadBean rBean ){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		Object result = null;
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", authId :"+authId +" transID"+rBean;
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		try{
			TransactionDoc transDoc = new TransactionDoc();
			long epoch = System.currentTimeMillis();
			UserMaster user = null;
			if(Constants.AUTH_USERID){
				user = UserDao.getUserFromAuthToken1(authToken,authId);
			}
			else{
				user = UserDao.getUserFromAuthToken(authToken);
			}
			if(user.getUserId()>0){
				UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
				if(TransactionDao.markTransactionsAsReadInSql(rBean.getTransIds(), user, 1)){
					result = ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE, "transactions marked as read.");
				}
				else
					result = ServiceResponse.getResponse(Constants.FAILURE, "Failed to mark transactions read.");
			}else{
				result = ServiceResponse.getResponse(401, "Invalid Auth Token");
			}
			try{
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName("update transaction as Read");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
			}catch(Exception e){
				System.out.println("Exception in Add Log Record , update transaction aS rEAD : "+e);
			}
		}catch(Exception e){
			System.out.println("Exception in Read transaction   : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
			   }
		timer.stop();
		timer.setMethodName("mark_transaction_read");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

	
	
	@POST
	@Path("/read/{readStatus}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response updateTransactionAsRead2(@HeaderParam("authToken") String authToken,
				@HeaderParam("authId") String authId, @PathParam("readStatus") int readStatus,
			     ReadBean rBean ){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		Object result = null;
		
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", authId :"+authId +" transID"+rBean;
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		try{
			TransactionDoc transDoc = new TransactionDoc();
			long epoch = System.currentTimeMillis();
			UserMaster user = null;
			if(Constants.AUTH_USERID){
				user = UserDao.getUserFromAuthToken1(authToken,authId);
			}
			else{
				user = UserDao.getUserFromAuthToken(authToken);
			}
			if(user.getUserId()>0){
				UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
				List<String> failed = new ArrayList<String>();
				if(!rBean.getTransIds().isEmpty())
				{
				TransactionDao.markTransactionsAsReadInSql(rBean.getTransIds(), user, readStatus);
				if(TransactionDao.updateReadStatusForTransaction(rBean.getTransIds(), user,readStatus, failed)){
					result = ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE, "transactions marked as read.");
				}
				else
					result = ServiceResponse.getResponse(Constants.FAILURE, "Failed to mark transactions read.");
			}else{
				result = ServiceResponse.getResponse(406, "transIds cannot be empty.");
				}
			}
				else{
				result = ServiceResponse.getResponse(401, "Invalid Auth Token");
			}
			
			try{
				logModel.setRequestData(req);
				logModel.setResponseData(res);
				logModel.setRequestName("update transaction as Read");
				if(Constants.RECORD_LOGS)
					LogHelper.addLogHelper(logModel);
				}catch(Exception e){
					System.out.println("Exception in Add Log Record , update transaction aS rEAD 2 : "+e);
				}
			
		}catch(Exception e){
			System.out.println("Exception in Read transaction 2 : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		timer.stop();
		timer.setMethodName("mark_transaction_read2");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

	
	@POST
	@Path("/dispute")
	@Consumes("application/json")
	@Produces("application/json")
	public Response disputeTransaction(@HeaderParam("authToken") String authToken, 
	 	        @HeaderParam("authId") String authId, TransactionDisputeBean transDispBean ){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		Object result = null;
		try{
			ObjectMapper mapper = new ObjectMapper();
	
			String req = "token : "+authToken+", disputeTransaction : "+", authId :"+authId;
			try {
				req += mapper.writeValueAsString(transDispBean);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String res = "";
			LogModel logModel = new LogModel();
			logModel.setUserToken(authToken);
			TransDocBean tranbean = new TransDocBean();
			TransactionDoc transDoc = null;
			long epoch = System.currentTimeMillis();
			UserMaster user = null;
			if(Constants.AUTH_USERID){
				user = UserDao.getUserFromAuthToken1(authToken,authId);
			}
			else{
				user = UserDao.getUserFromAuthToken(authToken);
			}
			if(user.getUserId()>0){
				UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
				logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
				if(ObjectId.isValid(transDispBean.getTransactionDocId()))
					transDoc = TransactionDao.disputeTransaction(transDispBean, user);
				
					result = ServiceResponse.getResponse(Constants.INVALID_PARAM, "Invalid TransactionDocId or TransactionId");
				
					if(transDoc!=null){
						tranbean.setTransDoc(transDoc);
						tranbean.setMsg("Success");
						tranbean.setStatus(Constants.SUCCESS_RESPONSE);
						result = tranbean;
					} 
				
			}else{
				result = ServiceResponse.getResponse(401, "Invalid Auth Token");
			}
			
			try {
				res = mapper.writeValueAsString(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName("dispute transaction");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
		}catch(Exception e)
		{
			System.out.println("Exception in Request dispute transaction : \n"+e.getMessage());
		    result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		timer.stop();
		timer.setMethodName("dispute_transaction");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

	
	@GET
	@Path("/delete/{transacId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response deleteTransaction(@HeaderParam("authToken") String authToken, 
	 	         @HeaderParam("transacDocId") String transacDocId, 
	 	         @PathParam("transacId") String transacId, 
	 	         @HeaderParam("authId") String authId){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		Object result = null;
		try{
			ObjectMapper mapper = new ObjectMapper();
			String req = "token : "+authToken+", transacDocId : "+transacDocId+", transacId :"
					+ ""+transacId+", authId :"+authId;
			
			String res = "";
			LogModel logModel = new LogModel();
			logModel.setUserToken(authToken);
			
			TransDocBean trnsDocBean = new TransDocBean();
			
			TransactionDoc transDoc = new TransactionDoc();
			long epoch = System.currentTimeMillis();
			System.out.println("transacId : "+transacId);
			System.out.println("transacDocId : "+transacDocId);
			UserMaster user = null;
			if(Constants.AUTH_USERID){
				user = UserDao.getUserFromAuthToken1(authToken,authId);
			}
			else{
				user = UserDao.getUserFromAuthToken(authToken);
			}
			if(user.getUserId()>0){
				UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
				logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
				if(transacDocId != null && transacId != null && ObjectId.isValid(transacDocId)){
					TransactionDoc trndocs = TransactionDao.getTransactionDocByDocId(transacDocId);
					if(trndocs != null && trndocs.getDocType() != 5){
						ModificationRequest modReq = ModificationRequestDao.getModificationRequestFor(transacId);
						if(modReq == null)
						{
							if(TransactionDao.deleteTransaction(transacId, transacDocId, user)){
								trndocs = TransactionDao.getTransactionDocByDocId(transacDocId);
								if(trndocs.getDocType() != 0){
									trndocs.setBackedUptransactions(null);
									trndocs.setModifiedTransactions(null);
									trndocs.setTransactions(null);
									
									trnsDocBean.setMsg(" successfull");
									trnsDocBean.setStatus(Constants.SUCCESS_RESPONSE);
									trnsDocBean.setTransDoc(trndocs);
									result = trnsDocBean;
								}
								else
								result = ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE, "transaction deleted successfull");
							}
							else{
								result = ServiceResponse.getResponse(501, "Delete operation Failed");
							}
						}else{
							ModificaltionBean modBean = new ModificaltionBean();
							modBean.setModRequest(modReq);
							modBean.setMsg("Already have Pending Request");
							modBean.setStatus(205);
							result = modBean;
						}
					}else{
						result = ServiceResponse.getResponse(406, "Blocked User Transaction");
					}
					
				}else{
					result = ServiceResponse.getResponse(402, "transaction Id or transaction Doc Id cannot be null or Invalid");
				}
				 
			}else{
				result = ServiceResponse.getResponse(401, "Invalid Auth Token");
			}
			
			try {
				res = mapper.writeValueAsString(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			try{
				logModel.setRequestData(req);
				logModel.setResponseData(res);
				logModel.setRequestName("delete transaction");
				if(Constants.RECORD_LOGS)
					LogHelper.addLogHelper(logModel);
				}catch(Exception e){
					System.out.println("Exception in Add Log Record , delete transaction : "+e);
				}
		
		}catch(Exception e){
			System.out.println("Exception in delete transaction : \n"+e.getMessage());
		    result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		timer.stop();
		timer.setMethodName("delete_transaction");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

	
	
	@POST
	@Path("/privateUsers/add/{userId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response addUnmanagedUserTransaction(@HeaderParam("authToken") String authToken, 
	 	         TransactionBean transBean, @PathParam("userId") String uid,
	 	         @HeaderParam("authId") String authId){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		Object result = null;
		
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", authId :"+authId +" userId : "+uid;
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		try{
		TransactionDoc transDoc = new TransactionDoc();
		long epoch = System.currentTimeMillis();
		UserMaster user = null;
		if(Constants.AUTH_USERID){
			user = UserDao.getUserFromAuthToken1(authToken,authId);
		}
		else{
			user = UserDao.getUserFromAuthToken(authToken);
		}
		if(user.getUserId()>0){
			UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
			transDoc.setUser1(""+user.getUserId());
			transDoc.setUser2(""+uid);
			transDoc.setDocType(Constants.NOT_REGISTERED_USER);
			transDoc = TransactionDao.getTransactionDocForUnmanagedUser(transDoc);
			transDoc.setTransactions(transBean.getTransactions());
			long lastCount = transDoc.getIdCount();
			for(Transaction t : transBean.getTransactions()){
				t.setTransactionId(transDoc.getUser1()+"_"+transDoc.getUser2()+"_"+(++lastCount));
				t.setCreatedTime(epoch);
				t.setCreatedBy(""+user.getUserId());
			}
			transDoc.setIdCount(lastCount);
			TransactionDao.addTransactions(transDoc, user);
//			TransactionDao.addTransactiontoSql(transDoc.getTransactions());

			transBean.setTransactions(transDoc.getTransactions());
			transBean.setMsg("Success");
			transBean.setStatus(200);
			result = transBean;
		}else{
			result = ServiceResponse.getResponse(401, "Invalid Auth Token");
		}
		
		try{
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName("Add private user transaction");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
			}catch(Exception e){
				System.out.println("Exception in Add Log Record , Add private user transaction : "+e);
			}
		
		}catch(Exception e){
			System.out.println("Exception in Add private user transaction: \n"+e.getMessage());
		    result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		timer.stop();
		timer.setMethodName("add_unmanaged_user_trans");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

	@POST
	@Path("/updatetransaction")
	@Consumes("application/json")
	@Produces("application/json")
	public Response updateTransactions(@HeaderParam("authToken") String authToken, 
	 	         @HeaderParam("authId") String authId, TransactionBean1 transBean){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		Object result = null;
//		try{
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", transactionBean : "+", authId :"+authId;
		try {
			req += mapper.writeValueAsString(transBean);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		
		
		int status;
		TransDocBean  transBean1 = new TransDocBean();
		TransactionDoc trnsDoc = null;
		long epoch = System.currentTimeMillis();
		UserMaster user = null;
		if(Constants.AUTH_USERID){
			user = UserDao.getUserFromAuthToken1(authToken,authId);
		}
		else{
			user = UserDao.getUserFromAuthToken(authToken);
		}
		if(user.getUserId()>0){
			UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
			logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
			if(transBean.getTransactions().getTransactionDocId()!= null
					&& ObjectId.isValid(transBean.getTransactions().getTransactionDocId())){
				ModificationRequest modReq = ModificationRequestDao.getModificationRequestFor(transBean.getTransactions().getTransactionId());
			    if(modReq == null){
					trnsDoc = TransactionDao.updateTransaction2(transBean.getTransactions(),user);
					
					if(trnsDoc!=null)
					{
						if(trnsDoc.getModifiedTransactions().get(0).getSyncFlag() == 2){
							result = ServiceResponse.getResponse(406, "Blocked User Transaction");
						}else{
							transBean1.setTransDoc(trnsDoc);
							transBean1.setStatus(Constants.SUCCESS_RESPONSE);
							transBean1.setMsg("Success");
							result=transBean1;
						}
						
					}
					else{
						result = ServiceResponse.getResponse(Constants.DB_FAILURE, "DataBase updation failed");
					}
				}else{
					ModificaltionBean modBean = new ModificaltionBean();
					modBean.setModRequest(modReq);
					modBean.setMsg("Already have Pending Request");
					modBean.setStatus(205);
					result = modBean;
				}
			}
			else{
				result = ServiceResponse.getResponse(Constants.INVALID_PARAMS, "transactionDoc id cannot be null");
			}
			
		}else{
			result = ServiceResponse.getResponse(401, "Invalid Auth Token");
		}
		

		try {
			res = mapper.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try{
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName("update transaction");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
			}catch(Exception e){
				System.out.println("Exception in Add Log Record , update transaction : "+e);
			}
		/*}catch(Exception e){
			System.out.println("Exception in update transaction Service : \n"+e.getMessage());
		    result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}*/
		timer.stop();
		timer.setMethodName("update_transaction");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	
	@GET
	@Path("/action/{action}/{transacId}/{response}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response transactionAction(@HeaderParam("authToken") String authToken,
				@HeaderParam("requestId") long reqId,
	 	         @HeaderParam("transacDocId") String transacDocId, 
	 	         @PathParam("transacId") String transacId, @PathParam("action") int action,
	 	         @PathParam("response") int userResponse,
	 	         @HeaderParam("authId") String authId){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		Object result = null;
		try{
		ObjectMapper mapper = new ObjectMapper();
		String req1 = "token : "+authToken+", transacDocId : "+transacDocId+", transactionId :"+transacId+
				", action :"+action+", response :"+userResponse+", authId :"+authId;
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		TransDocBean trnsDocBean = new TransDocBean();
		
		
		TransactionDoc transDoc = null;
		long epoch = System.currentTimeMillis();
		UserMaster user = null;
		if(Constants.AUTH_USERID){
			user = UserDao.getUserFromAuthToken1(authToken,authId);
		}
		else{
			user = UserDao.getUserFromAuthToken(authToken);
		}
//		TransactionDoc transDoc = null;
		if(user.getUserId()>0){
			UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
			logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
			if(transacDocId != null && transacId != null){
			
				ModificationRequest req = ModificationRequestDao.getModificationRequest(transacId, action, user);
				
				if(req!= null){
					if(reqId == req.getId()){
						int i = 0;
						switch (action) {
						case Constants.TRANSACTION_UPDATE:
							if(userResponse == 111 || userResponse == 112)
							{
							try {
								i =	TransactionDao.processResponseForTransactionUpdate(transacId, transacDocId,
										user, userResponse, req,false);
								
							} catch (Exception e) {
								System.out.println("Exception :1 "+e.getMessage());
								System.out.println("in approve transaction action.");
								i = -1;
							}
							
							if(i == 0)
							{   
								transDoc = TransactionDao.getTransactionDocByDocId(transacDocId);  
								transDoc.setBackedUptransactions(null);
								transDoc.setTransactions(null);
								trnsDocBean.setMsg(" successfull");
								trnsDocBean.setStatus(Constants.SUCCESS_RESPONSE);
								trnsDocBean.setTransDoc(transDoc);
								result = trnsDocBean;
							}else if(i == 1)
								result = ServiceResponse.getResponse(Constants.FAILURE, "Invalid Transaction Doc id.");
							else
								result = ServiceResponse.getResponse(Constants.FAILURE, "Invalid Transaction id.");
							}else
								result = ServiceResponse.getResponse(Constants.FAILURE, " Invalid Response type");
							break;
							
						case Constants.TRANSACTION_DELETE: 
							if(userResponse == 111 || userResponse == 112)
							{
							try {
								i =	TransactionDao.processResponseForTransactionDelete(transacId, transacDocId,
										user, userResponse, req,false);
							} catch (Exception e) {
								System.out.println("Exception :2 "+e.getMessage());
								System.out.println("in approve transaction action.");
								i = -1;
							}
							
				
							if(i == 0){
								    transDoc = TransactionDao.getTransactionDocByDocId(transacDocId);  
									transDoc.setBackedUptransactions(null);
									transDoc.setTransactions(null);
									trnsDocBean.setMsg(" successfull");
									trnsDocBean.setStatus(Constants.SUCCESS_RESPONSE);
									trnsDocBean.setTransDoc(transDoc);
									result = trnsDocBean;
									
							}else if(i == 1)
								result = ServiceResponse.getResponse(Constants.FAILURE, "Invalid Transaction Doc id.");
							else
								result = ServiceResponse.getResponse(Constants.FAILURE, "Invalid Transaction id.");
							}else
								result = ServiceResponse.getResponse(Constants.FAILURE, " Invalid Response type");
							
							break;
						default:
							result = ServiceResponse.getResponse(Constants.FAILURE, "Invalid Action.");
							break;
						}
					}
					else{ 
						TransactionBean1 transBean = new TransactionBean1();
						Transaction trans = null;
						int flag = 0;
						try {
							trans = TransactionDao.getTransactionForWeb(transacId, transacDocId, user);
						} catch (Exception e) {
							System.out.println("Exception :3 "+e.getMessage());
							System.out.println("in approve transaction action.");
							flag = -1;
						}
						
						if(flag == 0 && trans != null){
							transBean.setMsg("Your Action has been timed out here is the latest update request for transaction.");
							transBean.setStatus(203);
							transBean.setTransactions(trans);
							result = transBean;
						}
						else
							result = ServiceResponse.getResponse(402, "transaction Id or transaction Doc Id cannot be null or invalid");
					}
				}
				else{
					ModificationRequest modReq = ModificationRequestDao.getModificationRequest1(transacId, user,reqId);
					ModificaltionBean modBean = new ModificaltionBean();	
					if(modReq != null){
						modBean.setModRequest(modReq);
						modBean.setMsg("Request is aprroved already ");
						modBean.setStatus(205);
						result = modBean;
					}else
					result = ServiceResponse.getResponse(501, "No action Request Found for this transaction id.");
				}
			}else{
				result = ServiceResponse.getResponse(402, "transaction Id or transaction Doc Id cannot be null");
			}
			 
		}else{
			result = ServiceResponse.getResponse(401, "Invalid Auth Token");
		}
		
		
		try {
			res = mapper.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try{
			logModel.setRequestData(req1);
			logModel.setResponseData(res);
			logModel.setRequestName("transaction Action Response");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
			}catch(Exception e){
				System.out.println("Exception in Add Log Record , transaction Action Response : "+e);
			}
		}catch(Exception e){
			System.out.println("Exception in transaction action services : \n"+e.getMessage());
		    result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		timer.stop();
		timer.setMethodName("transaction_action");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

	
	
	@POST
	@Path("/sync/update")
	@Consumes("application/json")
	@Produces("application/json")
	public Response updateMultipleTransactions(@HeaderParam("authToken") String authToken, 
	 	         @HeaderParam("authId") String authId, TransactionBean transBean){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		Object result = null;
		try{
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", transactionBean : "+", authId :"+authId+", authId :"+authId;
		try {
			req += mapper.writeValueAsString(transBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		
		UserMaster user =null;
		if(Constants.AUTH_USERID){
			user = UserDao.getUserFromAuthToken1(authToken,authId);
		}
		else{
			user = UserDao.getUserFromAuthToken(authToken);
		}
		if(user.getUserId()>0){
			UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
			 logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
			 TransactionDao.syncUpdate(transBean.getTransactions(),user);
			 transBean.setStatus(Constants.SUCCESS_RESPONSE);
			 transBean.setMsg("Success");
			 result=transBean;
		}
		else{
			result = ServiceResponse.getResponse(Constants.INVALID_PARAM, "Invalid Auth Token");
		}
		try {
			res = mapper.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try{
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName("update mutiple transaction");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
			}catch(Exception e){
				System.out.println("Exception in Add Log Record , update mutiple transaction : "+e);
			}
		
		}catch(Exception e){
			System.out.println("Exception in update mutiple transaction : \n"+e.getMessage());
		    result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		timer.stop();
		timer.setMethodName("update_multi_transaction");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	/*@GET
	@Path("/delete/DatabaseOnlyMe")
	@Produces("application/json")
	public Response DeleteDatabase(){
		
		ObjectMapper mapper = new ObjectMapper();
		String req = "";
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken("");
		
		Object result = null;
		
		if(DeleteDB.deleteDataBase())
			result = ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE, "successfully truncated");
		else
			result = ServiceResponse.getResponse(Constants.DB_FAILURE, "failed");
		try {
			res = mapper.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logModel.setRequestData(req);
		logModel.setResponseData(res);
		logModel.setRequestName("databse delete");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}*/
	
	
	@POST
	@Path("/get/backup")
	@Consumes("application/json")
	@Produces("application/json")
	public Response getUpdateTransactionBackup(@HeaderParam("authToken") String authToken,
			     @HeaderParam("authId") String authId, ReadBean rBean ){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		Object result = null;
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", authId :"+authId;
		
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		try{
			TransactionDoc transDoc = new TransactionDoc();
			long epoch = System.currentTimeMillis();
			UserMaster user = null;
			if(Constants.AUTH_USERID){
				user = UserDao.getUserFromAuthToken1(authToken,authId);
			}
			else{
				user = UserDao.getUserFromAuthToken(authToken);
			}
			if(user.getUserId()>0){
				if(rBean.getTransIds()!= null && !rBean.getTransIds().isEmpty()){
					TransactionBean transBean = new TransactionBean();
					transBean.setTransactions(TransactionDao.getBackedupTransactionListForUser(rBean.getTransIds(), ""+user.getUserId()));
					transBean.setStatus(Constants.SUCCESS_RESPONSE);
					transBean.setMsg("Success");
					result = transBean;
				}
				else{
					result = ServiceResponse.getResponse(Constants.INVALID_PARAMS, "transIds list cannot be null or empty.");
				}
					
			}else{
				result = ServiceResponse.getResponse(401, "Invalid Auth Token");
			}
			

			try {
				result = mapper.writeValueAsString(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try{
				logModel.setRequestData(req);
				logModel.setResponseData(res);
				logModel.setRequestName("get update transaction backup");
				if(Constants.RECORD_LOGS)
					LogHelper.addLogHelper(logModel);
				}catch(Exception e){
					System.out.println("Exception in Add Log Record , get update transaction backup : "+e);
				}
		
		}catch(Exception e){
				System.out.println("Exception in get update transaction backup: \n"+e.getMessage());
			    result = ServiceResponse.getResponse(507, "Server was unable to process the request");
			}
		timer.stop();
		timer.setMethodName("get_update_transaction_backup");
		ExcecutorHelper.addExecutionLog(timer.toString());
			return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
		}
}
