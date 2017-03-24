package hisaab.services.user.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import hisaab.config.hibernate.HibernateUtil;

import hisaab.services.contacts.ContactHelper;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.Contact;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.user.UserHelper;

import hisaab.services.user.modal.UserMaster;
import hisaab.services.user.modal.UserProfile;
import hisaab.services.user.token.TokenModal;

import hisaab.services.user.webservices.bean.UserProfileFriendBean;

import hisaab.util.Constants;


import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.mongodb.morphia.Datastore;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;







public class UserDao {

	
	
	public static UserMaster userLogin(String contact,  String serverToken, String CountryCode,String appVersion) {
		Session session = null;
		Transaction tx = null;
		String str = "";

		
		UserMaster user = null;
		long epoch = System.currentTimeMillis();
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserMaster where contactNo = :contact and delFlag = :delFlag ";
			Query query = session.createQuery(hql);
			query.setParameter("contact", contact);
			query.setParameter("delFlag", 0);
			
			if(query.list().size()>0){
				
				user = (UserMaster) query.list().get(0);
				
				if(user.getUserType() == Constants.NOT_REGISTERED_USER){
					UserHelper.transformUnmanagedTomanagedUser(user);
					user.setUserType(0);
					user.getUserProfile().setUserType(0);
					tx = session.beginTransaction();
					
					String updateUser = "update UserMaster set userType = :type, updatedTime = :time, createdTime = :createtime where userId = :id";
					Query upq = session.createQuery(updateUser);
					upq.setParameter("type", 0);
					upq.setParameter("time", epoch);
					upq.setParameter("createtime", epoch);
					upq.setParameter("id", user.getUserId());
					upq.executeUpdate();
					
					updateUser = "update UserProfile set userType = :type, updatedTime = :time, createdTime = :createtime where userId = :id";
					upq = session.createQuery(updateUser);
					upq.setParameter("type", 0);
					upq.setParameter("time", epoch);
					upq.setParameter("id", user.getUserId());
					upq.setParameter("createtime", epoch);
					upq.executeUpdate();
		
					tx.commit();
				}
			}
			else{
				user = new UserMaster();
				user.setContactNo(contact);
				user.setCreatedTime(epoch);
				user.setUpdatedTime(epoch);
				user.setAuthToken(serverToken);
				user.setCountrCode(CountryCode);
				user.getUserProfile().setContactNo(contact);
				user.getUserProfile().setCreatedTime(epoch);
				user.getUserProfile().setUpdatedTime(epoch);
				user.setAppVersion(appVersion);
				tx = session.beginTransaction();
				session.save(user);
				
				user.getUserProfile().setUser(user);
				session.save(user.getUserProfile());
				tx.commit();
			}
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return user;
	}
	
	
	public static UserMaster getUserFromAuthToken(String authToken) {
		Session session = null;
		Transaction tx = null;
		String str = "";
		long epoch = System.currentTimeMillis();
		UserMaster user = new UserMaster();
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserMaster where authToken = :authToken and delFlag = :delFlag";
			Query query = session.createQuery(hql);
			query.setParameter("authToken", authToken);
			query.setParameter("delFlag", 0);
			
			if(query.list().size()>0){
				user = (UserMaster) query.list().get(0);
			}
			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return user;
	}
	
	
	public static List<String> getContactNoListOfUsers() {
		Session session = null;
		Transaction tx = null;
		List<String> userContactNos = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(UserMaster.class);
			criteria.add(Restrictions.eq("delFlag", 0));
			Projection p1 = Projections.property("contactNo");
			criteria.setProjection(p1);

			userContactNos = criteria.list();
			
//			if(requests != null && !requests.isEmpty()){
//				userContactNos = requests;
//			}
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return userContactNos;
	}

	
	public static HashMap<String,UserMaster> getUserListFronNumbers(List<String> contactNos) {
		Session session = null;
		Transaction tx = null;
		
		
		HashMap<String,UserMaster> userMap = new HashMap<String,UserMaster>();
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserMaster where contactNo IN (:contacts) and  delFlag = :delFlag";
			Query query = session.createQuery(hql);
			
			query.setParameter("delFlag", 0);
			query.setParameterList("contacts", contactNos);
			for(UserMaster user : (List<UserMaster>) query.list()){
				userMap.put(user.getContactNo(), user);
			}
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return userMap;
	}

