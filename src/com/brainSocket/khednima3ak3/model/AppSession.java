package com.brainSocket.khednima3ak3.model;

import java.util.Date;

import org.json.JSONObject;

import com.brainSocket.khednima3ak3.KhedniApp;

import android.content.Context;
import android.widget.ImageView;


/**
 * a session represent an conversation that actually exists on the server and have peers in it 
 * @author MolhamStein
 *
 */
public class AppSession {		
	
	
	String idGlobal = null;
	long lastUpdate ; 
	AppUser peers ;

	public AppSession(JSONObject json){
		if(json == null) {
			return;
		}
		try {
			if(json.has("id")) 
				idGlobal = json.getString("id");
		}catch (Exception e){} 
		try {
			if(json.has("last_update")) 
				lastUpdate = json.getLong("last_update");
		} 
		catch (Exception e) {}
		try {
			if(json.has("user")){
				JSONObject jsonArray = json.getJSONObject("user");
				peers = new AppUser(jsonArray);
			}
		} catch (Exception e){}
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public String getLastUpdateDisplayDate() {
		String strDate = "";
		try{
			strDate = KhedniApp.getDateTime(lastUpdate);
		}catch(Exception e){}
		return strDate;
	}
	
	public AppUser getPeer() {
		return peers;
	}
	
	/**
	 * Returns the {@link JSONObject} containing the user 
	 * details, just like the structure received from the API
	 * @return
	 */
	public JSONObject getJsonObject() {
		JSONObject json = new JSONObject();
		try {json.put("id", idGlobal);} catch (Exception e) {}
		try {json.put("last_update", lastUpdate);} catch (Exception e) {}
		try {json.put("user", peers.getJsonObject());} catch (Exception e) {}
		return json;
	}
	
	/**
	 * Returns a string formatted {@link JSONObject} of the user object
	 * 
	 * @return
	 */
	public String getJsonString() {
		JSONObject json = getJsonObject();
		return json.toString();
	}
	
	public String getPeerName (){
		if(peers!= null){
			return peers.getFirstName();
		}
		return "" ;
	}
	
	public String getGlobalId() {
		return idGlobal;
	}
	
	public void displayPhoto (Context con, ImageView iv){
		peers.displayImage(con, iv);
	}
	        
}
