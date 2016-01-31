package com.brainSocket.khednima3ak3.data;

import android.app.Activity;

import com.brainSocket.khednima3ak3.KhedniApp;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

public class TrackingMgr {

	public enum TRACKING_TYPE {
		SOCIAl_MEDIA_INVITATION_SENT,
		REQUEST_CREATE_GROUP
	};

	private static TrackingMgr instance;
	private EasyTracker tracker;

	public static TrackingMgr getInstance() {

		if (instance == null)
			instance = new TrackingMgr();
		return instance;
	}

	public TrackingMgr() {
		 tracker = EasyTracker.getInstance(KhedniApp.getAppContext());
	}

	public void onAcitivityStart(Activity activity) {
		try {
			 tracker.activityStart(activity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onAcitivityStop(Activity activity) {
		try {
			 tracker.activityStop(activity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendTrackingEvent(Activity activity, String paramEvent, String paramAction, String paramLabel) {
		try {
			 tracker.send(MapBuilder.createEvent(paramEvent, paramAction, paramLabel,
			 null).build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*public void sendFragmentTracking(TRACKING_TYPE type, String extra) {
		try {
			String trackingName = "";
			switch (type) {
			case FRAG_LIST:
				trackingName = "TRIPS_LIST";
				break;
			case FRAG_MAP:
				trackingName = "TRIPS_MAP";
				break;

			}
			
			this.tracker.set(Fields.SCREEN_NAME, trackingName);
			this.tracker.send(MapBuilder.createAppView().build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

}
