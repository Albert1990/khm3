package com.brainSocket.khednima3ak3;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.data.DataStore;
import com.brainSocket.khednima3ak3.data.FacebookProvider;
import com.brainSocket.khednima3ak3.data.GCMHandler;
import com.brainSocket.khednima3ak3.data.PhotoProvider;
import com.brainSocket.khednima3ak3.data.ServerAccess;
import com.brainSocket.khednima3ak3.data.ServerResult;
import com.brainSocket.khednima3ak3.data.ShareMgr;
import com.brainSocket.khednima3ak3.data.DataStore.APP_VERSION_STATUS;
import com.brainSocket.khednima3ak3.data.DataStore.DataRequestCallback;
import com.brainSocket.khednima3ak3.data.DataStore.DataStoreUpdatListener;
import com.brainSocket.khednima3ak3.fragments.FragAbout;
import com.brainSocket.khednima3ak3.fragments.FragConversationsList;
import com.brainSocket.khednima3ak3.fragments.FragMain;
import com.brainSocket.khednima3ak3.fragments.FragMyTrips;
import com.brainSocket.khednima3ak3.fragments.FragNotifications;
import com.brainSocket.khednima3ak3.fragments.FragUserProfile;
import com.brainSocket.khednima3ak3.model.AppArea;
import com.brainSocket.khednima3ak3.model.AppConversation;
import com.brainSocket.khednima3ak3.model.AppTrip;
import com.brainSocket.khednima3ak3.model.AppTripsSet;
import com.brainSocket.khednima3ak3.model.AppUser;
import com.brainSocket.khednima3ak3.views.TextViewCustomFont;

public class MainActivity extends AppBaseActivity implements OnClickListener, HomeCallbacks, OnBackStackChangedListener, DataStoreUpdatListener{
		
	enum FRAG_TYPE {MAIN, MY_TRIPS, CHATS, SETTINGS, NOTIFICATIONS, ABOUT,MY_PROFILE} ;
	public static int REQUEST_CODE_CREATE_TRIP = 34;
	
	Fragment fragment = null;
 	FragmentManager fragmentManager;
 	FRAG_TYPE currentFrag ;
 	ListView lvDrawer ;
 	TextView tvUserName;
 	ImageView ivUserPicture ;
 	DrawerAdapter adapter ;
 	View btnLogout, btnFeedback, btnShareApp ;
 	ProgressBar spinner ;

 	LinearLayout llLoading ;
 	TextViewCustomFont tvLoadingMsg ;
 	//used for the VideoRec Fragment
 	
 	ImageView ivMenu,ivLogo;
 	ImageView actionMessages, actionNotifications;
 	DrawerLayout dlDrawer ;
 	TextViewCustomFont tvFragTitle ;
 	TextView tvNotificationMsg ;
 	
