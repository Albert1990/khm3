package com.brainSocket.khednima3ak3;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.data.DataStore;
import com.brainSocket.khednima3ak3.model.AppEvent;


public class ActivityEventPrompt extends Activity implements OnClickListener {

	Handler handler;
	View btnOK, btnCansel;
	TextView tvMsg, tvZeker, tvCount;
	AppEvent event ;
	WakeLock wl ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | 
			    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
			    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
			    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
			    WindowManager.LayoutParams.FLAG_FULLSCREEN | 
			    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
			    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
			    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		
		setContentView(R.layout.activity_event_prompt);
		handler = new Handler();
		init();
		
		try {
			Bundle extras = getIntent().getExtras();
			if(extras != null){
				String eventStr = (String) extras.getString("event") ;
				JSONObject jsonEvent = new JSONObject(eventStr);
				event = new AppEvent(jsonEvent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	    wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "70 wakelock");
	    wl.acquire();
	        
	    playBeep();
	}

	private void init() {
		btnOK = findViewById(R.id.btnAccept);
		btnCansel = findViewById(R.id.btnCancel);
		tvMsg = (TextView) findViewById(R.id.tvMsg);
		tvZeker = (TextView) findViewById(R.id.tvZeker);
		tvCount = (TextView) findViewById(R.id.tvCount);

		btnOK.setOnClickListener(this);
		btnCansel.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateDisplay();

	}
	@Override
	protected void onStop() {
		super.onStop();
		try{
			wl.release();
		}catch(Exception e){}
	}
	
	private void updateDisplay(){
		try{
			if(event != null){
				//Log.d("event", event.getJsonString());
				//Toast.makeText(this, "hey", Toast.LENGTH_LONG).show();
				String msg = this.getString(R.string.event_prompt_msg_part1) + " " + event.getPeer().getName()+ " "+ this.getString(R.string.event_prompt_msg_part2) ;  
				tvMsg.setText(msg);
				
				tvZeker.setText(event.getContentValue());
				String countSuffix = getString(R.string.event_prompt_times);
				int goal = event.getGoal() ;
				if(goal == 1 || goal >10)
					countSuffix = getString(R.string.event_prompt_time);
				tvCount.setText(event.getGoal() + " " + countSuffix);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void playBeep() {
	    try {
	    	MediaPlayer m = new MediaPlayer() ;
	        if (m.isPlaying()) {
	            m.stop();
	            m.release();
	            m = new MediaPlayer();
	        }
	        
	        // detect silet mode
	        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	        if(am.getRingerMode() == AudioManager.RINGER_MODE_SILENT || am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE )
	        	return ;	        

	        AssetFileDescriptor descriptor = getAssets().openFd("ding.m4a");
	        m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
	        descriptor.close();

	        m.prepare();
	        m.setVolume(1f, 1f);
	        m.setLooping(false);
	        m.start();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	private void onCancel () {
		if(event != null)
			//DataStore.getInstance().responseToZekr(event.getIdGlobal(), false, null);
		finish();
	}
	
	private void onAccept (){
		if(event != null){
			//DataStore.getInstance().responseToZekr(event.getIdGlobal(), true, null);
			Intent i = new Intent(this, ConversationActivity.class);
			i.putExtra("sessionId", event.getSessionId());
			startActivity(i);
			finish();
		}else{
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId() ;
		switch (id) {
		case R.id.btnAccept:
			onAccept();
			break;
		case R.id.btnCancel:
			onCancel(); 
		break;
		}
	}

}
