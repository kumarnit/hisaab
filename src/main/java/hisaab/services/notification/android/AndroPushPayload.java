package hisaab.services.notification.android;

public class AndroPushPayload {

	private String title = "";
	
	private Object data ;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	
	
}
