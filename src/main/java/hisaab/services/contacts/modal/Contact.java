package hisaab.services.contacts.modal;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.MongoOptions;

public class Contact {
	
	
	private long id  ;
	
	private String name = "";
	
	private String contactNo;
	
	private int sFlag;
	
	private String countryCode = "";
	
	


	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public int getsFlag() {
		return sFlag;
	}

	public void setsFlag(int sFlag) {
		this.sFlag = sFlag;
	}
	
	
}
