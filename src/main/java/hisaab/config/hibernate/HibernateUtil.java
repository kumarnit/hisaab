package hisaab.config.hibernate;

import hisaab.util.Constants;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;



public class HibernateUtil {
	
	private static final SessionFactory sessionFactory = buildSessionFactory();
	
	private static ServiceRegistry serviceRegistry;
	private static StandardServiceRegistryBuilder serviceRegistryBuilder;
	
	private static SessionFactory buildSessionFactory() {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			System.out.println("setting database information");
			Configuration config = new Configuration();
			/*config.setProperty("hibernate.connection.url","jdbc:mysql://"+System.getenv("OPENSHIFT_MYSQL_DB_HOST")+"//"+System.getenv("OPENSHIFT_MYSQL_DB_PORT")+"//realestatetacktile2015");
			config.setProperty("hibernate.connection.username", "adminGsGV2l3");
			config.setProperty("hibernate.connection.password", "yaDvnKyDc8Ab");*/
			
			/*config.setProperty("hibernate.connection.url","jdbc:mysql://"+System.getenv("OPENSHIFT_MYSQL_DB_HOST")+"//"+System.getenv("OPENSHIFT_MYSQL_DB_PORT")+"//realestatetacktile2015TEST");
			config.setProperty("hibernate.connection.username", "adminGsGV2l3");
			config.setProperty("hibernate.connection.password", "yaDvnKyDc8Ab");*/
			
			
			config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
			
			
			//For developer change to IP address while using from local remote.
			if(Constants.DEV_MODE){

                config.setProperty("hibernate.connection.url","jdbc:mysql://localhost:3306/hisaab_local");
				
//				config.setProperty("hibernate.connection.url","jdbc:mysql://192.168.0.21:3306/productionwebapp");
				config.setProperty("hibernate.connection.username", "root");
				config.setProperty("hibernate.connection.password", "admin");
				
				/*config.setProperty("hibernate.connection.url","jdbc:mysql://139.59.26.175:3306/hisaab");
				
//				config.setProperty("hibernate.connection.url","jdbc:mysql://192.168.0.21:3306/productionwebapp");
				config.setProperty("hibernate.connection.username", "hisaab2");
				config.setProperty("hibernate.connection.password", "hisaab");*/

			}else{
				config.setProperty("hibernate.connection.url",Constants.MYSQL_URL+"?amp;amp;autoReconnect=true;characterEncoding=utf8");
				config.setProperty("hibernate.connection.username", Constants.MYSQL_USER);
				config.setProperty("hibernate.connection.password", Constants.MYSQL_PASS);
				
			}

			
			config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
			config.setProperty("hibernate.show_sql",""+Constants.DEV_MODE+"");
			config.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
			config.setProperty("hibernate.transaction.auto_close_session", "false");
			config.setProperty("hibernate.current_session_context_class", "thread");
			/*config.setProperty("hibernate.connection.pool_size", "1");*/
			config.setProperty("hibernate.hbm2ddl.auto", "update");
//			config.setProperty("hibernate.hbm2ddl.auto", "create");
			
			
			/*config.addClass(com.realestate.user.model.UsersTable.class);
			config.addClass(com.realestate.Property.model.PropertyUnits.class);
			config.addClass(com.realestate.Property.model.Propertytypes.class);
			config.addClass(com.realestate.Property.model.PropertyCategories.class);
			config.addClass(com.realestate.proppost.model.UploadImages.class);
			config.addClass(com.realestate.proppost.model.PropertyDetailModel.class);
			config.addClass(com.realestate.proppost.model.SellerOrRentalProertyInfo.class);*/
			
			config.configure();
			
			
			serviceRegistryBuilder = new StandardServiceRegistryBuilder();
//			
			serviceRegistryBuilder.applySettings(config.getProperties());
//			
			serviceRegistry = serviceRegistryBuilder.build();
//			
			SessionFactory  sessionFactory = config.buildSessionFactory(serviceRegistry);
//			SessionFactory  sessionFactory = config.buildSessionFactory();
			return sessionFactory;
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed." + ex);
			
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public static void main(String[] args) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			System.out.println(session.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(session.isOpen()){
				session.close();
			}
		}
	}
}
