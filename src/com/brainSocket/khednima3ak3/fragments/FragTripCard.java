package com.brainSocket.khednima3ak3.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brainSocket.khednima3ak3.HomeCallbacks;
import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.ConversationActivity;
import com.brainSocket.khednima3ak3.MainActivity;
import com.brainSocket.khednima3ak3.data.DataStore;
import com.brainSocket.khednima3ak3.data.PhotoProvider;
import com.brainSocket.khednima3ak3.data.ServerAccess;
import com.brainSocket.khednima3ak3.data.ServerResult;
import com.brainSocket.khednima3ak3.data.DataStore.DataRequestCallback;
import com.brainSocket.khednima3ak3.model.AppArea;
import com.brainSocket.khednima3ak3.model.AppTrip;
import com.brainSocket.khednima3ak3.model.AppUser;

public class FragTripCard extends Fragment implements OnClickListener {
	private View btnChat, btnCall, btnSendRequest;
	// Store instance variables
	private int pageIndex;
	private AppTrip trip;
	private HomeCallbacks homeCallbacks;
	UserCardActionsCallback homeListener;
	
	TextView tvName, tvJob, tvOrig, tvDest, tvPrice, tvDate, tvTime,tvFull;
	ImageView ivUser;
	View btnsContainer;

	// newInstance constructor for creating fragment with arguments
	public static FragTripCard newInstance(int pageIndex, AppTrip game) {
		FragTripCard fragmentFirst = new FragTripCard();
		Bundle args = new Bundle();
		args.putString("user", game.getJsonString());
		args.putInt("pageNum", pageIndex);
		fragmentFirst.setArguments(args);
		return fragmentFirst;
	}

