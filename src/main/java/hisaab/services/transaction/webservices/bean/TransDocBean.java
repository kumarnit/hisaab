package hisaab.services.transaction.webservices.bean;

import java.util.ArrayList;
import java.util.List;

import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;

public class TransDocBean {

	
	private int status;
	
	private String msg;
	
	private TransactionDoc transDoc;
	
	private List<Transaction> rejectedTransaction = new ArrayList<Transaction>();
	
	

	public List<Transaction> getRejectedTransaction() {
		return rejectedTransaction;
	}

	public void setRejectedTransaction(List<Transaction> rejectedTransaction) {
		this.rejectedTransaction = rejectedTransaction;
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

	public TransactionDoc getTransDoc() {
		return transDoc;
	}

	public void setTransDoc(TransactionDoc transDoc) {
		this.transDoc = transDoc;
	}
	
}
