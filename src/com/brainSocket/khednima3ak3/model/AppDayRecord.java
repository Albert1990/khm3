package com.brainSocket.khednima3ak3.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

public class AppDayRecord {

	long date;
	int count;

	public AppDayRecord(JSONObject json) {
		if (json == null) {
			return;
		}

		try {
			if (json.has("counter"))
				count = json.getInt("counter");
		} catch (Exception e) {
		}
		try {
			if (json.has("date"))
				date = json.getLong("date");
		} catch (Exception e) {
		}
	}

	/**
	 * Returns the {@link JSONObject} containing the user details, just like the
	 * structure received from the API
	 * 
	 * @return
	 */
	public JSONObject getJsonObject() {
		JSONObject json = new JSONObject();
		try {
			json.put("counter", count);
		} catch (Exception e) {
		}
		try {
			json.put("date", date);
		} catch (Exception e) {
		}
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

	public long getDate() {
		return date;
	}

	public int getCount() {
		return count;
	}

	public String getShortDateForDisplay() {
		String res = null;
		try {
			Date date = new Date(this.date);
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.ENGLISH);
			res = sdf.format(date);
		}
		catch (Exception e) {}
		return res;
	}

}
