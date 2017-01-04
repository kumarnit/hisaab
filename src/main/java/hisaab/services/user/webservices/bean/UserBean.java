package hisaab.services.user.webservices.bean;

import java.util.ArrayList;
import java.util.List;

import hisaab.services.staff.modal.StaffUser;
import hisaab.services.staff.modal.StaffUserRequest;
import hisaab.services.user.modal.UserMaster;

public class UserBean {
	private int status;
	
	private String msg;
	
	private UserMaster user ;
	
	private StaffUser staffUser ;
	
	private List<StaffUserRequest> staffRequestsForYou = new ArrayList<StaffUserRequest>(); 

	
	
	
	public StaffUser getStaffUser() {
		return staffUser;
	}

	public void setStaffUser(StaffUser staff) {
		this.staffUser = staff;
	}

	public List<StaffUserRequest> getStaffRequestsForYou() {
		return staffRequestsForYou;
	}

	public void setStaffRequestsForYou(List<StaffUserRequest> staffRequestsForYou) {
		this.staffRequestsForYou = staffRequestsForYou;
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

	public UserMaster getUser() {
		return user;
	}

	public void setUser(UserMaster user) {
		this.user = user;
	}
	
	
	
	
}
