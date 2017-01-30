package hisaab.services.staff.modal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="staff_user_request")
public class StaffUserRequest {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id" ,length=11)
	private long id;
	
	@Column(name="staff_user_id" ,length=15)
	private String staffUserId;
	
	@Column(name="owner_id" ,length=11)
	private long ownerId;
	
	@Column(name="created_time", length = 14)
	private long createdTime;
	
	@Column(name="updated_time", length = 14)
	private long updatedTime;

	@Column(name="status", length = 2)
	private int Status;
	
	@Column(name = "country_code", length = 10)
	private String countryCode = "";
	
	@Column(name="contact_no", length=20)
	private String contactNo;
	
	@Column(name="security_code", length=10)
	private String securityCode;
	
	@Column(name="display_name", length=50)
	private String displayName;

	@Column(name="owner_name", length=100)
	private String ownerName;
	
	@Column(name="owner_contactNo", length=10)
	private String ownerContactNo;
	
	
	
	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerContactNo() {
		return ownerContactNo;
	}

	public void setOwnerContactNo(String ownerContactNo) {
		this.ownerContactNo = ownerContactNo;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStaffUserId() {
		return staffUserId;
	}

	public void setStaffUserId(String staffUserId) {
		this.staffUserId = staffUserId;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
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

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}
	
}
