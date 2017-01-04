package hisaab.services.user.webservices.bean;

import java.util.HashMap;

public class ContactUserprofileBean {
	private String msg="";
	
	private int status;
	
	private HashMap<String,UserProfileFriendBean> list =new HashMap<String,UserProfileFriendBean>();

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

	public HashMap<String, UserProfileFriendBean> getList() {
		return list;
	}

	public void setList(HashMap<String, UserProfileFriendBean> list) {
		this.list = list;
	}
	

}
