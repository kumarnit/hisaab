package hisaab.services.pull.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import hisaab.services.contacts.dao.FriendsDao;
import hisaab.services.contacts.modal.FriendContact;
import hisaab.services.contacts.modal.FriendList;
import hisaab.services.pull.modal.PullBean;
import hisaab.services.pull.modal.PullStaffBean;
import hisaab.services.staff.dao.StaffUserDao;
import hisaab.services.staff.modal.StaffUser;
import hisaab.services.transaction.dao.TransactionDao;
import hisaab.services.transaction.modal.Transaction;
import hisaab.services.transaction.openingbalance.dao.OpeningBalDao;
import hisaab.services.transaction.request.dao.ModificationRequestDao;
import hisaab.services.transaction.staff_transaction.dao.DeletedStaffTransactionDao;
import hisaab.services.transaction.staff_transaction.dao.StaffTransactionDao;
import hisaab.services.user.dao.PrivateUserDao;
import hisaab.services.user.dao.UserDao;
import hisaab.services.user.modal.UserMaster;
import hisaab.services.user.modal.UserProfile;
import hisaab.util.Constants;

public class PullHelper {
	
	public static PullBean getUserData(long pullTime, UserMaster user) throws JsonGenerationException, JsonMappingException, IOException{
//		FriendContact frndCon = null;
		List<Transaction> listtran = new ArrayList<Transaction>();
//		HashMap<String,FriendContact> usr = new HashMap<String,FriendContact>();
		List<String> unmanUserList = new ArrayList<String>();
		
//		List<UserProfile> userpro = new ArrayList<UserProfile>();
//		HashMap<Long,FriendContact> usr = new HashMap<Long,FriendContact>();
		
		PullBean pullBean = new PullBean();
		ObjectMapper mapper = new ObjectMapper();
		pullBean.setFriendList(FriendsDao.pullAssociatedUserDocUpdated(user,pullTime));
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -5);
		if(pullTime > cal.getTimeInMillis()){
			pullBean.setTransactionList(TransactionDao.pullTransactionsSql(user,pullTime, Constants.TRANS_APROOVED));
			pullBean.setModifiedTransactionList(TransactionDao.pullTransactionsSql(user,pullTime, Constants.TRANS_NEED_TO_APROOVE));
			pullBean.getTransactionList().addAll(TransactionDao.pullPrivateUserTransaction(user, pullTime));
		}
		else{
			pullBean.setTransactionList(TransactionDao.pullTransactions1(user, pullTime));
			pullBean.setModifiedTransactionList(TransactionDao.pullModifiedTransactions(user, pullTime));
		}
		
		pullBean.setStaffRequestsForYou(StaffUserDao.getStaffRequestsForUser(user.getContactNo(), pullTime));
		FriendList frndlist = FriendsDao.getAssociatedUserDocForPull(user);
		if(frndlist != null){
			for(FriendContact frnd : frndlist.friends){
				if(frnd.getFrndStatus() == 0 || frnd.getFrndStatus() == Constants.NOT_REGISTERED_USER)
					{
					pullBean.getUserIds().add(frnd.getFrndId());
//					usr.put(frnd.getFrndId(), frnd);
					}
				if(frnd.getFrndStatus() == Constants.NOT_REGISTERED_USER)
					unmanUserList.add(frnd.getFrndId());
			}
		}

		pullBean.setStaffProfiles(StaffUserDao.getStaffUsers(user, pullTime));
		pullBean.setStaffRequests(StaffUserDao.getStaffRequestsByUser(user, pullTime));
		pullBean.setPrivateuser(PrivateUserDao.getPrivateUser(user, pullTime));
		pullBean.setModificationRequest(ModificationRequestDao.pullModificationRequest(user,pullTime));
		pullBean.setListOfDeletedStaffTransaction(DeletedStaffTransactionDao.pullDeletedTransactionId(""+user.getUserId(),pullTime,true));
		System.out.println(mapper.writeValueAsString(pullBean.getUserIds()));
		pullBean.setUserProfileList(UserDao.pullUsersByUserIds(user,pullBean.getUserIds(),pullTime));
		/**added staff transaction
		 * */
		listtran = pullBean.getTransactionList();
		listtran.addAll(StaffTransactionDao.getStaffTransactionforUser(user, pullTime));
		pullBean.setTransactionList(listtran);
		/*for (UserProfile uf : pullBean.getUserProfileList()){
			frndCon =   usr.get(""+uf.getUserId());

			 if(frndCon != null)
			    uf.setDisplayName(frndCon.getContactName());
			   	userpro.add(uf);
			  }
		pullBean.setUserProfileList(userpro);*/
		pullBean.setOpeningBalance(OpeningBalDao.pullOpeningBalance(user,pullTime));
		return pullBean;
	}
	
	
	/**
	 * pull data for staff users
	 **/
	public static PullStaffBean getUserDataForStaffUser(long pullTime, StaffUser user) throws JsonGenerationException, JsonMappingException, IOException{
		FriendContact frndCon = null;
		
		HashMap<String,FriendContact> usr = new HashMap<String,FriendContact>();
		List<String> unmanUserList = new ArrayList<String>();
		
		List<UserProfile> userpro = new ArrayList<UserProfile>();
//		HashMap<Long,FriendContact> usr = new HashMap<Long,FriendContact>();
		 
		PullStaffBean pullBean = new PullStaffBean();
		ObjectMapper mapper = new ObjectMapper();
		UserMaster ownerUser = UserDao.getUserForWeb(user.getOwnerId());
		pullBean.setFriendList(FriendsDao.pullFriendsOfOwner(""+ownerUser.getUserId(),pullTime));
		pullBean.setTransactionList(StaffTransactionDao.getTransactionForStaff(user, pullTime));
		
		FriendList friendList = FriendsDao.getAssociatedUserDoc(ownerUser);		
		for(FriendContact frnd : friendList.getFriends()){
			if(frnd.getFrndStatus() == 0 || frnd.getFrndStatus() == Constants.NOT_REGISTERED_USER)
				pullBean.getUserIds().add(frnd.getFrndId());
			usr.put(frnd.getFrndId(), frnd);
			if(frnd.getFrndStatus() == Constants.NOT_REGISTERED_USER)
				unmanUserList.add(frnd.getFrndId());
		}
		pullBean.setListOfDeletedStaffTransaction(DeletedStaffTransactionDao.pullDeletedTransactionId(""+ownerUser.getUserId(),pullTime,false));
		pullBean.setPrivateuser(PrivateUserDao.getPrivateUser(ownerUser, pullTime));
		System.out.println(mapper.writeValueAsString(pullBean.getUserIds()));
		pullBean.setUserProfileList(UserDao.pullUsersByUserIds(ownerUser,pullBean.getUserIds(),pullTime));
		
		for (UserProfile uf : pullBean.getUserProfileList()){

			 frndCon =   usr.get(""+uf.getUserId());

			frndCon =   usr.get(uf.getUserId());

			 if(frndCon != null)
			    uf.setDisplayName(frndCon.getContactName());
			   	userpro.add(uf);
			  }
		pullBean.setUserProfileList(userpro);
		return pullBean;
	}
	
}
