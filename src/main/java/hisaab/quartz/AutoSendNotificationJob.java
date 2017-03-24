package hisaab.quartz;

import hisaab.services.transaction.autoSendNotification.SendAutoNotification;
import hisaab.services.transaction.helper.TransactionHelper;
import hisaab.util.DateHelper;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AutoSendNotificationJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		System.out.println("Job Started Auto Notification Time : "+DateHelper.getDateInString());
		SendAutoNotification.sendUpdateNotification();
		SendAutoNotification.sendDeleteNotification();
		SendAutoNotification.sendClearTransNotification();
	}

}
