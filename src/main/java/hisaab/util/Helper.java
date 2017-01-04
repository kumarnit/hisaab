package hisaab.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
	public static String validatePhoneNo(String sPhoneNumber) {
		
	      sPhoneNumber = transformPhoneNo(sPhoneNumber);
	      Pattern pattern = Pattern.compile("\\+\\d{12}");
	      Matcher matcher = pattern.matcher(sPhoneNumber);
	      if (matcher.matches()) {
	    	  System.out.println("Phone Number Valid");
	    	  return sPhoneNumber;
	      }
	      else
	      {
	    	  System.out.println("Phone Number must be in the form XXX-XXXXXXX");
	    	  return null;
	      }
	 }
		
	public static String transformPhoneNo(String sPhoneNumber) {
		
		String originalNumber = sPhoneNumber;
		String defaultCountryCode = "91";
		String defaultCountryNationalPrefix = "0";
		// strip any non-significant characters
		String number = originalNumber.replaceAll("[^0-9+]", ""); 
		// check for prefixes
		if (number.startsWith ("+")) // already in desired format
			sPhoneNumber = number;
		else if (number.startsWith(defaultCountryNationalPrefix))
			sPhoneNumber = number.replaceFirst(defaultCountryNationalPrefix, "+" + defaultCountryCode);
		else {
			sPhoneNumber = "+"+defaultCountryCode+number;
		}
		System.out.println(sPhoneNumber+" === > "+sPhoneNumber.length());
		return sPhoneNumber;
	 }

	
	public static void main(String arg[]){
		validatePhoneNo("+91963963963");
	}
}
