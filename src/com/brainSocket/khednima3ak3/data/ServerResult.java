package com.brainSocket.khednima3ak3.data;

import java.util.HashMap;

public class ServerResult {
	
	private static String[] errors = {
		ServerAccess.ERROR_CODE_userExistsBefore,
		ServerAccess.ERROR_CODE_userNotExists ,
		ServerAccess.ERROR_CODE_unknown ,
		ServerAccess.ERROR_CODE_userExistsBefore ,
		ServerAccess.ERROR_CODE_tokenNotExists ,
		ServerAccess.ERROR_CODE_accessTokenExpired ,
		ServerAccess.ERROR_CODE_noEnrolledFriends ,
		ServerAccess.ERROR_CODE_invalidAccessToken ,
		ServerAccess.ERROR_CODE_contactsArrayParsingError ,
		ServerAccess.ERROR_CODE_verificationMessageNotExists ,
		ServerAccess.ERROR_CODE_cantFindUserTaskProcess,
		ServerAccess.ERROR_CODE_sessionNotExists,
		ServerAccess.ERROR_CODE_requsterUserIsNotInThisSession,
		ServerAccess.ERROR_CODE_destUserIsInThisSessionBefore,
		ServerAccess.ERROR_CODE_destMobileNumberNotExists,
		ServerAccess.ERROR_CODE_receivedEventIdNotExists,
		ServerAccess.ERROR_CODE_InInputParams,
		ServerAccess.ERROR_CODE_cantFindTask,
		ServerAccess.ERROR_CODE_noContactsRecieved,
		ServerAccess.ERROR_CODE_userNotVerified,
		ServerAccess.ERROR_CODE_appVersionInvalid,
		ServerAccess.ERROR_CODE_updateAvailable
		//ServerAccess.ERROR_CODE_allTasksAreDone,
	};	
	

	private String flag;
	private HashMap<String, Object> pairs;

	public ServerResult(String flag) {
		super();
		this.flag = flag;
	}

	public ServerResult() {
		pairs = new HashMap<String, Object>();
	}

	public HashMap<String, Object> getPairs() {
		return pairs;
	}

	public void setPairs(HashMap<String, Object> pairs) {
		this.pairs = pairs;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public void addPair(String key, Object value) {
		this.pairs.put(key, value);
	}
	
	public Object getValue(String key) {
		return this.pairs.get(key);
	}

	public boolean isValid() { 
		for(String error : errors) {
			if(flag.equals(error))
				return false;
		}
		return true;
	}
	public boolean connectionFailed (){
		return  (flag.equals(ServerAccess.RESPONCE_FORMAT_ERROR_CODE) || flag.equals(ServerAccess.CONNECTION_ERROR_CODE) ); 
	}
}
