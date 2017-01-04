package hisaab.services.staff.modal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;





@Entity
@Table(name="staff_profile", indexes ={
		@Index(name = "staff_index", columnList = "staff_id", unique = true)
	})
public class StaffProfile {

	@Id
	 @GeneratedValue(generator="gen")
	 @GenericGenerator(name="gen", strategy="foreign", parameters = @Parameter(name="property", value="user"))
	@Column(name="s_id", length=11)
	private long sId;

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

	@Column(name="staff_id", length = 14)
	private String staffId = "";
	
	@Column(name="owner_id", length = 14)
	private long ownerId;
	
	@Transient
	private String userIdString = "";
	
	@Column(name="user_type", length = 2, columnDefinition="int default '0'")
	private int userType;

	@OneToOne
    @PrimaryKeyJoinColumn
	    private StaffUser user;

	
	
	
	
	public long getOwnerId() {
		return ownerId;
	}


	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}


	public String getStaffId() {
		return staffId;
	}


	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}


	@JsonIgnore
	public StaffUser getUser() {
		return user;
	}

	
	public String getUserIdString() {
		return userIdString;
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


	public void setUser(StaffUser user) {
		this.user = user;
	}

	public long getsId() {
		return sId;
	}

	public void setsId(long userId) {
		this.sId = userId;
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
	
	
	public static boolean validateProfileUpdate(StaffProfile user){
		boolean flag = true;
		if(user.userName == null)
			user.userName = "";
		
		if(user.displayName == null)
			user.displayName = "";
		
		
		return flag;
	}
	
}
