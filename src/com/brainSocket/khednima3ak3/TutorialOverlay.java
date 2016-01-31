package com.brainSocket.khednima3ak3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.brainSocket.khednima3ak3.R;
import com.brainSocket.khednima3ak3.helpers.AnimationHelper;
import com.brainSocket.khednima3ak3.views.TextViewCustomFont;

public class TutorialOverlay extends Dialog implements android.view.View.OnClickListener {

	
	
	public enum TUTORIAL_TYPE {PLant,Self, Send, TASK_BUBLE} ; 
	 
	
	private Handler handler ;
	private Activity activity ;
	private TutorialCallback callback ;
	
	// Views 
	private ImageView  ivHeighlitedView,ivClose;
	private View heighlitedView ;
	private View rootView , vArrow1,vArrow2 ;
	private TextViewCustomFont tvTutMsg1, tvTutTitle ;
	private View flBuble ;
	String txtTitle, txtMsg1;
	TUTORIAL_TYPE tutorialType ;
	
	
	boolean animatingDirectionUp = false ; 
	AnimationListener animListner = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {}
		@Override
		public void onAnimationRepeat(Animation animation) {}
		@Override
		public void onAnimationEnd(Animation animation) {
			if(animatingDirectionUp){
				AnimationHelper.appliyPredefinedAmin(vArrow2, R.anim.floating_vertical_to_bottom, this);
				animatingDirectionUp = false ;
			}else{
				AnimationHelper.appliyPredefinedAmin(vArrow2, R.anim.floating_vertical_up, this);
				animatingDirectionUp = true ;
			}
		}
	};
	
	
	public TutorialOverlay(Activity activity , View heighlited ,String title, String msg1,TUTORIAL_TYPE type ,TutorialCallback callback) {
		super(activity,android.R.style.Theme_Translucent_NoTitleBar); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setWindowAnimations(R.style.DialogNoAnimation);
		setContentView(R.layout.tutorial_overlay);
		
		handler = new Handler() ;
		this.activity = activity ;
		this.heighlitedView = heighlited ;
		this.callback = callback ;
		this.txtMsg1 = msg1;
		this.txtTitle = title ;
		
		this.tutorialType = type ;
		init() ;		
	}
	
	
	private void init (){
		
		this.ivHeighlitedView = (ImageView) findViewById(R.id.ivHighlitedView);
		this.rootView = findViewById(R.id.tutorialRoot);
		this.tvTutTitle = (TextViewCustomFont) findViewById(R.id.tvTutTitle);
		this.tvTutMsg1 = (TextViewCustomFont) findViewById(R.id.tvTutMsg1);
		this.flBuble = findViewById(R.id.flBuble);
		this.ivClose = (ImageView) findViewById(R.id.ivClose);
		vArrow1  = findViewById(R.id.ivArrow1);
		vArrow2  = findViewById(R.id.ivArrow2);
		
		this.ivHeighlitedView.setOnClickListener(this);
		this.ivClose.setOnClickListener(this);
		
		
		if(txtTitle != null)
			this.tvTutTitle.setText(txtTitle);
		else
			this.tvTutTitle.setVisibility(View.GONE);
		
		if(txtMsg1 != null){
			this.tvTutMsg1.setText(txtMsg1);
		}else{
			this.tvTutMsg1.setVisibility(View.GONE);
			this.tvTutTitle.setGravity(Gravity.CENTER);
		}
				
		AnimationHelper.appliyPredefinedAmin(vArrow2, R.anim.floating_vertical_to_bottom, animListner);
	}
	
	@SuppressLint("NewApi") 
	@Override
	public void show() {
		super.show();
		
		final ViewTreeObserver observer= rootView.getViewTreeObserver();
	       observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            @Override
	            public void onGlobalLayout() {
	            	
	            	showContent() ;
	            	
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				    } else {
				    	rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				    }
	            }
	        });
		
	}
		
	
	private void showContent(){
		if(heighlitedView == null ){
			ivHeighlitedView.setVisibility(View.INVISIBLE);
			vArrow2.clearAnimation();
			vArrow2.setVisibility(View.INVISIBLE);
			return ;
		}
		 try{
			heighlitedView.setDrawingCacheEnabled(true);
			Bitmap bmScreenshot = Bitmap.createBitmap( heighlitedView.getMeasuredWidth(), heighlitedView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);                
		    Canvas c = new Canvas(bmScreenshot);
		    heighlitedView.draw(c);
		    ivHeighlitedView.setImageBitmap(bmScreenshot);
		    
		    //  positioning
		    // calculate StatuBar height
		    DisplayMetrics dm = new DisplayMetrics();
		    activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		    //final int topOffset = dm.heightPixels - rootView.getMeasuredHeight();
		    
		    int[] location = new int[2];
		    heighlitedView.getLocationOnScreen(location);
		    float x = location[0];
		    float y = location[1];
		    int totalHeight = dm.heightPixels ;
		    
		    RelativeLayout.LayoutParams imgParams = (RelativeLayout.LayoutParams)ivHeighlitedView.getLayoutParams();
		    int marginButtom = (int) (totalHeight - heighlitedView.getHeight() - y) ;
		    imgParams.setMargins((int)x, 0, 0, marginButtom);
		    
	/*	    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)flBuble.getLayoutParams();
		    params.addRule(RelativeLayout.ABOVE, R.id.ivHighlitedView);
		    flBuble.setLayoutParams(params); //causes layout update
	*/	    
		    rootView.invalidate();
		    rootView.requestLayout() ;
		 }catch(Exception e){
			 ivHeighlitedView.setVisibility(View.INVISIBLE);
				vArrow2.clearAnimation();
				vArrow2.setVisibility(View.INVISIBLE);
		 }
	}
	
	
	@Override
	public void onClick(View arg0) {
		int viewId = arg0.getId() ;
  
		switch (viewId) {
		case R.id.ivHighlitedView:
			if(callback != null)
				callback.onHeighlitedViewClicked(tutorialType) ;
			dismiss();
			break;
		case R.id.ivClose:
			if(callback != null)
				callback.onTutCanseled(tutorialType);
			dismiss();
			break;
		}

	}
	
	@Override
	public void onBackPressed() {
		if(callback != null)
			callback.onTutCanseled(tutorialType);
		super.onBackPressed();
	}

	public interface TutorialCallback {
		public void onHeighlitedViewClicked(TUTORIAL_TYPE tutType);
		public void onTutCanseled(TUTORIAL_TYPE tutType) ;
	}
	
}
