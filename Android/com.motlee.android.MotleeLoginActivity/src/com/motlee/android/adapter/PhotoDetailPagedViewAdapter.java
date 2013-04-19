package com.motlee.android.adapter;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.motlee.android.R;
import com.motlee.android.ZoomActivity;
import com.motlee.android.database.DatabaseHelper;
import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.Comment;
import com.motlee.android.object.DrawableCache;
import com.motlee.android.object.DrawableWithHeight;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.Like;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.PhotoDetail;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.UserInfo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import greendroid.widget.PagedAdapter;

public class PhotoDetailPagedViewAdapter extends PagedAdapter {

	
	private LayoutInflater inflater;
	private ArrayList<PhotoDetail> mData = new ArrayList<PhotoDetail>();
	private Context context;
	private boolean showFirstComment = false;
	
	private SimpleDateFormat formatter = new SimpleDateFormat("M/d '@' h:mm aa");
	
	private DatabaseHelper helper;
	
	private DatabaseWrapper dbWrapper;
	
	public PhotoDetailPagedViewAdapter(Context context, ArrayList<PhotoItem> data) {
		this.context = context;
		
		for (PhotoItem photo : data)
		{
			this.mData.add(new PhotoDetail(photo));
		}
		
		this.inflater = LayoutInflater.from(context);
		
		helper = DatabaseHelper.getInstance(context.getApplicationContext());
		dbWrapper = new DatabaseWrapper(context.getApplicationContext());
	}

	@Override
	public int getCount() {
		
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public void notifyDataSetChanged()
	{
		super.notifyDataSetChanged();
	}
	
	public void showFirstComment()
	{
		this.showFirstComment = true;
	}
	
	private class ViewHolder
	{
		public RelativeLayout header_view;
		public View touch_overlay;
        public RelativeLayout photo_detail_story;
        public ImageView photo_detail_picture;
        public TextView photo_detail_story_text;
        public LinearLayout photo_detail_bottom_info;
        public ImageView photo_detail_thumbnail;
        public TextView photo_detail_name_text;
        public TextView photo_detail_time_text;
        public ImageView photo_detail_thumbnail_bg;
        //public ProgressBar photo_detail_spinner;
        public ImageView photo_detail_thumb_image;
        public TextView photo_detail_upload_text;
        public View photo_detail_likes;
        public TextView photo_detail_likes_text;
        public TextView photo_detail_like_button_text;
        public TextView photo_detail_comment_button_text;
        public RelativeLayout photo_detail_user_row;
        //public ListView comment_list;
        public TextView photo_detail_caption;
        //public ImageButton photo_detail_like_button;
        //public ProgressBar photo_detail_download_progress;
        public LinearLayout photo_detail_comment_bar;
        public View photo_detail_comments;
        public ImageView photo_detail_comment_image;
        public TextView photo_detail_comments_text;
	}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			 ViewHolder holder = new ViewHolder();
	         
	         if (convertView == null) {
	         	Log.w(this.toString(), "inflating resource: " + R.layout.event_item_detail);
	                     convertView = this.inflater.inflate(R.layout.event_item_detail, null);
	                     
	                     holder.header_view = (RelativeLayout) convertView;
	                     //holder.comment_list = (ListView) convertView.findViewById(R.id.comment_list_view);
	                     holder.photo_detail_thumbnail = (ImageView) holder.header_view.findViewById(R.id.photo_detail_thumbnail);
	                     holder.photo_detail_user_row = (RelativeLayout) holder.header_view.findViewById(R.id.photo_detail_user_row);
	                     holder.photo_detail_name_text = (TextView) holder.header_view.findViewById(R.id.photo_detail_name_text);
	                     holder.photo_detail_story = (RelativeLayout) holder.header_view.findViewById(R.id.photo_detail_story);
	                     holder.photo_detail_picture = (ImageView) holder.header_view.findViewById(R.id.photo_detail_picture);
	                     holder.photo_detail_story_text = (TextView) holder.header_view.findViewById(R.id.photo_detail_story_text);
	                     holder.photo_detail_time_text = (TextView) holder.header_view.findViewById(R.id.photo_detail_time_text);
	                     //holder.photo_detail_spinner = (ProgressBar) holder.header_view.findViewById(R.id.photo_detail_spinner);
	                     holder.photo_detail_upload_text = (TextView) holder.header_view.findViewById(R.id.photo_detail_upload_text);
	                     holder.photo_detail_thumbnail_bg = (ImageView) holder.header_view.findViewById(R.id.photo_detail_thumbnail_bg);
	                     holder.photo_detail_likes = holder.header_view.findViewById(R.id.photo_detail_likes);
	                     holder.photo_detail_likes_text = (TextView) holder.header_view.findViewById(R.id.photo_detail_likes_text);
	                     holder.photo_detail_thumb_image = (ImageView) holder.header_view.findViewById(R.id.photo_detail_thumb_image);
	                     holder.photo_detail_like_button_text = (TextView) holder.header_view.findViewById(R.id.photo_detail_like_button_text);
	                     holder.touch_overlay = holder.header_view.findViewById(R.id.photo_detail_touch_overlay);
	                     holder.photo_detail_comment_button_text = (TextView) holder.header_view.findViewById(R.id.photo_detail_comment_button_text);
	                     holder.photo_detail_caption = (TextView) holder.header_view.findViewById(R.id.photo_detail_description);
	                     //holder.photo_detail_like_button = (ImageButton) holder.header_view.findViewById(R.id.photo_detail_like_button);
	                     //holder.photo_detail_download_progress = (ProgressBar) holder.header_view.findViewById(R.id.photo_detail_download_progress);
	                     holder.photo_detail_comments = holder.header_view.findViewById(R.id.photo_detail_comments);
	                     holder.photo_detail_comments_text = (TextView) holder.header_view.findViewById(R.id.photo_detail_comments_text);
	                     holder.photo_detail_comment_image = (ImageView) holder.header_view.findViewById(R.id.photo_detail_comment_image);
	                     holder.photo_detail_comment_bar = (LinearLayout) holder.header_view.findViewById(R.id.photo_detail_comment_bar);
	                     
	                     convertView.setTag(holder);
	         			
	         } else {
	                 holder = (ViewHolder) convertView.getTag();
	         }
	
	         this.bindData(holder, position);
	    
	         
	         
	         //int pixels = DrawableCache.convertDpToPixel(5);
	         
	         //convertView.setPadding(0, pixels, 0, 0);
	         //convertView.findViewById(R.id.photo_container).setPadding(pixels, (int) (GlobalVariables.DISPLAY_WIDTH * .075), pixels, pixels);
	         //holder.photo_detail_comment_bar.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.event_item_comment_bar, GlobalVariables.DISPLAY_WIDTH - 2 * pixels).getDrawable());
	         // bind the data to the view object
	         return convertView;
	 }
 
