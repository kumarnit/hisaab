package hisaab.services.transaction.webservices.bean;

import hisaab.services.transaction.modal.Transaction;

public class TransactionDisputeBean {

	private String transactionId = "";
	
	private double disputeAmount;
	
	private String transactionDocId = "";
	
	private int status ;
	
	private String msg = "";

	

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public double getDisputeAmount() {
		return disputeAmount;
	}

	public void setDisputeAmount(double disputeAmt) {
		this.disputeAmount = disputeAmt;
	}

	public String getTransactionDocId() {
		return transactionDocId;
	}

	public void setTransactionDocId(String transactionDocId) {
		this.transactionDocId = transactionDocId;
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
