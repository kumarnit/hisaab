package hisaab.services.notification;

public class ServerMigrateNotification {
	
	private String newServerBaseUrl = "";
	
	private int notificationType ;

	public String getNewServerBaseUrl() {
		return newServerBaseUrl;
	}

	public void setNewServerBaseUrl(String newServerBaseUrl) {
		this.newServerBaseUrl = newServerBaseUrl;
	}

	public int getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(int notificationType) {
		this.notificationType = notificationType;
	}
	
	
}
