package hisaab.services.viewlog.helper;

import hisaab.services.viewlog.dao.TransactionLogDao;

public class DelIntenalUser {
	public static void deleteIntenalUser(){
		TransactionLogDao.clearTransactionLog();
	}

}
