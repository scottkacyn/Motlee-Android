package com.motlee.android;

import com.flurry.android.FlurryAgent;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.fragment.EmptyFragmentWithCallbackOnResume;
import com.motlee.android.fragment.FirstUseFragment;
import com.motlee.android.fragment.LoginPageFragment;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FirstUseActivity extends BaseFacebookActivity {

	private static final double leftMargin = .08595041;
	private static final double rightMargin = 1 - leftMargin;
	private static final double topMargin = 0.80298913;
	private static final double bottomMargin = 0.93206522;
	
	ImageButton button;
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		FlurryAgent.onStartSession(this, getResources().getString(R.string.flurry_key));
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		setContentView(R.layout.first_use_page);

    	TextView header = (TextView) findViewById(R.id.first_use_header);
    	header.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
    	
    	UserInfo user = dbWrapper.getUser(SharePref.getIntPref(getApplicationContext(), SharePref.USER_ID));
    	
    	if (user != null && user.first_name != null)
    	{
    		header.setText("Welcome, " + user.first_name + "!");
    	}
    	else
    	{
    		header.setText("Welcome to Motlee!");
    	}
    	
    	button = (ImageButton) findViewById(R.id.skip_button);
    	
    	button.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				
				finish();
				
			}
    	});
    	
    	ImageView firstUseDescription = (ImageView) findViewById(R.id.first_use_description);
    	
    	firstUseDescription.setOnTouchListener(onTouchListener);
		
	}
	
    private View.OnTouchListener onTouchListener = new View.OnTouchListener(){

		public boolean onTouch(View v, MotionEvent event) {
			
			Rect rect = new Rect();
			
			v.getDrawingRect(rect);
			
			Rect buttonBounds = new Rect((int) (rect.right * leftMargin), 
					(int) (rect.bottom * topMargin), (int) (rect.right * rightMargin), (int) (rect.bottom * bottomMargin));
			
			if (buttonBounds.contains((int) event.getX(), (int) event.getY()) && event.getAction() == MotionEvent.ACTION_DOWN)
			{
				Intent createStreamIntent = new Intent(FirstUseActivity.this, CreateEventActivity.class);
				
				startActivity(createStreamIntent);
				
				finish();
			}
			else
			{
				return false;
			}
			
			return true;
		}
		
	};
	
	/*private View.OnTouchListener onTouchListener = new View.OnTouchListener(){

		
		
		public boolean onTouch(View v, MotionEvent event) {
			
			Rect rect = new Rect();
			
			v.getDrawingRect(rect);
			
			Rect buttonBounds = new Rect((int) (rect.right * leftMargin), 
					(int) (rect.bottom * topMargin), (int) (rect.right * rightMargin), (int) (rect.bottom * bottomMargin));
			
			if (buttonBounds.contains((int) event.getX(), (int) event.getY()) && event.getAction() == MotionEvent.ACTION_DOWN)
			{
				moveRight();
			}
			else
			{
				return gestureDetector.onTouchEvent(event);
			}
			
			return true;
		}
		
	};
	
	private void moveRight()
	{
		if (currentPage == 1)
		{
			currentPage = 2;
			DrawableCache.deleteDrawable(R.drawable.first_use_1);
			((BitmapDrawable)image.getDrawable()).getBitmap().recycle();
			image.setImageDrawable(DrawableCache.getDrawable(R.drawable.first_use_2, SharePref.getIntPref(getApplicationContext(), SharePref.DISPLAY_WIDTH)).getDrawable());
			
		}
		else if (currentPage == 2)
		{
			currentPage = 3;
			DrawableCache.deleteDrawable(R.drawable.first_use_2);
			((BitmapDrawable)image.getDrawable()).getBitmap().recycle();
			image.setImageDrawable(DrawableCache.getDrawable(R.drawable.first_use_3, SharePref.getIntPref(getApplicationContext(), SharePref.DISPLAY_WIDTH)).getDrawable());
		}
		else if (currentPage == 3)
		{
			currentPage = 4;
			DrawableCache.deleteDrawable(R.drawable.first_use_3);
			((BitmapDrawable)image.getDrawable()).getBitmap().recycle();
			image.setImageDrawable(DrawableCache.getDrawable(R.drawable.first_use_4, SharePref.getIntPref(getApplicationContext(), SharePref.DISPLAY_WIDTH)).getDrawable());
		}
		else if (currentPage == 4)
		{
			currentPage = 5;
			DrawableCache.deleteDrawable(R.drawable.first_use_4);
			((BitmapDrawable)image.getDrawable()).getBitmap().recycle();
			image.setImageDrawable(DrawableCache.getDrawable(R.drawable.first_use_5, SharePref.getIntPref(getApplicationContext(), SharePref.DISPLAY_WIDTH)).getDrawable());
		}
		else if (currentPage == 5)
		{
			//SharePref.setBoolPref(getApplicationContext(), SharePref.FIRST_EXPERIENCE, false);
			
			finish();
		}
	}
	
	private void moveLeft()
	{
		if (currentPage == 2)
		{
			currentPage = 1;
			DrawableCache.deleteDrawable(R.drawable.first_use_2);
			((BitmapDrawable)image.getDrawable()).getBitmap().recycle();
			image.setImageDrawable(DrawableCache.getDrawable(R.drawable.first_use_1, SharePref.getIntPref(getApplicationContext(), SharePref.DISPLAY_WIDTH)).getDrawable());
		}
		else if (currentPage == 3)
		{
			currentPage = 2;
			DrawableCache.deleteDrawable(R.drawable.first_use_3);
			((BitmapDrawable)image.getDrawable()).getBitmap().recycle();
			image.setImageDrawable(DrawableCache.getDrawable(R.drawable.first_use_2, SharePref.getIntPref(getApplicationContext(), SharePref.DISPLAY_WIDTH)).getDrawable());
		}
		else if (currentPage == 4)
		{
			currentPage = 3;
			DrawableCache.deleteDrawable(R.drawable.first_use_4);
			((BitmapDrawable)image.getDrawable()).getBitmap().recycle();
			image.setImageDrawable(DrawableCache.getDrawable(R.drawable.first_use_3, SharePref.getIntPref(getApplicationContext(), SharePref.DISPLAY_WIDTH)).getDrawable());
		}
		else if (currentPage == 5)
		{
			currentPage = 4;
			DrawableCache.deleteDrawable(R.drawable.first_use_5);
			((BitmapDrawable)image.getDrawable()).getBitmap().recycle();
			image.setImageDrawable(DrawableCache.getDrawable(R.drawable.first_use_4, SharePref.getIntPref(getApplicationContext(), SharePref.DISPLAY_WIDTH)).getDrawable());
		}
	}
	
	class MyGestureDetector extends SimpleOnGestureListener {
		
		@Override
		public boolean onDown(MotionEvent e)
		{
			return true;
		}
		
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	
                	moveRight();
                    return true;
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                   
                   moveLeft();
                   return true;
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

    }*/
	
}
