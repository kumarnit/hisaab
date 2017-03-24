package hisaab.services.transaction.webservices.bean;

import java.util.ArrayList;
import java.util.List;

import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.transaction.clear_transaction.modal.ClearTransactionRequest;
import hisaab.services.transaction.modal.TransactionDoc;

public class TransactionDocFriendBean {
	
	private int status;
	
	private String msg ="";
	
	private TransactionDoc transactionDoc = new TransactionDoc();
	
	private FriendContact friend = new FriendContact();
	
	private List<ClearTransactionRequest> clearTransRequestList = new ArrayList<ClearTransactionRequest>();
	
	
	

	public List<ClearTransactionRequest> getClearTransRequestList() {
		return clearTransRequestList;
	}

	public void setClearTransRequestList(
			List<ClearTransactionRequest> clearTransRequestList) {
		this.clearTransRequestList = clearTransRequestList;
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

	public TransactionDoc getTransactionDoc() {
		return transactionDoc;
	}

	public void setTransactionDoc(TransactionDoc transactionDoc) {
		this.transactionDoc = transactionDoc;
	}

	public FriendContact getFriend() {
		return friend;
	}

	public void setFriend(FriendContact friend) {
		this.friend = friend;
	}

	
}
