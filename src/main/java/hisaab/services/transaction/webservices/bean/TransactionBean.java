package hisaab.services.transaction.webservices.bean;

import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;

import java.util.ArrayList;
import java.util.List;

public class TransactionBean {

	private int status;
	
	private String msg;
	
	private List<Transaction> transactions = new ArrayList<Transaction>();
	
	
	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
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
