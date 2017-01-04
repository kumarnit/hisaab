package hisaab.services.transaction.staff_transaction.services.bean;

import hisaab.services.transaction.modal.Transaction;

import java.util.ArrayList;
import java.util.List;

public class ApproveBean {
	private List<String> failed = new ArrayList<String>();
	
	private int status;
	
	private String msg = "";
	
	private List<Transaction> transactions = new ArrayList<Transaction>();

	

	public List<String> getFailed() {
		return failed;
	}

	public void setFailed(List<String> failed) {
		this.failed = failed;
	}

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