	 /**
	  * Bind the provided data to the view.
	  * This is the only method not required by base adapter.
	  */
	 public void bindData(final ViewHolder holder, int position) {
	         
		 Log.d("PagedView", "position: " + position);
		 
	 	final PhotoDetail photo = this.mData.get(position);
	
	 	ArrayList<Comment> comments = new ArrayList<Comment>(dbWrapper.getComments(photo.photo.id));
	 	
	 	holder.photo_detail_like_button_text.setTag(photo);
	 	holder.photo_detail_comment_button_text.setTag(photo);
	 	
	 	/*Collections.sort(comments);
	 	
	 	holder.photo_detail_like_button.setTag(photo);
	 	
	 	holder.comment_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

			public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
				
				final Comment comment = (Comment) adapter.getItemAtPosition(position);
				
				if (comment.user_id == SharePref.getIntPref(context, SharePref.USER_ID))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage("Delete your comment?")
					.setCancelable(true)
					.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
							if (comment.id < 0)
							{
								dbWrapper.deleteComment(comment);
								
								notifyDataSetChanged();
							}
							else
							{
							
								comment.body = "Deleting...";
								
								dbWrapper.updateComment(comment);
								
								notifyDataSetChanged();
								
								EventServiceBuffer.deleteComment(comment);
							}
							
							dialog.cancel();
						}
					})
					.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							
						}
					});
					
					builder.create().show();
				}
				
				return false;
			}
	 	
	 	});
	 	
	 	if (holder.comment_list.getHeaderViewsCount() == 0)
	 	{
		 	holder.comment_list.addHeaderView(holder.header_view);
	 	}

	 	if (photo.hasReceivedDetail)
	 	{
		 	CommentAdapter adapter = new CommentAdapter(context, R.layout.comment_list_item, comments);
	 		holder.comment_list.setAdapter(adapter);
	 	}
	 	else
	 	{
		 	CommentAdapter adapter = new CommentAdapter(context, R.layout.comment_list_item, new ArrayList<Comment>());
	 		holder.comment_list.setAdapter(adapter);
	 	}*/
	 	
	 	holder.photo_detail_comments_text.setText(String.valueOf(comments.size()));
	 	holder.photo_detail_comments_text.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
	 	
	 	/*if (this.showFirstComment)
	 	{
	 		holder.comment_list.setSelection(1);
	 		this.showFirstComment = false;
	 	}*/
	 	
	 	//holder.photo_detail_comment_icon.setTag(item);
	 	//holder.photo_detail_thumb_icon.setTag(item);
			
			int height = -1;
			
			LocationInfo location = new LocationInfo();
				
			holder.photo_detail_story.setVisibility(View.GONE);
			
			if (photo.photo.id == -1)
			{
				holder.photo_detail_picture.setImageDrawable(DrawableCache.getDrawable(R.drawable.watermark, GlobalVariables.DISPLAY_WIDTH).getDrawable());
				//holder.photo_detail_spinner.setVisibility(View.VISIBLE);
				holder.photo_detail_upload_text.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
				holder.photo_detail_upload_text.setVisibility(View.VISIBLE);
			}
			else
			{
		    	
		    	location = photo.photo.location;
		    	
		    	/*if (!photo.photo.caption.equals(""))
		    	{
		    		holder.touch_overlay.setVisibility(View.VISIBLE);
		    		holder.touch_overlay.setBackgroundDrawable(DrawableCache.getDrawable(R.drawable.photo_detail_overlay, GlobalVariables.DISPLAY_WIDTH).getDrawable());
		    	}
		    	else
		    	{
		    		holder.touch_overlay.setVisibility(View.GONE);
		    	}*/
		    	
		    	holder.touch_overlay.setVisibility(View.GONE);
		    	
		    	//final View touchOverlay = holder.touch_overlay;
		    	
		    	holder.photo_detail_picture.setMaxHeight(SharePref.getIntPref(context, SharePref.DISPLAY_WIDTH));
		    	holder.photo_detail_picture.setMaxWidth(SharePref.getIntPref(context, SharePref.DISPLAY_WIDTH));
		    	
		    	holder.photo_detail_picture.setLayoutParams(
		    			new FrameLayout.LayoutParams(
		    					SharePref.getIntPref(context, SharePref.DISPLAY_WIDTH), 
		    					SharePref.getIntPref(context, SharePref.DISPLAY_WIDTH)));
		    	
		        GlobalVariables.getInstance().downloadImageWithThumbnail(holder.photo_detail_picture, 
		        		photo.photo, 
		        		SharePref.getIntPref(context.getApplicationContext(), SharePref.DISPLAY_WIDTH));
		        
	    		/*GlobalVariables.getInstance().downloadImage(holder.photo_detail_picture, 
	    				GlobalVariables.getInstance().getAWSUrlThumbnail(item.image1), 
	    				imageWidth, false, item.image1.local_store);*/
		        
				holder.photo_detail_caption.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
				holder.photo_detail_caption.setText(photo.photo.caption);
				
				holder.photo_detail_picture.setClickable(true);
				holder.photo_detail_picture.setOnClickListener(new OnClickListener(){

					public void onClick(View v) {
						
						Intent intent = new Intent(context, ZoomActivity.class);
						
						intent.putExtra("Photo", photo.photo);
						
						context.startActivity(intent);
						
					}
				});
				
				//holder.photo_detail_picture.setMinZoom(1f);
		        
		        
				//holder.photo_detail_spinner.setVisibility(View.GONE);
				holder.photo_detail_upload_text.setVisibility(View.GONE);
			}
			
	        holder.photo_detail_picture.setVisibility(View.VISIBLE);
	        
	        /*if (photo.hasReceivedDetail)
	        {
	        	holder.photo_detail_download_progress.setVisibility(View.GONE);
	        }
	        else
	        {
	        	holder.photo_detail_download_progress.setVisibility(View.VISIBLE);
	        }*/
	        
	        DrawableWithHeight drawable = DrawableCache.getDrawable(R.drawable.photo_detail_rect, SharePref.getIntPref(context, SharePref.DISPLAY_WIDTH));
	        
	        //holder.photo_detail_comment_bar.setBackgroundDrawable(drawable.getDrawable());
			
	        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(drawable.getWidth(), drawable.getHeight());
	        
	        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	        
	        holder.photo_detail_comment_bar.setLayoutParams(params);
	        
			setUpStoryPictureHeader(holder, photo.photo, height, location);
	 }
 
