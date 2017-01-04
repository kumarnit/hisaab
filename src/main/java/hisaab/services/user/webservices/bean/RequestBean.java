package hisaab.services.user.webservices.bean;

import hisaab.services.user.modal.UserRequest;

public class RequestBean {

	
	private int status ;
	
	private String msg = "";
	
	private UserRequest request = new UserRequest();

	
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

	public UserRequest getRequest() {
		return request;
	}

	public void setRequest(UserRequest request) {
		this.request = request;
	}
	
	
}
