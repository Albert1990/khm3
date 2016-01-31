package com.brainSocket.khednima3ak3.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.os.Handler;

import com.brainSocket.khednima3ak3.KhedniApp;
import com.brainSocket.khednima3ak3.data.GCMHandler.AppGcmListener;
import com.brainSocket.khednima3ak3.model.AppArea;
import com.brainSocket.khednima3ak3.model.AppContact;
import com.brainSocket.khednima3ak3.model.AppConversation;
import com.brainSocket.khednima3ak3.model.AppEvent;
import com.brainSocket.khednima3ak3.model.AppMessage;
import com.brainSocket.khednima3ak3.model.AppSession;
import com.brainSocket.khednima3ak3.model.AppTripsSet;
import com.brainSocket.khednima3ak3.model.AppUser;
import com.brainSocket.khednima3ak3.model.AppUserStats;
import com.brainSocket.khednima3ak3.model.AppUser.GENDER;

/**
 * This class will be responsible for requesting new data from the data providers
 * like  and invoking the callback when ready plus handling multithreading when required 
 * @author MolhamStein
 *
 */
public class DataStore {
	
	public static final String SABEEN_WEB_LINK = "http://khednym3ak.com" ;
	public static final String SABEEN_UPDATE_LINK = "https://play.google.com/store/apps/details?id=com.brainSocket.khednima3ak" ;
	public final static String VERSION_NUMBER = "1"; 
	
	public enum GENERIC_ERROR_TYPE {NO_ERROR, UNDEFINED_ERROR, NO_CONNECTION, NOT_LOGGED_IN} ;
	public enum App_ACCESS_MODE {NOT_LOGGED_IN, NOT_VERIFIED, VERIFIED} ;
	public enum APP_VERSION_STATUS {VALID, VERSION_INVALID, UPDATE_AVAILABLE} ;
	
	private static DataStore instance = null ;
	
	private static ServerAccess serverHandler = null;
	private static DataCacheProvider cacheProvider = null;
	private static Handler handler = null;
	private ArrayList<DataStoreUpdatListener> updateListeners ;
	
	private String apiAccessToken = null;
	private ArrayList<AppSession> sessions = null;
	private ArrayList<AppConversation> conversations; 
	//private ArrayList<AppContact> contacts ;
	private ArrayList<AppContact> enrolledFriensds ;
	private AppTripsSet allTripsSet ; // contains the initial data displayed in the trips set before performing any cutom  search
	private AppTripsSet searchTripSet ; // Contains the data retrieved after quering the server for trips with custom parameters 
	
	private AppUserStats myStats ;
	private HashMap<String, ArrayList<AppMessage>> mapSessionMessages ;
/*	private ArrayList<String> whatsAppFriendsLocalIds = null ;
	private ArrayList<String> viberFriendsLocalIds = null ;
*/	private AppUser me = null;
	public static String meId = "";
	private App_ACCESS_MODE accessMode ;
	private APP_VERSION_STATUS versionStatus ;
	private ArrayList<AppArea> arrayAreas ;	
	
	// tutorial flags
	boolean isTutPlantDisplayed ;
	boolean isTutSelfDisplayed ;
	boolean isTutSendDisplayed ;
	boolean isTutOpenTaskDisplayed ;
	
	// internal data
	private final int SCHEDULE_TIME_SESSIONS_MULTIPLIER = 2*60*1000; // update Sessions each 2 minutes 
	private final int SCHEDULE_LONG_TERM_UPDATE_TIME_SPAN = 12*60*60*1000 ; // update long term data each 12 hours
	private long lastSessionsUpdateLocalTime;
	private long lastSessionsUpdateServerTime;
	private long lastLongtTermUpdateTime;
	private long countTimerTick = 0; 
	private final int TIMER_TICK = 10000; // 10 seconds
	
	private static boolean isUpdatingDataStore  = false; 
	
