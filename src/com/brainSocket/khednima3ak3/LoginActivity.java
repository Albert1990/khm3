package com.brainSocket.khednima3ak3;

import java.util.ArrayList;
import java.util.HashMap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.KhedniApp.phoeNumCheckResult;
import com.brainSocket.khednima3ak3.data.DataStore;
import com.brainSocket.khednima3ak3.data.FacebookProvider;
import com.brainSocket.khednima3ak3.data.FacebookProviderListener;
import com.brainSocket.khednima3ak3.data.ServerAccess;
import com.brainSocket.khednima3ak3.data.ServerResult;
import com.brainSocket.khednima3ak3.data.DataStore.App_ACCESS_MODE;
import com.brainSocket.khednima3ak3.data.DataStore.DataRequestCallback;
import com.brainSocket.khednima3ak3.model.AppUser;
import com.brainSocket.khednima3ak3.model.AppUser.GENDER;
import com.facebook.Profile;

public class LoginActivity extends AppBaseActivity implements OnClickListener {

	private enum LOGIN_STAGE {ENTER_PHONE_NUM, ENTER_USER_DETAILS}
	private boolean attemptingLogin = false ;

	// Values for email and password at the time of the login attempt.
	String attemptingFName ;
	String attemptingLName ;
	String attemptingPhoneNum ;
	GENDER gender ;

	// UI references.
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	//private LinearLayout llPhoneNumForm;
	//ScrollView svUserInfoForm ;
	EditText etPhoneNum, etSignupFName, etSignupLName;
	View btnSignup, btnFBlogin, btnEnterPhoneNum;
	//CheckBox chkGender ;
	Spinner spGender;
	
	private LOGIN_STAGE currentLoginStage ;
	private boolean linkWithFB = false; // indicates if we should try to link with facebook whenSignUp is done ;
	private String FbToken =null;
	
	Handler handler ;
	