	public static boolean updatePushToken(UserMaster user){
			
			boolean flag = false;
			
			Session session = null;
			
			Transaction tx = null;
			try {
				
				session = HibernateUtil.getSessionFactory().openSession();
				
			tx = session.beginTransaction();
				
				String hql = "update UserMaster set pushId = :pushId, deviceType = :deviceType,"
						+ " updatedTime = :time WHERE userId = :userId";
				Query query = session.createQuery(hql);
				query.setParameter("pushId", user.getPushId());
				query.setParameter("deviceType", user.getDeviceType());
				query.setParameter("time", System.currentTimeMillis());
				query.setParameter("userId", user.getUserId());
				
				if(query.executeUpdate() > 0){
					
					flag = true;
				}
				
				tx.commit();
				
			} catch (Exception e) {
				flag = false;
				
				e.printStackTrace();
			}finally{
				session.close();
			}
			
			return flag;
		}

	public static boolean updateProfile(UserProfile userProfile){
		Session session = null;
		Transaction tx = null;
		long epoch = System.currentTimeMillis();
		boolean flag = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = "Update UserProfile set userName = :userName, displayName = :displayName, imageKey = :profileImageKey,"
							+ " pubStatus= :status, orgName = :orgName, updatedTime = :updatedDate where userId = :userId ";
			
			
			Query query = session.createQuery(hql);
			query.setParameter("userId", userProfile.getUserId());
			query.setParameter("userName", userProfile.getUserName());
			query.setParameter("displayName", userProfile.getDisplayName());
			query.setParameter("status", userProfile.getPubStatus());
			query.setParameter("orgName", userProfile.getOrgName());
			query.setParameter("profileImageKey", userProfile.getImageKey());
			query.setParameter("updatedDate", epoch);
			int i = query.executeUpdate();
			if(i>0){
				flag = true;
				updateUserOnBoardingFlag(userProfile.getUserId());
			}
			tx.commit();	
		}
		catch (Exception e) {
			System.out.println("Exception = "+e.getMessage());
			if(tx != null)
				tx.rollback();
			e.printStackTrace();
		}finally{
			session.close();
		}
		return flag;
	}
	
	public static void main(String[] args) {
//		updateUserOnBoardingFlag(3);
		
//		UserMaster user = new UserMaster();
//		user.setUserId(2);
//		updateSmsCount(user);
//		deleteUserRequest();
		
		updateLastSyncTime(Long.parseLong("2"), System.currentTimeMillis());
	}
	
	public static boolean updateUserOnBoardingFlag(Long userId){
		Session session = null;
		Transaction tx = null;
		long epoch = System.currentTimeMillis();
		boolean flag = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = "Update UserMaster set onBoardingFlag = :onBoarding, updatedTime = :updatedDate "
					+ "where userId = :userId ";
			Query query = session.createQuery(hql);
			query.setParameter("userId", userId);
			query.setParameter("onBoarding", Constants.USER_ONBOARDING_COMPLETE);
			query.setParameter("updatedDate", epoch);
			int i = query.executeUpdate();
			if(i>0){
				flag = true;
			}
			tx.commit();	
		}
		catch (Exception e) {
			System.out.println("Exception = "+e.getMessage());
			if(tx != null)
				tx.rollback();
			e.printStackTrace();
		}finally{
			session.close();
		}
		return flag;
	}

	public static List<UserProfile> pullUsersByUserIds(UserMaster ownerUser,List<String> userIds,Long pullTime){
		
		List<UserProfile> users = new ArrayList<UserProfile>();
		List<Long> ids = new ArrayList<Long>();
		for(String s : userIds){
			try {
				ids.add(Long.parseLong(s));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			String hql = "from UserProfile WHERE userId IN (:userIds) AND updatedTime > :time ";
			if(!ids.isEmpty()){
			
				Query query = session.createQuery(hql);
				query.setParameterList("userIds", ids);
				query.setParameter("time", pullTime);
				users = (ArrayList<UserProfile>) query.list();
				
				System.out.println("Size = "+users.size());
			}
			if(ownerUser != null){
				HashMap<String,FriendContact> frndcont = FriendsDao.getFriendContactbyUserList(ownerUser, ids);
				for(UserProfile user: users){
					FriendContact frnd = frndcont.get(user.getUserId()+"");
			        user.setDisplayName(frnd.getContactName());	
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session.isOpen())
				session.close();
		}
		return users;
	}

	
	
	public static boolean updateProfileImageKey(UserProfile userProfile){
		Session session = null;
		Transaction tx = null;
		long epoch = System.currentTimeMillis();
		boolean flag = false;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = "Update UserProfile set imageKey = :profileImageKey, updatedTime = :updatedDate "
					        + " where userId = :userId ";
			Query query = session.createQuery(hql);
			query.setParameter("userId", userProfile.getUserId());
			query.setParameter("profileImageKey", userProfile.getImageKey());
			query.setParameter("updatedDate", epoch);
			int i = query.executeUpdate();
			if(i>0){
				flag = true;
			}
			tx.commit();	
		}
		catch (Exception e) {
			System.out.println("Exception = "+e.getMessage());
			if(tx != null)
				tx.rollback();
			e.printStackTrace();
		}finally{
			session.close();
		}
		return flag;
	}


	public static List<UserProfile> getUserProfileforlist(List<Long> userlist) {
		Session session = null;
		Transaction tx = null;
		
		List<UserProfile> userprofilelist = new ArrayList<UserProfile>();
				
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserProfile where userId IN (:userlist)";
			Query query = session.createQuery(hql);
			query.setParameterList("userlist", userlist);
			userprofilelist= (List<UserProfile>)query.list();
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return userprofilelist;
		// TODO Auto-generated method stub
		
	}
	

	public static HashMap<String,UserProfileFriendBean> getHashUserProfileforlist(List<String> userlist) throws JsonGenerationException, JsonMappingException, IOException {
		Session session = null;
		Transaction tx = null;
		List<Long> ids = new ArrayList<Long>();
		for(String s : userlist){
			try {
				ids.add(Long.parseLong(s));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		HashMap<String,UserProfileFriendBean> hashprofile = new HashMap<String,UserProfileFriendBean>();
		UserProfileFriendBean userfrnd = new UserProfileFriendBean();
		List<UserProfile> userprofilelist = new ArrayList<UserProfile>();
		ObjectMapper mapper = new ObjectMapper();		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserProfile where userId IN (:userlist)";
			Query query = session.createQuery(hql);
			query.setParameterList("userlist", ids);
			userprofilelist= (List<UserProfile>)query.list();
			
			for(UserProfile user : userprofilelist){
				 userfrnd = new UserProfileFriendBean();
				 userfrnd.setUserprofile(user);
				
				hashprofile.put(""+user.getUserId(), userfrnd);
			}
			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		
		return hashprofile;
		// TODO Auto-generated method stub
		
	}
	
	public static List<UserMaster> getUserByIds(List<Long> userlist) {
		Session session = null;
		Transaction tx = null;
		
		List<UserMaster> userList = new ArrayList<UserMaster>();
				
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserMaster where userId IN (:userlist)";
			Query query = session.createQuery(hql);
			query.setParameterList("userlist", userlist);
			userList = (List<UserMaster>)query.list();
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return userList;
	}
	
	public static UserMaster getUserForWeb(long userId) {
		Session session = null;
		Transaction tx = null;
		
		UserMaster user = new UserMaster();
				
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserMaster where userId = :userId ";
			Query query = session.createQuery(hql);
			query.setParameter("userId", userId);
			if(query.list().size()>0)
				user = (UserMaster)query.list().get(0);
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return user;
	}
	public static UserProfile getUserProfileById(long userid) {
		Session session = null;
		Transaction tx = null;
		
		UserProfile userprofile = null;
				
		try {
				session = HibernateUtil.getSessionFactory().openSession();
				String hql = "from UserProfile where userId = :userlist";
				Query query = session.createQuery(hql);
				query.setParameter("userlist", userid);
				userprofile= (UserProfile) query.list().get(0);
			} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return userprofile;
		// TODO Auto-generated method stub
		
	}


	public static UserMaster userLogin2(Contact contact, String serverToken, int userType, long ownerId) {
		Session session = null;
		Transaction tx = null;
		String str = "";

		
		UserMaster user = null;
		long epoch = System.currentTimeMillis();
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserMaster where contactNo = :contact and delFlag = :delFlag ";
			Query query = session.createQuery(hql);
			query.setParameter("contact", contact.getContactNo());
			query.setParameter("delFlag", 0);
			
			
			if(query.list().size()>0){
				
				user = (UserMaster) query.list().get(0);
								
			}
			else{
				user = new UserMaster();
				user.setContactNo(contact.getContactNo());
				user.setCountrCode(contact.getCountryCode());
				user.setCreatedTime(epoch);
				user.setAuthToken(serverToken);
				user.setUserType(userType);
				user.setOwnerId(ownerId);
				user.getUserProfile().setContactNo(contact.getContactNo());
				user.getUserProfile().setCreatedTime(epoch);
				user.getUserProfile().setUserName(contact.getName());
				user.getUserProfile().setUserType(userType);
				tx = session.beginTransaction();
				session.save(user);
				
				user.getUserProfile().setUser(user);
				session.save(user.getUserProfile());
				tx.commit();
			}
			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		/**
		 * testing for cache of contact
		 * ***/
		/*UserCache usercache = new UserCache();
		usercache.setContactno(user.getContactNo());
		usercache.setUserId(user.getUserId());
		usercache.setUserType(user.getUserType());
		Constants.cache.put(user.getContactNo(), usercache);*/
		
		return user;
	}
	public static List<UserMaster> userLoginForStaffUser(List<String> contact) {
		Session session = null;
		List<UserMaster> user = null;
		long epoch = System.currentTimeMillis();
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserMaster where contactNo IN (:contact) and delFlag = :delFlag ";
			Query query = session.createQuery(hql);
			query.setParameterList("contact", contact);
			query.setParameter("delFlag", 0);
			
			if(query.list().size()>0){
				
				user = query.list();
							
			}
			/*
			else{
				user = new UserMaster();
				user.setContactNo(contact.getContactNo());
				user.setCreatedTime(epoch);
				user.setAuthToken(serverToken);
				user.setUserType(Constants.STAFF_USER);
				user.getUserProfile().setContactNo(contact.getContactNo());
				user.getUserProfile().setCreatedTime(epoch);
				user.getUserProfile().setUserName(contact.getName());
				tx = session.beginTransaction();
				session.save(user);
				
				user.getUserProfile().setUser(user);
				session.save(user.getUserProfile());
				tx.commit();
			}*/
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
//			if (tx != null)
//				tx.rollback();
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return user;
	}


	public static UserMaster getUserByContactNo(String contactNo) {
		
			Session session = null;
			Transaction tx = null;
			
			UserMaster userList = null;
					
			try {
				session = HibernateUtil.getSessionFactory().openSession();
				String hql = "from UserMaster where contactNo = :contactNo AND delFlag = :delete AND userType =:usertype";
				Query query = session.createQuery(hql);
				query.setParameter("contactNo", contactNo);
				query.setParameter("delete", 0);
				query.setParameter("usertype", 0);
				if(!query.list().isEmpty()){
					userList = (UserMaster)query.list().get(0);
				}
			} catch (Exception e) {
				System.out.println("Exception = " + e.getMessage());
				e.printStackTrace();
			} finally {
				session.close();
			}
			return userList;
		}
	
	public static void updateSmsCount(UserMaster user, UserMaster sender){
		
		boolean flag = false;
		
		Session session = null;
		
		Transaction tx = null;
		try {
			
			session = HibernateUtil.getSessionFactory().openSession();
			
		tx = session.beginTransaction();
			
			String hql = "update UserMaster set smsCount = smsCount+1, "
					+ " updatedTime = :time WHERE userId = :userId ";
			Query query = session.createQuery(hql);
			query.setParameter("time", System.currentTimeMillis());
			query.setParameter("userId", user.getUserId());
//			query.setParameter("msg",user.setValueMsgBy(sender.getUserId()));
//			query.setParameter("date",user.setValueDateOfMsg());
			if(query.executeUpdate() > 0){
				
				flag = true;
			}
			
			tx.commit();
			
		} catch (Exception e) {
			flag = false;
			
			e.printStackTrace();
		}finally{
			session.close();
		}
	}

	public static void deleteUserRequest(){
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx=null;
			tx = session.beginTransaction();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, -2);
			System.out.println(""+cal.getTimeInMillis());
			org.hibernate.Query query=session.createQuery("DELETE FROM UserRequest WHERE createdTime < :time3 AND status = :status");
			query.setParameter("time3", cal.getTimeInMillis());
			query.setParameter("status", 0);
			query.executeUpdate();
			tx.commit();				
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			
			e.printStackTrace();
			
		} finally {
			session.close();
		}
	}
	public static FriendContact addUnRegisteredUser(UserMaster user1, Contact contact)
	{
		
		FriendContact friendResponse = null;
		long epoch= System.currentTimeMillis();
		
		FriendContact fr = null;
		FriendList frndlist = FriendsDao.getAssociatedUserDoc(user1);
//	    String contact1=Helper.validatePhoneNo(contact.getContactNo());
	    PhoneNumber phnNum = ContactHelper.validatePhoneNumber(contact.getContactNo());
		if(phnNum !=null){
			contact.setContactNo(phnNum.getNationalNumber()+"");
			contact.setCountryCode(phnNum.getCountryCode()+"");
			try
			{
				UserMaster user = UserDao.userLogin2(contact,TokenModal.generateToken(), Constants.NOT_REGISTERED_USER, 0);
			    if(user.getUserId()>0){
			    	fr = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, user1);
					FriendContact frncon = new FriendContact();
				    frncon.setContactName(contact.getName());
				    frncon.setContactNo(contact.getContactNo());
				    if(user.getUserType() == 1)
				    	frncon.setFrndStatus(Constants.NOT_REGISTERED_USER);
				    /*else
				    	frncon.setFrndStatus(0);*/
				    frncon.setCreatedTime(System.currentTimeMillis());
				    frncon.setUpdatedTime(System.currentTimeMillis());
				    frncon.setFrndId(""+user.getUserId());
					    
						
					long count = frndlist.getIdCount();
					frncon.setId(++count);
					frndlist.setIdCount(count);
					
					frndlist.setUpdatedTime(epoch);
					frndlist.setUserId(""+user1.getUserId());
					frndlist.setFriends(Arrays.asList(frncon));
			    	/*if(fr != null)
			    	{
			    		if(fr.getFrndStatus() == 0){
			    			frndlist.setFriends(Arrays.asList(fr));
			    		}else if(fr.getFrndStatus() == 1){
			    			frndlist.setFriends(Arrays.asList(fr));
			    		}
			    	}else{
			    		FriendsDao.addFriends(frndlist);
			    	}*/
					if(user.getUserType() == 1){
						if(fr == null){
							if(FriendsDao.addFriends(frndlist))
							{
								friendResponse = (FriendContact)frndlist.getFriends().get(0); 
							}
						}
					
					}
//				ContactUserprofileBean frnBean = new ContactUserprofileBean();
				/*UserProfile userpro = new UserProfile();
				UserProfileFriendBean userbe = new UserProfileFriendBean();
				HashMap<String,UserProfileFriendBean> hash = new HashMap<String,UserProfileFriendBean>();
				userpro = UserDao.getUserProfileById(user.getUserId());
				Iterator<FriendContact> itr = frndlist.getFriends().iterator();
				if(itr.hasNext())
				userbe.setFriendcontact(itr.next());	
			    userbe.setUserprofile(userpro);
				hash.put(""+user.getUserId(),userbe);
				frnBean.setList(hash);
				frnBean.setStatus(Constants.SUCCESS_RESPONSE);
				result= frnBean;*/
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		
		
		}else{
		    System.out.println("Invalid Contact No");
		}
		return friendResponse;
	}


	public static FriendContact addUnRegisteredUserInBulk(List<String> contactList, HashMap<String, Contact> contactMap) {
		FriendContact friendResponse = null;
		long epoch= System.currentTimeMillis();
		
		FriendContact fr = null;
//		FriendList frndlist = FriendsDao.getAssociatedUserDoc(user1);
		for(String contact : contactList )
		{
			try
			{
//				UserDao.test(contactMap.get(contact),Constants.NOT_REGISTERED_USER);
				UserDao.userLogin2(contactMap.get(contact),TokenModal.generateToken(), Constants.NOT_REGISTERED_USER, 0);
//			    if(user.getUserId()>0){
			    	/*fr = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, user1);
					FriendContact frncon = new FriendContact();
				    frncon.setContactName(contact.getName());
				    frncon.setContactNo(contact.getContactNo());
				    if(user.getUserType() == 1)
				    	frncon.setFrndStatus(Constants.NOT_REGISTERED_USER);
				    
				    frncon.setCreatedTime(System.currentTimeMillis());
				    frncon.setUpdatedTime(System.currentTimeMillis());
				    frncon.setFrndId(""+user.getUserId());
					    
						
					long count = frndlist.getIdCount();
					frncon.setId(++count);
					frndlist.setIdCount(count);
					
					frndlist.setUpdatedTime(epoch);
					frndlist.setUserId(""+user1.getUserId());
					frndlist.setFriends(Arrays.asList(frncon));
			    	
					if(user.getUserType() == 1){
						if(fr == null){
							if(FriendsDao.addFriends(frndlist))
							{
								friendResponse = (FriendContact)frndlist.getFriends().get(0); 
							}
						}
					
					}*/

//			}
			}catch(Exception e){
				e.printStackTrace();
			}
		
		}
		
		return friendResponse;
	}
	public static UserMaster getUserFromAuthToken1(String authToken,String userId) {
		Session session = null;
		Transaction tx = null;
		UserMaster user = new UserMaster();
		UserMaster user1 = new UserMaster();
		user1 = Constants.userMaster.get(userId);
		if(user1 != null && user1.getUserId() > 0){
			if(user1.getAuthToken().equals(authToken)){
				user = user1;
			}
			System.out.print(":*Hash Map*:");
		}else{
			System.out.print(":*DAta BAse*:");
			try {
				
				session = HibernateUtil.getSessionFactory().openSession();
				String hql = "from UserMaster where authToken = :authToken and delFlag = :delFlag";
				Query query = session.createQuery(hql);
				query.setParameter("authToken", authToken);
				query.setParameter("delFlag", 0);
				
				if(query.list().size()>0){
					user = (UserMaster) query.list().get(0);
				}
				
			} catch (Exception e) {
				System.out.println("Exception = " + e.getMessage());
				if (tx != null)
					tx.rollback();
				e.printStackTrace();
				
			} finally {
				session.close();
			}
	}
		return user;
	}
	
	public static void setUserMasterInHashMap() {
		Session session = null;
		
		
		UserMaster useritr = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserMaster where userType = :usertype and delFlag = :delFlag";
			Query query = session.createQuery(hql);
			query.setParameter("usertype", 0);
			query.setParameter("delFlag", 0);
			Iterator<UserMaster> itr = null;
			if(query.list().size()>0){
				itr = query.list().iterator();
				while(itr.hasNext()){
					useritr = itr.next();
					Constants.userMaster.put(""+useritr.getUserId(), useritr);
				}
			}
			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			
			
		} finally {
			session.close();
		}
		
	}
	/*public static void setUserMasterInCacheMap() {
		Session session = null;
		
		List<UserCache> userlist = null; 
		UserMaster useritr = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserMaster where delFlag = :delFlag";
			Query query = session.createQuery(hql);
			query.setParameter("delFlag", 0);
			Iterator<UserMaster> itr = null;
			if(query.list().size()>0){
				UserCache temp = null;
				itr = query.list().iterator();
				while(itr.hasNext()){
					temp = new UserCache();
					useritr = itr.next();
					temp.setUserId(useritr.getUserId());
					temp.setUserType(useritr.getUserType());
					temp.setContactno(useritr.getContactNo());
					Constants.cache.put(temp.getContactno(), temp);
				}
			}
			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			
			
		} finally {
			session.close();
		}
		
	}*/
	public static void test(Contact contact, int userType) {
		  Session session = null;
		  Transaction tx = null;
		  String str = "";

		  
		  UserMaster user = null;
		  long epoch = System.currentTimeMillis();
		  
		  try {
		   session = HibernateUtil.getSessionFactory().openSession();
		   tx = session.beginTransaction();
		    Query query = session.createSQLQuery("INSERT IGNORE INTO user_master (auth_token,contact_no,user_type,created_time) "
		       + " VALUES (:token,:contact, :userType, :time)");
		             query.setParameter("token",TokenModal.generateToken() );
		             query.setParameter("contact", contact.getContactNo());
		             query.setParameter("userType",userType);
		             query.setParameter("time",epoch);
		             
		             int i= query.executeUpdate();
		             System.out.println("res : "+i);
		             if(i > 0){
		              Query subquery = session.createSQLQuery("INSERT IGNORE INTO user_profile (contact_no,user_name,user_type,created_time,updated_time) "
		        + " VALUES (:contact,:name,:userType, :time, :utime)");
		               
		               subquery.setParameter("contact", contact.getContactNo());
		               subquery.setParameter("name", contact.getName());
		               subquery.setParameter("userType",userType);
		               subquery.setParameter("time",epoch);
		               subquery.setParameter("utime",epoch);
		               
		               subquery.executeUpdate();
		             }
		/*    tx = session.beginTransaction();
		    session.save(user);
		    
		    user.getUserProfile().setUser(user);
		    session.save(user.getUserProfile());
		    tx.commit();*/
		             tx.commit();
		   
		  } catch (Exception e) {
		   System.out.println("Exception = " + e.getMessage());
		   if (tx != null)
		    tx.rollback();
		   e.printStackTrace();
		   
		  } finally {
		   session.close();
		  }
//		  return user;
		 }


	public static void setTransactionCount(Entry pair) {
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = "Update UserProfile set transactionCount = :transactioncount"
					        + " where userId = :userId ";
			Query query = session.createQuery(hql);
			query.setParameter("userId", pair.getKey());
			query.setParameter("transactioncount", pair.getValue());
			int i = query.executeUpdate();
			if(i>0){
				
			}
			tx.commit();	
		}
		catch (Exception e) {
			System.out.println("Exception = "+e.getMessage());
			if(tx != null)
				tx.rollback();
			e.printStackTrace();
		}finally{
			session.close();
		}
		
		
	}


	public static UserMaster getUserByContactNoForAddPullDoc(String contactNo) {
		
		Session session = null;
		Transaction tx = null;
		
		UserMaster userList = null;
				
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserMaster where contactNo = :contactNo AND delFlag = :delete ";
			Query query = session.createQuery(hql);
			query.setParameter("contactNo", contactNo);
			query.setParameter("delete", 0);
//			query.setParameter("usertype", 0);
			if(!query.list().isEmpty()){
				userList = (UserMaster)query.list().get(0);
			}
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return userList;
	}


	public static boolean setAppVersion(UserMaster user, int appVersion) {
		Session session = null;
		Transaction tx = null;
		long epoch = System.currentTimeMillis();
		boolean flag = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = "Update UserMaster set appVersion = :appversion, updatedTime = :updatedDate "
					+ "where userId = :userId ";
			Query query = session.createQuery(hql);
			query.setParameter("userId", user.getUserId());
			query.setParameter("appversion", ""+appVersion);
			query.setParameter("updatedDate", epoch);
			int i = query.executeUpdate();
			if(i>0){
				flag = true;
			}
			tx.commit();	
		}
		catch (Exception e) {
			System.out.println("Exception = "+e.getMessage());
			if(tx != null)
				tx.rollback();
			e.printStackTrace();
		}finally{
			session.close();
		}
		return flag;
	}
	
	
	public static boolean updateLastSyncTime(long userId, long epoch){
		boolean flag = false;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			String hql = "update UserProfile set lastSyncTime = :time"
					+ "  WHERE userId = :userId";
			
			Query query = session.createQuery(hql);
			query.setParameter("time", epoch);
			query.setParameter("userId", userId);
			System.out.println(query);
			System.out.println("user id : "+userId);
			System.out.println("time : "+epoch);
			if(query.executeUpdate() > 0){
				flag = true;
			}
			
			tx.commit();
			
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}finally{
			session.close();
		}
		
		return flag;
	}
	
	
	public static boolean updateTransActivityTime(long userId, long epoch){
		boolean flag = false;
		Session session = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			String hql = "update UserProfile set lastActivity = :time"
					+ "  WHERE userId = :userId";
			
			Query query = session.createQuery(hql);
			query.setParameter("time", epoch);
			query.setParameter("userId", userId);
			
			if(query.executeUpdate() > 0){
				flag = true;
			}
			
			tx.commit();
			
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}finally{
			session.close();
		}
		
		return flag;
	}

}
