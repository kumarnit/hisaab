package hisaab.services.transaction.clear_transaction.webservices.bean;

import hisaab.services.transaction.clear_transaction.modal.ClearTransactionRequest;

public class ClearTransactionBean {

	private String msg;
	
	private int status;
	
	private ClearTransactionRequest clearTransRequest;

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

	public ClearTransactionRequest getClearTransRequest() {
		return clearTransRequest;
	}

	public void setClearTransRequest(ClearTransactionRequest clearTransRequest) {
		this.clearTransRequest = clearTransRequest;
	}
	
	
}
