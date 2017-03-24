package hisaab.services.user.images.webservices;

import hisaab.services.Logs.LogHelper;
import hisaab.services.Logs.LogModel;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.images.dao.UserImageDao;
import hisaab.services.user.images.modal.UserImage;
import hisaab.services.user.images.webservices.bean.UserImageBean;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;
import hisaab.util.ExcecutorHelper;
import hisaab.util.ExecutionTimeLog;
import hisaab.util.ImageHelper;
import hisaab.util.ServiceResponse;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("v1/service/user/image")
public class UserImageServices {

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@Context ServletContext servletContext,
			@HeaderParam("authToken") String authToken,
			@HeaderParam("authId") String authId) {
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		UserMaster user = null;
		String req = "token : "+authToken+", authId :"+authId;
		
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		if(Constants.AUTH_USERID){
			user = UserDao.getUserFromAuthToken1(authToken,authId);
		}
		else{
			user = UserDao.getUserFromAuthToken(authToken);
		}
		Object result = null;
		if (user.getUserId() > 0) {
			String uploadedFileLocation = "";
			if (Constants.DEV_MODE)
				uploadedFileLocation = servletContext.getRealPath("/");
			else {
//				System.out.println(System.getenv("OPENSHIFT_DATA_DIR"));
				uploadedFileLocation = Constants.USER_IMAGES_FOLDER;
			}
			System.out.println("context path :" + uploadedFileLocation);
			String imgName = ImageHelper
					.getNewImgName(fileDetail.getFileName());
			if (ImageHelper.UploadProfilePicProcessing(uploadedInputStream,
					uploadedFileLocation, imgName)) {
				UserImage userImg = new UserImage();
				userImg.setImageKey(imgName);
				userImg.setUserId(user.getUserId());
				UserImageDao.addNewImage(userImg, user);
				if (userImg.getImageId() > 0) {
					/**
					 * update new Profile imageKey with imageName
					 **/
					user.getUserProfile().setImageKey(imgName);
					UserDao.updateProfileImageKey(user.getUserProfile());
					
					UserImageBean userImgBean = new UserImageBean();
					userImgBean.setUserImage(userImg);
					userImgBean.setStatus(200);
					result = userImgBean;
				} else {
					String msg = "Unable to add record in Db";
					ServiceResponse serv = ServiceResponse.getResponse(
							Constants.DB_FAILURE, msg);
					result = serv;
				}
			} else {
				String msg = "Unable to Upload image";
				ServiceResponse serv = ServiceResponse.getResponse(
						Constants.FAILURE, msg);
				result = serv;
			}
		} else {
			String msg = "Invalid Login";
			ServiceResponse serv = ServiceResponse.getResponse(
					Constants.AUTH_FAILURE, msg);
			result = serv;
		}
		try{
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName("update push");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
		}catch(Exception e){
			System.out.println("Unable to add log records for : update push Service \n"+e.getMessage());
		}
		timer.stop();
		timer.setMethodName("upload_image");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(200).entity(result).build();
	}

	
	@GET
	@Path("/{objectKey}")
	public Response download(@HeaderParam("authToken") String authToken,
			@HeaderParam("authId") String authId,
			@PathParam("objectKey") String objectKey,
			@Context ServletContext servletContext) {
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		String directory = "";
		Object result = null;
		String contentType = "application/json";
		byte[] imageByteArray = null;

		String contxPath = "";
		try {
			if(Constants.DEV_MODE){
				contxPath = servletContext.getRealPath("/");
			}
			else{
				contxPath = Constants.USER_IMAGES_FOLDER;
			}
			contentType = "image/png";
			imageByteArray = ImageHelper.getImage(contxPath + Constants.USER_IMAGES_SMALL +"/"+ objectKey);
			result = imageByteArray;
		} catch (Exception e) {
			contentType = "application/json";
			result = ServiceResponse.getResponse(Constants.FAILURE,
					"Something went wrong");
		}
		timer.stop();
		timer.setMethodName("download_image");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(200).type(contentType).entity(result).build();
	}

	
	@GET
	@Path("/test/{objectKey}")
	public Response downloadTest(@HeaderParam("authToken") String authToken,
			@PathParam("objectKey") String objectKey,
			@Context ServletContext servletContext) {
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		String directory = "";
		Object result = null;
		String contentType = "application/json";
		byte[] imageByteArray = null;

		String contxPath = "";
		try {
			if(Constants.DEV_MODE){
				contxPath = servletContext.getRealPath("/");
			}
			else{
				contxPath = "/home/nitish/hisaab";
			}
			contentType = "image/png";
			imageByteArray = ImageHelper.getImage(contxPath + Constants.USER_IMAGES_SMALL +"/"+ objectKey);
			result = imageByteArray;
		} catch (Exception e) {
			contentType = "application/json";
			result = ServiceResponse.getResponse(Constants.FAILURE,
					"Something went wrong");
		}
		timer.stop();
		timer.setMethodName("download_image_test");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(200).type(contentType).entity(result).build();
	}

}
