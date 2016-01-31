package com.brainSocket.khednima3ak3.model;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.res.Resources;

import com.brainSocket.khednima3ak3.R;

public class AppTrip /*implements Parcelable */{		
	
	AppUser user ;
	String id;
	String destId ;
	String originId ;
	String time ;
	int price ;
	String note ;
	boolean isRecurring;
	String date;
	boolean display_mobile_number;
	JSONArray days;
	boolean hasBookedByUserBefore=false;
	boolean isFull;
	
	public AppTrip(JSONObject json){
		if(json == null) {
			return;
		}
		try {
			if(json.has("id")) 
				id = json.getString("id");
		}catch(Exception e){}
		try {
			if(json.has("user")) 
				user = new AppUser(json.getJSONObject("user"));
		}catch(Exception e){}
		try {
			if(json.has("display_mobile_number")) 
				display_mobile_number = json.getInt("display_mobile_number")>0;
		}catch(Exception e){}
		try {
			if(json.has("to_place_id")) 
				destId = json.getString("to_place_id");
		}catch (Exception e) {}
		try {
			if(json.has("from_place_id")) 
				originId = json.getString("from_place_id");
		}catch (Exception e) {}
		try {
			if(json.has("launch_time")) 
				time = json.getString("launch_time");
		}catch (Exception e) {}
		try {
			if(json.has("start_date")) 
				date = json.getString("start_date");
		}catch (Exception e) {}
		try {
			if(json.has("note")) 
				note = json.getString("note");
		}catch (Exception e) {}
		try {
			if(json.has("days")) 
				days = new JSONArray(json.getString("days"));
		}catch (Exception e) {}
		try {
			if(json.has("price_per_passenger")) 
				price = json.getInt("price_per_passenger");
		}catch (Exception e) {}
		try {
			if(json.has("is_recurring")) 
				isRecurring = json.getInt("is_recurring")>0;
		}catch (Exception e) {}
		try
		{
			if(json.has("has_booked_it_before"))
				hasBookedByUserBefore= json.getBoolean("has_booked_it_before");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try
		{
			if(json.has("is_full"))
				isFull=json.getInt("is_full") > 0;
		}catch(Exception ex){}
	}
	
	/**
	 * Returns the {@link JSONObject} containing the user 
	 * details, just like the structure received from the API
	 * @return
	 */
	public JSONObject getJsonObject(){
		JSONObject json = new JSONObject();
		try {json.put("id", id);} catch (Exception e) {}
		try {json.put("to_place_id", destId);} catch (Exception e) {}
		try {json.put("from_place_id", originId);} catch (Exception e) {}
		try {json.put("user", user.getJsonObject());} catch (Exception e) {}
		try {json.put("launch_time", time);} catch (Exception e) {}
		try {json.put("price_per_passenger", price);} catch (Exception e) {}
		try {json.put("is_recurring", isRecurring?1:0);} catch (Exception e) {}
		try {json.put("display_mobile_number", display_mobile_number?1:0);} catch (Exception e) {}
		try {json.put("days", days);} catch (Exception e) {}
		try {json.put("start_date", date);} catch (Exception e) {}
		try {json.put("has_booked_it_before", hasBookedByUserBefore);}catch(Exception e){}
		try {json.put("is_full", isFull ? "1":"0");}catch(Exception ex){}
		return json;
	}
	
	/**
	 * Returns a string formatted {@link JSONObject} of the user object
	 * @return
	 */
	public String getJsonString(){
		JSONObject json = getJsonObject();
		return json.toString();
	}
	public AppUser getUser() {
		return user;
	}
	public String getDestId() {
		return destId;
	}
	public String getOriginId() {
		return originId;
	}
	public String getTime() {
		return time;
	}
	public String getDate() {
		return date;
	}
	public int getPrice() {
		return price;
	}
	public String getNote() {
		return note;
	}
	public boolean isReccuring(){
		return isRecurring;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean isMobileNumberEnabled(){
		return display_mobile_number;
	}
	
	public boolean HasBookedByUserBefore()
	{
		return hasBookedByUserBefore;
	}
	
	public String getweekDaysString( Resources res){
		String daysString = "";
		try{
			if(days == null)
				return daysString;

			if(days.getInt(6) >0 )
				daysString += "," + res.getString(R.string.weekday_sat);
			if(days.getInt(5) >0 )
				daysString += "," + res.getString(R.string.weekday_sun);
			if(days.getInt(4) >0 )
				daysString += "," + res.getString(R.string.weekday_mon);
			if(days.getInt(3) >0 )
				daysString += "," + res.getString(R.string.weekday_tue);
			if(days.getInt(2) >0 )
				daysString += "," + res.getString(R.string.weekday_wed);
			if(days.getInt(1) >0 )
				daysString += "," + res.getString(R.string.weekday_thu);
			if(days.getInt(0) >0 )
				daysString += "," + res.getString(R.string.weekday_fri);
			
			if(daysString.startsWith(","))
				daysString= daysString.subSequence(1, daysString.length()).toString();
			
		}catch(Exception e){}
		return daysString;
	}
	
	public boolean isFull()
	{
		return isFull;
	}
}
