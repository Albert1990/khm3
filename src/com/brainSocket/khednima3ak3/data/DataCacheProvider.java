
package com.brainSocket.khednima3ak3.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.brainSocket.khednima3ak3.KhedniApp;
import com.brainSocket.khednima3ak3.data.DataStore.APP_VERSION_STATUS;
import com.brainSocket.khednima3ak3.data.DataStore.App_ACCESS_MODE;
import com.brainSocket.khednima3ak3.model.AppArea;
import com.brainSocket.khednima3ak3.model.AppContact;
import com.brainSocket.khednima3ak3.model.AppMessage;
import com.brainSocket.khednima3ak3.model.AppSession;
import com.brainSocket.khednima3ak3.model.AppTripsSet;
import com.brainSocket.khednima3ak3.model.AppUser;
import com.brainSocket.khednima3ak3.model.AppUserStats;

public class DataCacheProvider {
	
	///KEYs
	public static final String PREF_DATA = "rosaryData";
	public static final String PREF_FIRST_TIME = "isFirstTime" ; 
	public static final String PREF_APP_USER  = "userMe" ;
	public static final String PREF_ENROLLED_FRIENDS = "enroledFriends" ;
	public static final String PREF_SETTINGS_NOTIFICATIONS = "settings_notifications" ;
	public static final String PREF_API_ACCESS_TOKEN = "accessToken" ;
	public static final String PREF_PHOTO_CACHE_CLEARED = "image_cache_clear" ;
	public static final String PREF_MAP_SESSIOINS_EVENTS = "sessionsEvents";
	public static final String PREF_SESSIONS = "sessions";
	public static final String PREF_AZKAR = "azkar";
	public static final String PREF_All_TRIPS_SET = "allTripSet";
	public static final String PREF_SEARCH_TRIPS_SET = "searchTripSet";
	public static final String PREF_USER_STATS = "userStats";
	public static final String PREF_USER_SCORE = "score";
	public static final String PREF_APP_ACCESS_MODE = "access_mode";
	public static final String PREF_APP_VERSION_STATUS = "version_status";
	
	// tutorial flags
	public static final String PREF_TUT_PLANT = "tut_plant";
	public static final String PREF_TUT_SELF = "tut_self";
	public static final String PREF_TUT_SEND = "tut_send";
	public static final String PREF_TUT_OPEN_TASK = "tut_open_task";
	
	// dates
	private static final String PREF_LAST_SESSIONS_UPDATE_SERVER_TIME = "lastSessionsUpdateServer" ; 
	private static final String PREF_LAST_SESSIONS_UPDATE_LOCAL_TIME = "lastSessionsUpdateLocal";
	private static final String PREF_LAST_LONG_TERM_DATA_UPDATE_TIME = "lastLongTermUpdateLocal";
	
	private static DataCacheProvider cacheProvider = null;
	// shared preferences
	SharedPreferences prefData;
	SharedPreferences.Editor prefDataEditor;

	
	public static DataCacheProvider getInstance()
	{
		if(cacheProvider == null) {
			cacheProvider = new DataCacheProvider();
		}
		return cacheProvider;
	}
	
