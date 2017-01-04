package hisaab.services.transaction.modal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="transaction_table")
public class TransactionSql {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", length=11)
	private long id;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name="transaction_id", length=40)
	private String transactionId ;
	
	@Column(name="from_", length=11)
	private String from1;
	
	@Column(name="to_", length=11)
	private String  to;
	
	@Column(name="amount_", length=20)
	private double amount;
	
	@Column(name="type_", length=2)
	private int type;
	
	@Column(name="comment_", length=100)
	private String comment;
	
	@Column(name="transaction_date", length=14)
	private long transactionDate;
	
	@Column(name="dispute_flag", length=11)
	private int disputeFlag;
	
	@Column(name="dispute_by", length=11)
	private String disputeBy;
	
	@Column(name="disputed_amount", length=11)
	private double disputeAmount;
	
	@Column(name="created_time", length=14)
	private long createdTime;
	
	@Column(name="updated_time", length=14)
	private long updatedTime;
	
	@Column(name="created_by", length=11)
	private String  createdBy;
	
	@Column(name="user_id", length=11)
	private String  userId;
	
	@Column(name="read_flag", length = 2)
	private int readFlag;
	
	@Column(name="transaction_status", length = 4)
	private int transactionStatus;
	
	@Column(name="action", length = 4)
	private int action;
	
	@Transient
	private int syncFlag;
	
	@Column(name="staffuser", length = 40)
    private String staffUser;
	
	@Column(name="ref_transactionid", length = 40)
	private String refTransacId = "0";
	
	@Column(name="received_time", length = 14)
	private long receivedTime;
	
	@Column(name="read_time", length = 14)
	private long readTime;
	
	@Column(name="mod_req_id", length = 14, columnDefinition="int default '0'")
	private long modReqId;
	
	
	@Column(name="read_status", length = 4)
	private int readStatus;
	
	@Column(name="transaction_doc_id", length = 50)
	private String transactionDocId;

	@Column(name="last_edited_by", length = 40)
	private String  lastEditedBy = "";
	
	
	
	public String getLastEditedBy() {
		return lastEditedBy;
	}

	public void setLastEditedBy(String lastEditedBy) {
		this.lastEditedBy = lastEditedBy;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(int transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public int getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(int readFlag) {
		this.readFlag = readFlag;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getFrom() {
		return from1;
	}

	public void setFrom(String from) {
		this.from1 = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public int getSyncFlag() {
		return syncFlag;
	}

	public void setSyncFlag(int syncFlag) {
		this.syncFlag = syncFlag;
	}

	public String getStaffUser() {
		return staffUser;
	}

	public void setStaffUser(String staffUser) {
		this.staffUser = staffUser;
	}

	public String getRefTransacId() {
		return refTransacId;
	}

	public void setRefTransacId(String refTransacId) {
		this.refTransacId = refTransacId;
	}

	public long getReceivedTime() {
		return receivedTime;
	}

	public void setReceivedTime(long receivedTime) {
		this.receivedTime = receivedTime;
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

	public String getTransactionDocId() {
		return transactionDocId;
	}

	public void setTransactionDocId(String transactionDocId) {
		this.transactionDocId = transactionDocId;
	}

	public long getModReqId() {
		return modReqId;
	}

	public void setModReqId(long modReqId) {
		this.modReqId = modReqId;
	}
	
	
}
