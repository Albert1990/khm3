package com.brainSocket.khednima3ak3;

import com.brainSocket.khednima3ak3.data.FacebookProvider;
import com.brainSocket.khednima3ak3.data.TrackingMgr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class AppBaseActivity extends ActionBarActivity {

	//TODO this comment will delete later;
	protected boolean isVisible = false ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		KhedniApp.setCurrentAcivity(null);
		isVisible = false ;
	}
	@Override
	protected void onResume() {
		super.onResume();
		KhedniApp.setCurrentAcivity(this);
		isVisible = true ;
	}
	
	@Override
	protected void onStop() {
		TrackingMgr.getInstance().onAcitivityStop(this);
		super.onStop();
	}
	@Override
	protected void onStart() {
		TrackingMgr.getInstance().onAcitivityStart(this);
		super.onStart();
	}
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		FacebookProvider.getInstance().onActiviyResult(arg0, arg1, arg2);
	}
	
}
