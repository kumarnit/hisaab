package hisaab.services.transaction.request.dao;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
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
	public static ModificationRequest getModificationRequest1(String transacId, UserMaster user, long reqId){
		ModificationRequest modreq = null;
		Session session = null;
		Transaction tx = null;
		String str = "";
		long epoch = System.currentTimeMillis();
		try {
			
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ModificationRequest.class);
			
			criteria.add(Restrictions.eq("transactionId", transacId));
			criteria.add(Restrictions.eq("id",reqId));
			criteria.add(Restrictions.ne("status",0));
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
//			criteria.add(Restrictions.ne("status", 0));
			criteria.add(Restrictions.gt("updatedTime",pullTime));
			if(pullTime == 0)
				criteria.add(Restrictions.eq("status", 0));
//			criteria.add(Restrictions.eq("action", Constants.TRANSACTION_DELETE));
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

/**
 * delete transactions by transaction ids
 **/	
	public static void deleteModificationRequestByTransId(List<String> transIds){
		
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx=null;
			tx = session.beginTransaction();
		
			org.hibernate.Query query=session.createQuery("DELETE from ModificationRequest where transactionId in (:transIds) ");
			query.setParameterList("transIds", transIds);
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
//		deleteModificationRequestOnBlock(String.valueOf(2),user);
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("ii : "+mapper.writeValueAsString(getAllUpdatePendingTransactionIdForNotification()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}


	public static List<String> getAllUpdatePendingTransactionId() {
		List<String> requests = null;
		Session session = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -5);
			System.out.println(""+cal.getTimeInMillis());
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ModificationRequest.class);
			
			criteria.add(Restrictions.lt("createdTime", cal.getTimeInMillis()));
			criteria.add(Restrictions.eq("status",0));
			criteria.add(Restrictions.eq("action",Constants.TRANSACTION_UPDATE));
			ProjectionList proList = Projections.projectionList();
	        proList.add(Projections.property("transactionId"));
	        criteria.setProjection(proList);
			requests = criteria.list();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		return requests;
		
	}


	public static Map<String,ModificationRequest> getUpdatePendingRequest() {
		Map<String,ModificationRequest> requests = new HashMap<String,ModificationRequest>();
		Session session = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -5);
			System.out.println(""+cal.getTimeInMillis());
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ModificationRequest.class);
			
			criteria.add(Restrictions.lt("createdTime", cal.getTimeInMillis()));
			criteria.add(Restrictions.eq("status",0));
			criteria.add(Restrictions.eq("action",Constants.TRANSACTION_UPDATE));
			if(!criteria.list().isEmpty()){
				Iterator<ModificationRequest> modReq = criteria.list().iterator();
				ModificationRequest temp = null ;
				while(modReq.hasNext()){
					temp = modReq.next(); 
					requests.put(temp.getTransactionId(), temp);
				}
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		return requests;
	}
	
	public static List<String> getAllDeletePendingTransactionId() {
		List<String> requests = null;
		Session session = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, -5);
			System.out.println(""+cal.getTimeInMillis());
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ModificationRequest.class);
			
			criteria.add(Restrictions.lt("createdTime", cal.getTimeInMillis()));
			criteria.add(Restrictions.eq("status",0));
			criteria.add(Restrictions.eq("action",Constants.TRANSACTION_DELETE));
			ProjectionList proList = Projections.projectionList();
	        proList.add(Projections.property("transactionId"));
	        criteria.setProjection(proList);
			requests = criteria.list();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		return requests;
		
	}
	
	public static Map<String,ModificationRequest> getDeletePendingRequest() {
		Map<String,ModificationRequest> requests = new HashMap<String,ModificationRequest>();
		Session session = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, -5);
			System.out.println(""+cal.getTimeInMillis());
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ModificationRequest.class);
			
			criteria.add(Restrictions.lt("createdTime", cal.getTimeInMillis()));
			criteria.add(Restrictions.eq("action",Constants.TRANSACTION_DELETE));
			criteria.add(Restrictions.eq("status",0));
			
			if(!criteria.list().isEmpty()){
				Iterator<ModificationRequest> modReq = criteria.list().iterator();
				ModificationRequest temp = null ;
				while(modReq.hasNext()){
					temp = modReq.next(); 
					requests.put(temp.getTransactionId(), temp);
				}
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		return requests;
	}


	public static Map<String,List<String>> getAllDeletePendingTransactionIdForNotification() {
		List<ModificationRequest> requests = null;
		Map<String,List<String>> pendingMod = new HashMap<String,List<String>>();
		Session session = null;
		ObjectMapper mapper = new ObjectMapper();
		long epoch = System.currentTimeMillis();
		try {
			Calendar endTime = Calendar.getInstance();
			endTime.add(Calendar.DAY_OF_YEAR, -6);
			Calendar startTime = Calendar.getInstance();
			startTime.add(Calendar.DAY_OF_YEAR, -1);
			System.out.println("End Time : "+endTime.getTimeInMillis());
			System.out.println("Start Time: "+startTime.getTimeInMillis());
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ModificationRequest.class);
			
//			criteria.add(Restrictions.lt("createdTime", cal.getTimeInMillis()));
			criteria.add(Restrictions.eq("status",0));
			criteria.add(Restrictions.eq("action",Constants.TRANSACTION_DELETE));
			criteria.add(Restrictions.between("createdTime",endTime.getTimeInMillis(),startTime.getTimeInMillis()));
//			
			
			requests = criteria.list();
			for(ModificationRequest modReq : requests){
				
//				ModificationRequest modreq = modReq;
				System.out.println("transId : "+modReq.getTransactionId()+"\n");
				int days = (int) TimeUnit.MILLISECONDS.toDays(epoch - modReq.getCreatedTime());
				System.out.print("Days : "+days+"\n");
				switch(days){
					case 1 :{
						System.out.println("in 1");
						List<String> modreq1 = null;
						modreq1 = pendingMod.get(String.valueOf(1));
						if(modreq1 != null && !modreq1.isEmpty()){
							modreq1.add(modReq.getForUser());
							pendingMod.put("1", modreq1);
						}else{
							modreq1 = new ArrayList<String>();
							modreq1.add(modReq.getForUser());
							pendingMod.put("1", (modreq1));
						}
						break;
					}
					case 2 :{
						System.out.println("in 1");
						List<String> modreq2 = null;
						modreq2 = pendingMod.get(String.valueOf(2));
						if(modreq2 != null && !modreq2.isEmpty()){
							modreq2.add(modReq.getForUser());
							pendingMod.put("2", modreq2);
						}else{
							modreq2 = new ArrayList<String>();
							modreq2.add(modReq.getForUser());
							pendingMod.put("2", (modreq2));
						}
						break;
					}
					case 3 :{
						System.out.println("in 1");
						List<String> modreq3 = null;
						modreq3 = pendingMod.get(String.valueOf(3));
						if(modreq3 != null && !modreq3.isEmpty()){
							modreq3.add(modReq.getForUser());
							pendingMod.put("3", modreq3);
						}else{
							modreq3 = new ArrayList<String>();
							modreq3.add(modReq.getForUser());
							pendingMod.put("3", (modreq3));
						}
						
						break;
					}
					case 4 :{
						System.out.println("in 1");
						List<String> modreq3 = null;
						modreq3 = pendingMod.get(String.valueOf(4));
						if(modreq3 != null && !modreq3.isEmpty()){
							modreq3.add(modReq.getForUser());
							pendingMod.put("4", modreq3);
						}else{
							modreq3 = new ArrayList<String>();
							modreq3.add(modReq.getForUser());
							pendingMod.put("4", (modreq3));
						}
						
						break;
					}
					case 5 :{
						System.out.println("in 1");
						List<String> modreq3 = null;
						modreq3 = pendingMod.get(String.valueOf(5));
						if(modreq3 != null && !modreq3.isEmpty()){
							modreq3.add(modReq.getForUser());
							pendingMod.put("5", modreq3);
						}else{
							modreq3 = new ArrayList<String>();
							modreq3.add(modReq.getForUser());
							pendingMod.put("5", (modreq3));
						}
						
						break;
					}
					default :{
						break;
					}
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		try {
			System.out.println("ii : "+mapper.writeValueAsString(pendingMod));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pendingMod;
	}


	public static Map<String, List<String>> getAllUpdatePendingTransactionIdForNotification() {
		List<ModificationRequest> requests = null;
		Map<String,List<String>> pendingMod = new HashMap<String,List<String>>();
		Session session = null;
		ObjectMapper mapper = new ObjectMapper();
		long epoch = System.currentTimeMillis();
		try {
			Calendar endTime = Calendar.getInstance();
			endTime.add(Calendar.DAY_OF_YEAR, -6);
			Calendar startTime = Calendar.getInstance();
			startTime.add(Calendar.DAY_OF_YEAR, -1);
			System.out.println("End Time : "+endTime.getTimeInMillis());
			System.out.println("Start Time: "+startTime.getTimeInMillis());
			session =  HibernateUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(ModificationRequest.class);
			
//			criteria.add(Restrictions.lt("createdTime", cal.getTimeInMillis()));
			criteria.add(Restrictions.eq("status",0));
			criteria.add(Restrictions.eq("action",Constants.TRANSACTION_UPDATE));
			criteria.add(Restrictions.between("createdTime",endTime.getTimeInMillis(),startTime.getTimeInMillis()));
//			
			
			requests = criteria.list();
			for(ModificationRequest modReq : requests){
				
//				ModificationRequest modreq = modReq;
				System.out.println("transId : "+modReq.getTransactionId()+"\n");
				int days = (int) TimeUnit.MILLISECONDS.toDays(epoch - modReq.getCreatedTime());
				System.out.print("Days : "+days+"\n");
				switch(days){
					case 1 :{
						System.out.println("in 1");
						List<String> modreq1 = null;
						modreq1 = pendingMod.get(String.valueOf(1));
						if(modreq1 != null && !modreq1.isEmpty()){
							modreq1.add(modReq.getForUser());
							pendingMod.put("1", modreq1);
						}else{
							modreq1 = new ArrayList<String>();
							modreq1.add(modReq.getForUser());
							pendingMod.put("1", (modreq1));
						}
						break;
					}
					case 2 :{
						System.out.println("in 1");
						List<String> modreq2 = null;
						modreq2 = pendingMod.get(String.valueOf(2));
						if(modreq2 != null && !modreq2.isEmpty()){
							modreq2.add(modReq.getForUser());
							pendingMod.put("2", modreq2);
						}else{
							modreq2 = new ArrayList<String>();
							modreq2.add(modReq.getForUser());
							pendingMod.put("2", (modreq2));
						}
						break;
					}
					case 3 :{
						System.out.println("in 1");
						List<String> modreq3 = null;
						modreq3 = pendingMod.get(String.valueOf(3));
						if(modreq3 != null && !modreq3.isEmpty()){
							modreq3.add(modReq.getForUser());
							pendingMod.put("3", modreq3);
						}else{
							modreq3 = new ArrayList<String>();
							modreq3.add(modReq.getForUser());
							pendingMod.put("3", (modreq3));
						}
						
						break;
					}
					case 4 :{
						System.out.println("in 1");
						List<String> modreq4 = null;
						modreq4 = pendingMod.get(String.valueOf(4));
						if(modreq4 != null && !modreq4.isEmpty()){
							modreq4.add(modReq.getForUser());
							pendingMod.put("4", modreq4);
						}else{
							modreq4 = new ArrayList<String>();
							modreq4.add(modReq.getForUser());
							pendingMod.put("4", (modreq4));
						}
						
						break;
					}
					case 5 :{
						System.out.println("in 1");
						List<String> modreq4 = null;
						modreq4 = pendingMod.get(String.valueOf(5));
						if(modreq4 != null && !modreq4.isEmpty()){
							modreq4.add(modReq.getForUser());
							pendingMod.put("5", modreq4);
						}else{
							modreq4 = new ArrayList<String>();
							modreq4.add(modReq.getForUser());
							pendingMod.put("5", (modreq4));
						}
						
						break;
					}
					default :{
						break;
					}
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		try {
			System.out.println("ii : "+mapper.writeValueAsString(pendingMod));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pendingMod;
	}


}

