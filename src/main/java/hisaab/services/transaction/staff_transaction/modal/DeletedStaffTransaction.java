package hisaab.services.transaction.staff_transaction.modal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "del_staff_trans")
public class DeletedStaffTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", length = 14)
	private long id;
	
	@Column(name = "satff_id", length = 20)
	private String staffId;
	
	@Column(name = "owner_id", length = 14)
	private String ownerId;
	
	@Column(name = "created_time", length = 14)
	private long createdTime;
	
	@Column(name = "transaction_id", length = 50)
	private String transactionId;
	
	@Column(name = "deleted_by", length = 14)
	private String deletedBy;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	
	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionIds) {
		this.transactionId = transactionIds;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(String deletedBy) {
		this.deletedBy = deletedBy;
	}
	
	
	
	
	
}
