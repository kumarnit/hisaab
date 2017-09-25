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
 * this listener is for auto approving transaction update and delete request.
 **/
public class QuartzListener1 implements ServletContextListener {
	Scheduler scheduler = null;

	 public void contextInitialized(ServletContextEvent servletContext) {
         System.out.println("Context Initialized 1 Time : "+DateHelper.getDateInString());
            
         try {
                 // Setup the Job class and the Job group
                 
        	 JobDetail logJob1 = newJob(AutoApprovalJob.class).withIdentity("auto_approval","Group2").build();
             Trigger trigger3 = newTrigger()
            		 .withIdentity("trig3","Group2")
            		 .withSchedule(CronScheduleBuilder.cronSchedule("0 30 9 1/1 * ? *"))
            		 .build();
             
             
             scheduler = new StdSchedulerFactory().getScheduler();
             scheduler.start();
             scheduler.scheduleJob(logJob1,trigger3);
                 
                
                 
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
