package com.brainSocket.khednima3ak3.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brainSocket.khednima3ak3.R;

public class ImageViewAppBG extends ImageView {

	public ImageViewAppBG(Context context) {
		super(context);
		init();
	}

	public ImageViewAppBG(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ImageViewAppBG(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		//DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		//((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int height = metrics.heightPixels;
		int width = metrics.widthPixels;
		 
		//BitmapDrawable bmap = (BitmapDrawable) this.getResources().getDrawable(R.drawable.bg1);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bg1) ;
		float bmapWidth = bitmap.getWidth() ;
		float bmapHeight = bitmap.getHeight();
		
		float wRatio = width / bmapWidth;
		float hRatio = height / bmapHeight;
		float ratioMultiplier = wRatio;
		
		float screenRatio = (float) width / height;
		float imgRatio = bmapWidth / bmapHeight;
		 if(screenRatio > imgRatio)
			 ratioMultiplier = wRatio ; 
		 else 
			 ratioMultiplier = hRatio ;
		 
		int newBmapWidth = (int) (bmapWidth*ratioMultiplier);
		int newBmapHeight = (int) (bmapHeight*ratioMultiplier);
		
		setMeasuredDimension ( (int)newBmapWidth , (int)newBmapHeight);
	}
	
}