package hisaab.services.transaction.helper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.transaction.request.dao.ModificationRequestDao;
import hisaab.services.transaction.request.modal.ModificationRequest;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;

public class TransactionHelper {

	
	public static void processResponseForTransactionUpdate(String transactionId, String transactionDocId, UserMaster user, int userResponse){
		TransactionDoc transDoc = TransactionDao.getTransactionDocByDocId(transactionDocId);
		
		if(transDoc != null){
			
			if(userResponse == Constants.ACTION_APPROVED){
				
			}
		}
		
	}
	
	public static void processResponseForTransactionDelete(String transactionId, String transactionDocId, UserMaster user, int userResponse){
		
	}
	public static void generateTransIdAndAddTransaction()
	{
		
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ModificationRequest modReq = pendingReq.get(transaction.getTransactionId());
			if(modReq != null){
			UserMaster user = UserDao.getUserForWeb(Long.parseLong(modReq.getForUser()));
			int i;
			try {
				i =	TransactionDao.processResponseForTransactionUpdate(transaction.getTransactionId(), transaction.getTransactionDocId(),
						user, Constants.ACTION_APPROVED, pendingReq.get(transaction.getTransactionId()));
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
	
	public static void main(String[] arg)
	{
		autoApprovalOfDeletePendingStatus();
		System.out.println("In Update APPROVAL");
//		autoApprovalUpdatePendingStatus();
	}
}
