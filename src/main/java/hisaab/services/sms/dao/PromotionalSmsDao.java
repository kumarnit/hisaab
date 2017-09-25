package hisaab.services.sms.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.services.sms.modal.PromotionalSms;

public class PromotionalSmsDao {

	/**
	 * Add Promotional sms to db
	 **/
	public static boolean addPromotionalSms(List<PromotionalSms> smsUserList){
		boolean resFlag = false;
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			for(PromotionalSms prosms : smsUserList)
				session.save(prosms);
			tx.commit();
			resFlag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(session!=null)
				session.close();
		}
		return resFlag;
	}
}
