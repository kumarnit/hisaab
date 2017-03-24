package hisaab.services.notification;

public class AutoDeleteNotification {

	private String message = "";
	
	private int notificationType ;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(int notificationType) {
		this.notificationType = notificationType;
	}
	
	
}
