package hisaab.util;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.config.morphia.MorphiaDatastoreTrasaction;
import hisaab.services.contacts.modal.ContactList;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.transaction.staff_transaction.modal.StaffTransactionDoc;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mongodb.morphia.Datastore;

public class DeleteDB {

	public static boolean deleteDataBase() {
		Session session = null;
		Transaction tx = null;
		boolean flag = false;
	try{
		try {
				session = HibernateUtil.getSessionFactory().openSession();
				tx = session.beginTransaction();
				 Query query = session.createSQLQuery("truncate table user_master");
				 Query query1 = session.createSQLQuery("truncate table user_profile");
				 Query query2 = session.createSQLQuery("truncate table user_request");
				 Query query3 = session.createSQLQuery("truncate table user_image");
				 Query query4 = session.createSQLQuery("truncate table transaction_table");
				 Query query5 = session.createSQLQuery("truncate table private_user");
				 Query query6 = session.createSQLQuery("truncate table staff_user_request");
				 Query query7 = session.createSQLQuery("truncate table staff_user");
				 Query query8 = session.createSQLQuery("truncate table staff_profile");
				 Query query9 = session.createSQLQuery("truncate table sms_table");
				 Query query10 = session.createSQLQuery("truncate table modification_request");
				 Query query11 = session.createSQLQuery("truncate table del_staff_trans");
				 Query query12 = session.createSQLQuery("truncate table user_staff_mapping");
				 
				 query.executeUpdate();
				 query1.executeUpdate();
				 query2.executeUpdate();
				 query3.executeUpdate();
				 query4.executeUpdate();
				 query5.executeUpdate();
				 query6.executeUpdate();
				 query7.executeUpdate();
				 query8.executeUpdate();
				 query9.executeUpdate();
				 query10.executeUpdate();
				 query11.executeUpdate();
				 query12.executeUpdate();
				 tx.commit();
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(StaffTransactionDoc.class);
		datastore.delete(datastore.createQuery(StaffTransactionDoc.class));
		Datastore datastore1 = MorphiaDatastoreTrasaction.getDatastore(TransactionDoc.class);
		datastore.delete(datastore1.createQuery(TransactionDoc.class));
		Datastore datastore2 = MorphiaDatastoreTrasaction.getDatastore(FriendList.class);
		datastore.delete(datastore2.createQuery(FriendList.class));
		Datastore datastore3 = MorphiaDatastoreTrasaction.getDatastore(ContactList.class);
		datastore.delete(datastore3.createQuery(ContactList.class));
		flag= true;
	}catch(Exception e){
		e.printStackTrace();
	}return flag;
	}
}
