package com.brainSocket.khednima3ak3.model;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.brainSocket.khednima3ak3.data.PhotoProvider;

public class AppUser /*implements Parcelable */{		
	
	public enum GENDER {MALE, FEMALE} ;
	
	public static final String DEFAULT_AVATAR=  "https://s3-us-west-2.amazonaws.com/almuajaha/profile/avatar01.png" ;
	public static final String API_IMGS_STORAGE_DIR=  "http://khednym3ak.com/private1/" ;
	
	String id ;
	String mobileNumber;
	String firstName;
	String lastName;
	String countryId;
	GENDER gender ;
	String accessToken;
	String photoPath;
	boolean isVerified;
	int age;
	String facebookId;
	String job;



	public AppUser(JSONObject json){
		if(json == null) {
			return;
		}
		
		try {
			if(json.has("id")) 
				id = json.getString("id");
		} 
		catch (Exception e) {}
		try {
			if(json.has("mobile_number")) 
				mobileNumber = json.getString("mobile_number");
		} 
		catch (Exception e) {}
		try {
			if(json.has("first_name")) 
				firstName = json.getString("first_name");
		} 
		catch (Exception e) {}
		try {
			if(json.has("last_name")) 
				lastName = json.getString("last_name");
		} 
		catch (Exception e) {}
		try {
			if(json.has("country_id")) 
				countryId = json.getString("country_id");
		} 
		catch (Exception e) {}
		try {
			if(json.has("gender")) 
				gender = (json.getInt("gender") == 1)?GENDER.MALE:GENDER.FEMALE;
		}catch (Exception e) {gender = GENDER.MALE;}
		try {
			if(json.has("is_verified")) 
				isVerified = (json.getInt("is_verified") > 0)?true:false;
		}catch (Exception e) {isVerified = false;}
		try {
			if(json.has("access_token"))
				accessToken = json.getString("access_token");
		}catch (Exception e) {}
		try {
			if(json.has("photo_path")){ 
				photoPath = json.getString("photo_path");
				if(photoPath.equals("-1"))
					photoPath = "";
			}
		}catch (Exception e) {}
		try {
			if(json.has("age")) 
				age = json.getInt("age");
		}catch (Exception e) {}
		try {
			if(json.has("job")) 
				job = json.getString("job");
		}catch (Exception e) {}
		try {
			if(json.has("facebook_id")) 
				facebookId = json.getString("facebook_id");
		}catch (Exception e) {}
	}
	
	/**
	 * Returns the {@link JSONObject} containing the user 
	 * details, just like the structure received from the API
	 * @return
	 */
	public JSONObject getJsonObject()
	{
		JSONObject json = new JSONObject();
		try {json.put("id", id);} catch (Exception e) {}
		try {json.put("first_name", firstName);} catch (Exception e) {}
		try {json.put("last_name", lastName);} catch (Exception e) {}
		try {json.put("photo_path", photoPath);} catch (Exception e) {}
		try {json.put("age", this.age);} catch (Exception e) {}
		try {json.put("mobile_number", mobileNumber);} catch (Exception e) {}
		try {json.put("job", job);} catch (Exception e) {}
		try {json.put("access_token", accessToken);} catch (Exception e) {}
		try {json.put("gender", (gender==GENDER.MALE)?1:0);} catch (Exception e) {}
		try {json.put("is_verified", (isVerified)?1:0);} catch (Exception e) {}
		try {json.put("facebook_id", facebookId);} catch (Exception e) {}
		try {json.put("country_id", countryId);} catch (Exception e) {}
		
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

	public String getId() {
		return id;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLAstName() {
		return lastName;
	}
	public String getFacebookId() {
		return facebookId;
	}

	public String getCountryId() {
		return countryId;
	}

	public GENDER getGender() {
		return gender;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getPhotoPath() {
		if(photoPath != null && !photoPath.isEmpty())
			return API_IMGS_STORAGE_DIR+photoPath;
		else
			return photoPath;
	}

	public boolean isIsVerified() {
		return isVerified;
	}

	public int getAge() {
		return age;
	}

	public String getFacebook_id() {
		return facebookId;
	}

	public String getJob() {
		return job;
	}

	public void displayImage(Context context, ImageView iv){
		try {
			//if(name.contains("Alaa")){
				//Log.d("con","Alaa");
			//}
			if(photoPath == null || photoPath.isEmpty()){
				iv.setImageDrawable(getPlaceHolderDrawable());
			}else{
				PhotoProvider.getInstance().displayPhotoNormal(photoPath, iv);
			}
		}catch(Exception e){
			PhotoProvider.getInstance().displayPhotoNormal(DEFAULT_AVATAR, iv);
		}
	}
	
	public Drawable getPlaceHolderDrawable(){
		
		ColorGenerator generator = ColorGenerator.SABEEN; // or use DEFAULT
		int color2 = generator.getColor(firstName);
		TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
//                    .bold()
//                    .width(150)
//                    .height(150)
                    .toUpperCase()
                .endConfig()
                .buildRound(firstName.substring(0, 1), color2);
		
		return drawable ;
	}    
}
