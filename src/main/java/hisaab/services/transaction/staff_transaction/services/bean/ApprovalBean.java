package hisaab.services.transaction.staff_transaction.services.bean;

import java.util.ArrayList;
import java.util.List;



public class ApprovalBean {

	List<String> approvedTransIds = new ArrayList<String>();
	
	List<String> rejectedTransIds = new ArrayList<String>();

	public List<String> getApprovedTransIds() {
		return approvedTransIds;
	}

	public void setApprovedTransIds(List<String> approvedTransIds) {
		this.approvedTransIds = approvedTransIds;
	}

	public List<String> getRejectedTransIds() {
		return rejectedTransIds;
	}

	public void setRejectedTransIds(List<String> rejectedTransIds) {
		this.rejectedTransIds = rejectedTransIds;
	}
	
	
}
