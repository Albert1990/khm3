package com.brainSocket.khednima3ak3.model;

import org.json.JSONObject;

import android.util.Log;

import com.brainSocket.khednima3ak3.data.DataStore;

public class AppEvent implements Comparable<AppEvent>{		
	
	private static final int DEFAULT_GOAL_VALUE = 2 ;
	
	public enum EVENT_TYPE {ZEKER_REQ, ZEKER_RESP } ;
	public enum CONTENT_TYPE {TEXT, UNKNOWN} ;
	
	public static enum TASK_STATE {ACCEPTED, REJECTED, UN_known};
     
	
	String idGlobal = null;
	String sessionId = null ;
	String taskId = null ;
	TASK_STATE hasAgree ;
	long date ;
	String fromUserId ;
	String toUserId ;
	
	// content
	CONTENT_TYPE contentType;
	String contentValue ;
	String contentId ;
	long contentDate ;
	
	AppContact peer ;
	boolean isEventGeneratedByMe ;

	// TASK
	AppTask task ;
//	boolean isComplete ;
//	int curentCount ;
//	int goal ;
	
	
	
	public AppEvent(JSONObject json)
	{
		if(json == null) {
			return;
		}
		Log.d("event", json.toString());
		try {
			if(json.has("id")) 
				idGlobal = json.getString("id");
		}catch (Exception e){} 
		try {
			if(json.has("sessionId")) 
				sessionId = json.getString("sessionId");
		}catch (Exception e){}
		try {
			if(json.has("taskId")) 
				taskId = json.getString("taskId");
		}catch (Exception e){}
		try {
			if(json.has("date")) 
				date = json.getLong("date");
		}catch (Exception e){}
		try {
			if(json.has("hasAgree")){
				int hasAgreeInt = json.getInt("hasAgree") ;
				switch (hasAgreeInt) {
				case 1:
					hasAgree = TASK_STATE.ACCEPTED ;
					break;
				case 0:
					hasAgree = TASK_STATE.REJECTED ;
					break;
				default:
					hasAgree = TASK_STATE.UN_known ;
					break;
				}
			}
		}catch (Exception e){}
//		try {
//			if(json.has("isCompleted")) 
//				isComplete = json.getInt("isCompleted")==1?true:false;
//		}catch (Exception e){}
//		try {
//			if(json.has("goal")) 
//				goal = json.getInt("goal");
//		}catch (Exception e){goal = DEFAULT_GOAL_VALUE; }
//		try {
//			if(json.has("counter")) 
//				curentCount = json.getInt("counter");
//		}catch (Exception e){}
		
		JSONObject jsonContent = null ;
		try {
			if(json.has("content")) 
				jsonContent = json.getJSONObject("content");
		} catch (Exception e) {}
		
		try {
			if(jsonContent != null  && jsonContent.has("id")) 
				contentId = jsonContent.getString("id");
		} catch (Exception e) {}
		try {
			if(jsonContent != null  && jsonContent.has("value")) 
				contentValue = jsonContent.getString("value");
		} catch (Exception e) {}
		try {
			if(jsonContent != null && jsonContent.has("contentTypeId")) 
				contentType = CONTENT_TYPE.values()[jsonContent.getInt("contentTypeId")];
		} catch (Exception e) {}
		
		
		/// Peer
		try {
			toUserId = json.getString("toUserId") ;
			fromUserId = json.getString("fromUserId") ;
			if(toUserId.equals(DataStore.meId)){
				isEventGeneratedByMe = false ;
				peer = new AppContact(json.getJSONObject("fromUser")) ;
			}else{
				isEventGeneratedByMe = true ;
				peer = new AppContact(json.getJSONObject("toUser")) ;
			}
		}catch (Exception e){}
		
		try {
			if(json.has("task") && !json.isNull("task")) {
				JSONObject jsonTask = json.getJSONObject("task");
				task = new AppTask(jsonTask);
			}
		} catch (Exception e) {}
	}
	
	public JSONObject getJsonObject() {
		JSONObject json = new JSONObject();
		try {json.put("id", idGlobal);} catch (Exception e) {}
		try {json.put("sessionId", sessionId);} catch (Exception e) {}
		try {json.put("taskId", taskId);} catch (Exception e) {}
		try {json.put("date", date);} catch (Exception e) {}
		try {json.put("toUserId", toUserId);} catch (Exception e) {}
		try {json.put("fromUserId", fromUserId);} catch (Exception e) {}
		int hasAgreeInt = -1 ;
		switch (hasAgree) {
		case ACCEPTED:
			hasAgreeInt = 1 ;
			break;
		case REJECTED:
			hasAgreeInt = 0 ;
			break;
		}
		try {json.put("hasAgree", hasAgreeInt);} catch (Exception e) {}
//		try {json.put("counter", curentCount);} catch (Exception e) {}
//		try {json.put("isCompleted", isComplete);} catch (Exception e) {}
//		try {json.put("goal", goal);} catch (Exception e) {}
		try {json.put("task", task.getJsonObject());} catch (Exception e) {}
		 
		JSONObject jsonContent = new JSONObject() ;
		try {jsonContent.put("value", contentValue);} catch (Exception e) {}
		try {jsonContent.put("id", contentId);} catch (Exception e) {}
		try{jsonContent.put("contentTypeId", contentType.ordinal());} catch (Exception e) {}
		
		try {json.put("content", jsonContent);} catch (Exception e) {}

		try {
			if (isEventGeneratedByMe){
				json.put("toUser", peer.getJsonObject());
				json.put("fromUser", DataStore.getInstance().getMe().getJsonObject());
			}else{
				json.put("fromUser", peer.getJsonObject());
				json.put("toUser", DataStore.getInstance().getMe().getJsonObject());
			}
		} catch (Exception e) {
		}
		
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

	public String getIdGlobal() {
		return idGlobal;
	}

	public void setIdGlobal(String idGlobal) {
		this.idGlobal = idGlobal;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public TASK_STATE isHasAgree() {
		return hasAgree ;
	}

	public void setHasAgree(TASK_STATE hasAgree) {
		this.hasAgree = hasAgree;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

	public CONTENT_TYPE getContentType() {
		return contentType;
	}

	public void setContentType(CONTENT_TYPE contentType) {
		this.contentType = contentType;
	}

	public String getContentValue() {
		return contentValue;
	}

	public void setContentValue(String contentValue) {
		this.contentValue = contentValue;
	}

	public long getContentDate() {
		return contentDate;
	}

	public void setContentDate(long contentDate) {
		this.contentDate = contentDate;
	}

	public AppContact getPeer() {
		return peer;
	}

	public void setPeer(AppContact peer) {
		this.peer = peer;
	}

	public boolean isEventGeneratedByMe() {
		return isEventGeneratedByMe;
	}

	public int getGoal() {
		if(task != null)
			return task.getGoal();
		return DEFAULT_GOAL_VALUE;
	}
	
	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	
	public int getCurentCount() {
		if(task != null)
			return task.getCounter();
		return 0 ;
	}
	public boolean isComplete() {
		if(task != null)
			return task.isCompleted();
		return false ;
	}

	@Override
	public int compareTo(AppEvent another) {
		if(date > another.getDate())
			return 1 ;
		else if(date == another.getDate())
			return 0 ;
		else
			return -1 ;
	}
	        
}
