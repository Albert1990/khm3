package com.brainSocket.khednima3ak3.helpers;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.brainSocket.khednima3ak3.KhedniApp;

public class AnimationHelper {

	public static void popup(View view) {

		AnimationSet animation = new AnimationSet(true);
		float pivotX = view.getWidth() / 2;
		float pivotY = view.getHeight() / 2;
		ScaleAnimation anim = new ScaleAnimation(0f, 1.1f, 0f, 1.1f, pivotX,
				pivotY);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(300);
		animation.addAnimation(anim);

		anim = new ScaleAnimation(1.1f, 1f, 1.1f, 1f, pivotX, pivotY);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(100);
		anim.setStartOffset(300);
		animation.addAnimation(anim);

		view.startAnimation(animation);
	}
	
	
	public static void enlargeToRight(FragmentActivity activity, View view, AnimationListener listener) {

		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int screenHeight = displaymetrics.heightPixels;
		int screenWidth = displaymetrics.widthPixels;
		
		AnimationSet animation = new AnimationSet(true);
		float pivotX = view.getWidth() / 2;
		float pivotY = view.getHeight() / 2;
		ScaleAnimation anim = new ScaleAnimation(0.8f, 1f, 0.8f, 1f, pivotX,pivotY);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(200);
		anim.setStartOffset(200);
		//anim.setFillAfter(true);
		animation.addAnimation(anim);

		TranslateAnimation animTrans = new TranslateAnimation(0.0f, (float)(screenWidth-view.getWidth()), 0.0f, 0.0f); 
		anim.setInterpolator(new LinearInterpolator());
		animTrans.setDuration(400);
		//animTrans.setStartOffset(300);
		animation.addAnimation(animTrans);

		if(listener != null)
			animation.setAnimationListener(listener);
		
		view.startAnimation(animation);
	}
	
	
	public static void shrinkToLeft(FragmentActivity activity, View view, AnimationListener listener) {

		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int screenHeight = displaymetrics.heightPixels;
		int screenWidth = displaymetrics.widthPixels;
		
		AnimationSet animation = new AnimationSet(true);
		float pivotX = view.getWidth() / 2;
		float pivotY = view.getHeight() / 2;
		ScaleAnimation anim = new ScaleAnimation(1f, 0.8f, 1f, 0.8f, pivotX,pivotY);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(200);
		anim.setStartOffset(200);
		//anim.setFillAfter(true);
		animation.addAnimation(anim);

		TranslateAnimation animTrans = new TranslateAnimation(0.0f, (float)-(screenWidth-view.getWidth()), 0.0f, 0.0f); 
		anim.setInterpolator(new LinearInterpolator());
		animTrans.setDuration(400);
		//animTrans.setStartOffset(300);
		animation.addAnimation(animTrans);

		if(listener != null)
			animation.setAnimationListener(listener);
		
		view.startAnimation(animation);
	}

	
	
	public static void appliyPredefinedAmin (View view , int resAnim, AnimationListener listener){
		if(resAnim == 0 )
			return ;
		Animation anim = AnimationUtils.loadAnimation(KhedniApp.getAppContext(), resAnim);
		if(listener != null)
			anim.setAnimationListener(listener);
		view.startAnimation(anim);
	}
	
	
	public static void animateHeight (View view, int toHeight){
		Animation animHide = new ResizeAnimation(view, view.getMeasuredWidth(), view.getMeasuredHeight(), view.getMeasuredWidth(), toHeight) ;
		//Animation animHide = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_hide_serach);
	    view.startAnimation(animHide);
	    //AnimationListener 
	   /* animHide.setAnimationListener( new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				getHomeCallback().showActioinBarSearchBar(true);
				isAnimating = false ;
			}
		});*/
	}
	

}