 	/// Temp dataHolders 	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		initCustomActionBar() ;
		switchSection(FRAG_TYPE.MAIN);
		getSupportFragmentManager().addOnBackStackChangedListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		DataStore.getInstance().triggerDataStoreUpdate(false);
		String gcmId = GCMHandler.getStoredRegistrationId(this);
		if(gcmId == null || gcmId.isEmpty() || GCMHandler.getStoredIsGcmIdSetOnServer(this)){
			DataStore.getInstance().requestGCMRegsitrationId();
		}
		if(DataStore.getInstance().getVersionStatus() == APP_VERSION_STATUS.VERSION_INVALID){
			Intent i = new Intent(this,SplashScreen.class);
			startActivity(i);
			finish();
		}
		tvNotificationMsg.setVisibility(View.GONE);
//		App_ACCESS_MODE mode = DataStore.getInstance().getAccessMode() ;
//		if(mode == App_ACCESS_MODE.TRIAL_MODE){
//			tvNotificationMsg.setVisibility(View.VISIBLE);
//			tvNotificationMsg.setText(R.string.warn_you_are_in_trial);
//		}else{
//			tvNotificationMsg.setVisibility(View.GONE);
//		}
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	protected void onStart() {
		super.onStart();
		//DataStore.getInstance().startScheduledUpdates();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataStore.getInstance().stopScheduledUpdates();
	}
	private void init(){
		lvDrawer = (ListView) findViewById(R.id.lvDrawer);
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		ivUserPicture = (ImageView) findViewById(R.id.ivUserPictue);
		btnLogout = findViewById(R.id.llLogout);
		btnFeedback = findViewById(R.id.llFeedback);
		btnShareApp = findViewById(R.id.llShareApp);
		
		adapter = new DrawerAdapter(this, lvDrawer);
		lvDrawer.setAdapter(adapter);
		spinner = (ProgressBar) findViewById(R.id.spinner);
		dlDrawer = (DrawerLayout) findViewById(R.id.dlDrawer);
		llLoading = (LinearLayout) findViewById(R.id.llLoading);
		tvLoadingMsg = (TextViewCustomFont) findViewById(R.id.tvLoadingMsg);
		tvNotificationMsg = (TextView) findViewById(R.id.tvNotificationMsg);
		
		fragmentManager= getSupportFragmentManager();
		
		AppUser me = DataStore.getInstance().getMe() ;
		if(me != null){
			tvUserName.setText(me.getFirstName());
			PhotoProvider.getInstance().displayProfilePicture(me.getPhotoPath(), ivUserPicture);
		}
		
		btnLogout.setOnClickListener(this);
		btnFeedback.setOnClickListener(this);
		tvNotificationMsg.setOnClickListener(this);
		btnShareApp.setOnClickListener(this);
		
		tvNotificationMsg.setVisibility(View.GONE);
		showProgress(false, R.string.loading_loading);
		//getActionBar().setBackgroundDrawable(new ColorDrawable(R.color.orange_app_theme));
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
		
		tvFragTitle = (TextViewCustomFont) mCustomView.findViewById(R.id.tvFragTitle) ;
		ivMenu = (ImageView) mCustomView.findViewById(R.id.ivMenu);
		ivLogo = (ImageView) mCustomView.findViewById(R.id.ivLogo);
		actionMessages = (ImageView) mCustomView.findViewById(R.id.actionChat);
		actionNotifications = (ImageView) mCustomView.findViewById(R.id.actionNotifications);
		
		ivMenu.setOnClickListener(this);
		actionMessages.setOnClickListener(this);
		actionNotifications.setOnClickListener(this);
	}

	/**
	 * update content of the actionBar accourding to the current fragment
	 * @param section
	 */
	private void updateActionbar(FRAG_TYPE section) {
		switch(section) {
		case MAIN:
			tvFragTitle.setVisibility(View.GONE);
			ivMenu.setVisibility(View.VISIBLE);
			ivLogo.setVisibility(View.VISIBLE);
			actionMessages.setVisibility(View.VISIBLE);
			actionNotifications.setVisibility(View.VISIBLE);
			break;
		case CHATS:
			tvFragTitle.setVisibility(View.VISIBLE);
			ivMenu.setVisibility(View.VISIBLE);
			ivLogo.setVisibility(View.GONE);
			actionMessages.setVisibility(View.GONE);
			actionNotifications.setVisibility(View.GONE);
			actionMessages.setImageResource(R.drawable.ic_chat); // reset icon
			break;
		case ABOUT:
			tvFragTitle.setVisibility(View.VISIBLE);
			ivMenu.setVisibility(View.VISIBLE);
			ivLogo.setVisibility(View.VISIBLE);
			actionMessages.setVisibility(View.GONE);
			actionNotifications.setVisibility(View.GONE);
			break;
		case NOTIFICATIONS:
			tvFragTitle.setVisibility(View.GONE);
			ivMenu.setVisibility(View.VISIBLE);
			ivLogo.setVisibility(View.VISIBLE);
			actionMessages.setVisibility(View.GONE);
			actionNotifications.setVisibility(View.GONE);
			actionNotifications.setImageResource(R.drawable.ic_notification); // reset icon
			break;
		case MY_TRIPS:
			tvFragTitle.setVisibility(View.GONE);
			ivMenu.setVisibility(View.VISIBLE);
			ivLogo.setVisibility(View.VISIBLE);
			actionMessages.setVisibility(View.GONE);
			actionNotifications.setVisibility(View.GONE);
			break;
		case SETTINGS:
			tvFragTitle.setVisibility(View.VISIBLE);
			ivMenu.setVisibility(View.VISIBLE);
			ivLogo.setVisibility(View.GONE);
			actionMessages.setVisibility(View.GONE);
			actionNotifications.setVisibility(View.GONE);
			break;
		}
	}
	
