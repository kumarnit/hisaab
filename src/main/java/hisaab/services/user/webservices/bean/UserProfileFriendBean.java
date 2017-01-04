package hisaab.services.user.webservices.bean;

import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.user.modal.UserProfile;

public class UserProfileFriendBean {
	private UserProfile userprofile;
	
	private FriendContact friendcontact;
	
	public UserProfile getUserprofile() {
		return userprofile;
	}
	public void setUserprofile(UserProfile userprofile) {
		this.userprofile = userprofile;
	}
	public FriendContact getFriendcontact() {
		return friendcontact;
	}
	public void setFriendcontact(FriendContact friendcontact) {
		this.friendcontact = friendcontact;
	}

}
