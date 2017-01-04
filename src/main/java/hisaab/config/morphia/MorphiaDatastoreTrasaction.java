package hisaab.config.morphia;

import org.mongodb.morphia.Datastore;



/**
 * The database transaction level class
 * @author Ashutosh
 *
 */
public class MorphiaDatastoreTrasaction {
	
	/**
	 * This method is use to create monogodb morphia datastore. 
	 * @param classOfT
	 * @return datastore
	 */
	public static <T> Datastore getDatastore(Class<T> classOfT){
		MongoResource resource = MongoResource.INSTANCE;
		Datastore datastore = resource.getDatastore(classOfT);

		
		return datastore;
	}
}
