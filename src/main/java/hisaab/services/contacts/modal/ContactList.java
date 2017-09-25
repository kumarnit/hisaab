package hisaab.services.contacts.modal;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * This is an Object Mapping class for ContactLists Collection  in Mongodb
 **/
@Entity("contact_lists")
public class ContactList {
	
	@Id
	private String userId;
	
	private long createdTime;
	
	private long updatedTime;
	
	private long idCount;
	
	
	
	@Embedded
	private List<Contact> contactList = new ArrayList<Contact>();

	
	
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getIdCount() {
		return idCount;
	}

	public void setIdCount(long idCount) {
		this.idCount = idCount;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public long getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(long updatedTime) {
		this.updatedTime = updatedTime;
	}

	public List<Contact> getContactList() {
		return contactList;
	}

	public void setContactList(List<Contact> contactList) {
		this.contactList = contactList;
	}
	
}
