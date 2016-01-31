package com.brainSocket.khednima3ak3;

import com.brainSocket.khednima3ak3.model.AppContact;
import com.brainSocket.khednima3ak3.model.AppConversation;


public interface HomeCallbacks {
	public void showProgress(boolean show, int msg);
	public void showToast (String msg);
	public void setTitle(String title);
	public void showConversation(AppConversation conversation);
}
