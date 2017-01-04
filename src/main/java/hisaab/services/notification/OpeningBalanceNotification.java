package hisaab.services.notification;

import hisaab.services.transaction.openingbalance.modal.OpeningBalRequest;

public class OpeningBalanceNotification {

	
	private int notificationType;
	
	private String userId;
	
	private OpeningBalRequest openingBalReq;

	public int getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(int notificationType) {
		this.notificationType = notificationType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public OpeningBalRequest getOpeningBalReq() {
		return openingBalReq;
	}

	public void setOpeningBalReq(OpeningBalRequest openingBalReq) {
		this.openingBalReq = openingBalReq;
	}
	
	
}
