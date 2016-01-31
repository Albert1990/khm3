package com.brainSocket.khednima3ak3.fragments;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.AddTripActivity;
import com.brainSocket.khednima3ak3.AreaSuggestiosAdapter;
import com.brainSocket.khednima3ak3.HomeCallbacks;
import com.brainSocket.khednima3ak3.KhedniApp;
import com.brainSocket.khednima3ak3.MainActivity;
import com.brainSocket.khednima3ak3.TutorialOverlay.TUTORIAL_TYPE;
import com.brainSocket.khednima3ak3.TutorialOverlay.TutorialCallback;
import com.brainSocket.khednima3ak3.data.DataStore;
import com.brainSocket.khednima3ak3.data.FacebookProvider;
import com.brainSocket.khednima3ak3.data.ServerResult;
import com.brainSocket.khednima3ak3.data.TrackingMgr;
import com.brainSocket.khednima3ak3.data.DataStore.DataRequestCallback;
import com.brainSocket.khednima3ak3.data.DataStore.DataStoreUpdatListener;
import com.brainSocket.khednima3ak3.fragments.FragTripCard.UserCardActionsCallback;
import com.brainSocket.khednima3ak3.helpers.SmartFragmentStatePagerAdapter;
import com.brainSocket.khednima3ak3.model.AppArea;
import com.brainSocket.khednima3ak3.model.AppTrip;
import com.brainSocket.khednima3ak3.model.AppTripsSet;
import com.brainSocket.khednima3ak3.views.CarousellViewPager;
import com.brainSocket.khednima3ak3.views.SearchAutoComplete;

public class FragMain extends Fragment implements OnClickListener, UserCardActionsCallback {

	private final static int FRIST_TUT_DELAY = 2500;
	private boolean canRefresh=false;

	HomeCallbacks homeCallback;

	//View btnStartSelfTask;
	SearchAutoComplete autoCompleteOrigin, autoCompleteDestination;
	private CarousellViewPager gamesPager;
	GamesPagerAdapter gamesPagerAdapter;
	View vNoResultsPlaceHolder, spinner, btnShareOnFacebook;
	
//	TextView tvGameIndex ;
	AppTripsSet tripSet ;

	Handler handler;
	ArrayList<String> suggestions = new ArrayList<String>();

	DataStoreUpdatListener dataUpdateListener = new DataStoreUpdatListener() {
		@Override
		public void onDataStoreUpdate() {
			
		}
		public void onNewNotificationsAvailable() {};
		public void onNewMessagesAvailable() {};
	};

	DataRequestCallback meUpdateCallback = new DataRequestCallback() {
		@Override
		public void onDataReady(ServerResult data, boolean success) {
			if (success) {
				
			}
		}
	};
	
	DataRequestCallback callbackTripSearch = new DataRequestCallback() {
		@Override
		public void onDataReady(ServerResult data, boolean success) {
			spinner.setVisibility(View.GONE);
			canRefresh=true;
			if (success) {
				tripSet = (AppTripsSet) data.getValue("tripSet");
				onDatReceived(tripSet);
				if(tripSet == null || tripSet.getTrips() == null || tripSet.getTrips().isEmpty())
					vNoResultsPlaceHolder.setVisibility(View.VISIBLE);
			}else{
				KhedniApp.showToast(getActivity(), R.string.error_connection_failed);
			}
		}
	};

