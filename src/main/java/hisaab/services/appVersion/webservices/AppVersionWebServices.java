package hisaab.services.appVersion.webservices;

import java.util.List;

import hisaab.services.appVersion.dao.AppVersionDao;
import hisaab.services.appVersion.modal.AppVersion;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;
import hisaab.util.ExcecutorHelper;
import hisaab.util.ExecutionTimeLog;
import hisaab.util.ServiceResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


@Path("v1/appVersion")
public class AppVersionWebServices {
	@GET
	@Path("/get/latestUpdate")
	@Consumes("application/json")
	@Produces("application/json")
	public static Response getAppVersionDetail(@HeaderParam("authToken") String authToken, @HeaderParam("appVersion")
	 int appVersion,@HeaderParam("authId") long authId){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		UserMaster user = UserDao.getUserFromAuthToken(authToken);
		Object result = null;
		if(user != null && user.getUserId()>0){
			if(UserDao.setAppVersion(user,appVersion))
			{
				List<AppVersion> appVersionList = AppVersionDao.getAppVersionUpdate(appVersion);
				if(appVersionList != null && !appVersionList.isEmpty()){
					AppVersionBean appVersionBean = new AppVersionBean();
					appVersionBean.setAppVersionList(appVersionList);
					appVersionBean.setMsg("sucess");
					appVersionBean.setStatus(Constants.SUCCESS_RESPONSE);
					result = appVersionBean;
				}else{
					result = ServiceResponse.getResponse(406, "No latest Upadte");
				}
			}else{
				result = ServiceResponse.getResponse(Constants.DB_FAILURE, "Database failure");
			}
		}else{
			result = ServiceResponse.getResponse(Constants.AUTH_FAILURE, "Invalid Auth Token");
		}
		timer.stop();
		timer.setMethodName("get_app_version_detail");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return  Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

	
		@POST
		@Path("/set/appVersionDetail")
		@Consumes("application/json")
		@Produces("application/json")
		public static Response setAppVersionDetail(AppVersion appVersion){
			ExecutionTimeLog timer = new ExecutionTimeLog();
			timer.start();
				
			AppVersionDao.setAppVersionDetail(appVersion);
			timer.stop();
			timer.setMethodName("set_app_version_detail");
			ExcecutorHelper.addExecutionLog(timer.toString());
			return  Response.status(Constants.SUCCESS_RESPONSE).entity("success").build();
		}
}
