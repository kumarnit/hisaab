package hisaab.services.notification.android;

import hisaab.services.notification.android.fcm.MulticastResultFcm;
import hisaab.services.notification.android.fcm.Result;
import hisaab.services.notification.android.fcm.SystemNotificationResponse;
import hisaab.util.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import com.google.gson.Gson;


public class SendNotification {
/**
 * this method actualy send the notification.
 * **/	
	public static boolean pushFCMNotification(AndroPushPayload pushPayLoad,List<String> userDeviceIdKey) throws     Exception{
		boolean flag = false;
		String authKey = Constants.AUTH_KEY_FCM;   // You FCM AUTH key
		String FMCurl = Constants.API_URL_FCM;     
	    
		URL url = new URL(FMCurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		System.out.println(userDeviceIdKey);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization","key="+authKey);
		conn.setRequestProperty("Content-Type","application/json");
		Gson gson = new Gson();
		
		JSONObject json = new JSONObject();
		json.put("registration_ids",userDeviceIdKey);
		JSONObject info = new JSONObject();
		info.put("title", pushPayLoad.getTitle());   // Notification title
		info.put("body",gson.toJson(pushPayLoad.getData()));
		json.put("data",info);
//		json.put("notification",info);
		
	
		System.out.println("== > "+json.toString());
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(json.toString());
		wr.flush();
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
			
		}

		/*BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}
*/		ObjectMapper mapper = new ObjectMapper();
		
/***
 * parsing response from fcm server
 * 
 ***/
		MulticastResultFcm mcResult = mapper.readValue(conn.getInputStream(), MulticastResultFcm.class);
		
		if (mcResult.getResults() != null) {
            for(Result r : mcResult.getResults()){
            	
	        	 int canonicalRegId = mcResult.getConical_ids();
	             if (canonicalRegId != 0) {
	             }
	             if(r.getError()==null ){
	            	 System.out.println("atleast 1 is Successful");
	            	 flag = true;
	             }
	             else{
	            	 System.out.println("Error : "+r.getError());
	             }
	             
            }
            System.out.println(mcResult.getSuccess());
         } else {
             int error = mcResult.getFailure();
             System.out.println(error);
         }
         System.out.println("Error occurred while sending push notification :" + mcResult.getFailure());
		
         return flag;
	} 

	
	
	
	
	
	public static boolean pushSystemNotification(AndroPushPayload pushPayLoad,String topic) throws     Exception{
		boolean flag = false;
		String authKey = Constants.AUTH_KEY_FCM;   // You FCM AUTH key
		String FMCurl = Constants.API_URL_FCM;     
	    
		URL url = new URL(FMCurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		System.out.println(topic);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization","key="+authKey);
		conn.setRequestProperty("Content-Type","application/json");
		Gson gson = new Gson();
		
		JSONObject json = new JSONObject();
		json.put("to","/topics/"+topic);
		JSONObject info = new JSONObject();
		info.put("title", pushPayLoad.getTitle());   // Notification title
		info.put("body",gson.toJson(pushPayLoad.getData()));
		json.put("data",info);
//		json.put("notification",info);
		
	
		System.out.println("== > "+json.toString());
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(json.toString());
		wr.flush();
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode() + "\n"+conn.getResponseMessage());
		}

		/*BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}
*/		ObjectMapper mapper = new ObjectMapper();
		
/***
 * parsing response from fcm server
 * 
 ***/
		SystemNotificationResponse mcResult = mapper.readValue(conn.getInputStream(), SystemNotificationResponse.class);
		
		if(conn.getResponseCode() == 200){
			if (mcResult.getMessageId() > 0) {
            System.out.println("System : "+mcResult.getMessageId());
			flag = true;
            }
          
         } else {
             
             System.out.println(conn.getResponseCode()+" ==> "+conn.getResponseMessage());
             flag = false;
         }
         return flag;
	} 

	
	public static void main(String[] args) {
		
		AndroPushPayload app = new AndroPushPayload();
		JSONObject json = new JSONObject();
		json.put("message", "This is test topic message.");
		
		app.setData(json);
		
		try {
			pushSystemNotification(app, "update");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}
