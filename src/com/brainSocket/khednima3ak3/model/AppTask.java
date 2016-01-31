package com.brainSocket.khednima3ak3.model;

import org.json.JSONObject;

public class AppTask {
	String id = null;
	int goal = 0;
	int counter = 0 ;
	boolean isCompleted ;	

	public AppTask(JSONObject json) {
		if(json == null) {
			return;
		}
		try {
			if(json.has("id")) 
				id = json.getString("id");
		}catch (Exception e){} 
		try {
			if(json.has("counter")) 
				counter =  json.getInt("counter");
		} 
		catch (Exception e) {}
		try {
			if(json.has("isCompleted")) 
				isCompleted = json.getInt("isCompleted")==1?true:false;
		}catch (Exception e){}
		try {
			if(json.has("goal")) 
				goal = json.getInt("goal");
		}catch (Exception e){}
	}
	

	public JSONObject getJsonObject() {
		JSONObject task = new JSONObject();
		
		try { task.put("taskId", id);} catch (Exception e) {}
		try { task.put("goal", goal);} catch (Exception e) {}
		try { task.put("counter", counter);} catch (Exception e) {}
		try { task.put("isCompleted", isCompleted?1:0);} catch (Exception e) {}
		return task;
	}
	
	/**
	 * Returns a string formatted {@link JSONObject} of the user object
	 * @return
	 */
	public String getJsonString()
	{
		JSONObject json = getJsonObject();
		String str = json.toString() ;
		return str;
	}


	public String getId() {
		return id;
	}


	public int getCounter() {
		return counter;
	}


	public void setCounter(int counter) {
		this.counter = counter;
	}

	public boolean isCompleted() {
		return isCompleted;
	}


	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}


	public void setId(String id) {
		this.id = id;
	}



	public int getGoal() {
		return goal;
	}


	public void setGoal(int goal) {
		this.goal = goal;
	}


	public int getProcessed() {
		return counter;
	}


	public void setProcessed(int processed) {
		this.counter = processed;
	}
        
}
