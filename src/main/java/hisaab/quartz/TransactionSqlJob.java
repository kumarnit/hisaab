package hisaab.quartz;


import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.transaction.request.dao.ModificationRequestDao;
import hisaab.services.user.dao.UserDao;
import hisaab.util.DateHelper;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TransactionSqlJob implements Job{

	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		System.out.println("Job Started Time : "+DateHelper.getDateInString());
		TransactionDao.deleteTransactionInSql();

		UserDao.deleteUserRequest();
		
//		ModificationRequestDao.deleteModificationRequest();

	}

}
