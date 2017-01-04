package hisaab.services.contacts.services.bean;

import hisaab.services.contacts.modal.FriendContact;

import java.util.ArrayList;
import java.util.List;

public class FriendListBean {
	
	private int status;
	
	private String msg;
	
	private List<FriendContact> friends = new ArrayList<FriendContact>();

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

	public List<FriendContact> getFriends() {
		return friends;
	}

	public void setFriends(List<FriendContact> friends) {
		this.friends = friends;
	}
	
	
}
