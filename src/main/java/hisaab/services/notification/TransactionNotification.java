package hisaab.services.notification;

import hisaab.services.transaction.modal.Transaction;

public class TransactionNotification {
	
	private Transaction transaction ;
	
	private int pullFlag;
	
	private int notificationType;
	
	private String userId;
	
	private double amount ;
	
	private int paymentStatus;

	public int getPullFlag() {
		return pullFlag;
	}

	public void setPullFlag(int pullFlag) {
		this.pullFlag = pullFlag;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public int getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(int type) {
		this.notificationType = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
}
