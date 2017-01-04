package hisaab.services.Logs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hisaab.config.morphia.MorphiaDatastoreTrasaction;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.util.DateHelper;

import org.codehaus.jackson.map.ObjectMapper;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

public class LoggingDao {

	
	public static LoggingDoc getLoggingDoc(LoggingDoc logDoc){
		
		Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(LoggingDoc.class);
		Query<LoggingDoc> query = datastore.createQuery(LoggingDoc.class);
		
		query.field("_id").equal(logDoc.getDate());
		if(query.get() != null){
			ObjectMapper mapper = new ObjectMapper();
			logDoc = query.get();
		}
		else{
			logDoc.setLastUpdated(Calendar.getInstance().getTime().toString());
			datastore.save(logDoc);
		}
		return logDoc;
		
	}
	
	
	public static boolean addLogs(LoggingDoc logDoc){
		boolean flag = false;
			
			Datastore datastore = MorphiaDatastoreTrasaction.getDatastore(LoggingDoc.class);
			Query<LoggingDoc> query = datastore.createQuery(LoggingDoc.class);
			
			query.field("_id").equal(logDoc.getDate());
			UpdateOperations<LoggingDoc> op = datastore.createUpdateOperations(LoggingDoc.class);
			op.set("lastUpdated", System.currentTimeMillis());
			op.set("count", logDoc.getCount());
			op.addAll("logs", logDoc.getLogs(),false);
			UpdateResults ur = datastore.update(query,op );
			if(ur.getUpdatedCount()>0){
				flag = true;
			}
		return flag;
	}
	
	public static void main(String[] args) {
		LoggingDoc logDoc = new LoggingDoc();
		logDoc.setDate(DateHelper.getDateWithoutTime());
		logDoc = LoggingDao.getLoggingDoc(logDoc);
		
	}
}
