package hisaab.services.user.dao;

import java.util.Calendar;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.services.sms.SMSHelper;
import hisaab.services.sms.dao.SmsDao;
import hisaab.services.sms.modal.SmsTable;
import hisaab.services.user.modal.UserMaster;
import hisaab.services.user.modal.UserRequest;
import hisaab.services.user.token.TokenModal;
import hisaab.util.Constants;
import hisaab.util.RandomStringHelper;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;


public class RequestDao {
	
	public static String addNewUserRequest(String appVersion) {
		Session session = null;
		Transaction tx = null;
		String str = "";
		long epoch = System.currentTimeMillis();
		UserRequest userRequest = new UserRequest();
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			userRequest.setServerToken(TokenModal.generateToken());
			userRequest.setCreatedTime(epoch);
			userRequest.setAppVersion(appVersion);
			session.save(userRequest);
			
			tx.commit();
			str = userRequest.getServerToken();
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return str;
	}
	
	
	public static UserRequest addContactToUserRequest(String serverToken, String contact, String countryCode,String appVersion) {
		Session session = null;
		Transaction tx = null;
		String str = "";
		Boolean smsSendingStatus = true;
		long epoch = System.currentTimeMillis();
		UserRequest userRequest = new UserRequest();
		try {
			String sCode = "";
			session = HibernateUtil.getSessionFactory().openSession();
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR_OF_DAY, -3);
			Calendar cal1 = Calendar.getInstance();
			cal1.add(Calendar.MINUTE, -5);
			long time = cal.getTimeInMillis();
			String reqCheck = "from UserRequest where createdTime > :time AND contactNo = :contact "
					+ " AND status = :status order by createdTime";
			Query quer = session.createQuery(reqCheck);
			quer.setParameter("time", time);
			System.out.println(time);
			quer.setParameter("contact", contact);
			quer.setParameter("status", 1);
			if(quer.list().size()>0){
				UserRequest userRequest1 = (UserRequest) quer.list().get(0);
				sCode = userRequest1.getSecurityCode();
			}
			
			
			String hql = "from UserRequest where serverToken = :serverToken";
			Query query = session.createQuery(hql);
			query.setParameter("serverToken", serverToken);
			
			if(query.list().size()>0){
				userRequest = (UserRequest) query.list().get(0);
				if(!sCode.isEmpty())
					userRequest.setSecurityCode(sCode);
				else
					userRequest.setSecurityCode(RandomStringHelper.getCodeRandomString());
				 userRequest.setUpdatedTime(epoch);
				 userRequest.setContactNo(contact);
				 userRequest.setCountryCode(countryCode);
				 
				String updateHql = "update UserRequest set securityCode = :scode, updatedTime = :epoche,"
						+ " status = :status, contactNo = :contact, countryCode =:ccode, appVersion = :version "
						+ " where id = :id";
				tx = session.beginTransaction();
				Query queryUp = session.createQuery(updateHql);
				queryUp.setParameter("scode",userRequest.getSecurityCode() );
				queryUp.setParameter("epoche", userRequest.getUpdatedTime());
				queryUp.setParameter("status", 1);
				queryUp.setParameter("contact", userRequest.getContactNo());
				queryUp.setParameter("ccode", countryCode);
				queryUp.setParameter("version",appVersion);
				queryUp.setParameter("id", userRequest.getId());
				queryUp.executeUpdate();
				tx.commit();
				
				
				Criteria criteria = session.createCriteria(SmsTable.class);
				criteria.add(Restrictions.gt("createdTime", cal1.getTimeInMillis()));
				criteria.add(Restrictions.eq("contactNo", contact));
				criteria.add(Restrictions.eq("type", Constants.SMS_TYPE_TRANSACTIONAL));
				
				if(!criteria.list().isEmpty()){
					smsSendingStatus = false;
				}
				
				if(Constants.SMS_PACK_ACTIVE && smsSendingStatus){
					System.out.println("in sms");
					String strMsg = SMSHelper.generateTransactionalCodeMessage(userRequest.getSecurityCode());
					SmsTable sms = new SmsTable();
					sms.setContactNo(userRequest.getContactNo());
					sms.setType(Constants.SMS_TYPE_TRANSACTIONAL);
					sms.setStatus("");
					sms.setMsgId(SMSHelper.sendSms(userRequest.getContactNo(), strMsg, Constants.SMS_TYPE_TRANSACTIONAL));
					SmsDao.addNewUserRequest(sms);
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
		return userRequest;
	}


	public static UserRequest verifyUserCode(String serverToken, String contact, String scode) {
		Session session = null;
		Transaction tx = null;
		UserRequest userRequest = null;
		boolean flag = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserRequest where serverToken = :serverToken and contactNo = :contact "
					+ " and securityCode = :scode ";
			Query query = session.createQuery(hql);
			query.setParameter("serverToken", serverToken);
			query.setParameter("contact", contact);
			query.setParameter("scode", scode);
			
			if(query.list().size()>0){
				
				flag = true;
				
				 userRequest = (UserRequest) query.list().get(0);
				 
				String updateHql = "update UserRequest set updatedTime = :epoche, status = :status where id = :id";
				tx = session.beginTransaction();
				Query queryUp = session.createQuery(updateHql);
				
				queryUp.setParameter("epoche", userRequest.getUpdatedTime());
				queryUp.setParameter("status", 2);
				queryUp.setParameter("id", userRequest.getId());
				queryUp.executeUpdate();
				tx.commit();
				
				
				
			}			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
		
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return userRequest;
	}

}
