package com.brainSocket.khednima3ak3.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.provider.Settings.Secure;

import com.brainSocket.khednima3ak3.KhedniApp;
import com.brainSocket.khednima3ak3.data.AndroidMultiPartEntity.ProgressListener;
import com.brainSocket.khednima3ak3.data.DataStore.APP_VERSION_STATUS;
import com.brainSocket.khednima3ak3.model.AppArea;
import com.brainSocket.khednima3ak3.model.AppContact;
import com.brainSocket.khednima3ak3.model.AppEvent;
import com.brainSocket.khednima3ak3.model.AppMessage;
import com.brainSocket.khednima3ak3.model.AppNotificationEvent;
import com.brainSocket.khednima3ak3.model.AppSession;
import com.brainSocket.khednima3ak3.model.AppTrip;
import com.brainSocket.khednima3ak3.model.AppTripsSet;
import com.brainSocket.khednima3ak3.model.AppUser;
import com.brainSocket.khednima3ak3.model.AppUserStats;

public class ServerAccess {
	// GENERIC ERROR CODES
	

	public static final String ERROR_CODE_BOOKKED_BEFORE ="-11";
	public static final String ERROR_CODE_userExistsBefore ="-16";
	public static final String ERROR_CODE_userNotExists ="-101";
	public static final String ERROR_CODE_unknown ="-100";
	public static final String ERROR_CODE_tokenNotExists = "-103";
	public static final String ERROR_CODE_accessTokenExpired = "-104";
	public static final String ERROR_CODE_noEnrolledFriends = "-105";
	public static final String ERROR_CODE_invalidAccessToken = "-106";
	public static final String ERROR_CODE_contactsArrayParsingError = "-107";
	public static final String ERROR_CODE_verificationMessageNotExists = "-109";
	public static final String ERROR_CODE_cantFindUserTaskProcess="-110";
	public static final String ERROR_CODE_sessionNotExists="-111";
	public static final String ERROR_CODE_requsterUserIsNotInThisSession="-112";
	public static final String ERROR_CODE_destUserIsInThisSessionBefore="-113";
	public static final String ERROR_CODE_destMobileNumberNotExists="-114";
	public static final String ERROR_CODE_receivedEventIdNotExists="-115";
	public static final String ERROR_CODE_InInputParams="-116";
	public static final String ERROR_CODE_cantFindTask="-117";
	public static final String ERROR_CODE_noContactsRecieved="-118";
	public static final String ERROR_CODE_allTasksAreDone="-119";
	public static final String ERROR_CODE_userNotVerified="-120";
	public static final String ERROR_CODE_appVersionInvalid= "-121";
	public static final String ERROR_CODE_updateAvailable= "-122";
		
	public static final String RESPONCE_FORMAT_ERROR_CODE = "-19" ;
	public static final String CONNECTION_ERROR_CODE = "-20" ;

	// api
	//static final int MAIN_PORT_NUM = 3000 ;
	static final String BASE_SERVICE_URL = "http://khednym3ak.com/private1";
	static final String baseServiceURL_FOR_UDP = "198.38.91.194" ;

	private static final int CHECK_UPDATES_PORT = 2522;

	// api keys
		private static final String FLAG = "flag";
		private static final String KEY = "key";

	
	private static ServerAccess serverAccess = null;
	//private ConnectivityManager cm ;

	private ServerAccess() {
		Context c = KhedniApp.getAppContext();
		//cm =(ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public static ServerAccess getInstance() {
		if (serverAccess == null) {
			serverAccess = new ServerAccess();
		}
		return serverAccess;
	}
	
	//----------------------------------
	// Server Utils 
	//-----------------------
	
	/*public boolean isOnline() {
		try{
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		        return true;
		    }
		}catch(Exception e){}
	    return false;
	}*/

	
	
	// API calls // ------------------------------------------------
	
	
	
	public ServerResult login(String phoneNum) {
		ServerResult result = new ServerResult();
		AppUser me  = null ;
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("mobile_number", phoneNum));
			
			try{
				String deviceId = Secure.getString(KhedniApp.getAppContext().getContentResolver(),Secure.ANDROID_ID);
				jsonPairs.add(new BasicNameValuePair("imei", deviceId));
			}catch(Exception e){
				e.printStackTrace();
			}
			
