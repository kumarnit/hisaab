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
}
