package com.brainSocket.khednima3ak3;

import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.data.FacebookProvider;
import com.brainSocket.khednima3ak3.data.TrackingMgr;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class DialogShareOnFacebook extends Dialog implements android.view.View.OnClickListener{
	
	private Activity activity ;
	android.view.View.OnClickListener onClickListener = null ;
	String msg, btnRightText, btnLeftText ;
	
	// Views 
	private TextView tvBtnRight, tvBtnLeft, tvMsg ;
	
	public DialogShareOnFacebook(Activity activity,String msg, String btnRight,String btnLeft ,android.view.View.OnClickListener clickListener) {
		super(activity); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
		setContentView(R.layout.dialog_share_on_facebook);
		
		this.activity = activity ;
		this.onClickListener = clickListener ;
		btnLeftText = btnLeft ;
		btnRightText = btnRight ;
		this.msg = msg ;
		
		init() ;
	}

	public DialogShareOnFacebook(Activity activity,int msg, int btnRight,int btnLeft ,android.view.View.OnClickListener clickListener) {
		
		this(activity,activity.getString(msg), activity.getString(btnRight), activity.getString(btnLeft) ,clickListener) ;
	}
		
	public DialogShareOnFacebook(Activity activity,String msg, int btnRight,int btnLeft ,android.view.View.OnClickListener clickListener) {
		
		this(activity,msg, activity.getString(btnRight), activity.getString(btnLeft) ,clickListener) ;
	}
	
	private void init (){
		
		tvBtnLeft= (TextView) findViewById(R.id.tvBtnLeft) ;
		tvBtnRight = (TextView) findViewById(R.id.tvBtnRight) ;
		tvMsg = (TextView) findViewById(R.id.tvMsg);
		
		tvMsg.setText(msg);
		
		tvBtnLeft.setText(btnLeftText);
		tvBtnRight.setText(btnRightText);
		
		if(onClickListener != null){
			tvBtnLeft.setOnClickListener(onClickListener);
			tvBtnRight.setOnClickListener(onClickListener);
		}else{
			tvBtnLeft.setOnClickListener(this);
			tvBtnRight.setOnClickListener(this);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvBtnLeft:
			dismiss();
			break;
		case R.id.tvBtnRight:
			FacebookProvider.getInstance().sharePhotoViaFacebook(activity);
			TrackingMgr.getInstance().sendTrackingEvent(activity, "share", "CreateTrip", "ShareDiag");
			dismiss();
			break;
		}
	}
}
