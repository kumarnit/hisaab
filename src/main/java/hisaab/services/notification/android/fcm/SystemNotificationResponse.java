package hisaab.services.notification.android.fcm;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.gson.annotations.Expose;

public class SystemNotificationResponse {
	
	@JsonProperty("message_id")
	private long messageId;

	
	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}
	
	
}
