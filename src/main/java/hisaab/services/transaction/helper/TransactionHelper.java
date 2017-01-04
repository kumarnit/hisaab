package hisaab.services.transaction.helper;

import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.transaction.modal.TransactionDoc;
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
}
