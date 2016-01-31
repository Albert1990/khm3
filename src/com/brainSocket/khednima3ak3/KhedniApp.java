package com.brainSocket.khednima3ak3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Toast;

import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.data.DataStore;

public class KhedniApp extends Application {

	private static Context AppContext ;
	private static Activity currentAcivity ;
	public static final String VERSIOIN_ID = "0.1";
	
	public final static int REQUEST_PICK_IMG_FROM_CAMERA = 894;
    public final static int REQUEST_PICK_IMG_FROM_GALLERY = 89;
	
	@Override
	public void onCreate() {
		super.onCreate();
		AppContext = this ;
		DataStore.getInstance() ; // to make sure DataStore is initalized 
	}
	
	public static Context getAppContext() {
		return AppContext;
	}
	
	/**
	 * used by the GCM receiver to change context on receive if not set
	 * @param appContext
	 */
	public static void setAppContext(Context appContext) {
		AppContext = appContext;
	}
	public static void setCurrentAcivity(Activity currentAcivity) {
		KhedniApp.currentAcivity = currentAcivity;
	}
	public static Activity getCurrentAcivity() {
		return currentAcivity;
	}
	
	public static void showToast (Context con, String msg){
		Toast.makeText(con, msg, Toast.LENGTH_SHORT).show();;
	}
	public static void showToast (Context con, int res){
		showToast(con, con.getString(res));
	}
	
	
	//-----------------------------------------------------------------------------------------------
	// utils ..... those methode could be move to a seperate Configs Class
	//-----------------------------------------------------------------------------------------------
	public enum phoeNumCheckResult {OK, SHORT, WRONG, EMPTY};
	public static phoeNumCheckResult validatePhoneNum(String num) {
		if(num == null || "".equals(num.trim()))
			return phoeNumCheckResult.EMPTY ;
		
		phoeNumCheckResult result = phoeNumCheckResult.OK;
		if (num.length() <= 8) {
			result = phoeNumCheckResult.SHORT;
		}
		
		if(!(num.startsWith("0") ) )
			result = phoeNumCheckResult.WRONG;
		
		return result;
	}
	
	
	/// DATE & TIME 
	
	public static String getFormatedDateForDisplay(long timestamp){
		String res = null;
		try {
			Date date = new Date(timestamp);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			res = sdf.format(date);
		}
		catch (Exception e) {}
		return res;
	}
	
	public static String getDateTime(long timestamp){
		String res = null;
		try {
			Date date = new Date(timestamp);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
			res = sdf.format(date);
		}catch (Exception e) {}
		return res;
	}
	
	public static String getTimeFromAPIString(String timestamp){
		try {
			SimpleDateFormat formaterIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			SimpleDateFormat formaterOut = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
			Date date = formaterIn.parse(timestamp);
			return formaterOut.format(date);
		}catch (Exception e) {}
		return "";
	}
	
	public static String getDateFromAPIString(String timestamp){
		try {
			SimpleDateFormat formaterIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			SimpleDateFormat formaterOut = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
			Date date = formaterIn.parse(timestamp);
			return formaterOut.format(date);
		}catch (Exception e) {}
		return "";
	}
	
	
	
    public static long getTimestampNow()
    {
    	long res = 0;
    	try {
    		res = Calendar.getInstance().getTimeInMillis();
    	}
    	catch (Exception e) {}
    	return res;
    }
    
	
	public static boolean isPhoneNumberEqual (String num1, String num2){
		if(num1 == null || num2 == null)
			return false ;
		return num1.equals(num2);
	}
    
	public static int dpToPx(int dp){
		int px = dp;
		try {
		    float density = getAppContext().getResources().getDisplayMetrics().density;
		    px = Math.round((float)dp * density);
		}
		catch (Exception e) {}
		return px;
	}
	
	public static Dialog getNewLoadingDilaog (Context con){
        Dialog dialogLoading = new Dialog(con);
        dialogLoading.setCancelable(false);
        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialogLoading.setContentView(R.layout.layout_loading_diag);
        dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialogLoading ;
    }
	
	public static String localizeMobileNumber(String mobileNumber)
	{
		return "0"+mobileNumber.substring(4, mobileNumber.length());
	}
	
	public static void resizeImage(Bitmap originalBitmap, String path,int newWidth) {
		try {
			int newHeight = (int) (originalBitmap.getHeight()*(newWidth/(float)originalBitmap.getWidth()));
			File imageFile = new File(path);
			Bitmap bmScreenshot = Bitmap.createScaledBitmap(originalBitmap,
					newWidth,newHeight , false);
			OutputStream fOut = new FileOutputStream(imageFile);
			bmScreenshot.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
			fOut.flush();
			fOut.close();
			bmScreenshot.recycle();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