	private void setUpStoryPictureHeader(ViewHolder holder, PhotoItem item, int height, LocationInfo location)
	{
		UserInfo photoOwner = null;
		try {
			photoOwner = helper.getUserDao().queryForId(item.user_id);
		} catch (SQLException e) {
			Log.e("DatabaseHelper", "Failed to queryForId for user", e);
		}
		
		DrawableWithHeight background = DrawableCache.getDrawable(R.drawable.photo_detail_rect, GlobalVariables.DISPLAY_WIDTH);
		
		holder.photo_detail_thumbnail.setTag(photoOwner);
		
		//holder.photo_detail_thumbnail_bg.setMaxHeight(background.getHeight());
		//holder.photo_detail_thumbnail_bg.setMaxWidth(background.getHeight());
		
		if (photoOwner != null)
		{
		
			Long facebookID = photoOwner.uid;
			
			GlobalVariables.getInstance().downloadImage(holder.photo_detail_thumbnail, 
					GlobalVariables.getInstance().getFacebookPictureUrlLarge(facebookID), 
					background.getHeight());

			holder.photo_detail_name_text.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
			holder.photo_detail_name_text.setText(photoOwner.name);
			holder.photo_detail_name_text.setTag(photoOwner);
			
			holder.photo_detail_time_text.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
			holder.photo_detail_time_text.setText(formatter.format(item.created_at));
			
			holder.photo_detail_like_button_text.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
			
			//holder.photo_detail_like_button_text.setTag(item);
			
			holder.photo_detail_comment_button_text.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
			
			holder.photo_detail_likes_text.setTypeface(GlobalVariables.getInstance().getGothamLightFont());
			
			Collection<Like> likes = dbWrapper.getLikes(item.id);
			
			//holder.photo_detail_likes.setTag(item);
			
			if (likes.size() > 0)
			{
				//holder.photo_detail_likes.setVisibility(View.VISIBLE);
				holder.photo_detail_likes_text.setText(Integer.toString(likes.size()));
				boolean hasLiked = false;
				for (Like like : likes)
				{
					if (like.user_id == SharePref.getIntPref(context.getApplicationContext(), SharePref.USER_ID))
					{
						hasLiked = true;
					}
				}
				if (hasLiked)
				{
					holder.photo_detail_thumb_image.setPressed(true);
					holder.photo_detail_like_button_text.setText("Unlike");
				}
				else
				{
					holder.photo_detail_thumb_image.setPressed(false);
					holder.photo_detail_like_button_text.setText("Like");
				}
			}
			else
			{
				//holder.photo_detail_likes.setVisibility(View.GONE);
				holder.photo_detail_likes_text.setText(Integer.toString(likes.size()));
				holder.photo_detail_like_button_text.setText("Like");
			}
			
			/*holder.photo_detail_comments_text.setTypeface(GlobalVariables.getInstance().getHelveticaNeueBoldFont());
			if (item.comments.size() > 0)
			{
				holder.photo_detail_comments.setVisibility(View.VISIBLE);
				holder.photo_detail_comments_text.setText(Integer.toString(item.comments.size()));
			}
			else
			{
				holder.photo_detail_comments.setVisibility(View.GONE);
			}*/
			
			//holder.photo_detail_bottom.removeAllViews();
			//holder.photo_detail_bottom.addView(bottomInfo);
		}
	}
}
