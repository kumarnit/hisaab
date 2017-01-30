package hisaab.services.transaction.request.webservices;

import hisaab.services.transaction.request.modal.ModificationRequest;


public class ModificaltionBean {

	private int status;
	
	private String msg = "";
	
	private ModificationRequest modRequest = new ModificationRequest();

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

	public ModificationRequest getModRequest() {
		return modRequest;
	}

	public void setModRequest(ModificationRequest modRequest) {
		this.modRequest = modRequest;
	}
	
	
}
