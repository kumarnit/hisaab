package hisaab.services.notification;


import hisaab.services.notification.android.PullAndroidNotify;
import hisaab.services.staff.modal.StaffUser;
import hisaab.services.staff.modal.StaffUserRequest;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.transaction.openingbalance.modal.OpeningBalRequest;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class NotificationHelper {
	
	
	public static void buildAndSendTransactionNotification(final UserMaster user, final UserMaster userB, final Transaction transaction, final TransactionDoc transactionDoc, final boolean flag, final String notificationText, final int notificationType){
		Thread thrd = new Thread(){
			 @Override
	         public void run(){
				TransactionNotification tn = new TransactionNotification();
				tn.setNotificationType(notificationType);
				tn.setTransaction(transaction);
				tn.setAmount(transactionDoc.getAmount());
				tn.setUserId(""+userB.getUserId());
				if(flag)
					tn.setPaymentStatus(transactionDoc.getPaymentStatus());
				else{
					if(transactionDoc.getPaymentStatus() == Constants.TO_GIVE)
						tn.setPaymentStatus(Constants.TO_TAKE);
					else
						tn.setPaymentStatus(Constants.TO_GIVE);
				}
				if(transactionDoc.getTransactions() != null && transactionDoc.getTransactions().size()>1 )
					tn.setPullFlag(1);
				else if (transactionDoc.getModifiedTransactions() != null && transactionDoc.getModifiedTransactions().size()>1)
					tn.setPullFlag(1);
			
				PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB.getUserId()), null, tn, notificationText);
			 }
		};
		thrd.start();
	}

	
	public static void buildAndSendTransactionReadNotification(final UserMaster userB, final List<String> transIds, final int readStat, final String notificationText, final long serverTime){
		Thread thrd = new Thread(){
			 @Override
	         public void run(){
			
				 TransactionReadNotification tn = new TransactionReadNotification();
					tn.setReadStatus(readStat);
					tn.setTransactionIds(transIds);
					tn.setUserId(userB.getUserId()+"");
					tn.setServerTime(serverTime);
					tn.setNotificationType(Constants.NOTIFICATION_TRANS_READ_STAT);

				PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB.getUserId()), null, tn, notificationText);
			 }
		};
		thrd.start();
	}


	
	public static void buildAndSendStaffInviteNotification(final UserMaster user,final UserMaster userB, final StaffUserRequest staffRequest, final String notificationText, final boolean isResponse){
		Thread thrd = new Thread(){
			 @Override
	         public void run(){
			
				 StaffInviteNotification sin = new StaffInviteNotification();
				 sin.setStaffRequest(staffRequest);
				 sin.setUserId(userB.getUserId()+"");
				 sin.setUserContactNo(user.getUserProfile().getContactNo());
				 
				 if(user.getUserProfile().getUserName() != null){
					 sin.setUsername(user.getUserProfile().getUserName());
				 }
				 if(isResponse){
					 sin.setNotificationType(Constants.NOTIFICATION_STAFF_REQUEST_STATUS);
				 }
				 else{
					 sin.setNotificationType(Constants.NOTIFICATION_STAFF_INVITE);
				 }
				
					 PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB.getUserId()), null, sin, notificationText);
				 
				 
				 }
				 
		};
		thrd.start();
	}
	
	
	
	
	public static void buildAndSendBlockedUserNotification(final String blocUser, final String userId , final String notificationText, final boolean isStaff){
		Thread thrd = new Thread(){
			 @Override
	         public void run(){
			
				 
				 BlockedUserNotification bun = new BlockedUserNotification();
				 
				bun.setBlockedUser(blocUser);
				bun.setUserId(userId);
				bun.setNotificationType(Constants.NOTIFICATION_BLOCK_USER);
				
				long id = 0;
				
				
				 
				if(!isStaff){
					try {
						id =  Long.parseLong(userId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					PushNotificationControler.sendNotificationsToUser(Arrays.asList(id), null, bun, notificationText);
					
				}
				else
					PushNotificationControler.sendNotificationsToStaff(Arrays.asList(userId), null, bun, notificationText);
				 
				 
				 }
				 
		};
		thrd.start();
	}
	
	
	
	
	
	public static void buildAndSendOpeningBalRequestNotification( final OpeningBalRequest obr, final String notificationText){
		Thread thrd = new Thread(){
			 @Override
	         public void run(){
			
				 
				 OpeningBalanceNotification obn = new OpeningBalanceNotification();
				 obn.setUserId(obr.getForUserId());
				 obn.setOpeningBalReq(obr);
				 obn.setNotificationType(Constants.NOTIFICATION_OPENING_BAL_REQ);
				 	
				 try {
					long id = Long.parseLong(obr.getForUserId());
					PushNotificationControler.sendNotificationsToUser(Arrays.asList(id), null, obn, notificationText);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				 
			 }
		};
		thrd.start();
	}
	
	public static void buildAndSendStaffRemovalNotification(final long userB, final String staffUser,  final String notificationText, final boolean forStaff){
		Thread thrd = new Thread(){
			 @Override
	         public void run(){
			
				 StaffRemoveNotification srn = new StaffRemoveNotification();
				 srn.setNotificationType(Constants.NOTIFICATION_STAFF_REMOVAL_STATUS);
				 srn.setOwnerId(userB+"");
				 srn.setStaffUserId(staffUser);
				 srn.setServerTime(System.currentTimeMillis());
				
				 if(forStaff){
					srn.setUserId(staffUser);
					PushNotificationControler.sendNotificationsToStaff(Arrays.asList(staffUser), null, srn, notificationText);
				 }else{
					 srn.setUserId(userB+"");
					 PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB), null, srn, notificationText);
				 }
			 }
		};
		thrd.start();
	}
	public static void buildAndSendOpeningBalanceResponse(final long userB, final OpeningBalRequest obr, final String notificationText){
		Thread thrd = new Thread(){
			 @Override
	         public void run(){
			
				 OpeningBalanceNotification obn = new OpeningBalanceNotification();
				 obn.setUserId(obr.getForUserId());
				 obn.setOpeningBalReq(obr);
				 obn.setNotificationType(Constants.NOTIFICATION_OPENING_BAL_RES);
				 	
				 try {
					
					PushNotificationControler.sendNotificationsToUser(Arrays.asList(userB), null, obn, notificationText);
				    System.out.println("oo7 : opening balance response");
				 } catch (Exception e) {
					System.out.println(e.getMessage());
				}
			 }
		};
		thrd.start();
	}
	public static void buildAndSendUpdateSystemNotification(final SystemUpdateNotification obsys, final String notificationText){
		Thread thrd = new Thread(){
			 @Override
	         public void run(){
				
				 try {
					 PullAndroidNotify.setAndSendAndroidSystemNotification(obsys,notificationText,Constants.SYSTEM_NOTIFICATION_UPDATE);
				    System.out.println("oo7 : System Notification");
				 } catch (Exception e) {
					System.out.println(e.getMessage());
				}
			 }
		};
		thrd.start();
	}
	public static void buildAndSendServerMigrateSystemNotification(final ServerMigrateNotification obsys, final String notificationText){
		Thread thrd = new Thread(){
			 @Override
	         public void run(){
				
				 try {
					 PullAndroidNotify.setAndSendAndroidSystemNotification(obsys,notificationText,Constants.SYSTEM_NOTIFICATION_SERVER_MIGRATE);
				    System.out.println("oo7 : System Notification");
				 } catch (Exception e) {
					System.out.println(e.getMessage());
				}
			 }
		};
		thrd.start();
	}
	public static void SendSimpleMessageAsThread(final String msg) {
		Thread thrd = new Thread(){
			 @Override
	         public void run(){
				 for(int i = 0; i <= 20;i++){
					 System.out.println(msg+" -> "+i);
					 if(i == 10){
						try {
							sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 }
				 }
			 }
		};
		thrd.start();
	}
	public static void buildAndSendAutoDeleteNotification(final List<Long> users, final AutoDeleteNotification msg, final String notificationText){
		Thread thrd = new Thread(){
			 @Override
	         public void run(){
														
				     ObjectMapper mapper = new ObjectMapper();
					 PushNotificationControler.sendNotificationsToUser(users, null, msg, notificationText);
				 try {
					System.out.println("]]"+mapper.writeValueAsString(msg));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		};
		thrd.start();
	}
	public static void main(String[] args) {
		System.out.println("in main....");
		SendSimpleMessageAsThread("First");
		SendSimpleMessageAsThread("Second");
		System.out.println("exit main...");
	}
}