	Fragment currentFrag ;
	FragmentManager fragMgr ;
	
	
	FacebookProviderListener facebookLoginListner = new FacebookProviderListener() {
		
		@Override
		public void onFacebookSessionOpened(String accessToken, String userId) {
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
		
		  FbToken = accessToken ;
		  Profile profile = com.facebook.Profile.getCurrentProfile();
		  String fname = profile.getFirstName();
		  String lname = profile.getLastName();
		  String id = profile.getId();
		  String gender = "male"; // TODO cant retrieve gender for now, facebook login always results a male gender 
		  GENDER genderType = ( gender.equalsIgnoreCase("male") )? GENDER.MALE : GENDER.FEMALE ;
		  
		  DataStore.getInstance().attemptSignUp(attemptingPhoneNum, fname, lname, genderType, KhedniApp.VERSIOIN_ID, id, apiLoginCallback);
		  linkWithFB = true ;
		  FacebookProvider.getInstance().unregisterListener();
		}
		@Override
		public void onFacebookSessionClosed() {}
		@Override
		public void onFacebookException(Exception exception) {
			
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		init() ;
	}
	
	private void init() {
		
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		
		fragMgr = getSupportFragmentManager() ;
		currentLoginStage = LOGIN_STAGE.ENTER_PHONE_NUM;
		handler = new Handler() ;
		switchLoginStage(currentLoginStage);
	}
	
	void hideKeypoard (TextView tv){  
		try{
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);
		}catch(Exception e){}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	/**
	 * try login first using facebook if success then singning up to the API Server using the 
	 * facebook Id and phone number entered in the previous stage 
	 */
	public void attempFBtLogin() {

		if( attemptingPhoneNum == null || attemptingPhoneNum.isEmpty())
			return;
					
		resetInputErrors();
		
		ArrayList<String> perm1=new ArrayList<String>();
		perm1.add("public_profile");
		perm1.add("user_friends");
		perm1.add("email");
		
		//Session.openActiveSession(this, true, permissions, callback)
		FacebookProvider.getInstance().registerListener(facebookLoginListner);
		FacebookProvider.getInstance().requestFacebookLogin(this);
		//Session.StatusCallback callback =  new LoginStatsCallback() ;
		//Session.openActiveSession(LoginActivity.this, true, perm1, callback ) ;
		showProgress(true);
	}
	
	public void attempSignUp() {
		
		// Store values at the time of the login attempt.
		attemptingFName = etSignupFName.getText().toString();
		attemptingLName = etSignupLName.getText().toString();
		resetInputErrors();
		boolean isMale = spGender.getSelectedItemPosition()==0 ;
		this.gender = (isMale)?GENDER.MALE : GENDER.FEMALE ;

		boolean cancel = false;
		View focusView = null;
		
		if(attemptingFName == null || attemptingFName.length() < 2){
			etSignupFName.setError(getString(R.string.error_user_name_required));
			cancel = true;
			focusView = etSignupFName ;
		}else if(attemptingLName == null || attemptingLName.length() < 2){
			etSignupLName.setError(getString(R.string.error_user_name_required));
			cancel = true;
			focusView = etSignupLName ;
		}else if(spGender.getSelectedItemPosition()<0){
			cancel = true;
			focusView = spGender ;
			Toast.makeText(this, R.string.error_gender_required, Toast.LENGTH_LONG).show();
		}
		
		if (cancel) {
			focusView.requestFocus();
		} else { 
			showProgress(true);
			DataStore.getInstance().attemptSignUp(attemptingPhoneNum, attemptingFName, attemptingLName, gender, KhedniApp.VERSIOIN_ID,"", apiLoginCallback);
		}
	}
	
	
		
	public void attempLogIn() {
		try{
			attemptingPhoneNum = etPhoneNum.getText().toString().replaceAll("\\s+","");
			resetInputErrors();
			boolean cancel = false;
			View focusView = null;
	
			// Check for a valid email address.
			phoeNumCheckResult numValid = KhedniApp.validatePhoneNum(attemptingPhoneNum);
			
			switch (numValid) {
			case SHORT:
				etPhoneNum.setError(getString(R.string.error_short_phone_num));
				focusView = etPhoneNum;
				cancel = true;
				break;
			case EMPTY:
				etPhoneNum.setError(getString(R.string.error_field_required));
				focusView = etPhoneNum;
				cancel = true;
				break;
			case WRONG:
				etPhoneNum.setError(getString(R.string.error_incorrect_phone_num));
				focusView = etPhoneNum;
				cancel = true;
				break;
			}
			
			if (cancel) {
				focusView.requestFocus();
			} else {
				showProgress(true);
				DataStore.getInstance().attemptLogin(attemptingPhoneNum, apiLoginCallback);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	private void resetInputErrors(){
		try {
			etPhoneNum.setError(null);
		} catch (Exception e) {}
		try {
			etSignupFName.setError(null);
			etSignupLName.setError(null);
		} catch (Exception e) {}
	}
	
	
	private void switchLoginStage(LOGIN_STAGE newStage) {
		try {
			switch(newStage) {		
			case ENTER_PHONE_NUM:
				currentFrag = new FragPhoenNumberForm();
				fragMgr.beginTransaction()
					.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right,R.anim.slide_in_from_right,R.anim.slide_out_to_left)
					//.addToBackStack(section.name())
					.replace(R.id.login_form, currentFrag)
					.commit();
				break;
			case ENTER_USER_DETAILS:
				currentFrag = new FragUserDetailsForm() ;
				fragMgr.beginTransaction()
					.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right,R.anim.slide_in_from_right,R.anim.slide_out_to_left)
					.addToBackStack(newStage.name())
					.replace(R.id.login_form, currentFrag)
					.commit();
				break;
			}
			currentLoginStage = newStage ;
			resetInputErrors();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	
	
	//// temp Callback >>>> this is just a tempCallback cuz the API is not available yet 
/*	DataRequestCallback apiLoginCallback = new DataRequestCallback() {
		@Override
		public void onDataReady(HashMap<String, Object> data, boolean success) {
			try {
				attemptingLogin = false ;
				showProgress(false);
				// success means data is retrived from server and does not indicate login success 
				if(currentLoginStage == LOGIN_STAGE.ENTER_PHONE_NUM){
					switchLoginStage(LOGIN_STAGE.ENTER_USER_DETAILS) ;
				}else if(currentLoginStage == LOGIN_STAGE.ENTER_USER_DETAILS){
					setResult(RESULT_OK);
					finish();
				}

			}
			catch(Exception c)
			{	
				Toast.makeText(getApplicationContext(), getString(R.string.error_signingin), Toast.LENGTH_SHORT).show();
				attemptingLogin = false ;
			}
		}
	};*/
	
	 
	DataRequestCallback apiLoginCallback = new DataRequestCallback() {
		@Override
		public void onDataReady(ServerResult result, boolean success) {
			try {
				attemptingLogin = false ;
				showProgress(false);
				// success means data is retrived from server and does not indicate login success 
				if(success){
					HashMap<String, Object> data = result.getPairs() ;
					AppUser user = (AppUser) data.get("appUser");
					//boolean loginSuccess = (Boolean) data.get("loginResult");
					//LoginMode loginMode = (LoginMode) data.get("loginMode");
					if(result.getFlag().equals(ServerAccess.ERROR_CODE_userNotExists)){ // user tried to login but server didnt recognize him "he isn't registered before"
						switchLoginStage(LOGIN_STAGE.ENTER_USER_DETAILS);
					}else if(result.getFlag().equals(ServerAccess.ERROR_CODE_userExistsBefore)){
						KhedniApp.showToast(LoginActivity.this, getString(R.string.login_msg_facebook_account_already_exists));
					}else{
						DataStore.getInstance().setMe(user);
						DataStore.getInstance().requestGCMRegsitrationId();
						DataStore.getInstance().triggerDataStoreUpdate(true);
						//DataStore.getInstance().setAccessMode(user.isTriallMode()?App_ACCESS_MODE.TRIAL_MODE:App_ACCESS_MODE.BLOCKED);//DataStore.getInstance().requestVerificationMsg(null);
						DataStore.getInstance().setAccessMode(App_ACCESS_MODE.NOT_VERIFIED);//DataStore.getInstance().requestVerificationMsg(null);
						if(linkWithFB && FbToken != null){
							DataStore.getInstance().connectWithFB(FbToken, null);
						}
						setResult(RESULT_OK);
						finish();
					}
				}else{
					// optinonaly we may extract some error message from "data" in some cases
					Toast.makeText(getApplicationContext(), getString(R.string.error_connection_failed), Toast.LENGTH_SHORT).show();
				}

			}
			catch(Exception c)
			{	
				Toast.makeText(getApplicationContext(), getString(R.string.error_signingin), Toast.LENGTH_SHORT).show();
				attemptingLogin = false ;
			}
		}
	};
	
	
//	public class  LoginStatsCallback implements Session.StatusCallback {
//
//		@Override
//		public void call(Session session, SessionState state,Exception exception) {
//			Request meRequest ;
//			Request friendsRequest ;
//			
//			if (session.isOpened()){
//				
//	    		meRequest = Request.newMeRequest(session, new Request.GraphUserCallback() {
//
//	    		  // callback after Graph API response with user object
//	    		  @Override
//	    		  public void onCompleted(GraphUser user, Response response) {
//	    			  if (user != null) {
//	    				  //Toast.makeText(LoginActivity.this, "Authenticated to facebook ID "+ user.getId(), Toast.LENGTH_LONG).show();
//  	    				  mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
//  	    				  FbToken = Session.getActiveSession().getAccessToken() ;
//  	    				  String name = user.getName();
//  	    				  String gender = user.getProperty("gender").toString() ;
//  	    				  GENDER genderType = ( gender.equalsIgnoreCase("male") )? GENDER.MALE : GENDER.FEMALE ;
//  	    				  DataStore.getInstance().attemptSignUp(attemptingPhoneNum, name, genderType, String.valueOf(android.os.Build.VERSION.SDK_INT), RosaryApp.VERSIOIN_ID, apiLoginCallback);
//  	    				  linkWithFB = true ;
//	    				}
//	    		  }
//	    		});
//	    		
//	    		
//	    		friendsRequest = new Request( session,"/me/friends", null, HttpMethod.GET,
//	    		    new Request.Callback() {
//	    		        public void onCompleted(Response response) {
//	    		            /* handle the result */
//	    		        	//Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
//	    		        	//Log.i("friends", response.toString());
//	    		        }
//	    		    }
//	    		);
//	    		
//	    		meRequest.executeAsync();
//	    		friendsRequest.executeAsync();
//	    		
//	    	}
//			
//		}
//		
//	}

	@Override
	public void onClick(View arg0) {
		int vId = arg0.getId();
		
		switch (vId) {
		case R.id.btnFBLogin:
			attempFBtLogin();
			break;
		case R.id.btnEnterPhoneNum:
			attempLogIn() ;
			break;
		case R.id.btnSignup:
			attempSignUp();
			break;

		}
	}
	
	
	public static class FragPhoenNumberForm extends Fragment  {


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			View view = inflater.inflate(R.layout.frag_enter_num, container, false);
			return view;
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			try{
				init((LoginActivity) getActivity());
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		protected void send() {
			try {
				((LoginActivity)getActivity()).attempLogIn() ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void init(final LoginActivity activity){
			activity.etPhoneNum = (EditText) getView().findViewById(R.id.phone);
			activity.btnEnterPhoneNum = getView().findViewById(R.id.btnEnterPhoneNum);
	
			activity.btnEnterPhoneNum.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					send();
				}
			});
			activity.handler.postDelayed( new Runnable() {
				@Override
				public void run() {
					if(isAdded())
					activity.hideKeypoard(activity.etPhoneNum);
				}
			}, 1000);
		}	
	}
	
	public static class FragUserDetailsForm extends Fragment  {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			View view = inflater.inflate(R.layout.frag_enter_user_details, container, false);
			return view;
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			init((LoginActivity) getActivity());
		}
		
		private void init(final LoginActivity activity){
			activity.etSignupFName = (EditText) getView().findViewById(R.id.etFirstName);
			activity.etSignupLName = (EditText) getView().findViewById(R.id.etLastName);
			activity.btnFBlogin = getView().findViewById(R.id.btnFBLogin);
			activity.btnSignup =  getView().findViewById(R.id.btnSignup);
			activity.spGender = (Spinner) getView().findViewById(R.id.spGenderPicker);
			
			activity.btnFBlogin.setOnClickListener(activity);
			activity.btnSignup.setOnClickListener(activity);
			
			activity.handler.postDelayed( new Runnable() {
				@Override
				public void run() {
					if(!isAdded())
						return ;
					activity.hideKeypoard(activity.etPhoneNum);
				}
			}, 1000);
		}			
			
	}
	
}
