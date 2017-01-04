package hisaab.services.transaction.openingbalance.modal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="opening_bal_request")
public class OpeningBalRequest {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="id", length= 11)
	private long id;
	
	@Column(name="requester_user_id", length = 11)
	private String requesterUserId = "0";
	
	@Column(name="for_user_id", length = 11)
	private String forUserId = "0";
	
	@Column(name="opening_bal_amt")
	private double openingBalAmt;
	
	@Column(name="payment_status", length = 2)
	private int paymentStatus;
	
	@Column(name = "opening_bal_date", length = 14)
	private long openingBalDate;
	
	@Column(name = "created_time", length = 14)
	private long createdTime;
	
	@Column(name = "updated_time", length=14)
	private long updatedTime;
	
	@Column(name = "status", length = 2)
	private int status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public double getOpeningBalAmt() {
		return openingBalAmt;
	}

	public void setOpeningBalAmt(double openingBalAmt) {
		this.openingBalAmt = openingBalAmt;
	}

	public int getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public long getOpeningBalDate() {
		return openingBalDate;
	}

	public void setOpeningBalDate(long openingBalDate) {
		this.openingBalDate = openingBalDate;
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
	
	
	
}