	private DataCacheProvider() {
		try {
			// initialize
			prefData = KhedniApp.getAppContext().getSharedPreferences(PREF_DATA, Context.MODE_PRIVATE);
			prefDataEditor = prefData.edit();
		} 
		catch (Exception e) {}
	}

	
	public void clearCache() {
		try {
			prefDataEditor.clear();
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	
	//
	
	
	/**
	 * Stores the timestamp of the last photo cache clear
	 */
	public void storePhotoClearedCacheTimestamp(long timestamp)
	{
		try {
			prefDataEditor.putLong(PREF_PHOTO_CACHE_CLEARED, timestamp);
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	
	/**
	 * Returns the stored timestamp of the last photo cache clear
	 */
	public long getStoredPhotoClearedCacheTimestamp()
	{
		long timestamp = 0;
		try {
			timestamp = prefData.getLong(PREF_PHOTO_CACHE_CLEARED, 0);
		}
		catch (Exception e) {}
		return timestamp;
	}
	
	public void storeLasteSessoinsUpdateLocalTimestamp(long timestamp) {
		try {
			prefDataEditor.putLong(PREF_LAST_SESSIONS_UPDATE_LOCAL_TIME, timestamp);
			prefDataEditor.commit();
		}catch (Exception e) {}
	}
	public long getStoredLasteSessoinsUpdateLocalTimestamp() {
		long timestamp = 0;
		try {
			timestamp = prefData.getLong(PREF_LAST_SESSIONS_UPDATE_LOCAL_TIME, 0);
		}catch (Exception e) {}
		return timestamp;
	}
	
	public void storeLasteSessoinsUpdateServerlTimestamp(long timestamp) {
		try {
			prefDataEditor.putLong(PREF_LAST_SESSIONS_UPDATE_SERVER_TIME, timestamp);
			prefDataEditor.commit();
		}catch (Exception e) {}
	}
	public long getStoredLasteSessoinsUpdateServerTimestamp() {
		long timestamp = 0;
		try {
			timestamp = prefData.getLong(PREF_LAST_SESSIONS_UPDATE_SERVER_TIME, 0);
		}catch (Exception e) {}
		return timestamp;
	}
	
	public void storeLastLongTermUpdateTimestamp(long timestamp) {
		try {
			prefDataEditor.putLong(PREF_LAST_LONG_TERM_DATA_UPDATE_TIME, timestamp);
			prefDataEditor.commit();
		}catch (Exception e) {}
	}
	public long getStoredLastlongTermUpdateTimestamp() {
		long timestamp = 0;
		try {
			timestamp = prefData.getLong(PREF_LAST_LONG_TERM_DATA_UPDATE_TIME, 0);
		}catch (Exception e) {}
		return timestamp;
	}
	
	public boolean isFirstTime() {
		boolean res = false;
		try {
			res = prefData.getBoolean(PREF_FIRST_TIME, true);
			if(res) {
				prefDataEditor.putBoolean(PREF_FIRST_TIME, false);
				prefDataEditor.commit();
			}
		}
		catch (Exception e) {}
		return res;
	}
	
	
	public void storeSessionsEvents(HashMap<String, ArrayList<AppMessage>> map) {
		try {
				JSONObject obj = sessionsEventsMapToJSON(map);
				String str = obj.toString() ;
				prefDataEditor.putString(PREF_MAP_SESSIOINS_EVENTS, str);
				prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	
	public HashMap<String, ArrayList<AppMessage>> getStoredSessionsEvents() {
		HashMap<String, ArrayList<AppMessage>> map = null;
		try {
			String str = prefData.getString(PREF_MAP_SESSIOINS_EVENTS, null);
			if(str != null) {
				JSONObject json = new JSONObject(str);
				map = jsonToSessionsEventsMap(json);
			}
		}
		catch (Exception e) {}
		return map;
	}
	
	
		
	public static HashMap<String, ArrayList<AppMessage>> jsonToSessionsEventsMap(JSONObject object) {
	    HashMap<String, ArrayList<AppMessage>> map = new HashMap<String, ArrayList<AppMessage>>();
	    try {
		    Iterator<String> keysItr = object.keys();
		    while(keysItr.hasNext()) {
		        String key = keysItr.next();
		        ArrayList<AppMessage> events = new ArrayList<AppMessage>();
		        JSONArray jsonArray = object.getJSONArray(key);
		        for (int i = 0; i < jsonArray.length(); i++) {
					AppMessage event = new AppMessage(jsonArray.getJSONObject(i));
					events.add(event);
				}
		        map.put(key, events);
		    }
	    } catch (Exception e) {}
	    return map;
	}

	public static JSONObject sessionsEventsMapToJSON(HashMap<String, ArrayList<AppMessage>> map) {
	    JSONObject jsonObj = new JSONObject() ;
	    try {
		    for(Map.Entry<String, ArrayList<AppMessage>> entry : map.entrySet() ){
		        ArrayList<AppMessage> events = (ArrayList<AppMessage>) map.get(entry.getKey());
		        JSONArray jsonArray = new JSONArray() ; 
		        for (int i = 0; i < events.size(); i++) {
					AppMessage event = events.get(i);
					jsonArray.put(event.getJsonObject());
				}
		        jsonObj.put(entry.getKey(), jsonArray);
		    }
	    } catch (Exception e) {}
	    return jsonObj;
	}
	
	
	
	
	// USER //
	
	/**
	 * Stores the {@link AppUser} object in Shared Preferences
	 * @param appUser
	 */
	public void storeMe(AppUser me) {
		try {
			if(me != null) {
				String str = me.getJsonString();
				prefDataEditor.putString(PREF_APP_USER, str);
				prefDataEditor.commit();
			}else
				removeStoredMe();
		}
		catch (Exception e) {}
	}
	
	/**
	 * Returns the {@link AppUser} object stored in Shared Preferences,
	 * null otherwise
	 */
	public AppUser getStoredMe() {
		AppUser appUser = null;
		try {
			String str = prefData.getString(PREF_APP_USER, null);
			if(str != null) {
				JSONObject json = new JSONObject(str);
				appUser = new AppUser(json);
			}
		}
		catch (Exception e) {}
		return appUser;
	}
	
	/**
	 * Removes the stored {@link AppUser} from Shared Preferences
	 */
	public void removeStoredMe()
	{
		try {
			prefDataEditor.remove(PREF_APP_USER);
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	
	// USER //
	//

	/**
	 * Stores the Array of enrolled Friends 
	 */
	public void storeEnrolledFriends(ArrayList<AppContact> arrayFriends) {
		try {
			if(arrayFriends != null) {
				JSONArray array = new JSONArray();
				for(AppContact user: arrayFriends) {
					JSONObject json = user.getJsonObject();
					array.put(json);
				}
				String str = array.toString();
				prefDataEditor.putString(PREF_ENROLLED_FRIENDS, str);
				prefDataEditor.commit();
			}
		}
		catch (Exception e) {}
	}
	
	/**
	 * Returns the arrayFriends stored in Shared Preferences
	 */
	public ArrayList<AppContact> getStoredEnrolledFriends() {
		ArrayList<AppContact> arrayFriends = null;
		try {
			String str = prefData.getString(PREF_ENROLLED_FRIENDS, null);
			if(str != null) {
				JSONArray array = new JSONArray(str);
				arrayFriends = new ArrayList<AppContact>();
				for(int i = 0; i < array.length(); i++) {
					try {
						JSONObject object = array.getJSONObject(i);
						AppContact user = new AppContact(object);
						arrayFriends.add(user);
					} 
					catch (Exception e) {}
				}
			}
		}
		catch (Exception e) {}		
		return arrayFriends;
	}
	

	
	
	public void storeAzkar(ArrayList<AppArea> arrayAzkar) {
		try {
			if(arrayAzkar != null) {
				JSONArray array = new JSONArray();
				for(AppArea zirk: arrayAzkar) {
					JSONObject json = zirk.getJsonObject();
					array.put(json);
				}
				String str = array.toString();
				prefDataEditor.putString(PREF_AZKAR, str);
				prefDataEditor.commit();
			}
		}
		catch (Exception e) {}
	}
	
	/**
	 * Returns the arrayFriends stored in Shared Preferences
	 */
	public ArrayList<AppArea> getStoredAzkar() {
		ArrayList<AppArea> arrayAzkar = null;
		try {
			String str = prefData.getString(PREF_AZKAR, null);
			if(str != null) {
				JSONArray array = new JSONArray(str);
				arrayAzkar = new ArrayList<AppArea>();
				for(int i = 0; i < array.length(); i++) {
					try {
						JSONObject object = array.getJSONObject(i);
						AppArea zekr = new AppArea(object);
						arrayAzkar.add(zekr);
					} 
					catch (Exception e) {}
				}
			}
		}
		catch (Exception e) {}		
		return arrayAzkar;
	}
	
	
	
	
	public void storeSessions(ArrayList<AppSession> arraySessions) {
		try {
			if(arraySessions != null) {
				JSONArray array = new JSONArray();
				for(AppSession session: arraySessions) {
					JSONObject json = session.getJsonObject();
					array.put(json);
				}
				String str = array.toString();
				prefDataEditor.putString(PREF_SESSIONS, str);
				prefDataEditor.commit();
			}
		}
		catch (Exception e) {}
	}
	
	/**
	 * Returns the arrayFriends stored in Shared Preferences
	 */
	public ArrayList<AppSession> getStoredSessions() {
		ArrayList<AppSession> arraySessions = null;
		try {
			String str = prefData.getString(PREF_SESSIONS, null);
			if(str != null) {
				JSONArray array = new JSONArray(str);
				arraySessions = new ArrayList<AppSession>();
				for(int i = 0; i < array.length(); i++) {
					try {
						JSONObject object = array.getJSONObject(i);
						AppSession user = new AppSession(object);
						arraySessions.add(user);
					} 
					catch (Exception e) {}
				}
			}
		}
		catch (Exception e) {}		
		return arraySessions;
	}
	
	
	
	/**
	 * Stores the accessToken received on login from the API 
	 * in shared preferences
	 * @param accessToken
	 */
	public void storeAccessToken(String accessToken)
	{
		try {
			if(accessToken != null) {
				prefDataEditor.putString(PREF_API_ACCESS_TOKEN, accessToken);
				prefDataEditor.commit();
			}
		}
		catch (Exception e) {}
	}
	
	/**
	 * Returns the cached API accessToken from Shared Preferences,
	 *  null if it wasn't stored before
	 */
	public String getStoredAccessToken()
	{
		String apiAccessToken = null;
		try {
			apiAccessToken = prefData.getString(PREF_API_ACCESS_TOKEN, null);
		}
		catch(Exception e) {}
		return apiAccessToken;
	}
	
	/**
	 * Removes the API accessToken stored in Shared Preferences
	 */
	public void removeAccessToken()
	{
		try {
			prefDataEditor.remove(PREF_API_ACCESS_TOKEN);
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}	
	
	
	
	/// settings 
	
	public boolean getNotificationsSettings() {
		boolean settings = true;
		try {
			settings = prefData.getBoolean(PREF_SETTINGS_NOTIFICATIONS, true);
		}
		catch (Exception e) {}
		return settings;
	}
	
	public void storeSearchTripsSet(AppTripsSet accessToken){
		try {
			if(accessToken != null) {
				prefDataEditor.putString(PREF_SEARCH_TRIPS_SET, accessToken.getJsonString());
				prefDataEditor.commit();
			}
		}
		catch (Exception e) {}
	}
	
	public AppTripsSet getStoredSearchTripsSet(){
		AppTripsSet obj = null;
		try {
			String str = prefData.getString(PREF_SEARCH_TRIPS_SET, null);
			obj = new AppTripsSet(new JSONObject(str));
		}
		catch(Exception e) {}
		return obj;
	}
	
	public void storeAllTripsSet(AppTripsSet accessToken){
		try {
			if(accessToken != null) {
				prefDataEditor.putString(PREF_All_TRIPS_SET, accessToken.getJsonString());
				prefDataEditor.commit();
			}
		}
		catch (Exception e) {}
	}
	
	public AppTripsSet getStoredAllTripsSet(){
		AppTripsSet obj = null;
		try {
			String str = prefData.getString(PREF_All_TRIPS_SET, null);
			obj = new AppTripsSet(new JSONObject(str));
		}
		catch(Exception e) {}
		return obj;
	}
	
	public void storeUserStats( AppUserStats accessToken ){
		try {
			if(accessToken != null) {
				prefDataEditor.putString(PREF_USER_STATS, accessToken.getJsonString());
				prefDataEditor.commit();
			}
		}
		catch (Exception e) {}
	}
	
	public AppUserStats getStoredUserStats(){
		AppUserStats appUser = null;
		try {
			String str = prefData.getString(PREF_USER_STATS, null);
			if(str != null) {
				JSONObject json = new JSONObject(str);
				appUser = new AppUserStats(json);
			}
		}
		catch (Exception e) {}
		return appUser;
	}
	
	public void storeUserScore(int score){
		try {
			prefDataEditor.putInt(PREF_USER_SCORE, score);
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	
	public int getStoredUserScore(){
		int apiAccessToken = 0;
		try {
			apiAccessToken = prefData.getInt(PREF_USER_SCORE,0);
		}
		catch(Exception e) {}
		return apiAccessToken;
	}
	
	public void storeAppAccessMode(App_ACCESS_MODE accessMode){
		try {
			prefDataEditor.putInt(PREF_APP_ACCESS_MODE, accessMode.ordinal());
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	
	public App_ACCESS_MODE getStoredAppAccessMode(){
		int accessModeInt = 0;
		try {
			accessModeInt = prefData.getInt(PREF_APP_ACCESS_MODE,0);
		}
		catch(Exception e) {}
		return App_ACCESS_MODE.values()[accessModeInt];
	}
	
	
	public void storeAppVersionStatus(APP_VERSION_STATUS status){
		try {
			prefDataEditor.putInt(PREF_APP_VERSION_STATUS, status.ordinal());
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	
	public APP_VERSION_STATUS getStoredAppVersionStatus(){
		int accessModeInt = 0;
		try {
			accessModeInt = prefData.getInt(PREF_APP_VERSION_STATUS,0);
		}
		catch(Exception e) {}
		return APP_VERSION_STATUS.values()[accessModeInt];
	}
	
	//// tut
	public void storeTutPlantFlag(boolean isDisplayed){
		try {
			prefDataEditor.putBoolean(PREF_TUT_PLANT, isDisplayed);
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	public boolean getStoredTutPlantFlag(){
		boolean isDisplayed = false ;
		try {
			isDisplayed = prefData.getBoolean(PREF_TUT_PLANT, false);
		}
		catch(Exception e) {}
		return isDisplayed;
	}
	
	public void storeTutSelfFlag(boolean isDisplayed){
		try {
			prefDataEditor.putBoolean(PREF_TUT_SELF, isDisplayed);
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	public boolean getStoredTutSelfFlag(){
		boolean isDisplayed = false ;
		try {
			isDisplayed = prefData.getBoolean(PREF_TUT_SELF, false);
		}
		catch(Exception e) {}
		return isDisplayed;
	}
	
	public void storeTutSendZickerFlag(boolean isDisplayed){
		try {
			prefDataEditor.putBoolean(PREF_TUT_SEND, isDisplayed);
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	public boolean getStoredTutSendZickerFlag(){
		boolean isDisplayed = false ;
		try {
			isDisplayed = prefData.getBoolean(PREF_TUT_SEND, false);
		}
		catch(Exception e) {}
		return isDisplayed;
	}
	
	public void storeTutOpenTaskFlag(boolean isDisplayed){
		try {
			prefDataEditor.putBoolean(PREF_TUT_OPEN_TASK, isDisplayed);
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	public boolean getStoredTutOpenTaskFlag(){
		boolean isDisplayed = false ;
		try {
			isDisplayed = prefData.getBoolean(PREF_TUT_OPEN_TASK, false);
		}
		catch(Exception e) {}
		return isDisplayed;
	}
	
}

