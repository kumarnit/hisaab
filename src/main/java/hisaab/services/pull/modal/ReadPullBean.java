package hisaab.services.pull.modal;

import java.util.ArrayList;
import java.util.List;

public class ReadPullBean {
	
	private long readPullTime;
	
	private String msg = "";
	
	private int status;
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	private List<ReadTransaction> readTransactionList = new ArrayList<ReadTransaction>();

	
	public long getReadPullTime() {
		return readPullTime;
	}

	public void setReadPullTime(long readPullTime) {
		this.readPullTime = readPullTime;
	}

	public List<ReadTransaction> getReadTransactionList() {
		return readTransactionList;
	}

	public void setReadTransactionList(List<ReadTransaction> readTransactionList) {
		this.readTransactionList = readTransactionList;
	}
	
	
	
	
}
