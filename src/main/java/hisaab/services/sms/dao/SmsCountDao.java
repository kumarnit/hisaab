package hisaab.services.sms.dao;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.services.sms.modal.SmsCountTable;
import hisaab.services.user.modal.UserMaster;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

public class SmsCountDao {

	public static boolean addSmsCount(SmsCountTable smsRequest ) {
		Session session = null;
		Transaction tx = null;
		boolean flag  = false;
		long epoch = System.currentTimeMillis();
		SmsCountTable smsCount = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Criteria q=session.createCriteria(SmsCountTable.class);
			q.add(Restrictions.or(
					Restrictions.and(Restrictions.eq("receiverId", smsRequest.getReceiverId()),Restrictions.eq("senderId", smsRequest.getSenderId()))
					,Restrictions.and(Restrictions.eq("receiverId", smsRequest.getSenderId()),Restrictions.eq("senderId", smsRequest.getReceiverId()))
					));
			if(q.list().size()>0){
				smsCount = (SmsCountTable) q.list().get(0);
				String updateUser = "update SmsCountTable set smsCount = :count1, updatedTime = :update,"
						+ "time = :time1 where id = :id1";
				Query upq = session.createQuery(updateUser);
				upq.setParameter("count1",smsCount.getSmsCount()+1 );
				upq.setParameter("time1", epoch);
				upq.setParameter("update", epoch);
				upq.setParameter("id1", smsCount.getId());
				upq.executeUpdate();
			}else{
				session.save(smsRequest);
			}
			tx.commit();
						
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return flag;
	}
	public static SmsCountTable getSmsCountDetail(UserMaster user, String friendId){
		Session session = null;
				
		SmsCountTable smsCount = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			Criteria q=session.createCriteria(SmsCountTable.class);
			q.add(Restrictions.or(
					Restrictions.and(Restrictions.eq("receiverId", user.getUserId()),Restrictions.eq("senderId", Long.parseLong(friendId)))
					,Restrictions.and(Restrictions.eq("receiverId", Long.parseLong(friendId)),Restrictions.eq("senderId", user.getUserId()))
					));

			if(q.list().size()>0){
				smsCount = (SmsCountTable) q.list().get(0);
			}
		
						
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return smsCount;
	}
	
}
