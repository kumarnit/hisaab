package hisaab.services.contacts.services.bean;

import hisaab.services.contacts.modal.Contact;
import hisaab.services.contacts.modal.FriendContact;

import java.util.ArrayList;
import java.util.List;

public class ContactListBean {
	
	private int status;
	
	private String msg;
	
	private List<Contact> contactList = new ArrayList<Contact>();

	private List<FriendContact> friends = new ArrayList<FriendContact>();
	
	private List<String> friendIds =new ArrayList<String>();
	
	public List<String> getFriendIds() {
		return friendIds;
	}

	public void setFriendIds(List<String> friendId) {
		this.friendIds = friendId;
	}

	public List<FriendContact> getFriends() {
		return friends;
	}

	public void setFriends(List<FriendContact> friends) {
		this.friends = friends;
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

	public List<Contact> getContactList() {
		return contactList;
	}

	public void setContactList(List<Contact> contactList) {
		this.contactList = contactList;
	}
	
	
}
