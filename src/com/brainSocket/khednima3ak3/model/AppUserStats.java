package com.brainSocket.khednima3ak3.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;


public class AppUserStats  {		

	
	AppContact bestPartner ;
	ArrayList<AppDayRecord> historyRecords ;
	int averagePerformance ;
	int todayPerformance ; 
	

	public AppUserStats(JSONObject json)
	{
		if(json == null) {
			return;
		} 
		try {
			if(json.has("monthStatistics")){ 
				JSONArray arrayRecords  = json.getJSONArray("monthStatistics");
				historyRecords = new ArrayList<AppDayRecord>();
				for (int i = 0; i < arrayRecords.length(); i++) {
					AppDayRecord day = new AppDayRecord(arrayRecords.getJSONObject(i));
					historyRecords.add(day);
				}
			}
		}catch (Exception e) {}
		try {
			if(json.has("dayStatistics")) 
				todayPerformance = json.getInt("dayStatistics");
		}catch (Exception e){}
		try {
			if(json.has("weekAverageStatistics")) 
				averagePerformance = json.getInt("weekAverageStatistics");
		}catch (Exception e) {}
		try {
			if(json.has("topPartner") && !json.isNull("topPartner")) 
				bestPartner = new AppContact( json.getJSONObject("topPartner"));
		}catch (Exception e) {}
	}
	
	/**
	 * Returns the {@link JSONObject} containing the user 
	 * details, just like the structure received from the API
	 * @return
	 */
	public JSONObject getJsonObject() {
		JSONObject json = new JSONObject();
		JSONArray records = new JSONArray();
		if(historyRecords != null){
			for (int i = 0; i < historyRecords.size(); i++) {
				records.put(historyRecords.get(i).getJsonObject());
			}
		}
		try {json.put("monthStatistics", records);} catch (Exception e) {}
		try {json.put("dayStatistics", todayPerformance);} catch (Exception e) {}
		try {json.put("weekAverageStatistics", averagePerformance);} catch (Exception e) {}
		try {json.put("topPartner", bestPartner.getJsonObject());} catch (Exception e) {}
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

	public AppContact getBestPartner() {
		return bestPartner;
	}

	public ArrayList<AppDayRecord> getHistoryRecords() {
		return historyRecords;
	}

	public int getAveragePerformance() {
		return averagePerformance;
	}

	public int getTodayPerformance() {
		return todayPerformance;
	}

    
}
