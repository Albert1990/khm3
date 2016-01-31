package com.brainSocket.khednima3ak3.model;

import java.util.Date;

import org.json.JSONObject;

import com.brainSocket.khednima3ak3.KhedniApp;
import com.brainSocket.khednima3ak3.data.DataStore;

public class AppMessage implements Comparable<AppMessage>{		
		
     
	String idGlobal = null;
	String sessionId = null ;
	boolean hasSeen ;
	String date ;
	String fromUserId ;
	String toUserId ;
	boolean has_fetched;

	String contentValue ;
	
	boolean isEventGeneratedByMe ;
	
	public AppMessage(JSONObject json)
	{
		if(json == null) {
			return;
		}
		try {
			if(json.has("id")) 
				idGlobal = json.getString("id");
		}catch (Exception e){} 
		try {
			if(json.has("chat_session_id")) 
				sessionId = json.getString("chat_session_id");
		}catch (Exception e){}
		try {
			if(json.has("time"))
				date = json.getString("time");
		}catch (Exception e){}
		try {
			if(json.has("has_seen")){
				hasSeen = json.getInt("has_seen")>0 ;
			}
		}catch (Exception e){}
		try {
			if(json.has("has_fetched")){
				has_fetched = json.getInt("has_fetched")>0 ;
			}
		}catch (Exception e){}
		try {
			if(json.has("message")) 
				contentValue = json.getString("message");
		} catch (Exception e) {}
				
		/// Peer
		try {
			toUserId = json.getString("to_user_id") ;
			fromUserId = json.getString("from_user_id") ;
			if(toUserId.equals(DataStore.meId)){
				isEventGeneratedByMe = false ;
			}else{
				isEventGeneratedByMe = true ;
			}
		}catch (Exception e){}
	}
	
	public JSONObject getJsonObject() {
		JSONObject json = new JSONObject();
		try {json.put("id", idGlobal);} catch (Exception e) {}
		try {json.put("chat_session_id", sessionId);} catch (Exception e) {}
		try {json.put("time", date);} catch (Exception e) {}
		try {json.put("toUserId", toUserId);} catch (Exception e) {}
		try {json.put("from_user_id", fromUserId);} catch (Exception e) {}
		try {json.put("has_fetched", has_fetched?1:0);} catch (Exception e) {}
		try {json.put("has_seen", hasSeen?1:0);} catch (Exception e) {}
		try {json.put("message", contentValue);} catch (Exception e) {}
		
		return json;
	}
	
	/**
	 * Returns a string formatted {@link JSONObject} of the user object
	 * @return
	 */
	public String getJsonString()
	{
		JSONObject json = getJsonObject();
		return json.toString();
	}

	public String getContentValue() {
		return contentValue;
	}

	public String getSessionId() {
		return sessionId;
	}
	
	public boolean isEventGeneratedByMe(){
		return isEventGeneratedByMe;
	}
	public String getDate() {
		return date;
	}
	
	public String getDisplayDate(){
		return KhedniApp.getTimeFromAPIString(date);
	}
	public boolean hasFetchedBefore(){
		return has_fetched;
	}
	
	@Override
	public int compareTo(AppMessage another) {
		return date.compareTo(another.getDate());
	}
}
