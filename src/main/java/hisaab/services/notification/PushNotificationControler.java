package hisaab.services.notification;

import hisaab.services.notification.android.PullAndroidNotify;
import hisaab.services.staff.dao.StaffUserDao;
import hisaab.services.staff.modal.StaffUser;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;

import java.util.ArrayList;
import java.util.List;






public class PushNotificationControler {
	
	public static void sendNotificationsToUser(List<Long> userIds, final String iosCertpath, final Object notification, final String message){
		List<UserMaster> users  = new ArrayList<UserMaster>();
		ArrayList<String> androidPushIds = new ArrayList<String>();
		ArrayList<String> iosPushIds = new ArrayList<String>();
		
		if(!(userIds.isEmpty())){
			 
			users = UserDao.getUserByIds(userIds);
			long counter = 1;
			long userListSize = users.size();

			for(UserMaster user : users){
				
				if(user.getDeviceType() == Constants.ANDROID){
					androidPushIds.add(user.getPushId());
				}
				else if(user.getDeviceType() == Constants.IOS){
					iosPushIds.add(user.getPushId());
				}
				
				if (counter % Constants.BULK_PUSH_COUNTER == 0) {
//					PullIosNotify.setAndSendIOSNotification(iosCertpath, iosPushIds, notification, message);
					PullAndroidNotify.setAndSendAndroidNotification(androidPushIds, notification, message);
					
					iosPushIds = new ArrayList<String>(); 
					androidPushIds = new ArrayList<String>();
				}

				if (counter == userListSize) {
					
					if(!(iosPushIds.isEmpty()))	{				
//						PullIosNotify.setAndSendIOSNotification(iosCertpath, iosPushIds, notification, message);
					}
					if(!(androidPushIds.isEmpty()))
						PullAndroidNotify.setAndSendAndroidNotification(androidPushIds, notification, message);
				}

				counter++;

			}
			
			// put code for striking notification for ios & aos
		}
	}
	
	
	public static void sendNotificationsToStaff(List<String> staffIds, final String iosCertpath, final Object notification, final String message){
		List<StaffUser> users  = new ArrayList<StaffUser>();
		ArrayList<String> androidPushIds = new ArrayList<String>();
		ArrayList<String> iosPushIds = new ArrayList<String>();
		
		if(!(staffIds.isEmpty())){
			 
			users = StaffUserDao.getStaffUserByStaffIds(staffIds);
			long counter = 1;
			long userListSize = users.size();

			for(StaffUser user : users){
				
				if(user.getDeviceType() == Constants.ANDROID){
					androidPushIds.add(user.getPushId());
				}
				else if(user.getDeviceType() == Constants.IOS){
					iosPushIds.add(user.getPushId());
				}
				
				if (counter % Constants.BULK_PUSH_COUNTER == 0) {
//					PullIosNotify.setAndSendIOSNotification(iosCertpath, iosPushIds, notification, message);
					PullAndroidNotify.setAndSendAndroidNotification(androidPushIds, notification, message);
					
					iosPushIds = new ArrayList<String>(); 
					androidPushIds = new ArrayList<String>();
				}

				if (counter == userListSize) {
					
					if(!(iosPushIds.isEmpty()))	{				
//						PullIosNotify.setAndSendIOSNotification(iosCertpath, iosPushIds, notification, message);
					}
					if(!(androidPushIds.isEmpty()))
						PullAndroidNotify.setAndSendAndroidNotification(androidPushIds, notification, message);
				}

				counter++;

			}
			
			// put code for striking notification for ios & aos
		}
	}

}
