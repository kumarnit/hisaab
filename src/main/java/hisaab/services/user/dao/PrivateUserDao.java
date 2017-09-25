package hisaab.services.user.dao;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.config.morphia.MorphiaDatastoreTrasaction;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.Contact;
import hisaab.services.contacts.modal.ContactList;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.staff.modal.StaffProfile;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.user.modal.PrivateUser;
import hisaab.services.user.modal.UserMaster;
import hisaab.services.user.modal.UserProfile;
import hisaab.util.Constants;
import hisaab.util.Helper;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.hql.internal.ast.tree.DeleteStatement;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.MorphiaIterator;
import org.mongodb.morphia.query.QueryResults;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import com.mongodb.WriteResult;


public class PrivateUserDao {

	/**
	 * add private-user By owner.
	 *  
	 **/
		public static PrivateUser addPrivateuser(UserMaster user1,Contact contact){
		Session session = null;
		Transaction tx = null;
		PrivateUser user = null;
		long epoch = System.currentTimeMillis();
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			/*	String hql = "from PrivateUser where ownerId =:ownerId";
			
			if(!contact.getContactNo().equals("")){
				hql +=" and contactNo =:contactNo"; 
			}
			if(!contact.getName().equals("")){
				hql +=" and displayName =:name"; 
			}
			Query query = session.createQuery(hql);
			if(!contact.getContactNo().equals(""))
				query.setParameter("contactNo", contact.getContactNo());
			query.setParameter("ownerId", user1.getUserId());
			if(!contact.getName().equals(""))
				query.setParameter("name", contact.getName());
			if(query.list().size()>0){
//				user = (PrivateUser) query.list().get(0);
			}
			else{*/
		
				user = new PrivateUser();
				
				user.setCreatedTime(epoch);
				user.setUpdatedTime(epoch);
				user.setDisplayName(contact.getName());
				
				 /*String conta = Helper.validatePhoneNo(contact.getContactNo());
				    if(conta != null){
						user.setContactNo(conta);
				    }else{
						user.setContactNo(contact.getContactNo());
				    }*/
				user.setContactNo(contact.getContactNo());
				
				user.setOwnerId(user1.getUserId());
				tx = session.beginTransaction();
				session.save(user);
				user.setPrivateUserId("PR-"+user.getId());

				tx.commit();
				//adding to friend contact 
//			   } 
			}catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return user;
	}
		
		
		/**
		 * Create a private user for a Blocked contact Of Owner
		 *  
		 **/
	public static boolean addPrivateUserForBlocked(long userId, PrivateUser privateUser){
			Session session = null;
			Transaction tx = null;
			long epoch = System.currentTimeMillis();
			boolean flag = false;
			try {
					session = HibernateUtil.getSessionFactory().openSession();
					privateUser.setCreatedTime(epoch);
					privateUser.setOwnerId(userId);
					tx = session.beginTransaction();
					session.save(privateUser);
					privateUser.setPrivateUserId("PR-"+privateUser.getId());
					tx.commit();
					flag = true; 
				
			} catch (Exception e) {
				System.out.println("Exception = " + e.getMessage());
				if (tx != null)
					tx.rollback();
				e.printStackTrace();
				
			} finally {
				session.close();
			}
			return flag;
		}

		
	/*	public static List<UserProfile> getUnmangedUserProfiles(List<String> userlist) {
			Session session = null;
			Transaction tx = null;
			
			List<UserProfile> userprofilelist = new ArrayList<UserProfile>();
					
			try {
				session = HibernateUtil.getSessionFactory().openSession();
				String hql = "from Owner where unmanageUserId IN (:userlist)";
				Query query = session.createQuery(hql);
				query.setParameterList("userlist", userlist);
				
				for(Owner owner :(List<Owner>) query.list()){
					UserProfile  user = new UserProfile();
					user.setUserIdString(owner.getPrivateUserId());
					user.setDisplayName(owner.getDisplayName());
					user.setContactNo(owner.getContactNo());
					user.setCreatedTime(owner.getCreatedTime());
					user.setUpdatedTime(owner.getUpdatedTime());
					userprofilelist.add(user);
				}
			} catch (Exception e) {
				System.out.println("Exception = " + e.getMessage());
				e.printStackTrace();
				
			} finally {
				session.close();
			}
			return userprofilelist;
			// TODO Auto-generated method stub
			
		}
*/

	/**
	 * Add Associate User entry for a private user
	 **/
		public static FriendList addFriendContactofPrivateUser(UserMaster user,Contact contact, PrivateUser unmanUser) {
			FriendList frndlist = null ;
			long epoch= System.currentTimeMillis();
			FriendContact frncon = new FriendContact();
		    frncon.setContactName(contact.getName());
		    String conta = Helper.validatePhoneNo(contact.getContactNo());
		    if(conta != null)
		    	frncon.setContactNo(conta);
		    else
		    	frncon.setContactNo(contact.getContactNo());
		    frncon.setFrndStatus(Constants.PRIVATE_USER);
		    frncon.setCreatedTime(System.currentTimeMillis());
		    frncon.setUpdatedTime(System.currentTimeMillis());
		    frncon.setFrndId(unmanUser.getPrivateUserId());
		    
		    frndlist = FriendsDao.getAssociatedUserDoc(user);
			long count = frndlist.getIdCount();
			frncon.setId(++count);
			frndlist.setIdCount(count);
			
			frndlist.setUpdatedTime(epoch);
			frndlist.setUserId(""+user.getUserId());
			frndlist.setFriends(Arrays.asList(frncon));
			FriendsDao.addFriends(frndlist);
			return frndlist;
		}


		/**
		 * get List of all private User created by Requester.
		 *  
		 **/
		public static List<PrivateUser> getPrivateUser(UserMaster user,
				long pullTime) {
			
			Session session = null;
			Transaction tx = null;
			String str = "";
			long epoch = System.currentTimeMillis();
			List<PrivateUser> staffUser = new ArrayList<PrivateUser>();
			try {
				session = HibernateUtil.getSessionFactory().openSession();
				String hql = "FROM PrivateUser WHERE ownerId = :owner ";
				if(pullTime > 0)
					hql += "and updatedTime > :time ";
				Query query = session.createQuery(hql);
				query.setParameter("owner", user.getUserId());
				if(pullTime > 0)
					query.setParameter("time", pullTime);
				if(query.list().size()>0){
					staffUser =  query.list();
				}
			} catch (Exception e) {
				System.out.println("Exception = " + e.getMessage());
				if (tx != null)
					tx.rollback();
				e.printStackTrace();
				
			} finally {
				session.close();
			}
			return staffUser;
		}


		/**
		 * get Private User by Id. 
		 **/
		public static boolean getPrivateUserById(String privateUserId, UserMaster user) {
			
			Session session = null;
			Transaction tx = null;
			String str = "";
			boolean flag = false;
			long epoch = System.currentTimeMillis();
			List<PrivateUser> privateUser = new ArrayList<PrivateUser>();
			try {
				session = HibernateUtil.getSessionFactory().openSession();
				String hql = "FROM PrivateUser WHERE privateUserId = :privateUserId AND ownerId "
						+ "= :ownerId";
				Query query = session.createQuery(hql);
				query.setParameter("ownerId",user.getUserId());
				query.setParameter("privateUserId",privateUserId);
				if(query.list().size()>0){
					
					Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
					org.mongodb.morphia.query.Query<FriendList> query1 = datastore.createQuery(FriendList.class);
					query1.field("_id").equal(""+user.getUserId());
					query1.filter("friends.frndId", privateUserId );
					 
					if(query1.get() != null && !query1.asList().isEmpty()){
						flag = true;
					}
				}
			} catch (Exception e) {
				System.out.println("Exception = " + e.getMessage());
				if (tx != null)
					tx.rollback();
				e.printStackTrace();
				
			} finally {
				session.close();
			}
			return flag;
		}


		/**
		 * Delete Private User and all transactions with it 
		 **/
		public static boolean deletePrivateUser(String privateUserId,
				UserMaster usermaster) {
			boolean flag = false;
			/**
			 * Deleting Transaction Doc Of private User
			 * */
			Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
			org.mongodb.morphia.query.Query<TransactionDoc> query = datastore.createQuery(TransactionDoc.class);
			query.or(
					query.and(query.criteria("user1").equal(""+usermaster.getUserId()),query.criteria("user2").equal(privateUserId)),
					query.and(query.criteria("user1").equal(privateUserId),query.criteria("user2").equal(""+usermaster.getUserId()))
					);
			WriteResult result = datastore.delete(query);
            System.out.println(result.toString());		
            /**
			 * Deleting Private User Profile 
			 * */
            Transaction tx = null;
            Session session = null;
            int status =0;
            try{
            	session = HibernateUtil.getSessionFactory().openSession();
            	tx = session.beginTransaction();
            	String hql = "DELETE FROM PrivateUser WHERE privateUserId = :privateUserId AND ownerId "
					+ "= :ownerId";
            	Query query1 = session.createQuery(hql);
            	query1.setParameter("ownerId",usermaster.getUserId());
            	query1.setParameter("privateUserId",privateUserId);
			
            	status = query1.executeUpdate();
            	System.out.println();
            	tx.commit();
            } catch (Exception e) {
            	System.out.println("Exception = " + e.getMessage());
            	if (tx != null)
            		tx.rollback();
            	e.printStackTrace();
            } finally {
            	session.close();
            }
            
            /**
			 * Deleting Friend from FriendContact 
			 * */
            if(status > 0)
            if(FriendsDao.deleteFrndContFromList(privateUserId,usermaster)){
            	flag =true;
            }
			return flag;
            
		}
public static void main(String [] arg){
	UserMaster user = new UserMaster();
	user.setUserId(15);
	deletePrivateUser("PR-8",user);
}


/**
 * get Private user by id 
 **/
public static PrivateUser getPrivateUserByIdFor(String frndId) {
	Session session = null;
	
	PrivateUser privateUser = null;
	try {
		session = HibernateUtil.getSessionFactory().openSession();
		String hql = "FROM PrivateUser WHERE privateUserId = :privateUserId";
		Query query = session.createQuery(hql);
		
		query.setParameter("privateUserId",frndId);
		if(query.list().size()>0){
			privateUser = (PrivateUser) query.list().get(0);
		}
	} catch (Exception e) {
		System.out.println("Exception = " + e.getMessage());
	} finally {
		session.close();
	}
	
	return privateUser;
}

}
