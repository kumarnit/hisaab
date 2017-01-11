package hisaab.services.transaction.staff_transaction.services;

import hisaab.services.Logs.LogHelper;
import hisaab.services.Logs.LogModel;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.staff.dao.StaffUserDao;
import hisaab.services.staff.modal.StaffUser;
import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.transaction.staff_transaction.dao.StaffTransactionDao;
import hisaab.services.transaction.staff_transaction.modal.StaffTransactionDoc;
import hisaab.services.transaction.staff_transaction.services.bean.ApprovalBean;
import hisaab.services.transaction.staff_transaction.services.bean.ApproveBean;
import hisaab.services.transaction.staff_transaction.services.bean.StaffTransactionBean;
import hisaab.services.transaction.webservices.bean.ReadBean;
import hisaab.services.transaction.webservices.bean.TransDocBean;
import hisaab.services.transaction.webservices.bean.TransactionBean;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;
import hisaab.util.ServiceResponse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

@Path("/v1/staff/transaction")
public class StaffTransactionService {
	
	@POST
	@Path("/add")
	@Consumes("application/json")
	@Produces("application/json")
	public Response addTransaction(@HeaderParam("authToken") String authToken,
			@HeaderParam("authId") String authId,TransactionBean transBean){
		
		ObjectMapper mapper = new ObjectMapper();
		List<Transaction> rejected = new ArrayList<Transaction>();
		
		String req = "token : "+authToken+", transaction : ";
		try {
			req += mapper.writeValueAsString(transBean);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		Object result = null;
		StaffTransactionDoc transDoc = new StaffTransactionDoc();
		long epoch = System.currentTimeMillis();
		StaffUser staffUser = null;
		if(Constants.AUTH_USERID){
			staffUser = StaffUserDao.getStaffUserFromAuthToken1(authToken,authId);
		}
		else{
			staffUser = StaffUserDao.getStaffUserFromAuthToken(authToken);
		}
		if(staffUser.getsId()>0){
			logModel.setUser(staffUser.getStaffId()+"_"+staffUser.getStaffProfile().getUserName());
			List<String> frnds = new ArrayList<String>();
			/*FriendList frndList = FriendsDao.getAssociatedUserDoc(user);
			FriendContact frnd = null;
			for(FriendContact fn : frndList.getFriends()){
				if(fn.getFrndId().equals(""+uid))
					frnd = fn;
			}*/
				
				transDoc.setStaffId(staffUser.getStaffId());
				transDoc.setOwnerId(""+staffUser.getOwnerId());
				System.out.println(staffUser.getOwnerId());
				
				transDoc = StaffTransactionDao.getTransactionDoc(transDoc);
				transDoc.setTransactions(transBean.getTransactions());
				long lastCount = transDoc.getIdCount();
				List<String> blockedFriend = FriendsDao.getBlockedUserIdForSaff(staffUser.getOwnerId());
				Iterator<Transaction> itr = transBean.getTransactions().iterator();
				
				while(itr.hasNext()){
					Transaction trans = itr.next();
					String from = trans.getFrom();
					String to = trans.getTo();
				    if(blockedFriend.contains(from) || 
				    blockedFriend.contains(to)){
				    	rejected.add(trans);
				    	itr.remove();
				    }
				}
				if(transBean.getTransactions() != null && !transBean.getTransactions().isEmpty())
				{
					for(Transaction t : transBean.getTransactions()){
						t.setTransactionId(staffUser.getStaffId()+"_"+(++lastCount));
						t.setSrNo(lastCount);
	//					t.setTransactionDocId("");
						t.setCreatedTime(epoch);
						t.setUpdatedTime(epoch);
						t.setCreatedBy(""+staffUser.getOwnerId());
						t.setStaffUser(""+staffUser.getStaffId());
						t.setTransactionStatus(Constants.TRANS_ADDED_BY_STAFF);
						t.setSyncFlag(0);
					}
					transDoc.setIdCount(lastCount);
					StaffTransactionDao.addStaffTransactions(transDoc, staffUser);
					transBean.getTransactions().addAll(rejected);
					transBean.setMsg("transactions Added");
					transBean.setStatus(Constants.SUCCESS_RESPONSE);
					result = transBean;
				}else{
					transBean.getTransactions().addAll(rejected);
					transBean.setMsg("transaction against blocked user not allowed");
					transBean.setStatus(Constants.SUCCESS_RESPONSE);
					result = transBean;
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
		logModel.setRequestName("Add transaction by Staff");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

	
	
	@POST
	@Path("/approve")
	@Consumes("application/json")
	@Produces("application/json")
	public Response approveTransaction(@HeaderParam("authToken") String authToken, 
			@HeaderParam("authId") String authId, ApprovalBean rBean){
		
		ObjectMapper mapper = new ObjectMapper();
		
		String req = "token : "+authToken+", ApprovalBean : "+rBean;
		try {
			req += mapper.writeValueAsString(rBean);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		Object result = null;
		ApproveBean apvbean = new ApproveBean();
		List<String> failed = new ArrayList<String>();
		List<Transaction> transac = new ArrayList<Transaction>();
		long epoch = System.currentTimeMillis();
		UserMaster user = null;
		if(Constants.AUTH_USERID){
			user = UserDao.getUserFromAuthToken1(authToken,authId);
		}
		else{
			user = UserDao.getUserFromAuthToken(authToken);
		}
		if(user.getUserId()>0){
			logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
			boolean aFlag = false;
			boolean rFlag = false;
			
			if(!rBean.getApprovedTransIds().isEmpty()){
				aFlag = StaffTransactionDao.updateUserResponseForStaffTransaction(rBean.getApprovedTransIds(), user, Constants.ACTION_APPROVED, failed ,transac);
			}

			if(!rBean.getRejectedTransIds().isEmpty()){
				rFlag = aFlag = StaffTransactionDao.updateUserResponseForStaffTransaction(rBean.getRejectedTransIds(), user, Constants.ACTION_REJECTED, failed ,transac );
			}
						
			/*JSONObject js = new JSONObject();
			js.put("status", Constants.SUCCESS_RESPONSE);
			js.put("msg", "ok");
			js.put("failed", failed);*/
			apvbean.setMsg("success");
			apvbean.setStatus(200);
			apvbean.setFailed(failed);
			apvbean.setTransactions(transac);
			result =apvbean;
			
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
		logModel.setRequestName("Approve transaction By user");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

	
	
	@POST
	@Path("/update")
	@Consumes("application/json")
	@Produces("application/json")
	public Response updateMultipleTransactions(@HeaderParam("authToken") String authToken, 
			@HeaderParam("authId") String authId, TransactionBean transBean){
		ObjectMapper mapper = new ObjectMapper();
		
		String req = "token : "+authToken+", transaction :";
		try {
			req += mapper.writeValueAsString(transBean);
		} catch (Exception e) {

			e.printStackTrace();
		}
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		Object result = null;
		StaffUser user = null;
		if(Constants.AUTH_USERID){
			user = StaffUserDao.getStaffUserFromAuthToken1(authToken,authId);
		}
		else{
			user = StaffUserDao.getStaffUserFromAuthToken(authToken);
		}
		if(user.getsId()>0){
			 logModel.setUser(user.getStaffId()+"_"+user.getStaffProfile().getUserName());
			 StaffTransactionDao.update(transBean.getTransactions(),user);
			 transBean.setStatus(Constants.SUCCESS_RESPONSE);
			 transBean.setMsg("Success");
			 result=transBean;
		}
		else{
			result = ServiceResponse.getResponse(401, "Invalid Auth Token");
		}
		
		try {
			res = mapper.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logModel.setRequestData(req);
		logModel.setResponseData(res);
		logModel.setRequestName("Update Transaction By staff");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	

	@GET
	@Path("/delete/staff")
	@Consumes("application/json")
	@Produces("application/json")
	public Response deleteTransactionByStaff(@HeaderParam("authToken") String authToken, 
			@HeaderParam("authId") String authId,@HeaderParam("transactionId") String transactionId){
		
		ObjectMapper mapper = new ObjectMapper();
		
		String req = "token : "+authToken+", transactionId : "+transactionId;
		
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		Object result = null;
		StaffUser user = null;
		if(Constants.AUTH_USERID){
			user = StaffUserDao.getStaffUserFromAuthToken1(authToken,authId);
		}
		else{
			user = StaffUserDao.getStaffUserFromAuthToken(authToken);
		}
		if(user.getsId()>0){
			logModel.setUser(user.getStaffId()+"_"+user.getStaffProfile().getUserName());
			
				 if(StaffTransactionDao.deleteTransaction(transactionId, user.getStaffId(), ""+user.getOwnerId(), user.getStaffId(), true)){
					 result = ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE, "Transaction Deleted Successfully");
				 }
				 else
					 result = ServiceResponse.getResponse(403, "You Cannot delete this transaction.");
			
		}
		else{
			result = ServiceResponse.getResponse(401, "Invalid Auth Token");
		}
		
		try {
			res = mapper.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logModel.setRequestData(req);
		logModel.setResponseData(res);
		logModel.setRequestName("Delete Staff_Transaction by Staff ");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	
	@GET
	@Path("/delete/user/{staffId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response deleteTransactionByUser(@HeaderParam("authToken") String authToken, 
			@HeaderParam("authId") String authId, @HeaderParam("transactionId") String transactionId
			, @PathParam("staffId") String staffId){
		
		ObjectMapper mapper = new ObjectMapper();
		
		String req = "token : "+authToken+", transactionId : "+transactionId+", staffId : "+staffId;
		
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		Object result = null;
		UserMaster user = null;
		if(Constants.AUTH_USERID){
			user = UserDao.getUserFromAuthToken1(authToken,authId);
		}
		else{
			user = UserDao.getUserFromAuthToken(authToken);
		}
		if(user.getUserId()>0){
			 logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
			 
				 if(StaffTransactionDao.deleteTransaction(transactionId, staffId, ""+user.getUserId(), ""+user.getUserId(), false)){
					 result = ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE, "Transaction Deleted Successfully");
				 }
				 else
					 result = ServiceResponse.getResponse(Constants.FAILURE, "Transaction delete failed.");
	
		}
		else{
			result = ServiceResponse.getResponse(401, "Invalid Auth Token");
		}
		
		try {
			res = mapper.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logModel.setRequestData(req);
		logModel.setResponseData(res);
		logModel.setRequestName("Delete Staff_Transaction by User");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	
	@POST
	@Path("/get/byIds")
	@Consumes("application/json")
	@Produces("application/json")
	public Response getStaffTransactionsById(@HeaderParam("authToken") String authToken, 
			@HeaderParam("authId") String authId,ReadBean rBean){
		ObjectMapper mapper = new ObjectMapper();
		
		String req = "token : "+authToken+", transaction :";
		try {
			req += mapper.writeValueAsString(rBean);
		} catch (Exception e) {

			e.printStackTrace();
		}
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		Object result = null;
		StaffUser user = null;
		if(Constants.AUTH_USERID){
			user = StaffUserDao.getStaffUserFromAuthToken1(authToken,authId);
		}
		else{
			user = StaffUserDao.getStaffUserFromAuthToken(authToken);
		}
		if(user.getsId()>0){
			 logModel.setUser(user.getStaffId()+"_"+user.getStaffProfile().getUserName());
			 if(rBean.getTransIds() != null && !rBean.getTransIds().isEmpty()){
				 TransactionBean transBean = new TransactionBean();
				 
				 transBean.setTransactions(StaffTransactionDao.getStaffTransactionforStaffByIds(user,rBean.getTransIds()));
				 transBean.setStatus(Constants.SUCCESS_RESPONSE);
				 transBean.setMsg("Success");
				 result=transBean;
			 }
			 else{
				 result = ServiceResponse.getResponse(Constants.INVALID_PARAMS, "transIds list cannot be null or empty.");
			 }
		}
		else{
			result = ServiceResponse.getResponse(401, "Invalid Auth Token");
		}
		
		try {
			res = mapper.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logModel.setRequestData(req);
		logModel.setResponseData(res);
		logModel.setRequestName("getTransaction By Ids");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	

	
}
