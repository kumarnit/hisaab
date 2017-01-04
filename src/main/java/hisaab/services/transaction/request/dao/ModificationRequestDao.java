package hisaab.services.transaction.request.dao;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.services.transaction.request.modal.ModificationRequest;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;

public class ModificationRequestDao {

	public static void addModificationRequest(ModificationRequest modReq, UserMaster user){
		
		Session session = null;
		Transaction tx = null;
		String str = "";
		long epoch = System.currentTimeMillis();
		try {
			
			session =  HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			modReq.setCreatedBy(user.getUserId());
			modReq.setCreatedTime(epoch);
			modReq.setUpdatedTime(epoch);
			session.save(modReq);
			tx.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
	}
	
	
	public static ModificationRequest getModificationRequest(String transacId, int operation, UserMaster user){
		ModificationRequest modreq = null;
		Session session = null;
		Transaction tx = null;
		String str = "";
		long epoch = System.currentTimeMillis();
		try {
			
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ModificationRequest.class);
			criteria.add(Restrictions.eq("action", operation));
			criteria.add(Restrictions.eq("transactionId", transacId));
			criteria.add(Restrictions.eq("forUser",""+user.getUserId()));
			criteria.add(Restrictions.eq("status",0));
			criteria.addOrder(Order.desc("createdTime"));
			List<ModificationRequest> requests = criteria.list();
			
			if(requests != null && !requests.isEmpty()){
				modreq = requests.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		return modreq;
	}
	
	public static void updateModificationRequest(ModificationRequest modReq, UserMaster user){
		
		Session session = null;
		Transaction tx = null;
		String str = "";
		long epoch = System.currentTimeMillis();
		try {
			
			session =  HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = " update ModificationRequest set status = :status, updatedTime = :time"
					+ " where id = :id and forUser = :uid and status = :stat ";
			
			Query query  = session.createQuery(hql);
			query.setParameter("status", modReq.getStatus());
			query.setParameter("time", System.currentTimeMillis());
			query.setParameter("id", modReq.getId());
			query.setParameter("stat", 0);
			query.setParameter("uid", ""+user.getUserId());
			
			query.executeUpdate();
			
			tx.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
	}


	public static List<ModificationRequest> pullModificationRequest(
			UserMaster user, long pullTime) {
		List<ModificationRequest> modreq = null;
		Session session = null;
		Transaction tx = null;
		String str = "";
		long epoch = System.currentTimeMillis();
		try {
			
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ModificationRequest.class);
			criteria.add(Restrictions.eq("createdBy", user.getUserId()));
			criteria.add(Restrictions.ne("status", 0));
			criteria.add(Restrictions.gt("updatedTime",pullTime));
			criteria.add(Restrictions.eq("action", Constants.TRANSACTION_DELETE));
			List<ModificationRequest> requests = criteria.list();
			
			if(requests != null && !requests.isEmpty()){
				modreq = requests;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		return modreq;
	}
	
	public static ModificationRequest getModificationRequestFor(String transacId){
		ModificationRequest modreq = null;
		Session session = null;
		try {
			
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ModificationRequest.class);
			criteria.add(Restrictions.eq("transactionId", transacId));
			criteria.add(Restrictions.eq("status",0));
			List<ModificationRequest> requests = criteria.list();
			
			if(requests != null && !requests.isEmpty()){
				modreq = requests.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		return modreq;
	}
	
	public static void deleteModificationRequest(){
		
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx=null;
			tx = session.beginTransaction();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -2);
			System.out.println(""+cal.getTimeInMillis());
			org.hibernate.Query query=session.createQuery("DELETE FROM ModificationRequest WHERE updatedTime < :time3 AND Status <>:status");
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
	
	public static void deleteModificationRequestOnBlock(String friendId,UserMaster usermaster){
		
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx=null;
			tx = session.beginTransaction();
			org.hibernate.Query query=session.createQuery("DELETE FROM ModificationRequest WHERE (createdBy =:createdBy AND forUser =:forUser) OR "
					+ "(createdBy =:createdBy1 AND forUser =:forUser1)");
			query.setParameter("createdBy", usermaster.getUserId());
			query.setParameter("createdBy1", Long.parseLong(friendId));
			query.setParameter("forUser",  friendId);
			query.setParameter("forUser1",""+usermaster.getUserId());
			query.executeUpdate();
			tx.commit();
						
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		
	}

	public static void main(String [] arg){
//		deleteModificationRequest();
		UserMaster user = new UserMaster();
		user.setUserId(Long.parseLong("4"));
		deleteModificationRequestOnBlock(String.valueOf(2),user);
	}
	
	
}

