package hisaab.services.sms.modal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "promotional_sms")
public class PromotionalSms {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", length = 14)
	private long id;
	
	@Column(name="contact_no", length=14)
	private String contact_no;
	
	@Column(name="epoch_time", length = 14)
	private long epochTime;
	
	@Column(name="msg_id", length = 50)
	private String msgId;
	
	@Column(name="status", length=2)
	private int status;

	@Column(name="user_id", length=11)
	private long userId;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContact_no() {
		return contact_no;
	}

	public void setContact_no(String contact_no) {
		this.contact_no = contact_no;
	}

	public long getEpochTime() {
		return epochTime;
	}

	public void setEpochTime(long epochTime) {
		this.epochTime = epochTime;
	}

	public String getMsgId() {
		return msgId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
