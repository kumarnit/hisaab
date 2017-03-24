package hisaab.services.transaction.clear_transaction.modal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="clear_transaction_request")
public class ClearTransactionRequest {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="req_id", length= 11)
	private long reqId;
	
	@Column(name="requester_user_id", length = 11)
	private String requesterUserId = "0";
	
	@Column(name="for_user_id", length = 11)
	private String forUserId = "0";
	
	@Column(name = "till_date", length = 14)
	private long tillDate;
	
	@Column(name = "created_time", length = 14)
	private long createdTime;
	
	@Column(name="opening_bal_amt")
	private double openingBalAmt;
	
	@Column(name="opening_bal_date", length = 14)
	private long openingBalDate;
	
	@Column(name = "updated_time", length=14)
	private long updatedTime;
	
	@Column(name = "status", length = 2)
	private int status;

	public long getReqId() {
		return reqId;
	}

	public void setReqId(long reqId) {
		this.reqId = reqId;
	}

	public String getRequesterUserId() {
		return requesterUserId;
	}

	public void setRequesterUserId(String requesterUserId) {
		this.requesterUserId = requesterUserId;
	}

	public String getForUserId() {
		return forUserId;
	}

	public void setForUserId(String forUserId) {
		this.forUserId = forUserId;
	}

	public long getTillDate() {
		return tillDate;
	}

	public void setTillDate(long tillDate) {
		this.tillDate = tillDate;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	

	public double getOpeningBalAmt() {
		return openingBalAmt;
	}

	public void setOpeningBalAmt(double openingBalAmt) { 
		this.openingBalAmt = openingBalAmt;
	}

	public long getOpeningBalDate() {
		return openingBalDate;
	}

	public void setOpeningBalDate(long openingBalDate) {
		this.openingBalDate = openingBalDate;
	}
	
	
	
}
