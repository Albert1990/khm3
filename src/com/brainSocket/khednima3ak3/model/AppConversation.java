package com.brainSocket.khednima3ak3.model;

import java.util.ArrayList;

import android.content.Context;
import android.widget.ImageView;

import com.brainSocket.khednima3ak3.data.DataStore;

public class AppConversation implements Comparable<AppConversation>{		
	
	AppUser contacts ;
	
	AppSession session ;
	boolean hasSession = false ;
	
	// non Server properties

	public AppConversation (AppSession session){
		if(session == null )
			return ;
		
		hasSession = true ;
		contacts = session.getPeer() ;
		this.session = session ; 
	}
	
	public AppConversation (AppUser contact ){
		if(contact == null )
			return ;
		hasSession = false ;
		contacts = contact;
	}
		
	public String getLatestMsg (){
		if(hasSession){
			ArrayList<AppMessage> events = DataStore.getInstance().getSessionEvents(session.getGlobalId());
			if(events != null && !events.isEmpty() ){
				return events.get(events.size()-1).getContentValue();
			}
			return "..." ;
		}else{
			return "..." ;
		}
	}
	
	public String getLatestUpdateString (){
		if(hasSession){
			//String date = KhedniApp.getDaysLapsedString(session.getLastUpdate()) ;
			return session.getLastUpdateDisplayDate();
		}else{
			return "--:--" ;
		}
	}
	
	public String getName (){
		if(hasSession){
			return session.getPeerName();
		}
		return "";
	}
	
	public AppUser getContact() {
		return contacts;
	}
	
	public void displayPhoto (Context con, ImageView iv){
		if(hasSession){
			session.displayPhoto(con, iv);
		}
	}
		
	public AppSession getSession() {
		return session;
	}
	
	public boolean isHasSession() {
		return hasSession;
	}
	
	@Override
	public int compareTo(AppConversation another) {
		return getName().compareTo(another.getName());
	}        
}
