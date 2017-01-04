package hisaab.util;

import java.util.Arrays;
import java.util.List;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber.CountryCodeSource;

public class PhoneNumberValidate {

	
	public static boolean isValidNumber(String aNumber)
	{
	    boolean result = false;
	    String dumy = aNumber;
	    aNumber = aNumber.trim();

	    if (aNumber.startsWith("00"))
	    {
	        // Replace 00 at beginning with +
	        aNumber = aNumber.replace("00", "");
	    }
	    if(aNumber.startsWith("0")){
	    	aNumber = aNumber.replaceFirst("0", "");
	    }
	    try
	    {
	    	PhoneNumberUtil pUtil = PhoneNumberUtil.getInstance();
//	    	System.out.println("ex : "+pUtil.getExampleNumber("IN"));
	        PhoneNumber phoneNumber = null ;
//	        if(aNumber.length() == 13)
//	        	phoneNumber = PhoneNumberUtil.getInstance().parse(aNumber, "");
//	        else 
	        	phoneNumber = PhoneNumberUtil.getInstance().parse(aNumber, PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(+91));
	        System.out.println(dumy+" is valid "+pUtil.isValidNumber(phoneNumber));
	        
	        if(pUtil.isValidNumber(phoneNumber)){
	        	if(pUtil.getNumberType(phoneNumber) == PhoneNumberUtil.PhoneNumberType.MOBILE)
	        		System.out.println(dumy+" is mobile "+pUtil.isValidNumber(phoneNumber));
	        	else{
	        		System.out.println(dumy+" is landline "+pUtil.isValidNumber(phoneNumber));
	        	
	        	}
	        }
	        
	        
	        System.out.println("retrived format : "+phoneNumber.toString());
	        System.out.println("aNumber : "+aNumber);
	        System.out.println("country Code : "+phoneNumber.hasCountryCode());
	        CountryCodeSource cs = phoneNumber.getCountryCodeSource();
//	        cs.
	    }
	    catch(Exception e)
	    {
	        // Exception means is no valid number
	    	System.out.println(dumy+ "  is invalid number");
	    }

	    return result; 
	}
	public static void main(String[] args) {
		List<String>  numbers = Arrays.asList("918087611301", "+91 808-76-113011", "008087611301", "001236747747", "8087611301", "1-541-754-3010","712-2124212");
		
		/*for(String number : numbers){
			System.out.println("===================\n\n");
			isValidNumber(number);
			System.out.println("\n\n===================");
		}*/
		
		isValidNumber("+916525412563");
	}
	
	
}
