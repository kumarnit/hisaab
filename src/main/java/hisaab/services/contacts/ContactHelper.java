package hisaab.services.contacts;

import hisaab.services.contacts.dao.ContactsDao;
import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.Contact;
import hisaab.services.contacts.modal.ContactList;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.notification.webservice.bean.SystemUpdateBean;
import hisaab.services.staff.dao.StaffUserDao;
import hisaab.services.staff.modal.StaffUser;
import hisaab.services.staff.modal.StaffUserRequest;
import hisaab.services.transaction.modal.TransactionDoc;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.util.Constants;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class ContactHelper {
	
	public static String validatePhoneNo(String sPhoneNumber) {
		
	      Pattern pattern = Pattern.compile("\\+\\d{12}");
	      Matcher matcher = pattern.matcher(sPhoneNumber);
	      if (matcher.matches()) {
	    	  System.out.println("Phone Number Valid");
	    	  return sPhoneNumber;
	      }
	      else
	      {
	    	  System.out.println("Phone Number must be in the form XXX-XXXXXXX");
	      }
	      return null;
	 }
	
	public static PhoneNumber validatePhoneNumber(String number){
		PhoneNumberUtil pUtil = PhoneNumberUtil.getInstance();
		boolean valid = false;
		if(number.startsWith("00")){
			number.replaceFirst("00", "");
		}
		if(number.startsWith("0")){
			number.replaceFirst("0", "");
		}
		PhoneNumber phoneNum = null;
		
		try {
			phoneNum = pUtil.parse(number, pUtil.getRegionCodeForCountryCode(Integer.parseInt(
					Constants.DEFAULT_COUNTRY_CODE)));
			
			if(pUtil.isValidNumber(phoneNum)){
				/*if(pUtil.getNumberType(phoneNum) == PhoneNumberType.MOBILE){
					valid = true;

					
				}
				}*/
				valid = true;

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(phoneNum !=null && !valid)
			phoneNum = null;
		
		return phoneNum;
	}
	
	
	public static List<Contact> validateContactNoList(List<Contact> clist){
		
		List<Contact> cont = new ArrayList<Contact>(); 
		Contact conta = null;
		for(Contact contact : clist){
					conta = new Contact();
					String originalNumber = contact.getContactNo();
					String defaultCountryCode = "91";
					String defaultCountryNationalPrefix = "0";
					// strip any non-significant characters
					String number = originalNumber.replaceAll("[^0-9+]", ""); 
					// check for prefixes
					if (number.startsWith ("+"))// already in desired format
					{
						if(validatePhoneNo(number) != null){
							conta.setContactNo(number);
							conta.setName(contact.getName());
							cont.add(conta)	;
						}
					}
					else if (number.startsWith(defaultCountryNationalPrefix)){
						String num = number.replaceFirst(defaultCountryNationalPrefix, "+" + defaultCountryCode);
						if(validatePhoneNo(num) != null){
							conta.setContactNo(num);
							conta.setName(contact.getName());
							cont.add(conta)	;
						}
					}	
					else {
						String num = "+"+defaultCountryCode+number;
						if(validatePhoneNo(num) != null){
							conta.setContactNo(num);
							conta.setName(contact.getName());
							cont.add(conta)	;
						}
						
					}
				
			}
		
		return cont;
	}

	
	
	public static List<Contact> validateContactNoList2(List<Contact> clist){
		
		List<Contact> cont = new ArrayList<Contact>(); 
		Contact conta = null;
		for(Contact contact : clist){
					conta = new Contact();
					String originalNumber = contact.getContactNo();
					String defaultCountryCode = "91";
					String defaultCountryNationalPrefix = "0";
					// strip any non-significant characters
					String number = originalNumber.replaceAll("[^0-9+]", ""); 
					// check for prefixes
						PhoneNumber pnum = validatePhoneNumber(number);
						if(pnum != null){
							conta.setContactNo(pnum.getNationalNumber()+"");
							conta.setName(contact.getName());
							cont.add(conta);
						}
						
					}
				
			
		
		return cont;
	}
	              
	public static List<String> getContactNoList(List<Contact> contactList){
		List<String> contactNos = new ArrayList<String>();
		
		for(Contact contact : contactList){
			if(!contactNos.contains(contact.getContactNo()))
				contactNos.add(contact.getContactNo());
		}
		return contactNos;
	}

	
public static List<FriendContact> getFriends(ContactList clist, long count){
		
		HashMap<String, Contact> contactMap	 = 	new HashMap<String, Contact>();
		for(Contact cont : clist.getContactList()){
			contactMap.put(cont.getContactNo(), cont);
		}
		
		List<String> contactNos = ContactHelper.getContactNoList(clist.getContactList());
		List<String> userContactNos = UserDao.getContactNoListOfUsers();
		List<String> tempContacts = contactNos;
		List<String> unmanaged = contactNos; 
		unmanaged.removeAll(userContactNos);
		UserDao.addUnRegisteredUserInBulk(unmanaged,contactMap);
		List<FriendContact> friends = new ArrayList<FriendContact>();
		
		userContactNos = UserDao.getContactNoListOfUsers();
		tempContacts.retainAll(userContactNos);
//		tempContacts.addAll(unmanaged);
		if(!tempContacts.isEmpty()){
			
			HashMap<String, UserMaster> userMap	 = 	 UserDao.getUserListFronNumbers(tempContacts);
			
			
			
			for(String number : tempContacts ){
				Contact cont = contactMap.get(number);
				UserMaster usr = userMap.get(number);
				System.out.println(cont.getName());
				FriendContact frndc = new FriendContact();
				frndc.setId(++count);
				frndc.setContactName(cont.getName());
				frndc.setCreatedTime(System.currentTimeMillis());
				frndc.setUpdatedTime(System.currentTimeMillis());
				frndc.setContactNo(number);
				frndc.setFrndId(""+usr.getUserId());
				friends.add(frndc);
			}
		}
		
		return friends;
	}
	
	public static void main(String[] args) {
		/*List<Long> ids = new ArrayList<Long>();
		for(long i = 0 ; i<5; i++){
			ids.add(i+1);
		}
		*/
		System.out.println("=="+validatePhoneNumber("7276603191"));
		
		/*ids.remove(3);
		
		System.out.println(ids);
		System.out.println("-- "+ids.get(3));*/
	}

	public static void addStaffContact(Contact contact,long reqId,StaffUser staffuser) {
		
		UserMaster usermaster = UserDao.getUserByContactNo(contact.getContactNo());
		StaffUserRequest req = StaffUserDao.getStaffRequestsByReqId(reqId);
		UserMaster mainuser =  (UserMaster) UserDao.getUserByIds(Arrays.asList(req.getOwnerId())).get(0);
		ContactList contlist = new ContactList();
		Contact cont = new Contact();
		
		FriendList frndlist = null;
		FriendContact frnd = null;
		FriendContact fr = null;
		FriendContact frstaff = null;
		long epoch = System.currentTimeMillis();
		if(usermaster != null ){
			fr = FriendsDao.getFriendForWeb(""+usermaster.getUserId(), 0, mainuser);
			
			if(fr == null){
				contlist = new ContactList();
				cont = new Contact();
				
				cont.setContactNo(contact.getContactNo());
				cont.setCountryCode(contact.getCountryCode());
				cont.setName(req.getDisplayName());
				contlist.setUserId(""+req.getOwnerId());
				contlist.setContactList(Arrays.asList(cont));
				ContactsDao.addContacts(contlist);
				
				
				frndlist = FriendsDao.getAssociatedUserDoc(mainuser);
				frnd = new FriendContact();
				frnd.setContactNo(contact.getContactNo());
				frnd.setFrndStatus(0);
				frnd.setFrndId(""+usermaster.getUserId());
				frnd.setCreatedTime(epoch);
				frnd.setUpdatedTime(epoch);
				
				long count = frndlist.getIdCount();
				frnd.setId(++count);
				frndlist.setIdCount(count);
				frnd.setContactName(req.getDisplayName());
				frndlist.setUserId(""+req.getOwnerId());
				frndlist.setFriends(Arrays.asList(frnd));
				FriendsDao.addFriends(frndlist);
			}
		}
		frstaff = FriendsDao.getFriendForWeb(staffuser.getStaffId(), 0, mainuser);
		if(frstaff == null){
			frndlist = FriendsDao.getAssociatedUserDoc(mainuser);
			frnd = new FriendContact();
			frnd.setContactNo(contact.getContactNo());
			frnd.setFrndStatus(Constants.STAFF_USER);
			frnd.setFrndId(staffuser.getStaffId());
			frnd.setCreatedTime(epoch);
			frnd.setUpdatedTime(epoch);
			
			long count = frndlist.getIdCount();
			frnd.setId(++count);
			frndlist.setIdCount(count);
			String stringFromBytes = null;
			try {
				stringFromBytes = new String(req.getDisplayName().getBytes("UTF-16"), "UTF-16");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			frnd.setContactName(stringFromBytes);
			frndlist.setUserId(""+req.getOwnerId());
			frndlist.setFriends(Arrays.asList(frnd));
			FriendsDao.addFriends(frndlist);
		}
	}
	
	
	/**
     * add friend  to friendcontact list of user 2
     ***/
	public static void checkAndAddAssociate(FriendContact  frnd, UserMaster user, String uid, TransactionDoc transDoc){
	 		if(frnd.getFrndStatus() == 0){
				long id = 0;
				try {
					id = Long.parseLong(uid);
				} catch (Exception e) {
					System.out.println("=="+e.getMessage());
					System.out.println("in add checkAndAddAssociate parsing uid");
				}
				if(id>0){
					UserMaster usermaster = UserDao.getUserForWeb(id);
					if(usermaster != null && usermaster.getUserType() != Constants.NOT_REGISTERED_USER){
						FriendContact frndcon = FriendsDao.getFriendForWeb(""+user.getUserId(), 0, usermaster);
						if(frndcon == null){
							FriendList frndlist = FriendsDao.getAssociatedUserDoc(usermaster);
							Contact contact = ContactsDao.getContactForWeb(user.getContactNo(), ""+uid);
							FriendContact frndsc = new FriendContact();
							if(contact!=null){
								System.out.println("===> "+contact.getName());
								frndsc.setContactName(contact.getName());
							}
							
							frndsc.setContactNo(user.getContactNo());
							frndsc.setCreatedTime(System.currentTimeMillis());
							frndsc.setFrndId(""+user.getUserId());
							frndsc.setId(frndlist.getIdCount()+1);
							frndsc.setTransactionDocId(transDoc.getIdString());
							frndsc.setFrndStatus(0);
							frndlist.setFriends(Arrays.asList(frndsc));
							frndlist.setIdCount(frndlist.getIdCount()+1);
							
							FriendsDao.addFriends(frndlist);
						}
					}
				}
			}
	}
	
}
