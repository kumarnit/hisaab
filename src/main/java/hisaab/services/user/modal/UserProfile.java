package hisaab.services.user.modal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;





@Entity
@Table(name="user_profile")
public class UserProfile {

	@Id
	 @GeneratedValue(generator="gen")
	 @GenericGenerator(name="gen", strategy="foreign", parameters = @Parameter(name="property", value="user"))
	@Column(name="user_id", length=11)
	private long userId;
	
	@Column(name="created_time", length=14)
	private long createdTime;
	
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

	@Column(name="last_sync_time", length=14, columnDefinition="bigint default '0'")
	private long lastSyncTime;
	
	@Column(name="last_activity", length=14, columnDefinition="bigint default '0'")
	private long lastActivity;
	
	
	
	@Transient
	private String userIdString = "";
	
	@Column(name="user_type", length = 2, columnDefinition="int default '0'")
	private int userType;

	@OneToOne
    @PrimaryKeyJoinColumn
	    private UserMaster user;
	
	@Column(name="transaction_count", length=20 , columnDefinition="int default '0'")
	private long transactionCount = 0;
	
	@JsonIgnore
	public UserMaster getUser() {
		return user;
	}

	
	public String getUserIdString() {
		return userIdString;
	}

	
	public long getTransactionCount() {
		return transactionCount;
	}


	public void setTransactionCount(long transactionCount) {
		this.transactionCount = transactionCount;
	}


	public int getUserType() {
		return userType;
	}


	public void setUserType(int userType) {
		this.userType = userType;
	}


	public void setUserIdString(String userIdString) {
		this.userIdString = userIdString;
	}


	public void setUser(UserMaster user) {
		this.user = user;
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

	public void setUserName(String fName) {
		this.userName = fName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String lName) {
		this.displayName = lName;
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
	
	
	
	
	public long getLastSyncTime() {
		return lastSyncTime;
	}


	public void setLastSyncTime(long lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}


	public long getLastActivity() {
		return lastActivity;
	}


	public void setLastActivity(long lastActivity) {
		this.lastActivity = lastActivity;
	}


	public static boolean validateProfileUpdate(UserProfile user){
		boolean flag = true;
		if(user.userName == null)
			user.userName = "";
		
		if(user.displayName == null)
			user.displayName = "";
		
		
		return flag;
	}
	
}
