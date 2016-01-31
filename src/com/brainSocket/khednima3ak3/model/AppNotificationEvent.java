package com.brainSocket.khednima3ak3.model;

import org.json.JSONObject;

import com.brainSocket.khednima3ak3.data.DataStore;


public class AppNotificationEvent  {		

	//public static enum NOTIFICATION_TYPE {REQ_SENT, REQ_REC, RESP_SENT, RESP_REC};
	public static enum NOTIFICATION_TYPE {
		TRIP_CREATED(1), RATE(2),
		BOOKING_REQUEST(4), BOOKING_RESPONSE(6),
		PASSENGER_REMOVED_FROM_TRIP(7),MATCHER_FOUND(8),NONE(9);
		
		private final int value;
		private NOTIFICATION_TYPE(int value)
		{
			this.value=value;
		}
		
		public static NOTIFICATION_TYPE parse(int value)
		{
			NOTIFICATION_TYPE resultType=NOTIFICATION_TYPE.NONE;
			switch(value)
			{
			case 1:
				resultType=NOTIFICATION_TYPE.TRIP_CREATED;
				break;
			case 2:
				resultType=NOTIFICATION_TYPE.RATE;
				break;
			case 4:
				resultType=NOTIFICATION_TYPE.BOOKING_REQUEST;
				break;
			case 6:
				resultType=NOTIFICATION_TYPE.BOOKING_RESPONSE;
				break;
			case 7:
				resultType=NOTIFICATION_TYPE.PASSENGER_REMOVED_FROM_TRIP;
				break;
			case 8:
				resultType=NOTIFICATION_TYPE.MATCHER_FOUND;
				break;
			}
			return resultType;
		}
		
		};
	
	public final static int EVENT_TYPE_CODE_BOOKING_REQUEST = 4;
	public final static int EVENT_TYPE_CODE_BOOKING_RESPONCE = 6;
	
	String id = null;
	//AppUser peer;
	AppUser fromUser;
	AppUser toUser;
	boolean has_fetched;
	AppBooking booking;
	String object_id;
	
	// generated data
	boolean isValidEvent=false;
	NOTIFICATION_TYPE type;
	
	public AppNotificationEvent(JSONObject json){
		if(json == null) {
			return;
		}
		try {
			if(json.has("id")) 
				id = json.getString("id");
		}catch (Exception e){} 
		try {
			if(json.has("event_type_id"))
			{
				int eventTypeId = json.getInt("event_type_id");
				type=NOTIFICATION_TYPE.parse(eventTypeId);
				if(type==NOTIFICATION_TYPE.BOOKING_REQUEST)
					isValidEvent=true;
			}
		}catch (Exception e) {
			isValidEvent=false;
		}
		
		try
		{
			if(json.has("from_user"))
				fromUser=new AppUser(json.getJSONObject("from_user"));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try
		{
			if(json.has("to_user"))
				toUser=new AppUser(json.getJSONObject("to_user"));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try{
			if(json.has("has_fetched")) 
				has_fetched = json.getInt("has_fetched")>0;
		}catch (Exception e) {}
		try
		{
			if(json.has("object_id"))
				object_id=json.getString("object_id");
		}
		catch(Exception ex){}
		try{
			if(json.has("object"))
				if(json.has("table_name"))
				{
					String table_name=json.getString("table_name");
					if(table_name.equals("bookings"))
						booking = new AppBooking(json.getJSONObject("object"));
				}
		}catch (Exception e) {}
		
	}
	
	
	
	//----------- NOTE Enabling cashing for this class could cause alot of problems 
	//------------as the api Response format is different from the Model itself plus the model is using the user id to parse itself----------- 
	
	
	/**
	 * Returns the {@link JSONObject} containing the user 
	 * details, just like the structure received from the API
	 * @return
	 */
//	public JSONObject getJsonObject() {
//		JSONObject json = new JSONObject();
//		try {json.put("id", id);} catch (Exception e) {}
//		try {json.put("event_type_id", event_type_id);} catch (Exception e) {}
//		try {json.put("from_user", peer.getJsonObject());} catch (Exception e) {}
//		try {json.put("object", trip.getJsonObject());} catch (Exception e) {}
//		try {json.put("has_fetched", has_fetched?1:0);} catch (Exception e) {}
//		try {json.put("is_accepted", is_accepted?1:0);} catch (Exception e) {}
//		try {json.put("object_id", object_id);} catch (Exception e) {}
//		
//		return json;
//	}
	
	/**
	 * Returns a string formatted {@link JSONObject} of the user object
	 * @return
	 */
//	public String getJsonString(){
//		JSONObject json = getJsonObject();
//		return json.toString();
//	}

	public String getId() {
		return id;
	}

	public AppUser getFromUser()
	{
		return fromUser;
	}
	
	public AppUser getToUser()
	{
		return toUser;
	}
	
	public boolean isHasFetched(){
		return has_fetched;
	}
	
	public boolean isValidEvent(){
		return isValidEvent;
	}
	public AppBooking getBooking() {
		return booking;
	}
	
	public String getObject_id() {
		return object_id;
	}
	
	public NOTIFICATION_TYPE getType() {
		return type;
	}
}
