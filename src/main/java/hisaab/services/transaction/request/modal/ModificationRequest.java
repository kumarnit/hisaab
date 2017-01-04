package hisaab.services.transaction.request.modal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "modification_request")
public class ModificationRequest {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", length = 11)
	private long id;
	
	@Column(name = "action", length=5)
	private int action;

	@Column(name = "status", length=5)
	private int status;

	@Column(name = "transaction_id" )
	private String transactionId;
	
	@Column(name = "created_by", length=11)
	private long createdBy;
	
	@Column(name = "for_user", length = 11)
	private String forUser;
	
	@Column(name = "created_time", length = 14)
	private long createdTime;

	@Column(name = "updated_time", length = 14)
	private long updatedTime;
	
	@Column(name = "edited_by", length = 14)
	private String editedBy;
	
	
	
	public long getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(long updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getEditedBy() {
		return editedBy;
	}

	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	

	public String getForUser() {
		return forUser;
	}

	public void setForUser(String forUser) {
		this.forUser = forUser;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	


	
}
