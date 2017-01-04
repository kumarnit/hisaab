package hisaab.services.pull.webservice;

import java.io.IOException;

import hisaab.services.Logs.LogHelper;
import hisaab.services.Logs.LogModel;
import hisaab.services.pull.helper.PullHelper;
import hisaab.services.pull.modal.PullBean;
import hisaab.services.pull.modal.PullStaffBean;
import hisaab.services.pull.webservice.bean.PushTransactionData;
import hisaab.services.staff.dao.StaffUserDao;
import hisaab.services.staff.modal.StaffUser;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;
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
	public Response pullUserData(@HeaderParam("authToken") String authToken, @HeaderParam("pullTime") long pullTime) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", pullTime : "+pullTime;
		
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		Object result = null;
		long epoch = System.currentTimeMillis();
		UserMaster user = UserDao.getUserFromAuthToken(authToken);
		PullBean pullBean = new PullBean();
		if(user.getUserId()>0){
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
			res = mapper.writeValueAsString(result);
		}
		

		logModel.setRequestData(req);
		logModel.setResponseData(res);
		logModel.setRequestName("pull user data");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	@Path("/pull/staff")
	@GET
	@Produces("application/json")
	public Response pushTransactions(@HeaderParam("authToken") String authToken, @HeaderParam("pullTime") long pullTime) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", pullTime : "+pullTime;
		
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		Object result = null;
		long epoch = System.currentTimeMillis();
		StaffUser user = StaffUserDao.getStaffUserFromAuthToken(authToken);
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
						res = mapper.writeValueAsString(result);
		}
		
		logModel.setRequestData(req);
		logModel.setResponseData(res);
		logModel.setRequestName("pull staff data");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

}
