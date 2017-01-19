package hisaab.app.cronjob;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import hisaab.app.modal.TransactionCountModal;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.user.dao.UserDao;

public class TransactionCount {

	public static void setTransactionCount(){
		ObjectMapper maper = new ObjectMapper();
		Gson gson = new Gson();
		Object object = TransactionDao.getTransactionCount();
		Map<Long,Long> countHash = new HashMap<Long,Long>();
		List<TransactionCountModal> result = null;
	    try {
			result = (List<TransactionCountModal>) gson.fromJson(maper.writeValueAsString(object), Object.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
	    try {
			System.out.println("abc :"+maper.writeValueAsString(result));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    for (Object object2 : result ){
	    	TransactionCountModal object1 = gson.fromJson((gson.toJson(object2)), TransactionCountModal.class);
	    	if(object1.getCount() > 0){
	    		try{
	    			long userid = Long.parseLong(object1.getUser1());
		    		if(countHash.get(userid) != null ){
		    			
		    				long temp = countHash.get(userid);
		    				countHash.put(userid,temp + object1.getCount());
	//	    				countHash.			
		    		}else {
		    				countHash.put(userid,object1.getCount());
		    		}
	    		}catch(Exception e){
    				e.printStackTrace();
    			}
	    		try{
	    			long userid = Long.parseLong(object1.getUser2());
	    			if(countHash.get(userid) != null ){
	    				long temp = countHash.get(userid);
	    				countHash.put(userid,temp + object1.getCount());
	    			
	    			}else {
	    			
	    				countHash.put(userid,object1.getCount()); 
	    			
	    			}
	    			}catch(Exception e){
    				
	    			}
	    	}
	    	
	    }
	    Iterator it = countHash.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        UserDao.setTransactionCount(pair);
	       
	    }   
	    	}
	public static void main(String [] arg){
		setTransactionCount();
	}
}
