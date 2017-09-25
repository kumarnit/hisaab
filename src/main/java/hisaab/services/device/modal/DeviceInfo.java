package hisaab.services.device.modal;

/**
 * Actual Properties that are stored in Documents of DeviceInfo Collection.
 **/
public class DeviceInfo {
	
     private long id;
     
     private String deviceId;
     
     private String osVersion;
     
     private String device;
     
     private String osApiLevel;
     
     private String release;
     
     private String brand;
     
     private String display;
     
     private String maufacturer;
     
     private long createdTime;
 	
 	 private long updatedTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getOsApiLevel() {
		return osApiLevel;
	}

	public void setOsApiLevel(String osApiLevel) {
		this.osApiLevel = osApiLevel;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getMaufacturer() {
		return maufacturer;
	}

	public void setMaufacturer(String maufacturer) {
		this.maufacturer = maufacturer;
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
 	 
 	 
}