	private DataStore() { 
		try {
			serverHandler = ServerAccess.getInstance();
			cacheProvider = DataCacheProvider.getInstance();
			FacebookProvider.getInstance();
			handler = new Handler() ;
			updateListeners = new ArrayList<DataStore.DataStoreUpdatListener>() ;
			getLocalData();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DataStore getInstance() {
		if(instance == null) {
			instance = new DataStore();
		}
		return instance;
	}
	
	/**
	 * user to invoke the DataRequestCallback on the main thread 
	 */	
	private void invokeCallback(final DataRequestCallback callback ,final boolean success, final ServerResult data){
		handler.post(new Runnable() {
			@Override
			public void run() {
				if(callback == null )
					return ;
				callback.onDataReady(data,success);
			}
		});
	}
	
	private void getLocalData(){
		me = cacheProvider.getStoredMe() ;
		if(me!=null)
			meId = me.getId() ;
		else
			meId = null;
		apiAccessToken = cacheProvider.getStoredAccessToken();
		accessMode = cacheProvider.getStoredAppAccessMode() ;
		versionStatus = cacheProvider.getStoredAppVersionStatus() ;
		arrayAreas = cacheProvider.getStoredAzkar() ;
		mapSessionMessages = cacheProvider.getStoredSessionsEvents() ;
		enrolledFriensds = cacheProvider.getStoredEnrolledFriends() ;
		lastSessionsUpdateLocalTime = cacheProvider.getStoredLasteSessoinsUpdateLocalTimestamp() ;
		lastSessionsUpdateServerTime = cacheProvider.getStoredLasteSessoinsUpdateServerTimestamp() ;
		lastLongtTermUpdateTime = cacheProvider.getStoredLastlongTermUpdateTimestamp() ;
		myStats = cacheProvider.getStoredUserStats() ;
		sessions = cacheProvider.getStoredSessions();
		allTripsSet = cacheProvider.getStoredAllTripsSet();
		
		// tut
		isTutPlantDisplayed = cacheProvider.getStoredTutPlantFlag() ;
		isTutSelfDisplayed = cacheProvider.getStoredTutSelfFlag() ;
		isTutSendDisplayed = cacheProvider.getStoredTutSendZickerFlag();
		isTutOpenTaskDisplayed = cacheProvider.getStoredTutOpenTaskFlag();
	}
	
	public void clearLocalData(){
		try {
			cacheProvider.clearCache();
			apiAccessToken = null;
			GCMHandler.reset(KhedniApp.getAppContext());
			getLocalData();
		}
		catch (Exception e) {}
	}
	
	public String getApiAccessToken() {
		return apiAccessToken;
	}
	public void setApiAccessToken(String apiAccessToken) {
		this.apiAccessToken = apiAccessToken;
		cacheProvider.storeAccessToken(apiAccessToken);
	}
	//--------------------
	// DataStore Update
	//-------------------------------------------
		
	public void startScheduledUpdates() {
		try {
			// reset counter
			countTimerTick = 0;
			// start schedule timer
			handler.postDelayed(runnableUpdate, TIMER_TICK);
		}catch (Exception e) {}
	}
	
	public void stopScheduledUpdates() {
		try {
			handler.removeCallbacks(runnableUpdate);
		}catch (Exception e) {}
	}
	
	Runnable runnableUpdate = new Runnable() {		
		@Override
		public void run() {
			triggerScheduledUpdates();
			handler.postDelayed(runnableUpdate, TIMER_TICK);
		}
	};
	
	public void triggerScheduledUpdates (){
		// check schedules and trigger updates accordingly
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(isUpdatingDataStore)
						return;
					countTimerTick = countTimerTick + TIMER_TICK;
					if((countTimerTick % SCHEDULE_TIME_SESSIONS_MULTIPLIER) == 0) { // check conversations schedule
						isUpdatingDataStore = true ;
						//contacts = ContactsMgr.getInstance().getLocalContacts(KhedniApp.getAppContext());
						enrolledFriensds = updateEnrolledFriends() ;
						updateSessionList(false);
						broadcastDataStoreUpdate();
					}
					long currentTimestamp = System.currentTimeMillis();
					if(currentTimestamp - lastLongtTermUpdateTime >  SCHEDULE_LONG_TERM_UPDATE_TIME_SPAN) {isUpdatingDataStore = true ;
						updateSessionList(true);
						//contacts = ContactsMgr.getInstance().getLocalContacts(KhedniApp.getAppContext());
						enrolledFriensds = updateEnrolledFriends() ;
						arrayAreas = updateAreas() ;
						lastLongtTermUpdateTime = System.currentTimeMillis() ;
						cacheProvider.storeLastLongTermUpdateTimestamp(lastLongtTermUpdateTime);
						countTimerTick = 0;
						broadcastDataStoreUpdate();
					}
					isUpdatingDataStore = false ;
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	// TIMER AND SYNC //
	//
	
	public void triggerDataStoreUpdate(final boolean fullUpdate){
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(isUpdatingDataStore)
					return ;
				//contacts = ContactsMgr.getInstance().getLocalContacts(KhedniApp.getAppContext());
				enrolledFriensds = updateEnrolledFriends() ;
				updateSessionList(fullUpdate);
				arrayAreas = updateAreas() ;

				broadcastDataStoreUpdate();
				isUpdatingDataStore = false ;
			}
		}).start();
	}
		
