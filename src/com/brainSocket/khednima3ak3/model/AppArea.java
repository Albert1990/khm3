package com.brainSocket.khednima3ak3.model;

import org.json.JSONObject;


public class AppArea  {		

	
	
	String id = null;
	String cityId = null ;
	String name = null ;

	public AppArea(JSONObject json){
		if(json == null) {
			return;
		}
		try {
			if(json.has("id")) 
				id = json.getString("id");
		}catch (Exception e){} 
		try {
			if(json.has("city_id")) 
				cityId = json.getString("city_id");
		}
		catch (Exception e) {}
		try {
			if(json.has("name")) 
				name = json.getString("name");
		} 
		catch (Exception e) {}
	}
	
	
	
	/**
	 * Returns the {@link JSONObject} containing the user 
	 * details, just like the structure received from the API
	 * @return
	 */
	public JSONObject getJsonObject() {
		JSONObject json = new JSONObject();
		try {json.put("id", id);} catch (Exception e) {}
		try {json.put("city_id", cityId);} catch (Exception e) {}
		try {json.put("name", name);} catch (Exception e) {}
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

	public String getId() {
		return id;
	}

	public String getCityId() {
		return cityId;
	}

	public String getName() {
		return name;
	}
	
	public void getOriginArea(){
		
	}
}
