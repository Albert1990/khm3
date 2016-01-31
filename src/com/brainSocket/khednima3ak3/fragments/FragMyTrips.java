package com.brainSocket.khednima3ak3.fragments;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.media.tv.TvTrackInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brainSocket.khednima3ak3.AddTripActivity;
import com.brainSocket.khednima3ak3.DialogAlert;
import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.AppBaseActivity;
import com.brainSocket.khednima3ak3.HomeCallbacks;
import com.brainSocket.khednima3ak3.KhedniApp;
import com.brainSocket.khednima3ak3.data.DataStore;
import com.brainSocket.khednima3ak3.data.PhotoProvider;
import com.brainSocket.khednima3ak3.data.ServerResult;
import com.brainSocket.khednima3ak3.data.DataStore.DataRequestCallback;
import com.brainSocket.khednima3ak3.model.AppArea;
import com.brainSocket.khednima3ak3.model.AppTrip;
import com.brainSocket.khednima3ak3.model.AppUser;

public class FragMyTrips extends Fragment {

	ListView lvTasks ;
	ArrayList<AppTrip> events ;
	View vNoTasksContainer ;
	SwipeRefreshLayout srSwipeToRefresh ;
	TextView tvEmptyMsg, tvEmptyHint;
	
	
	HomeCallbacks homeCallback ;
	TodoTasksAdapter adapter ;
	DialogAlert alertDialog;
	
	DataRequestCallback eventsCallback = new DataRequestCallback() {
		@Override 
		public void onDataReady(ServerResult data, boolean success) {
			homeCallback.showProgress(false, R.string.loading_loading);
			srSwipeToRefresh.setRefreshing(false);
			if(success){
				events = (ArrayList) data.getValue("events");
				//Collections.reverse(events);
				//adapter.updateData(events);
				onEventsReceived(events);
			}else{
				vNoTasksContainer.setVisibility(View.VISIBLE);
				tvEmptyMsg.setText(R.string.my_trips_no_trips);
				tvEmptyHint.setText(R.string.my_trips_no_trips_desc);
				showError(R.string.my_trips_failed_to_update_trips);
			}
		}
	};
	
