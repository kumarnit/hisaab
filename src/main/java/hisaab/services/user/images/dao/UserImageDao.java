package hisaab.services.user.images.dao;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.services.user.images.modal.UserImage;
import hisaab.services.user.modal.UserMaster;

import org.hibernate.Session;
import org.hibernate.Transaction;


public class UserImageDao {

	public static void addNewImage(UserImage img,  UserMaster user) {
		Session session = null;
		Transaction tx = null;
		long epoch = System.currentTimeMillis();
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			img.setCreatedTime(epoch);
			img.setUserId(user.getUserId());
			session.save(img);
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

}
