package hisaab.services.transaction.helper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hisaab.services.notification.NotificationHelper;
import hisaab.services.transaction.clear_transaction.dao.ClearTransactionRequestDao;
import hisaab.services.transaction.clear_transaction.modal.ClearTransactionRequest;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;






import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.transaction.request.dao.ModificationRequestDao;
import hisaab.services.transaction.request.modal.ModificationRequest;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;
import hisaab.util.DateHelper;

public class TransactionHelper {

	/***
	 * 
	 * NOT IN USE
	 * 
	 * **/
	public static void processResponseForTransactionUpdate(String transactionId, String transactionDocId, UserMaster user, int userResponse){
		TransactionDoc transDoc = TransactionDao.getTransactionDocByDocId(transactionDocId);
		
		if(transDoc != null){
			
			if(userResponse == Constants.ACTION_APPROVED){
				
			}
		}
		
	}
	
	/***
	 * 
	 * NOT IN USE
	 * 
	 * **/
	public static void processResponseForTransactionDelete(String transactionId, String transactionDocId, UserMaster user, int userResponse){
		
	}
	
	/***
	 * 
	 * NOT IN USE
	 * 
	 * **/
	public static boolean clearTransactionByDateRequest(UserMaster user, String tUser, long epochDate, int docType){
			
			TransactionDoc transDoc = new TransactionDoc();
			transDoc.setUser1(user.getUserId()+"");
			transDoc.setUser2(tUser);
			transDoc.setDocType(docType);
			transDoc = TransactionDao.getTransactionDoc(transDoc);
			
			List<Transaction> transactions = TransactionDao.getTransactionListBeforeDate(user.getUserId()+"", tUser, docType, epochDate);
			
			if(transactions!=null && !transactions.isEmpty()){
				double amt = calculateAmountforTransactions(transactions, transDoc);
				amt = amt + transDoc.getOpeningBalAmt();

			}
			
			return true;
		}
	
	
	
	/***
	 * for normal clear Trans approve/ reject action.
	 ***/
	public static boolean clearTransactionByDateAction(UserMaster user, ClearTransactionRequest clearTranRequest,
				int docType, int userResponse){
			boolean resultFlag = false;
			
		if(userResponse == Constants.ACTION_APPROVED){
			TransactionDoc transDoc = new TransactionDoc();
			transDoc.setUser1(clearTranRequest.getRequesterUserId());
			transDoc.setUser2(clearTranRequest.getForUserId());
			transDoc.setDocType(docType);
			transDoc = TransactionDao.getTransactionDoc(transDoc);
			
			List<Transaction> transactions = TransactionDao.getTransactionListBeforeDate(clearTranRequest.getRequesterUserId(), 
					clearTranRequest.getForUserId(), docType, clearTranRequest.getTillDate());
			
			if(transactions!=null && !transactions.isEmpty()){
				double amt = calculateAmountforTransactions(transactions, transDoc);
				amt = amt + transDoc.getOpeningBalAmt();
				List<String> transIds = new ArrayList<String>();
				for(Transaction t : transactions){
					transIds.add(t.getTransactionId());
				}
				if(TransactionDao.deleteMultipleTransactions(transIds, transDoc.getIdString())){
					transDoc.setOpeningBalAmt(amt);
					transDoc.setOpeningBalDate(DateHelper.getDateForOpen(clearTranRequest.getTillDate()));
					if(TransactionDao.updateOpeningBal(transDoc, clearTranRequest.getRequesterUserId())){
						
//						if(docType == 0)
//							ClearTransactionRequestDao.updateClearTransactionRequest( user, clearTranRequest, userResponse);

						if(clearTranRequest.getRequesterUserId().equals(transDoc.getUser1()))
							clearTranRequest.setOpeningBalAmt(amt);
						else
							clearTranRequest.setOpeningBalAmt(amt*(-1));
						clearTranRequest.setOpeningBalDate(DateHelper.getDateForOpen(clearTranRequest.getTillDate()));
						resultFlag = true;
						
						if(docType == 0){
						clearModificationRequestsForTransactions(transIds);
						TransactionDao.deleteTransactionSqlByIds(transIds);
						}
					}
					
				}
			}
		
		}
		else if(userResponse == Constants.ACTION_REJECTED){
			
			resultFlag = ClearTransactionRequestDao.updateClearTransactionRequest( user, clearTranRequest, userResponse);
			
		}
		
		
		return resultFlag;
	}
	
	
	public static double  calculateAmountforTransactions(List<Transaction> transactions, TransactionDoc transactionDoc){
		double user1Amt = 0 ;
		double user2Amt = 0;
		double del = 0;
		
		for(Transaction t : transactions){
			if(t.getFrom().equals(transactionDoc.getUser1()) ){
				user1Amt += t.getAmount();
			}
			else if(t.getFrom().equals(transactionDoc.getUser2())){
				user2Amt += t.getAmount();
			}
		}
		del = user1Amt - user2Amt;
		return del;
	}
	
