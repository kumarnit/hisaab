package hisaab.services.transaction.clear_transaction.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hisaab.config.hibernate.HibernateUtil;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.notification.NotificationHelper;
import hisaab.services.transaction.clear_transaction.modal.ClearTransactionRequest;
import hisaab.services.transaction.openingbalance.modal.OpeningBalRequest;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

public class ClearTransactionRequestDao {

	
	
	public static boolean addClearTransactionRequest(UserMaster user, ClearTransactionRequest ctr){
		Session session = null;
		boolean resFlag = false;
		Transaction tx = null;
		String str = "";
		long epoch = System.currentTimeMillis();
		ClearTransactionRequest clearTransReq = null;
		try {
			
			session =  HibernateUtil.getSessionFactory().openSession();
			
		
			Criteria criteria = session.createCriteria(ClearTransactionRequest.class);
 	        criteria.add(
 	        		Restrictions.or(
 	        				Restrictions.and(
 	        						Restrictions.eq("requesterUserId", ""+user.getUserId()),Restrictions.eq("forUserId", ""+ctr.getForUserId())),
 	        				Restrictions.and(
 	        						Restrictions.eq("requesterUserId",""+ctr.getForUserId() ),Restrictions.eq("forUserId", ""+user.getUserId()))));
 	        		System.out.println("oo:  =>" +criteria.list().size()); 		
 	        		tx = session.beginTransaction();
 	        if(criteria.list().size()>0){
 	        	 clearTransReq = (ClearTransactionRequest) criteria.list().get(0);
 	        	criteria.add(Restrictions.eq("status", 0));
 	        	if(criteria.list().isEmpty()){
 	        		ctr.setRequesterUserId(""+user.getUserId());
 	 	        	ctr.setCreatedTime(epoch);
 	 	        	ctr.setUpdatedTime(epoch);
 	 	        	session.save(ctr);
 	 	        	resFlag = true;
 	        	}
 	        	else{
 	        		ctr = (ClearTransactionRequest)criteria.list().get(0);
 	        	}
 	        }else{
 	        	ctr.setRequesterUserId(""+user.getUserId());
 	        	ctr.setCreatedTime(epoch);
 	        	session.save(ctr);
 	        	resFlag = true;
 	        }
    		tx.commit();
			
			
			
			
			/***
			 * Notification Message
			 **/
    		if(resFlag){
    			UserMaster userB = new UserMaster();
    			userB.setUserId(Long.parseLong(ctr.getForUserId()));
			FriendContact frnd = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, userB);
			
			
			String msg = "";
			if(frnd != null)
			{
				if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
					msg = frnd.getContactName();
				else if(user.getUserProfile().getUserName() != null && 
						!user.getUserProfile().getUserName().isEmpty())
					msg = user.getUserProfile().getUserName();
				else
					msg = user.getContactNo();
			}
	
			msg +=" has requested to clear transactions with you.";
			System.out.println("==///=="+msg);
			NotificationHelper.buildAndSendClearTransactionRequestNotification(ctr, msg);
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		return resFlag;
	}

	
	public static ClearTransactionRequest getExistingClearTransactionRequest( ClearTransactionRequest ctr){
		Session session = null;
		
		ClearTransactionRequest clearTransReq = null;
		try{
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ClearTransactionRequest.class);
 	        criteria.add(
 	        		Restrictions.or(
 	        				Restrictions.and(
 	        						Restrictions.eq("requesterUserId", ctr.getRequesterUserId()),Restrictions.eq("forUserId", ""+ctr.getForUserId())),
 	        				Restrictions.and(
 	        						Restrictions.eq("requesterUserId",""+ctr.getForUserId() ),Restrictions.eq("forUserId", ctr.getRequesterUserId()))));
 	        	criteria.add(Restrictions.eq("status", 0));
 	        	if(!criteria.list().isEmpty()){
 	        		clearTransReq = (ClearTransactionRequest)criteria.list().get(0);
 	        	}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in :"+e);
		}finally{
			if(session != null)
				session.close();
		}
				return clearTransReq;
	}

	
	public static ClearTransactionRequest getClearTransRequest(long reqId, int response, UserMaster user){
		Session session = null;
		ClearTransactionRequest obr = null;
		try {
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ClearTransactionRequest.class);
			criteria.add(Restrictions.eq("reqId", reqId));
			criteria.add(Restrictions.eq("forUserId", ""+user.getUserId()));
			if(!criteria.list().isEmpty()){
				obr = (ClearTransactionRequest)criteria.list().get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		
		return obr;
	}

	
	
	
	public static boolean updateClearTransactionRequest(UserMaster user,
			ClearTransactionRequest req, int userResponse) {
		// TODO Auto-generated method stub
		Session session = null;
		boolean resFlag = false;
		Transaction tx = null;
		String str = "";
		long epoch = System.currentTimeMillis();
		try {
			
			session =  HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = "Update ClearTransactionRequest set status = :userrespo, updatedTime = :updatedDate, "
					+ "openingBalAmt = :opBalAmt, openingBalDate = :opBalDate where forUserId = :userId AND reqId = :ids ";
			Query query = session.createQuery(hql);
			query.setParameter("userrespo", userResponse);
			query.setParameter("updatedDate", epoch);
			query.setParameter("opBalAmt", req.getOpeningBalAmt());
			query.setParameter("opBalDate", req.getOpeningBalDate());
			query.setParameter("userId", req.getForUserId());
			query.setParameter("ids",req.getReqId() );
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
			FriendContact frnd = FriendsDao.getFriendForWeb(""+req.getForUserId(), 0,userB );
			String msg = "";
			if(frnd != null)
			{
				if(frnd.getContactName() != null && !frnd.getContactName().isEmpty())
					msg = frnd.getContactName();
				else if(user.getUserProfile().getUserName() != null && 
						!user.getUserProfile().getUserName().isEmpty())
					msg = user.getUserProfile().getUserName();
				else
					msg = user.getContactNo();
			}
			if(userResponse == 111){
			msg +=" Has approved Clear Transaction Request";
			}else if (userResponse == Constants.ACTION_REJECTED){
				msg +=" Has Rejected ClearTransaction Request";
			}
			
			System.out.println("==///=="+msg);
			NotificationHelper.buildAndSendClearTransactionResponseNotification( req, msg);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		return resFlag;
	}

	
	/***
	 * For System auto approval purpose.
	 * **/
	public static boolean updateClearTransactionRequestForAutoApproval(ClearTransactionRequest req,
			int userResponse) {

		Session session = null;
		boolean resFlag = false;
		Transaction tx = null;
		String str = "";
		long epoch = System.currentTimeMillis();
		try {
			
			session =  HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			String hql = "Update ClearTransactionRequest set status = :userrespo, updatedTime = :updatedDate, "
					+ "openingBalAmt = :opBalAmt, openingBalDate = :opBalDate where forUserId = :userId AND reqId = :ids ";
			Query query = session.createQuery(hql);
			query.setParameter("userrespo", userResponse);
			query.setParameter("updatedDate", epoch);
			query.setParameter("opBalAmt", req.getOpeningBalAmt());
			query.setParameter("opBalDate", req.getOpeningBalDate());
			query.setParameter("userId", req.getForUserId());
			query.setParameter("ids",req.getReqId() );
			int i = query.executeUpdate();
			req.setUpdatedTime(epoch);
			req.setStatus(userResponse);
			if(i>0){
				resFlag = true;
			}
			tx.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		return resFlag;
	}
	
	
	
	
	public static List<ClearTransactionRequest> getClearTransRequestListForNotifying(){
		Session session = null;
		List<ClearTransactionRequest> obr = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -5);
			long timeLimit = cal.getTimeInMillis();
			
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ClearTransactionRequest.class);
			criteria.add(Restrictions.gt("createdTime", timeLimit));
			criteria.add(Restrictions.eq("status", 0));
			if(!criteria.list().isEmpty()){
				
				obr = criteria.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		
		return obr;
	}


	public static List<ClearTransactionRequest> getClearTransRequestListForAutoAprove(){
		Session session = null;
		List<ClearTransactionRequest> obr = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -4);
			long timeLimit = cal.getTimeInMillis();
			
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ClearTransactionRequest.class);
			criteria.add(Restrictions.lt("createdTime", timeLimit));
			criteria.add(Restrictions.eq("status", 0));
			if(!criteria.list().isEmpty()){
				
				obr = criteria.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		
		return obr;
	}

	
	public static List<ClearTransactionRequest> pullClearTransRequestList(long pullTime, long userId){
		Session session = null;
		List<ClearTransactionRequest> obr = null;
		try {
		
			
			
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ClearTransactionRequest.class);
			criteria.add(Restrictions.ge("updatedTime", pullTime));
			criteria.add(
	 	        		Restrictions.or(
	 	        						Restrictions.eq("requesterUserId", ""+userId),
	 	        						Restrictions.eq("forUserId",""+userId ))
	 	        						);
			if(!criteria.list().isEmpty()){
				
				obr = criteria.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		
		return obr;
	}

	
	public static List<ClearTransactionRequest> pullAllClearTransactionRequestForusers(String userId1, String userId2){
		Session session = null;
		
		List<ClearTransactionRequest> obr = new ArrayList<ClearTransactionRequest>();
		try{
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ClearTransactionRequest.class);
 	        criteria.add(
 	        		Restrictions.or(
 	        				Restrictions.and(
 	        						Restrictions.eq("requesterUserId", userId1),Restrictions.eq("forUserId", userId2)),
 	        				Restrictions.and(
 	        						Restrictions.eq("requesterUserId",""+userId2 ),Restrictions.eq("forUserId", userId1))));
 	        	if(!criteria.list().isEmpty()){
 	        		obr = criteria.list();
 	        	}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in :"+e);
		}finally{
			if(session != null)
				session.close();
		}
		
				return obr;
	}
	
	
}
