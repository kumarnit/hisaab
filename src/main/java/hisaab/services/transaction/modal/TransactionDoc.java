package hisaab.services.transaction.modal;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Transient;

@Entity("transaction_doc")
public class TransactionDoc {

	@Id
	private ObjectId _id;
	
	@Transient
	private String idString = "";
	
	private String user1;
	
	private String user2;
	
	private int paymentStatus;
	
	private double amount;
	
	private long createdTime;
	
	private long updatedTime;
	
	private long idCount;
	
	private int docType;
	
	private String blockedBy = "";
	
	private long openingBalDate;
	
	private double openingBalAmt;
	
	private String openingBalBy = "0";
	
	
	private List<Transaction> backedUptransactions = new ArrayList<Transaction>();
	
	private List<Transaction> modifiedTransactions = new ArrayList<Transaction>();
	
	
	
	public List<Transaction> getModifiedTransactions() {
		return modifiedTransactions;
	}

	public void setModifiedTransactions(List<Transaction> modifiedTransactions) {
		this.modifiedTransactions = modifiedTransactions;
	}

	public String getOpeningBalBy() {
		return openingBalBy;
	}

	public void setOpeningBalBy(String openingBalBy) {
		this.openingBalBy = openingBalBy;
	}

	public String getBlockedBy() {
		return blockedBy;
	}

	public void setBlockedBy(String blockedBy) {
		this.blockedBy = blockedBy;
	}

	public int getDocType() {
		return docType;
	}

	public void setDocType(int docType) {
		this.docType = docType;
	}

	
	public List<Transaction> getBackedUptransactions() {
		return backedUptransactions;
	}

	public void setBackedUptransactions(List<Transaction> backedUptransactions) {
		this.backedUptransactions = backedUptransactions;
	}

	public long getIdCount() {
		return idCount;
	}

	public void setIdCount(long idCount) {
		this.idCount = idCount;
	}

	@Embedded
	private List<Transaction> transactions = new ArrayList<Transaction>();

	
	@JsonIgnore
	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}
    
	public String getIdString() {
		if(_id != null)
		return _id.toString();
		else
		return idString;
	}

	public void setIdString(String idString) {
		
		this.idString = idString;
	}


	public String getUser1() {
		return user1;
	}

	public void setUser1(String user1) {
		this.user1 = user1;
	}

	public String getUser2() {
		return user2;
	}

	public void setUser2(String user2) {
		this.user2 = user2;
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

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

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
	
	
	
}
