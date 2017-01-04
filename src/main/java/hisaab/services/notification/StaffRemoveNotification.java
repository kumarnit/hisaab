package hisaab.services.notification;

public class StaffRemoveNotification {
	
	private String userId;
	
	private String staffUserId;
	
	private String ownerId;
	
	private long serverTime;
	
	private int notificationType;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStaffUserId() {
		return staffUserId;
	}

	public void setStaffUserId(String staffUserId) {
		this.staffUserId = staffUserId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public long getServerTime() {
		return serverTime;
	}

	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}

	public int getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(int notificationType) {
		this.notificationType = notificationType;
	}
	
	
	
}
