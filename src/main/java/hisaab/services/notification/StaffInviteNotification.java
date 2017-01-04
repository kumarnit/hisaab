package hisaab.services.notification;

import hisaab.services.staff.modal.StaffUserRequest;

public class StaffInviteNotification {

	
	private String userId;
	
	private String username = "";
	
	private String userContactNo = "";
	
	private StaffUserRequest staffRequests;
	
	private int notificationType;
	
	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserContactNo() {
		return userContactNo;
	}

	public void setUserContactNo(String userContactNo) {
		this.userContactNo = userContactNo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	public StaffUserRequest getStaffRequest() {
		return staffRequests;
	}

	public void setStaffRequest(StaffUserRequest staffRequest) {
		this.staffRequests = staffRequest;
	}

	public int getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(int notificationType) {
		this.notificationType = notificationType;
	}
	
	
	
}
