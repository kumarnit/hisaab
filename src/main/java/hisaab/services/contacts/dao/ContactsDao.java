package hisaab.services.contacts.dao;




import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hisaab.config.morphia.MorphiaDatastoreTrasaction;
import hisaab.services.contacts.modal.Contact;
import hisaab.services.contacts.modal.ContactList;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.user.modal.UserMaster;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.TagSet;

public class ContactsDao {

	public static ContactList getContactsDocForUser(UserMaster user){
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(ContactList.class);
		ContactList contactList = null;
		Query<ContactList> query = datastore.createQuery(ContactList.class);
		query.field("_id").equal(""+user.getUserId());
		if(query.get() != null){
			ObjectMapper mapper = new ObjectMapper();
			contactList = query.get();
		}            
		else{
			contactList = new ContactList();
			contactList.setUserId(""+user.getUserId());
			contactList.setCreatedTime(System.currentTimeMillis());
			contactList.setUpdatedTime(System.currentTimeMillis());
			datastore.save(contactList);
		}
		return contactList;
	}
	
	public static boolean addContacts(ContactList contactList){
		boolean flag = false;
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		Query<ContactList> query = datastore.createQuery(ContactList.class);

		query.field("_id").equal(contactList.getUserId());
		UpdateOperations<ContactList> op = datastore.createUpdateOperations(ContactList.class);
		op.set("updatedTime", System.currentTimeMillis());
		op.set("idCount",contactList.getIdCount());
		op.addAll("contactList", contactList.getContactList(),false);
		UpdateResults ur = datastore.update(query,op );
		if(ur.getUpdatedCount()>0){
			flag = true;
		}

		return flag;
	}
	
	
	public static void getContactList(List<Contact> contacts ){
		
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(ContactList.class);
		Query<ContactList> query = datastore.createQuery(ContactList.class);
//		query.field("_id").equals(androidId);
		query.field("_id").equal(3);
		UpdateOperations<ContactList> op = datastore.createUpdateOperations(ContactList.class);
//		op.disableValidation();
		op.addAll("contactList", contacts,false);
		datastore.update(query,op );//(query, {"$set": new Contact()});
		
		if(query.get() != null){
			ObjectMapper mapper = new ObjectMapper();
			try {
				System.out.println(query.get().getContactList());
			} catch ( Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		
//		List<Contact> contacts = new ArrayList<Contact>();
//		Contact con = new Contact();
//		con.setContactNo("1232146");
//		con.setName("asdhak");
//		contacts.add(con);
//		
//		con = new Contact();
//		con.setContactNo("1156");
//		con.setName("ask");
//		contacts.add(con);
//		getContactList(contacts);
		getContactForUserId("+916532541251",4);
		getContactForWeb("+916532541251","4");
	}
	
	
	public static List<FriendContact> getFriendListbyUserId(String userId ){

		  List<FriendContact> frncon=new ArrayList<FriendContact>();
		     FriendList frnlst=new FriendList();
		  Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		  Query<FriendList> query = datastore.createQuery(FriendList.class);
		  
		  query.criteria("userId").equal(userId);
		  if(query.get() != null){
		   ObjectMapper mapper = new ObjectMapper();
		   frnlst = query.get();
		   try {
		    System.out.println("-------------------");
		        System.out.println(mapper.writeValueAsString(query.get().getFriends()));
		          } catch ( Exception e) {
		        e.printStackTrace();
		       }
		  frncon=query.get().getFriends();
		  
		 }
		  return frncon;
		}

	public static Contact getContactForWeb(String contactno, String  id){
		 Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(ContactList.class);
		 Contact contact = null;
		 DBObject match2 = new BasicDBObject("$match", new BasicDBObject("_id",id)); 
		 DBObject match = new BasicDBObject("$match", new BasicDBObject("contactList.contactNo", contactno));
		  DBObject gdb1 = new BasicDBObject();
		  gdb1.put("_id","$_id");
		  gdb1.put("contactList",new BasicDBObject("$push","$contactList"));
		  DBObject group = new BasicDBObject("$group", gdb1);
		  DBObject project = new BasicDBObject("$unwind", "$contactList");
		  try {
		  AggregationOutput output = datastore.getCollection(ContactList.class).aggregate(match2,project,match,group);
		  System.out.println("i m in");
		  
		   ObjectMapper mapper = new ObjectMapper();
//		   System.out.println("== : "+mapper.writeValueAsString(((BasicDBList)(BasicDBList)output.getCommandResult().get(0))));
		   System.out.println("== : "+mapper.writeValueAsString(output.getCommandResult()));
		   List objList =  (List) output.getCommandResult().get("result") ;
		   if(!objList.isEmpty()){
//			   System.out.println(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("friends")));
			   List<Contact> flist = (List<Contact>) mapper.readValue(mapper.writeValueAsString(((BasicDBObject)objList.get(0)).get("contactList")), List.class);
			   contact =  mapper.readValue(mapper.writeValueAsString(flist.get(0)), Contact.class);
//			   System.out.println("=>"+contact.getFrndStatus());
		   }
		   
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 
		 return contact;
		 
		 

	}

	public static Contact getContactForUserId(String contactNo, long uid) {
		
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(ContactList.class);
		ContactList contactList = null;
		Contact name= null;
		Query<ContactList> query = datastore.createQuery(ContactList.class);
		query.field("userId").equal(""+uid);
		if(query.get() != null){
			ObjectMapper mapper = new ObjectMapper();
			contactList = query.get();
		}            
		for(Contact contact : contactList.getContactList())
		{
			if(contact.getContactNo().equals(contactNo)){
				name = contact;
				System.out.println(contact.getName());
			}
		}
		// TODO Auto-generated method stub
		return name;
	}
	

}
