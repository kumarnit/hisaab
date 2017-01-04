package hisaab.services.contacts.services.bean;

import hisaab.services.contacts.modal.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactBean {
	private String msg="";
	
	private int Status;
	
	private List<Contact> contacts = new ArrayList<Contact>();

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	
	

}
