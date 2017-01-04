package hisaab.services.notification.webservice.bean;

import hisaab.services.notification.SystemUpdateNotification;

public class SystemUpdateBean {
	
	private int status;
	
	private String msg;
	
	private SystemUpdateNotification update = new SystemUpdateNotification() ;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public SystemUpdateNotification getUpdate() {
		return update;
	}

	public void setUpdate(SystemUpdateNotification update) {
		this.update = update;
	}
	
	
}
