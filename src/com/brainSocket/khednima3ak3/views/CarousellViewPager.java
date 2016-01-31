package com.brainSocket.khednima3ak3.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CarousellViewPager extends ViewPager {

	private boolean isSlidingEnabled = true;

	public CarousellViewPager(Context context) {
		super(context);
		init();
	}

	public CarousellViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		//setPageTransformer(false, new RotationPageTransformer(0));
		//setPageTransformer(false, new FadePageTransformer());
	}

	public void setSlidingEnabled(Boolean val) {
		isSlidingEnabled = val;
	}

	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if(isSlidingEnabled)
			return super.onInterceptTouchEvent(arg0);
		else
			return false ;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (isSlidingEnabled) {
			return super.onTouchEvent(arg0);
		} else {
			return false;
		}
	}
	
	public class FadePageTransformer implements ViewPager.PageTransformer{
	    private float minAlpha = 0.5f;
	    private float minScale = 0.92f;
	 	 
	    @SuppressLint("NewApi") 
	    public void transformPage(View view, float position){
	    	if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
		        if(position < -1){
		            //view.setAlpha(minAlpha);
		            view.setScaleX(1);
		            view.setScaleY(1);
		            view.setTranslationX(0);
		        }else if(position <= 1){
		            //view.setAlpha( minAlpha + (1.0f-minAlpha)* (1-Math.abs(position)) );
		        	if(position <0)
		        		view.setTranslationX(-(getMeasuredWidth() * position));
		        	//else
		        	//	view.setTranslationX(-(getMeasuredWidth() * position));
		        	view.setTranslationY((30 * position));
		            //float  scale = minScale + (1.0f-minScale)* (1-Math.abs(position)) ;
		            //float  scale = 1 + (0.3f * (1-Math.abs(position))) ;
		            //view.setScaleX(scale);
		            //view.setScaleY(scale);
		            
		        }else{ 
		            //view.setAlpha(minAlpha);
		            view.setScaleX(1);
		            view.setScaleY(1);
		            view.setTranslationX(0);
		        }
	    	}
	    }
	    
	}
	
	
//	public class RotationPageTransformer implements ViewPager.PageTransformer{
//	    private float minAlpha;
//	    private int degrees;
//	    private float distanceToCentreFactor;
//	 
//	    /**
//	     * Creates a RotationPageTransformer
//	     * @param degrees the inner angle between two edges in the "polygon" that the pages are on.
//	     * Note, this will only work with an obtuse angle
//	     */
//	    public RotationPageTransformer(int degrees){
//	        this(degrees, 0.7f);
//	    }
//	 
//	    /**
//	     * Creates a RotationPageTransformer
//	     * @param degrees the inner angle between two edges in the "polygon" that the pages are on.
//	     * Note, this will only work with an obtuse angle
//	     * @param minAlpha the least faded out that the side
//	     */
//	    public RotationPageTransformer(int degrees, float minAlpha){
//	        this.degrees = degrees;
//	        distanceToCentreFactor = (float) Math.tan(Math.toRadians(degrees / 2))/2;
//	        this.minAlpha = minAlpha;
//	    }
//	 
//	    public void transformPage(View view, float position){
//	         
//	        if(position < -1){
//	            view.setRotation(0);
//	            view.setAlpha(0);
//	        }else if(position <= 1){ //[-1,1]
//	            rotateRollette(view, position);
//	        }else{ 
//	            view.setRotation(0);
//	            view.setAlpha(0);
//	        }
//	    }
//	    
//	    private void rotateY (View page, float position){
//	    	//if(Math.abs(position) < 0.06)
//	    		//page.setRotationY(0);
//	    	//else{
//	    		page.setRotationY(position * -60);
//	    	//}
//	    	page.setAlpha(1);
//	    	Log.d("transformation", "pos " + position) ;
//	    }
//	    
//	    private void rotateRollette (View view, float position){
//	    	int pageWidth = view.getWidth();
//	        int pageHeight = view.getHeight();
//	        view.setPivotX((float) pageWidth / 2);
//	        view.setPivotY((float) (pageHeight + pageWidth * distanceToCentreFactor));
//	    	
//	    	view.setTranslationX((-position) * pageWidth); //shift the view over
//            view.setRotation(position * (180 - degrees)); //rotate it
//            view.setAlpha(Math.max(minAlpha, 1 - Math.abs(position)/3));
//	    } 
//	    
//	}
	

}