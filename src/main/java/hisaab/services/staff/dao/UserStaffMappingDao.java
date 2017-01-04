package hisaab.services.staff.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.services.contacts.modal.Contact;
import hisaab.services.staff.modal.StaffUser;
import hisaab.services.staff.modal.UserStaffMapping;

public class UserStaffMappingDao {

	public static boolean addUserStaffMapping(UserStaffMapping usm){
		
		Session session = null;
		Transaction tx = null;
		boolean flag = false;
		
		long epoch = System.currentTimeMillis();
		UserStaffMapping existingUsm;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserStaffMapping where contactNo = :contact and staffId = :staff"
					+ " and userId = :user";
//			+" AND Status = :stat ";
			Query query = session.createQuery(hql);
			query.setParameter("contact", usm.getContactNo());
			query.setParameter("staff",usm.getStaffId());
			query.setParameter("user", usm.getUserId());
			if(query.list().size()>0){
				tx = session.beginTransaction();
				existingUsm = (UserStaffMapping) query.list().get(0);
				if(existingUsm.getStatus() != 1){
					String updateHql = "Update UserStaffMapping set status = :status, updatedTime = :time "
							+ " where id = :id ";
					
					Query updateQuery = session.createQuery(updateHql);
					updateQuery.setParameter("status", 1);
					updateQuery.setParameter("time", epoch);
					updateQuery.setParameter("id", existingUsm.getId());
					updateQuery.executeUpdate();
				}
						
			}
			else{
				usm.setCreatedTime(epoch);
				tx = session.beginTransaction();
				session.save(usm);
			}

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
	
	/**
	 * getStaffMapping by staff Id
	 **/
	public static UserStaffMapping getActiveUserMapping(String staffId) {
		Session session = null;
		Transaction tx = null;

		UserStaffMapping userStaffMapping = null;
		
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "from UserStaffMapping where staffId = :staff and status = :status ";
			Query query = session.createQuery(hql);
			query.setParameter("staff", staffId);
			query.setParameter("status", 1);
			
			if(query.list().size()>0){
				userStaffMapping = (UserStaffMapping) query.list().get(0);
			}
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return userStaffMapping;
	}

}
