package com.brainSocket.khednima3ak3.fragments;


import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.AppBaseActivity;
import com.brainSocket.khednima3ak3.ConversationActivity;
import com.brainSocket.khednima3ak3.HomeCallbacks;
import com.brainSocket.khednima3ak3.KhedniApp;
import com.brainSocket.khednima3ak3.data.DataStore;
import com.brainSocket.khednima3ak3.data.PhotoProvider;
import com.brainSocket.khednima3ak3.data.ServerResult;
import com.brainSocket.khednima3ak3.data.DataStore.DataRequestCallback;
import com.brainSocket.khednima3ak3.model.AppConversation;

public class FragConversationsList extends Fragment {

	ListView lvTasks ;
	ArrayList<AppConversation> arrayConversations ;
	View vNoTasksContainer ;
	SwipeRefreshLayout srSwipeToRefresh ;
	TextView tvEmptyMsg, tvEmptyHint;
	
	HomeCallbacks homeCallback ;
	ConversationsAdapter adapter ;
	
	DataRequestCallback eventsCallback = new DataRequestCallback() {
		@Override 
		public void onDataReady(ServerResult data, boolean success) {
			homeCallback.showProgress(false, R.string.loading_loading);
			srSwipeToRefresh.setRefreshing(false);
			if(success){
				arrayConversations = (ArrayList) data.getValue("events");
				//Collections.reverse(events);
				//adapter.updateData(events);
				onEventsReceived(arrayConversations);
			}else{
				vNoTasksContainer.setVisibility(View.VISIBLE);
				tvEmptyMsg.setText(R.string.conversatoins_list_no_trips);
				tvEmptyHint.setText(R.string.conversatoins_list_no_trips_desc);
				showError(R.string.conversatoins_list_failed_to_update_trips);
			}
		}
	};
	
	OnRefreshListener onRefreshRequestedListner = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			//DataStore.getInstance().requestUndoneTasks(eventsCallback);
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
		adapter = new ConversationsAdapter(null);
		lvTasks.setAdapter(adapter);
		lvTasks.setOnItemClickListener(adapter);
		srSwipeToRefresh.setOnRefreshListener(onRefreshRequestedListner);
	}
	
	private void requestUndoneTasks(){
		arrayConversations = DataStore.getInstance().getConversations();
		adapter.updateData(arrayConversations);
		if(arrayConversations == null || arrayConversations.isEmpty()){
			vNoTasksContainer.setVisibility(View.VISIBLE);
			tvEmptyMsg.setText(R.string.conversatoins_list_no_trips);
			tvEmptyHint.setText(R.string.conversatoins_list_no_trips_desc);
		}else{
			vNoTasksContainer.setVisibility(View.GONE);
		}
		//homeCallback.showProgress(true, R.string.loading_loading);
	}

	private void onEventsReceived(ArrayList<AppConversation> eventsRecieved){
		if(eventsRecieved != null){
			arrayConversations = eventsRecieved;
			//Collections.sort(events);
			adapter.updateData(arrayConversations);
		}
	}
	
	private void showError(int msg){
		if(isAdded())
			Toast.makeText(KhedniApp.getAppContext(), msg, Toast.LENGTH_SHORT).show();
	}

	private class ConversationsAdapter extends BaseAdapter implements OnItemClickListener{

		LayoutInflater inflater ;
		ArrayList<AppConversation> conversations ;
		//ArrayList<TrackInfo> fullTracksList ;
		
		public void updateData(ArrayList<AppConversation> cons){
			if(cons == null)
				this.conversations = new ArrayList<AppConversation> () ;
			else {
				this.conversations= cons ;  
			}			
			notifyDataSetChanged() ;
		}
		
		public ConversationsAdapter(ArrayList<AppConversation> cons){
			this.inflater = (LayoutInflater) getActivity().getSystemService(AppBaseActivity.LAYOUT_INFLATER_SERVICE);
			if(cons != null)
				this.conversations = cons ;
			else
				this.conversations= new ArrayList<AppConversation>(); 
		}
		
		@Override
		public int getCount() {
			return conversations.size();
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
		            convertView = inflater.inflate(R.layout.row_conversation, parent, false);
		            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
		            holder.tvdesc = (TextView) convertView.findViewById(R.id.tvDesc);
		            holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
		            holder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
		            holder.tvUnreadMsgsCount = (TextView) convertView.findViewById(R.id.tvUnreadMsgsCount);
		            holder.vUnreadMsgsContainer = convertView.findViewById(R.id.vUnreadMsgsContainer);
		            holder.vDataContainer = convertView.findViewById(R.id.vDataContainer);
		            convertView.setTag(holder);
		        }else{
				holder = (Holder) convertView.getTag() ;
			}
	
			try{
				AppConversation conversation = conversations.get(position);
	     		holder.tvName.setText(conversation.getName());
	     		holder.tvDate.setText(conversation.getLatestUpdateString());
	     		holder.tvdesc.setText(conversation.getLatestMsg());
	     		PhotoProvider.getInstance().displayProfilePicture(conversation.getContact().getPhotoPath(), holder.ivPhoto);
	     		
	     		int unread = 0;
     			if(unread > 0){
     				holder.vUnreadMsgsContainer.setVisibility(View.VISIBLE);
     				holder.tvUnreadMsgsCount.setText(String.valueOf(unread));
     				//holder.vDataContainer.setBackgroundResource(R.drawable.bg_list_active);
     			}else{
     				holder.vUnreadMsgsContainer.setVisibility(View.INVISIBLE);
     				//holder.vDataContainer.setBackgroundResource(R.drawable.bg_list);
     			}
			} catch (Exception e) {}
			return convertView;
		}
		
		class Holder{
			TextView tvName, tvDate, tvdesc, tvUnreadMsgsCount ;
			ImageView ivPhoto;
			View vUnreadMsgsContainer, vDataContainer ;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			AppConversation conv = this.conversations.get(position);
			if(conv.isHasSession()){
				Intent i = new Intent(getActivity(), ConversationActivity.class);
				i.putExtra(ConversationActivity.SESSION_ID_KEY, conv.getSession().getGlobalId());
				startActivity(i);		
			}else{
				Intent i = new Intent(getActivity(), ConversationActivity.class);
				i.putExtra("contact", conv.getContact().getJsonObject().toString());
				startActivity(i);
			}
		}
	}
}
