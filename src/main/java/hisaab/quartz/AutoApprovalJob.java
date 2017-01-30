package hisaab.quartz;

import hisaab.services.transaction.autoSendNotification.SendAutoNotification;
import hisaab.services.transaction.helper.TransactionHelper;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AutoApprovalJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		TransactionHelper.autoApprovalUpdatePendingStatus();
		TransactionHelper.autoApprovalOfDeletePendingStatus();
		SendAutoNotification.sendUpdateNotification();
		SendAutoNotification.sendDeleteNotification();
	}

}
