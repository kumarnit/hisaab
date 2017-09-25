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

/**
 * Application Lifecycle Listener implementation class QuartzListener2
 *
 *this listener is used for sending notification for auto update and auto delete
 */
public class QuartzListener2 implements ServletContextListener {

	Scheduler scheduler = null;
	 public void contextInitialized(ServletContextEvent servletContext) {
        System.out.println("Context Initialized2 Time : "+DateHelper.getDateInString());
           
        try {
                // Setup the Job class and the Job group
                
                JobDetail logJob = newJob(AutoSendNotificationJob.class).withIdentity(
                        "auto_notification", "Group3").build();
               // Create a Trigger that fires every 5 minutes.
                     Trigger trigger2 = newTrigger()
                        .withIdentity("trig4", "Group3")
                        .withSchedule(CronScheduleBuilder.cronSchedule("0 30 3 1/1 * ? *"))
                        .build();

                     
                     
//         12:00 AM Daily ==>   	0 0 1 1/1 * ? *      2 miniutes ==> 0 0/2 * 1/1 * ? *
                // Setup the Job and Trigger with Scheduler & schedule jobs
                scheduler = new StdSchedulerFactory().getScheduler();
                scheduler.start();
                
                scheduler.scheduleJob(logJob,trigger2);
                
               
                
        }
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
