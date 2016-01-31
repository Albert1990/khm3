package com.brainSocket.khednima3ak3.fragments;

import java.io.File;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

import com.brainSocket.khednima3ak3.HomeCallbacks;
import com.brainSocket.khednima3ak3.KhedniApp;
import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.data.DataStore;
import com.brainSocket.khednima3ak3.data.PhotoProvider;
import com.brainSocket.khednima3ak3.data.ServerResult;
import com.brainSocket.khednima3ak3.data.DataStore.DataRequestCallback;
import com.brainSocket.khednima3ak3.dialogs.DiagPickPhoto;
import com.brainSocket.khednima3ak3.dialogs.DiagPickPhoto.PickDiagActions;
import com.brainSocket.khednima3ak3.dialogs.DiagPickPhoto.PickDiagCallBack;
import com.brainSocket.khednima3ak3.model.AppUser;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragUserProfile extends Fragment implements OnClickListener{
	private ImageView ivUser;
	private TextView etFirstName;
	private TextView etLastName;
	private TextView etJob;
	private TextView etAge;
	private View btnSaveUserProfile;
	
	///temp 
	private Uri outputFileUri; //holder for the image picked from the camera
	private String profileURI;
	
	private HomeCallbacks homeCallbacks;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.frag_user_profile, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
	}
	
	private void init()
	{
		homeCallbacks=(HomeCallbacks)getActivity();
		bindViewItems();
		bindViewData();
	}
	
	private void bindViewItems()
	{
		ivUser=(ImageView)getView().findViewById(R.id.ivUser);
		ivUser.setOnClickListener(this);
		etFirstName=(TextView)getView().findViewById(R.id.etFirstName);
		etLastName=(TextView)getView().findViewById(R.id.etLastName);
		etJob=(TextView)getView().findViewById(R.id.etJob);
		etAge=(TextView)getView().findViewById(R.id.etAge);
		btnSaveUserProfile=getView().findViewById(R.id.btnSaveUserProfile);
		btnSaveUserProfile.setOnClickListener(this);
	}
	
	private void bindViewData()
	{
		try
		{
			AppUser me= DataStore.getInstance().getMe();
			if(me!=null)
			{
				PhotoProvider.getInstance().displayProfilePicture(me.getPhotoPath(), ivUser);
				etFirstName.setText(me.getFirstName());
				etLastName.setText(me.getLAstName());
				etJob.setText(me.getJob());
				etAge.setText(me.getAge());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private DataRequestCallback updateUserProfileCallback=new DataRequestCallback() {
		
		@Override
		public void onDataReady(ServerResult data, boolean success) {
			homeCallbacks.showProgress(false, R.string.loading_loading);
			if(success)
			{
				homeCallbacks.showToast(getString(R.string.frag_user_profile_update_success));
			}
			else
			{
				homeCallbacks.showToast(getString(R.string.frag_user_profile_update_failed));
			}
		}
	};
	
	private void updateUserProfile()
	{
		//check inputs
		String firstName=etFirstName.getText().toString();
		String lastName=etLastName.getText().toString();
		String job=etJob.getText().toString();
		String age=etAge.getText().toString();
		
		boolean cancel=false;
		View focusView=null;
		if(firstName.length()<=1)
		{
			cancel=true;
			etFirstName.setError(getString(R.string.frag_user_profile_first_name_required));
			focusView=etFirstName;
		}
		if(lastName.length()<=1)
		{
			cancel=true;
			etLastName.setError(getString(R.string.frag_user_profile_last_name_required));
			focusView=etLastName;
		}
		if(cancel)
		{
			focusView.requestFocus();
		}
		else
		{
			homeCallbacks.showProgress(true, R.string.loading_loading);
			DataStore.getInstance().attemptUpdateUserProfile(firstName, lastName, job, age, profileURI, updateUserProfileCallback);
		}
	}
	
	private static File getNewTempImgFile(boolean isForUpload){
		final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "aswaq_temp_imgs" + File.separator);
		root.mkdirs();
		final String fname = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ((isForUpload)?"_resized":"")+ ".jpg";
		File sdImageMainDirectory = new File(root, fname);
		return sdImageMainDirectory;
	}
	
	private void browseImage(){

		final DiagPickPhoto diag;
		PickDiagCallBack x1=new PickDiagCallBack() {
            @Override
            public void onActionChoose(PickDiagActions action) {
                switch (action){
                    case CAMERA:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        outputFileUri = Uri.fromFile(getNewTempImgFile(false));
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                        startActivityForResult(intent,KhedniApp.REQUEST_PICK_IMG_FROM_CAMERA);
                        break;
                    case GALLERY:
                        Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intentGallery.setType("image/*");
                        startActivityForResult(Intent.createChooser(intentGallery, "Select File"),KhedniApp.REQUEST_PICK_IMG_FROM_GALLERY);

                        break;
                    case CANCEL:
                        //uriImgInvoice = null;
                        break;
                }
            }
        };
		diag=new DiagPickPhoto(getActivity(), x1);
        diag.show();
		}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
try{
			
			if(resultCode == Activity.RESULT_OK){
				String filePath = null;
	            if(requestCode ==KhedniApp.REQUEST_PICK_IMG_FROM_CAMERA || requestCode == KhedniApp.REQUEST_PICK_IMG_FROM_GALLERY){
		            if(requestCode ==KhedniApp.REQUEST_PICK_IMG_FROM_CAMERA){
		            	filePath = outputFileUri.getPath();
		            }else if(requestCode == KhedniApp.REQUEST_PICK_IMG_FROM_GALLERY){
		                Uri uri = data.getData();
		                if (uri.getScheme() != null && uri.getScheme().equals("file")) {
		                	filePath = uri.getPath();
		                }else{
		                	String[] filePathField = {MediaStore.Images.Media.DATA};
		                	Cursor cursor = getActivity().getContentResolver().query(uri, filePathField, null, null, null);
		                    if (cursor == null) {
		                        throw new IllegalArgumentException("got null cursor when attempting to find path for external storage uri");
		                    }

		                    cursor.moveToFirst();
		                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		                    filePath = cursor.getString(column_index);
		                    
//		                    Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
//		                    cursor.moveToFirst();
//		                    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//		                    String path = cursor.getString(idx);
//		                    uri = Uri.parse(path);
		                }
		            }
		            
		            if(filePath != null && !filePath.isEmpty()){
		            	filePath = new File(filePath).getAbsolutePath(); // make sure we have a valid absolute path
			            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
			            String resizedImgPath = getNewTempImgFile(true).getAbsolutePath();
			            KhedniApp.resizeImage(yourSelectedImage, resizedImgPath,1400);
			            profileURI = resizedImgPath;
			            ivUser.setImageBitmap(yourSelectedImage);
		            }
	            }
	        }else if(requestCode == KhedniApp.REQUEST_PICK_IMG_FROM_CAMERA ){
	            // if picking a picture from camera failed or canceled then reset the URi
                outputFileUri = null;
            }     
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		int viewId=v.getId();
		switch(viewId)
		{
		case R.id.btnSaveUserProfile:
			updateUserProfile();
			break;
		case R.id.ivUser:
			browseImage();
			break;
		}
	}
}
