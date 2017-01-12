package hisaab.services.user;



import java.util.Arrays;

import java.util.List;


import hisaab.services.contacts.dao.FriendsDao;

import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.contacts.services.bean.PrivateUserBean;
import hisaab.services.notification.NotificationHelper;
import hisaab.services.staff.dao.StaffUserDao;
import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.transaction.openingbalance.modal.OpeningBalRequest;
import hisaab.services.transaction.staff_transaction.dao.StaffTransactionDao;
import hisaab.services.transaction.webservices.bean.TransDocBean;
import hisaab.services.user.dao.PrivateUserDao;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.PrivateUser;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;

public class UserHelper {
	
	
	public static void transformUnmanagedTomanagedUser(UserMaster user){
		
		List<TransactionDoc> transactionDocs = TransactionDao.updateTransactionDocsForUnmangedUser(user);
		FriendList frndList = FriendsDao.getAssociatedUserDoc(user);
//		FriendsDao.updateFriendsDocsForUnmangedUser(user);
		updateFrndDocThread(user);
		long count = frndList.getIdCount();
		long epoch = System.currentTimeMillis();
		
		FriendContact frnd = null ;
		UserMaster fuser = null;
		
		for(TransactionDoc transDoc : transactionDocs){
			frnd = new FriendContact();
			frnd.setId(++count);
			if(transDoc.getUser1().equals(""+user.getUserId())){
				frnd.setPaymentStatus(transDoc.getPaymentStatus());
				frnd.setFrndId(transDoc.getUser2());
				try {
					fuser = UserDao.getUserForWeb(Long.parseLong(transDoc.getUser2()));	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else{
				if(transDoc.getPaymentStatus() == Constants.TO_GIVE)
					frnd.setPaymentStatus(Constants.TO_TAKE);
				else
					frnd.setPaymentStatus(Constants.TO_GIVE);
				frnd.setFrndId(transDoc.getUser1());
				try {
					fuser = UserDao.getUserForWeb(Long.parseLong(transDoc.getUser2()));	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(fuser != null){
				frnd.setContactNo(fuser.getContactNo());
			}
			frnd.setCreatedTime(epoch);
			frnd.setUpdatedTime(epoch);
			frnd.setAmount(transDoc.getAmount());
			frndList.getFriends().add(frnd);
		}

		frndList.setIdCount(count);
		if(frndList.getFriends()!=null && !frndList.getFriends().isEmpty())
		FriendsDao.addFriends(frndList);
	}
	
	
	public static PrivateUserBean blockUser(String frndId, UserMaster user){
			
		boolean flag = false;

		/**
		 * tpfc : target private friendContact  i.e. friend to be blocked.
		 * rpfc : requester private friendContact. This will be for friend user.
		 ***/
		PrivateUserBean tpfc = blockNcreatePrivateUser(frndId, user, ""+user.getUserId());
		PrivateUserBean rpfc = null;
		try {
			UserMaster tempUser = new UserMaster();
			tempUser.setUserId(Long.parseLong(frndId));
			rpfc = blockNcreatePrivateUser(""+user.getUserId(), tempUser, frndId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(tpfc.getFriendContact() != null || rpfc.getFriendContact() != null){
			System.out.println("IN tpfc and rpfc");
			TransDocBean transDocBean = TransactionDao.updateTransactionDocsAsBlocked(user, frndId);
			if(transDocBean.getStatus() == 1){
				System.out.println("IN transDocBean...");
				TransactionDoc transDoc = transDocBean.getTransDoc(); 
				OpeningBalRequest opbr = null;	
				if(tpfc!=null){
					System.out.println("IN tpfc....");
					TransactionDoc requesterTransDoc = new TransactionDoc();
					requesterTransDoc.setUser1(user.getUserId()+"");
					requesterTransDoc.setUser2(tpfc.getFriendContact().getFrndId());
					requesterTransDoc.setDocType(Constants.PRIVATE_USER);
					requesterTransDoc = TransactionDao.getTransactionDoc(requesterTransDoc);
					
					if(transDoc.getOpeningBalAmt() != 0){
						opbr = new OpeningBalRequest();
						if(transDoc.getUser1().equals(user.getUserId()+"")){
							opbr.setForUserId(user.getUserId()+"");
							opbr.setRequesterUserId(tpfc.getFriendContact().getFrndId());
							if(transDoc.getOpeningBalAmt() > 0){
								opbr.setPaymentStatus(Constants.TO_GIVE);
								opbr.setOpeningBalAmt(transDoc.getOpeningBalAmt());
							}else{
								opbr.setPaymentStatus(Constants.TO_TAKE);
								opbr.setOpeningBalAmt(transDoc.getOpeningBalAmt()*(-1));
							}
						}else{
							opbr.setForUserId(user.getUserId()+"");
							opbr.setRequesterUserId(tpfc.getFriendContact().getFrndId());
							if(transDoc.getOpeningBalAmt() > 0){
								opbr.setPaymentStatus(Constants.TO_TAKE);
								opbr.setOpeningBalAmt(transDoc.getOpeningBalAmt());
							}else{
								opbr.setPaymentStatus(Constants.TO_GIVE);
								opbr.setOpeningBalAmt(transDoc.getOpeningBalAmt()*(-1));
							}
						}
					}
										
					if(opbr!= null){
						TransactionDao.updateOpeningBalTransactionDoc(user, opbr, Constants.PRIVATE_USER);
					}
					opbr = null;
					transferTransactionsToPrivateDoc(user, tpfc.getFriendContact().getFrndId(), 
							transDoc.getTransactions(), requesterTransDoc);
					tpfc.getFriendContact().setTransactionDocId(requesterTransDoc.getIdString());
				}
				
				if(rpfc.getFriendContact() != null){
					System.out.println("IN rpfc ....");
					TransactionDoc targetTransDoc = new TransactionDoc();
					targetTransDoc.setUser1(frndId);
					targetTransDoc.setUser2(rpfc.getFriendContact().getFrndId());
					targetTransDoc.setDocType(Constants.PRIVATE_USER);
					targetTransDoc = TransactionDao.getTransactionDoc(targetTransDoc);
					UserMaster  fUser = new UserMaster();
					fUser.setUserId(Long.parseLong(frndId));
//					transDoc = TransactionDao.getTransactionDoc(transDoc);
					
					if(transDoc.getOpeningBalAmt() != 0){
						opbr = new OpeningBalRequest();
						if(transDoc.getUser1().equals(fUser.getUserId()+"")){
							opbr.setForUserId(fUser.getUserId()+"");
							opbr.setRequesterUserId(rpfc.getFriendContact().getFrndId());
							if(transDoc.getOpeningBalAmt() > 0){
								opbr.setPaymentStatus(Constants.TO_GIVE);
								opbr.setOpeningBalAmt(transDoc.getOpeningBalAmt());
							}else{
								opbr.setPaymentStatus(Constants.TO_TAKE);
								opbr.setOpeningBalAmt(transDoc.getOpeningBalAmt()*(-1));
							}
						}else{
							opbr.setForUserId(fUser.getUserId()+"");
							opbr.setRequesterUserId(rpfc.getFriendContact().getFrndId());
							if(transDoc.getOpeningBalAmt() > 0){
								opbr.setPaymentStatus(Constants.TO_TAKE);
								opbr.setOpeningBalAmt(transDoc.getOpeningBalAmt());
							}else{
								opbr.setPaymentStatus(Constants.TO_GIVE);
								opbr.setOpeningBalAmt(transDoc.getOpeningBalAmt()*(-1));
							}
						}
					}
					
					
					if(opbr!= null){
						TransactionDao.updateOpeningBalTransactionDoc(fUser, opbr, Constants.PRIVATE_USER);
					}
					
					transferTransactionsToPrivateDoc(fUser, rpfc.getFriendContact().getFrndId(), 
							transDoc.getTransactions(), targetTransDoc);
					rpfc.getFriendContact().setTransactionDocId(targetTransDoc.getIdString());
				}
			
			        System.out.println("---in delete staFF TRANSACTION");
					StaffTransactionDao.deleteTransactionsOfBlockedUser(frndId, user.getUserId()+"");
					StaffTransactionDao.deleteTransactionsOfBlockedUser( user.getUserId()+"",frndId);
					 System.out.println("---OUT delete staFF TRANSACTION");
					/**
					 * generated notification message
					 **/
					String notiText =  "";
					/*if(rpfc.getFriendContact().getContactName() != null && !rpfc.getFriendContact().getContactName().isEmpty()){
						notiText = rpfc.getFriendContact().getContactName();
					}
					else
						notiText = rpfc.getFriendContact().getContactNo();*/
					notiText += " has blocked you";
					
					System.out.println("msg : "+notiText);
					
					NotificationHelper.buildAndSendBlockedUserNotification(user.getUserId()+"", frndId, notiText, false);
					
					List<String> targetStaff = StaffUserDao.getStaffIdsForUser(Long.parseLong(frndId));
					
					if(targetStaff != null && !targetStaff.isEmpty()){
						for(String staff : targetStaff){
							NotificationHelper.buildAndSendBlockedUserNotification(user.getUserId()+"", staff, notiText, true);
						}
					}
					
					List<String> requesterStaff = StaffUserDao.getStaffIdsForUser(user.getUserId());
					
					if(requesterStaff != null && !requesterStaff.isEmpty()){
						for(String staff : requesterStaff){
							NotificationHelper.buildAndSendBlockedUserNotification(frndId, staff, notiText, true);
						}
					}
					
					
					
					
					flag = true;
			}
		}
		return tpfc;
		
	}
	
	/**
	 * This method transfers the transactions of blocked transactionDoc to
	 * new private user Doc.
	 **/
	public static int transferTransactionsToPrivateDoc(final UserMaster usr, final String prUser, final List<Transaction> oldTransList, final TransactionDoc privateDoc){
		
		Thread thrd = new Thread(){
			 @Override
	         public void run(){
				Transaction trans = null;
				long count = privateDoc.getIdCount();
				long epoch = System.currentTimeMillis();
				for(Transaction t : oldTransList){
					count++;
					trans = t;
					t.setTransactionId(usr.getUserId()+"_"+prUser+"_"+count);
					if(trans.getFrom().equals(usr.getUserId()+"")){
						trans.setTo(prUser);
					}else{
						trans.setFrom(prUser);
					}
					
					if(!trans.getCreatedBy().equals(usr.getUserId()+""))
						trans.setCreatedBy(prUser);
					
					
					trans.setUpdatedTime(epoch);
					trans.setSrNo(count);
					trans.setTransactionDocId(privateDoc.getIdString());
					privateDoc.getTransactions().add(trans);
				}
				privateDoc.setIdCount(count);
				privateDoc.setUpdatedTime(epoch);
				
				TransactionDao.addTransactions(privateDoc, usr);
			 }
		};
		thrd.start();
//		return privateDoc;
				return 0;
	}
	
	
	
	public static PrivateUserBean blockNcreatePrivateUser(String frndId, UserMaster user, String requesterId){
		long epoch = System.currentTimeMillis();
		//Step1 : update frndStatus of target user.
		boolean flag = false;
		PrivateUserBean pub = new PrivateUserBean();
		
		FriendContact newFrnd = null;
		int res = FriendsDao.blockFriend(frndId, user, Constants.BLOCKED_USER, requesterId);
		if(res == 1){
			long pt = 0;
			FriendContact frnd = FriendsDao.getFriendForWeb(frndId, 0, user);
			if(frnd!=null){
				FriendList frndDoc = FriendsDao.getAssociatedUserDoc(user);
				boolean existFlag = false;
				for(FriendContact fc :frndDoc.getFriends()){
					if(fc.getReffId().equals(frnd.getFrndId()) && fc.getFrndStatus() == Constants.PRIVATE_USER){
						existFlag = true;
						newFrnd = fc;
						pub.setFriendContact(fc);
						pub.setPrivateUser(PrivateUserDao.getPrivateUserByIdFor(fc.getFrndId()));
						break;
					}
				}
				
				if(!existFlag){
					PrivateUser privateUser = new PrivateUser();
					privateUser.setContactNo(frnd.getContactNo());
					privateUser.setDisplayName(frnd.getContactName());
					privateUser.setOwnerId(user.getUserId());
					privateUser.setCreatedTime(epoch);
					privateUser.setUpdatedTime(epoch);
					if(PrivateUserDao.addPrivateUserForBlocked(user.getUserId(), privateUser)){
						FriendContact nfc = new FriendContact();
					long count =  frndDoc.getIdCount();
						nfc.setContactName(frnd.getContactName());
						nfc.setContactNo(frnd.getContactNo());
						nfc.setCreatedTime(epoch);
						nfc.setFrndId(privateUser.getPrivateUserId());
						nfc.setId(count++);
						nfc.setFrndStatus(Constants.PRIVATE_USER);
						nfc.setReffId(frndId);
						frndDoc.setIdCount(count);
						frndDoc.setFriends(Arrays.asList(nfc));
						FriendsDao.addFriends(frndDoc);
						flag = true;
						
						pub.setFriendContact(nfc);
						pub.setPrivateUser(privateUser);
						newFrnd = nfc;
					}
				}
				
			}
		}
		return pub;
	}
	
	
	
	public static void main(String[] args) {
		
		UserMaster user = new UserMaster();
		user.setUserId(1);
		blockUser("2", user);
	}
	
	
	public static void updateFrndDocThread(final UserMaster user){
		Thread thrd = new Thread(){
			 @Override
	         public void run(){
				 FriendsDao.updateFriendsDocsForUnmangedUser(user);
			}
		};
		thrd.start();
	}
}
