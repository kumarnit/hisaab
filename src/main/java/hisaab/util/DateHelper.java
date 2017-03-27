package hisaab.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {

	public static String getDateWithoutTime(){
		Calendar cal = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		Date dat = cal.getTime();
		String date = formatter.format(dat);
		return date;
	}

	public static String getDateInString() {
		Calendar cal = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		Date dat = cal.getTime();
		String date = formatter.format(dat);
		return date;
	}
	
	  public static long getDateForOpen(long date) {

	        if (date == 0) {
	            //returns current date
	            Calendar cal = Calendar.getInstance();
	            cal.add(Calendar.DATE, 0);
	            return cal.getTimeInMillis();
	        }

//	        Calendar c = Calendar.getInstance();
//	        //Set time in milliseconds
//	        c.setTimeInMillis(date);
////	        int mYear = c.get(Calendar.YEAR);
//	        int mMonth = c.get(Calendar.MONTH);
//	        int mDay = c.get(Calendar.DAY_OF_MONTH);


	        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("1720"));
	        cal.setTimeInMillis(date);
	        cal.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
	        cal.set(Calendar.HOUR_OF_DAY, 0); //set hour to 00:00 hour
	        cal.set(Calendar.MINUTE, 0); //set minutes to 0 minute
	        cal.set(Calendar.SECOND, 0); //set seconds to 0 second
	        cal.set(Calendar.MILLISECOND, 1); //set milliseconds to first millisecond
          
//	        c.set(mYear, mMonth, mDay, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
//	        c.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));


	        return cal.getTimeInMillis();
	       
	    }
	  public static long getDateForClearTrans(long date) {

	        if (date == 0) {
	            Calendar cal = Calendar.getInstance();
	            cal.add(Calendar.DATE, 0);
	            return cal.getTimeInMillis();
	        }

	        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("1720"));
	        cal.setTimeInMillis(date);
	        cal.set(Calendar.HOUR_OF_DAY, 23); //set hour to 00:00 hour
	        cal.set(Calendar.MINUTE, 59); //set minutes to 0 minute
	        cal.set(Calendar.SECOND, 59); //set seconds to 0 second
	        cal.set(Calendar.MILLISECOND, 999); //set milliseconds to first millisecond


	        return cal.getTimeInMillis();
	       
	    }
	  public static String getDateString(Long currentTime) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(currentTime);
			cal.setTimeZone(TimeZone.getTimeZone("IST"));
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			formatter.setTimeZone(TimeZone.getTimeZone("IST"));
			Date dat = cal.getTime();
			String date = formatter.format(dat);
			return date;
		}
	  public static void main(String arg[]){
		  System.out.println(getDateString(1490604862000l)); 
	  }
	  
}
