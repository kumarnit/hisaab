package hisaab.services.device.modal;

import hisaab.services.contacts.modal.Contact;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("device_doc")
public class DeviceInfoDoc {
	
	@Id
	private String userId;
	
    private long createdTime;
	
	private long updatedTime;
	
	private long idCount;
	
	@Embedded
	private List<DeviceInfo> device = new ArrayList<DeviceInfo>();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public long getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(long updatedTime) {
		this.updatedTime = updatedTime;
	}

	public long getIdCount() {
		return idCount;
	}

	public void setIdCount(long idCount) {
		this.idCount = idCount;
	}

	public List<DeviceInfo> getDevice() {
		return device;
	}

	public void setDevice(List<DeviceInfo> device) {
		this.device = device;
	}
	
	

}
