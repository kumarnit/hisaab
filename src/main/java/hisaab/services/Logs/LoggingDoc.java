package hisaab.services.Logs;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("logs_doc")
public class LoggingDoc {

	@Id
	private String date = "";
	
	private List<LogModel> logs = new ArrayList<LogModel>();
	
	private long count ;
	
	private  String lastUpdated;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<LogModel> getLogs() {
		return logs;
	}

	public void setLogs(List<LogModel> logs) {
		this.logs = logs;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	
}
