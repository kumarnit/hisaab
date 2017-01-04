package hisaab.services.notification.android.fcm;

public class Result {
 
	private String message_id = ""; 
	
	private String error ;

	public String getMessage_id() {
		return message_id;
	}

	public String getError() {
		return error;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	
	
	

}
