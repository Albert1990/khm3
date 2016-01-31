package com.brainSocket.khednima3ak3;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.data.DataStore;
import com.brainSocket.khednima3ak3.data.ServerResult;
import com.brainSocket.khednima3ak3.data.TrackingMgr;
import com.brainSocket.khednima3ak3.data.DataStore.DataRequestCallback;
import com.brainSocket.khednima3ak3.model.AppArea;
import com.brainSocket.khednima3ak3.views.TextViewCustomFont;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

public class AddTripActivity extends AppBaseActivity implements OnClickListener,OnDateSetListener, TimePickerDialog.OnTimeSetListener {
	
	static String TIMEPICKER_TAG ="fragDiagTime";
	static String DATEPICKER_TAG ="fragDiagDate";
	static int MIN_PRICE_PER_SEAT = 10;
	
	AutoCompleteTextView autoCompleteDestination, autoCompleteOrigin;
	// weekDays
	View vWeeekDaysContainer;
	ToggleButton tbSun,tbMon,tbTue,tbWed,tbThu,tbFri,tbSat;
	View btnPickTime;
	TextView tvTime;
	View btnSetDate ;
	TextView tvDate, tvDateTitle;
	DiscreteSeekBar sbPrice;
	TextView txtPriceTitle;
	TextView tvPrice;
	View btnAdd;
	Dialog diagLoading;
	
	DatePickerDialog datePickerDialog;
	TimePickerDialog timePickerDialog;
	boolean isTimeSet;
	boolean isDateSet;
 
	//DATA
	int year = -1;
	int month = -1;
	int day = -1;
	int hour = -1;
	int minutes = -1;
	int price = 100; // when u edit this dont forget to edit the default value in the Layout XML
	boolean isRequring = true;
	boolean EnablePhoneNumber = false;
	
	DialogAlert alertDialog;

	DiscreteSeekBar.NumericTransformer callbackPriceChange = new DiscreteSeekBar.NumericTransformer() {
         @Override
         public int transform(int value) {
        	 price = value * 5;
        	 if(price < MIN_PRICE_PER_SEAT)
        		 price = MIN_PRICE_PER_SEAT;
        	 tvPrice.setText(price + " " + getString(R.string.my_trips_price_suffix));
             return price;
         }
     };
     
