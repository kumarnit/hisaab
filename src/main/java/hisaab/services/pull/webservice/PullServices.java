package hisaab.services.pull.webservice;

import java.io.IOException;

import hisaab.services.Logs.LogHelper;
import hisaab.services.Logs.LogModel;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.pull.helper.PullDocDao;
import hisaab.services.pull.helper.PullHelper;
import hisaab.services.pull.modal.PullBean;
import hisaab.services.pull.modal.PullDoc;
import hisaab.services.pull.modal.PullStaffBean;
import hisaab.services.pull.modal.ReadPullBean;
import hisaab.services.pull.webservice.bean.PushTransactionData;
import hisaab.services.staff.dao.StaffUserDao;
import hisaab.services.staff.modal.StaffUser;
import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.transaction.staff_transaction.dao.DeletedStaffTransactionDao;
import hisaab.services.user.dao.PrivateUserDao;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;
import hisaab.util.ExcecutorHelper;
import hisaab.util.ExecutionTimeLog;
import hisaab.util.ServiceResponse;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@Path("v1/sync")
public class PullServices {

	
	@Path("/pull")
	@GET
	@Produces("application/json")
	public Response pullUserData(@HeaderParam("authToken") String authToken,
			@HeaderParam("authId") String authId, @HeaderParam("pullTime") long pullTime){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", pullTime : "+pullTime+", authId :"+authId;
		
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		Object result = null;
		UserMaster user = null;
 		long epoch = System.currentTimeMillis();

		long test1,test2;
		test1 = System.currentTimeMillis();
		System.out.println("##starttime : "+test1);
		if(Constants.AUTH_USERID){
			user = UserDao.getUserFromAuthToken1(authToken,authId);
		}
		else{
			user = UserDao.getUserFromAuthToken(authToken);
		}
		test2 = System.currentTimeMillis();
		System.out.println("**EndTime : "+test2);
		System.out.println("Difference :: "+(test2-test1));
		PullBean pullBean = new PullBean();
		if(user.getUserId()>0){
			UserDao.updateLastSyncTime(user.getUserId(), System.currentTimeMillis());
				logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
				pullBean = PullHelper.getUserData(pullTime, user);
				pullBean.setPullTime(epoch);
				pullBean.setStatus(Constants.SUCCESS_RESPONSE);
				pullBean.setMsg("Success");
				result = pullBean;
				
				if(pullBean.getUserIds() != null && !pullBean.getUserIds().isEmpty())
					res = "userId : "+pullBean.getUserIds();
				if(pullBean.getFriendList() != null && !pullBean.getFriendList().isEmpty())
				        res += ", No Of FriendList : "+pullBean.getFriendList().size();
					}else{
			result = ServiceResponse.getResponse(Constants.AUTH_FAILURE, "Invalid token");
			try {
				res = mapper.writeValueAsString(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try{
		logModel.setRequestData(req);
		logModel.setResponseData(res);
		logModel.setRequestName("pull user data");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		}catch(Exception e){
			System.out.println(e);
		}
		timer.stop();
		timer.setMethodName("pull_user_data");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	@Path("/pull/staff")
	@GET
	@Produces("application/json")
	public Response pushTransactions(@HeaderParam("authToken") String authToken, 
			@HeaderParam("pullTime") long pullTime, @HeaderParam("authId") String authId) {
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", pullTime : "+pullTime+", authId :"+authId;
		
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		Object result = null;
		long epoch = System.currentTimeMillis();
		StaffUser user = null;
		if(Constants.AUTH_USERID){
			user = StaffUserDao.getStaffUserFromAuthToken1(authToken, authId);
		}else{
			user = StaffUserDao.getStaffUserFromAuthToken(authToken);
		}
		
		
		PullStaffBean pullBean = new PullStaffBean();
		if(user.getsId()>0){
				logModel.setUser(user.getStaffId()+"_"+user.getStaffProfile().getUserName());
				pullBean = PullHelper.getUserDataForStaffUser(pullTime, user);
				pullBean.setPullTime(epoch);
				pullBean.setMyStaffProfile(user.getStaffProfile());
				pullBean.setStatus(Constants.SUCCESS_RESPONSE);
				pullBean.setMsg("Success");
				result = pullBean;
				if(pullBean.getUserIds() != null && !pullBean.getUserIds().isEmpty())
					res = "userId : "+pullBean.getUserIds();
				if(pullBean.getFriendList() != null && !pullBean.getFriendList().isEmpty())
				        res += ", No Of FriendList : "+pullBean.getFriendList().size();
					}else{
						result = ServiceResponse.getResponse(Constants.AUTH_FAILURE, "Invalid token");
						try {
							res = mapper.writeValueAsString(result);
						} catch (Exception e) {
							e.printStackTrace();
						}
		}
		try{
		logModel.setRequestData(req);
		logModel.setResponseData(res);
		logModel.setRequestName("pull staff data");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		}catch(Exception e){
			System.out.println(e);
		}
		timer.stop();
		timer.setMethodName("pull_staff_data");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	
	@Path("/read/pull")
	@GET
	@Produces("application/json")
	public Response pullReadTransactions(@HeaderParam("authToken") String authToken,
			@HeaderParam("authId") String authId, @HeaderParam("readPullTime") long readPullTime){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", readPullTime : "+readPullTime+", authId :"+authId;
		
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		Object result = null;
		UserMaster user = null;
 		long epoch = System.currentTimeMillis();

		long test1,test2;
		test1 = System.currentTimeMillis();
		System.out.println("##starttime : "+test1);
		if(Constants.AUTH_USERID){
			user = UserDao.getUserFromAuthToken1(authToken,authId);
		}
		else{
			user = UserDao.getUserFromAuthToken(authToken);
		}
		test2 = System.currentTimeMillis();
		System.out.println("**EndTime : "+test2);
		System.out.println("Difference :: "+(test2-test1));
		ReadPullBean pullBean = new ReadPullBean();
		if(user.getUserId()>0){
			UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
				logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
				pullBean.setReadTransactionList(TransactionDao.pullReadTransactions(user, readPullTime)); 
				pullBean.setReadPullTime(epoch);
				pullBean.setStatus(Constants.SUCCESS_RESPONSE);
				pullBean.setMsg("Success");
				result = pullBean;
		}else{
			result = ServiceResponse.getResponse(Constants.AUTH_FAILURE, "Invalid token");
			try {
				res = mapper.writeValueAsString(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try{
		logModel.setRequestData(req);
		logModel.setResponseData(res);
		logModel.setRequestName("pull read transaction");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		}catch(Exception e){
			System.out.println(e);
		}
		timer.stop();
		timer.setMethodName("pull_read_trans");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	@Path("/pullDoc")
	@GET
	@Produces("application/json")
	public Response syncPullDoc(@HeaderParam("authToken") String authToken,
			@HeaderParam("authId") String authId, @HeaderParam("PullTime") long pullTime){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", readPullTime : "+pullTime+", authId :"+authId;
		
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		Object result = null;
		UserMaster user = null;
 		long epoch = System.currentTimeMillis();

		long test1,test2;
		test1 = System.currentTimeMillis();
		System.out.println("##starttime : "+test1);
		if(Constants.AUTH_USERID){
			user = UserDao.getUserFromAuthToken1(authToken,authId);
		}
		else{
			user = UserDao.getUserFromAuthToken(authToken);
		}
		test2 = System.currentTimeMillis();
		System.out.println("**EndTime : "+test2);
		System.out.println("Difference :: "+(test2-test1));
		PullDoc pullDoc = new PullDoc();
		if(user.getUserId()>0){
				logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
				pullDoc = PullDocDao.getPullDocForSync(user.getUserId());
				if(pullDoc != null)
					{
					pullDoc.setFriendList(FriendsDao.pullAssociatedUserDocUpdated(user,pullTime));
					pullDoc.setStaffProfiles(StaffUserDao.getStaffUsers(user, pullTime));
					pullDoc.setListOfDeletedStaffTransaction(DeletedStaffTransactionDao.pullDeletedTransactionId
							(""+user.getUserId(),pullTime,true));
					
					pullDoc.setPrivateuser(PrivateUserDao.getPrivateUser(user, pullTime));
					FriendList frndlist = FriendsDao.getAssociatedUserDocForPull(user);
					if(frndlist != null){
						for(FriendContact frnd : frndlist.friends){
							if(frnd.getFrndStatus() == 0 || frnd.getFrndStatus() == Constants.NOT_REGISTERED_USER)
								{
								pullDoc.getUserIds().add(frnd.getFrndId());
		//							usr.put(frnd.getFrndId(), frnd);
								}
						}
					}
					pullDoc.setUserProfileList(UserDao.pullUsersByUserIds(user,pullDoc.getUserIds(),pullTime));
					pullDoc.setPullTime(System.currentTimeMillis());
					pullDoc.setStatus(Constants.SUCCESS_RESPONSE);
					pullDoc.setMsg("success");
					result = pullDoc;
					PullDocDao.clearPullDoc(user);
					}else 
						result = ServiceResponse.getResponse(101, "No Recent Data Available");
		}else{
			result = ServiceResponse.getResponse(Constants.AUTH_FAILURE, "Invalid token");
			try {
				res = mapper.writeValueAsString(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try{
		logModel.setRequestData(req);
		logModel.setResponseData(res);
		logModel.setRequestName("pull user data");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		}catch(Exception e){
			System.out.println(e);
		}
		timer.stop();
		timer.setMethodName("sync_pull_doc");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

}