	public static void clearModificationRequestsForTransactions(final List<String> transIds) {
		Thread thrd = new Thread(){
			 @Override
	         public void run(){
			
				 TransactionDao.deleteMultipleModifiedTransactions(transIds);
				 
				 TransactionDao.deleteTransactionSqlByIds(transIds);
				 
				 ModificationRequestDao.deleteModificationRequestByTransId(transIds);
			 }
		};
		thrd.start();
	}
	public static void autoApprovalUpdatePendingStatus()
	{
		List<String> pendingTransId = null;
		ObjectMapper mapper = new ObjectMapper();
		pendingTransId = ModificationRequestDao.getAllUpdatePendingTransactionId();
		List<Transaction> tranlist = TransactionDao.getPendingTransaction(pendingTransId);
		Map<String,ModificationRequest> pendingReq = ModificationRequestDao.getUpdatePendingRequest();
		for(Transaction transaction : tranlist )	
		{
			try {
				System.out.println("::: "+mapper.writeValueAsString(transaction) );
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			ModificationRequest modReq = pendingReq.get(transaction.getTransactionId());
			if(modReq != null){
			UserMaster user = UserDao.getUserForWeb(Long.parseLong(modReq.getForUser()));
			int i;
			try {
				i =	TransactionDao.processResponseForTransactionUpdate(transaction.getTransactionId(), transaction.getTransactionDocId(),
						user, Constants.ACTION_APPROVED, pendingReq.get(transaction.getTransactionId()),true);
				System.out.println(" i : " +i);
			} catch (Exception e) {
				System.out.println("Exception :1 "+e.getMessage());
				System.out.println("in approve transaction action.");
				i = -1;
			}
			}
		}
		
	}
	
	public static void autoApprovalOfDeletePendingStatus()
	{
		List<String> pendingTransId = null;
		ObjectMapper mapper = new ObjectMapper();
		pendingTransId = ModificationRequestDao.getAllDeletePendingTransactionId();
		List<Transaction> tranlist = TransactionDao.getPendingTransaction(pendingTransId);
		Map<String,ModificationRequest> pendingReq = ModificationRequestDao.getDeletePendingRequest();
		for(Transaction transaction : tranlist )	
		{
			/*try {
				System.out.println("::: "+mapper.writeValueAsString(transaction) );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			ModificationRequest modReq = pendingReq.get(transaction.getTransactionId());
			if(modReq != null){
			UserMaster user = UserDao.getUserForWeb(Long.parseLong(modReq.getForUser()));
			int i;
			try {
				i =	TransactionDao.processResponseForTransactionDelete(transaction.getTransactionId(), transaction.getTransactionDocId(),
						user, Constants.ACTION_APPROVED, pendingReq.get(transaction.getTransactionId()),true);
				System.out.println(" i : " +i);
			} catch (Exception e) {
				System.out.println("Exception :1 "+e.getMessage());
				System.out.println("in approve transaction action.");
				i = -1;
			}
			}
		}
		
	}
	
	public static HashMap<Integer, List<Long>> autoApprovalClearTransactionNotification(){
		List<ClearTransactionRequest> clearTransactionList = ClearTransactionRequestDao.getClearTransRequestListForNotifying();
		long epoch = System.currentTimeMillis();
		
		List<Long> userList = null;
		
		HashMap<Integer, List<Long>> notifMap = new HashMap<Integer, List<Long>>();
		long id =0;
		for(ClearTransactionRequest ctr : clearTransactionList){
			int days = (int) TimeUnit.MILLISECONDS.toDays(epoch - ctr.getCreatedTime());
			if(days > 0 && days <= 5 ){
				userList = notifMap.get(days);
				if(userList == null)
					userList = new ArrayList<Long>();
					try{
						id = Long.parseLong(ctr.getForUserId());
						if(id > 0){
							userList.add(id);
						}
						notifMap.put(days, userList);
					}catch(Exception e){
						e.printStackTrace();
					}
					id=0;
					userList = null;
					
			}
		}
		
		return notifMap;
	}

	
	
/***
 * For auto approval of clear transactions
 ***/	
	public static void autoApprovalClearTransaction(){
		List<ClearTransactionRequest> clearTransactionList = ClearTransactionRequestDao.getClearTransRequestListForAutoAprove();
		
		int docType = 0;
		for(ClearTransactionRequest clearTranRequest : clearTransactionList){
			
			TransactionDoc transDoc = new TransactionDoc();
			transDoc.setUser1(clearTranRequest.getRequesterUserId());
			transDoc.setUser2(clearTranRequest.getForUserId());
			transDoc.setDocType(docType);
			transDoc = TransactionDao.getTransactionDoc(transDoc);
			
			List<Transaction> transactions = TransactionDao.getTransactionListBeforeDate(clearTranRequest.getRequesterUserId(), 
					clearTranRequest.getForUserId(), docType, clearTranRequest.getTillDate());
			
			if(transactions!=null && !transactions.isEmpty()){
				double amt = calculateAmountforTransactions(transactions, transDoc);
				amt = amt + transDoc.getOpeningBalAmt();
				List<String> transIds = new ArrayList<String>();
				for(Transaction t : transactions){
					transIds.add(t.getTransactionId());
				}
				if(TransactionDao.deleteMultipleTransactions(transIds, transDoc.getIdString())){
					transDoc.setOpeningBalAmt(amt);
					transDoc.setOpeningBalDate(clearTranRequest.getTillDate());
					if(TransactionDao.updateOpeningBal(transDoc, clearTranRequest.getRequesterUserId())){
						
						ClearTransactionRequestDao.updateClearTransactionRequestForAutoApproval(  clearTranRequest, Constants.ACTION_APPROVED);

						if(clearTranRequest.getRequesterUserId().equals(transDoc.getUser1()))
							clearTranRequest.setOpeningBalAmt(amt);
						else
							clearTranRequest.setOpeningBalAmt(amt*(-1));
						clearTranRequest.setOpeningBalDate(clearTranRequest.getTillDate());
						clearModificationRequestsForTransactions(transIds);
						String msg = "System has aouto approved your clear transaction requests that were "
								+ "pending for more than 5 days.";
						NotificationHelper.buildAndSendClearTransactionResponseNotificationForAutoApproval(clearTranRequest, msg);
						
						if(docType == 0){
							clearModificationRequestsForTransactions(transIds);
							TransactionDao.deleteTransactionSqlByIds(transIds);
						}
						
					}
					
				}
			}
		}
		
				
		
	}

	
	
	
	
	
	
	public static void main(String[] arg)
	{
		autoApprovalOfDeletePendingStatus();
		System.out.println("In Update APPROVAL");
//		autoApprovalUpdatePendingStatus();
		
	
	}
}
