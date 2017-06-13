package hisaab.quartz;

import hisaab.services.user.UserHelper;

import java.util.Calendar;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class PromotionalSmsJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("Promotional Sms Sending at "+Calendar.getInstance().getTime());
		UserHelper.sendPromotionalMessageToUser();
	}

	
}
