package hisaab.services.transaction.staff_transaction.modal;

import hisaab.services.transaction.modal.Transaction;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Transient;

@Entity("staff_transaction_doc")
public class StaffTransactionDoc {


	@Id
	private ObjectId _id;
	
	@Transient
	private String idString;
	
	private String staffId;
	
	private String ownerId;
	
	private long createdTime;
	
	private long updatedTime;
	
	private long idCount;
	
	
	private List<Transaction> backedUptransactions = new ArrayList<Transaction>();
	
	@Embedded
	private List<Transaction> transactions = new ArrayList<Transaction>();

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getIdString() {
		return idString;
	}

	public void setIdString(String idString) {
		this.idString = idString;
	}

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffUserId) {
		this.staffId = staffUserId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
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

	public long getIdCount() {
		return idCount;
	}

	public void setIdCount(long idCount) {
		this.idCount = idCount;
	}

	public List<Transaction> getBackedUptransactions() {
		return backedUptransactions;
	}

	public void setBackedUptransactions(List<Transaction> backedUptransactions) {
		this.backedUptransactions = backedUptransactions;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

}
