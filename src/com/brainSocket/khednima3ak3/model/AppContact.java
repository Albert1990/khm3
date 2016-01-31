package com.brainSocket.khednima3ak3.model;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.brainSocket.khednima3ak3.data.PhotoProvider;

public class AppContact implements Parcelable {		
	
	public enum GENDER {MALE, FEMALE} ;
	public enum SOCIAL_MEDIA_ACCOUNT_TYPE {PHONE_BOOK, SAB3EEN, VIBER, WHATSAP} ;
	
	public static final String DEFAULT_AVATAR=  "https://s3-us-west-2.amazonaws.com/almuajaha/profile/avatar01.png" ;
	
	
	String globalId = null;
	String localId = null ;
	String phoneNum = null;
	String name = null;
	String pictureURI = null;

	// non Server properties
	SOCIAL_MEDIA_ACCOUNT_TYPE network ;
		


	public AppContact(JSONObject json)
	{
		if(json == null) {
			return;
		}
		
		try {
			if(json.has("id")) 
				globalId = json.getString("id");
		}catch (Exception e){} 
		try {
			if(json.has("userName")) 
				name = json.getString("userName");
		}
		catch (Exception e) {}
		try {
			if(json.has("mobileNumber")) 
				phoneNum = json.getString("mobileNumber");
		} 
		catch (Exception e) {}
		try {
			if(json.has("photo") && !json.isNull("photo")) 
				pictureURI =  json.getString("photo").trim();
			if(pictureURI == null ) {
			}
		} 
		catch (Exception e) {}

		network = SOCIAL_MEDIA_ACCOUNT_TYPE.PHONE_BOOK ;
		
//		if(name.contains("Alaa")){
//			Log.d("con","Alaa");
//		}
	}
	
	public AppContact (Cursor cursor){
		if( cursor == null || cursor.isAfterLast() )
			return ;
		
		try {
			pictureURI = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
		} catch (Exception e) { }		 
		try {
			name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)) ;
		} catch (Exception e) { }
		try {
			localId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)) ;
		} catch (Exception e) { }
		try {
			phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)) ;
		} catch (Exception e) { } 
		// TODO backPort to SDK Version 10 "Normalized phone number is not supported"
	}
	
	
	
	/**
	 * Returns the {@link JSONObject} containing the user 
	 * details, just like the structure received from the API
	 * @return
	 */
	public JSONObject getJsonObject() {
		JSONObject json = new JSONObject();
		try {json.put("id", globalId);} catch (Exception e) {}
		try {json.put("userName", name);} catch (Exception e) {}
		try {json.put("photo", pictureURI);} catch (Exception e) {}
		try {json.put("mobileNumber", phoneNum);} catch (Exception e) {}
		return json;
	}
	
	/**
	 * Returns a string formatted {@link JSONObject} of the user object
	 * @return
	 */
	public String getJsonString()
	{
		JSONObject json = getJsonObject();
		return json.toString();
	}
	


	public String getGlobalId() {
		return globalId;
	}

	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

	public String getLocalId() {
		return localId;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPictureURI() {
		return pictureURI;
	}

	public void setPictureURI(String pictureURI) {
		this.pictureURI = pictureURI;
	}

	public SOCIAL_MEDIA_ACCOUNT_TYPE getNetwork() {
		return network;
	}

	public void setNetwork(SOCIAL_MEDIA_ACCOUNT_TYPE network) {
		this.network = network;
	}

	public void displayImage(Context context, ImageView iv){
		try {
			//if(name.contains("Alaa")){
				//Log.d("con","Alaa");
			//}
			if(pictureURI == null || pictureURI.isEmpty()){
				iv.setImageDrawable(getPlaceHolderDrawable());
			}else if(localId!=null && !localId.isEmpty()){
				Uri uri = Uri.parse(pictureURI) ;
				iv.setImageURI(uri);
			}else{
				PhotoProvider.getInstance().displayPhotoNormal(pictureURI, iv);
			}
		}catch(Exception e){
			PhotoProvider.getInstance().displayPhotoNormal(DEFAULT_AVATAR, iv);
		}
	}
	
	public Drawable getPlaceHolderDrawable(){
		
		ColorGenerator generator = ColorGenerator.SABEEN; // or use DEFAULT
		int color2 = generator.getColor(name);
		TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
//                    .bold()
//                    .width(150)
//                    .height(150)
                    .toUpperCase()
                .endConfig()
                .buildRound(name.substring(0, 1), color2);
		
		return drawable ;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int arg1) 	{		
		parcel.writeString(globalId);
		parcel.writeString(name);
		parcel.writeString(pictureURI);
		parcel.writeString(phoneNum);
	}
	
	public AppContact(Parcel parcel) 	{	// order does matter here
		globalId = parcel.readString();
		name = parcel.readString();
		pictureURI = parcel.readString();
		phoneNum = parcel.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
	

	public static final Parcelable.Creator<AppContact> CREATOR = new Parcelable.Creator<AppContact>()
    {
		public AppContact createFromParcel(Parcel in)
		{
			return new AppContact(in);
		}

		public AppContact[] newArray(int size)
		{
			return new AppContact[size];
		}
    };
        
}
