package hisaab.services.transaction.clear_transaction.webservices;

import hisaab.services.Logs.LogHelper;
import hisaab.services.Logs.LogModel;
import hisaab.services.contacts.ContactHelper;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.transaction.clear_transaction.dao.ClearTransactionRequestDao;
import hisaab.services.transaction.clear_transaction.modal.ClearTransactionRequest;
import hisaab.services.transaction.clear_transaction.webservices.bean.ClearTransactionBean;
import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.transaction.helper.TransactionHelper;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.transaction.openingbalance.dao.OpeningBalDao;
import hisaab.services.transaction.openingbalance.modal.OpeningBalRequest;
import hisaab.services.transaction.openingbalance.webservice.bean.OpeningBalBean;
import hisaab.services.transaction.webservices.bean.TransDocBean;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;

@Path("v1/clearTrans")
public class ClearTransactionServices {
	
	@POST
	@Path("/clear")
	@Consumes("application/json")
	@Produces("application/json")
	public static Response clearTransactionRequest(@HeaderParam("authToken") String authToken, 
	 	         @HeaderParam("authId") String authId, ClearTransactionBean clrTransBean){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		Object result = null;
		try{
		ObjectMapper mapper = new ObjectMapper();
		String req = "token : "+authToken+", transactionBean : "+", authId : "+authId;
		try {
			req += mapper.writeValueAsString(clrTransBean);
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
			UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
			 logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
			 
			 FriendList frndList = FriendsDao.getAssociatedUserDoc(user);
				FriendContact frnd = null;
				for(FriendContact fn : frndList.getFriends()){
					if(fn.getFrndId().equals(clrTransBean.getClearTransRequest().getForUserId()))
						frnd = fn;
				}
			
				if(frnd != null){
//					if(!TransactionDao.checkForOpeninigBalDate(clrTransBean.getOpeningBalRequest(), frnd.getFrndStatus())){
						if(frnd.getFrndStatus() == 0){
								ClearTransactionRequest ctr = clrTransBean.getClearTransRequest();
								if(clrTransBean.getClearTransRequest().getTillDate() > 0){
									if(ClearTransactionRequestDao.addClearTransactionRequest(user, ctr)){
										clrTransBean.getClearTransRequest().setRequesterUserId(user.getUserId()+"");
										clrTransBean.setStatus(Constants.SUCCESS_RESPONSE);
										clrTransBean.setMsg("Added Request");
										result = clrTransBean;
									}else{
										clrTransBean.getClearTransRequest().setRequesterUserId(user.getUserId()+"");
										clrTransBean.setClearTransRequest(ClearTransactionRequestDao.getExistingClearTransactionRequest(clrTransBean.getClearTransRequest()));
										clrTransBean.setStatus(205);
										clrTransBean.setMsg("Already have pending Request.");
//										result = ServiceResponse.getResponse(205, "Already have pending Request.");
										result = clrTransBean;
									}
									
									
								}else{
									result = ServiceResponse.getResponse(501, "Specify Opening Balance date.");
							}
							

						}else if(frnd.getFrndStatus() != 5){
							clrTransBean.getClearTransRequest().setRequesterUserId(user.getUserId()+"");
							if(TransactionHelper.clearTransactionByDateAction(user, clrTransBean.getClearTransRequest(), frnd.getFrndStatus(), Constants.ACTION_APPROVED)){
								
								clrTransBean.setStatus(Constants.SUCCESS_RESPONSE);
								 clrTransBean.setMsg("Updated.");
								 result = clrTransBean;
							}else{
								result = ServiceResponse.getResponse(501, "Unable to add your request.");
							}
						}else{
							result = ServiceResponse.getResponse(501, "User is Blocked.");
						}
						
					/*}else{
						result = ServiceResponse.getResponse(401, "There are transactions older than the specified opening balance date.");
					}*/
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
			e.printStackTrace();
		    result = ServiceResponse.getResponse(507, "Server was unable to process the request");
		}
		timer.stop();
		timer.setMethodName("clear_transaction_request");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	
	
	@GET
	@Path("/verify/{reqId}/{response}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response clearTransactionVerification(@HeaderParam("authToken") String authToken,
				@PathParam("reqId") long reqId, @PathParam("response") int userResponse,
				@HeaderParam("authId") String authId){
		ExecutionTimeLog timer = new ExecutionTimeLog();
		timer.start();
		Object result = null;
//		try{
		ObjectMapper mapper = new ObjectMapper();
		 ClearTransactionBean ctrBean = new ClearTransactionBean();
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
			UserDao.updateTransActivityTime(user.getUserId(), System.currentTimeMillis());
			logModel.setUser(user.getUserId()+"_"+user.getUserProfile().getUserName());
			if(reqId != 0 && userResponse != 0){
				
				ClearTransactionRequest req = ClearTransactionRequestDao.getClearTransRequest(reqId, userResponse, user);
				
				if(req!= null){
					FriendContact frnd = FriendsDao.getFriendForWeb(req.getRequesterUserId(), 0, user);
					if(frnd != null && frnd.getFrndStatus() != 5)
					{
						switch (userResponse) {
						case Constants.ACTION_APPROVED:
							if(req.getStatus() == 0){
							
							if(TransactionHelper.clearTransactionByDateAction(user, req, 0, userResponse)){
								ClearTransactionRequestDao.updateClearTransactionRequest(user, req, userResponse);
								
		                        ctrBean.setStatus(Constants.SUCCESS_RESPONSE);
		                        ctrBean.setMsg("Approved !!");
		                        ctrBean.setClearTransRequest(req);
		                        result = ctrBean;
							}
							else
								result = ServiceResponse.getResponse(Constants.FAILURE, "updation failed.");
							} else{
								result = ServiceResponse.getResponse(Constants.RESPONSE_REQUEST_EXPIRED, "Request Expired.");
							}
							break;
							
						case Constants.ACTION_REJECTED: 
							if(req.getStatus() == 0){
									ClearTransactionRequestDao.updateClearTransactionRequest(user, req, userResponse);
			                        ctrBean.setStatus(Constants.SUCCESS_RESPONSE);
			                        ctrBean.setMsg("Rejected !!");
			                        ctrBean.setClearTransRequest(req);
			                        result = ctrBean;
								
								
								} else{
									result = ServiceResponse.getResponse(Constants.RESPONSE_REQUEST_EXPIRED , "Request Expired.");
								}
								break;
						
						
						default:
							result = ServiceResponse.getResponse(Constants.FAILURE, "Invalid response.");
							break;
						}
					}else{
						result = ServiceResponse.getResponse(501, "User is Blocked.");
					}
				}
				else{ 
					result = ServiceResponse.getResponse(508, "No Clear Request found for this id.");
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
		logModel.setRequestName("Clear Transaction Response");
		if(Constants.RECORD_LOGS)
			LogHelper.addLogHelper(logModel);
//		}catch(Exception e){
//			System.out.println("Exception in Request Server Token Service : \n"+e.getMessage());
//		    result = ServiceResponse.getResponse(507, "Server was unable to process the request");
//		}
		timer.stop();
		timer.setMethodName("clear_transaction_verify");
		ExcecutorHelper.addExecutionLog(timer.toString());
		return Response.status(Constants.SUCCESS_RESPONSE).entity(result).build();
	}
	
	
}