			// url
			String url = BASE_SERVICE_URL + "/index.php/users_api/login";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) { // check if response is empty
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("object")&&!jsonResponse.isNull("object")){
					me = new AppUser(jsonResponse.getJSONObject("object").getJSONObject("user"));
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("appUser", me);

		return result;
	}
	
	/**
	 * register a new user with UserName and phoneNumber  
	 * @param name
	 * @param phoneNum
	 * @return
	 */
	public ServerResult registerUser(String fName,String lName, String phoneNum, boolean isMale, String versionId, String FBID) {
		ServerResult result = new ServerResult();
		AppUser me  = null ;
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("first_name", fName));
			jsonPairs.add(new BasicNameValuePair("last_name", lName));
			jsonPairs.add(new BasicNameValuePair("mobile_number", phoneNum));
			jsonPairs.add(new BasicNameValuePair("gender", isMale?"1":"0"));
			jsonPairs.add(new BasicNameValuePair("version", versionId));
			jsonPairs.add(new BasicNameValuePair("facebook_id", "-1"));
			
			try{
				String deviceId = Secure.getString(KhedniApp.getAppContext().getContentResolver(),Secure.ANDROID_ID);
				jsonPairs.add(new BasicNameValuePair("imei", deviceId));
			}catch(Exception e){
				e.printStackTrace();
			}
			
			// url
			String url = BASE_SERVICE_URL + "/index.php/users_api/register";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) { // check if response is empty
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("object")){
					if(!jsonResponse.isNull("object"))
						me = new AppUser(jsonResponse.getJSONObject("object"));
				}

			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("appUser", me);

		return result;
	}
	
	public ServerResult setGcmId(String accessToken, String gcmId) {
		ServerResult result = new ServerResult();
		boolean success = false;
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token", accessToken));
			jsonPairs.add(new BasicNameValuePair("gcm_id", gcmId));
			// url
			String url = BASE_SERVICE_URL + "/index.php/users_api/set_gcm_id";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) { // check if response is empty
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("user") && !jsonResponse.isNull("user")){
					success = true ;
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("success", success);
		return result;
	}
	
	
	public ServerResult getMe(String accessToken) {
		ServerResult result = new ServerResult();
		AppUser me  = null ;
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("accessToken", accessToken));
			// url
			String url = BASE_SERVICE_URL + "/users/getMe";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) { // check if response is empty
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("user")){
					me = new AppUser(jsonResponse.getJSONObject("user"));
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("appUser", me);

		return result;
	}
	
	public ServerResult requestVerificationMsg(String accessToken) {
		ServerResult result = new ServerResult();
		boolean msgSent = false ;
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token", accessToken));
			// url
			String url = BASE_SERVICE_URL + "/index.php/verification_messages_api/send_verification_code";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) { // check if response is empty
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(result.getFlag().equals("0")){
					msgSent = true ;
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("msgSent", msgSent);

		return result;
	}
	
	public ServerResult verifyAccount(String accessToken, String verifMsg) {
		ServerResult result = new ServerResult();
		boolean verified = false ;
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token", accessToken));
			jsonPairs.add(new BasicNameValuePair("verification_code", verifMsg));
			// url
			String url = BASE_SERVICE_URL + "/index.php/verification_messages_api/accept_verification_code";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) { // check if response is empty
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(result.getFlag().equals("0")){
					verified = true ;
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("verified", verified);
		return result;
	}
	
	public ServerResult getSessionsByDate(String accessToken, long date) {
		ServerResult result = new ServerResult();
		long serverTime = 0 ;
		ArrayList<AppSession> sessions = null ;
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token", accessToken));
			jsonPairs.add(new BasicNameValuePair("date", String.valueOf(date)));
			// url
			String url = BASE_SERVICE_URL + "/index.php/sessions_api/get_sessions";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) { // check if response is empty
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("object")){
					sessions = new ArrayList<AppSession>();
					if(!jsonResponse.isNull("object")){
						JSONArray jsonArray = jsonResponse.getJSONArray("object");
						for (int i = 0; i < jsonArray.length(); i++) {
							AppSession se = new AppSession(jsonArray.getJSONObject(i));
							sessions.add(se) ;
						}
					}
					JSONObject attach = jsonResponse.getJSONObject("attach");
					serverTime = attach.getLong("date");
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("sessions", sessions);
		result.addPair("timeStamp", serverTime);

		return result;
	}
	
	public ServerResult getSessionEvents(String accessToken, String sessionId) {
		ServerResult result = new ServerResult();
		ArrayList<AppMessage> sessionEvents = null ;
		try {
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token", accessToken));
			jsonPairs.add(new BasicNameValuePair("chat_session_id", sessionId));
			
			String url = BASE_SERVICE_URL + "/index.php/sessions_api/get_session_messages";
			String response = sendPostRequest(url, jsonPairs);
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("object")){
					sessionEvents = new ArrayList<AppMessage>() ;
					if(!jsonResponse.isNull("object")){
						JSONArray jsonArray = jsonResponse.getJSONObject("object").getJSONArray("messages");
						for (int i = 0; i < jsonArray.length(); i++) {
							AppMessage event = new AppMessage(jsonArray.getJSONObject(i));
							sessionEvents.add(event) ;
						}
					}
				}
			} else {result.setFlag(CONNECTION_ERROR_CODE);}
		} catch (Exception e) {result.setFlag(RESPONCE_FORMAT_ERROR_CODE);}
		result.addPair("events", sessionEvents);
		return result;
	}
	
	
	public ServerResult getAreas(String accessToken) {
		ServerResult result = new ServerResult();
		ArrayList<AppArea> azkar = null ;
		try {
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token", accessToken));			
			String url = BASE_SERVICE_URL + "/index.php/places_api/get_all";
			String response = sendPostRequest(url, jsonPairs);
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("object")){
					azkar = new ArrayList<AppArea>() ;
					if(!jsonResponse.isNull("object")){
						JSONArray jsonArray = jsonResponse.getJSONArray("object");
						for (int i = 0; i < jsonArray.length(); i++) {
							AppArea event = new AppArea(jsonArray.getJSONObject(i));
							azkar.add(event) ;
						}
					}
				}
			} else {result.setFlag(CONNECTION_ERROR_CODE);}
		} catch (Exception e) {result.setFlag(RESPONCE_FORMAT_ERROR_CODE);}
		result.addPair("contents", azkar);
		return result;
	}

	public ServerResult getEnrolledFriends(String accessToken, ArrayList<AppContact> contatcs) {
		ServerResult result = new ServerResult();
		ArrayList<AppContact> enrolledFriends = null ;
		try {
			JSONArray jsonContacts = new JSONArray() ;
			if(contatcs != null){
				for (AppContact appContact : contatcs) {
					if(appContact.getPhoneNum() != null && !appContact.getPhoneNum().isEmpty()){
						JSONObject jsn = new JSONObject();
						jsn.put("mobileNumber", appContact.getPhoneNum());
						jsonContacts.put(jsn);
					}
				}
			}
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("accessToken", accessToken));
			jsonPairs.add(new BasicNameValuePair("contactsArray", jsonContacts.toString()));			
			String url = BASE_SERVICE_URL + "/users/getEnrolledFriends";
			String response = sendPostRequest(url, jsonPairs);
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("enrolledUsers")){
					enrolledFriends = new ArrayList<AppContact>() ;
					if(!jsonResponse.isNull("enrolledUsers")){
						JSONArray jsonArray = jsonResponse.getJSONArray("enrolledUsers");
						for (int i = 0; i < jsonArray.length(); i++) {
							AppContact enrolledFriend = new AppContact(jsonArray.getJSONObject(i));
							enrolledFriends.add(enrolledFriend) ;
						}
					}
				}else if(result.getFlag().equals(ERROR_CODE_noEnrolledFriends)){
					enrolledFriends = new ArrayList<AppContact>() ;
				}
			} else {result.setFlag(CONNECTION_ERROR_CODE);}
			
		} catch (Exception e) {result.setFlag(RESPONCE_FORMAT_ERROR_CODE);}
		result.addPair("enrolledUsers", enrolledFriends);
		return result;
	}
	
	/**
	 * import facebook friends
	 * @param userId
	 * @param sessionAccessToken
	 * @return 
	 */
	public ServerResult importFacebookFriends(String accessToken, String sessionAccessToken) {
		ServerResult result = new ServerResult();
		HashMap<String, String> facebookFirends = new HashMap<String, String>();
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("accessToken", accessToken));
			jsonPairs.add(new BasicNameValuePair("facebookAccessToken", sessionAccessToken));
			// url
			String url = BASE_SERVICE_URL + "/users/importFacebookFriends";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				if(!flag.equals("-1")) {
					JSONArray fbFriendsJson = jsonResponse.getJSONArray("newFriends");
					for(int i=0;i<fbFriendsJson.length();++i){
						JSONObject fbFriend = fbFriendsJson.getJSONObject(i);
						facebookFirends.put(fbFriend.getString("id"), fbFriend.getString("userName"));
					}
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("users", facebookFirends);
		return result;
	}

	public ServerResult addSelfZeker(String accessToken,int counter,String contentId){	
		ServerResult result = new ServerResult();
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("accessToken", accessToken));
			jsonPairs.add(new BasicNameValuePair("counter", String.valueOf(counter)));
			jsonPairs.add(new BasicNameValuePair("contentId", String.valueOf(contentId)));
			// url
			String url = BASE_SERVICE_URL + "/selfZeker/addSelfZeker";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);								
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}	
		return result;
	}
	
	public ServerResult searchForTrips(String accessToken, String fromArea, String toArea, int page){
		ServerResult result = new ServerResult();
		AppTripsSet tripSet = null ;
		//System.out.println("ServerAccess.sendZekrToUsers()");
		try {
			// parameters
			if(fromArea == null)
				fromArea = "-1";
			if(toArea == null)
				toArea = "-1";
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token",accessToken));
			jsonPairs.add(new BasicNameValuePair("from_place_id",fromArea));
			jsonPairs.add(new BasicNameValuePair("to_place_id",toArea));
			jsonPairs.add(new BasicNameValuePair("from_page",String.valueOf(page)));
			// url
			String url = BASE_SERVICE_URL + "/index.php/trips_api/get_trips";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				if(flag.equals("0")) {
					JSONArray valueObject = jsonResponse.getJSONArray("object");
					ArrayList<AppTrip> trips = new ArrayList<AppTrip>();
					AppTrip trip ;
					for (int i = 0; i < valueObject.length(); i++) {
						trip = new AppTrip(valueObject.getJSONObject(i));
						trips.add(trip);
					}
					tripSet = new AppTripsSet(trips, fromArea, toArea, page);
				}else if(flag.equals("-13")){ // no trips ---> returning empty list
					tripSet = new AppTripsSet(new ArrayList<AppTrip>(), fromArea, toArea, page);
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}	
		result.addPair("tripSet", tripSet);
		return result;
	}
	
	public ServerResult sendChatMsg(String accessToken,String peerId,String msg){
		ServerResult result = new ServerResult();
		AppMessage event =  null ;
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token",accessToken));
			jsonPairs.add(new BasicNameValuePair("to_user_id",peerId));
			jsonPairs.add(new BasicNameValuePair("message",msg));
			
			// url
			String url = BASE_SERVICE_URL + "/index.php/messages_api/send_message";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				if(flag.equals("0")) {
					JSONObject valueObject = jsonResponse.getJSONObject("object");
					event = new AppMessage(valueObject);
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}	
		result.addPair("event", event);
		return result;
	}
	
	
	public ServerResult ResponseToZekr(String accessToken, String eventId, int hasAgree){
		ServerResult result = new ServerResult();
		boolean success = false ;
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
//			jsonPairs.add(new BasicNameValuePair("accessToken", accessToken));
//			jsonPairs.put("receivedEventId", eventId);
//			jsonPairs.put("hasAgree", hasAgree);
			// url
			String url = BASE_SERVICE_URL + "/events/respondToZeker";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				if(result.getFlag().equals("0")){
					success = true ;
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("success", success);
		return result;
	}
	
	public ServerResult isVersionValid(String versionNumber){
		ServerResult result = new ServerResult();
		APP_VERSION_STATUS status = APP_VERSION_STATUS.VALID ;
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("version_id", versionNumber));						
			// url
			String url = BASE_SERVICE_URL + "/index.php/users_api/check_version";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				if(result.isValid()){
					int stat = jsonResponse.getJSONObject("object").getInt("state");
					status = APP_VERSION_STATUS.VALID ;
					if(stat >= 3)
						status = APP_VERSION_STATUS.VERSION_INVALID ;
					else if	(stat >= 2)
						status = APP_VERSION_STATUS.UPDATE_AVAILABLE ;
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("status", status);
		return result;
	}

	public ServerResult createTrip( String AccessToken, int pricePerPassenger, String launchTime, String origId, String destId, long startDate, int[] weekdays, boolean isRecurring, boolean displayMobileNumber){
		ServerResult result = new ServerResult();
		AppTrip stats = null ;
		try {
			// parameters
			String days = "[";
			for (int i = 0; i < weekdays.length; i++) {
				days += weekdays[i] + ",";
			}
			days += "]";
			
			String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(startDate)); 
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token", AccessToken));
			jsonPairs.add(new BasicNameValuePair("is_recurring", isRecurring?"1":"0"));
			jsonPairs.add(new BasicNameValuePair("start_date", date));
			jsonPairs.add(new BasicNameValuePair("days", days));
			jsonPairs.add(new BasicNameValuePair("to_place_id", destId));
			jsonPairs.add(new BasicNameValuePair("from_place_id", origId));
			jsonPairs.add(new BasicNameValuePair("launch_time", launchTime));
			jsonPairs.add(new BasicNameValuePair("price_per_passenger", String.valueOf(pricePerPassenger)));
			jsonPairs.add(new BasicNameValuePair("display_mobile_number", displayMobileNumber?"1":"0"));
			// url
			String url = BASE_SERVICE_URL + "/index.php/trips_api/insert_car_owner_trip";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				if(result.isValid()){
					if(jsonResponse.has("object"))
					stats = new AppTrip(jsonResponse.getJSONObject("object"));
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("stats", stats);
		return result;
	}
	
	public ServerResult deleteTrip( String AccessToken, String tripId){
		ServerResult result = new ServerResult();
		try { 
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token", AccessToken));
			jsonPairs.add(new BasicNameValuePair("trip_id", tripId));
			// url
			String url = BASE_SERVICE_URL + "/index.php/trips_api/delete";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				if(result.isValid()){
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	
	public ServerResult markTripAsFull( String AccessToken, String tripId,boolean isFull){
		ServerResult result = new ServerResult();
		try { 
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token", AccessToken));
			jsonPairs.add(new BasicNameValuePair("trip_id", tripId));
			jsonPairs.add(new BasicNameValuePair("is_full", isFull ? "1":"0"));
			// url
			String url = BASE_SERVICE_URL + "/index.php/trips_api/mark_as_full";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				if(result.isValid()){
					if(jsonResponse.has("object"))
					{
						if(!jsonResponse.isNull("object"))
							result.addPair("isFull", jsonResponse.getInt("object")>0);
					}
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	
	public ServerResult getUserStats(String AccessToken){
		ServerResult result = new ServerResult();
		AppUserStats stats = null ;
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("accessToken", AccessToken));						
			// url
			String url = BASE_SERVICE_URL + "/users/getStatistics";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				if(result.isValid()){
					if(jsonResponse.has("stats"))
					stats = new AppUserStats(jsonResponse.getJSONObject("stats"));
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("stats", stats);
		return result;
	}
	
	public ServerResult editUserProfile(String userId, String userName, String gender, String userFile){
		ServerResult result = new ServerResult();
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("userId", userId));
									
			// url
			String url = BASE_SERVICE_URL + "/users/editUserProfile";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
	
		return result;
	}
	
	public ServerResult updateZekrConter(String accessToken, String taskId, int counter){
		ServerResult result = new ServerResult();
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("accessToken", accessToken));
								
			String url = BASE_SERVICE_URL + "/tasks/updateTaskCounter";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	public ServerResult updatePersonalZekrConter(String userId, String counterValue){
		ServerResult result = new ServerResult();
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("userId", userId));			
			jsonPairs.add(new BasicNameValuePair("counterValue", counterValue));
									
			// url
			String url = BASE_SERVICE_URL + "/users/updatePersonalZekerCounter";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	public ServerResult getPersonalZekrConter(String userId){
		ServerResult result = new ServerResult();
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("userId", userId));						
									
			// url
			String url = BASE_SERVICE_URL + "/users/getPersonalZekerCounter";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	public ServerResult getMyTrips(String userId){
		ServerResult result = new ServerResult();
		ArrayList<AppTrip> events  = null;
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token", userId));						
									
			// url
			String url = BASE_SERVICE_URL + "/index.php/trips_api/my_trips";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("object")){
					events = new ArrayList<AppTrip>() ;
					if(!jsonResponse.isNull("object")){
						JSONArray jsonArray = jsonResponse.getJSONArray("object");
						for (int i = 0; i < jsonArray.length(); i++) {
							AppTrip event = new AppTrip(jsonArray.getJSONObject(i));
							events.add(event) ;
						}
					}
				}
			} else {result.setFlag(CONNECTION_ERROR_CODE);}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("events",events);
		return result;
	}
	
	public ServerResult getEventNotifications(String token){
		ServerResult result = new ServerResult();
		ArrayList<AppNotificationEvent> events  = null;
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token", token));						
									
			// url
			String url = BASE_SERVICE_URL + "/index.php/events_api/get_events";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("object")){
					events = new ArrayList<AppNotificationEvent>() ;
					if(!jsonResponse.isNull("object")){
						JSONArray jsonArray = jsonResponse.getJSONArray("object");
						for (int i = 0; i < jsonArray.length(); i++) {
							AppNotificationEvent event = new AppNotificationEvent(jsonArray.getJSONObject(i));
							events.add(event) ;
						}
					}
				}
			} else {result.setFlag(CONNECTION_ERROR_CODE);}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("events",events);
		return result;
	}
	
	public ServerResult respondToRideRequest(String token, String bookingId, boolean isAccepted ){
		ServerResult result = new ServerResult();
		ArrayList<AppNotificationEvent> events  = null;
		try {
			// parameters
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token", token));
			jsonPairs.add(new BasicNameValuePair("booking_id", bookingId));
			jsonPairs.add(new BasicNameValuePair("has_agree", isAccepted?"1":"0"));
									
			// url
			String url = BASE_SERVICE_URL + "/index.php/bookings_api/ride_respond";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("object")){
					result.addPair("hasAccepted", isAccepted);
				}
			} else {result.setFlag(CONNECTION_ERROR_CODE);}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("events",events);
		return result;
	}
	
	public ServerResult sendRideRequest(String token, String fromPlaceId, String toPlaceId, String tripId){
		ServerResult result = new ServerResult();
		ArrayList<AppNotificationEvent> events  = null;
		try {
			// parameters
			if(toPlaceId == null || toPlaceId.isEmpty())
				toPlaceId = "-1";
			if(fromPlaceId == null || fromPlaceId.isEmpty())
				fromPlaceId = "-1";
			List<NameValuePair> jsonPairs = new ArrayList<NameValuePair>();
			jsonPairs.add(new BasicNameValuePair("access_token", token));
			jsonPairs.add(new BasicNameValuePair("from_place_id", fromPlaceId));
			jsonPairs.add(new BasicNameValuePair("to_place_id", toPlaceId));
			jsonPairs.add(new BasicNameValuePair("trip_id", tripId));
									
			// url
			String url = BASE_SERVICE_URL + "/index.php/bookings_api/ride_request";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("object")){
//					events = new ArrayList<AppNotificationEvent>() ;
//					if(!jsonResponse.isNull("object")){
//						JSONArray jsonArray = jsonResponse.getJSONArray("object");
//						for (int i = 0; i < jsonArray.length(); i++) {
//							AppNotificationEvent event = new AppNotificationEvent(jsonArray.getJSONObject(i));
//							events.add(event) ;
//						}
//					}
					if(!jsonResponse.isNull("object"))
					{
						
					}
				}
			} else {result.setFlag(CONNECTION_ERROR_CODE);}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("events",events);
		return result;
	}
	
	/**
	 * Register the device with the API to receive Push notifications
	 * @param regId
	 * The registration Id received from the GCM provider
	 */
	/*public void sendGCMRegistratinId(String apiAccessToken, String regId, String userId)
	{
		try {
			if(regId != null && !regId.isEmpty()) {
				// parameters
				JSONObject jsonPairs = new JSONObject();
				jsonPairs.put("token", regId);
				jsonPairs.put("type", "3");
				
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("token", regId));
				pairs.add(new BasicNameValuePair("type", "3"));
				// url
				String url = BASE_SERVICE_URL + "/users/register_device?access_token=" + apiAccessToken;
				// send request
				String response = sendPostRequest(url, jsonPairs);
			}
		}
		catch (Exception e) {}
	}*/
	
	
	// Request executers //
	static HttpClient client  ;
	private String sendGetRequest(String url) {
		client = new DefaultHttpClient();
		String result = null;

		try {
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));
				StringBuilder str = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					str.append(line + "\n");
				}
				reader.close();
				result = str.toString();
			} catch (Exception ex) {
				result = null;
			}
		} catch (Exception ex) {
			result = null;
		}

		return result;
	}
	
	public ServerResult updateUserProfile(String accessToken,String firstName,String lastName,
			String job,String age,String imgPath) {
		String url = BASE_SERVICE_URL + "ads_api/upload_ad_photos";
		String responseString = null;
		ServerResult res = new ServerResult();
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		try {
//			ProgressListener progressListener=new ProgressListener() {
//				
//				@Override
//				public void transferred(long num) {
//					// TODO Auto-generated method stub
//					
//				}
//			};
			AndroidMultiPartEntity entity=new AndroidMultiPartEntity(new ProgressListener() {
				
				@Override
				public void transferred(long num) {
					// publishProgress((int) ((num / (float) totalSize)
//					// * 100));
				}
			});

			if (imgPath != null) {
				File sourceFile;
					if (imgPath != null) {
						sourceFile = new File(imgPath);
						entity.addPart("user_image", new FileBody(sourceFile));
					}
			}

			// Adding file data to http body
			entity.addPart("access_token", new StringBody(accessToken));
			entity.addPart("first_name", new StringBody(firstName)); // Extra
			entity.	addPart("last_name",new StringBody(lastName));																// parameters
			entity.	addPart("job",new StringBody(job));																	// if
			entity.	addPart("age",new StringBody(age));																	// you
																				// want
																				// to
																				// pass
																				// to
																				// server
			

			// totalSize = entity.getContentLength();
			httppost.setEntity(entity);

			// Making server call
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity r_entity = response.getEntity();

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				try {
					// Server response
					responseString = EntityUtils.toString(r_entity);
					if (responseString != null && !responseString.equals("")) { // check
																				// if
																				// response
																				// is
																				// empty
						JSONObject jsonResponse = new JSONObject(responseString);
						res.setFlag(jsonResponse.getString(FLAG));

					} else {
						res.setFlag(CONNECTION_ERROR_CODE);
					}
				} catch (Exception ex) {
					res.setFlag(CONNECTION_ERROR_CODE);
					ex.printStackTrace();
				}
			} else {
				responseString = "Error occurred! Http Status Code: "
						+ statusCode;
			}

		} catch (ClientProtocolException e) {
			res.setFlag(ServerAccess.ERROR_CODE_unknown);
		} catch (IOException e) {
			res.setFlag(ServerAccess.ERROR_CODE_unknown);
		}
		return res;
	}

	private String sendPostRequest(String url, List<NameValuePair> jsonPairs) {
		client = new DefaultHttpClient();
		String result = null;
		try {
			HttpPost post = new HttpPost(url);
			//StringEntity entity = new StringEntity(jsonPairs.toString(),HTTP.UTF_8); 
			//entity.setContentType("application/json");
			post.setEntity(new UrlEncodedFormEntity(jsonPairs,HTTP.UTF_8));
			HttpResponse response = client.execute(post);

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));
				StringBuilder str = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					str.append(line + "\n");
				}
				reader.close();
				result = str.toString().trim();
			} catch (Exception ex) {
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	private String sendUDPRequest(String url) {
		
		String text = null;
	    byte[] message = new byte[1500];
	    try{
		    InetAddress serveraddr = InetAddress.getByName(baseServiceURL_FOR_UDP);
		    DatagramPacket res = new DatagramPacket(message,message.length);
		    DatagramPacket send = new DatagramPacket(url.getBytes(),url.getBytes().length,serveraddr,CHECK_UPDATES_PORT);		    
		    DatagramSocket s = new DatagramSocket();
		    try {
			    s.setSoTimeout(1500);    
			    s.send(send);
			    s.receive(res);
			    text = new String(message, 0, res.getLength(),"UTF-8");
			    s.close();
			} catch (Exception e) {
				s.close();
			}
	    }catch(Exception e){
	    }
	    return text;
	}
	
	
/*	static {
	    HttpParams params = new BasicHttpParams();
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
	    HttpProtocolParams.setUseExpectContinue(params, false);  
	    HttpConnectionParams.setConnectionTimeout(params, 10000);
	    HttpConnectionParams.setSoTimeout(params, 10000);
	    ConnManagerParams.setMaxTotalConnections(params, 100);
	    ConnManagerParams.setTimeout(params, 30000);

	    SchemeRegistry registry = new SchemeRegistry();
	    registry.register(new Scheme("http",PlainSocketFactory.getSocketFactory(), MAIN_PORT_NUM )); 
	    ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
	    client = new DefaultHttpClient(manager, params);
	    //httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
	}*/
	
	
}
