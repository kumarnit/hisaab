package hisaab.services.contacts.modal;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.annotations.Transient;




public class FriendContact {

	private long id;
	
	private String frndId;
	
	private String contactName;

	private String contactNo;
	
	private String transactionDocId;
	
	private int paymentStatus;
	
	private double amount;
	
	private long createdTime;
	
	private long updatedTime;

	@Transient
	private long localFrndId;
	
	private int frndStatus;
	
	private String reffId = "";
	
	private double openingBalAmt;
	
	private long openingBalDate;
	
	
	private List<String> blockedByList = new ArrayList<String>();
	
	
	
	public long getOpeningBalDate() {
		return openingBalDate;
	}

	public void setOpeningBalDate(long openingBalDate) {
		this.openingBalDate = openingBalDate;
	}

	public double getOpeningBalAmt() {
		return openingBalAmt;
	}

	public void setOpeningBalAmt(double openingBalAmt) {
		this.openingBalAmt = openingBalAmt;
	}

	public List<String> getBlockedByList() {
		return blockedByList;
	}

	public void setBlockedByList(List<String> blockedByList) {
		this.blockedByList = blockedByList;
	}

	public String getReffId() {
		return reffId;
	}

	public void setReffId(String reffId) {
		this.reffId = reffId;
	}

	public int getFrndStatus() {
		return frndStatus;
	}

	public void setFrndStatus(int frndStatus) {
		this.frndStatus = frndStatus;
	}

	public long getLocalFrndId() {
		return localFrndId;
	}

	public void setLocalFrndId(long localFrndId) {
		this.localFrndId = localFrndId;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFrndId() {
		return frndId;
	}

	public void setFrndId(String frndId) {
		this.frndId = frndId;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getTransactionDocId() {
		return transactionDocId;
	}

	public void setTransactionDocId(String transactionDocId) {
		this.transactionDocId = transactionDocId;
	}

	public int getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public long getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(long updatedTime) {
		this.updatedTime = updatedTime;
	}
	
}
