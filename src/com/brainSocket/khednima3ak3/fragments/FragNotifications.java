package com.brainSocket.khednima3ak3.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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
import com.brainSocket.khednima3ak3.model.AppEvent;
import com.brainSocket.khednima3ak3.model.AppNotificationEvent;
import com.brainSocket.khednima3ak3.model.AppUser;
import com.brainSocket.khednima3ak3.model.AppNotificationEvent.NOTIFICATION_TYPE;

public class FragNotifications extends Fragment {

	ListView lvTasks;
	ArrayList<AppNotificationEvent> events;
	View vNoTasksContainer;
	SwipeRefreshLayout srSwipeToRefresh;
	TextView tvEmptyMsg, tvEmptyHint;

	HomeCallbacks homeCallback;
	TodoTasksAdapter adapter;
	DialogAlert alertDialog;
	AppNotificationEvent tempEvent;

	DataRequestCallback eventsCallback = new DataRequestCallback() {
		@Override
		public void onDataReady(ServerResult data, boolean success) {
			homeCallback.showProgress(false, R.string.loading_loading);
			srSwipeToRefresh.setRefreshing(false);
			if (success) {
				events = (ArrayList) data.getValue("events");
				// Collections.reverse(events);
				// adapter.updateData(events);
				onEventsReceived(events);
			} else {
				vNoTasksContainer.setVisibility(View.VISIBLE);
				tvEmptyMsg.setText(R.string.event_notification_no_trips);
				tvEmptyHint.setText(R.string.event_notification_no_trips_desc);
				showError(R.string.event_notification_failed_to_update_trips);
			}
		}
	};
	
