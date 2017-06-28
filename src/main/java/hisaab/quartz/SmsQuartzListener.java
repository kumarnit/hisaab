package hisaab.quartz;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import hisaab.util.DateHelper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class SmsQuartzListener implements ServletContextListener{

	Scheduler scheduler = null;
	 public void contextInitialized(ServletContextEvent servletContext) {
        System.out.println("Context Initialized Time : "+DateHelper.getDateInString());
           
        try {
                // Setup the Job class and the Job group
                
                JobDetail logJob = newJob(PromotionalSmsJob.class).withIdentity(
                        "smsPromotion", "Group5").storeDurably(true).build();
                
               // Create a Trigger that fires every day 9 am.
                     Trigger trigger1 = newTrigger().forJob(logJob)
                        .withIdentity("trig101", "Group5")
                        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 4 1/1 * ? *"))
                        .build();
                     
                     Trigger trigger2 = newTrigger().forJob(logJob)
                             .withIdentity("trig102", "Group5")
                             .withSchedule(CronScheduleBuilder.cronSchedule("0 0 6 1/1 * ? *"))
                             .build();

                     Trigger trigger3 = newTrigger().forJob(logJob)
                             .withIdentity("trig103", "Group5")
                             .withSchedule(CronScheduleBuilder.cronSchedule("0 0 8 1/1 * ? *"))
                             .build();

                     Trigger trigger4 = newTrigger().forJob(logJob)
                             .withIdentity("trig104", "Group5")
                             .withSchedule(CronScheduleBuilder.cronSchedule("0 0 10 1/1 * ? *"))
                             .build();

                     Trigger trigger5 = newTrigger().forJob(logJob)
                             .withIdentity("trig105", "Group5")
                             .withSchedule(CronScheduleBuilder.cronSchedule("0 0 12 1/1 * ? *"))
                             .build();
                     
//         12:00 AM Daily ==>   	0 0 1 1/1 * ? *      2 miniutes ==> 0 0/2 * 1/1 * ? *
                // Setup the Job and Trigger with Scheduler & schedule jobs
                scheduler = new StdSchedulerFactory().getScheduler();
         
             /*   scheduler.addJob(logJob, true);
                scheduler.scheduleJob(trigger1);
                scheduler.scheduleJob(trigger2);
                scheduler.scheduleJob(trigger3);
                scheduler.scheduleJob(trigger4);
                scheduler.scheduleJob(trigger5);
                
                scheduler.start();
*/        }
        catch (SchedulerException e) {
                e.printStackTrace();
        }
}

@Override
public void contextDestroyed(ServletContextEvent servletContext) {
        System.out.println("Context Destroyed");
        try 
        {
                scheduler.shutdown();
        } 
        catch (SchedulerException e) 
        {
                e.printStackTrace();
        }
}
	
	
}
