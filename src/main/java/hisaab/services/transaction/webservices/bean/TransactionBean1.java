package hisaab.services.transaction.webservices.bean;

import hisaab.services.transaction.modal.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionBean1 {
	private int status;
	
	private String msg;
	
	private Transaction transactions;

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

	public Transaction getTransactions() {
		return transactions;
	}

	public void setTransactions(Transaction transactions) {
		this.transactions = transactions;
	}
	
	
}
