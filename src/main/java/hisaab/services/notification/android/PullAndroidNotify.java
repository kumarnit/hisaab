package hisaab.services.notification.android;

import hisaab.services.notification.SystemUpdateNotification;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;

import java.util.ArrayList;
import java.util.List;




public class PullAndroidNotify {

	/**
	 * Async Thread to send the push notification for pulling the data onto the device for particular userIds
	 * @param userIds
	 * @param notification
	 * @param message
	 */
	public static void sendPullNotificationByUserIds(final List<Long> userIds,  final Object notification, final String message) {

		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					List<String> androidPushIds = new ArrayList<String>();
					
					List<UserMaster> users = UserDao.getUserByIds(userIds);
					

					long counter = 1;
					long userListSize = users.size();

					for (UserMaster user : users) {

						
						if(user.getDeviceType() == Constants.ANDROID){
							androidPushIds.add(user.getPushId());
						}
						if (counter % Constants.BULK_PUSH_COUNTER == 0) {
							
							setAndSendAndroidNotification(androidPushIds, notification, message);
							
							androidPushIds = new ArrayList<String>(); 
						}

						if (counter == userListSize) {

							setAndSendAndroidNotification( androidPushIds, notification, message);

						}

						counter++;

					} // End Loop
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	
	public static void setAndSendAndroidNotification(List<String> devices, Object notification, String message) {
		System.out.println("Sending..");
		try {
			AndroPushPayload payload = new AndroPushPayload();
			payload.setTitle(message);
			payload.setData(notification);
			SendNotification send = new SendNotification();
			send.pushFCMNotification(payload, devices);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void setAndSendAndroidSystemNotification(Object notification, String message,String topic) {
		System.out.println("Sending..S");
		try {
			AndroPushPayload payload = new AndroPushPayload();
			payload.setTitle(message);
			payload.setData(notification);
			SendNotification send = new SendNotification();
			send.pushSystemNotification(payload, topic);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}


