package hisaab.services.user.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import hisaab.services.Logs.LogHelper;
import hisaab.services.Logs.LogModel;
import hisaab.services.contacts.ContactHelper;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.Contact;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.contacts.services.bean.ContactBean;
import hisaab.services.contacts.services.bean.PrivateUserBean;
import hisaab.services.staff.dao.StaffUserDao;
import hisaab.services.staff.dao.UserStaffMappingDao;
import hisaab.services.staff.modal.StaffUser;
import hisaab.services.staff.modal.StaffUserRequest;
import hisaab.services.staff.modal.UserStaffMapping;
import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.transaction.openingbalance.dao.OpeningBalDao;
import hisaab.services.transaction.request.dao.ModificationRequestDao;
import hisaab.services.transaction.webservices.bean.TransactionDocFriendBean;
import hisaab.services.user.UserHelper;
import hisaab.services.user.dao.PrivateUserDao;
import hisaab.services.user.dao.RequestDao;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.PrivateUser;
import hisaab.services.user.modal.UserMaster;
import hisaab.services.user.modal.UserProfile;
import hisaab.services.user.modal.UserRequest;
import hisaab.services.user.token.TokenModal;
import hisaab.services.user.webservices.bean.ContactUserprofileBean;
import hisaab.services.user.webservices.bean.RequestBean;
import hisaab.services.user.webservices.bean.UserBean;
import hisaab.services.user.webservices.bean.UserProfileFriendBean;
import hisaab.services.user.webservices.bean.UserprofileBean;
import hisaab.services.user.webservices.bean.UserprofileMainBean;
import hisaab.util.Constants;
import hisaab.util.Helper;
import hisaab.util.ServiceResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.bson.BasicBSONObject;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.mongodb.BasicDBObject;

@Path("v1/user")
public class UserServices {

