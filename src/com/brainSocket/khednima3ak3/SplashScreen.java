package com.brainSocket.khednima3ak3;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Toast;

import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.DiagMsg.DiagMsgListener;
import com.brainSocket.khednima3ak3.data.DataStore;
import com.brainSocket.khednima3ak3.data.DataStore.APP_VERSION_STATUS;
import com.brainSocket.khednima3ak3.data.DataStore.App_ACCESS_MODE;
import com.brainSocket.khednima3ak3.helpers.AnimationHelper;
import com.brainSocket.khednima3ak3.model.AppUser;


public class SplashScreen extends FragmentActivity {

	private final static int TUT_ACTIVITY_REQ_CODE = 3452 ;
	private final static int LOGIN_ACTIVITY_REQ_CODE = 3454 ;
	private final static int SPLASH_DURATION = 4000 ;
	
	
	
	Handler handler;
	View vSpinner ;
	APP_VERSION_STATUS versionStatus ;
	
	
	boolean animatingDirectionUp = false ; 
	AnimationListener animListner = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {}
		@Override
		public void onAnimationRepeat(Animation animation) {}
		@Override
		public void onAnimationEnd(Animation animation) {
			if(animatingDirectionUp){
				AnimationHelper.appliyPredefinedAmin(vSpinner, R.anim.floating_vertical_to_bottom, this);
				animatingDirectionUp = false ;
			}else{
				AnimationHelper.appliyPredefinedAmin(vSpinner, R.anim.floating_vertical_up, this);
				animatingDirectionUp = true ;
			}
		}
	};
	
	DiagMsgListener listenerDiagVersioninvalid  = new DiagMsgListener() {
		@Override
		public void onBtn2Pressed(DialogFragment dialog) {
			versionStatus = APP_VERSION_STATUS.VALID ;
			handler.post(proceedRunnable);
			dialog.dismiss();
		}
		
		@Override
		public void onBtn1Pressed(DialogFragment dialog) {
			triggerAppUpdate();
			//Toast.makeText(SplashScreen.this, "trigger veresion updat", Toast.LENGTH_SHORT).show();
		}
	};
	
	Runnable proceedRunnable = new Runnable() {
		@Override
		public void run() {
			
			AppUser currentUser = DataStore.getInstance().getMe();
			
			App_ACCESS_MODE accessMode = DataStore.getInstance().getAccessMode() ; 
			if(versionStatus != null && versionStatus != APP_VERSION_STATUS.VALID){
				onVersionInvalid();
			}else if (( currentUser == null )) {
				//showLogin();
				showTut();
			}
//			else if( accessMode == App_ACCESS_MODE.BLOCKED || accessMode == App_ACCESS_MODE.TRIAL_MODE ){
//				goToEnterVerificationCode();
//				finish();
//			}
			else{
				goToMain();
				finish() ;
			}
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		handler = new Handler() ;
		handler.postDelayed(proceedRunnable, SPLASH_DURATION);
		versionStatus = DataStore.getInstance().getVersionStatus() ;
		init();
		DataStore.getInstance().startScheduledUpdates();
		//DataStore.getInstance().triggerDataStoreUpdate(true);
	}
	
	private void init(){
		vSpinner = findViewById(R.id.vSpinner);
		//AnimationHelper.appliyPredefinedAmin(vSpinner, R.anim.floating_vertical_to_bottom, animListner);
	}

	@Override
	protected void onResume() {
		super.onResume();
		DataStore.getInstance().checkVersionValid(null);
	}
	
	private void triggerAppUpdate(){
		Intent i = new Intent() ;
		i.setData(Uri.parse(DataStore.SABEEN_UPDATE_LINK));
		startActivity(i);
	}
	
	private void showLogin(){
		
		Intent i = new Intent(getApplicationContext(), LoginActivity.class);
		startActivityForResult(i, LOGIN_ACTIVITY_REQ_CODE);
	}
	private void goToMain(){
		Intent i = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(i);
	}
	
	private void goToEnterVerificationCode(){
		Intent i = new Intent(getApplicationContext(), VerificationActivity.class);
		startActivity(i);
	}
	
	private void onVersionInvalid (){
		APP_VERSION_STATUS status = DataStore.getInstance().getVersionStatus() ;
		if(status == APP_VERSION_STATUS.VERSION_INVALID){
			DiagMsg diagMsg = new DiagMsg(getString(R.string.is_version_valid_msg_update_rquired),
					R.string.is_version_valid_download,
					0,
					false, false, listenerDiagVersioninvalid);
			diagMsg.show(getSupportFragmentManager(), "diag_invalid");
		}else{
			DiagMsg diagMsg = new DiagMsg(getString(R.string.is_version_valid_msg_outdated),
					R.string.is_version_valid_download,
					R.string.is_version_valid_cansel,
					true, true, listenerDiagVersioninvalid);
			diagMsg.show(getSupportFragmentManager(), "diag_invalid");
		}
	}
	
	private void showTut(){
		Intent i = new Intent(getApplicationContext(), TutActivity.class);
		startActivityForResult(i, TUT_ACTIVITY_REQ_CODE);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == TUT_ACTIVITY_REQ_CODE){
			showLogin();
		}else if(requestCode == LOGIN_ACTIVITY_REQ_CODE && resultCode == Activity.RESULT_OK ){
			if(DataStore.getInstance().getAccessMode() != App_ACCESS_MODE.NOT_VERIFIED ){
				goToMain() ;
				finish();
			}else{
				goToEnterVerificationCode();
				finish();
			}
		}else{
			finish() ;
		}
	}
	
	

}
