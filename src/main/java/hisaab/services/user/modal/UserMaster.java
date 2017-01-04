package hisaab.services.user.modal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.Transient;



import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;





import com.google.gson.Gson;




@Entity
@Table(name="user_master", indexes = {
        	@Index(name = "hash1", columnList = "auth_token", unique = true),
        	@Index(name = "contact_index", columnList = "contact_no", unique = true)
        })

public class UserMaster {
	
	 @Id
	 @GeneratedValue(strategy=GenerationType.IDENTITY)
	 @Column(name="user_id", length=11)
	 private long userId;
	
	 @Column(name="contact_no",length=15)
	 private String contactNo;
	
	 @Column(name = "email", length=100)
	 private String email;
	
	 @Column(name="device_type", length=2)
   	 private int deviceType;

	
	 @Column(name="push_id")
	 private String pushId;
	
	 @Column(name="verify_flag", length=2)
     private int vrifyFlag;
	
	 @Column(name="pass", length = 100)
	 private String pass;
	
	 
	 @Column(name="auth_token", length=100,nullable= false)
	 private String authToken;
	
	 @Column(name="on_boarding_flag", length=2)
	 private int onBoardingFlag;
	
	 @Column(name="del_flag", length=2)
	 private int delFlag;
	
	 @Column(name="org_id", length=11)
	 private long orgId;
	
	 @Column(name="country_code", length=100)
	 private String countrCode;
	
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
	
	@Column(name = "sms_count", length = 4)
	private int smsCount;
	
	@Column(name = "date_of_msg")
	private String dateOfMsg = "[]";
	
	@Column(name="app_version", length = 4)
    private String appVersion;
	
	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getDateOfMsg() {
		return dateOfMsg;
	}

	public void setDateOfMsg(String dateOfMsg) {
		this.dateOfMsg = dateOfMsg;
	}

	public String getMsgByuser() {
		return msgByuser;
	}

	public void setMsgByuser(String msgByuser) {
		this.msgByuser = msgByuser;
	}
	@Column(name = "msg_by_user")
	private String msgByuser = "[]";
	
	@Transient 
	private List<Long> valueMsgBy;
	
	@Transient 
	private String valueDateOfMsg;
	
	@JsonIgnore
	public List<Long> getValueDateOfMsg() {
		ObjectMapper mapper = new ObjectMapper();
		Gson j = new Gson();
		List<Long> valu = new ArrayList<Long>();
		try {
			valu = (List<Long>)mapper.readValue(getDateOfMsg(),List.class);
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return valu;
	}

	public String setValueDateOfMsg() {
		ObjectMapper mapper = new ObjectMapper();
		Gson j = new Gson();
		List<Long> valu = new ArrayList<Long>();
		Long epoch = System.currentTimeMillis();
		try {
			valu = mapper.readValue(getDateOfMsg(),List.class) ;
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		valu.add(epoch);
		return valu.toString();
	}
	@JsonIgnore
	public List<Long> getValueMsgBy() {
		ObjectMapper mapper = new ObjectMapper();
		Gson j = new Gson();
		List<Long> valu = new ArrayList<Long>();
		try {
			valu = (List<Long>)mapper.readValue(getMsgByuser(), TypeFactory.collectionType(List.class, Long.class));
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return valu;
	}

	public String setValueMsgBy(Long value) {
		ObjectMapper mapper = new ObjectMapper();
		Gson j = new Gson();
		List<Long> valu = new ArrayList<Long>();
		try {
			valu = mapper.readValue(getMsgByuser(),TypeFactory.collectionType(List.class, Long.class)) ;
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		valu.add(value);
		return valu.toString();
	}


	@OneToOne(cascade = CascadeType.PERSIST)
	 @JoinColumn(name="user_id")
	 private UserProfile userProfile = new UserProfile();
	 
	 public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public int getSmsCount() {
		return smsCount;
	}

	public void setSmsCount(int smsCount) {
		this.smsCount = smsCount;
	}
	
	public static void main(String arg[])
	{
		
	}
}
