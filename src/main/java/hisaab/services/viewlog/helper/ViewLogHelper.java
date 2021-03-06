package hisaab.services.viewlog.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import hisaab.services.user.modal.UserProfile;
import hisaab.services.viewlog.dao.TransactionLogDao;
import hisaab.services.viewlog.dao.UserLogDao;
import hisaab.services.viewlog.modal.TransactionLog;
import hisaab.services.viewlog.webservices.bean.PageRequest;

public class ViewLogHelper {
	
	public static void manageResponse(List<UserProfile> userprofile,
			PageRequest resp) {
		int count = 1;
		for(UserProfile userpro : userprofile){
			List<String> i = new ArrayList<String>();
			
			i.add((resp.getStart()+count)+"");
			i.add(userpro.getUserName());
			i.add(userpro.getContactNo());
			i.add(convertToISTWithoutYear(userpro.getCreatedTime()));
			
			resp.getData().add(i);
			
			count++;
			
		}
		
		resp.setRecordsTotal(UserLogDao.getCountUserDetail());
		resp.setRecordsFiltered(resp.getRecordsTotal());
	}

	public static String convertToISTWithoutYear(long epochTime){
		String str = "";
		DateFormat formatter = new SimpleDateFormat("dd MMM  HH:mm:ss ");
		Calendar cal = Calendar.getInstance();
		 cal.setTimeInMillis(epochTime);
		 //System.out.println(inv.getCreatedTime());
		 DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		// System.out.println("time : "+sdf.format(cal.getTime()));
		 try {
			Date date = sdf.parse(sdf.format(cal.getTime()));
			sdf.setTimeZone(TimeZone.getTimeZone("IST"));
			formatter.setTimeZone(TimeZone.getTimeZone("IST"));
			str = formatter.format(date);
		 } catch (ParseException e) {
				
			e.printStackTrace();
		}
		 
		 return str;
	}

	public static void manageResponseAll(List<UserProfile> userprofile,
			PageRequest resp) {
		int count = 1;
		for(UserProfile userpro : userprofile){
			List<String> i = new ArrayList<String>();
			
			i.add((resp.getStart()+count)+"");
			i.add(userpro.getUserName());
			i.add(userpro.getContactNo());
			i.add(convertToISTWithoutYear(userpro.getCreatedTime()));
			i.add(""+userpro.getTransactionCount());
			if(userpro.getUserType() == 0){
				i.add("<html><font color=\"green\">Registered</font></html>");
			}else {
				i.add("<html><font color=\"red\">Un Registered</font></html>");
			}
			
			resp.getData().add(i);
			
			count++;
			
		}
		
		resp.setRecordsTotal(UserLogDao.getCountUserDetailAll());
		resp.setRecordsFiltered(resp.getRecordsTotal());
	}

	public static void manageResponseTransCount(List<UserProfile> userprofile,
			PageRequest resp) {
		int count = 1;
		for(UserProfile userpro : userprofile){
			List<String> i = new ArrayList<String>();
			
			i.add((resp.getStart()+count)+"");
			i.add(userpro.getUserName());
			i.add(userpro.getContactNo());
			i.add(convertToISTWithoutYear(userpro.getCreatedTime()));
			i.add(""+userpro.getTransactionCount());
			if(userpro.getUserType() == 0){
				i.add("<html><font color=\"green\">Registered</font></html>");
			}else {
				i.add("<html><font color=\"red\">Un Registered</font></html>");
			}
			
			resp.getData().add(i);
			
			count++;
			
		}
		
		resp.setRecordsTotal(UserLogDao.getCountUserDetailTransCount());
		resp.setRecordsFiltered(resp.getRecordsTotal());
		
	}

	public static void manRespFortranLog(List<TransactionLog> transactionLog,
			PageRequest resp) {
		int count = 1;
		for(TransactionLog trans : transactionLog){
			List<String> i = new ArrayList<String>();
			
			i.add((resp.getStart()+count)+"");
			i.add(trans.getContactNo());
			i.add(trans.getContactName());
			if(trans.getTransType() == 1){
				i.add("Paid");
			}else if(trans.getTransType() == 2) {
				i.add("Received");
			}else if(trans.getTransType() == 3)
			{
				i.add("Sale");
			}else if(trans.getTransType() == 4){
				i.add("Purchase");
			}
			else{
				i.add("Undefined");
			}
			
			i.add(convertToISTWithoutYear(trans.getCreatedTime()));
			
			
			
			resp.getData().add(i);
			
			count++;
			
		}
		
		resp.setRecordsTotal(TransactionLogDao.getCountUserDetailTransCount());
		resp.setRecordsFiltered(resp.getRecordsTotal());
	}
}
