package hisaab.services.transaction.staff_transaction.dao;

import java.util.List;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.services.transaction.request.modal.ModificationRequest;
import hisaab.services.transaction.staff_transaction.modal.DeletedStaffTransaction;
import hisaab.services.user.modal.UserMaster;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class DeletedStaffTransactionDao {

	
	public static void addDeleteStaffTransId(DeletedStaffTransaction dst) {
		Session session = null;
		Transaction tx = null;
		long epoch = System.currentTimeMillis();
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			dst.setCreatedTime(epoch);
			
			session.save(dst);
			tx.commit();
			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
		} finally {
			session.close();
		}
	}

	public static List<String> pullDeletedTransactionId(String Id,
			long pullTime, boolean isOwner) {
		List<String> transactionId = null;
		Session session = null;
		Transaction tx = null;
		String str = "";
		long epoch = System.currentTimeMillis();
		try {
			
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(DeletedStaffTransaction.class);
			
			if(isOwner)
				criteria.add(Restrictions.eq("ownerId", Id));
			else
				criteria.add(Restrictions.eq("staffId", Id));
			criteria.add(Restrictions.gt("createdTime",pullTime));
			Projection p1 = Projections.property("transactionId");
			criteria.setProjection(p1);

			List<String> requests = criteria.list();
			
			if(requests != null && !requests.isEmpty()){
				transactionId = requests;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		return transactionId;
	}
}