	// Store instance variables based on arguments passed
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pageIndex = getArguments().getInt("pageNum", 0);
		try {
			String gameStr = getArguments().getString("user");
			trip = new AppTrip(new JSONObject(gameStr));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// Inflate the view for the fragment based on layout XML
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_trip_element, container, false);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			homeListener = (UserCardActionsCallback) activity;
		} catch (Exception e) {
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
		
		if (trip != null) {
			updateFrag(trip);
		}
	}

	private void init() {
		homeCallbacks=(HomeCallbacks)getActivity();
		tvName = (TextView) getView().findViewById(R.id.tvName);
		tvPrice = (TextView) getView().findViewById(R.id.tvPrice);
		tvOrig = (TextView) getView().findViewById(R.id.tvOirgin);
		tvDest = (TextView) getView().findViewById(R.id.tvDest);
		tvJob = (TextView) getView().findViewById(R.id.tvJob);
		ivUser = (ImageView) getView().findViewById(R.id.ivUser);
		tvDate = (TextView) getView().findViewById(R.id.tvDate);
		tvTime = (TextView) getView().findViewById(R.id.tvTime);
		btnCall = getView().findViewById(R.id.btnCall);
		btnChat = getView().findViewById(R.id.btnChat);
		btnSendRequest = getView().findViewById(R.id.btnSendReq);
		btnsContainer = getView().findViewById(R.id.btnsContainer);
		tvFull=(TextView) getView().findViewById(R.id.tvFull);
		
		
		getView().setOnClickListener(this);
		btnChat.setOnClickListener(this);
		btnCall.setOnClickListener(this);
		btnSendRequest.setOnClickListener(this);
	}

	public void updateFrag(AppTrip trip) {
		if (trip != null) {
			this.trip = trip;
			try{
				AppUser driver = trip.getUser();
				String meId = DataStore.getInstance().getMe().getId();
				AppArea dest = DataStore.getInstance().getAreaById(trip.getDestId());
				AppArea orign = DataStore.getInstance().getAreaById(trip.getOriginId()); 
				boolean isMe = meId.equals(driver.getId());
				if(isMe)
					tvName.setText(R.string.me);
				else
					tvName.setText(driver.getFirstName());
				
				//init
				btnsContainer.setAlpha(1f);
				btnCall.setEnabled(true);
				btnChat.setEnabled(true);
				btnSendRequest.setEnabled(true);
				tvFull.setVisibility(View.GONE);
				//end init
				
				
				if(trip.isFull())
				{
					tvFull.setVisibility(View.VISIBLE);
					btnSendRequest.setEnabled(false);
				}
				tvPrice.setText(trip.getPrice() +"\n"+ getString(R.string.main_trip_card_price_suffix));
				tvJob.setText(driver.getJob());
				tvTime.setText(getString(R.string.main_trip_card_time_prefix) +" "+ trip.getTime());
				if(orign != null)
					tvOrig.setText(getString(R.string.frag_trip_card_from)+orign.getName());
				if(dest != null)
					tvDest.setText(getString(R.string.frag_trip_card_to)+dest.getName());
				
				String date = "";
				if(trip.isReccuring()){
					 date = getString(R.string.main_trip_card_days_prefix) +" "+ trip.getweekDaysString(getResources());
				}else{
					date = getString(R.string.main_trip_card_date_prefix) +" "+ trip.getDate();
				}
				tvDate.setText(date);
				if(isMe){
					btnsContainer.setAlpha(0.4f);
					btnCall.setEnabled(false);
					btnChat.setEnabled(false);
					btnSendRequest.setEnabled(false);
				}else{
					btnsContainer.setAlpha(1f);
					btnCall.setEnabled(true);
					btnChat.setEnabled(true);
					btnSendRequest.setEnabled(true);
				}
				if(trip.isMobileNumberEnabled())
					btnCall.setVisibility(View.VISIBLE);
				else
					btnCall.setVisibility(View.GONE);
				if(trip.HasBookedByUserBefore())
				{
					btnSendRequest.setAlpha(0.4f);
					btnSendRequest.setEnabled(false);
				}
				PhotoProvider.getInstance().displayProfilePicture(driver.getPhotoPath(), ivUser);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}

	public int getPageIndex() {
		return pageIndex;
	}
	
	private void openChat(){
		Intent i = new Intent(getActivity(), ConversationActivity.class);
		i.putExtra("contact", trip.getUser().getJsonString());
		startActivity(i);
	}
	
//	private void sendRideRequest(){
//		try{
//			((MainActivity) getActivity()).sendRideRequest(trip);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	public void sendRideRequest(){
		try{
			homeCallbacks.showProgress(true, R.string.loading_loading);
			//showProgress(true, R.string.loading_loading);
			//AppTripsSet tripsSet = DataStore.getInstance().getSearchTripSet();
			
			AppArea orig = DataStore.getInstance().getAreaById(trip.getOriginId());
			AppArea dest = DataStore.getInstance().getAreaById(trip.getDestId());
			String destId = "-1";
			String origId = "-1";
			if(orig != null)
				origId = orig.getId();
			if(dest != null)
				destId = dest.getId();
			
			//if(tripsSet != null){
				destId = trip.getDestId();//tripsSet.getToAreaSet();
				origId = trip.getOriginId();//tripsSet.getFromAreaSet();
			//}
				
			DataStore.getInstance().sendRideRequest(origId, destId, trip.getId(), callbackRequestRide);
//			Intent i = new Intent(this, ConversationActivity.class);
//			i.putExtra("contact", trip.getUser().getJsonString());
//			startActivity(i);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	DataRequestCallback callbackRequestRide = new DataRequestCallback() {
		@Override
		public void onDataReady(ServerResult data, boolean success) {
			homeCallbacks.showProgress(false, 0);
			if(success){
				if(data.getFlag().equals("0"))
				{
					//btnSendRequest.setVisibility(View.GONE);
					btnSendRequest.setAlpha(0.4f);
					btnSendRequest.setEnabled(false);
					homeCallbacks.showToast(getString(R.string.main_ride_request_sent_successfuly));
					
				}
				else if(data.getFlag().equals(ServerAccess.ERROR_CODE_BOOKKED_BEFORE))
					homeCallbacks.showToast(getString(R.string.main_ride_request_sent_before));
				
			}else{
				homeCallbacks.showToast(getString(R.string.error_connection_failed));
			}
		}
	};
	
	

	private void call(){
		String mobileNumber=trip.getUser().getMobileNumber();
		Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ mobileNumber));
		startActivity(i);
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btnCall:
			call();
			break;
		case R.id.btnChat:
			openChat();
			break;
		case R.id.btnSendReq:
			sendRideRequest();
			break;
		}
	}
	
	public interface UserCardActionsCallback{
		public void onChatClick(String UserId);
		
	}
}
