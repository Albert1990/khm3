package com.brainSocket.khednima3ak3.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FrameAnimationView extends ImageView {
    private Context mContext = null;
     
    private static final int DELAY = 100; //delay between frames in milliseconds
     
    private AnimationDrawable animDrawable ;
    
    public FrameAnimationView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
    }
    public FrameAnimationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
    
    public FrameAnimationView(Context context) {
		super(context);
	}
     
    public void loadAnimation(String prefix, int StartFrame, int endFrame,int drawx,int drawy) {
    	animDrawable = new AnimationDrawable();
    	animDrawable.setOneShot(true);
        for (int x = StartFrame; x >= endFrame; x--){
            String name = prefix + "" + x;  // prefix = "spark" see loadAnimation call
            int res_id = mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());
            Drawable d = mContext.getResources().getDrawable(res_id);
            animDrawable.addFrame(d, DELAY);
        }
    }
     
    public void playAnimation() {
    	if(animDrawable != null){
    		setImageDrawable(animDrawable);
    		animDrawable.start();
    	}
    }
}