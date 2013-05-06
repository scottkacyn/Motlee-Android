package com.motlee.android;

import com.flurry.android.FlurryAgent;
import com.motlee.android.object.Comment;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.SharePref;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class AddCommentActivity extends BaseFacebookActivity {
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_add);
		
		FlurryAgent.logEvent("AddCommentPage");
		
		setUpBottomBar();
	}
	
	private void setUpBottomBar()
	{
        DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.photo_detail_rect, SharePref.getIntPref(this, SharePref.DISPLAY_WIDTH));
        
        RelativeLayout commentBar = (RelativeLayout) findViewById(R.id.comment_cancel_send_bar);
        
        commentBar.setBackgroundDrawable(drawable.getDrawable());
        
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(drawable.getWidth(), drawable.getHeight());
        
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        
        commentBar.setLayoutParams(params);
	}
	
	public void onCancelClick(View view)
	{
		onBackPressed();
	}
	
	public void onSendClick(View view)
	{
		FlurryAgent.logEvent("CommentedOnPhoto");
		
		Intent intent = new Intent();
		
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
		EditText editText = (EditText) findViewById(R.id.edit_event_location_text);
		
		intent.putExtra("CommentText", editText.getText().toString());
		
		setResult(0, intent);
		
		finish();
	}

}
