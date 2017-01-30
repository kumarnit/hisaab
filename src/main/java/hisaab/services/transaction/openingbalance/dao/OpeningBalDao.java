package hisaab.services.transaction.openingbalance.dao;



import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.notification.NotificationHelper;
import hisaab.services.pull.helper.PullDocDao;
import hisaab.services.pull.modal.PullDoc;
import hisaab.services.transaction.openingbalance.modal.OpeningBalRequest;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;

public class OpeningBalDao {

	public static boolean addOpeningBalRequest(UserMaster user, OpeningBalRequest obr){
		Session session = null;
		boolean resFlag = false;
		Transaction tx = null;
		String str = "";
		long epoch = System.currentTimeMillis();
		OpeningBalRequest openingBalance = null;
		try {
			
			session =  HibernateUtil.getSessionFactory().openSession();
			
		
			Criteria criteria = session.createCriteria(OpeningBalRequest.class);
 	        criteria.add(
 	        		Restrictions.or(
 	        				Restrictions.and(
 	        						Restrictions.eq("requesterUserId", ""+user.getUserId()),Restrictions.eq("forUserId", ""+obr.getForUserId())),
 	        				Restrictions.and(
 	        						Restrictions.eq("requesterUserId",""+obr.getForUserId() ),Restrictions.eq("forUserId", ""+user.getUserId()))));
 	        		System.out.println("oo:  =>" +criteria.list().size()); 		
 	        		tx = session.beginTransaction();
 	        if(criteria.list().size()>0){
 	        	 openingBalance = (OpeningBalRequest) criteria.list().get(0);
 	        	criteria.add(Restrictions.ne("status", 0));
 	        	if(!criteria.list().isEmpty()){
 	        		openingBalance = (OpeningBalRequest) criteria.list().get(0);
 	        		obr.setRequesterUserId(""+user.getUserId());
 	        		obr.setCreatedTime(epoch);
 	        		String hql = "Update OpeningBalRequest set status = :userrespo, updatedTime = :updatedDate "
 	        				+ ",requesterUserId = :requesterUserId , forUserId = :forUserId, openingBalAmt = :openingBalAmt"
 	        				+ ",paymentStatus = :paymentStatus, openingBalDate = :openingBalDate, createdTime = :createdTime"
 	   					+ " where id = :ids ";
 	        		Query query = session.createQuery(hql);
 	        		query.setParameter("userrespo", obr.getStatus());
 	        		query.setParameter("updatedDate", epoch);
 	        		query.setParameter("forUserId", obr.getForUserId());
 	        		query.setParameter("ids", openingBalance.getId() );
 	        		obr.setId(openingBalance.getId());
 	        		obr.setUpdatedTime(epoch);
 	        		query.setParameter("requesterUserId", obr.getRequesterUserId());
 	        		query.setParameter("openingBalAmt", obr.getOpeningBalAmt());
 	        		query.setParameter("paymentStatus", obr.getPaymentStatus());
 	        		query.setParameter("openingBalDate", obr.getOpeningBalDate());
 	        		query.setParameter("createdTime", epoch);
 	        		int i = query.executeUpdate();
 	        		if(i>0){
 	   				resFlag = true;
 	        		}
 	        	}
 	        }else{
 	        	obr.setRequesterUserId(""+user.getUserId());
 	        	obr.setCreatedTime(epoch);
 	        	session.save(obr);
 	        	resFlag = true;
 	        }
    		tx.commit();
			
			
			
			
			/***
			 * Notification Message
			 **/
    		if(resFlag){
    			
			FriendContact frnd = FriendsDao.getFriendForWeb(obr.getForUserId(), 0, user);
			
			
			String msg = "";
			if(frnd != null)
			{
				
				PullDoc pullDoc = new PullDoc();
				pullDoc.setUserId(frnd.getFrndId());
//				pullDoc = PullDocDao.getPullDoc(pullDoc);
//				PullDocDao.addAndUpadteOpeningBalanceRequest(obr,pullDoc);
				
				
				if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
					msg = frnd.getContactName();
				else if(user.getUserProfile().getUserName() != null && 
						!user.getUserProfile().getUserName().isEmpty())
					msg = user.getUserProfile().getUserName();
				else
					msg = user.getContactNo();
			}
	
			msg +=" has updated opening balance for transactions with you.";
			System.out.println("==///=="+msg);
			NotificationHelper.buildAndSendOpeningBalRequestNotification(obr, msg);
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		return resFlag;
	}

	
	public static OpeningBalRequest getOpeningBalRequest(long reqId, int response, UserMaster user){
		Session session = null;
		OpeningBalRequest obr = null;
		
		try {
			
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(OpeningBalRequest.class);
			criteria.add(Restrictions.eq("id", reqId));
			criteria.add(Restrictions.eq("forUserId", ""+user.getUserId()));
			if(!criteria.list().isEmpty()){
				obr = (OpeningBalRequest)criteria.list().get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		
		return obr;
	}


	public static boolean updateOpeningBalRequest(UserMaster user,
			OpeningBalRequest req, int userResponse) {
		// TODO Auto-generated method stub
		Session session = null;
		boolean resFlag = false;
		Transaction tx = null;
		String str = "";
		long epoch = System.currentTimeMillis();
		try {
			
			session =  HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = "Update OpeningBalRequest set status = :userrespo, updatedTime = :updatedDate "
					+ "where forUserId = :userId AND id = :ids ";
			Query query = session.createQuery(hql);
			query.setParameter("userrespo", userResponse);
			query.setParameter("updatedDate", epoch);
			query.setParameter("userId", req.getForUserId());
			query.setParameter("ids",req.getId() );
			int i = query.executeUpdate();
			req.setUpdatedTime(epoch);
			req.setStatus(userResponse);
			if(i>0){
				resFlag = true;
			}
			tx.commit();
			
			/***
			 * Notification Message
			 **/
			UserMaster userB = UserDao.getUserForWeb(Long.parseLong(req.getRequesterUserId()));
			FriendContact frnd = FriendsDao.getFriendForWeb(""+user.getUserId(), 0,userB );
			String msg = "";
			if(frnd != null)
			{
				
				if(resFlag){
					PullDoc pullDoc = new PullDoc();
					pullDoc.setUserId(""+userB.getUserId());
//					pullDoc = PullDocDao.getPullDoc(pullDoc);
//					PullDocDao.UpadteOpeningBalanceResponse(req,pullDoc,userResponse);
//					PullDoc pullDoc2 = new PullDoc();
//					pullDoc2.setUserId(""+user.getUserId());
//					pullDoc2 = PullDocDao.getPullDoc(pullDoc2);
//					PullDocDao.UpadteOpeningBalanceResponse(req,pullDoc,userResponse);
								
					}
				
				if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
					msg = frnd.getContactName();
				else if(user.getUserProfile().getUserName() != null && 
						!user.getUserProfile().getUserName().isEmpty())
					msg = user.getUserProfile().getUserName();
				else
					msg = user.getContactNo();
			}
			if(userResponse == 1){
			msg +=" Has approved Opening Balance";
			}else if (userResponse == 2){
				msg +=" Has Rejected Opening Balance";
			}
			
			System.out.println("==///=="+msg);
			NotificationHelper.buildAndSendOpeningBalanceResponse(userB.getUserId(), req, msg);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		return resFlag;
	}


	public static void deleteOpeningBalOnBlock(String frndId,
			UserMaster usermaster) {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx=null;
			tx = session.beginTransaction();
			org.hibernate.Query query=session.createQuery("DELETE FROM OpeningBalRequest WHERE (requesterUserId =:requesterUserId AND forUserId =:forUserId) OR "
					+ "(requesterUserId =:requesterUserId1 AND forUserId =:forUserId1)");
			query.setParameter("requesterUserId", ""+usermaster.getUserId());
			query.setParameter("forUserId", frndId);
			query.setParameter("requesterUserId1", frndId);
			query.setParameter("forUserId1", ""+usermaster.getUserId());
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
//		deleteOpeningBalOnBlock(String.valueOf(1),user);
		System.out.println("-- "+pullOpeningBalance(user,
				Long.parseLong("0")).toString());
	}


	public static List<OpeningBalRequest> pullOpeningBalance(UserMaster user,
			long pullTime) {
		Session session = null;
		List<OpeningBalRequest> openingBalance = new ArrayList<OpeningBalRequest>();
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx=null;
			tx = session.beginTransaction();
			String hql = "FROM OpeningBalRequest WHERE (requesterUserId =:requesterUserId OR forUserId =:forUserId)";
			if(pullTime > 0)
				hql += " AND updatedTime > :pullTIme";
			Query query = session.createQuery(hql);
			query.setParameter("requesterUserId", ""+user.getUserId());
			query.setParameter("forUserId", ""+user.getUserId());
//			query.setParameter("status", 0);
			if(pullTime > 0)
				query.setParameter("pullTIme", pullTime);
			if(query.list() !=null && !query.list().isEmpty()){
				openingBalance = query.list();
			}
			tx.commit();
						
		} catch (Exception e) {
			System.out.println("Exception = " + e.getMessage());
			
			e.printStackTrace();
			
		} finally {
			session.close();
		}
		return openingBalance;
	}
}
