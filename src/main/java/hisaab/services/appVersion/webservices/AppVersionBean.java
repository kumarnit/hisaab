package hisaab.services.appVersion.webservices;

import hisaab.services.appVersion.modal.AppVersion;

import java.util.ArrayList;
import java.util.List;

public class AppVersionBean {
	private String msg= "";
	
	private int status;
	
	private List<AppVersion> appVersionList = new ArrayList<AppVersion>();

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

	public List<AppVersion> getAppVersionList() {
		return appVersionList;
	}

	public void setAppVersionList(List<AppVersion> appVersionList) {
		this.appVersionList = appVersionList;
	}
	
	
}
