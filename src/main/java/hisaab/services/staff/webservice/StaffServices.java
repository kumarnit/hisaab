package hisaab.services.staff.webservice;

import hisaab.services.Logs.LogHelper;
import hisaab.services.Logs.LogModel;
import hisaab.services.contacts.ContactHelper;
import hisaab.services.contacts.dao.ContactsDao;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.Contact;
import hisaab.services.contacts.modal.ContactList;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.contacts.services.bean.ContactBean;
import hisaab.services.pull.helper.PullDocDao;
import hisaab.services.pull.modal.PullDoc;
import hisaab.services.staff.dao.StaffUserDao;
import hisaab.services.staff.modal.StaffProfile;
import hisaab.services.staff.dao.UserStaffMappingDao;
import hisaab.services.staff.modal.StaffUser;
import hisaab.services.staff.modal.StaffUserRequest;
import hisaab.services.staff.modal.UserStaffMapping;
import hisaab.services.staff.webservice.bean.StaffUserBean;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.services.user.modal.UserProfile;
import hisaab.services.user.token.TokenModal;
import hisaab.services.user.webservices.bean.UserBean;
import hisaab.util.Constants;
import hisaab.util.Helper;
import hisaab.util.ServiceResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.mongodb.morphia.mapping.Mapper;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

