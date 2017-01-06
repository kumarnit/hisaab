package hisaab.services.device.webservices.bean;

import hisaab.services.device.modal.DeviceInfo;

public class DeviceInfoBean {
	
	private DeviceInfo deviceInfo = new DeviceInfo();
	
	private String msg = "";
	
	private int status;

	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	
}