     DataRequestCallback callbackCreateTrip = new DataRequestCallback() {
		@Override
		public void onDataReady(ServerResult data, boolean success) {
			if(diagLoading != null)
				diagLoading.dismiss();
			if(success){
				setResult(RESULT_OK);
				finish();
				Toast.makeText(getApplicationContext(), getString(R.string.add_trip_activity_success), Toast.LENGTH_SHORT).show();
			}else{
				KhedniApp.showToast(AddTripActivity.this, getString(R.string.error_connection_failed));
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_add_trip);
		init() ;
		initAutocomplete();
		initDatePickers();
		initCustomActionBar();
	}
	
	private void init() {
		
		btnPickTime = findViewById(R.id.btnSetTime);
		btnSetDate = findViewById(R.id.btnSetDate);
		tvDateTitle = (TextView) findViewById(R.id.tvTitlePickDate);
		txtPriceTitle = (TextView) findViewById(R.id.txtPriceTitle);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvTime =(TextView) findViewById(R.id.tvTime);
		autoCompleteDestination = (AutoCompleteTextView) findViewById(R.id.autocompleteDest);
		autoCompleteOrigin = (AutoCompleteTextView) findViewById(R.id.autocompleteOrigin);
		vWeeekDaysContainer = findViewById(R.id.vWeekDaysContainer);
		tbFri = (ToggleButton) findViewById(R.id.tbFri);
		tbMon =(ToggleButton) findViewById(R.id.tbMon);
		tbSat =(ToggleButton) findViewById(R.id.tbSat);
		tbSun =(ToggleButton) findViewById(R.id.tbSun);
		tbThu =(ToggleButton) findViewById(R.id.tbThu);
		tbTue =(ToggleButton) findViewById(R.id.tbTue);
		tbWed =(ToggleButton) findViewById(R.id.tbWed);
		btnAdd = findViewById(R.id.btnAddTrip);
		sbPrice = (DiscreteSeekBar) findViewById(R.id.sbPrice);
		tvPrice = (TextView) findViewById(R.id.tvPrice);
		
		btnAdd.setOnClickListener(this);
		btnSetDate.setOnClickListener(this);
		btnPickTime.setOnClickListener(this);
		sbPrice.setNumericTransformer(callbackPriceChange);
		
		isRequring = getIntent().getExtras().getBoolean("isRequring");
		
		// inital state
		if(isRequring){
			tvDateTitle.setText(R.string.add_trip_title_weekdays);
			vWeeekDaysContainer.setVisibility(View.VISIBLE);
			btnSetDate.setVisibility(View.GONE);
		}else{
			tvDateTitle.setText(R.string.add_trip_title_date);
			vWeeekDaysContainer.setVisibility(View.GONE);
			btnSetDate.setVisibility(View.VISIBLE);
		}
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
		
		TextView tvFragTitle = (TextView) mCustomView.findViewById(R.id.tvFragTitle) ;
		View ivMenu = mCustomView.findViewById(R.id.ivMenu);
		View actionMessages = (ImageView) mCustomView.findViewById(R.id.actionChat);
		View actionNotifications = (ImageView) mCustomView.findViewById(R.id.actionNotifications);
		
		tvFragTitle.setText(R.string.add_trip_title);
		ivMenu.setVisibility(View.GONE);
		actionMessages.setVisibility(View.GONE);
		actionNotifications.setVisibility(View.GONE);
	}

	private void initAutocomplete() {
		ArrayList<String> suggestions = DataStore.getInstance().getAreasNames();
		ArrayAdapter<String> adapterStatic2 = new AreaSuggestiosAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions);
		autoCompleteOrigin.setAdapter(adapterStatic2);
		
		ArrayAdapter<String> adapterStatic1 = new AreaSuggestiosAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions);
		autoCompleteDestination.setAdapter(adapterStatic1);
	}
	
	private void initDatePickers(){
		final Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), true);
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);
	}
	
	private void resetInputErrors(){
		try {
			tvDate.setError(null);
			tvTime.setError(null);
			txtPriceTitle.setError(null);
			autoCompleteDestination.setError(null);
			autoCompleteOrigin.setError(null);
		} catch (Exception e) {}
	}
	
	private void openTimePicker(){
		 timePickerDialog.setVibrate(true);
         timePickerDialog.setCloseOnSingleTapMinute(false);
         timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
         //timePickerDialog.setOnTimeSetListener(this);
	}
	
	private void openDatePicker(){
		 datePickerDialog.setVibrate(false);
         datePickerDialog.setYearRange(2015, 2018);
         datePickerDialog.setCloseOnSingleTapDay(false);
         datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
         //datePickerDialog.setOnDateSetListener(this);
	}
	
	private void attemptSubmit(){
		
		resetInputErrors();
		if(!isDateSet && !isRequring){
			tvDate.setError(getString(R.string.add_trip_error_date_not_set));
			return;
		}
		if(!isTimeSet){
			tvTime.setError(getString(R.string.add_trip_error_time_not_set));
			return;
		}
		String dest = autoCompleteDestination.getText().toString();
		if(dest == null || dest.isEmpty()){
			autoCompleteDestination.setError(getString(R.string.add_trip_error_dest_not_set));
			return;
		}
		String orig = autoCompleteOrigin.getText().toString();
		if(orig == null || orig.isEmpty()){
			autoCompleteOrigin.setError(getString(R.string.add_trip_error_orig_not_set));
			return;
		}
		final AppArea origin = DataStore.getInstance().getAreaByName(orig) ;
		final AppArea destination = DataStore.getInstance().getAreaByName(dest) ;
		if(origin== null){
			autoCompleteOrigin.setError(getString(R.string.add_trip_error_orig_choose_from_list));
			return;
		}
		if(destination == null){
			autoCompleteDestination.setError(getString(R.string.add_trip_error_dest_not_set));
			return;
		}
		final int[] weekDaysSelected = readWeekDaysActive();
		if(isRequring){
			int countOfDaysSelected = 0;
			for (int i = 0; i < weekDaysSelected.length; i++) {
				countOfDaysSelected += weekDaysSelected[i];
			} 
			// if no week days selected
			if(countOfDaysSelected <= 0){
				tvDateTitle.setError(getString(R.string.add_trip_error_week_days_not_set));
				return;
			}
		}
		
		final String time = hour + ":" + minutes;
		final Date StartDate = new Date(year, month, day);
		
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (arg0.getId() == R.id.tvBtnRight) {
					EnablePhoneNumber = true;
				}else{
					EnablePhoneNumber = false;
				}
				submit(price,
						time,
						origin,
						destination,
						StartDate.getTime(),
						weekDaysSelected,
						isRequring,
						EnablePhoneNumber);
				alertDialog.dismiss();
				diagLoading = KhedniApp.getNewLoadingDilaog(AddTripActivity.this);
				diagLoading.show();
			}
		};

		alertDialog = new DialogAlert(this, R.string.add_trip_confirm_dialog_title, R.string.add_trip_confirm_dialog_desc, R.string.add_trip_confirm_dialog_ok, R.string.add_trip_confirm_dialog_cancel, clickListener);
		alertDialog.show();
	}
	
	private void submit(
			int price,
			String time,
			AppArea origin,
			AppArea destination,
			long startDate,
			int[] weekDaysSelected,
			boolean isRequring,
			boolean EnablePhoneNumber){
		try{
			
			DataStore.getInstance().createTrip(
					price,
					time,
					origin.getId(),
					destination.getId(),
					startDate,
					weekDaysSelected,
					isRequring,
					EnablePhoneNumber, 
					callbackCreateTrip);
			TrackingMgr.getInstance().sendTrackingEvent(this, "CreateNewTrip",isRequring?"Requring":"OneTimeTrip", origin.getName()+"_"+destination.getName());
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	private int[] readWeekDaysActive(){
		int[] res = {tbFri.isChecked()?1:0,
		                tbThu.isChecked()?1:0,
		                tbWed.isChecked()?1:0,
		                tbTue.isChecked()?1:0,
		                tbMon.isChecked()?1:0,
		                tbSun.isChecked()?1:0,
		                tbSat.isChecked()?1:0
					};
		return res;
	}
   @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
       //Toast.makeText(this, "new date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
	   this.year = year ;
	   this.month = month;
	   this.day = day;
       tvDate.setText(year + "/" + month + "/" + day);
       isDateSet = true;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        //Toast.makeText(this, "new time:" + hourOfDay + "-" + minute, Toast.LENGTH_LONG).show();
    	this.hour = hourOfDay;
    	this.minutes = minute;
    	tvTime.setText(hourOfDay + ":" + minute);
    	isTimeSet = true;
    }
    
	@Override
	public void onClick(View arg0) {
		int vId = arg0.getId();
		switch (vId) {
		case R.id.btnSetDate:
			openDatePicker();
		break;
		case R.id.btnSetTime:
			openTimePicker();
			break;
		case R.id.btnAddTrip:
			attemptSubmit();
			break;
		}
	}
	
	
}
