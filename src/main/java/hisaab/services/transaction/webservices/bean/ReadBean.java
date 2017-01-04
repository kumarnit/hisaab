package hisaab.services.transaction.webservices.bean;

import java.util.ArrayList;
import java.util.List;

public class ReadBean {
	
	
	private List<String> transIds = new ArrayList<String>();

	public List<String> getTransIds() {
		return transIds;
	}

	public void setTransIds(List<String> transIds) {
		this.transIds = transIds;
	}
	
}
