package hisaab.services.transaction.openingbalance.webservice;

import hisaab.services.Logs.LogHelper;
import hisaab.services.Logs.LogModel;
import hisaab.services.contacts.ContactHelper;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.transaction.openingbalance.dao.OpeningBalDao;
import hisaab.services.transaction.openingbalance.modal.OpeningBalRequest;
import hisaab.services.transaction.openingbalance.webservice.bean.OpeningBalBean;
import hisaab.services.transaction.request.dao.ModificationRequestDao;
import hisaab.services.transaction.request.modal.ModificationRequest;
import hisaab.services.transaction.request.webservices.OpeningBalHelper;
import hisaab.services.transaction.webservices.bean.TransDocBean;
import hisaab.services.transaction.webservices.bean.TransactionBean;
import hisaab.services.transaction.webservices.bean.TransactionBean1;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;
import hisaab.util.ServiceResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;

@Path("v1/openingbal")
public class OpeningBalService {
	
	@POST
	@Path("/add")
	@Consumes("application/json")
	@Produces("application/json")
	public Response openingBalRequest(@HeaderParam("authToken") String authToken, 
	 	         @HeaderParam("authId") String authId, OpeningBalBean obrBean){
		Object result = null;
		try{
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", transactionBean : "+", authId : "+authId;
		try {
			req += mapper.writeValueAsString(obrBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		
		
		UserMaster user = null;
		if(Constants.AUTH_USERID){
			user = UserDao.getUserFromAuthToken1(authToken,authId);
		}
		else{
			user = UserDao.getUserFromAuthToken(authToken);
		}
		if(user.getUserId()>0){
			 logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
//			 int i = OpeningBalHelper.validateOpeningBalRequest(obrBean.getOpeningBalRequest(), user);
			 
			 FriendList frndList = FriendsDao.getAssociatedUserDoc(user);
				FriendContact frnd = null;
				for(FriendContact fn : frndList.getFriends()){
					if(fn.getFrndId().equals(obrBean.getOpeningBalRequest().getForUserId()))
						frnd = fn;
				}
			
				if(frnd != null){
					if(!TransactionDao.checkForOpeninigBalDate(obrBean.getOpeningBalRequest(), frnd.getFrndStatus())){
						if(frnd.getFrndStatus() == 0){
							if(obrBean.getOpeningBalRequest().getOpeningBalAmt() >0){
								if(obrBean.getOpeningBalRequest().getOpeningBalDate() > 0){
									if(obrBean.getOpeningBalRequest().getPaymentStatus() == 1
											|| obrBean.getOpeningBalRequest().getPaymentStatus() == 2){
										if(OpeningBalDao.addOpeningBalRequest(user, obrBean.getOpeningBalRequest())){
											obrBean.getOpeningBalRequest().setRequesterUserId(user.getUserId()+"");
										obrBean.setStatus(Constants.SUCCESS_RESPONSE);
										obrBean.setMsg("Added Request");
										result = obrBean;
										ContactHelper.checkAndAddAssociate(frnd, user, obrBean.getOpeningBalRequest().getForUserId(), null);

										}else{
											result = ServiceResponse.getResponse(501, "AllReady have pending Request.");
										}
									}else{
										result = ServiceResponse.getResponse(501, "Please Enter Valid Payement Type.");
									}	
								}else{
									result = ServiceResponse.getResponse(501, "Specify Opening Balance date.");
										}
							}else{
								result = ServiceResponse.getResponse(501, "Opening Balance Amount cannot be null or Zero.");
								}
								
						}else if(frnd.getFrndStatus() != 5){
							obrBean.getOpeningBalRequest().setRequesterUserId(user.getUserId()+"");
							if(TransactionDao.updateOpeningBalTransactionDoc(user, obrBean.getOpeningBalRequest(), frnd.getFrndStatus())){
								
								obrBean.setStatus(Constants.SUCCESS_RESPONSE);
								 obrBean.setMsg("Updated the opening Bal.");
								 result = obrBean;
							}else{
								result = ServiceResponse.getResponse(501, "Unable to add your request.");
							}
						}else{
							result = ServiceResponse.getResponse(501, "User is Blocked.");
						}
						
					}else{
						result = ServiceResponse.getResponse(401, "There are transactions older than the specified opening balance date.");
					}
				}
				else{
					result = ServiceResponse.getResponse(405, "The specified user is not associated with you.");
				}
		}
		else{
			result = ServiceResponse.getResponse(Constants.INVALID_PARAM, "Invalid Auth Token");
		}
		try {
			res = mapper.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logModel.setRequestData(req);
		logModel.setResponseData(res);
		logModel.setRequestName("update opening balance");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		
		}catch(Exception e){
			System.out.println("Exception in Request Server Token Service : \n"+e.getMessage());
		    result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	
	
	
	
	
	
	@GET
	@Path("/verify/{reqId}/{response}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response openingBalVerification(@HeaderParam("authToken") String authToken,
				@PathParam("reqId") long reqId, @PathParam("response") int userResponse,
				@HeaderParam("authId") String authId){
		Object result = null;
		try{
		ObjectMapper mapper = new ObjectMapper();
		 OpeningBalBean obrBean = new OpeningBalBean();
		String req1 = "token : "+authToken+", requestId : "+reqId+
						", response :"+userResponse+", authId : "+authId;
		String res = "";
		LogModel logModel = new LogModel();
		logModel.setUserToken(authToken);
		TransDocBean trnsDocBean = new TransDocBean();
		
		
		TransactionDoc transDoc = null;
		long epoch = System.currentTimeMillis();
		UserMaster user = null;
		if(Constants.AUTH_USERID){
			user = UserDao.getUserFromAuthToken1(authToken,authId);
		}
		else{
			user = UserDao.getUserFromAuthToken(authToken);
		}
		
//		TransactionDoc transDoc = null;
		if(user.getUserId()>0){
			logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
			if(reqId != 0 && userResponse != 0){
				
				OpeningBalRequest req = OpeningBalDao.getOpeningBalRequest(reqId, userResponse, user);
				
				if(req!= null){
						switch (userResponse) {
						case 1:
							if(req.getStatus() == 0){
							if(OpeningBalDao.updateOpeningBalRequest(user,req,userResponse))
							{   
								TransactionDao.updateOpeningBalTransactionDoc(user, req, 0);
		                        obrBean.setStatus(Constants.SUCCESS_RESPONSE);
		                        obrBean.setMsg("Approved !!");
		                        obrBean.setOpeningBalRequest(req);
		                        result = obrBean;
							}
							else
								result = ServiceResponse.getResponse(Constants.FAILURE, "updation failed.");
							} else{
								result = ServiceResponse.getResponse(Constants.FAILURE, "Request Expired.");
							}
							break;
							
						case 2: 
							if(req.getStatus() == 0){
			                        OpeningBalDao.updateOpeningBalRequest(user,req,userResponse);
			                        obrBean.setStatus(Constants.SUCCESS_RESPONSE);
			                        obrBean.setMsg("Rejected !!");
			                        obrBean.setOpeningBalRequest(req);
			                        result = obrBean;
								
								
								} else{
									result = ServiceResponse.getResponse(Constants.FAILURE, "Request Expired.");
								}
								break;
						
						
						default:
							result = ServiceResponse.getResponse(Constants.FAILURE, "Invalid response.");
							break;
						}
					
				}
				else{ 
					result = ServiceResponse.getResponse(501, "No Oening Balance found for this id.");
				}
			}else{
				result = ServiceResponse.getResponse(402, "request Id and reponse cannot be null");
			}
			 
		}else{
			result = ServiceResponse.getResponse(401, "Invalid Auth Token");
		}
		
		
		try {
			res = mapper.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logModel.setRequestData(req1);
		logModel.setResponseData(res);
		logModel.setRequestName("transaction Action Response");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
		}catch(Exception e){
			System.out.println("Exception in Request Server Token Service : \n"+e.getMessage());
		    result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
}
