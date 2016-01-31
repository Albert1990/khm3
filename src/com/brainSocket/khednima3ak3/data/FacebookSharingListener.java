package com.brainSocket.khednima3ak3.data;

public interface FacebookSharingListener
{
	void onShareResult(boolean success);
	
	void onShareError(String error);
	
	void onShareCancelled();
}
