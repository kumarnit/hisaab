package hisaab.services.user.modal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="private_user")
public class PrivateUser {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", length=11)
	private long id;
	
	@Column(name="private_user_id", length=20)
	private String privateUserId;
	
	@Column(name="user_id",length=11)
	private long userId;
	
	@Column(name="created_time", length=14)
	private long createdTime;
	
	@Column(name="owner_id", length=14)
	private long ownerId;
	
	@Column(name="updated_time", length=14)
	private long updatedTime;
	
	@Column(name="user_name", length=50)
	private String userName;
	
	
	@Column(name="display_name", length=50)
	private String displayName;
	
	@Column(name="org_name", length=50)
	private String orgName;
	
	@Column(name="address", length=100)
	private String address;
	
	@Column(name="image_key", length=50)
	private String imageKey;
	
	@Column(name="pub_status", length=100)
	private String pubStatus;
	
	@Column(name="contact_no", length=20)
	private String contactNo;

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getImageKey() {
		return imageKey;
	}

	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}

	public String getPubStatus() {
		return pubStatus;
	}

	public void setPubStatus(String pubStatus) {
		this.pubStatus = pubStatus;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	

	public String getPrivateUserId() {
		return privateUserId;
	}

	public void setPrivateUserId(String privateUserId) {
		this.privateUserId = privateUserId;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	

}
