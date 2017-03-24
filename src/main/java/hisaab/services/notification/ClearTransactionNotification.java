package hisaab.services.notification;

import hisaab.services.transaction.clear_transaction.modal.ClearTransactionRequest;

public class ClearTransactionNotification {

	
	private int notificationType;
	
	private String userId;
	
	private ClearTransactionRequest clearTransactionRequest;

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

	public ClearTransactionRequest getClearTransactionRequest() {
		return clearTransactionRequest;
	}

	public void setClearTransactionRequest(
			ClearTransactionRequest clearTransactionRequest) {
		this.clearTransactionRequest = clearTransactionRequest;
	}
	
	
	
	
}
