package hisaab.services.contacts.modal;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
/**
 * This is an Object Mapping class for FriendLists Collection  in Mongodb
 * This are the Document with list of Associated User
 **/
@Entity("friend_list")
public class FriendList {
	
	@Id
	private String userId;
	
	private long createdTime;
	
	private long updatedTime;
	
	private long idCount;
	
	@Embedded
	public List<FriendContact> friends = new ArrayList<FriendContact>();

	
	
	
	public long getIdCount() {
		return idCount;
	}

	public void setIdCount(long idCount) {
		this.idCount = idCount;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public List<FriendContact> getFriends() {
		return friends;
	}

	public void setFriends(List<FriendContact> friends) {
		this.friends = friends;
	}
	
}
