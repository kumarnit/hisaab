package hisaab.services.transaction.modal;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Transient;

public class Transaction {
	
	
	private String transactionId ;
	
	private String from;
	
	private String to;
	
	private double amount;
	
	private int type;
	
	private String comment;
	
	private int readFlag;
	
	private long transactionDate;
	
	private int disputeFlag;
	
	private int action;
	
	private String disputeBy;
	
	private double disputeAmount;
	
	private long createdTime;
	
	private long updatedTime;

	private String  createdBy;
	
	private String  lastEditedBy = "";
	
	@Transient
	private long localTransactionId;
	
	private long srNo;
	
	private String transactionDocId;
	
	private int transactionStatus;
	
	private String staffUser;
	
	private String refTransacId = "0";
	
	private long receivedTime;
	
	private long readTime;
	
	private int readStatus;
	
	private long modReqId;
	
	
	
	
	public String getLastEditedBy() {
		return lastEditedBy;
	}

	public void setLastEditedBy(String lastEditedBy) {
		this.lastEditedBy = lastEditedBy;
	}

	public long getModReqId() {
		return modReqId;
	}

	public void setModReqId(long modReqId) {
		this.modReqId = modReqId;
	}

	public String getRefTransacId() {
		return refTransacId;
	}

	public void setRefTransacId(String refTransacId) {
		this.refTransacId = refTransacId;
	}

	@Transient
	private int syncFlag;
	
	
	
	
	public long getReceivedTime() {
		return receivedTime;
	}

	public void setReceivedTime(long receiveTime) {
		this.receivedTime = receiveTime;
	}

	public long getReadTime() {
		return readTime;
	}

	public void setReadTime(long readTime) {
		this.readTime = readTime;
	}

	public int getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(int readStatus) {
		this.readStatus = readStatus;
	}

	public int getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(int readFlag) {
		this.readFlag = readFlag;
	}

	public String getStaffUser() {
		return staffUser;
	}

	public void setStaffUser(String staffUser) {
		this.staffUser = staffUser;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public long getSrNo() {
		return srNo;
	}

	public void setSrNo(long srNo) {
		this.srNo = srNo;
	}

	public String getTransactionDocId() {
		return transactionDocId;
	}

	public void setTransactionDocId(String tranactionDocId) {
		this.transactionDocId = tranactionDocId;
	}

	public int getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(int transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public long getLocalTransactionId() {
		return localTransactionId;
	}

	public void setLocalTransactionId(long localTransactionId) {
		this.localTransactionId = localTransactionId;
	}

	public int getSyncFlag() {
		return syncFlag;
	}

	public void setSyncFlag(int syncFlag) {
		this.syncFlag = syncFlag;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	
	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(long transactionDate) {
		this.transactionDate = transactionDate;
	}

	public int getDisputeFlag() {
		return disputeFlag;
	}

	public void setDisputeFlag(int disputeFlag) {
		this.disputeFlag = disputeFlag;
	}


	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getDisputeBy() {
		return disputeBy;
	}

	public void setDisputeBy(String disputeBy) {
		this.disputeBy = disputeBy;
	}

	public double getDisputeAmount() {
		return disputeAmount;
	}

	public void setDisputeAmount(double disputeAmount) {
		this.disputeAmount = disputeAmount;
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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	
	
}
