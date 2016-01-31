package com.brainSocket.khednima3ak3.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.brainSocket.khednima3ak3.R;



public class FragAbout extends Fragment implements OnClickListener {

	private static final String BRAIN_SOCKET_LINK = "http://brain-socket.com" ;
	public static final String SABEEN_WEB_LINK = "http://khednym3ak.com/" ; 
	private static final String ESTAGHFART_LINK = "http://facebook.com/khednym3ak" ; 
	
	
	View btnBrainSocket ;
	View btnSabeen ;
	View btnFbPage;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.aboutus, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
	}
	
	private void init(){
		btnBrainSocket = getView().findViewById(R.id.btnBrainSocket);
		btnSabeen = getView().findViewById(R.id.btnWeb);
		btnFbPage = getView().findViewById(R.id.btnFace);
		
		btnBrainSocket.setOnClickListener(this);
		btnSabeen.setOnClickListener(this);
		btnFbPage.setOnClickListener(this);
	}
	
	private void openLInk (String link){
		Intent i = new Intent() ;
		i.setData(Uri.parse(link));
		startActivity(i);
	}
	

	@Override
	public void onClick(View v) {
		int id  = v.getId();
		switch (id) {
		case R.id.btnBrainSocket:
			openLInk(BRAIN_SOCKET_LINK);
			break;
		case R.id.btnWeb:
			openLInk(SABEEN_WEB_LINK);
			break;
		case R.id.btnFace:
			openLInk(ESTAGHFART_LINK);
			break;
		}
		
	}
}