	private void broadcastDataStoreUpdate(){
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (DataStoreUpdatListener listener : updateListeners) {
					listener.onDataStoreUpdate();
				}
			}
		});
	}
	
	private void broadcastNewNotificationsAvailable(){
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (DataStoreUpdatListener listener : updateListeners) {
					listener.onNewNotificationsAvailable();;
				}
			}
		});
	}
	
	private void broadcastNewChatMessagesAvailable(){
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (DataStoreUpdatListener listener : updateListeners) {
					listener.onNewMessagesAvailable();;
				}
			}
		});
	}
	public void removeUpdateBroadcastListener(DataStoreUpdatListener listener){
		if(updateListeners != null && updateListeners.contains(listener))
			updateListeners.remove(listener);
	}
	public void addUpdateBroadcastListener(DataStoreUpdatListener listener){
		if(updateListeners == null)
			updateListeners = new ArrayList<DataStore.DataStoreUpdatListener>() ;
		if(!updateListeners.contains(listener))
			updateListeners.add(listener);
	}
	
//	public ArrayList<AppContact> getContacts() {
//		return contacts;
//	}
	
	//
	// USER //
	
	public boolean isUserLoggedIn() {
		return (me!=null) ? true : false;
	}
	
	public AppUser getMe() {
		if(me == null) {
			me = cacheProvider.getStoredMe() ;
			if(me != null)
				meId = me.getId();
		}
		return me;
	}
	public void setMe(AppUser me){
		cacheProvider.storeMe(me);
		this.me = me ;
		meId = me.getId();
		apiAccessToken = me.getAccessToken();
	}
	
	public void setAccessMode(App_ACCESS_MODE accessMode) {
		this.accessMode = accessMode;
		cacheProvider.storeAppAccessMode(accessMode);
	}
	public App_ACCESS_MODE getAccessMode() {
		return accessMode;
	}
	
	public void setVersionStatus(APP_VERSION_STATUS versionStatus) {
		this.versionStatus = versionStatus;
		cacheProvider.storeAppVersionStatus(versionStatus);
	}
	public APP_VERSION_STATUS getVersionStatus() {
		return versionStatus;
	}

	/**
	 * attempting login using phone number
	 * @param phoneNumfinal
	 * @param callback
	 */
	public void attemptLogin(final String phoneNumfinal, final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.login(phoneNumfinal);
				if(result.connectionFailed()){
					success = false ;
				}else{
					if(result.isValid()){
						AppUser me= (AppUser) result.getPairs().get("appUser") ;
						apiAccessToken = me.getAccessToken();
						meId = me.getId();
						setApiAccessToken(apiAccessToken);
						setMe(me);
					}
				}
				invokeCallback(callback, success, result); // invoking the callback
			}
		}).start();	
	}
	
	public void getMe( final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				try{
					boolean success = true ;
					ServerResult result = serverHandler.getMe(apiAccessToken);
					if(result.connectionFailed()){
						success = false ;
					}else{
						if(result.isValid()){
							AppUser me= (AppUser) result.getPairs().get("appUser") ;
							apiAccessToken = me.getAccessToken();
							meId = me.getId();
							setApiAccessToken(apiAccessToken);
							setMe(me);
						}
					}
					if(callback != null)
						invokeCallback(callback, success, result); // invoking the callback
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();	
	}
	
	/** 
	 * @param phoneNumfinal
	 * @param username
	 * @param gender
	 * @param FbID : pass null if signing-up without facebook
	 * @param FbAccessToken : pass null if signing-up without facebook
	 * @param callback
	 */
	public void attemptSignUp(final String phoneNum, final String firstName,final String lastName,final GENDER gender, final String versionId,final String FBID, final DataRequestCallback callback) {
		
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.registerUser(firstName, lastName, phoneNum, gender==GENDER.MALE, versionId, FBID);
				if(result.connectionFailed()){
					success = false ;
				}else{
					try {
						AppUser me= (AppUser) result.getPairs().get("appUser") ;
						apiAccessToken = me.getAccessToken();
						setApiAccessToken(apiAccessToken);
					} catch (Exception e) {
						success = false ;
					}

				}
				invokeCallback(callback, success, result); // invoking the callback
			}
		}).start();	
	}
	
	public void connectWithFB(final String fbAccessToken,  final DataRequestCallback callback) {
		
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.importFacebookFriends(apiAccessToken, fbAccessToken);
				if(result.connectionFailed()){
					success = false ;
				}else{
					//TODO : add contacts if not already added
				}
				if(callback != null)
					invokeCallback(callback, success, result); 
			}
		}).start();	
	}
	
	public void logoutUser() {
		try {
			stopScheduledUpdates();
			clearLocalData();
		}
		catch (Exception e) {}
	}
	
	public void requestVerificationMsg( final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.requestVerificationMsg(apiAccessToken);
				if(result.connectionFailed()){
					success = false ;
				}
				if(callback != null)
					invokeCallback(callback, success, result); 
			}
		}).start();	
	}
	
	public void verifyAccount(final String verifMsg,  final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.verifyAccount(apiAccessToken, verifMsg);
				if(result.connectionFailed()){
					success = false ;
				}else{
					//boolean loginSuccess = (Boolean) data.get("verified");
					if(!result.isValid()){
						setAccessMode(App_ACCESS_MODE.NOT_VERIFIED);
					}else{
						setAccessMode(App_ACCESS_MODE.VERIFIED);
					}
				}
				if(callback != null)
					invokeCallback(callback, success, result); 
			}
		}).start();	
	}
	
	
	public void checkVersionValid(final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.isVersionValid(VERSION_NUMBER);
				if(result.connectionFailed()){
					success = false ;
				}else{
					//boolean loginSuccess = (Boolean) data.get("verified");
					if(!result.connectionFailed()){
						HashMap<String, Object> data = result.getPairs() ;
						APP_VERSION_STATUS stat = (APP_VERSION_STATUS) data.get("status");
						setVersionStatus(stat);
					}
				}
				if(callback != null)
					invokeCallback(callback, success, result); 
			}
		}).start();	
	}
	
	public void searchForTrip(final String fromId, final String toId, final int page, final DataRequestCallback callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String APIAccessToken = getApiAccessToken();
				boolean success = true;
				ServerResult result = serverHandler.searchForTrips(APIAccessToken,fromId,toId,page);
				if (result.connectionFailed())
					success = false;
				else{
					// TODO handle merge pages
					if( (fromId == null || fromId.isEmpty()) &&
						(toId == null || toId.isEmpty())){
						allTripsSet = (AppTripsSet) result.getValue("tripSet");
						cacheProvider.storeAllTripsSet(allTripsSet);
					}else{
						searchTripSet = (AppTripsSet) result.getValue("tripSet");
						cacheProvider.storeSearchTripsSet(searchTripSet);
					}
					invokeCallback(callback, success, result);
				}
			}
		}).start();
	}
	
	public void createTrip( final int pricePerPassenger, final String launchTime, final String origId, final String destId, final long startDate, final int[] weekdays, final boolean isRecurring,final boolean displayMobileNumber, final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.createTrip(apiAccessToken, pricePerPassenger, launchTime, origId, destId, startDate, weekdays, isRecurring, displayMobileNumber);
				if(result.connectionFailed()){
					success = false ;
				}else{
//					myStats = (AppUserStats) result.getValue("stats");
//					cacheProvider.storeUserStats(myStats);
				}
				invokeCallback(callback, success, result);
			}
		}).start();	
	}
	
	public void attemptDeleteTrip( final String tripId,final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.deleteTrip(apiAccessToken, tripId);
				if(result.connectionFailed()){
					success = false ;
				}
				invokeCallback(callback, success, result);
			}
		}).start();	
	}
	
	public void attemptMarkTripAsFull( final String tripId,final boolean isFull,final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.markTripAsFull(apiAccessToken, tripId,isFull);
				if(result.connectionFailed()){
					success = false ;
				}
				invokeCallback(callback, success, result);
			}
		}).start();	
	}
	
	public void respondToRideRequest( final String bookingId, final boolean isAccepted, final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.respondToRideRequest(apiAccessToken, bookingId, isAccepted);
				if(result.connectionFailed()){
					success = false ;
				}
				invokeCallback(callback, success, result);
			}
		}).start();	
	}
	
	public void sendRideRequest( final String fromPlaceId, final String toPlaceId, final String tripId, final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.sendRideRequest(apiAccessToken, fromPlaceId, toPlaceId, tripId);
				if(result.connectionFailed()){
					success = false ;
				}
				invokeCallback(callback, success, result);
			}
		}).start();	
	}
	
	public void sendChatMessage(final String peerId, final String msg, final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.sendChatMsg(apiAccessToken, peerId, msg);
				if(result.connectionFailed()){
					success = false ;
				}else{
//					myStats = (AppUserStats) result.getValue("stats");
//					cacheProvider.storeUserStats(myStats);
				}
				invokeCallback(callback, success, result);
			}
		}).start();	
	}
	
	public void getUserStats(final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.getUserStats(apiAccessToken);
				if(result.connectionFailed()){
					success = false ;
				}else{
					myStats = (AppUserStats) result.getValue("stats");
					cacheProvider.storeUserStats(myStats);
				}
				invokeCallback(callback, success, result);
			}
		}).start();	
	}
	
	public AppUserStats getMyStats() {
		return myStats;
	}
	
	
	public void addSelfZeker(final int counter, final String contentId,
			final DataRequestCallback callback) {
		System.out.println("Datastore.addSelfZeker() is called!!");
		new Thread(new Runnable() {

			@Override
			public void run() {
				String APIAccessToken = getApiAccessToken();
				boolean success = true;
				ServerResult result = serverHandler.addSelfZeker(APIAccessToken, counter, contentId);
				System.out.println("result  " + "result.getFlag(): " + result.getFlag() + "result.connectionFailed(): "+ result.connectionFailed());
				if (result.connectionFailed())
					success = false;
				else
					invokeCallback(callback, success, result);
			}
		}).start();
	}
	
	
	
	public void requestSessionEvents(final String SessionId, final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				
				boolean success = true ;
				ServerResult result = serverHandler.getSessionEvents(apiAccessToken, SessionId);
				if(result.connectionFailed()){
					success = false ;
				}else{
					if(mapSessionMessages == null)
						mapSessionMessages = new HashMap<String, ArrayList<AppMessage>>() ;
					ArrayList<AppMessage> events = (ArrayList<AppMessage>) result.getValue("events") ;
					if(events != null){
						mapSessionMessages.put(SessionId, events) ;
						cacheProvider.storeSessionsEvents(mapSessionMessages);
					}
				}
				if(callback != null)
					invokeCallback(callback, success, result);
			}
		}).start();	
	}
	
	public void requestUndoneTasks(final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.getMyTrips(apiAccessToken);
				if(result.connectionFailed()){
					success = false ;
				}
				if(callback != null)
					invokeCallback(callback, success, result);
			}
		}).start();	
	}
	
	public void requesEventNotifications(final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.getEventNotifications(apiAccessToken);
				if(result.connectionFailed()){
					success = false ;
				}
				if(callback != null)
					invokeCallback(callback, success, result);
			}
		}).start();	
	}
	
	// GCM
	
	public void requestGCMRegsitrationId() {
		
		GCMHandler.requestGCMRegistrationId(KhedniApp.getAppContext(), new AppGcmListener() {			
			@Override
			public void onRegistratinId(final String regId) {
				sendGCMRegistratinId(regId);
			}
			
			
			@Override
			public void onPlayServicesError() {
				//Configuration.displayToast("onPlayServicesError", Toast.LENGTH_SHORT);
			}
		});
	}
	
		
	/**
	 * Register the device with the API to receive Push notifications
	 * @param regId
	 * The registration Id received from the GCM provider
	 */
	public void sendGCMRegistratinId(final String regId) {
		try {
			new Thread( new Runnable() { 
				@Override
				public void run() {
					boolean success = true ;
					ServerResult result = serverHandler.setGcmId(apiAccessToken, regId);
					if(result.connectionFailed()){
						success = false ;
					}else{
						success = (Boolean) result.getValue("success") ;
					}
					GCMHandler.storeIsGcmIdSetOnServer(KhedniApp.getAppContext(), success);
				}
			}).start();
		}catch (Exception e) {}
	}
	
	
	private void updateSessionList(final boolean fullUpdate){
		ArrayList<AppSession> apiSessions = null ;
		long servertimeStamp = 0 ;
		ServerResult result = serverHandler.getSessionsByDate(apiAccessToken, fullUpdate?0:lastSessionsUpdateServerTime);
		HashMap<String, Object> pairs = result.getPairs();
		if (!result.connectionFailed()){
			apiSessions = (ArrayList<AppSession>) result.getPairs().get("sessions");
			servertimeStamp = (Long) result.getValue("timeStamp");
		}
		if (sessions == null)
			sessions = new ArrayList<AppSession>();
		
		if (apiSessions != null){
			if(fullUpdate){
				sessions = apiSessions;
				cacheProvider.storeSessions(sessions);
			}else{
				sessions = mergeSessions(sessions, apiSessions);
				for (AppSession appSession : apiSessions) {
					requestSessionEvents(appSession.getGlobalId(), null); // TODO Limit simltanus updates to 4 and cache the rest for later
				}
				cacheProvider.storeSessions(sessions);
				broadcastNewChatMessagesAvailable();
			}
			//check for new messages
			if(apiSessions != null && !apiSessions.isEmpty()){
				for (AppSession appSession : apiSessions) {
					if(appSession.getLastUpdate() > lastSessionsUpdateServerTime){
						broadcastNewChatMessagesAvailable();
						break;
					}
				}
			}
			
			// update data here
			lastSessionsUpdateServerTime = (lastSessionsUpdateServerTime < servertimeStamp)?servertimeStamp : lastSessionsUpdateServerTime ;
			lastSessionsUpdateLocalTime = System.currentTimeMillis() ;
			// caching back
			//updateConversations();
			cacheProvider.storeLasteSessoinsUpdateServerlTimestamp(lastSessionsUpdateServerTime);
			cacheProvider.storeLasteSessoinsUpdateLocalTimestamp(lastSessionsUpdateLocalTime);
			
			generateConversationsFromSessions(sessions);
		}
	}
	
	private ArrayList<AppSession> mergeSessions (ArrayList<AppSession> originalSessions, ArrayList<AppSession> newSessions){
		if(newSessions == null || newSessions.isEmpty() ){
			return originalSessions ;
		}
		try{
			ArrayList<AppSession> tempArray = new ArrayList<AppSession>(newSessions) ;
			for (AppSession newSession : tempArray) {
				for (AppSession originalSession : originalSessions) {
					if(newSession.getGlobalId().equals(originalSession.getGlobalId())){
						tempArray.remove(newSession);
						originalSessions.add(newSession);
						originalSessions.remove(originalSession);
					}
				}
			}
			originalSessions.addAll(tempArray);
		}catch(Exception e){
			e.printStackTrace();
		}
		return originalSessions ;
	}
	private boolean checkForNewMessages(ArrayList<AppSession> currentSessions, ArrayList<AppSession> newSessions ){
		boolean newMessagesAvailable = false;
		for (AppSession appSession : newSessions) {
			
		}
		return newMessagesAvailable;
	}
	private ArrayList<AppContact> updateEnrolledFriends(){
		ArrayList<AppContact> enrolledFriends = null ;
		try{
//			if(contacts==null)
//				contacts = ContactsMgr.getInstance().getLocalContacts(KhedniApp.getAppContext());
			
			ServerResult result = serverHandler.getEnrolledFriends(apiAccessToken, new ArrayList<AppContact>());
			if(!result.connectionFailed()){
				enrolledFriends = (ArrayList<AppContact>) result.getValue("enrolledUsers") ; 
			}
		}catch(Exception e){e.printStackTrace();}
		
		if(enrolledFriends != null ){
			cacheProvider.storeEnrolledFriends(enrolledFriends);
			return enrolledFriends ;
		}else
			return this.enrolledFriensds ;
	}
	
	private ArrayList<AppArea> updateAreas(){
		ArrayList<AppArea> azkar = null ;
		try{
			ServerResult result = serverHandler.getAreas(apiAccessToken);
			if(!result.connectionFailed()){
				azkar = (ArrayList<AppArea>) result.getValue("contents") ; 
			}
			
		}catch(Exception e){e.printStackTrace();}
		
		if(azkar != null ){
			cacheProvider.storeAzkar(azkar);
			return azkar ;
		}else
			return this.arrayAreas ;
	}
	
	private void generateConversationsFromSessions(ArrayList<AppSession> sessions){
		try{
			if(sessions == null)
				return;
			if(conversations == null)
				conversations = new ArrayList<AppConversation>();
			else
				conversations.clear();
			for (AppSession appSession : sessions) {
				conversations.add(new AppConversation(appSession));
			}
			Collections.sort(conversations);
		}catch(Exception e){}
	}
	
	public ArrayList<AppConversation> getConversations() {
		return conversations;
	}
	
	public ArrayList<AppContact> getEnrolledFriensds() {
		return enrolledFriensds;
	}
	
	public ArrayList<AppArea> getAppAreas() {
		return arrayAreas;
	}
	
	public AppTripsSet getAllTripsSet() {
		return allTripsSet;
	}
	
	public AppTripsSet getSearchTripSet() {
		return searchTripSet;
	}
	
	public ArrayList<String> getAreasNames(){
		ArrayList<String> names = new ArrayList<String>();
		for (AppArea appArea : arrayAreas) {
			names.add(appArea.getName());
		}		
		return names;
	}
	
	public AppArea getAreaByName(String name){
		if(name == null || arrayAreas == null )
			return null;
		for (AppArea appArea : arrayAreas) {
			if(appArea.getName().equals(name))
				return appArea;
		}
		return null;
	}
	
	public AppArea getAreaById(String name){
		if(name == null || arrayAreas == null )
			return null;
		for (AppArea appArea : arrayAreas) {
			if(appArea.getId().equals(name))
				return appArea;
		}
		return null;
	}
	
	// tutorial 
	public boolean isTutPlantDisplayed() {
		return isTutPlantDisplayed;
	}
	public boolean isTutSelfDisplayed() {
		return isTutSelfDisplayed;
	}
	public boolean isTutSendDisplayed() {
		return isTutSendDisplayed;
	}
	public boolean isTutOpenTaskDisplayed() {
		return isTutOpenTaskDisplayed;
	}
	public void setTutPlantDisplayed(boolean isTutPlantDisplayed) {
		this.isTutPlantDisplayed = isTutPlantDisplayed;
		cacheProvider.storeTutPlantFlag(isTutPlantDisplayed);
	}
	public void setTutSelfDisplayed(boolean isTutSelfDisplayed) {
		this.isTutSelfDisplayed = isTutSelfDisplayed;
		cacheProvider.storeTutSelfFlag(isTutSelfDisplayed);
	}
	public void setTutSendDisplayed(boolean isTutSendDisplayed) {
		this.isTutSendDisplayed = isTutSendDisplayed;
		cacheProvider.storeTutSendZickerFlag(isTutSendDisplayed);
	}
	public void setTutOpenTaskDisplayed(boolean isTutopenTaskDisplayed) {
		this.isTutOpenTaskDisplayed = isTutopenTaskDisplayed;
		cacheProvider.storeTutOpenTaskFlag(isTutOpenTaskDisplayed);
	}
	
	public ArrayList<AppMessage> getSessionEvents(String sessionId) {
		if(mapSessionMessages == null)
			mapSessionMessages = new HashMap<String, ArrayList<AppMessage>>() ;
		if(sessionId!= null && mapSessionMessages.containsKey(sessionId))
			return mapSessionMessages.get(sessionId);
		return null;
	}
	
	public void attemptUpdateUserProfile(final String firstName,final String lastName,
			final String job,final String age, final String photo,
			final DataRequestCallback callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean success = true;
				ServerResult result = serverHandler.updateUserProfile(apiAccessToken, firstName, lastName, job, age, photo);
				if (result.connectionFailed())
					success = false;
				else {
				}
				if (callback != null)
					invokeCallback(callback, success, result);
			}
		}).start();
	}
	
	/// utils 
	
	public AppSession getSessionById(String sessionId){
		if(sessions == null || sessionId==null || sessionId.isEmpty())
			return null;
		for (AppSession appSession : sessions) {
			if(appSession.getGlobalId().equals(sessionId))
				return appSession;
		}
		return null;
	}
	
	public AppSession getSessionByPeerId(String peerId){
		if(sessions == null || peerId==null || peerId.isEmpty())
			return null;
		for (AppSession appSession : sessions) {
			if(appSession.getPeer().getId().equals(peerId))
				return appSession;
		}
		return null;
	}
	public boolean isRnrolledFriend (String phoneNumber){
		if(enrolledFriensds == null )
			return false ;
		for(AppContact con : enrolledFriensds){
			if(con.getPhoneNum().equals(phoneNumber) )
				return true;
		}
		return false ;
	}
	
	public interface DataRequestCallback {
		public void onDataReady(ServerResult data, boolean success);
	}
	
	public interface DataStoreUpdatListener{
		public void onDataStoreUpdate();
		public void onNewNotificationsAvailable();
		public void onNewMessagesAvailable();
	}
}
