package hisaab.services.transaction.openingbalance.webservice.bean;

import hisaab.services.transaction.openingbalance.modal.OpeningBalRequest;

public class OpeningBalBean {

	private OpeningBalRequest openingBalRequest = new OpeningBalRequest();

	private int status;

	private String msg;

	public OpeningBalRequest getOpeningBalRequest() {
		return openingBalRequest;
	}

	public void setOpeningBalRequest(OpeningBalRequest openingBalRequest) {
		this.openingBalRequest = openingBalRequest;
	}

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
	
	
}
