package com.brainSocket.khednima3ak3.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;


public class AppTripsSet  {		

	int pagesCountInSet ;
	String fromAreaSet = null;
	String toAreaSet = null ;
	ArrayList<AppTrip> trips ;

	public AppTripsSet(JSONObject json){
		if(json == null) {
			return;
		}
		try {
			if(json.has("pageCount")) 
				pagesCountInSet = json.getInt("pageCount");
		}catch (Exception e){} 
		try {
			if(json.has("fromAreaSet")) 
				fromAreaSet = json.getString("fromAreaSet");
		}
		catch (Exception e) {}
		try {
			if(json.has("toAreaSet")) 
				toAreaSet = json.getString("toAreaSet");
		}catch (Exception e) {}
		try {
			if(json.has("trips")){
					JSONArray jsnArray = json.getJSONArray("trips");
					trips = new ArrayList<AppTrip>();
					AppTrip trip ;
				for (int i = 0; i < jsnArray.length(); i++) {
					trip = new AppTrip(jsnArray.getJSONObject(i));
					trips.add(trip);
				}
			}

		}catch (Exception e) {}
	}
	
	public AppTripsSet (ArrayList<AppTrip> trips, String fromId, String toId, int page){
		this.trips = trips ;
		this.fromAreaSet = fromId;
		this.toAreaSet = toId ;
	}
	
	/**
	 * Returns the {@link JSONObject} containing the user 
	 * details, just like the structure received from the API
	 * @return
	 */
	public JSONObject getJsonObject() {
		JSONObject json = new JSONObject();
		try {json.put("pageCount", pagesCountInSet);} catch (Exception e) {}
		try {json.put("fromAreaSet", fromAreaSet);} catch (Exception e) {}
		try {json.put("toAreaSet", toAreaSet);} catch (Exception e) {}
		try {
			JSONArray jsnArray = new JSONArray();
			for (int i = 0; i < trips.size(); i++) {
				jsnArray.put(trips.get(i).getJsonObject());
			}
			json.put("trips", jsnArray);
		} catch (Exception e) {}
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



	public int getPagesCountInSet() {
		return pagesCountInSet;
	}



	public String getFromAreaSet() {
		return fromAreaSet;
	}



	public String getToAreaSet() {
		return toAreaSet;
	}



	public ArrayList<AppTrip> getTrips() {
		return trips;
	}


}