	@GET
	@Path("/request")
	@Produces("application/json")
	public Response getAuthToken(@HeaderParam("appVersion") String appVersion){
		Object result = null;
		try{
			String newToken = RequestDao.addNewUserRequest(appVersion);
			result =  "{ " + "\"status\" : 200, " +  "\"serverToken\" : \""+newToken+ "\", \"msg\": \"New token\"}";
		}catch(Exception e){
			System.out.println("Exception in Request Server Token Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

	
	@GET
	@Path("/request/contact")
	@Produces("application/json")
	public Response requestContact(@HeaderParam("serverToken") String serverToken, 
			          @HeaderParam("contactNo") String contactNo,@HeaderParam("appVersion") String appVersion ){
		Object result = null;	
		String req = "token : "+serverToken+", contactNo : "+contactNo;
		String res = "";
		ObjectMapper mapper = new ObjectMapper();
		LogModel logModel = new LogModel();
		logModel.setUserToken(serverToken);
	
		try{
			PhoneNumber phnNum = ContactHelper.validatePhoneNumber(contactNo);
			if(phnNum != null){
				UserRequest userRequest = RequestDao.addContactToUserRequest(serverToken, phnNum.getNationalNumber()+"", phnNum.getCountryCode()+"",appVersion);
				if(userRequest.getId()>0){
					
					RequestBean rbean = new RequestBean();
					rbean.setRequest(userRequest);
					rbean.setStatus(Constants.SUCCESS_RESPONSE);
					rbean.setMsg("success");
					result = rbean;
				}else{
					result = ServiceResponse.getResponse(401, "Invalid Server Token");
				}
			}else{
				result = ServiceResponse.getResponse(402, "please enter valid mobile number.");
			}
			try {
				res = mapper.writeValueAsString(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}catch(Exception e){
			System.out.println("Exception in Request Contact Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		
		try{
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName("request Security Code");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
		}catch(Exception e){
			System.out.println("Unable to add log records for : Request Contact Service \n"+e.getMessage());
		}		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	
	@GET
	@Path("/request/verify")
	@Produces("application/json")
	public Response requestVerification(@HeaderParam("serverToken") String serverToken, 
			          @HeaderParam("contactNo") String contactNo, 
			          @HeaderParam("securityCode") String securityCode,
			          @HeaderParam("appVersion") String appVersion){
		String req = "token : "+serverToken+", contactNo : "+contactNo+", securityCode : "+securityCode;
		String res = "";
		ObjectMapper mapper = new ObjectMapper();
		LogModel logModel = new LogModel();
		logModel.setUserToken(serverToken);
		Object result = null;
		try{
			PhoneNumber num = ContactHelper.validatePhoneNumber(contactNo);
			if(num != null){
				contactNo = num.getNationalNumber()+"";
				UserRequest userReq = RequestDao.verifyUserCode(serverToken,contactNo, securityCode);
				if(userReq != null){
					UserMaster user = UserDao.userLogin(contactNo, serverToken, userReq.getCountryCode(),appVersion);
					UserBean ubean = new UserBean();
					ubean.setUser(user);
					
					StaffUser staff = StaffUserDao.getStaffUserByContactNo(contactNo);
								if(staff != null){
									if(staff.getOwnerId() != 0){
										UserStaffMapping usm = UserStaffMappingDao.getActiveUserMapping(staff.getStaffId());
										if(usm != null && usm.getUserId() == staff.getOwnerId()){
											ubean.setStaffUser(staff);
										}
									}
								}
					ubean.setStaffRequestsForYou(StaffUserDao.getStaffRequestsForUser(contactNo, 0));
					ubean.setStatus(Constants.SUCCESS_RESPONSE);
					ubean.setMsg("success");
					result = ubean;
				}else{
					result = ServiceResponse.getResponse(401, "Invalid securityCode");
				}
			}else{
				result = ServiceResponse.getResponse(402, "please enter valid phone number");
			}
			try {
				res = mapper.writeValueAsString(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}catch(Exception e){
			System.out.println("Exception in Request Verify Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		
		try{
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName("verifying securityCode");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
		}catch(Exception e){
			System.out.println("Unable to add logs for Request Verify service.\n"+e.getMessage());
			e.printStackTrace();
		}		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	@Path("/update/pushToken")
	@PUT
	@Consumes("application/json")
	@Produces("application/json")
	public Response updatePush(@HeaderParam("authToken") String authToken, UserBean userBean){
		
		Object result = null;
		long epoch = System.currentTimeMillis();
		try {
				UserMaster user = UserDao.getUserFromAuthToken(authToken);
				
				long userId = user.getUserId();
				String email = user.getEmail();
				user = userBean.getUser();
				user.setUserId(userId);
				user.setEmail(email);
				
				if(user.getUserId() > 0){
					
					user = userBean.getUser();
					user.setUserId(userId);
//					user.setOnboarding(Constants.ONBOARDING_COMPLETED);
					if(UserDao.updatePushToken(user)){
						
						result = ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE, "PushToken updated successfully.");
						
					}else{
							
						result = ServiceResponse.getResponse(Constants.DB_FAILURE, "Unable to store into the database");
					}
				}else{
					
					result = ServiceResponse.getResponse(Constants.AUTH_FAILURE, "Invalid Login");
				}
		}catch(Exception e){
			System.out.println("Exception in Update User push Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();		
	}

	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	@Path("/update")
	public Response updateProfile(@HeaderParam("authToken") String authToken, UserBean userBean){
		
		Object result = null;
		try{
		
			UserMaster requestingUser = UserDao.getUserFromAuthToken(authToken);
			if(requestingUser.getUserId()>0){
				userBean.getUser().getUserProfile().setUserId(requestingUser.getUserId());
				if(UserProfile.validateProfileUpdate(userBean.getUser().getUserProfile())){
					if(UserDao.updateProfile(userBean.getUser().getUserProfile())){
						userBean.setStatus(Constants.SUCCESS_RESPONSE);
							userBean.setMsg("Successfuly Updated.");
						result = userBean;
						
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
		}catch(Exception e){
			System.out.println("Exception in Update User Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	@POST
	@Path("/request/userprofile")
	@Consumes("application/json")
	@Produces("application/json")
	public Response getUserProfile(UserprofileBean userbean ){
		
		UserprofileBean userbeanRes = new UserprofileBean();
		Object result = null;
		try{
			List<Long> userlist1 = new  ArrayList<Long>();
			List<String> userlist = userbean.getUserlist();
			if(!userbean.getUserlist().isEmpty()){
				 for(String str : userlist){
					 userlist1.add(Long.parseLong(str));
				 }
				for(UserProfile usrpro : UserDao.getUserProfileforlist(userlist1))
				{
					userbeanRes.getUserlist().add(""+usrpro.getUserId());
					
				}
				userbeanRes.setUserprofilelist(UserDao.getUserProfileforlist(userlist1));
				result=userbeanRes;
				}else{
					result = ServiceResponse.getResponse(Constants.FAILURE," Empty List" );
				}
		}catch(Exception e){
			System.out.println("Exception in Request UserProfile Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

	@POST
	@Path("/request/userProfileList")
	@Consumes("application/json")
	@Produces("application/json")
	public Response requestUserProfileList(UserprofileBean userbean,@HeaderParam("authToken") String authToken ) throws JsonGenerationException, JsonMappingException, IOException{
//		HashMap<Long,FriendContact> frndhash = new HashMap<Long,FriendContact>();
		ObjectMapper mapper = new ObjectMapper();
		Gson gson = new Gson();
		Gson gson1 = new Gson();
		
		UserprofileMainBean userbeanRes = new UserprofileMainBean();
		Object frndobj = new Object();
		Object result = null;


		try{
		List<String> userlist = userbean.getUserlist();
		HashMap<String,UserProfileFriendBean> hashmap =null;
//		HashMap<Long,UserProfileFriendBean> hashmap1 = new HashMap<Long,UserProfileFriendBean>();
//		HashMap<Long,UserProfileFriendBean> hashprofile = new HashMap<Long,UserProfileFriendBean>();
		UserMaster user = UserDao.getUserFromAuthToken(authToken);
		if(user.getUserId()>0){
			if(!userbean.getUserlist().isEmpty()){
				try{
//			frndhash =UserDao.getFriendContactbyUserList(user,userbean.getUserlist());
			frndobj = FriendsDao.pullFriendsById(user, userlist);
            if(frndobj != null){
            	BasicDBObject test = (BasicDBObject) frndobj;
						
			List<FriendContact> test1 = gson1.fromJson(gson.toJson(test.get("friend")), List.class);
			hashmap =UserDao.getHashUserProfileforlist(userlist);
			
			System.out.println(mapper.writeValueAsString(test1));
			FriendContact frncnt = new FriendContact();
			
			/*for (Object obj : test1){
				  frncnt = gson1.fromJson((gson.toJson(obj)), FriendContact.class);
				  userfrnd = new UserProfileFriendBean();
					 userfrnd.setFriendcontact(frncnt);
					hashprofile.put(frncnt.getFrndId(), userfrnd);
			}
			for(Long li : userlist){
				UserProfileFriendBean ubean = null;
				ubean = hashprofile.get(li);
				if(ubean!=null){
					  ubean.setUserprofile(hashmap.get(li).getUserprofile());
					  hashmap1.put(li, ubean);
					  userbeanRes.getList().add(li);
				  }
			}*/
			
			for (Object obj : test1){
				  UserProfileFriendBean ubean = null;
				  frncnt = gson1.fromJson((gson.toJson(obj)), FriendContact.class);
				  ubean = hashmap.get(frncnt.getFrndId());
				  if(ubean!=null){
					  ubean.setFriendcontact(frncnt);
					  ubean.getUserprofile().setDisplayName(frncnt.getContactName());
					  hashmap.put(frncnt.getFrndId(), ubean);
				  }else{
					  ubean = new UserProfileFriendBean();
					  ubean.setFriendcontact(frncnt);
					  hashmap.put(frncnt.getFrndId(), ubean);
				  }
			}  
			for (String li : userlist){
				if(hashmap.get(li)!=null)
				userbeanRes.getList().add(li);
			}
			                 
                  userbeanRes.setList2(hashmap);
                  userbeanRes.setStatus(Constants.SUCCESS_RESPONSE);
                  
			result=userbeanRes;
            }else{
            	result=ServiceResponse.getResponse(Constants.FAILURE, "UserLsit is Not Associated with User");
            }
				}catch(Exception e){
					result=ServiceResponse.getResponse(200, "No friend list");
					e.printStackTrace();
					}
				}else{
					result = ServiceResponse.getResponse(Constants.FAILURE," Empty List" );
				}
			/*
				usrpro =UserDao.getHashUserProfileforlist(userlist);
				for(Long list : userlist){
					frndcontact = frndhash.get(list);
					userprofile = usrpro.get(list);
					friendbean.setFriendcontact(frndcontact);
					friendbean.setUserprofile(userprofile);
	                hashmap.put(list, friendbean);
	                userbeanRes.getList().add(list);
	                userbeanRes.getList2().add(hashmap);
				}
				result=userbeanRes;
				*/
			}else
				result = ServiceResponse.getResponse(Constants.FAILURE,"Invalid authtoken" );
		}catch(Exception e){
			System.out.println("Exception in Request UserProfileList Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	
	/*@POST
	@Path("/add/unmanaged")
	@Consumes("application/json")
	@Produces("application/json")
	public Response addUnmanageduser(@HeaderParam("authToken") String authToken , Contact contact){
		UserMaster user=UserDao.getUserFromAuthToken(authToken);
		Object result = null;
		long epoch= System.currentTimeMillis();
		if(user.getUserId()>0){
			try
			{
			UnmanagedUser unmanUser = UnmanagedDao.addUnmanagedUser(user,contact.getContactNo(),contact.getName());
			if(unmanUser.getId()>0){
				FriendContact frncon = new FriendContact();
			    frncon.setContactName(contact.getName());
			    frncon.setContactNo(contact.getContactNo());
			    frncon.setFrndStatus(Constants.NOT_REGISTERED_USER);
			    frncon.setCreatedTime(System.currentTimeMillis());
			    frncon.setFrndId(unmanUser.getUnmanageUserId());
			    
				FriendList frndlist = FriendsDao.getAssociatedUserDoc(user);
				long count = frndlist.getIdCount();
				frncon.setId(++count);
				frndlist.setIdCount(count);
				
				frndlist.setUpdatedTime(epoch);
				frndlist.setUserId(""+user.getUserId());
				frndlist.setFriends(Arrays.asList(frncon));
				FriendsDao.addFriends(frndlist);
				
				FrndConBean frnBean = new FrndConBean();
				frnBean.setFriends(frndlist.getFriends());
				frnBean.setStatus(Constants.SUCCESS_RESPONSE);
				result= frnBean;
			}
			}catch(Exception e){

				result= ServiceResponse.getResponse(501, "failed");
				e.printStackTrace();
			}
		
		}else
			result =ServiceResponse.getResponse(401, "Invalid AuthToken");

		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}*/
	
	@POST
	@Path("/add/unmanaged")
	@Consumes("application/json")
	@Produces("application/json")
	public Response adNewuser(@HeaderParam("authToken") String authToken , Contact contact){
		UserMaster user1=UserDao.getUserFromAuthToken(authToken);
		Object result = null;
		long epoch= System.currentTimeMillis();
		try{
			
		FriendContact fr = null;
		FriendList frndlist = FriendsDao.getAssociatedUserDoc(user1);
//	    String contact1=Helper.validatePhoneNo(contact.getContactNo());
	    PhoneNumber phnNum = ContactHelper.validatePhoneNumber(contact.getContactNo());
		if(phnNum !=null){
			contact.setContactNo(phnNum.getNationalNumber()+"");
			contact.setCountryCode(phnNum.getCountryCode()+"");
			
		if(user1.getUserId()>0){
			try
			{
				UserMaster user = UserDao.userLogin2(contact,TokenModal.generateToken(), Constants.NOT_REGISTERED_USER, 0);
			    if(user.getUserId()>0){
			    	fr = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, user1);
					FriendContact frncon = new FriendContact();
				    frncon.setContactName(contact.getName());
				    frncon.setContactNo(contact.getContactNo());
				    if(user.getUserType() == Constants.NOT_REGISTERED_USER);
				    	frncon.setFrndStatus(Constants.NOT_REGISTERED_USER);
				    frncon.setCreatedTime(System.currentTimeMillis());
				    frncon.setUpdatedTime(System.currentTimeMillis());
				    frncon.setFrndId(""+user.getUserId());
					    
						
					long count = frndlist.getIdCount();
					frncon.setId(++count);
					frndlist.setIdCount(count);
					
					frndlist.setUpdatedTime(epoch);
					frndlist.setUserId(""+user1.getUserId());
					frndlist.setFriends(Arrays.asList(frncon));
			    	if(fr != null)
			    	{
			    		if(fr.getFrndStatus() == 0){
			    			frndlist.setFriends(Arrays.asList(fr));
			    		}else if(fr.getFrndStatus() == 1){
			    			frndlist.setFriends(Arrays.asList(fr));
			    		}
			    	}else{
			    		FriendsDao.addFriends(frndlist);
			    	}
					
				
				ContactUserprofileBean frnBean = new ContactUserprofileBean();
				UserProfile userpro = new UserProfile();
				UserProfileFriendBean userbe = new UserProfileFriendBean();
				HashMap<String,UserProfileFriendBean> hash = new HashMap<String,UserProfileFriendBean>();
				userpro = UserDao.getUserProfileById(user.getUserId());
				Iterator<FriendContact> itr = frndlist.getFriends().iterator();
				if(itr.hasNext())
				userbe.setFriendcontact(itr.next());	
			    userbe.setUserprofile(userpro);
				hash.put(""+user.getUserId(),userbe);
				frnBean.setList(hash);
				frnBean.setStatus(Constants.SUCCESS_RESPONSE);
				result= frnBean;
			}
			}catch(Exception e){

				result= ServiceResponse.getResponse(501, "failed");
				e.printStackTrace();
			}
		
		}else
			result =ServiceResponse.getResponse(401, "Invalid AuthToken");
		}else
		    result = ServiceResponse.getResponse(402, "Invalid Contact No");          	
		}catch(Exception e){
			System.out.println("Exception in Request add Unmanaged User Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	
	@POST
	@Path("/add/staffuser")
	@Consumes("application/json")
	@Produces("application/json")
	public Response addStaff(@HeaderParam("authToken") String authToken , ContactBean contact){
		
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", contactBean : ";
		try {
			req += mapper.writeValueAsString(contact);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		Object result = null;
		try{
			UserMaster user1=UserDao.getUserFromAuthToken(authToken);
			if(user1.getUserId()>0){
			
				List<String> contact1 = new ArrayList<String>();
				Contact con1 = null;
				int flag = 0;
				List<Contact> invalidcontact = new ArrayList<Contact>();
				List<Contact> listcon = new ArrayList<Contact>();
				String tempConta = null;
				PhoneNumber phnNum = null;
				for(Contact con : contact.getContacts()){
					/*tempConta = Helper.validatePhoneNo(con.getContactNo());
					if(tempConta !=  null)
						contact1.add(tempConta);
					tempConta = null;*/
					
					phnNum = ContactHelper.validatePhoneNumber(con.getContactNo());
					if(phnNum != null){
						contact1.add(phnNum.getNationalNumber()+"");
						con.setContactNo(phnNum.getNationalNumber()+"");
						con.setCountryCode(phnNum.getCountryCode()+"");
					}
										
				}
				for(Contact con : contact.getContacts() ){
					flag = 0;
					for(String str : contact1){
						
						if(con.getContactNo().equals(str)){
							con1 = new Contact();
							con1.setContactNo(str);
						    con1.setName(con.getName());
						    con1.setCountryCode(con.getCountryCode());
						    listcon.add(con1);
						    flag = 1;
						    
						}
					} if(flag==0)
				    	invalidcontact.add(con);
					}
				if(contact1 != null){
		//			contact.setContactNo(contact1);
					
						logModel.setUser(user1.getUserId()+"_"+user1.getUserProfile().getUserName());
							List<StaffUser> user = StaffUserDao.getStaffUserByContact(contact1);
						
							
								contact.setContacts(StaffUserDao.addStaffUser(user,user1,listcon,invalidcontact));
								contact.setMsg("sucess");
								contact.setStatus(200);
							
								result =contact;
							
						
					}
			}else
				result =ServiceResponse.getResponse(401, "Invalid AuthToken");
			
		}catch(Exception e){
			System.out.println("Exception in Request Add Staff Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}		
		try {
			res = mapper.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try{
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName("Add Staff Services");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
		}catch(Exception e){
			System.out.println("Unable to add logs for Add Staff Service \n"+e.getMessage());
		}
		
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	
	@GET
	@Path("/remove/staffuser/{staffId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response removeStaff(@HeaderParam("authToken") String authToken, @PathParam("staffId") String staffId ){
		UserMaster user1=UserDao.getUserFromAuthToken(authToken);
		Object result = null;
		
		try{	
			if(user1.getUserId()>0){
				StaffUser userstaff = StaffUserDao.getStaffUserByStaffIdForWeb(staffId);
				if(userstaff != null)
				{
					if(StaffUserDao.removeStaffUser(user1.getUserId(),userstaff.getContactNo(), staffId, 4)>0){
						result =ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE, "User Removed Successfully");
					}
					else{
						result =ServiceResponse.getResponse(402, "unable to remove staff");
					}
				}
				else{
					result =ServiceResponse.getResponse(402, "Invalid StaffId");
				}
			}else
				result =ServiceResponse.getResponse(401, "Invalid AuthToken");
		}catch(Exception e){
				System.out.println("Exception in Remove staff User Service : \n"+e.getMessage());
				result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
			
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	
	/*@GET
	@Path("/staff/verify")
	@Produces("application/json")
	public Response staffLogin( 
			          @HeaderParam("contactNo") String contactNo, 
			          @HeaderParam("securityCode") String securityCode){
						
		Object result = null;
		Contact contact = new Contact();
		contact.setContactNo(contactNo);
		contact.setName("");
		StaffUserRequest st = StaffUserDao.verifyStaffUserCode( contactNo, securityCode);
		if(st != null){
			UserMaster user = UserDao.userLogin2(contact, TokenModal.generateToken(), Constants.STAFF_USER, st.getOwnerId());
			UserBean ubean = new UserBean();
			ubean.setUser(user);
			ubean.setStatus(Constants.SUCCESS_RESPONSE);
			ubean.setMsg("success");
			result = ubean;
		}else{
			result = ServiceResponse.getResponse(401, "Invalid securityCode");
		}
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
*/

	@GET
	@Path("/cancel/staffrequest/{requestId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response cancelRequestForStaffUser(@HeaderParam("authToken") String authToken,
													@PathParam("requestId") long reqId){
		Object result = null;
		try{
			UserMaster usermaster = UserDao.getUserFromAuthToken(authToken);
			if(usermaster != null){
				    StaffUserRequest st =StaffUserDao.getStaffRequestsByReqId(reqId);
					if(StaffUserDao.cancelStaffUserRequest(usermaster,reqId,4,st)){
						result = ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE,"Request Cancelled" );
					}
					else
						result = ServiceResponse.getResponse(Constants.DB_FAILURE,"database failure" );
				
				}else{
					result = ServiceResponse.getResponse(Constants.FAILURE,"Invalid authToken" );
				}
		}catch(Exception e){
			System.out.println("Exception in Cancel Staff Request Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}

	@POST
	@Path("/add/privateuser")
	@Consumes("application/json")
	@Produces("application/json")
	public Response addPrivateUser(@HeaderParam("authToken") String authToken , Contact contact){
		Object result = null;
		FriendList frndlist = null;
		try{
			UserMaster user=UserDao.getUserFromAuthToken(authToken);
			if(user.getUserId()>0){
				try
				{
					PrivateUser unmanUser = PrivateUserDao.addPrivateuser(user,contact);
				if(unmanUser != null ){
					frndlist = PrivateUserDao.addFriendContactofPrivateUser(user,contact,unmanUser);
					if(frndlist != null){
						PrivateUserBean frnBean = new PrivateUserBean();
						frnBean.setPrivateUser(unmanUser);
						frnBean.setStatus(Constants.SUCCESS_RESPONSE);
						frnBean.setFriendContact(frndlist.getFriends().get(0));
						result= frnBean;
					}else{
						result = ServiceResponse.getResponse(Constants.DB_FAILURE,"Private not added to friendContact");
					}
				}else{
					result = ServiceResponse.getResponse(Constants.FAILURE,"Already a Private User ");
				}
				
				}catch(Exception e){
		
					result= ServiceResponse.getResponse(501, "failed");
					e.printStackTrace();
				}
			
			}else
				result =ServiceResponse.getResponse(401, "Invalid AuthToken");
		
		}catch(Exception e){
			System.out.println("Exception in Add Private User Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}


	@GET
	@Path("/delete/privateUser/{privateUserId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response deletePrivateUser(@HeaderParam("authToken") String authToken,
								@PathParam("privateUserId") String privateUserId){

		Object result = null;
		try{
			UserMaster usermaster = UserDao.getUserFromAuthToken(authToken);
			if(usermaster != null){
					if(PrivateUserDao.getPrivateUserById(privateUserId,usermaster)){
						if(PrivateUserDao.deletePrivateUser(privateUserId,usermaster))
							result = ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE,"Deleted Successfully" );
						else 
							result =ServiceResponse.getResponse(Constants.DB_FAILURE,"Database failure" );
					}
					else
						result = ServiceResponse.getResponse(Constants.FAILURE,"Private user not associated with user" );
				
				}else{
					result = ServiceResponse.getResponse(Constants.AUTH_FAILURE,"Invalid authToken" );
				}
		}catch(Exception e){
			System.out.println("Exception in Delete Private User Service : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}


	@GET
	@Path("/block/{frndId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response blockUser(@HeaderParam("authToken") String authToken,
						@PathParam("frndId") String frndId){
		Object result = null;
//		try{
			UserMaster usermaster = UserDao.getUserFromAuthToken(authToken);
			if(usermaster != null){
					PrivateUserBean pub = UserHelper.blockUser(frndId, usermaster);
					if(pub.getFriendContact() != null){
						ModificationRequestDao.deleteModificationRequestOnBlock(frndId, usermaster);
						OpeningBalDao.deleteOpeningBalOnBlock(frndId, usermaster);
						TransactionDao.deleteTransactionSqlOnBlock(frndId, usermaster);
						pub.setStatus(Constants.SUCCESS_RESPONSE);
						pub.setMsg("User Blocked!");
						result = pub;
						
					}
					else
						result = ServiceResponse.getResponse(Constants.DB_FAILURE,"Failure! Unable to block this user" );
				
				}else{
					result = ServiceResponse.getResponse(Constants.AUTH_FAILURE,"Invalid authToken" );
				}
//		}catch(Exception e){
//			System.out.println("Exception in Block associate Service : \n"+e.getMessage());
//			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
//		}
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	

	@GET
	@Path("/request/associateDoc/{associateId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response getTansactionDocAndAssociateFriend(@PathParam("associateId") String associateId,
			@HeaderParam("authToken") String authToken){
		TransactionDoc transDoc = null;
		Object result = null;
		try{
		UserMaster user = UserDao.getUserFromAuthToken(authToken);
		TransactionDocFriendBean responsebean = new TransactionDocFriendBean();
		if(user.getUserId()>0)
		{   
			FriendContact frnd = FriendsDao.getFriendForWeb(associateId, 0, user);
			if(frnd != null){
			    TransactionDoc  transactionDoc = new TransactionDoc();
			    transactionDoc.setUser1(associateId);
			    transactionDoc.setUser2(""+user.getUserId());
			    transactionDoc.setDocType(frnd.getFrndStatus());
			    transDoc = TransactionDao.getTransactionDocForUser(transactionDoc);
			    if(transDoc != null){
			    	responsebean.setFriend(frnd);
			    	responsebean.setMsg("success");
			    	responsebean.setStatus(200);
			    	responsebean.setTransactionDoc(transDoc);
			    	result = responsebean;
			    }
			    else
			    {
			    	responsebean.setFriend(frnd);
			    	responsebean.setStatus(Constants.SUCCESS_RESPONSE);
			    	responsebean.setMsg("Transaction Doc Doesn't exist");
			    	responsebean.setTransactionDoc(null);
			    	result = responsebean;
			    }
			}
			else
				result = ServiceResponse.getResponse(Constants.FAILURE, "Associate Doesn't exist");
			
		}else
			result = ServiceResponse.getResponse(Constants.AUTH_FAILURE,"Invalid authToken" );
		}catch(Exception e){

			System.out.println("Exception in Block associate Service : \n"+e.getMessage());
			e.printStackTrace();
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
}

