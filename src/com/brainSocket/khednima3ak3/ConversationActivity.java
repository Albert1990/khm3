package com.brainSocket.khednima3ak3;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.data.DataStore;
import com.brainSocket.khednima3ak3.data.PhotoProvider;
import com.brainSocket.khednima3ak3.data.ServerResult;
import com.brainSocket.khednima3ak3.data.DataStore.DataRequestCallback;
import com.brainSocket.khednima3ak3.model.AppConversation;
import com.brainSocket.khednima3ak3.model.AppMessage;
import com.brainSocket.khednima3ak3.model.AppSession;
import com.brainSocket.khednima3ak3.model.AppUser;
import com.brainSocket.khednima3ak3.views.TextViewCustomFont;

public class ConversationActivity extends AppBaseActivity implements OnClickListener{

	public final static int DO_TASK_REQUEST_CODE = 416 ;
	public final static String START_DO_TASK_IMMEDIATELLY_KEY = "startTaskImmediatelly";
	public final static String SESSION_ID_KEY = "sessionId";
	public final static String event_KEY = "event";
	
	ListView lvEvents ;
	ConversationEventsAdapter adapter ;
	ArrayList<AppMessage> events ;
	AppConversation conversation ;
	AppUser peers ;
	
	Handler handler ;
	
	String sessionId ;
	View  progressSpinner ;  
	
	ImageView ivlogo ;
	TextView tvTitle ;
	SwipeRefreshLayout srSwipeToRefresh ;
	View vEmptyContainer ;
	TextView tvWarning ;
	View vArrowPointer ;
	
	EditText etMsg;
	View btnSendMsg ;
	
	//callback for conversation history activity
	
	int newEventCount ;
	DataRequestCallback eventsCallback = new DataRequestCallback() {
		@Override
		public void onDataReady(ServerResult data, boolean success) {
			showloading(false);
			srSwipeToRefresh.setRefreshing(false);
			if(success){
				events = (ArrayList<AppMessage>) data.getValue("events");
				//Collections.reverse(events);
				//adapter.updateData(events);
				onEventsReceived(events);
			}else{
				showError(getString(R.string.error_events_refresh_failed));
			}
		}
	};
				
	DataRequestCallback callbackSendMsg = new DataRequestCallback() {
		@Override
		public void onDataReady(ServerResult data, boolean success) {
			showloading(false);
			if(success){
				try {
					AppMessage newEvent = (AppMessage) data.getValue("event") ;
					sessionId = newEvent.getSessionId() ;
				} catch (Exception e) {
					// TODO: handle exception
				}
				requestEvents();
			}else{
				showError(getString(R.string.error_operation_failed));
			}
		}
	};
	//callback for send Zicker to user dialog
	
