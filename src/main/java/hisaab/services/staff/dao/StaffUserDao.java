package hisaab.services.staff.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.Contact;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.notification.NotificationHelper;
import hisaab.services.notification.StaffRemoveNotification;
import hisaab.services.sms.SMSHelper;
import hisaab.services.sms.dao.SmsDao;
import hisaab.services.sms.modal.SmsTable;
import hisaab.services.staff.modal.StaffProfile;
import hisaab.services.staff.modal.StaffUser;
import hisaab.services.staff.modal.StaffUserRequest;
import hisaab.services.staff.modal.UserStaffMapping;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.services.user.modal.UserProfile;
import hisaab.util.Constants;
import hisaab.util.RandomStringHelper;

public class StaffUserDao {


	public static List<Contact> addStaffUser(List<StaffUser> user, UserMaster user1,List<Contact> contact,List<Contact> invalidcontactlist) {
		
		Session session = null;
		Transaction tx = null;
		long epoch = System.currentTimeMillis();
		StaffUserRequest staffUser = null;
		UserStaffMapping staffUsermap =null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			for(Contact conta : contact){
			String hql = "from UserStaffMapping  where contactNo = :contact AND Status = :stat";
//			String hql = "from StaffUserRequest where contactNo = :contact_no AND Status <> :stat";
//			+" AND Status = :stat ";
			Query query = session.createQuery(hql);
			query.setParameter("contact", conta.getContactNo() );
			query.setParameter("stat", 1 );
/*
			String hqldelete = "from UserStaffMapping  where contactNo = :contact AND Status = :stat";
			Query querydelete = session.createQuery(hqldelete);
			querydelete.setParameter("contact", conta.getContactNo() );
			querydelete.setParameter("stat", 2 );
			*/
			if(query.list().size()>0){
				/** If user Staff already added and active
				 * */
				staffUsermap = (UserStaffMapping) query.list().get(0);
				conta.setsFlag(Constants.STAFF_NOT_ADDED);
						
			
				}

			else {
				/** If user Staff not active in userStaffmapping table
				 * */
				
					String hql1 = "from StaffUserRequest where contactNo = :contact AND Status NOT IN (3,4) order by createdTime desc";
		
					Query query1 =session.createQuery(hql1);
//					query1.setParameter("stat", 3 );
					query1.setParameter("contact", conta.getContactNo() );
					if(query1.list().size()>0){
						/** IN staff request Table
						 * */
						staffUser = (StaffUserRequest) query1.list().get(0);
						int status = staffUser.getStatus();
						
						
							Calendar cal = Calendar.getInstance();
							cal.add(Calendar.HOUR, -6);
							long epoch1 = cal.getTimeInMillis();
							long id = staffUser.getId();
							String hql5 ="from StaffUserRequest where id = :id and createdTime < :createtime ";
							Query query5 = session.createQuery(hql5);
							query5.setParameter("id", id);
							query5.setParameter("createtime",epoch1);
							
							if(query5.list().size()>0){
								/** If staff request is pending for more than 6 hours
								 * */
							
							staffUser = new StaffUserRequest();
							staffUser.setOwnerId(user1.getUserId());
							if(user != null){
								for(StaffUser user2 : user){
									if(user2.getContactNo().equals(conta.getContactNo())){
										staffUser.setStaffUserId(user2.getStaffId());
									}
								}
							}

							staffUser.setCreatedTime(epoch);
							staffUser.setContactNo(conta.getContactNo());
							staffUser.setCountryCode(conta.getCountryCode());
							staffUser.setSecurityCode(RandomStringHelper.getCodeRandomAlphaNumeric());
							staffUser.setDisplayName(conta.getName());
							staffUser.setUpdatedTime(epoch);
//							tx = session.beginTransaction();
							addStaffUserRequest(staffUser);
//							session.save(staffUser);
//							tx.commit();
							/***
							 * Sending text message to invite staff user.
							 ***/
							UserMaster userB = UserDao.getUserByContactNo(conta.getContactNo());
							if(userB != null){
								String msg = new StringBuffer(user1.getContactNo())
											.append(" has sent you Staff Invite.").toString();
								
								NotificationHelper.buildAndSendStaffInviteNotification(user1,userB, staffUser, msg, false);
							}
							userB = null;
							
							if(Constants.SMS_PACK_ACTIVE){
								String strMsg = SMSHelper.generatePromotionalStaffInviteMessage(user1, conta.getContactNo());
								String id1 =  SMSHelper.sendSms(conta.getContactNo(), strMsg, Constants.SMS_TYPE_PROMOTIONAL);
								SmsTable sms = new SmsTable();
								sms.setContactNo(conta.getContactNo());
								sms.setMsgId(id1);
								sms.setSenderId(user1.getUserId());
								sms.setType(Constants.SMS_TYPE_PROMOTIONAL);
								sms.setStatus("");
								SmsDao.addNewUserRequest(sms);
							}
							
									
							conta.setId(staffUser.getId());
							conta.setsFlag(0);
							/*Session session2 =HibernateUtil.getSessionFactory().openSession(); 
							String hql2 ="update StaffUserRequest set Status = :stat where id = :id and createdTime < :createtime";
							tx = session2.beginTransaction();
							Query query2 = session2.createQuery(hql2);
							query2.setParameter("stat", Constants.REQUEST_EXPIRED);
							query2.setParameter("id", id);
							query2.setParameter("createtime",epoch1);
							query2.executeUpdate();
							tx.commit();
							session2.close();*/
							
							}
							else
								conta.setsFlag(Constants.STAFF_NOT_ADDED);
						
						
					}else{
					staffUser = new StaffUserRequest();
					staffUser.setOwnerId(user1.getUserId());
					if(user != null){
						for(StaffUser user2 : user){
							if(user2.getContactNo().equals(conta.getContactNo())){
								staffUser.setStaffUserId(user2.getStaffId());
							}
						}
					}

					staffUser.setCreatedTime(epoch);
					staffUser.setContactNo(conta.getContactNo());
					staffUser.setCountryCode(conta.getCountryCode());
					staffUser.setSecurityCode(RandomStringHelper.getCodeRandomAlphaNumeric());
					staffUser.setDisplayName(conta.getName());
					staffUser.setUpdatedTime(epoch);
//					tx = session.beginTransaction();
					addStaffUserRequest(staffUser);
//					session.save(staffUser);
//					tx.commit();
					/***
					 *Sending text message to invite staff user.
					 ***/
					UserMaster userB = UserDao.getUserByContactNo(conta.getContactNo());
					if(userB != null){
						String msg = new StringBuffer(user1.getContactNo())
									.append(" has sent you Staff Invite.").toString();
						
						NotificationHelper.buildAndSendStaffInviteNotification(user1, userB, staffUser, msg, false);
					}
					userB = null;
					if(Constants.SMS_PACK_ACTIVE){
						String strMsg = SMSHelper.generatePromotionalStaffInviteMessage(user1, conta.getContactNo());
						String id1 =  SMSHelper.sendSms(conta.getContactNo(), strMsg, Constants.SMS_TYPE_PROMOTIONAL);
						SmsTable sms = new SmsTable();
						sms.setContactNo(conta.getContactNo());
						sms.setMsgId(id1);
						sms.setSenderId(user1.getUserId());
						sms.setType(Constants.SMS_TYPE_PROMOTIONAL);
						sms.setStatus("");
						SmsDao.addNewUserRequest(sms);
					}
					
					conta.setId(staffUser.getId());
					conta.setsFlag(0);
					
					}
			}
			
			}
			for(Contact invalidcontact : invalidcontactlist)
			{
				invalidcontact.setsFlag(Constants.STAFF_NOT_ADDED);
				contact.add(invalidcontact);
			}
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return contact;
		}

	
	private static void addStaffUserRequest(StaffUserRequest staffUser) {
		Session session = null;
		Transaction tx = null;
		StaffUserRequest staffUserlocal = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(StaffUserRequest.class);
			criteria.add(Restrictions.eq("ownerId", staffUser.getOwnerId()));
			criteria.add(Restrictions.eq("contactNo", staffUser.getContactNo()));
			
			if(!criteria.list().isEmpty()){
				staffUserlocal = (StaffUserRequest) criteria.list().get(0);
				Query query = session.createQuery("update StaffUserRequest set staffUserId =:staffUserId, createdTime =:createdTime,"
						+ "updatedTime =:updatedTime, Status =:Status, securityCode =:securityCode,"
						+ "displayName =:displayName where id =:id");
				query.setParameter("staffUserId", staffUser.getStaffUserId());
				query.setParameter("createdTime", staffUser.getCreatedTime());
				query.setParameter("updatedTime", staffUser.getUpdatedTime());
				query.setParameter("Status", 0);
				query.setParameter("securityCode", staffUser.getSecurityCode());
				query.setParameter("displayName", staffUser.getDisplayName());
				query.setParameter("id", staffUserlocal.getId());
				query.executeUpdate();
				
			}else{
				session.save(staffUser);
			}
			
		}catch(Exception e){
			tx.rollback();
			e.printStackTrace();
		}finally{
			tx.commit();
			session.close();
		}
			
