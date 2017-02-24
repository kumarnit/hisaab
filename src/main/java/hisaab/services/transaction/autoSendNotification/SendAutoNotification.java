package hisaab.services.transaction.autoSendNotification;

import hisaab.services.notification.AutoDeleteNotification;
import hisaab.services.notification.NotificationHelper;
import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.transaction.helper.TransactionHelper;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.request.dao.ModificationRequestDao;
import hisaab.services.transaction.request.modal.ModificationRequest;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

public class SendAutoNotification {
	public static void sendDeleteNotification(){
		Map<String,List<String>> pendingModReq = null;
		ObjectMapper mapper = new ObjectMapper();
		pendingModReq = ModificationRequestDao.getAllDeletePendingTransactionIdForNotification();
		AutoDeleteNotification msg = new AutoDeleteNotification();
		for(String daysKey :pendingModReq.keySet()){
			
			msg.setMessage("Some Transaction are Pending for Approval from "+(Long.parseLong(daysKey))+" Days.\n"
					+ "Please approve or Reject, Otherwise it will automatically Approved in "+(5-Long.parseLong(daysKey))+" Days" );
			msg.setNotificationType(Constants.NOTIFICATION_AUTO_DELETE_TRANSACTION);
			List<String> modreq = pendingModReq.get(daysKey);
			List<Long> users = new ArrayList<Long>();
			for(String users1 : modreq){
				users.add(Long.parseLong(users1));
			}
			if(users != null && !users.isEmpty()){
				NotificationHelper.buildAndSendAutoDeleteNotification(users, msg," Delete Transaction Action");			}
	
		}
		
		
	}
	
	
	public static void sendUpdateNotification(){
		Map<String,List<String>> pendingModReq = null;
		ObjectMapper mapper = new ObjectMapper();
		pendingModReq = ModificationRequestDao.getAllUpdatePendingTransactionIdForNotification();
		AutoDeleteNotification msg = new AutoDeleteNotification();
		for(String daysKey :pendingModReq.keySet()){
			
			msg.setMessage("Some Transaction are Pending for Approval from "+(Long.parseLong(daysKey))+" Days.\n"
					+ "Please approve or Reject, Otherwise it will automatically Approved in "+(5-Long.parseLong(daysKey))+" Days" );
			msg.setNotificationType(Constants.NOTIFICATION_AUTO_DELETE_TRANSACTION);
			List<String> modreq = pendingModReq.get(daysKey);
			List<Long> users = new ArrayList<Long>();
			for(String users1 : modreq){
				users.add(Long.parseLong(users1));
			}
			if(users != null && !users.isEmpty()){
				NotificationHelper.buildAndSendAutoDeleteNotification(users, msg," Update Transaction Action");			}
	
		}
		
	}
		
	
	public static void sendClearTransNotification(){
		Map<Integer,List<Long>> pendingClrTranReq = null;
		ObjectMapper mapper = new ObjectMapper();
		pendingClrTranReq = TransactionHelper.autoApprovalClearTransactionNotification();
		AutoDeleteNotification msg = new AutoDeleteNotification();
		for(int daysKey :pendingClrTranReq.keySet()){
			
			msg.setMessage("Some Clear Transaction Requests are Pending for Approval from "+daysKey+" Days.\n"
					+ "Please Approve or Reject, Otherwise it will automatically Approved in "+(5-daysKey)+" Days" );
			msg.setNotificationType(Constants.NOTIFICATION_AUTO_DELETE_TRANSACTION);
			
			List<Long> users = pendingClrTranReq.get(daysKey);
			
			if(users != null && !users.isEmpty()){
				NotificationHelper.buildAndSendAutoDeleteNotification(users, msg," Clear Transaction Action");			}
	
		}
	}
	
		
public static void main(String[] arg){
	sendUpdateNotification();
}
}
