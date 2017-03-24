package hisaab.services.appVersion.dao;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.services.appVersion.modal.AppVersion;
import hisaab.services.user.modal.UserMaster;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

public class AppVersionDao {

	public static List<AppVersion> getAppVersionUpdate(int appVersion) {
		Session session = null;
		List<AppVersion> appVersionList = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(AppVersion.class);
			criteria.add(Restrictions.gt("currentVersion", appVersion));
			appVersionList = criteria.list();
	          		
			
		}
		catch (Exception e) {
			System.out.println("Exception = "+e.getMessage());
			e.printStackTrace();
		}finally{
			session.close();
		}
		return appVersionList;
	}

	public static void setAppVersionDetail(AppVersion appVersion) {
		Session session = null;
		Transaction tx = null;
		
		
		try {
				session = HibernateUtil.getSessionFactory().openSession();
			
				tx = session.beginTransaction();
				appVersion.setCreatedTime(System.currentTimeMillis());
				session.save(appVersion);
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