@Path("v1/staff")
public class StaffServices {

	
	
	
	@GET
	@Path("/verify/{requestId}/{response}")
	@Produces("application/json")
	public Response staffLogin(@HeaderParam("contactNo") String contactNo,
			@PathParam("requestId") long reqId, @PathParam("response") int status) 
			          {
						
		Object result = null;
		try{
		
		PhoneNumber phnNum = ContactHelper.validatePhoneNumber(contactNo);
		if(phnNum != null){
			contactNo = phnNum.getNationalNumber()+"";
			Contact contact = new Contact();
			contact.setContactNo(contactNo);
			contact.setCountryCode(phnNum.getCountryCode()+"");
			contact.setName("");
			StaffUserRequest st = StaffUserDao.verifyStaffUserCode( contactNo,reqId, status);
			if(st != null){
				/**
				 * Adding to pull Doc
				 * */
				UserMaster userStaff = UserDao.getUserByContactNo(contact.getContactNo());
				PullDoc pullDoc = new PullDoc();
				pullDoc.setUserId(""+st.getOwnerId());
//				pullDoc = PullDocDao.getPullDoc(pullDoc);
//				PullDoc pullDoc1 = new PullDoc();
//				if(userStaff != null){
//					
//					pullDoc1.setUserId(""+userStaff.getUserId());
//					pullDoc1 = PullDocDao.getPullDoc(pullDoc1);
//				}
				
				if(status == Constants.STAFFUSER_REQ_ACCEPTED){
					StaffUser user = StaffUserDao.staffUserLogin(st,contact, TokenModal.generateToken(), Constants.STAFF_USER, st.getOwnerId(),status);
					if(user != null)
						Constants.staffUser.put(user.getStaffId(), user);
					
					/**
					 * ADding staff to contact and friendcontacts
					 * */
					ContactHelper.addStaffContact(contact,reqId,user);
					/**
					 * ending
					 * */
					UserStaffMapping usm = new UserStaffMapping();
					usm.setContactNo(user.getContactNo());
					usm.setStaffId(user.getStaffId());
					usm.setUserId(user.getOwnerId());
					usm.setStatus(1);
					UserStaffMappingDao.addUserStaffMapping(usm);
					StaffUserBean ubean = new StaffUserBean();
					ubean.setStaffUser(user);
					ubean.setStatus(Constants.SUCCESS_RESPONSE);
					ubean.setMsg("success");
					result = ubean;
//					PullDocDao.setStatusToStaffUserRequest(st,pullDoc,status);
//					if(pullDoc1 != null)
//					PullDocDao.addAndUpdateStaffRequestForYou(pullDoc1, st, status);
				}else if(status == 2){
					UserMaster user = new UserMaster();       
					user = UserDao.getUserForWeb(st.getOwnerId());
					if(StaffUserDao.cancelStaffUserRequest(user,reqId,3,st )){
						
						result = ServiceResponse.getResponse(Constants.STAFF_REQUEST_REJECT_RESPONSE, "Request rejected");
					}else{
						result = ServiceResponse.getResponse(Constants.FAILURE, "Request rejection Failed");
					}
//					PullDocDao.setStatusToStaffUserRequest(st,pullDoc,status);
//					if(pullDoc1 != null)
//						PullDocDao.addAndUpdateStaffRequestForYou(pullDoc1, st, status);
				}	
				else{
					result = ServiceResponse.getResponse(401, "Invalid Response Type");
				}
			}else{
				result = ServiceResponse.getResponse(401, "Request Expired");
			}
		}else{
			result = ServiceResponse.getResponse(402, "please enter a valid phone number");
		}
		}catch(Exception e){
			System.out.println("Exception in Verify staff User Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	

	/*@GET
	@Path("/verify")

	@Produces("application/json")
	public Response terminateStaff( 
			          @HeaderParam("contactNo") String contactNo, 
			          @HeaderParam("securityCode") String securityCode){
						
		Object result = null;
		Contact contact = new Contact();
		contact.setContactNo(contactNo);
		contact.setName("");
		StaffUserRequest st = StaffUserDao.verifyStaffUserCode( contactNo, securityCode);
		if(st != null){
			StaffUser user = StaffUserDao.staffUserLogin(contact, TokenModal.generateToken(), Constants.STAFF_USER, st.getOwnerId());
			StaffUserBean ubean = new StaffUserBean();
			ubean.setStaffUser(user);
			ubean.setStatus(Constants.SUCCESS_RESPONSE);
			ubean.setMsg("success");
			result = ubean;
		}else{
			result = ServiceResponse.getResponse(401, "Invalid securityCode");
		}
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
*/
	
	@Path("/update/pushToken")
	@PUT
	@Consumes("application/json")
	@Produces("application/json")
	public Response updatePush(@HeaderParam("authToken") String authToken,
			@HeaderParam("authId") String authId, StaffUserBean userBean){
		
		Object result = null;
		
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", authId : "+authId;	
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authId);
		
		StaffUser user = null;
		long epoch = System.currentTimeMillis();
		try {
				if(Constants.AUTH_USERID){
					user = StaffUserDao.getStaffUserFromAuthToken1(authToken,authId);
				}
				else{
					user = StaffUserDao.getStaffUserFromAuthToken(authToken);
				}
				
				long userId = user.getsId();
				
				user = userBean.getStaffUser();
				user.setsId(userId);
				
				
				if(user.getsId() > 0){
					
					user = userBean.getStaffUser();
					user.setsId(userId);
//					user.setOnboarding(Constants.ONBOARDING_COMPLETED);
					if(StaffUserDao.updatePushToken(user)){
						
						result = ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE, "PushToken updated successfully.");
						
						try {
							res = mapper.writeValueAsString(result);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}else{
							
						result = ServiceResponse.getResponse(Constants.DB_FAILURE, "Unable to store into the database");
					}
				}else{
					
					result = ServiceResponse.getResponse(Constants.AUTH_FAILURE, "Invalid Login");
				}
		
		
		try{
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName(" staff update push");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
		}catch(Exception e){
			System.out.println("Unable to add log records for : Staff update push Service \n"+e.getMessage());
		}
		
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in Update Staff push token Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();		
	}


	@POST
	@Consumes("application/json")
	@Produces("application/json")
	@Path("/update/profile")
	public Response updateStaffProfile(@HeaderParam("authToken") String authToken,
			@HeaderParam("authId") String authId,StaffUserBean userBean){
		
		Object result = null;
		
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", authId : "+authId;	
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authId);
		
		StaffUser requestingUser =null;
		try{
			if(Constants.AUTH_USERID){
				requestingUser = StaffUserDao.getStaffUserFromAuthToken1(authToken,authId);
			}
			else{
				requestingUser = StaffUserDao.getStaffUserFromAuthToken(authToken);
			}
			if(requestingUser.getsId()>0){
				userBean.getStaffUser().getStaffProfile().setsId(requestingUser.getsId());
				if(StaffProfile.validateProfileUpdate(userBean.getStaffUser().getStaffProfile())){
					if(StaffUserDao.updateStaffProfile(userBean.getStaffUser().getStaffProfile())){
						userBean.setStatus(Constants.SUCCESS_RESPONSE);
							userBean.setMsg("Successfuly Updated.");
						result = userBean;
						
						try {
							res = mapper.writeValueAsString(result);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						String msg = "Something went wrong.";
						result = ServiceResponse.getResponse(Constants.DB_FAILURE, msg);
					}
				}else{
					String msg = "Invalid parameters";
				
					result = ServiceResponse.getResponse(Constants.INVALID_PARAMS, msg);
				}
			}else{
				String msg = "Invalid Login";
				result = ServiceResponse.getResponse(Constants.AUTH_FAILURE, "Invalid Login");
			}
		
		try{
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName("update staff profile");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
		}catch(Exception e){
			System.out.println("Unable to add log records for : update staff profile services \n"+e.getMessage());
		}
		
		}catch(Exception e){
			System.out.println("Exception in Update Staff Profile : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}

		
			return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

	
	
	@GET
	@Path("/leave/owner/{ownerId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response removeStaff(@HeaderParam("authToken") String authToken,
			@HeaderParam("authId") String authId,@PathParam("ownerId") long ownerId ){
		StaffUser requestingUser = null;
		
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", authId : "+authId;	
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authId);
		
		if(Constants.AUTH_USERID){
			requestingUser = StaffUserDao.getStaffUserFromAuthToken1(authToken,authId);
		}
		else{
			requestingUser = StaffUserDao.getStaffUserFromAuthToken(authToken);
		}
		Object result = null;
		
		try{
			if(requestingUser.getsId()>0){
				if(StaffUserDao.removeStaffUser(ownerId,requestingUser.getContactNo(),requestingUser.getStaffId() , 3)>0){
					result =ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE, "User Removed Successfully");
				}
				else{
					result =ServiceResponse.getResponse(402, "unable to remove staff");
				}
			
			}else
				result =ServiceResponse.getResponse(401, "Invalid AuthToken");
				
			
		try{
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName("remove staff services");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
		}catch(Exception e){
			System.out.println("Unable to add log records for : remove staff Service \n"+e.getMessage());
		}
		
		}catch(Exception e){
			System.out.println("Exception in Leave owner : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
}
