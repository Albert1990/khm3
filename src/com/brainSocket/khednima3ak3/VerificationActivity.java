package com.brainSocket.khednima3ak3;

import java.util.HashMap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.data.DataStore;
import com.brainSocket.khednima3ak3.data.ServerAccess;
import com.brainSocket.khednima3ak3.data.ServerResult;
import com.brainSocket.khednima3ak3.data.DataStore.DataRequestCallback;
import com.brainSocket.khednima3ak3.model.AppUser.GENDER;

public class VerificationActivity extends AppBaseActivity implements OnClickListener {
	private final static int CODE_REQUEST_MIN_DURATION = 1000 * 60 * 3 ;  
	public final static int resultCodeReRegiter = 34 ;
	
	private boolean attemptingLogin = false ;
	
	// Values for email and password at the time of the login attempt.
	String attemptingMsg ;
	GENDER gender ;
	
	private View mLoginFormView;
	private View mLoginStatusView;
	EditText etVerifMsg;
	View btnEnterVerifCode, btnResendVerifCode, btnRegisterAgain;
	
	private Handler handler ;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_activate_account);
		init() ;
	}
	
	private void init() {
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		etVerifMsg = (EditText) findViewById(R.id.etVerifCode);
		btnEnterVerifCode = findViewById(R.id.btnVerif);
		btnRegisterAgain = findViewById(R.id.btnRe_register);
		btnResendVerifCode = findViewById(R.id.btnRsendCode);
		
		btnEnterVerifCode.setOnClickListener(this);
		btnRegisterAgain.setOnClickListener(this);
		btnResendVerifCode.setOnClickListener(this);
		
		handler = new Handler() ;
	}
		
	public void attempVerif() {
		
		attemptingMsg = etVerifMsg.getText().toString();
		resetInputErrors();
		boolean cancel = false;
		View focusView = null;

		if(attemptingMsg == null || attemptingMsg.isEmpty()){
			etVerifMsg.setError(getString(R.string.verif_err_verif_code_required));
			focusView = etVerifMsg;
			cancel = true;
		}
		
		if (cancel) {
			focusView.requestFocus();
		} else {
			showProgress(true);
			DataStore.getInstance().verifyAccount(attemptingMsg, verificationCallback);
		}
	}

	
	public void requestResendVerifMsg() {
		showProgress(true);
		DataStore.getInstance().requestVerificationMsg(callbackRequestResendVeri);
	}
	
	public void re_register(){
		DataStore.getInstance().logoutUser();
		setResult(resultCodeReRegiter);
		finish();
		Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
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
			etVerifMsg.setError(null);
		} catch (Exception e) {}
	}
	
	DataRequestCallback verificationCallback = new DataRequestCallback() {
		@Override
		public void onDataReady(ServerResult result, boolean success) {
			try {
				attemptingLogin = false ;
				showProgress(false);
				resetInputErrors();
				// success means data is retrived from server and does not indicate login success 
				if(success){
					HashMap<String, Object> data = result.getPairs() ;
					//boolean loginSuccess = (Boolean) data.get("verified");
					if(result.getFlag().equals(ServerAccess.ERROR_CODE_verificationMessageNotExists)){
						etVerifMsg.setError(getString(R.string.verif_err_verif_code_invalid));
					}else if (result.isValid()){
						DataStore.getInstance().requestGCMRegsitrationId();
						Intent i = new Intent(VerificationActivity.this, MainActivity.class);
						startActivity(i);
						setResult(RESULT_OK);
						finish();
					}
				}else{
					//Optionaly we may extract some error message from "data" in some cases
					Toast.makeText(getApplicationContext(), getString(R.string.error_connection_failed), Toast.LENGTH_SHORT).show();
				}

			}
			catch(Exception c) {	
				Toast.makeText(getApplicationContext(), getString(R.string.verif_err_verif_failed), Toast.LENGTH_SHORT).show();
				attemptingLogin = false ;
			}
		}
	};

	DataRequestCallback callbackRequestResendVeri = new DataRequestCallback() {
		@Override
		public void onDataReady(ServerResult data, boolean success) {
			showProgress(false);
			if(success){
				boolean resent = (Boolean) data.getValue("msgSent");
				if(resent){
					btnResendVerifCode.setEnabled(false);
					btnResendVerifCode.setAlpha(0.3f);
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if(isVisible){
								btnResendVerifCode.setEnabled(true);
								btnResendVerifCode.setAlpha(1f);
							}
						}
					}, CODE_REQUEST_MIN_DURATION);
				}else{
					Toast.makeText(VerificationActivity.this, R.string.verif_err_failed_to_resend_verif_code, Toast.LENGTH_LONG).show();
					btnResendVerifCode.setEnabled(true);
					btnResendVerifCode.setAlpha(1f);
				}
			}else{
				btnResendVerifCode.setEnabled(true);
				btnResendVerifCode.setAlpha(1f);
				Toast.makeText(getApplicationContext(), getString(R.string.error_connection_failed), Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	@Override
	public void onClick(View arg0) {
		int vId = arg0.getId();
		
		switch (vId) {
		case R.id.btnVerif:
			attempVerif() ;
			break;
		case R.id.btnRe_register:
			re_register();
		break;
		case R.id.btnRsendCode:
			requestResendVerifMsg();
			break;
		}
	}
	
	
}
