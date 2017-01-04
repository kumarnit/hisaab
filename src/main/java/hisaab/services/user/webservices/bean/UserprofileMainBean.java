package hisaab.services.user.webservices.bean;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserprofileMainBean {
	List<String> list = new ArrayList<String>();
	
    HashMap<String,UserProfileFriendBean> list2 =new HashMap<String,UserProfileFriendBean>();
    
    private int status;
    
    private String msg;
    
    
    
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
	public List<String> getList() {
		return list;
	}
	public void setList(List<String> list) {
		this.list = list;
	}
	public HashMap<String, UserProfileFriendBean> getList2() {
		return list2;
	}
	public void setList2(HashMap<String, UserProfileFriendBean> list2) {
		this.list2 = list2;
	}
    
}
