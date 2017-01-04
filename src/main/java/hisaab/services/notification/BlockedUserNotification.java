package hisaab.services.notification;

public class BlockedUserNotification {

	
	private String userId;
	
	private int notificationType;
	
	private String blockedUser;

	
	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(int notificationType) {
		this.notificationType = notificationType;
	}

	public String getBlockedUser() {
		return blockedUser;
	}

	public void setBlockedUser(String blockedUser) {
		this.blockedUser = blockedUser;
	}
	
	
}