	void switchSection(FRAG_TYPE section){
		if(currentFrag == section)
			return ;
		//fragmentManager.popBackStack();
		loadSection(section);	
		updateActionbar(section);
		closeDrawer();			 
	}
	
	void loadSection(FRAG_TYPE section){	
		try {
			switch(section) {		
			case MAIN:
				FragMain fragPickTrack = new FragMain();
				fragment = fragPickTrack ;
				fragmentManager.beginTransaction()
					.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right,R.anim.slide_in_from_right,R.anim.slide_out_to_left)
					//.addToBackStack(section.name())
					.replace(R.id.content_frame, fragment)
					.commit();
				currentFrag = section ;
				break;
			case MY_TRIPS:
				FragMyTrips fragTodo = new FragMyTrips();
				fragment = fragTodo ;
				fragmentManager.beginTransaction()
					.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right,R.anim.slide_in_from_right,R.anim.slide_out_to_left)
					.addToBackStack(section.name())
					.replace(R.id.content_frame, fragment)
					.commit();
				currentFrag = section ;
				break;
			case CHATS:
				fragment = new FragConversationsList() ;
				fragmentManager.beginTransaction()
					.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right,R.anim.slide_in_from_right,R.anim.slide_out_to_left)
					.addToBackStack(section.name())
					.replace(R.id.content_frame, fragment)
					.commit();
				currentFrag = section ;
				break;
			case NOTIFICATIONS:
				fragment = new FragNotifications();
				fragmentManager.beginTransaction()
					.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right,R.anim.slide_in_from_right,R.anim.slide_out_to_left)
					.addToBackStack(section.name())
					.replace(R.id.content_frame, fragment)
					.commit();
				currentFrag = section ;
				break;
			case ABOUT:
				FragAbout fragAbout = new FragAbout();
				fragment = fragAbout ;
				fragmentManager.beginTransaction()
					.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right,R.anim.slide_in_from_right,R.anim.slide_out_to_left)
					.addToBackStack(section.name())
					.replace(R.id.content_frame, fragment)
					.commit();
				currentFrag = section ;
				break;
			case MY_PROFILE:
				FragUserProfile fragUserProfile = new FragUserProfile();
				fragment = fragUserProfile ;
				fragmentManager.beginTransaction()
					.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right,R.anim.slide_in_from_right,R.anim.slide_out_to_left)
					.addToBackStack(section.name())
					.replace(R.id.content_frame, fragment)
					.commit();
				currentFrag = section ;
				break;
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void ContactUs(){
		ShareMgr.getInstance().sendFeedback(this);
	}
	
	private void toggleDrawer(){
		try{
			if(dlDrawer.isDrawerOpen(Gravity.RIGHT)){
				dlDrawer.closeDrawer(Gravity.RIGHT);
			}else{
				dlDrawer.openDrawer(Gravity.RIGHT);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void closeDrawer(){
		try{
			if(dlDrawer.isDrawerOpen(Gravity.RIGHT)){
				dlDrawer.closeDrawer(Gravity.RIGHT);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private  void backToHome(){
		try { 
			//switchSection(FRAG_TYPE.PICK_TRACK);
			//getFragmentManager().popBackStack (FRAG_TYPE.PICK_TRACK.name(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			
            final FragmentManager fm = getSupportFragmentManager();
            while (fm.getBackStackEntryCount() > 0) {
                fm.popBackStackImmediate();
            }
		    
			adapter.onFragmentChange(FRAG_TYPE.MAIN);
			updateActionbar(FRAG_TYPE.MAIN);
			closeDrawer();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onDataStoreUpdate() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onNewNotificationsAvailable() {
		
	}
	
	@Override
	public void onNewMessagesAvailable() {
		actionMessages.setVisibility(View.VISIBLE);
		actionMessages.setImageResource(R.drawable.ic_chart_active);
	}

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_CREATE_TRIP){
			Dialog diag = new DialogShareOnFacebook(this, R.string.share_diag_after_create_rtip_msg, R.string.share_diag_btn_ok, R.string.share_diag_btn_ok, null);
			diag.show();
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId() ;
		switch (id) {
		case R.id.ivMenu:
			toggleDrawer();
			break;
		case R.id.llLogout:
			DataStore.getInstance().logoutUser();
			Intent i = new Intent(this, SplashScreen.class);
			startActivity(i);
			finish();
			break;
		case R.id.llFeedback:
			ContactUs() ;
		break;
		case R.id.llShareApp:
			FacebookProvider.getInstance().sendInvitations(this, "title");
		break;
		case R.id.tvNotificationMsg:
			openVerifActivity();
		break;
		case R.id.actionNotifications:
			switchSection(FRAG_TYPE.NOTIFICATIONS);
		break;
		case R.id.actionChat:
			switchSection(FRAG_TYPE.CHATS);
		break;
//		case R.id.btnGroup:
//			onCreateGroupRequested();
//		break;
		}
	}
	
	public class DrawerAdapter extends BaseAdapter implements OnItemClickListener{
		
		  private DrawerElement [] elements  = {
			  new DrawerElement(R.string.drawer_main, R.drawable.ic_home, R.drawable.ic_home_active,DRAWER_ITEM_TYPE.MAIN),
			  new DrawerElement(R.string.drawer_my_trips, R.drawable.ic_todo, R.drawable.ic_todo_active, DRAWER_ITEM_TYPE.MY_TRIPS) ,
			  new DrawerElement(R.string.drawer_chats, R.drawable.ic_chart, R.drawable.ic_chart_active, DRAWER_ITEM_TYPE.CHATS) ,
			  new DrawerElement(R.string.drawer_notifications, R.drawable.ic_chart, R.drawable.ic_chart_active, DRAWER_ITEM_TYPE.NOTIFICATIONS) ,
			  new DrawerElement(R.string.drawer_about, R.drawable.ic_info, R.drawable.ic_info_active, DRAWER_ITEM_TYPE.ABOUT) ,
			  new DrawerElement(R.string.drawer_my_profile, R.drawable.ic_home, R.drawable.ic_home_active,DRAWER_ITEM_TYPE.MY_PROFILE)
			  };
		
		  protected final Context context;
		  protected Boolean selectable ;
		  protected List<Integer> selected ; 
		  protected ListView list ;
		  int selectedItemIndex = 0 ;
		  
		  public void setSelectedItemIndex(int selectedItemIndex) {
			this.selectedItemIndex = selectedItemIndex;
			notifyDataSetChanged() ;
		}
		  
		  public DrawerAdapter(Context context, ListView view ) {
		    super();
		    this.context = context;
		    
		    list = view ;
		    list.setOnItemClickListener(this);
		  }

		  public void onFragmentChange(FRAG_TYPE fragType){
			  switch (fragType) {
			case MAIN:
				selectedItemIndex = 0 ;
				break;
			case MY_TRIPS:
				selectedItemIndex = 1 ;
				break;
			case CHATS:
				selectedItemIndex = 2 ;
				break;
			case NOTIFICATIONS:
				selectedItemIndex = 3 ;
				break;
			case ABOUT:
				selectedItemIndex = 4 ;
				break;
			case MY_PROFILE:
				selectedItemIndex=5;
				break;
			default:
				selectedItemIndex = -1 ;
				break;
			}
			  notifyDataSetChanged();
		  }
		 
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
			  
			View rowView ;
			if(convertView == null){  
			  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			  rowView = inflater.inflate(R.layout.row_drawer, parent, false);
			}else{
			  rowView = convertView ;
			}
			String title = context.getString(elements[position].stringId);
			int imRes  = elements[position].iconId;
			int color = Color.WHITE;
			
			if(selectedItemIndex == position){
				imRes  = elements[position].activeIconId;
				color = getResources().getColor(R.color.app_theme_orange);
			}
			
		    TextView txt  = (TextView) rowView.findViewById(R.id.title);
		    ImageView icon = (ImageView) rowView.findViewById(R.id.drawable_icon);
		    
		    txt.setText(title);
		    txt.setTextColor(color);
		    icon.setImageResource(imRes);
		    
		    return rowView;
		  }
		  
		@Override
		public int getCount() {
			return elements.length;
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
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			setSelectedItemIndex(arg2);
			DrawerElement elem = elements[arg2];
			switch (elem.itemType) {
			case MAIN:
				backToHome();
				break;
			case MY_TRIPS:
				switchSection(FRAG_TYPE.MY_TRIPS);
				break;
			case ABOUT:
				switchSection(FRAG_TYPE.ABOUT);
				break;
			case NOTIFICATIONS:
				switchSection(FRAG_TYPE.NOTIFICATIONS);
				break;
			case CHATS:
				switchSection(FRAG_TYPE.CHATS);
				break;
			case MY_PROFILE:
				switchSection(FRAG_TYPE.MY_PROFILE);
				break;
			}
		
		}	  
	}
	public static enum DRAWER_ITEM_TYPE{MAIN, MY_TRIPS, SETTINGS, CHATS, NOTIFICATIONS, ABOUT,MY_PROFILE}
	static class DrawerElement{
		int stringId ;
		int iconId ;
		int activeIconId ;
		DRAWER_ITEM_TYPE itemType ;
		
		public DrawerElement(int str , int ico, int activeIcon ,DRAWER_ITEM_TYPE type ){
			stringId = str ;
			iconId = ico ;
			activeIconId  = activeIcon;
			itemType = type ;
		}
	}


	
	@Override
	public void onBackStackChanged() {
		int  entrys = getSupportFragmentManager().getBackStackEntryCount() ;
		
		try {
			String entryName ;
			if(entrys == 0)
				entryName = FRAG_TYPE.MAIN.name();
			else
				entryName = getSupportFragmentManager().getBackStackEntryAt(entrys-1).getName() ;
			FRAG_TYPE fragType = FRAG_TYPE.valueOf(entryName) ;
			if( fragType != null ){
				currentFrag = fragType;
				updateActionbar(fragType);
				adapter.onFragmentChange(fragType) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setTitle(String title) {
		if(title != null && tvFragTitle != null){
			tvFragTitle.setText(title);
		}
	}

	@Override
	public void showProgress(boolean show,int msg) {
		
		if(show){
			llLoading.setVisibility(View.VISIBLE);
			if(msg <=0){
				tvLoadingMsg.setVisibility(View.GONE);
			}else{
				tvLoadingMsg.setText(msg);
				tvLoadingMsg.setVisibility(View.VISIBLE);
			}
		}else{
			llLoading.setVisibility(View.GONE);
		}
	}

	private void openVerifActivity(){
		Intent i = new Intent(this, VerificationActivity.class);
		startActivity(i);
	}
	
//	private void onCreateGroupRequested () {
//		TrackingMgr.getInstance().sendTrackingEvent(this, TRACKING_TYPE.REQUEST_CREATE_GROUP, "Main", "");
//		DiagMsg diag = new DiagMsg(getString(R.string.diag_soon_create_groups_msg),R.string.diag_soon_create_groups_ok,0,false,true,null);
//		diag.show(getSupportFragmentManager(), null);
//	}

	@Override
	public void showConversation(AppConversation conversation) {
//		if(conversation == null)
//			return ;
//		if(DataStore.getInstance().getAccessMode() != App_ACCESS_MODE.VERIFIED){
//			showToast(getString(R.string.error_action_blocked_in_trial_mode));
//		}
//		
//		Intent i = new Intent(this, ConversationActivity.class);
//		if( conversation.isHasSession() ){
//			i.putExtra("sessionId", conversation.getSession().getIdGlobal());
//		//} else if(conversation.getNetwork() ==  SOCIAL_MEDIA_ACCOUNT_TYPE.SAB3EEN){
//			
//		}else{
//			i.putExtra("contact", conversation.getSession().getContacts().get(0));
//		}
//		startActivity(i);

	}


//	@Override
//	public void sendSocialMediaZeker(AppContact contact) {
//		String shareBody = "";
//		shareBody += getString(R.string.send_social_invitation_part1) + "\n" +
//				"\" " + getString(R.string.send_social_invitation_default_zecker) + "\" \n"+
//				getString(R.string.send_social_invitation_default_count) + "\n"+
//				getString(R.string.send_social_invitation_part2) + " "+ FragAbout.SABEEN_WEB_LINK;
//		
//		String diagTitle = contact.getName() +" " + getString(R.string.send_social_invitation_diag_title_part1) ;  
//		
//	    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//	        sharingIntent.setType("text/plain");
//	        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "���� �����");
//	        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//	        startActivity(Intent.createChooser(sharingIntent,diagTitle));
//	        TrackingMgr.getInstance().sendTrackingEvent(this, TRACKING_TYPE.SOCIAl_MEDIA_INVITATION_SENT, "Main", "");
//	}
	



}