	private void callMobileNumber(String mobileNumber) {
		//String mobileNumber = (String) v.getTag();
		Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber));
		try {
			startActivity(i);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private OnClickListener alertDialogClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.tvBtnRight) {
				String selectedMobileNumber="";
				if (tempEvent.getToUser().getId().equals(DataStore.meId))
				{
					selectedMobileNumber=tempEvent.getFromUser().getMobileNumber();
				}
				else
				{
					selectedMobileNumber=tempEvent.getToUser().getMobileNumber();
				}
				callMobileNumber(selectedMobileNumber);
			}
			alertDialog.dismiss();
		}
	};

	DataRequestCallback callbackRespondToEvent = new DataRequestCallback() {
		@Override
		public void onDataReady(ServerResult data, boolean success) {
			if(success)
			{
				boolean hasAccepted=(Boolean)data.getValue("hasAccepted");
			homeCallback.showProgress(false, R.string.loading_loading);
			srSwipeToRefresh.setRefreshing(false);
			if(hasAccepted)
			{
				String msg="";
				if(tempEvent!=null)
				{
					if(tempEvent.getToUser().getId().equals(DataStore.meId))
					{
						msg=getString(R.string.row_event_user_request3)+" "
								 +tempEvent.getFromUser().getFirstName()+", "
								 +getString(R.string.row_event_contact_him_call);
					}
				}
				alertDialog= new DialogAlert(getActivity(), R.string.diag_msg_what_next, msg,R.string.main_trip_action_call, R.string.share_diag_btn_cancel, alertDialogClickListener);
				alertDialog.show();
			}
			requestUndoneTasks();
			}
		}
	};
	OnRefreshListener onRefreshRequestedListner = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			DataStore.getInstance().requesEventNotifications(eventsCallback);
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

	private void init() {
		lvTasks = (ListView) getView().findViewById(R.id.lvTasks);
		vNoTasksContainer = getView().findViewById(R.id.vNoToDoTasks);
		srSwipeToRefresh = (SwipeRefreshLayout) getView().findViewById(R.id.srSwipeToRefreshLayout);
		tvEmptyHint = (TextView) getView().findViewById(R.id.tvEmptyHint);
		tvEmptyMsg = (TextView) getView().findViewById(R.id.tvEmptyMsg);

		homeCallback = (HomeCallbacks) getActivity();
		adapter = new TodoTasksAdapter(null);
		lvTasks.setAdapter(adapter);
		srSwipeToRefresh.setOnRefreshListener(onRefreshRequestedListner);
	}

	private void requestUndoneTasks() {
		DataStore.getInstance().requesEventNotifications(eventsCallback);
		homeCallback.showProgress(true, R.string.loading_loading);
	}

	private void onEventsReceived(ArrayList<AppNotificationEvent> eventsRecieved) {
		if (eventsRecieved != null) {
			events = eventsRecieved;
			// Collections.sort(events);
			adapter.updateData(events);
		}
	}

	private void showError(int msg) {
		if (isAdded())
			Toast.makeText(KhedniApp.getAppContext(), msg, Toast.LENGTH_SHORT).show();
	}

	private class TodoTasksAdapter extends BaseAdapter implements OnClickListener {

		LayoutInflater inflater;
		ArrayList<AppNotificationEvent> events;

		public void updateData(ArrayList<AppNotificationEvent> events) {
			if (events == null) {
				this.events = new ArrayList<AppNotificationEvent>();
			} else {
				// / removing invalid events from list
				this.events = new ArrayList<AppNotificationEvent>();
				for (AppNotificationEvent appNotificationEvent : events) {
					if (appNotificationEvent.isValidEvent())
						this.events.add(appNotificationEvent);
				}
			}

			if (this.events.isEmpty()) {
				vNoTasksContainer.setVisibility(View.VISIBLE);
				tvEmptyMsg.setText(R.string.event_notification_no_trips);
				tvEmptyHint.setText(R.string.event_notification_no_trips_desc);
			} else {
				vNoTasksContainer.setVisibility(View.GONE);
			}
			notifyDataSetChanged();
		}

		public TodoTasksAdapter(ArrayList<AppNotificationEvent> cons) {
			this.inflater = (LayoutInflater) getActivity().getSystemService(AppBaseActivity.LAYOUT_INFLATER_SERVICE);
			if (cons != null)
				this.events = cons;
			else
				this.events = new ArrayList<AppNotificationEvent>();
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

			Holder holder;
			if (convertView == null) {
				holder = new Holder();
				convertView = inflater.inflate(R.layout.row_event_notification, parent, false);
				holder.tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
				holder.tvFrom = (TextView) convertView.findViewById(R.id.tvFrom);
				holder.tvTo = (TextView) convertView.findViewById(R.id.tvTo);
				holder.tvResult = (TextView) convertView.findViewById(R.id.tvResult);
				holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPhoto);
				holder.llButtons = convertView.findViewById(R.id.llButtons);
				holder.btnAccept = convertView.findViewById(R.id.btnAccept);
				holder.btnAccept.setOnClickListener(this);
				holder.btnReject = convertView.findViewById(R.id.btnReject);
				holder.btnReject.setOnClickListener(this);
				holder.ivCall = convertView.findViewById(R.id.ivCall);
				holder.ivCall.setOnClickListener(this);

				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			try {
				holder.tvResult.setVisibility(View.VISIBLE);
				holder.llButtons.setVisibility(View.VISIBLE);
				holder.ivCall.setVisibility(View.GONE);
				holder.btnAccept.setTag(position);
				holder.btnReject.setTag(position);

				AppNotificationEvent notification = this.events.get(position);
				// AppUser peer = notification.getPeer();
				// String name = peer.getFirstName();

				String origName = "";
				String destName = "";
				SpannableString spannablecontent = null;
				String userPhotoPath = null;
				String userName = null;

				AppArea orig = DataStore.getInstance().getAreaById(notification.getBooking().getTrip().getOriginId());
				AppArea dest = DataStore.getInstance().getAreaById(notification.getBooking().getTrip().getDestId());

				if (orig != null)
					origName = orig.getName();
				if (dest != null)
					destName = dest.getName();

				String msg = null, msg1 = null;
				switch (notification.getType()) {
				case BOOKING_REQUEST:

					if (notification.getToUser().getId().equals(DataStore.meId)) {
						userPhotoPath = notification.getFromUser().getPhotoPath();
						userName = notification.getFromUser().getFirstName();
						msg = getString(R.string.row_event_user_request) + " " + notification.getFromUser().getFirstName() + " " + notification.getFromUser().getLAstName() + " "
								+ getString(R.string.row_event_user_request1) + " " + getString(R.string.row_event_inreturn) + " " + notification.getBooking().getPrice() + " "
								+ getString(R.string.main_trip_card_price_suffix);

						if (notification.getBooking().isHasAnswered()) {
							holder.llButtons.setVisibility(View.GONE);
							if (notification.getBooking().isAccepted()) {
								// msg1=getString(R.string.row_event_user_request2)+" "
								// +notification.getFromUser().getFirstName()+" "
								// +getString(R.string.row_event_is)+" "
								// +KhedniApp.localizeMobileNumber(notification.getFromUser().getMobileNumber())+" "
								// +getString(R.string.row_event_contact_him);
								msg1 = getString(R.string.row_event_user_request2) + " " + getString(R.string.row_event_contact_him);
								holder.ivCall.setTag(notification.getFromUser().getMobileNumber());
								holder.ivCall.setVisibility(View.VISIBLE);
							} else {
								msg1 = getString(R.string.row_event_rejected);
							}
						} else {
							holder.tvResult.setVisibility(View.GONE);
						}
					} else {
						holder.llButtons.setVisibility(View.GONE);
						userPhotoPath = notification.getFromUser().getPhotoPath();
						userName = notification.getToUser().getFirstName();
						msg = getString(R.string.row_event_user_resp) + " " + notification.getToUser().getFirstName() + " " + getString(R.string.row_event_user_resp1) + " "
								+ getString(R.string.row_event_inreturn) + " " + notification.getBooking().getPrice() + " " + getString(R.string.main_trip_card_price_suffix);

						if (notification.getBooking().isHasAnswered()) {
							if (notification.getBooking().isAccepted()) {
								// msg1=getString(R.string.row_event_user_request2)+" "
								// +notification.getToUser().getFirstName()+" "
								// +getString(R.string.row_event_is)+" "
								// +KhedniApp.localizeMobileNumber(notification.getToUser().getMobileNumber())+" "
								// +getString(R.string.row_event_contact_him);
								msg1 = getString(R.string.row_event_user_request2) + " " + getString(R.string.row_event_contact_him);
								holder.ivCall.setTag(notification.getToUser().getMobileNumber());
								holder.ivCall.setVisibility(View.VISIBLE);
							} else {
								msg1 = getString(R.string.row_event_rejected);
							}
						} else {
							msg1 = getString(R.string.row_event_no_answer_yet) + " " + notification.getToUser().getFirstName() + " " + getString(R.string.row_event_your_request);
						}
					}
					// spannablecontent = new SpannableString(msg);
					// spannablecontent.setSpan(new
					// StyleSpan(android.graphics.Typeface.BOLD_ITALIC),
					// msg.length(), msg.length() + userName.length(), 0);
					break;

				default:
					// spannablecontent = new SpannableString(" ");
					break;
				}

				PhotoProvider.getInstance().displayProfilePicture(userPhotoPath, holder.ivPicture);
				holder.tvMsg.setText(msg);
				holder.tvFrom.setText(getString(R.string.row_event_from) + " " + origName);
				holder.tvTo.setText(getString(R.string.row_event_to) + " " + destName);
				holder.tvResult.setText(msg1);
				// String
				// price=Integer.toString(notification.getBooking().getPrice());
				// holder.tvPrice.setText(price);

			} catch (Exception e) {
			}

			return convertView;
		}

		class Holder {
			ImageView ivPicture;
			TextView tvMsg;
			TextView tvFrom;
			TextView tvTo;
			// TextView tvPrice;
			View btnAccept;
			View btnReject;
			View llButtons;
			TextView tvResult;
			View ivCall;
		}

		private void respondToRequest(View v, boolean hasAccept) {
			int index = (Integer) v.getTag();
			AppNotificationEvent event = events.get(index);
			tempEvent=event;
			DataStore.getInstance().respondToRideRequest(event.getObject_id(), hasAccept, callbackRespondToEvent);
		}

		

		@Override
		public void onClick(View v) {
			int viewId = v.getId();
			switch (viewId) {
			case R.id.btnAccept:
				respondToRequest(v, true);
				break;
			case R.id.btnReject:
				respondToRequest(v, false);
				break;
			case R.id.ivCall:
				callMobileNumber((String)v.getTag());
				break;
			}
		}
	}

}
