package hisaab.services.contacts.services;




import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import hisaab.services.Logs.LogHelper;
import hisaab.services.Logs.LogModel;
import hisaab.services.contacts.ContactHelper;
import hisaab.services.contacts.dao.ContactsDao;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.Contact;
import hisaab.services.contacts.modal.ContactList;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.contacts.services.bean.ContactListBean;
import hisaab.services.contacts.services.bean.FriendListBean;
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

import org.codehaus.jackson.map.ObjectMapper;

@Path("v1/contact")
public class ContactServices {
	
	@POST
	@Path("/add")
	@Consumes("application/json")
	@Produces("application/json")
	public Response addContactList(@HeaderParam("authToken") String authToken, 
			       @HeaderParam("authId") String authId,  ContactListBean contactListBean ){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		long epoch = System.currentTimeMillis();
		Object result = null;
		String req = "token : "+authToken+", authId : "+authId+"contactSize :"
				+ ""+contactListBean.getContactList().size();
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authId);
		try{

		UserMaster user = null;
		
		if(Constants.AUTH_USERID){
			user = UserDao.getUserFromAuthToken1(authToken,authId);
		}
		else{
			user = UserDao.getUserFromAuthToken(authToken);
		}
		
		if(user.getUserId()>0){
			UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
			logModel.setUser(user.getUserId()+"");
			ContactList clist = ContactsDao.getContactsDocForUser(user);
			clist.setContactList(ContactHelper.validateContactNoList2(contactListBean.getContactList()));
			clist.setUpdatedTime(epoch);
			/*long conId = clist.getIdCount();
			for(Contact con : clist.getContactList()){
				con.setId(++conId);
			}
			clist.setIdCount(conId);
			*/
			/**
			 * Removing User's own number from list
			 **/
			Iterator<Contact> itr = clist.getContactList().iterator();
			while(itr.hasNext())
			{
			   if(itr.next().getContactNo().equals(user.getContactNo()))
			   {
				   itr.remove();
			   }
			}
			

//			System.out.println("==> "+clist.getContactList().get(0).getName());

			if(!clist.getContactList().isEmpty()){
				ContactsDao.addContacts(clist);
				
				FriendList frndList = FriendsDao.getAssociatedUserDoc(user);
				
				if(frndList.getFriends().isEmpty()){
					frndList.setFriends(ContactHelper.getFriends(clist,frndList.getIdCount()));
					frndList.setIdCount(frndList.getFriends().size());
				}else{
					List<FriendContact> newFrndList = ContactHelper.getFriends(clist,frndList.getIdCount());
					HashMap<String, FriendContact> existingFrndMap = new HashMap<String,FriendContact>();
					for(FriendContact frnd : frndList.getFriends()){
						existingFrndMap.put(frnd.getFrndId(),frnd);
					}
					long id = frndList.getIdCount();
					Iterator<FriendContact> frndIterator = newFrndList.iterator();
					while(frndIterator.hasNext()){
						FriendContact frnd = frndIterator.next();
						FriendContact tempFr = existingFrndMap.get(frnd.getFrndId());
						if( tempFr != null ){
							frndIterator.remove();
						}else{
							frnd.setId(++id);

						}
					}
						frndList.setIdCount(id);
						frndList.setFriends(newFrndList);
					}
	//				frndList.setIdCount(frndList.getFriends().size());
					if(!frndList.getFriends().isEmpty())
						FriendsDao.addFriends(frndList);
					for(FriendContact frndcon : frndList.getFriends())
					{
						contactListBean.getFriendIds().add(frndcon.getFrndId());
					}
					contactListBean.setFriends(frndList.getFriends());
					contactListBean.setStatus(Constants.SUCCESS_RESPONSE);
					contactListBean.setMsg("Contacts Added Successfully");
					contactListBean.setContactList(clist.getContactList());
					result = contactListBean;
					res ="Friend List Sze :"+contactListBean.getFriends().size();
				}
				else
			     	result = ServiceResponse.getResponse(402, "NO contact to add");
			}else{
				result = ServiceResponse.getResponse(401, "Invalid Server Token");
			}
		
		try{
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName("addContact List");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
		}catch(Exception e){
			System.out.println("Unable to add log records for : add Contact Service \n"+e.getMessage());
		}	
		
		}catch(Exception e){
			System.out.println("Exception in Upload Contact : \n"+e.getMessage());
			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		timer.stop();
		timer.setMethodName("add_contact_list");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	
	@GET
	@Path("/get/frnds")
	@Consumes("application/json")
	@Produces("application/json")
	public Response getFriendList(@HeaderParam("authToken") String authToken,
				@HeaderParam("authId") String authId) {
				ExecutionTimeLog timer = new ExecutionTimeLog();
				timer.start();
				    Object result = null;
				    
				    String req = "token : "+authToken+", authId : "+authId;	
					String res = "";
					LogModel logModel = new LogModel();
					logModel.setUserToken(authId);
					
				    try{
					    String userId;
					    FriendListBean frndbean=new FriendListBean();
					    
					    UserMaster user = null;
					    
					    if(Constants.AUTH_USERID){
							user = UserDao.getUserFromAuthToken1(authToken,authId);
						}
						else{
							user = UserDao.getUserFromAuthToken(authToken);
						}
					    if(user.getUserId()>0){
					    	UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
						    userId=""+user.getUserId();
						    List<FriendContact> frnd=ContactsDao.getFriendListbyUserId(userId);
												
						    frndbean.setFriends(frnd);
						    frndbean.setMsg("Success");
						    frndbean.setStatus(200);
							result = frndbean;
							res = "Friend Size :"+frndbean.getFriends().size();
	                    }else{
		                     result = ServiceResponse.getResponse(401, "Invalid Server Token");
	                         }
				    
				    
				    try{
						logModel.setRequestData(req);
						logModel.setResponseData(res);
						logModel.setRequestName("getfriend list");
						if(Constants.RECORD_LOGS)
							LogHelper.addLogHelper(logModel);
					}catch(Exception e){
						System.out.println("Unable to add log records for : get friend list Service \n"+e.getMessage());
					}
				    
				    }catch(Exception e){
						System.out.println("Exception in get Associated userList : \n"+e.getMessage());
						result = ServiceResponse.getResponse(507, "Server was unable to process the request");
					}
				    timer.stop();
				    timer.setMethodName("get_friend_list");
				    ExcecutorHelper.addExecutionLog(timer.toString());
				    System.out.println(timer.toString());
                    return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
            }
	
	@POST
	@Path("/update/frnds")
	@Produces("application/json")
	@Consumes("application/json")
	
	public Response updateFriend(@HeaderParam("authToken") String authToken,FriendListBean friendlistbean,
					@HeaderParam("authId") String authId) {
					ExecutionTimeLog timer = new ExecutionTimeLog();
					timer.start();
					List<FriendContact> frndcont;
					ObjectMapper mapper = new ObjectMapper();
					Object result=null;
					String req = "token : "+authToken+", authId : "+authId
							+ "friendlist Bean :"+friendlistbean;	
					String res = "";
					LogModel logModel = new LogModel();
					logModel.setUserToken(authId);
					try{
						UserMaster user = null;
						if(Constants.AUTH_USERID){
							user = UserDao.getUserFromAuthToken1(authToken,authId);
						}
						else{
							user = UserDao.getUserFromAuthToken(authToken);
						}
						if (user.getUserId()>0)
						{
							UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
							 frndcont= friendlistbean.getFriends();
							 FriendsDao.updateFriend(frndcont,user);
							 result=ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE, "Successfully updated");
						}
						else
						result=ServiceResponse.getResponse(Constants.SUCCESS_RESPONSE, "invalid token");
					
					try {
						res = mapper.writeValueAsString(result);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try{
						logModel.setRequestData(req);
						logModel.setResponseData(res);
						logModel.setRequestName("update friend");
						if(Constants.RECORD_LOGS)
							LogHelper.addLogHelper(logModel);
					}catch(Exception e){
						System.out.println("Unable to add log records for : update friend Service \n"+e.getMessage());
					}
					
					}catch(Exception e){
						System.out.println("Exception in Update Friends : \n"+e.getMessage());
						result = ServiceResponse.getResponse(507, "Server was unable to process the request");
					}
					timer.stop();
					timer.setMethodName("update_friends");
					ExcecutorHelper.addExecutionLog(timer.toString());
					System.out.println(timer.toString());
                    return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
            }
	
	@POST
	@Path("/add/test")
	@Consumes("application/json")
	@Produces("application/json")
	public Response addContactListtest(@HeaderParam("authToken") String authToken, 
			      @HeaderParam("authId") String authId, ContactListBean contactListBean ){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		long epoch = System.currentTimeMillis();
		Object result = null;
		String req = "token : "+authToken+", authId : "+authId+"contactSize :"
				+ ""+contactListBean.getContactList().size();
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authId);
		
		long test1,test2;
		test1 = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
//		try{

			
		UserMaster user = null;
		
		if(Constants.AUTH_USERID){
			user = UserDao.getUserFromAuthToken1(authToken,authId);
		}
		else{
			user = UserDao.getUserFromAuthToken(authToken);
		}
		
		System.out.println(authToken+" in contact service called "+test1);
		if(user.getUserId()>0){
			
			UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
			ContactList clist = ContactsDao.getContactsDocForUser(user);
			clist.setContactList(ContactHelper.validateContactNoList2(contactListBean.getContactList()));
			clist.setUpdatedTime(epoch);
			List<FriendContact> unmangedFriend = new ArrayList<FriendContact>();
			/*long conId = clist.getIdCount();
			for(Contact con : clist.getContactList()){
				con.setId(++conId);
			}
			clist.setIdCount(conId);
			*/
			/**
			 * Removing User's own number from list
			 **/
			Iterator<Contact> itr = clist.getContactList().iterator();
			while(itr.hasNext())
			{
			   if(itr.next().getContactNo().equals(user.getContactNo()))
			   {
				   itr.remove();
			   }
			}
			

//			System.out.println("==> "+clist.getContactList().get(0).getName());

			if(!clist.getContactList().isEmpty()){
				ContactsDao.addContacts(clist);
				/*for(Contact contact24 : clist.getContactList()){
					FriendContact friendunmanged = UserDao.addUnRegisteredUser(user, contact24);
					if(friendunmanged != null){
						unmangedFriend.add(friendunmanged);
					}
				}*/
				FriendList frndList = FriendsDao.getAssociatedUserDoc(user);
				
				if(frndList.getFriends().isEmpty()){
					frndList.setFriends(ContactHelper.getFriends(clist,frndList.getIdCount()));
					frndList.setIdCount(frndList.getFriends().size());
				}else{
					List<FriendContact> newFrndList = ContactHelper.getFriends(clist,frndList.getIdCount());
					HashMap<String, FriendContact> existingFrndMap = new HashMap<String,FriendContact>();
					for(FriendContact frnd : frndList.getFriends()){
						if(frnd.getFrndStatus() != Constants.PRIVATE_USER)
							 existingFrndMap.put(frnd.getFrndId(),frnd);
					}
					long id = frndList.getIdCount();
					Iterator<FriendContact> frndIterator = newFrndList.iterator();
					while(frndIterator.hasNext()){
						FriendContact frnd = frndIterator.next();
						FriendContact tempFr = existingFrndMap.get(frnd.getFrndId());
						if( tempFr != null ){
							frndIterator.remove();
						}else{
							frnd.setId(++id);

						}
					}
						frndList.setIdCount(id);
						frndList.setFriends(newFrndList);
//						frndList.getFriends().addAll(unmangedFriend);
					}
	//				frndList.setIdCount(frndList.getFriends().size());
					if(!frndList.getFriends().isEmpty())
						FriendsDao.addFriends(frndList);
					for(FriendContact frndcon : frndList.getFriends())
					{
						contactListBean.getFriendIds().add(frndcon.getFrndId());
					}
					contactListBean.setFriends(frndList.getFriends());
					contactListBean.setStatus(Constants.SUCCESS_RESPONSE);
					contactListBean.setMsg("Contacts Added Successfully");
					contactListBean.setContactList(clist.getContactList());
					result = contactListBean;
					
					res ="Friend List Sze :"+contactListBean.getFriends().size();
				}
				else
			     	result = ServiceResponse.getResponse(402, "NO contact to add");
			}else{
				result = ServiceResponse.getResponse(401, "Invalid Server Token");
			}
		
		
		test2 = System.currentTimeMillis();
		System.out.println(authToken+"  contact service finished :"+test2);
		System.out.println("!! Difference : "+(test2-test1));
		
		try{
			logModel.setRequestData(req);
			logModel.setResponseData(res);
			logModel.setRequestName("addContact List");
			if(Constants.RECORD_LOGS)
				LogHelper.addLogHelper(logModel);
		}catch(Exception e){
			System.out.println("Unable to add log records for : add Contact Service \n"+e.getMessage());
		}
		
//		}catch(Exception e){
//			System.out.println("Exception in Upload Contact : \n"+e.getMessage());
//			result = ServiceResponse.getResponse(507, "Server was unable to process the request");
//		}
		timer.stop();
		timer.setMethodName("add_contact_list_new");
		ExcecutorHelper.addExecutionLog(timer.toString());
		System.out.println(timer.toString());
	      
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
}
