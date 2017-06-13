package hisaab.services.viewlog.dao;

import java.util.ArrayList;
import java.util.List;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.services.user.modal.UserProfile;
import hisaab.services.viewlog.modal.TransactionLog;
import hisaab.util.Constants;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class TransactionLogDao {

	public static void saveTransactionLog(TransactionLog transLog) {
		Session session = null;
		Transaction tx = null;
		long total = 0;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx =session.beginTransaction();
			session.save(transLog);
			
			if (!tx.wasCommitted())
				tx.commit();
			
			} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			if(tx != null)
				tx.rollback();
				
			
		} finally {
			
			session.close();
		}
		
	}
	public static List<TransactionLog> getTransactionLog(int start,
			int length, String ord, int order) {
		Session session = null;
		Transaction tx = null;

		ArrayList<TransactionLog> transLog = new ArrayList<TransactionLog>();
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(TransactionLog.class);

			switch(order){
			case 0:{
			criteria.addOrder(Order.desc("createdTime"));break;
			}
			case 1:{
				if(ord.equals("desc")){
				criteria.addOrder(Order.desc("contactNo"));
				}
				else if(ord.equals("asc")){
					criteria.addOrder(Order.asc("contactNo"));
				}
				break;
			}
			case 2:{
				if(ord.equals("desc")){
					criteria.addOrder(Order.desc("contactName"));
					}
					else if(ord.equals("asc")){
						criteria.addOrder(Order.asc("contactName"));
					}
					break;
				
			}
			
			case 3:{
				if(ord.equals("desc")){
					criteria.addOrder(Order.desc("transType"));
					}
					else if(ord.equals("asc")){
						criteria.addOrder(Order.asc("transType"));
					}
					break;
				
			}
			case 4:{
				if(ord.equals("desc")){
					criteria.addOrder(Order.desc("createdTime"));
					}
					else if(ord.equals("asc")){
						criteria.addOrder(Order.asc("createdTime"));
					}
					break;
				
			}
			
			
			}
			criteria.setFirstResult(start);
			criteria.setMaxResults(length);

			if (criteria.list().size() > 0) {
				transLog = (ArrayList<TransactionLog>) criteria.list();
			} 
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return transLog;
	}

	public static long getCountUserDetailTransCount() {
		Session session = null;
		Transaction tx = null;
		long total = 0;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			total = (Long) session.createQuery("select count(*) from  TransactionLog")
		            .uniqueResult();
			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return total;
	}
	public static int clearTransactionLog() {
		Session session = null;
		Transaction tx = null;
		int status = 0;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = "delete From TransactionLog where contactNo IN (:contacts)" ;
			Query  query = session.createQuery(hql);
			query.setParameterList("contacts", Constants.testingContact);
			status = query.executeUpdate();
			tx.commit();
			System.out.println("<> "+status);
			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return status;
	}
	public static void main(String[] arg){
		System.out.println(";;;"+clearTransactionLog());
		
	}
}
