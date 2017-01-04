package hisaab.util;

import hisaab.services.notification.TransactionNotification;
import hisaab.services.notification.android.AndroPushPayload;
import hisaab.services.notification.android.fcm.MulticastResultFcm;
import hisaab.services.notification.android.fcm.Result;
import hisaab.services.transaction.modal.Transaction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import com.google.android.gcm.server.Message;
import com.google.gson.Gson;





public class FCMTest {

	
	// userDeviceIdKey is the device id you will query from your database     
	public static void pushFCMNotification(AndroPushPayload pushPayLoad,List<String> userDeviceIdKey) throws     Exception{

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
//		json.put("notification",info);
		json.put("data",info);
	
		System.out.println("== > "+json.toString());
//		json.put("title", "tttttt");
		
		
		
		System.out.println("== > "+json.toString());
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(json.toString());
		wr.flush();
		if (conn.getResponseCode() != 200) {
//			throw new RuntimeException("Failed : HTTP error code : "
//					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}
		ObjectMapper mapper = new ObjectMapper();
		
		/*MulticastResultFcm mcResult = mapper.readValue(conn.getInputStream(), MulticastResultFcm.class);
		
		if (mcResult.getResults() != null) {
            for(Result r : mcResult.getResults()){
            	
	        	 int canonicalRegId = mcResult.getConical_ids();
	             if (canonicalRegId != 0) {
	             }
	             if(r.getError()==null ){
	            	 System.out.println("atleast 1 is Successful");
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
	*/	
	} 
	
	public static Message createAndroidPayload(AndroPushPayload payload){
    	Message msg = new Message.Builder()
    	.addData("title", payload.getTitle())
    	.addData("notification",new Gson().toJson(payload.getData()))
    		    .build();
    	return msg;
    }
	
	public static void main(String[] args) {
		String [] userTestToken = {"ff8UjfTIQjk:APA91bGw-XxEhrhq_hr8G6OoWyPpAb6L6Y_yVltlYFbTlgqiRLwfMHhxyiA4Nc4jNP4CAGRuKyks-TQ6IgYusNS-m9jee9Gfz55CgEth06KuCTxe449700cc0NBFS81xJ4zEpv-cEWmh"};
		try {
			AndroPushPayload pp = new AndroPushPayload();
			TransactionNotification tn =new TransactionNotification();
			tn.setTransaction(new Transaction());
			pp.setData(tn);
			pushFCMNotification(pp,Arrays.asList(userTestToken));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