	TutorialCallback inAppTutCallback = new TutorialCallback() {
		@Override
		public void onTutCanseled(TUTORIAL_TYPE tutType) {
			if (tutType == TUTORIAL_TYPE.PLant) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (isAdded()) {

						}
					}
				}, 500);
			}
		}

		@Override
		public void onHeighlitedViewClicked(TUTORIAL_TYPE tutType) {
			if (tutType == TUTORIAL_TYPE.PLant) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (!isAdded())
							return;

					}
				}, 500);
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
		initAutocomplete();
		DataStore.getInstance().addUpdateBroadcastListener(dataUpdateListener);
		DataStore.getInstance().triggerDataStoreUpdate(false);
		DataStore.getInstance().searchForTrip(null, null, 1, callbackTripSearch);
		onDatReceived(DataStore.getInstance().getAllTripsSet());

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!isAdded())
					return;
				// if (!DataStore.getInstance().isTutPlantDisplayed()
				// && currentSegment == SEGMENT_TYPE.MAIN) {
				// Dialog diagTut = new TutorialOverlay(getActivity(),ivPlant,
				// getString(R.string.tut_plant_title),getString(R.string.tut_plant_msg),TUTORIAL_TYPE.PLant,
				// inAppTutCallback);
				// diagTut.show();
				// DataStore.getInstance().setTutPlantDisplayed(true);
				// }
			}
		}, FRIST_TUT_DELAY);
	}
	
	public void refreshTrips()
	{
		if(canRefresh)
		{
		spinner.setVisibility(View.VISIBLE);
		DataStore.getInstance().searchForTrip(null, null, 1, callbackTripSearch);
		}
	}

	// public void setHomeCallback(HomeCallbacks callback){
	// homeCallback = callback ;
	// }

	private void init() {
		//btnStartSelfTask = getView().findViewById(R.id.btnStartSelfTask);
		gamesPager = (CarousellViewPager) getView().findViewById(R.id.vpCarousel);
//		tvGameIndex = (TextView) getView().findViewById(R.id.tvIndex);
		autoCompleteDestination = (SearchAutoComplete) getView().findViewById(R.id.autocompleteDest);
		autoCompleteOrigin = (SearchAutoComplete) getView().findViewById(R.id.autocompleteOrigin);
		View btnCreateWeekly = getView().findViewById(R.id.btnCreateWeekly);
		View btnCreateOneTrip = getView().findViewById(R.id.btnCreateOneTrip);
		vNoResultsPlaceHolder = getView().findViewById(R.id.vNoResultsPlaceHolder);
		spinner = getView().findViewById(R.id.spinner);
		btnShareOnFacebook = getView().findViewById(R.id.btnShareOnFacebook);

		homeCallback = (HomeCallbacks) getActivity();
		handler = new Handler();

		//btnStartSelfTask.setOnClickListener(this);
		btnCreateOneTrip.setOnClickListener(this);
		btnCreateWeekly.setOnClickListener(this);
		btnShareOnFacebook.setOnClickListener(this);
		
		// init Carousel 
		gamesPagerAdapter = new GamesPagerAdapter(getChildFragmentManager());
		gamesPager.setAdapter(gamesPagerAdapter);
		gamesPager.setClipToPadding(false);
		gamesPager.setOnPageChangeListener(gamesPagerAdapter);
		int dp6 = KhedniApp.dpToPx(6);
		//int padding = (int) (getResources().getDisplayMetrics().widthPixels * 0.07);
		gamesPager.setPageMargin(dp6);
		//gamesPager.setCurrentItem(0);
		
		// initial state
		vNoResultsPlaceHolder.setVisibility(View.GONE);
		spinner.setVisibility(View.GONE);
	}

	private void initAutocomplete() {
		try{
			suggestions = DataStore.getInstance().getAreasNames();
			ArrayAdapter<String> adapterStatic2 = new AreaSuggestiosAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, suggestions);
			autoCompleteDestination.setAdapter(adapterStatic2);
			autoCompleteDestination.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					TextView tv = (TextView) arg1;
					String areaName = (String) tv.getText();
					searchForTrips();
				}
			});
			
			//Origin autoComplete
			ArrayAdapter<String> adapterStatic1 = new AreaSuggestiosAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, suggestions);
			autoCompleteOrigin.setAdapter(adapterStatic1);
			autoCompleteOrigin.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					TextView tv = (TextView) arg1;
					String areaName = (String) tv.getText();
					searchForTrips();
				}
			});
			
			autoCompleteDestination.setHintTextColor(getResources().getColor(R.color.gray_off_white));
			autoCompleteOrigin.setHintTextColor(getResources().getColor(R.color.gray_off_white));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		DataStore.getInstance().getMe(meUpdateCallback);
		refreshTrips();
	}

	@Override
	public void onPause() {
		super.onPause();

	}
	
	public void onDatReceived (AppTripsSet tripsSet){
		try {
			gamesPagerAdapter.updateAdapter(tripsSet.getTrips());
			//TODO set AutoComplete
			//autoCompleteOrigin.setText(tripsSet.get);
		}catch (Exception e) {}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		DataStore.getInstance().removeUpdateBroadcastListener(dataUpdateListener);
	}

	public HomeCallbacks getHomeCallback() {
		if (homeCallback == null)
			homeCallback = (HomeCallbacks) (getActivity());
		return homeCallback;
	}
	
	public void searchForTrips(){
		try {
			spinner.setVisibility(View.VISIBLE);
			String strDest = autoCompleteDestination.getText().toString();
			String strOrigin = autoCompleteOrigin.getText().toString();
			AppArea dest = DataStore.getInstance().getAreaByName(strDest);
			AppArea orign = DataStore.getInstance().getAreaByName(strOrigin);
			
			String desId = null;
			String origId = null;
			
			if(dest != null)
				desId = dest.getId();
			if(orign != null)
				origId = orign.getId();
			
			DataStore.getInstance().searchForTrip(origId, desId, 1, callbackTripSearch);
			if(origId != null)
				TrackingMgr.getInstance().sendTrackingEvent(getActivity(), "SearchForTrip", "Origin", orign.getName());
			if(desId != null)
				TrackingMgr.getInstance().sendTrackingEvent(getActivity(), "SearchForTrip", "Destination", dest.getName());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * 
	 * @param isRequring: defines the input form time that should be displayed wether the trip is repeated weekly or just a one time trip
	 */
	private void openNewTripForm(boolean isRequring){
		try {
			Intent i = new Intent(getActivity(), AddTripActivity.class);
			i.putExtra("isRequring",isRequring);
			startActivityForResult(i, MainActivity.REQUEST_CODE_CREATE_TRIP);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	public void onChatClick(String UserId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btnCreateOneTrip:
			openNewTripForm(false);
			break;
		case R.id.btnCreateWeekly:
			openNewTripForm(true);
			break;
		case R.id.btnShareOnFacebook:
			FacebookProvider.getInstance().sharePhotoViaFacebook(getActivity());
			TrackingMgr.getInstance().sendTrackingEvent(getActivity(), "share", "noTrip", "main");
			break;
		}
	}

	public class GamesPagerAdapter extends SmartFragmentStatePagerAdapter implements OnPageChangeListener{
		
		String userId ;
		ArrayList<AppTrip> arrayGames ;
		int countTotal ; 
		//boolean showPromo ;
		public GamesPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		public void updateAdapter(ArrayList<AppTrip> arrayFinished) {
			try {
//				if(arrayFinished == null || arrayFinished.isEmpty()){
//					tvGameIndex.setVisibility(View.INVISIBLE);
//				}else{
//					tvGameIndex.setVisibility(View.VISIBLE);
//				}
				arrayGames = new ArrayList<AppTrip>();
				arrayGames.addAll(arrayFinished);
				
				countTotal = arrayGames.size();
				
//				if(tvGameIndex != null && countTotal >= 1){
//					String str = arrayGames.size()  +"\\" + (gamesPager.getCurrentItem()+1);
//					FragMain.this.tvGameIndex.setText(str);
//				}
				
				if(countTotal <= 1){
					//gamesPager.setSlidingEnabled(false);
				}else{
					//gamesPager.setSlidingEnabled(true);
					Collections.reverse(arrayGames);
				}
				
				this.notifyDataSetChanged();
				//gamesPager.setCurrentItem(getCount()-1);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		
		/**
		 * used to update fragments after the NotefyDatSteChanged as this type of adapters does not update all pages 
		 * after the dataSetChanged 		
		 * @param object
		 * @return
		 */
		@Override
		public int getItemPosition(Object object) {
			try{
				if(object instanceof FragTripCard){
					FragTripCard frag = (FragTripCard) object ;
					int pos = frag.getPageIndex() ;
					AppTrip game  = arrayGames.get(pos);
					frag.updateFrag(game);
					return pos;
				}else
					return super.getItemPosition(object);
			}catch(Exception e){e.printStackTrace();}
			return POSITION_NONE ;
		}
		
		// Returns total number of pages
		@Override
		public int getCount() {
			return countTotal ;
		}
		
		@Override
		public float getPageWidth(int position) {
			return 0.85f;
			//return 1.0f ;
		}
		// Returns the fragment to display for that page
		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			
			FragTripCard fragCard = FragTripCard.newInstance(position, arrayGames.get(position));
			
			return fragCard ;

		}
		// Returns the page title for the top indicator
		@Override
		public CharSequence getPageTitle(int position) {
			return "Page " + position;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
//			if(tvGameIndex != null){
//				String str = getCount() + " \\ " +  ( gamesPagerAdapter.getCount() - (gamesPager.getCurrentItem() ) ) ;
//				FragMain.this.tvGameIndex.setText(str);
//			}
		}


		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}


		@Override
		public void onPageSelected(int arg0) {
			
		}
	}
	
}
