package hisaab.services.viewlog.helper;

import java.util.List;




import hisaab.services.transaction.modal.Transaction;
import hisaab.services.user.modal.UserMaster;
import hisaab.services.viewlog.dao.TransactionLogDao;
import hisaab.services.viewlog.modal.TransactionLog;

public class TransactionLogHelper {

	public static void saveTransLog(UserMaster user,List<Transaction> trans)
	{
		long epoch = System.currentTimeMillis();
		TransactionLog transLog = new TransactionLog();
		transLog.setContactName(user.getUserProfile().getUserName());
		transLog.setContactNo(user.getContactNo());
		transLog.setCreatedTime(epoch);
		for(Transaction transac : trans){
			transLog.setTransType(transac.getType());
			TransactionLogDao.saveTransactionLog(transLog);
		}
		
		
	}
}
