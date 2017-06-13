package hisaab.services.viewlog.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.services.user.modal.UserProfile;
import hisaab.util.Constants;

public class UserLogDao {

	public static List<UserProfile> getUserDetail(int start, int length, String ord, int order) {
		Session session = null;
		Transaction tx = null;

		ArrayList<UserProfile> invList = new ArrayList<UserProfile>();
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(UserProfile.class);
//			criteria.add(Restrictions.eq("delFlag", 0));
			criteria.add(Restrictions.eq("userType", 0));
			switch(order){
			case 0:{
			criteria.addOrder(Order.desc("createdTime"));break;
			}
			case 1:{
				if(ord.equals("desc")){
				criteria.addOrder(Order.desc("userName"));
				}
				else if(ord.equals("asc")){
					criteria.addOrder(Order.asc("userName"));
				}
				break;
			}
			case 2:{
				if(ord.equals("desc")){
					criteria.addOrder(Order.desc("contactNo"));
					}
					else if(ord.equals("asc")){
						criteria.addOrder(Order.asc("contactNo"));
					}
					break;
				
			}
			
			case 3:{
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
				invList = (ArrayList<UserProfile>) criteria.list();
			} 
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return invList;
	}

	public static long getCountUserDetail() {
		Session session = null;
		Transaction tx = null;
		long total = 0;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			total = (Long) session.createQuery("select count(*) from  UserProfile where userType = 0 ")
		            .uniqueResult();
			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return total;
	}

	public static List<UserProfile> getUserDetailAll(int start, int length,
			String ord, int order) {
		Session session = null;
		Transaction tx = null;

		ArrayList<UserProfile> userprofile = new ArrayList<UserProfile>();
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(UserProfile.class);

			criteria.add(Restrictions.not(Restrictions.in("contactNo", Constants.testingContact)));
			switch(order){
			case 0:{
			criteria.addOrder(Order.desc("createdTime"));break;
			}
			case 1:{
				if(ord.equals("desc")){
				criteria.addOrder(Order.desc("userName"));
				}
				else if(ord.equals("asc")){
					criteria.addOrder(Order.asc("userName"));
				}
				break;
			}
			case 2:{
				if(ord.equals("desc")){
					criteria.addOrder(Order.desc("contactNo"));
					}
					else if(ord.equals("asc")){
						criteria.addOrder(Order.asc("contactNo"));
					}
					break;
				
			}
			
			case 3:{
				if(ord.equals("desc")){
				criteria.addOrder(Order.desc("createdTime"));
				}
				else if(ord.equals("asc")){
					criteria.addOrder(Order.asc("createdTime"));
				}
				break;
				
			}
			case 4:{
				if(ord.equals("desc")){
				criteria.addOrder(Order.desc("transactionCount"));
				}
				else if(ord.equals("asc")){
					criteria.addOrder(Order.asc("transactionCount"));
				}
				break;
				
			}
			
			
			}
			criteria.setFirstResult(start);
			criteria.setMaxResults(length);

			if (criteria.list().size() > 0) {
				userprofile = (ArrayList<UserProfile>) criteria.list();
			} 
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return userprofile;
	}

	public static long getCountUserDetailAll() {
		Session session = null;
		Transaction tx = null;
		long total = 0;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			total = (Long) session.createQuery("select count(*) from  UserProfile")
		            .uniqueResult();
			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return total;
	}

	public static List<UserProfile> getActiveUserDetailAll(int start,
			int length, String ord, int order) {
		Session session = null;
		Transaction tx = null;

		ArrayList<UserProfile> userprofile = new ArrayList<UserProfile>();
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(UserProfile.class);
//			criteria.add(Restrictions.eq("delFlag", 0));
			criteria.add(Restrictions.gt("transactionCount", Long.parseLong("0")));
			criteria.add(Restrictions.not(Restrictions.in("contactNo", Constants.testingContact)));
			switch(order){
			case 0:{
			criteria.addOrder(Order.desc("createdTime"));break;
			}
			case 1:{
				if(ord.equals("desc")){
				criteria.addOrder(Order.desc("userName"));
				}
				else if(ord.equals("asc")){
					criteria.addOrder(Order.asc("userName"));
				}
				break;
			}
			case 2:{
				if(ord.equals("desc")){
					criteria.addOrder(Order.desc("contactNo"));
					}
					else if(ord.equals("asc")){
						criteria.addOrder(Order.asc("contactNo"));
					}
					break;
				
			}
			
			case 3:{
				if(ord.equals("desc")){
				criteria.addOrder(Order.desc("createdTime"));
				}
				else if(ord.equals("asc")){
					criteria.addOrder(Order.asc("createdTime"));
				}
				break;
				
			}
			case 4:{
				if(ord.equals("desc")){
				criteria.addOrder(Order.desc("transactionCount"));
				}
				else if(ord.equals("asc")){
					criteria.addOrder(Order.asc("transactionCount"));
				}
				break;
				
			}
			
			
			}
			criteria.setFirstResult(start);
			criteria.setMaxResults(length);

			if (criteria.list().size() > 0) {
				userprofile = (ArrayList<UserProfile>) criteria.list();
			} 
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return userprofile;
	}

	public static long getCountUserDetailTransCount() {
		Session session = null;
		Transaction tx = null;
		long total = 0;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			total = (Long) session.createQuery("select count(*) from  UserProfile where transactionCount >0")
		            .uniqueResult();
			
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return total;
	}
}
