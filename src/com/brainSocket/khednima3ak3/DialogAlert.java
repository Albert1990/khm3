package com.brainSocket.khednima3ak3;

import com.brainSocket.khednima3ak3.R;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class DialogAlert extends Dialog {

	
	private Activity activity ;
	android.view.View.OnClickListener onClickListener = null ;
	String title , msg, btnRightText, btnLeftText ;
	boolean okDialogOnly=false;
	
	// Views 
	private TextView tvBtnRight, tvBtnLeft, tvTitle, tvMsg ;
	
	

	
	public DialogAlert(Activity activity,String title,String msg, String btnRight,String btnLeft ,android.view.View.OnClickListener clickListener) {
		super(activity); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
		setContentView(R.layout.dialog_alert);
		
		this.activity = activity ;
		this.onClickListener = clickListener ;
		if(btnLeft==null)
			okDialogOnly=true;
		else
			btnLeftText = btnLeft ;
		btnRightText = btnRight ;
		this.title = title ;
		this.msg = msg ;
		
		init() ;
	}

	public DialogAlert(Activity activity,int title,int msg, int btnRight,int btnLeft ,android.view.View.OnClickListener clickListener) {
		
		this(activity,activity.getString(title),activity.getString(msg), activity.getString(btnRight), activity.getString(btnLeft) ,clickListener) ;
	}
	
	public DialogAlert(Activity activity, String title,int msg, int btnRight,int btnLeft ,android.view.View.OnClickListener clickListener) {
		
		this(activity,title,activity.getString(msg), activity.getString(btnRight), activity.getString(btnLeft) ,clickListener) ;
	}
	
	public DialogAlert(Activity activity, int title,String msg, int btnRight,int btnLeft ,android.view.View.OnClickListener clickListener) {
		
		this(activity,activity.getString(title),msg, activity.getString(btnRight), activity.getString(btnLeft) ,clickListener) ;
	}
	
public DialogAlert(Activity activity, int title,String msg, int btn ,android.view.View.OnClickListener clickListener) {
		
		this(activity,activity.getString(title),msg, activity.getString(btn), null ,clickListener) ;
	}
	
	private void init (){
		
		tvBtnLeft= (TextView) findViewById(R.id.tvBtnLeft) ;
		if(okDialogOnly)
			tvBtnLeft.setVisibility(View.GONE);
		tvBtnRight = (TextView) findViewById(R.id.tvBtnRight) ;
		tvTitle  = (TextView) findViewById(R.id.tvTitle);
		tvMsg = (TextView) findViewById(R.id.tvMsg);
		
		tvTitle.setText(title);
		tvMsg.setText(msg);
		
		tvBtnLeft.setText(btnLeftText);
		tvBtnRight.setText(btnRightText);
		
		if(title == null || title.isEmpty()){
			tvTitle.setVisibility(View.GONE);
		}
		
		if(onClickListener != null){
			tvBtnLeft.setOnClickListener(onClickListener);
			tvBtnRight.setOnClickListener(onClickListener);
		}
	}

	
}