	OnRefreshListener onRefreshRequestedListner = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			DataStore.getInstance().requestUndoneTasks(eventsCallback);
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_todo, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		init();
		requestUndoneTasks();
	}
	
	private void init(){
		lvTasks = (ListView) getView().findViewById(R.id.lvTasks);
		vNoTasksContainer = getView().findViewById(R.id.vNoToDoTasks);
		srSwipeToRefresh = (SwipeRefreshLayout) getView().findViewById(R.id.srSwipeToRefreshLayout);
		tvEmptyHint = (TextView) getView().findViewById(R.id.tvEmptyHint);
		tvEmptyMsg = (TextView) getView().findViewById(R.id.tvEmptyMsg);
		
		homeCallback = (HomeCallbacks) getActivity() ;
		adapter = new TodoTasksAdapter(null);
		lvTasks.setAdapter(adapter);
		srSwipeToRefresh.setOnRefreshListener(onRefreshRequestedListner);
	}
	
	private void requestUndoneTasks(){
		DataStore.getInstance().requestUndoneTasks(eventsCallback);
		homeCallback.showProgress(true, R.string.loading_loading);
//		if(sessionId != null){
//			ArrayList<AppEvent> events = DataStore.getInstance().getSessionEvents(sessionId);
//			if(events != null && !events.isEmpty())
//				onEventsReceived(events);
//			DataStore.getInstance().requestSessionEvents( sessionId,eventsCallback );
//			showloading(true);
//		}
	}

	private void onEventsReceived(ArrayList<AppTrip> eventsRecieved){
		if(eventsRecieved != null){
			events = eventsRecieved;
			//Collections.sort(events);
			adapter.updateData(events);
		}
	}
	
	private void showError(int msg){
		if(isAdded())
			Toast.makeText(KhedniApp.getAppContext(), msg, Toast.LENGTH_SHORT).show();
	}
	
	private class TodoTasksAdapter extends BaseAdapter implements OnClickListener{

		LayoutInflater inflater ;
		ArrayList<AppTrip> events ;
		//ArrayList<TrackInfo> fullTracksList ;	
		
		public void updateData(ArrayList<AppTrip> events){
			if(events == null){
				this.events = new ArrayList<AppTrip> () ;
			}else {
				this.events= events ;
			}			
			if(events.isEmpty()){
				vNoTasksContainer.setVisibility(View.VISIBLE);
				tvEmptyMsg.setText(R.string.my_trips_no_trips);
				tvEmptyHint.setText(R.string.my_trips_no_trips_desc);
			}else{
				vNoTasksContainer.setVisibility(View.GONE);
			}
			notifyDataSetChanged() ;
		}

		
		public TodoTasksAdapter (ArrayList<AppTrip> cons){
			this.inflater = (LayoutInflater)getActivity().getSystemService(AppBaseActivity.LAYOUT_INFLATER_SERVICE);
			if(cons != null)
				this.events = cons ;
			else
				this.events= new ArrayList<AppTrip>(); 
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
		
//		private OnCheckedChangeListener isFullCheckboxListener=new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if(isChecked)
//				{
//					markTripAsFull(buttonView);
//				}
//			}
//		};

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			Holder holder ;
			 if (convertView == null) {
		            holder = new Holder();
		            convertView = inflater.inflate(R.layout.row_todo, parent, false);
		            holder.tvOrigin = (TextView)convertView.findViewById(R.id.tvOirgin);
		            holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
		            holder.tvDays = (TextView) convertView.findViewById(R.id.tvDays);
		            holder.tvDest = (TextView) convertView.findViewById(R.id.tvDest);
		            holder.tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
		            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPhoto);
		            holder.tvDeleteTrip= convertView.findViewById(R.id.tvDeleteTrip);
		            holder.tvDeleteTrip.setOnClickListener(this);
		            holder.cbFull=(CheckBox) convertView.findViewById(R.id.cbFull);
		            //holder.cbFull.setOnCheckedChangeListener(isFullCheckboxListener);
		            holder.cbFull.setOnClickListener(this);
		            convertView.setTag(holder);
		        }else{
				holder = (Holder) convertView.getTag() ;
			}
			try{
				holder.tvDeleteTrip.setTag(position);
				holder.cbFull.setTag(position);
				
				AppTrip trip = events.get(position);
				AppUser peer = trip.getUser();
				PhotoProvider.getInstance().displayProfilePicture(peer.getPhotoPath(), holder.ivPicture);						
				
				AppArea orig = DataStore.getInstance().getAreaById(trip.getOriginId());
				AppArea dest = DataStore.getInstance().getAreaById(trip.getDestId());
						
				if(orig != null)
					holder.tvOrigin.setText(orig.getName());
				if(dest != null)
					holder.tvDest.setText(dest.getName());
				
				if(trip.isReccuring())
					holder.tvDays.setText(getString(R.string.my_trips_days_prefix) + " " +trip.getweekDaysString(getResources()));					
				else
					holder.tvDays.setText(getString(R.string.my_trips_date_prefix) + " " + trip.getDate());

				holder.tvDate.setText(trip.getTime());
				holder.tvStatus.setText(trip.getPrice() +"\n"+ getString(R.string.my_trips_price_suffix));
				if(trip.isFull())
					holder.cbFull.setChecked(true);
				else
					holder.cbFull.setChecked(false);
				

			}catch(Exception e){}
			
			return convertView;
		}
		
		
		class Holder {
			ImageView ivPicture;
			TextView tvOrigin, tvStatus, tvDest , tvDate, tvDays;
			View tvDeleteTrip;
			CheckBox cbFull;
		}
		
		private DataRequestCallback deleteTripCallback=new DataRequestCallback() {
			
			@Override
			public void onDataReady(ServerResult data, boolean success) {
				homeCallback.showProgress(false, R.string.loading_loading);
				if(success)
					requestUndoneTasks();
				else
					homeCallback.showToast(getString(R.string.frag_my_trips_delete_trip_error));
			}
		};
		
		private void deleteTrip(View v)
		{
			int tripIndex=(Integer)v.getTag();
			String tripId=this.events.get(tripIndex).getId();
			homeCallback.showProgress(true, R.string.loading_loading);
			DataStore.getInstance().attemptDeleteTrip(tripId,deleteTripCallback);
		}
		
		private void markTripAsFull(View v)
		{
			int tripIndex=(Integer)v.getTag();
			AppTrip trip=this.events.get(tripIndex);
			
			homeCallback.showProgress(true, R.string.loading_loading);
			DataStore.getInstance().attemptMarkTripAsFull(trip.getId(),!trip.isFull(),markTripAsFullCallback);
		}
		
		private DataRequestCallback markTripAsFullCallback=new DataRequestCallback() {
			
			@Override
			public void onDataReady(ServerResult data, boolean success) {
				homeCallback.showProgress(false, R.string.loading_loading);
				if(success)
				{
					boolean newIsFullState=(Boolean)data.getValue("isFull");
					String msg="";
					if(newIsFullState)
						alertDialog=new DialogAlert(getActivity(), R.string.diag_msg_warning, getString(R.string.frag_my_trips_is_full_activated), R.string.diag_msg_ok, warningAlertDialogClickListener);
					else
						alertDialog=new DialogAlert(getActivity(), R.string.diag_msg_warning, getString(R.string.frag_my_trips_is_full_deactivated), R.string.diag_msg_ok, warningAlertDialogClickListener);
					alertDialog.show();
					requestUndoneTasks();
				}
				else
					homeCallback.showToast(getString(R.string.frag_my_trips_delete_trip_error));
			}
			
			private OnClickListener warningAlertDialogClickListener = new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.dismiss();
				}
			};
		};


		@Override
		public void onClick(View v) {
			int viewId=v.getId();
			switch(viewId)
			{
			case R.id.tvDeleteTrip:
				deleteTrip(v);
				break;
			case R.id.cbFull:
				markTripAsFull(v);
				break;
			}
		}
	}
	
}
