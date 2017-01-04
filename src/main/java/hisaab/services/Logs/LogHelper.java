package hisaab.services.Logs;

import hisaab.util.DateHelper;

import java.util.Arrays;
import java.util.Calendar;

import javax.transaction.SystemException;

public class LogHelper {

	public static void addLogHelper(LogModel logModel){
		
		
		LoggingDoc logDoc = new LoggingDoc();
		logDoc.setDate(DateHelper.getDateWithoutTime());
		logDoc = LoggingDao.getLoggingDoc(logDoc);
		
		long count = logDoc.getCount();
		
		logModel.setReqId(++count);
		logModel.setCreatedTime(System.currentTimeMillis());
		logModel.setReqDate(Calendar.getInstance().getTime().toString());
		logDoc.setLogs(Arrays.asList(logModel));
		logDoc.setCount(count);
		logDoc.setLastUpdated(Calendar.getInstance().getTime().toString());
		
		LoggingDao.addLogs(logDoc);
		
	}
	
	
}
