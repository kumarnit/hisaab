package hisaab.services.staff.webservice.bean;

import hisaab.services.staff.modal.StaffUser;

public class StaffUserBean {

	private String msg;
	
	private int status;
	
	private StaffUser staffUser = new StaffUser();

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public StaffUser getStaffUser() {
		return staffUser;
	}

	public void setStaffUser(StaffUser staffUser) {
		this.staffUser = staffUser;
	}
	
}
