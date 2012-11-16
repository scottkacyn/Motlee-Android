package com.motlee.android.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.facebook.Request;
import com.facebook.Session;
import com.motlee.android.R;
import com.motlee.android.adapter.CommentAdapter;
import com.motlee.android.adapter.SearchPeopleAdapter;
import com.motlee.android.object.Comment;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.EventItem;
import com.motlee.android.object.GlobalVariables;

public class CommentFragment extends ListFragmentWithHeader {

	private String pageTitle = "All Events";
	
	private View view;
	
	private LayoutInflater inflater;
	
	private EditText editText;
	
	private Handler mHandler = new Handler();
	
	private ArrayList<Comment> mComments;
	
	private String mSearchText;
	
	private CommentAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState)
	{
		
		this.inflater = inflater;
		view = (View) this.inflater.inflate(R.layout.activity_comment, null);
		
		LinearLayout textBackground = (LinearLayout) view.findViewById(R.id.comment_text_background);
		textBackground.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.comment_box, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		
		Collections.sort(mComments);
		
		adapter = new CommentAdapter(getActivity(), R.layout.comment_list_item, mComments);
		
		setListAdapter(adapter);
		
		TextView tv = (TextView) mHeaderView.findViewById(R.id.header_textView);
		tv.setText(pageTitle);
		tv.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
		
		View headerLeftButton = mHeaderView.findViewById(R.id.header_left_button);
		headerLeftButton.setVisibility(View.VISIBLE);
		
		setEditText();
		
		return view;
	}
	
	public void addCommentToListAdapter(Comment comment)
	{
		adapter.add(comment);
		mComments.add(comment);
	}
	
	public void setComments(ArrayList<Comment> comments)
	{
		mComments = comments;
	}
	
	private void setEditText()
	{
		editText = (EditText) view.findViewById(R.id.comment_text);
		editText.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
		editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		
		editText.setOnEditorActionListener(editorActionListener);
		editText.setOnFocusChangeListener(focusChangeListener);
		
	}
	
	public String getCommentText()
	{
		return editText.getText().toString();
	}
	
	private OnFocusChangeListener focusChangeListener = new OnFocusChangeListener(){
		// if we have focus, change color for input text
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                ((EditText) v).setTextColor(Color.WHITE);
                ((EditText) v).setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
            } 
        }
	};
	
	private OnEditorActionListener editorActionListener = new OnEditorActionListener(){
		
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			
			// if we hit enter on the keyboard, hide keyboard
            if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                // NOTE: In the author's example, he uses an identifier
                // called searchBar. If setting this code on your EditText
                // then use v.getWindowToken() as a reference to your 
                // EditText is passed into this callback as a TextView

                in.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                editText.clearFocus();
                
               return true;

            }
            return false;
		}
		
	};

	public void removeTextAndKeyboard() {
		
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
        editText.setText("");
	}
}