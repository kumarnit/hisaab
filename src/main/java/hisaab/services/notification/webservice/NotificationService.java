package hisaab.services.notification.webservice;

import java.util.ArrayList;
import java.util.List;

import hisaab.services.notification.NotificationHelper;
import hisaab.services.notification.ServerMigrateNotification;
import hisaab.services.notification.webservice.bean.SystemUpdateBean;
import hisaab.services.user.dao.RequestDao;
import hisaab.util.Constants;
import hisaab.util.ServiceResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.multipart.FormDataParam;

@Path("v1/notify")
public class NotificationService {

	
	
	@POST
	@Path("/update")
	
	@Produces("application/json")
	public Response updateSystemNotification(@FormParam("min") String min,
			@FormParam("max") String max,
			@QueryParam("fields[]") final List<String> list,
			@FormParam("stop") String stop){
		Object result = null;
//		try{
			System.out.println("==  "+min);
			System.out.println("==  "+max);
			System.out.println("==  "+stop);
			System.out.println("00"+list.toString());
			for(String str : list ){
				System.out.println("inner : "+str);
			}
			
			SystemUpdateBean sub = new SystemUpdateBean();
			sub.getUpdate().setMaxAvialableVersion(max);
			sub.getUpdate().setMinStableAppVersion(min);
			sub.getUpdate().setStopSupportForVersion(stop);
			List<String> features = new ArrayList<String>();
			
			sub.getUpdate().setNewFeatures(features);
			sub.getUpdate().setNotificationType(Constants.NOTIFICATION_SYSTEM_NOTIFICATION);
			NotificationHelper.buildAndSendUpdateSystemNotification(sub.getUpdate(),"New Update Availble");
//			String newToken = RequestDao.addNewUserRequest();
//			result =  "{ " + "\"status\" : 200, " +  "\"serverToken\" : \""+newToken+ "\", \"msg\": \"New token\"}";
			sub.setMsg("success");
			sub.setStatus(200);
			result = sub;
//		}catch(Exception e){
//			System.out.println("Exception in Update System Notification Service : \n"+e.getMessage());
//			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
//		}
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	@POST
	@Path("/migrateserver")
	@Produces("application/json")
	@Consumes("application/json")
	public Response servermigrateSystemNotification(ServerMigrateNotification sermigrate ){
		Object result = null;
		try{
			sermigrate.setNotificationType(Constants.NOTIFICATION_SYSTEM_NOTIFICATION);
			NotificationHelper.buildAndSendServerMigrateSystemNotification(sermigrate,"Server Migrate");
//			String newToken = RequestDao.addNewUserRequest();
//			result =  "{ " + "\"status\" : 200, " +  "\"serverToken\" : \""+newToken+ "\", \"msg\": \"New token\"}";
		}catch(Exception e){
			System.out.println("Exception in Update System Notification Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
}
