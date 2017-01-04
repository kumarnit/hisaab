package hisaab.services.staff.modal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;



@Entity
@Table(name="staff_user", indexes = {
    	@Index(name = "auth_token_index", columnList = "auth_token", unique = true),
    	@Index(name = "contact_index", columnList = "contact_no", unique = true),
    	@Index(name = "staff_index", columnList = "staff_id", unique = true)
    })
public class StaffUser {
	
	 @Id
	 @GeneratedValue(strategy=GenerationType.IDENTITY)
	 @Column(name="s_id", length=11)
	 private long sId;
	
	 @Column(name="contact_no",length=15)
	 private String contactNo;
	
	 @Column(name="device_type", length=2)
   	 private int deviceType;
	
	 @Column(name="push_id")
	 private String pushId;
	
	 @Column(name="verify_flag", length=2)
     private int vrifyFlag;
	
	 @Column(name="pass", length = 100)
	 private String pass;
	
	 @Column(name="auth_token", length=100)
	 private String authToken;
	
	 @Column(name="on_boarding_flag", length=2)
	 private int onBoardingFlag;
	
	 @Column(name="del_flag", length=2)
	 private int delFlag;
	
	 @Column(name="org_id", length=11)
	 private long orgId;
	
	 @Column(name="country_code", length=100)
	 private String countrCode;
	
	 @Column(name="staff_id", length=14)
	 private String staffId = "";
	 
	 
	 @Column(name="locale", length=50)
	 private String locale;

	 @Column(name="created_time", length = 14)
	 private long createdTime;
	
	 @Column(name="updated_time", length = 14)
	 private long updatedTime;
	
	@Column(name="user_type", length = 2, columnDefinition="int default '0'")
	private int userType;
	
	@Column(name="owner_id", length = 11, columnDefinition="int default '0'")
	private long ownerId;
	
	
	 @OneToOne(cascade = CascadeType.PERSIST)
	 @JoinColumn(name="s_id")
	 private StaffProfile staffProfile = new StaffProfile();
	 
	 
	
	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public StaffProfile getStaffProfile() {
		return staffProfile;
	}

	public void setStaffProfile(StaffProfile userProfile) {
		this.staffProfile = userProfile;
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

	public long getsId() {
		return sId;
	}

	public void setsId(long userId) {
		this.sId = userId;
	}
	

	

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	
	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public String getPushId() {
		return pushId;
	}

	public void setPushId(String pushId) {
		this.pushId = pushId;
	}

	public int getVrifyFlag() {
		return vrifyFlag;
	}

	public void setVrifyFlag(int vrifyFlag) {
		this.vrifyFlag = vrifyFlag;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public int getOnBoardingFlag() {
		return onBoardingFlag;
	}

	public void setOnBoardingFlag(int onBoardingFlag) {
		this.onBoardingFlag = onBoardingFlag;
	}

	public int getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(int delFlag) {
		this.delFlag = delFlag;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public String getCountrCode() {
		return countrCode;
	}

	public void setCountrCode(String countrCode) {
		this.countrCode = countrCode;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	
}
