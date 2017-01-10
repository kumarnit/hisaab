package hisaab.util;

import hisaab.services.staff.dao.StaffUserDao;
import hisaab.services.user.dao.UserDao;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Application Lifecycle Listener implementation class Intializer
 *
 */
public class Intializer implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public Intializer() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
    	UserDao.setUserMasterInHashMap();
    	StaffUserDao.setStaffUserInHashMap();
         // TODO Auto-generated method stub
    }
	
}
