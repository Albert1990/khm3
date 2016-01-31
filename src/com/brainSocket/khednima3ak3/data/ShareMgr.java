package com.brainSocket.khednima3ak3.data;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.widget.Toast;

import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.KhedniApp;


public class ShareMgr {
	
	public static final String SUPPORT_EMAIL = "fatherboard1@gmail.com" ;
	private static final String DEFAULT_MAIL_CLIENT = "gmail";
	
	private static ShareMgr instance ;
	
	public static ShareMgr getInstance (){
		if(instance == null)
			instance = new ShareMgr() ;
		return instance;
	}
	
	private ShareMgr() {
		
	}
	
	public void sendFeedback (Activity activity){
		String subj = activity.getString(R.string.send_feedback_subj);
		String diagTitle = activity.getString(R.string.send_feedback_dialog_title);
		sendEmail(activity, subj, SUPPORT_EMAIL,diagTitle); 
	}
		
	/*	
	public void shareApp(Activity activity){
		String msg = activity.getString(R.string.share_app_msg);
		String subj = activity.getString(R.string.share_app_subject);
		String dialogTitle = activity.getString(R.string.share_app_dialog_title);
		
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_TEXT  , msg);
		i.putExtra(Intent.EXTRA_SUBJECT, subj);
		try {
		    activity.startActivity(Intent.createChooser(i, dialogTitle ));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(RosaryApp.getAppContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}*/
	
	
	/**
	 * trying to open Gmail Directly ..... if it fails we will open a chooser dialog
	 * @param activity
	 */
	private void sendEmail(Activity activity, String subject, String emial,String diagTitle){
		
		Intent intent = new Intent(android.content.Intent.ACTION_SEND); 
		intent.setType("text/html");
		List<ResolveInfo> resInfo = activity.getPackageManager().queryIntentActivities(intent, 0);

		ResolveInfo mailClientInfo = null ;
		if (!resInfo.isEmpty()) {
		    for (ResolveInfo info : resInfo){
			    if (info.activityInfo.packageName.toLowerCase().contains(DEFAULT_MAIL_CLIENT) || info.activityInfo.name.toLowerCase().contains(DEFAULT_MAIL_CLIENT)) {
		    		mailClientInfo = info ;
		    		break ;
			    }
		    }
		} 
		    
		if(mailClientInfo != null){ // we found gmail package
            intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{emial});
    		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.setPackage(mailClientInfo.activityInfo.packageName);   
            activity.startActivity(Intent.createChooser(intent, diagTitle ));
		}else{/// we couldnt find gmail so open Chooser dialog
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL  , new String[]{SUPPORT_EMAIL});
			i.putExtra(Intent.EXTRA_SUBJECT, subject);
			try {
			    activity.startActivity(Intent.createChooser(i, diagTitle ));
			} catch (android.content.ActivityNotFoundException ex) {
			    Toast.makeText(KhedniApp.getAppContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
