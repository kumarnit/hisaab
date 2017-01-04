
package hisaab.services.contacts.services.bean;

import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.user.modal.PrivateUser;

public class PrivateUserBean {

	private PrivateUser privateUser ;
	
	private FriendContact friendContact ;
	
	private int status;
	
	private String msg = "";
    
	
	
	public FriendContact getFriendContact() {
		return friendContact;
	}

	public void setFriendContact(FriendContact friendContact) {
		this.friendContact = friendContact;
	}

	public PrivateUser getPrivateUser() {
		return privateUser;
	}

	public void setPrivateUser(PrivateUser privateUser) {
		this.privateUser = privateUser;
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
	
	
}
