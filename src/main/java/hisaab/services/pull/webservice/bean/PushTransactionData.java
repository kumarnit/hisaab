package hisaab.services.pull.webservice.bean;

import hisaab.services.transaction.modal.Transaction;

import java.util.HashMap;
import java.util.List;

public class PushTransactionData {

	private int status;
	 
	private String msg;
	
	private HashMap<String, List<Transaction>> userTransaction = new HashMap<String, List<Transaction>>();

	
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

	public HashMap<String, List<Transaction>> getUserTransaction() {
		return userTransaction;
	}

	public void setUserTransaction(
			HashMap<String, List<Transaction>> userTransaction) {
		this.userTransaction = userTransaction;
	}
	
	
}
