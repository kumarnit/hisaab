package hisaab.services.device.webservices;

import java.util.Arrays;

import hisaab.services.device.dao.DeviceInfoDao;
import hisaab.services.device.modal.DeviceInfoDoc;
import hisaab.services.device.webservices.bean.DeviceInfoBean;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;
import hisaab.util.ServiceResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("v1/deviceInfo")
public class DeviceInfoService {
	
	@POST
	@Path("/add")
	@Consumes("application/json")
	@Produces("application/json")
	public Response addDeviceInfo(@HeaderParam("authToken") String authToken,
			DeviceInfoBean deviceInfoBean){
		UserMaster user = UserDao.getUserFromAuthToken(authToken);
		Object result = null;
		if(user != null){
			
			DeviceInfoDoc deviceInfodoc = DeviceInfoDao.getDeviceInfoDoc(user);
			deviceInfodoc.setDevice(Arrays.asList(deviceInfoBean.getDeviceInfo()));
			if(!DeviceInfoDao.checkAndUpdateForDeviceId(deviceInfodoc))
			{
			    if(DeviceInfoDao.setDeviceInfo(deviceInfodoc))
			    {
			    	/*deviceInfoBean.setMsg("success");
			    	deviceInfoBean.setStatus(Constants.SUCCESS_RESPONSE);*/
			    	result = ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE, "success");
			    }else
			    	result = ServiceResponse.getResponse(507, "Server was unable to process the request");
			}else
				result = ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE, "success");
		}else
			result = ServiceResponse.getResponse(401, "Invalid Server Token");
		
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

}
