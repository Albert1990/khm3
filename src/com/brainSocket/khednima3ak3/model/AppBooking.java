package com.brainSocket.khednima3ak3.model;

import org.json.JSONObject;

public class AppBooking {
	private int id;
	private int userId;
	private AppUser user;
	private int tripId;
	private AppTrip trip;
	private int fromPlaceId;
	private int toPlaceId;
	private int requiredSeatsNumber;
	private int price;
	private boolean hasAnswered;
	private boolean isAccepted;
	private String createdAt;
	
	public AppBooking(JSONObject ob)
	{
		if(ob==null)
			return;
		try
		{
			if(ob.has("id"))
				id=ob.getInt("id");
		}catch(Exception ex){}
		try
		{
			if(ob.has("user_id"))
				userId=ob.getInt("user_id");
		}catch(Exception ex){}
		try
		{
			if(ob.has("user"))
				user=new AppUser(ob.getJSONObject("user"));
		}catch(Exception ex){}
		try
		{
			if(ob.has("trip_id"))
				tripId=ob.getInt("trip_id");
		}catch(Exception ex){}
		try
		{
			if(ob.has("trip"))
				trip=new AppTrip(ob.getJSONObject("trip"));
		}catch(Exception ex){}
		try
		{
			if(ob.has("from_place_id"))
				fromPlaceId=ob.getInt("from_place_id");
		}catch(Exception ex){}
		try
		{
			if(ob.has("to_place_id"))
				toPlaceId=ob.getInt("to_place_id");
		}catch(Exception ex){}
		try
		{
			if(ob.has("required_seats_number"))
				requiredSeatsNumber=ob.getInt("required_seats_number");
		}catch(Exception ex){}
		try
		{
			if(ob.has("price"))
				price=ob.getInt("price");
		}catch(Exception ex){}
		try
		{
			if(ob.has("has_answered"))
				hasAnswered=ob.getInt("has_answered")>0;
		}catch(Exception ex){}
		try
		{
			if(ob.has("is_accepted"))
				isAccepted=ob.getInt("is_accepted")>0;
		}catch(Exception ex){}
		try
		{
			if(ob.has("created_at"))
				createdAt=ob.getString("created_at");
		}catch(Exception ex){}
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public AppUser getUser() {
		return user;
	}

	public int getTripId() {
		return tripId;
	}

	public AppTrip getTrip() {
		return trip;
	}

	public int getFromPlaceId() {
		return fromPlaceId;
	}

	public int getToPlaceId() {
		return toPlaceId;
	}

	public int getRequiredSeatsNumber() {
		return requiredSeatsNumber;
	}

	public int getPrice() {
		return price;
	}

	public boolean isHasAnswered() {
		return hasAnswered;
	}

	public boolean isAccepted() {
		return isAccepted;
	}

	public String getCreatedAt() {
		return createdAt;
	}
	
	public JSONObject getJsonObject()
	{
		JSONObject ob=new JSONObject();
		try{ob.put("id", id);}catch(Exception ex){}
		try{ob.put("user_id", userId);}catch(Exception ex){}
		try{ob.put("user", user.getJsonObject());}catch(Exception ex){}
		try{ob.put("trip_id", tripId);}catch(Exception ex){}
		try{ob.put("trip", trip.getJsonObject());}catch(Exception ex){}
		try{ob.put("from_place_id", fromPlaceId);}catch(Exception ex){}
		try{ob.put("to_place_id", toPlaceId);}catch(Exception ex){}
		try{ob.put("required_seats_number", requiredSeatsNumber);}catch(Exception ex){}
		try{ob.put("price", price);}catch(Exception ex){}
		try{ob.put("has_answered", hasAnswered ? 1:0);}catch(Exception ex){}
		try{ob.put("is_accepted", isAccepted ? 1:0);}catch(Exception ex){}
		try{ob.put("created_at", createdAt);}catch(Exception ex){}
		return ob;
	}
}
