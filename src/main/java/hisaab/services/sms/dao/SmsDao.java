package hisaab.services.sms.dao;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.services.sms.modal.SmsTable;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class SmsDao {

	
	public static boolean addNewUserRequest(SmsTable smsRequest ) {
		Session session = null;
		Transaction tx = null;
		boolean flag  = false;
		long epoch = System.currentTimeMillis();
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			smsRequest.setCreatedTime(epoch);
			session.save(smsRequest);
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
	
	
}
