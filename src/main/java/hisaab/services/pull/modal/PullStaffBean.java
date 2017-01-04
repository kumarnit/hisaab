package hisaab.services.pull.modal;

import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.staff.modal.StaffProfile;
import hisaab.services.staff.modal.StaffUserRequest;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.request.modal.ModificationRequest;
import hisaab.services.user.modal.PrivateUser;
import hisaab.services.user.modal.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class PullStaffBean {

	private int status;
	
	private String msg;
	
	private List<Transaction> transactionList = new ArrayList<Transaction>();
	
	private List<UserProfile> userProfileList = new ArrayList<UserProfile>();
	
	private List<FriendContact> friendList = new ArrayList<FriendContact>();
	
	private List<String> userIds = new ArrayList<String>();
	
	private List<StaffUserRequest> staffRequests  = new ArrayList<StaffUserRequest>();
	
	private List<StaffUserRequest> staffRequestsForYou  = new ArrayList<StaffUserRequest>();
	
	private List<StaffProfile> staffProfiles = new ArrayList<StaffProfile>();
	
	private List<PrivateUser> privateuser = new ArrayList<PrivateUser>();
	
	private List<ModificationRequest> modificationRequest = new ArrayList<ModificationRequest>();
	
	private List<String> listOfDeletedStaffTransaction = new ArrayList<String>();
	
    private StaffProfile  myStaffProfile = new StaffProfile();
	
    private long pullTime ;
	
	
	
	public StaffProfile getMyStaffProfile() {
		return myStaffProfile;
	}

	public void setMyStaffProfile(StaffProfile myStaffProfile) {
		this.myStaffProfile = myStaffProfile;
	}

	public List<StaffUserRequest> getStaffRequestsForYou() {
		return staffRequestsForYou;
	}

	public void setStaffRequestsForYou(List<StaffUserRequest> staffRequestsForYou) {
		this.staffRequestsForYou = staffRequestsForYou;
	}

	public List<StaffProfile> getStaffProfiles() {
		return staffProfiles;
	}

	public void setStaffProfiles(List<StaffProfile> staffProfiles) {
		this.staffProfiles = staffProfiles;
	}

	public List<StaffUserRequest> getStaffRequests() {
		return staffRequests;
	}

	public void setStaffRequests(List<StaffUserRequest> staffRequests) {
		this.staffRequests = staffRequests;
	}

	public long getPullTime() {
		return pullTime;
	}

	public void setPullTime(long pullTime) {
		this.pullTime = pullTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<Transaction> getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(List<Transaction> transactionList) {
		this.transactionList = transactionList;
	}

	public List<UserProfile> getUserProfileList() {
		return userProfileList;
	}

	public void setUserProfileList(List<UserProfile> userProfileList) {
		this.userProfileList = userProfileList;
	}

	public List<FriendContact> getFriendList() {
		return friendList;
	}

	public void setFriendList(List<FriendContact> friendList) {
		this.friendList = friendList;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public List<PrivateUser> getPrivateuser() {
		return privateuser;
	}

	public void setPrivateuser(List<PrivateUser> privateuser) {
		this.privateuser = privateuser;
	}

	public List<ModificationRequest> getModificationRequest() {
		return modificationRequest;
	}

	public void setModificationRequest(List<ModificationRequest> modificationRequest) {
		this.modificationRequest = modificationRequest;
	}

	public List<String> getListOfDeletedStaffTransaction() {
		return listOfDeletedStaffTransaction;
	}

	public void setListOfDeletedStaffTransaction(
			List<String> listOfDeletedStaffTransaction) {
		this.listOfDeletedStaffTransaction = listOfDeletedStaffTransaction;
	}
	
	
	
}
