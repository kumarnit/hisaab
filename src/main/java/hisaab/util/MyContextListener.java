package hisaab.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyContextListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("Destroying MyContextListener...");
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("Initializing MyContextListener...");
		
	}

}
