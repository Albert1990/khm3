package com.brainSocket.khednima3ak3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.model.AppEvent;
import com.brainSocket.khednima3ak3.model.AppEvent.TASK_STATE;

public class DiagMsg extends DialogFragment implements android.view.View.OnClickListener {
	private static final String TAG = "diagMsg";
	
	View view;
	AlertDialog.Builder builder ;
	TextView btnOk, btnCansel;
	TextView tvMsg;

	DiagMsgListener diagCallback;
	
	// data
	String msg ;
	boolean enableBtn2 ;
	int btn1Txt;
	int btn2txt;
	boolean canselable ;
	public DiagMsg( String msg, int btn1Txt, int btn2Txt, boolean enableBtn2, boolean isCanselable, DiagMsgListener callBack) {
		diagCallback = callBack;
		this.enableBtn2 = enableBtn2;
		this.btn1Txt = btn1Txt ;
		this.btn2txt = btn2Txt ;
		this.msg = msg ;
		this.canselable = isCanselable ;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		view= getActivity().getLayoutInflater().inflate(R.layout.diag_msg, null);
		dialog.setContentView(view);
		init();
		initBtns();
		updateViewData();
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		return dialog ;
	}

	private void init(){
		tvMsg = (TextView) view.findViewById(R.id.tvMsg);
		setCancelable(canselable);
	}
	
	private void updateViewData(){
		tvMsg.setText(msg);
		btnOk.setText(btn1Txt);
		if(enableBtn2 && btn1Txt!=0)
			btnCansel.setText(btn2txt);
	}
	
	void initBtns(){
		btnOk = (TextView) view.findViewById(R.id.btnAccept);
		btnCansel = (TextView) view.findViewById(R.id.btnCansel);
		if(!enableBtn2){
			btnCansel.setVisibility(View.GONE);
		}
		btnOk.setOnClickListener(this);
		btnCansel.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		int viewId=v.getId();
		switch (viewId) {
		case R.id.btnAccept:
				//sendZicker();
			if(diagCallback != null){
				diagCallback.onBtn1Pressed(this);
			}else if(!enableBtn2){
				dismiss();
			}
			break;
		case R.id.btnCansel:
			if(diagCallback != null){
				diagCallback.onBtn2Pressed(this);
			}
			break;
		}
	}

	public interface DiagMsgListener {
		public void onBtn1Pressed( DialogFragment dialog);
		public void onBtn2Pressed(DialogFragment dialog);
	}	

}