	OnRefreshListener onRefreshRequestedListner = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			DataStore.getInstance().requestSessionEvents( sessionId,eventsCallback );
			//showloading(true);
		}
	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		init();
		initCustomActionBar() ;
		try{
			Bundle extras = getIntent().getExtras() ;
			if(extras.containsKey(SESSION_ID_KEY)){
				sessionId = getIntent().getExtras().getString(SESSION_ID_KEY);
				AppSession session = DataStore.getInstance().getSessionById(sessionId);
				conversation = new AppConversation(session);
				peers = conversation.getSession().getPeer();
			}else if(extras.containsKey("contact")) {
				sessionId = null ;
				JSONObject jsnPeer = new JSONObject(extras.getString("contact"));
				peers = new AppUser(jsnPeer);
				AppSession session = DataStore.getInstance().getSessionByPeerId(peers.getId());
				if(session != null){
					conversation = new AppConversation(session);
					sessionId = session.getGlobalId();
				}else
					conversation = new AppConversation(peers);
			}
			
			//TODO check to remove tvWarning
			tvWarning.setVisibility(View.GONE);
			
			if(sessionId==null || sessionId.isEmpty()){
				vEmptyContainer.setVisibility(View.VISIBLE);
				vArrowPointer.clearAnimation();
			}else{
				vEmptyContainer.setVisibility(View.INVISIBLE);
				vArrowPointer.clearAnimation();
			}	
			if(peers != null){
				tvTitle.setText(peers.getFirstName());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private void init(){
		lvEvents = (ListView) findViewById(R.id.lvEvents);
		btnSendMsg = findViewById(R.id.btnSendMsg);
		srSwipeToRefresh = (SwipeRefreshLayout) findViewById(R.id.srSwipeToRefreshLayout);
		vEmptyContainer = findViewById(R.id.vEmptyListContainer);
		vArrowPointer = findViewById(R.id.ivArrow2);
		tvWarning = (TextView) findViewById(R.id.tvWarning);
		adapter = new ConversationEventsAdapter(new ArrayList<AppMessage>());
		progressSpinner = findViewById(R.id.pbSpinner);
		etMsg = (EditText) findViewById(R.id.etMsg);
		
		lvEvents.setAdapter(adapter);
		btnSendMsg.setOnClickListener(this);
		srSwipeToRefresh.setOnRefreshListener(onRefreshRequestedListner);
		
		tvWarning.setVisibility(View.GONE);
		vEmptyContainer.setVisibility(View.GONE);
		
		handler = new Handler();
		showloading(false);
	}
	
	
	private void initCustomActionBar() {
		
		ActionBar mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayHomeAsUpEnabled(false) ;
		mActionBar.setHomeAsUpIndicator(null);
		//LayoutInflater mInflater = LayoutInflater.from(this); 
		mActionBar.setCustomView(R.layout.custom_actionbar);
		mActionBar.setDisplayShowCustomEnabled(true);
		View mCustomView = mActionBar.getCustomView() ;
		mCustomView.invalidate();
		
		tvTitle = (TextViewCustomFont) mCustomView.findViewById(R.id.tvFragTitle) ;
		ImageView ivMenu = (ImageView) mCustomView.findViewById(R.id.ivMenu);
		View notifications = mCustomView.findViewById(R.id.actionNotifications);
		View chats = mCustomView.findViewById(R.id.actionChat);
		ivlogo = (ImageView) mCustomView.findViewById(R.id.ivLogo);
		
		ivlogo.setVisibility(View.GONE);
		ivMenu.setVisibility(View.GONE);
		tvTitle.setVisibility(View.VISIBLE);
		chats.setVisibility(View.GONE);
		notifications.setVisibility(View.GONE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		requestEvents();
	}
	
	private void sendZiker() {
		
		String msg = etMsg.getText().toString();
		if(msg == null || msg.isEmpty())
			return;
		etMsg.setText("");
		DataStore.getInstance().sendChatMessage(peers.getId(), msg, callbackSendMsg);
//		if(peers != null){
//			DiagCreateTask diag = new DiagCreateTask(getString(R.string.diag_create_title_with_peer), diagCreateListener) ;
//			diag.show(getSupportFragmentManager(),null);
//		}
//		String destMobileNumber=peers.get(0).getPhoneNum();
//		showloading(true);
//		DataStore.getInstance().sendZekrToUsers(destMobileNumber,contentId, NumberPickerValue, createZickerCallback);
//		dialog.dismiss();
	}
	
	private void showError(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	private void requestEvents(){
		if(sessionId != null){
			ArrayList<AppMessage> events = DataStore.getInstance().getSessionEvents(sessionId);
			if(events != null && !events.isEmpty())
				onEventsReceived(events);
			DataStore.getInstance().requestSessionEvents( sessionId,eventsCallback );
			showloading(true);
		}
	}
	
	private void scrollListToBottom() {
	    lvEvents.post(new Runnable() {
	        @Override
	        public void run() {
	            // Select the last row so it will scroll into view...
	        	int lastIndex = adapter.getCount() -1 ;
	        	if(lastIndex >= 0)
	        		lvEvents.setSelection(lastIndex);
	        }
	    });
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btnSendMsg:
			sendZiker();
			break;
		}
		
	}
	
	private void showloading (boolean show){
		if(show){
			progressSpinner.setVisibility(View.VISIBLE);
		}else{
			progressSpinner.setVisibility(View.GONE);
		}
	}
	
	private void onEventsReceived(ArrayList<AppMessage> eventsRecieved){
		if(eventsRecieved != null){
			events = eventsRecieved;
			Collections.sort(events);
			adapter.updateData(events);
			scrollListToBottom();
		}
		if(eventsRecieved == null || eventsRecieved.isEmpty()){
			vEmptyContainer.setVisibility(View.VISIBLE);
			vArrowPointer.clearAnimation();
		}else{
			vEmptyContainer.setVisibility(View.INVISIBLE);
			vArrowPointer.clearAnimation();
		}
	}
	
	private class ConversationEventsAdapter extends BaseAdapter {

		LayoutInflater inflater ;
		ArrayList<AppMessage> events ;
		//ArrayList<TrackInfo> fullTracksList ;	
		
		public void updateData(ArrayList<AppMessage> events){
			if(events == null)
				this.events = new ArrayList<AppMessage> () ;
			else {
				this.events= events ;  
			}			
			notifyDataSetChanged() ;
		}

		
		public ConversationEventsAdapter (ArrayList<AppMessage> cons){
			this.inflater = (LayoutInflater)ConversationActivity.this.getSystemService(AppBaseActivity.LAYOUT_INFLATER_SERVICE);
			if(cons != null)
				this.events = cons ;
			else
				this.events= new ArrayList<AppMessage>(); 
		}
		
		@Override
		public int getCount() {
			return events.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			Holder holder ;
			 if (convertView == null) {
		            holder = new Holder();
		            convertView = inflater.inflate(R.layout.row_conversation_events_list, parent, false);
		            holder.vMe = convertView.findViewById(R.id.vMe);
		            holder.vPeer = convertView.findViewById(R.id.vPeer);
		            holder.vinfo = convertView.findViewById(R.id.vInfo);
		            holder.vUnreadMsgsContainer = convertView.findViewById(R.id.vUnreadMsgsContainer);
		            holder.tvUnreadMsgsCount = (TextView) convertView.findViewById(R.id.tvUnreadMsgsCount);
		            holder.tvMe = (TextView) holder.vMe.findViewById(R.id.tvMe);
		            holder.tvPendingMe = (TextView) holder.vMe.findViewById(R.id.tvPendingMe);
		            holder.ivMe= (ImageView) holder.vMe.findViewById(R.id.ivMe);
		            holder.tvPeer= (TextView) holder.vPeer.findViewById(R.id.tvPeer);
		            holder.ivPeer= (ImageView) holder.vPeer.findViewById(R.id.ivPeer);
		            holder.tvPendingPeer = (TextView) holder.vPeer.findViewById(R.id.tvPendingPeer);
		            holder.tvInfo= (TextView) holder.vinfo.findViewById(R.id.tvInfo);
		            holder.ivCheckMe= (ImageView) holder.vMe.findViewById(R.id.ivCheckMe);
		            holder.ivCheckPeer= (ImageView) holder.vPeer.findViewById(R.id.ivCheckPeer);
		            holder.ivCheckMe.setVisibility(View.INVISIBLE);
		            holder.ivCheckPeer.setVisibility(View.INVISIBLE);
		            convertView.setTag(holder);
		        }else{
				holder = (Holder) convertView.getTag() ;
			}
			try{
				holder.vinfo.setVisibility(View.GONE);
				
				AppMessage event = events.get(position);
				
				if(event.isEventGeneratedByMe()){
					AppUser me = DataStore.getInstance().getMe() ;
					holder.vPeer.setVisibility(View.GONE);
					holder.vMe.setVisibility(View.VISIBLE);
					
					/// save paddings
					int paddingLeft = holder.tvMe.getPaddingLeft() ;
					int paddingRight = holder.tvMe.getPaddingRight() ;
					int paddingBottom = holder.tvMe.getPaddingBottom() ;
					int paddingTop = holder.tvMe.getPaddingTop();
					
					String pictureUrl = me.getPhotoPath();
					if(pictureUrl != null && !pictureUrl.equals(""))
						PhotoProvider.getInstance().displayProfilePicture(me.getPhotoPath(), holder.ivMe);
					else{
						Drawable dr = me.getPlaceHolderDrawable();
						holder.ivMe.setImageDrawable(dr);
					}
					
					String content = event.getContentValue() ;
					if(content != null)
						holder.tvMe.setText(content);
					
					String peerFirstName = peers.getFirstName().split(" ")[0] ;
					holder.tvMe.setBackgroundResource(R.drawable.bg_buble_green_right);
					holder.tvPendingMe.setText(event.getDisplayDate());
					
					holder.tvMe.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
						
				}else{
					holder.vPeer.setVisibility(View.VISIBLE);
					holder.vMe.setVisibility(View.GONE);
					PhotoProvider.getInstance().displayProfilePicture(peers.getPhotoPath(), holder.ivPeer);
					
					// save original padding before seting a 9Pach background
					int paddingLeft = holder.tvPeer.getPaddingLeft() ;
					int paddingRight = holder.tvPeer.getPaddingRight() ;
					int paddingBottom = holder.tvPeer.getPaddingBottom() ;
					int paddingTop = holder.tvPeer.getPaddingTop();
					
					String content = event.getContentValue() ;
					if(content != null)
						holder.tvPeer.setText(content);
 
					holder.tvPeer.setBackgroundResource(R.drawable.bg_buble_gray_left);
					holder.tvPendingPeer.setText(event.getDisplayDate());
					holder.tvPeer.setBackgroundResource(R.drawable.bg_buble_orange_left);
					
					holder.tvPeer.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
				}
			}catch(Exception e){}
			
			return convertView;
		}
		
		
		class Holder {
			View vMe,vPeer, vinfo, vUnreadMsgsContainer ;
			ImageView ivPeer, ivMe, ivCheckMe, ivCheckPeer;
			TextView tvMe, tvPeer, tvInfo , tvPendingMe, tvPendingPeer, tvUnreadMsgsCount;
		}
	}		
}
