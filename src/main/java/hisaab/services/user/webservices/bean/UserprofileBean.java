package hisaab.services.user.webservices.bean;

import hisaab.services.user.modal.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class UserprofileBean {
	private List<String> userlist = new ArrayList<String>();
	
	private List<UserProfile> userprofilelist = new ArrayList<UserProfile>();
	
	public List<String> getUserlist() {
		return userlist;
	}
	public void setUserlist(List<String> userlist) {
		this.userlist = userlist;
	}
	public List<UserProfile> getUserprofilelist() {
		return userprofilelist;
	}
	public void setUserprofilelist(List<UserProfile> userprofilelist) {
		this.userprofilelist = userprofilelist;
	}
	

}
