package hisaab.util;

import hisaab.services.staff.modal.StaffUser;
import hisaab.services.user.modal.UserMaster;

import java.util.HashMap;

public class Constants {
	
	public static boolean DEV_MODE = false;
	
	public static HashMap<String,UserMaster> userMaster = new HashMap<String,UserMaster>();
		
	public static HashMap<String,StaffUser> staffUser = new HashMap<String,StaffUser>();
	
	public static boolean RECORD_LOGS = true;
	
	public static boolean AUTH_USERID = false;
	
	
	public static String USER_IMAGES_FOLDER = "/root";
	
	public static String USER_IMAGES_SMALL = "/hisaab/user_images";
	
	public static String USER_IMAGES_LARGE = "/hisaab/user_images_large";
	
	public static final int SUCCESS_RESPONSE = 200;
	
	public static final int INVALID_PARAM = 400;
	
	public static final int AUTH_FAILURE = 401;
	
	public static final int DELETE = 1;
	
	public static final int NOT_DELETED = 0;
		
	public static final int TRANSC_SUCCESS = 0;
	
	public static final int TRANSC_FAILURE = 1;
	
	public static final int NOT_AVAILABLE = 2;
	
	public static final int INVALID_PARAMS = 422;
	
	public static final int DB_FAILURE = 501;
	
	public static final int FAILURE = 503;
	
	public static final int DB_EMAIL_ALREADY_EXIST = 1;
	
	public static final int EMAIL_NOT_VERIFIED = -1;
	
	public static final int EMAIL_ALREADY_EXIST = 409;
	
	
	public static final int ANDROID = 2;
	
	public static final int IOS = 1;
	
	public static final int BULK_PUSH_COUNTER = 500;
	
	public static final int STAFF_NOT_ADDED = 2;
	
	
	
	public static final int REQUEST_EXPIRED = 3;
	
	public static final int STAFFUSER_REQ_ACCEPTED = 1;
	
	public static final int STAFF_DELETED = 2;
	
	public static final int STAFF_REQUEST_REJECT_RESPONSE = 508;
	
	
	/**
	 * Transaction Constants
	 * for transactions
	 **/
	
	public static final int TO_TAKE = 1;
	
	public static final int TO_GIVE = 2;
	
	public static final int TRANS_TYPE_SALE = 1;
	
	public static final int TRANS_TYPE_PURCHASE = 2;
	
	public static final int TRANS_NEED_TO_APROOVE = 1;
	
	public static final int TRANS_ADDED_BY_STAFF = 3;
	
	
	public static final int TRANS_REJECTED = 2;
	
	public static final int TRANS_APROOVED = 0;
	
	public static final int  ACTION_APPROVED= 111;
	
	public static final int ACTION_REJECTED = 112;
	
	public static final int TRANSACTION_UPDATE = 221;
	
	public static final int TRANSACTION_DELETE = 222;
	
	
	/***
	 * Transaction Read Statuses
	 ***/
	public static final int TRANSACTION_RECIEVED = 1;
	
	public static final int TRANSACTION_READ = 2;
	
	
	
	
	
	/**
	 * User Constants 
	 ***/
		public static final int NOT_REGISTERED_USER = 1;
		
		public static final int PRIVATE_USER = 2;
		
		public static final int USER_ONBOARDING_COMPLETE = 1;
		
		public static final int STAFF_USER = 3;
		
		public static final int BLOCKED_USER= 5;
		

		
	
	/**
	 * Android Notification key
	 **/
	
	public final static String AUTH_KEY_FCM = "AIzaSyAYh_NGhDOBoWGDn36pQ6lpQtU0V2XuYLI";
	public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
	
	
	public static final int NOTIFICATION_TRANS_NEW = 1;
	
	public static final int NOTIFICATION_TRANS_UPDATE = 3;
	
	public static final int NOTIFICATION_TRANS_DELETE = 4;
	
	public static final int NOTIFICATION_TRANS_DISPUTE = 2;
	
	public static final int NOTIFICATION_TRANS_BY_STAFF = 5;
	
	public static final int NOTIFICATION_TRANS_READ_STAT = 6;
	
	public static final int NOTIFICATION_STAFF_TRANS_DELETE = 7;
	
	public static final int NOTIFICATION_STAFF_TRANS_UPDATE = 8;
	
	public static final int NOTIFICATION_STAFF_INVITE = 9;
	
	public static final int NOTIFICATION_STAFF_REQUEST_STATUS = 10;
	
	public static final int NOTIFICATION_STAFF_REMOVAL_STATUS = 11;
	
	public static final int NOTIFICATION_OPENING_BAL_REQ = 12;
	
	public static final int NOTIFICATION_OPENING_BAL_RES = 13;
	
	public static final int NOTIFICATION_SYSTEM_NOTIFICATION = 14;
	
	public static final int NOTIFICATION_BLOCK_USER = 15;
	
	public static final int NOTIFICATION_SERVER_MIGRATE = 16;
	
	/**
	 * System update Notification 
	 **/
	public static final String SYSTEM_NOTIFICATION_UPDATE = "update";
	
	public static final String SYSTEM_NOTIFICATION_SERVER_MIGRATE = "servermigrate";
	
	/***
	 *Sms sending 
	 ***/
	
	public static final String SMS_PROMO_USERNAME = "HisaabApp";
	
	public static final String SMS_PROMO_APIKEY = "9EXWRd0Xbcqu2E0gpAa8";
	
	public static final String SMS_PROMO_SENDERID = "promo_id";
	
	
	public static final String SMS_TRANS_USERNAME = "HisaabAppTran";
	
	public static final String SMS_TRANS_APIKEY = "AkMhx409w0qaQSP14YSr";
	
	public static final String SMS_TRANS_SENDERID = "HISAAB";
	
	public static final String SMS_TXT_TYPE = "txt";
	
	public static final int PROMOTIONAL_SMS_LIMIT = 3;
	
	
	
	public static final int SMS_TYPE_PROMOTIONAL = 1;
	
	public static final int SMS_TYPE_TRANSACTIONAL = 2;
	
	public static final boolean SMS_PACK_ACTIVE = true; 
	
	public static final String DEFAULT_COUNTRY_CODE = "+91";

	
	
	/***
	 * Mongo Creds
	 ***/
	public static final String MONGO_URL = "139.59.26.175";

	public static final String MONGO_USER = "tacktile";
	
	public static final String MONGO_PASS = "tacktile2014";
	
	
	/***
	 * MYSQL Creds
	 ***/	
	public static final String MYSQL_URL = "jdbc:mysql://139.59.26.175:3306/hisaab";

	public static final String MYSQL_USER = "hisaab2";
	
	public static final String MYSQL_PASS = "hisaab";

}
