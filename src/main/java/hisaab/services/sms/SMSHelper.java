package hisaab.services.sms;

import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SMSHelper {

	
	public static String sendSms(String mobileNo, String msg, int type){
		//Prepare Url
		URLConnection myURLConnection=null;
		URL myURL=null;
		BufferedReader reader=null;
		String msgId = "";
		
		//encoding message 
		String encoded_message=URLEncoder.encode(msg);

		//Send SMS API
		String mainUrl="http://smshorizon.co.in/api/sendsms.php?";
		
		//Prepare parameter string 
		StringBuilder sbPostData= new StringBuilder(mainUrl);
		if(type == Constants.SMS_TYPE_PROMOTIONAL){
			sbPostData.append("user="+ Constants.SMS_PROMO_USERNAME ) ;
			sbPostData.append("&apikey="+Constants.SMS_PROMO_APIKEY);
			sbPostData.append("&senderid="+Constants.SMS_PROMO_SENDERID);
			
		}else if(type == Constants.SMS_TYPE_TRANSACTIONAL){
			sbPostData.append("user="+ Constants.SMS_TRANS_USERNAME ) ;
			sbPostData.append("&apikey="+Constants.SMS_TRANS_APIKEY);
			sbPostData.append("&senderid="+Constants.SMS_TRANS_SENDERID);
		}
		
		sbPostData.append("&message="+encoded_message);
		sbPostData.append("&mobile="+mobileNo);
		
		sbPostData.append("&type="+Constants.SMS_TXT_TYPE);

		//final string
		mainUrl = sbPostData.toString();
		try
		{
		    //prepare connection
		    myURL = new URL(mainUrl);
		    myURLConnection = myURL.openConnection();
		    myURLConnection.connect();
		    reader= new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
		    //reading response 
		    String response;
		    while ((response = reader.readLine()) != null){ 
		    //print response 
//		    System.out.println(response);
			    try {
					msgId = response;
					
				} catch (Exception e) {
					System.err.println("Error : "+e.getMessage());
					System.out.println("in sending sms to "+mobileNo);
				}
		    }
		    System.out.println("msgId : "+msgId);
		    //finally close connection
		    reader.close();
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace();
		} 
		return msgId;
		
	}
	
	
   public static String generateTransactionalCodeMessage(String code){
	   
	   String str = "";
	   
	   str = "Hi, \n"
	   		+ "Welcome to LenaDena App. Your OTP :"+code+".\n"
	   				+ "Your OTP will expire in 3 Hrs.";
	   return str;
   }

   public static String generatePromotionalTransactionMessage(UserMaster user, String contactNo){
	   String name = "";
	   if(user.getUserProfile().getUserName() != null)
		   name = user.getUserProfile().getUserName();
	   String str = "Hi "+contactNo+", \n"
	   		+ " "+name+" ("+user.getContactNo()+") is adding transactions with you on LenaDena App."
	   				+ "To view the transactions join LenaDena App.\n"
	   				+ "https://play.google.com/store/apps/details?id=tacktile.hisaab";
	   return str;
   }
   
   public static String generatePromotionalStaffInviteMessage(UserMaster user, String contactNo){
	   String name = "";
	   if(user.getUserProfile().getUserName() != null)
		   name = user.getUserProfile().getUserName();
	   String str = "Hi "+contactNo+", \n"
	   		+ " "+name+" ("+user.getContactNo()+") has invited you as a staff on LenaDena App."
	   				+ "To be a staff join LenaDena App.\n"
	   				+ "https://play.google.com/store/apps/details?id=tacktile.hisaab";
	   return str;
   }


   public static void main(String[] args) {
//	sendSms("+918087611301", "jsdjhgfj 73476 gsjdfg", 0);
}
}