		// TODO Auto-generated method stub
		
	}


	public static StaffUserRequest verifyStaffUserCode(String contact, long reqId, int status) {
		Session session = null;
		Transaction tx = null;
		long epoch = System.currentTimeMillis();
		boolean flag = false;
		StaffUserRequest st = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from StaffUserRequest where  contactNo = :contact and id = :id ";
			Query query = session.createQuery(hql);
			query.setParameter("contact", contact);
			
			query.setParameter("id", reqId);
			if(query.list().size()>0){
				
				st = (StaffUserRequest) query.list().get(0);
				if(st.getStatus()!= 0)
				{
					st = null;	
				}
					 	
			}			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return st;
	}

	
	public static StaffUser staffUserLogin(StaffUserRequest reqUser,Contact contact, String serverToken, int userType, long ownerId, int status) {
		Session session = null;
		Transaction tx = null;
		String str = "";

		
		StaffUser user = null;
		long epoch = System.currentTimeMillis();
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from StaffUser where contactNo = :contact and delFlag = :delFlag ";
			Query query = session.createQuery(hql);
			query.setParameter("contact", contact.getContactNo());
			query.setParameter("delFlag", 0);
			
			if(query.list().size()>0){
				
				user = (StaffUser) query.list().get(0);
				if(reqUser != null ){

					tx = session.beginTransaction();
					Query query2 = session.createQuery("update StaffUser set ownerId = :id , updatedTime = :update where contactNo = :contact");
							query2.setParameter("contact", contact.getContactNo());
							query2.setParameter("id",reqUser.getOwnerId());
							query2.setParameter("update", epoch);
							query2.executeUpdate();
							tx.commit();
							user.setOwnerId(reqUser.getOwnerId());
							tx = session.beginTransaction();
					Query query3 = session.createQuery("update StaffProfile set ownerId = :id , updatedTime = :update where contactNo = :contact");
							query3.setParameter("contact", contact.getContactNo());
							query3.setParameter("id",reqUser.getOwnerId());
							query3.setParameter("update", epoch);
							query3.executeUpdate();
							user.setOwnerId(reqUser.getOwnerId());
							user.getStaffProfile().setOwnerId(reqUser.getOwnerId());
							user.setUpdatedTime(epoch);
							user.getStaffProfile().setUpdatedTime(epoch);
							tx.commit();
				}
				
			}
			else{
					
				user = new StaffUser();
				user.setContactNo(contact.getContactNo());
				user.setCountrCode(contact.getCountryCode());
				user.setCreatedTime(epoch);
                user.setUpdatedTime(epoch);
				user.setAuthToken(serverToken);
				user.setUserType(userType);
				user.setOwnerId(ownerId);
				
				user.getStaffProfile().setContactNo(contact.getContactNo());
				user.getStaffProfile().setCreatedTime(epoch);
				user.getStaffProfile().setUpdatedTime(epoch);
				user.getStaffProfile().setUserName(contact.getName());
				user.getStaffProfile().setUserType(userType);
				user.getStaffProfile().setOwnerId(ownerId);
				tx = session.beginTransaction();
				
				session.save(user);
				user.setStaffId("ST-"+user.getsId());
				user.getStaffProfile().setUser(user);
				user.getStaffProfile().setStaffId(user.getStaffId());
				session.save(user.getStaffProfile());
				tx.commit();
			}
			
			reqUser.setStaffUserId(user.getStaffId());
			StaffUserRequestUpdate(reqUser ,status);
			
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

	
	public static StaffUser getStaffUserByContactNo(String contact) {
		Session session = null;
		Transaction tx = null;
		String str = "";

		
		StaffUser user = null;
		long epoch = System.currentTimeMillis();
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from StaffUser where contactNo = :contact and delFlag = :delFlag ";
			Query query = session.createQuery(hql);
			query.setParameter("contact", contact);
			query.setParameter("delFlag", 0);
			
			if(query.list().size()>0){
				
				user = (StaffUser) query.list().get(0);
								
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

	
	
	
	public static StaffUser getStaffUserFromAuthToken(String authToken) {
		Session session = null;
		Transaction tx = null;
		String str = "";
		long epoch = System.currentTimeMillis();
		StaffUser staffUser = new StaffUser();
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from StaffUser where authToken = :authToken and delFlag = :delFlag";
			Query query = session.createQuery(hql);
			query.setParameter("authToken", authToken);
			query.setParameter("delFlag", 0);
			if(query.list().size()>0){
				staffUser = (StaffUser) query.list().get(0);
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
	
	
	public static List<StaffUserRequest> getStaffRequestsByUser(UserMaster user, long pullTime) {
		Session session = null;
		Transaction tx = null;
		
		
		List<StaffUserRequest> staffRequests = new ArrayList<StaffUserRequest>();
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from StaffUserRequest where ownerId = :owner ";
			if(pullTime > 0)
				hql += "and updatedTime >= :time ";
			Query query = session.createQuery(hql);
			query.setParameter("owner", user.getUserId());
			if(pullTime > 0)
				query.setParameter("time", pullTime);
			
			if(query.list().size()>0){
				staffRequests =  query.list();
			}
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return staffRequests;
	}


	public static List<StaffUserRequest> getStaffRequestsForUser(String contact, long pullTime) {
		Session session = null;
		Transaction tx = null;
		List<StaffUserRequest> staffRequests = new ArrayList<StaffUserRequest>();
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			cal.add(Calendar.HOUR_OF_DAY, -6);
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from StaffUserRequest where contactNo = :contact and createdTime > :createdTime ";
			if(pullTime > 0)
				hql += "and updatedTime > :time ";
			Query query = session.createQuery(hql);
			query.setParameter("contact", contact);
			query.setParameter("createdTime", cal.getTimeInMillis());
			if(pullTime > 0)
			query.setParameter("time", pullTime);
			
			if(query.list().size()>0){
				staffRequests =  query.list();
			}
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return staffRequests;
	}

	
	public static List<StaffProfile> getStaffUsers(UserMaster user, long pullTime) {
		Session session = null;
		Transaction tx = null;
		List<StaffProfile> staffUser = new ArrayList<StaffProfile>();
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from StaffProfile where ownerId = :owner ";
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
	 *Remove Staff and user mapping
	 **/
	public static int removeStaffUser(long ownerId,String contactno ,String staffId, int status) {
		Session session = null;
		Transaction tx = null;
		
		boolean flag = false;
		long blank = 0;
		long epoch = System.currentTimeMillis();
		
		int i=0;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = " Update StaffUser set ownerId = :blank, updatedTime = :time where "
					+ " staffId = :staff and ownerId = :owner";
			Query query = session.createQuery(hql);
			query.setParameter("blank", blank);
			query.setParameter("time", epoch);
			query.setParameter("staff", staffId);
			query.setParameter("owner", ownerId);
			
			 i = query.executeUpdate();
			if(i>0){
				
				String hqStaffProfile = "Update StaffProfile set ownerId = :blank, updatedTime = :time where "
						+ " staffId = :staff and ownerId = :owner";
				Query querProfile = session.createQuery(hqStaffProfile);
				querProfile.setParameter("blank", blank);
				querProfile.setParameter("time", epoch);
				querProfile.setParameter("staff", staffId);
				querProfile.setParameter("owner", ownerId);
				querProfile.executeUpdate();
				
				String hqStaffRequest = "Update UserStaffMapping set status = :deleted, updatedTime = :time where "
						+ " staffId = :staff and userId = :owner";
				Query querReq = session.createQuery(hqStaffRequest);
				querReq.setParameter("deleted", Constants.STAFF_DELETED);
				querReq.setParameter("time", epoch);
				querReq.setParameter("staff", staffId);
				querReq.setParameter("owner", ownerId);
				querReq.executeUpdate();
				
				String hqStaffRequest1 = "from StaffUserRequest where ownerId = :owner AND contactNo = :contactno order by createdTime desc";
				Query querReq1 = session.createQuery(hqStaffRequest1);
				querReq1.setParameter("owner", ownerId);
				querReq1.setParameter("contactno", contactno);
				if(querReq1.list().size()>0)
				{
				
					StaffUserRequest id =(StaffUserRequest) querReq1.list().get(0);
					System.out.println("==========="+id.getId());
					String hqldelete1 = "Update StaffUserRequest set status = :deleted, updatedTime = :time where "
							+ " id = :id and ownerId = :owner ";
					Query querdelete1 = session.createQuery(hqldelete1);
					querdelete1.setParameter("deleted", status);
					querdelete1.setParameter("time", epoch);
					querdelete1.setParameter("id", id.getId());
					querdelete1.setParameter("owner", ownerId);
					querdelete1.executeUpdate();
				}
				
			}
			
			tx.commit();
			flag = true;

			/**
			 * Notification Code ;
			 **/
			if(i>0){
					String msg = "";
					boolean isStaff = false;
					if(status == 4){
						isStaff = true;
						UserMaster userm =  UserDao.getUserForWeb(ownerId);
						if(userm != null){
							if(userm.getUserProfile().getUserName()!=null && !userm.getUserProfile().getUserName().isEmpty())
								msg += userm.getUserProfile().getUserName();
							else
								msg += userm.getContactNo();
						}
						msg += " Owner has removed you from staff";
					}
					else{
						StaffUser su = StaffUserDao.getStaffUserByStaffIdForWeb(staffId);
						if(su != null && su.getsId()>0)
							msg += su.getContactNo();
						
						msg += "  is no more working for you.";
					}
					
					NotificationHelper.buildAndSendStaffRemovalNotification(ownerId, staffId, msg, isStaff);
	
			}

		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
		} finally {
			session.close();
		}

		return i;
	}
	
	public static boolean updatePushToken(StaffUser user){
		
		boolean flag = false;
		
		Session session = null;
		
		Transaction tx = null;
		try {
			
			session = HibernateUtil.getSessionFactory().openSession();
			
		tx = session.beginTransaction();
			
			String hql = "update StaffUser set pushId = :pushId, deviceType = :deviceType,"
					+ " updatedTime = :time WHERE sId = :sId";
			Query query = session.createQuery(hql);
			query.setParameter("pushId", user.getPushId());
			query.setParameter("deviceType", user.getDeviceType());
			query.setParameter("time", System.currentTimeMillis());
			query.setParameter("sId", user.getsId());
			
			if(query.executeUpdate() > 0){
				
				flag = true;
			}
			
			tx.commit();
			
		} catch (Exception e) {
			flag = false;
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
			e.printStackTrace();
		}finally{
			session.close();
		}
		
		return flag;
	}
	
	public static void main(String[] args) {
//		System.out.println(removeStaffUser(4, "ST-4"));
		         
		List<StaffUser> staff = getStaffUserByStaffIds(Arrays.asList("ST-5", "ST-4"));
		
		System.out.println(staff);
		
	}

	public static List<StaffUser> getStaffUserByContact(List<String> contact1) {
		Session session = null;
		List<StaffUser> user = new ArrayList<StaffUser>();	
		Transaction tx = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from StaffUser where contactNo IN (:contacts)";
			Query query = session.createQuery(hql);
			query.setParameterList("contacts", contact1);
			
			if(!contact1.isEmpty()){
				if(query.list().size()>0){
					user =query.list();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
		return user;
	}
	
	
	
	/***
	 * this method to fetch staff members by staff id list.
	 ***/
	public static List<StaffUser> getStaffUserByStaffIds(List<String> staffIds) {
		Session session = null;
		List<StaffUser> users = new ArrayList<StaffUser>();	
		Transaction tx = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			String hql = "from StaffUser where staffId IN (:staffIds)";
			Query query = session.createQuery(hql);
			query.setParameterList("staffIds", staffIds);
			
		 
			if(query.list().size()>0){
				users =query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			session.close();
		}
		return users;
	}


	
	/***
	 * this method to fetch staff members by staff id list.
	 ***/
	public static StaffUser getStaffUserByStaffIdForWeb(String staffId) {
		Session session = null;
		StaffUser user = null;	
		Transaction tx = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			String hql = "from StaffUser where staffId = :staffId";
			Query query = session.createQuery(hql);
			query.setParameter("staffId", staffId);
			
		 
			if(query.list().size()>0){
				user =(StaffUser) query.list().get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			session.close();
		}
		return user;
	}

	
	
	
	public static boolean updateStaffProfile(StaffProfile userProfile){
		Session session = null;
		Transaction tx = null;
		long epoch = System.currentTimeMillis();
		boolean flag = false;

		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = "Update StaffProfile set userName = :userName, imageKey = :profileImageKey,"
							+ " pubStatus= :status, orgName = :orgName, updatedTime = :updatedDate where sId = :sId ";
			
			
			Query query = session.createQuery(hql);
			query.setParameter("sId", userProfile.getsId());
			query.setParameter("userName", userProfile.getUserName());
			query.setParameter("status", userProfile.getPubStatus());
			query.setParameter("orgName", userProfile.getOrgName());
			query.setParameter("profileImageKey", userProfile.getImageKey());
			userProfile.setUpdatedTime(epoch);
			query.setParameter("updatedDate", userProfile.getUpdatedTime());
			System.out.println(epoch);
			int i = query.executeUpdate();
			if(i>0){
				flag = true;
				updateUserOnBoardingFlag(userProfile.getsId());
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
	public static boolean updateUserOnBoardingFlag(Long userId){
		Session session = null;
		Transaction tx = null;
		long epoch = System.currentTimeMillis();
		boolean flag = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = "Update StaffUser set onBoardingFlag = :onBoarding, updatedTime = :updatedDate "
					+ "where sId = :sId ";
			Query query = session.createQuery(hql);
			query.setParameter("sId", userId);
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


	public static StaffUserRequest getStaffRequestsByReqId(long reqId) {
		Session session = null;
		StaffUserRequest user = new StaffUserRequest();	
		Transaction tx = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = "from StaffUserRequest where id = :id)";
			Query query = session.createQuery(hql);
			query.setParameter("id", reqId);
			
		 
			if(query.list().size()>0){
				user =(StaffUserRequest) query.list().get(0);
			}
				tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
			
			e.printStackTrace();
		}finally{
			session.close();
		}
		return user;
	}


	public static boolean cancelStaffUserRequest(UserMaster user, long id, int status,StaffUserRequest st) {
		Session session = null;
		Transaction tx = null;
		long epoch = System.currentTimeMillis();
		boolean flag = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = "Update StaffUserRequest set Status = :status where ownerId = :owner AND id = :id ";
			Query query = session.createQuery(hql);
			query.setParameter("status", status);
			st.setStatus(status);
			query.setParameter("owner",user.getUserId());
			query.setParameter("id",id);
			int i = query.executeUpdate();
			if(i>0){
				flag = true;
			}
			tx.commit();
			String msg = "";
			UserMaster userB = new UserMaster();
			if(status == 3){
				msg = new StringBuffer().append(st.getContactNo())
						 .append(" has rejected staff request.").toString();
				userB.setUserId(st.getOwnerId());
				System.out.println(" has rejected staff request.");
				NotificationHelper.buildAndSendStaffInviteNotification(user,userB, st, msg, true );
			}
			/*else{
				userB = UserDao.getUserByContactNo(st.getContactNo());
				if(userB != null){
					msg = new StringBuffer().append(user.getContactNo())
							 .append(" has Cacelled staff request.").toString();
					System.out.println(" has Cacelled staff request..");
				}
				
			}*/
		    
			
//			System.out.println("===--- :"+mapper.writeValueAsString(st));
			
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
	
	public static void StaffUserRequestUpdate(StaffUserRequest st, int status) {
			Session session = null;
			Transaction tx = null;
			long epoch = System.currentTimeMillis();
//			ObjectMapper mapper = new ObjectMapper();
			try {
			           session = HibernateUtil.getSessionFactory().openSession();
			           tx = session.beginTransaction();
						String hq = "Update StaffUserRequest set updatedTime = :time, status = :status,staffUserId = :staffId  where "
								+ "id = :id ";
						Query queryUp = session.createQuery(hq);
						queryUp.setParameter("time", epoch);
						queryUp.setParameter("status", status);
						queryUp.setParameter("staffId", st.getStaffUserId());
						queryUp.setParameter("id", st.getId());
						
						queryUp.executeUpdate();
						tx.commit();
						
						st.setStatus(status);
						st.setUpdatedTime(epoch);
						
						String msg = "";
						
						if(status == Constants.STAFFUSER_REQ_ACCEPTED){
							msg = new StringBuffer().append(st.getContactNo())
									 .append(" has accepted staff request.").toString();
						}else{
							msg = new StringBuffer().append(st.getContactNo())
									 .append(" has rejected staff request.").toString();
						}
					    UserMaster userB = new UserMaster();
						userB.setUserId(st.getOwnerId());
//						System.out.println("===--- :"+mapper.writeValueAsString(st));
						NotificationHelper.buildAndSendStaffInviteNotification(userB,userB, st, msg, true);
		     } catch (Exception e) {
					System.out.println("Exception = " + e.getMessage());
					e.printStackTrace();
					
				} finally {
					session.close();
				}
			}
	
	
	public static List<String> getStaffIdsForUser(Long ownerId) {
		Session session = null;
		Transaction tx = null;
		
		List<String> staffIds = null;
		List<StaffUser> staff = new ArrayList<StaffUser>();
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from StaffUser where ownerId = :owner ";
			Query query = session.createQuery(hql);
			query.setParameter("owner", ownerId);
			
			if(query.list().size()>0){
				staffIds = new ArrayList<String>();
				
				for(StaffUser su : (List<StaffUser>) query.list()){
					staffIds.add(su.getStaffId());
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
		return staffIds;
	}
	
	public static void setStaffUserInHashMap() {
		Session session = null;
		Transaction tx = null;
		String str = "";
		
		StaffUser staffUser = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from StaffUser where ownerId > :ownerid and delFlag = :delFlag";
			Query query = session.createQuery(hql);
			query.setParameter("ownerid", Long.parseLong("0"));
			query.setParameter("delFlag", 0);
			Iterator<StaffUser> itr = null;
			if(query.list().size()>0){
				itr = query.list().iterator();
				while(itr.hasNext()){
					staffUser = itr.next();
					Constants.staffUser.put(staffUser.getStaffId(), staffUser);
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
		
	}
	
	public static StaffUser getStaffUserFromAuthToken1(String authToken,String userId) {
		Session session = null;
		Transaction tx = null;
		StaffUser user = new StaffUser();
		StaffUser user1 = new StaffUser();
		user1 = Constants.staffUser.get(userId);
		if(user1 != null && user1.getStaffId().equals("")){
			if(user1.getAuthToken().equals(authToken)){
				user = user1;
			}
			System.out.print(":*Hash Map*:");
		}else{
			System.out.print(":*DAta BAse*:");
			try {
				
				session = HibernateUtil.getSessionFactory().openSession();
				String hql = "from StaffUser where authToken = :authToken and delFlag = :delFlag";
				Query query = session.createQuery(hql);
				query.setParameter("authToken", authToken);
				query.setParameter("delFlag", 0);
				
				if(query.list().size()>0){
					user = (StaffUser) query.list().get(0);
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
}
