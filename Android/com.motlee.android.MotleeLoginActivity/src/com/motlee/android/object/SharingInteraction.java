package com.motlee.android.object;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.flurry.android.FlurryAgent;
import com.motlee.android.BaseFacebookActivity;
import com.motlee.android.BaseMotleeActivity;
import com.motlee.android.R;
import com.motlee.android.adapter.ShareIntentListAdapter;

public class SharingInteraction {

	private static ShareIntentListAdapter adapter;
	
	public static void postToFacebook(String body, Uri uri, final Context context, Session session)
	{
		postToFacebook(body, uri, null, context, session);
	}
	
	public static void postToFacebook(String body, Uri uri, String url, final Context context, Session session)
	{
        if (uri != null)
        {
	        try {
	        	
	        	Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
	        	
				Request request = Request.newUploadPhotoRequest(session, bitmap, new Request.Callback() {

            		public void onCompleted(Response response) {
            			
            			FacebookRequestError error = response.getError();
            			if (error != null) {
            				Toast.makeText(context, "Facebook post failed, try again :(",
            						Toast.LENGTH_LONG).show();
            			} 
            			else 
            			{
            				Toast.makeText(context, "Facebook post succeeded!",
            						Toast.LENGTH_SHORT).show();
            			}
            		}
                });
	        	
				Bundle params = request.getParameters();
				params.putString("message", body);
				
				request.setParameters(params);
				
				RequestAsyncTask task = new RequestAsyncTask(request);
		        task.execute();
				
			} catch (FileNotFoundException e) {
				Log.d("Facebook", "filenotf0unc", e);
			}
	        catch (IOException e)
	        {
	        	Log.d("Facebook", "ioexception", e);
	        }
        }
        else
        {
        	Bundle postParams = new Bundle();
        	postParams.putString("message", body);
        	
        	Request request = new Request(session, "me/feed", postParams, 
                    HttpMethod.POST, new Request.Callback() {

                		public void onCompleted(Response response) {
                			
                			FacebookRequestError error = response.getError();
                			if (error != null) {
                				Toast.makeText(context, "Facebook post failed, try again :(",
                						Toast.LENGTH_LONG).show();
                			} 
                			else 
                			{
                				Toast.makeText(context, "Facebook post succeeded!",
                						Toast.LENGTH_SHORT).show();
                			}
                		}
                    });

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
        }
	}
	
	public static boolean share(final String subject, final String body, final Uri uri, final BaseFacebookActivity activity) 
	{
		Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
		if (uri != null)
		{
			sendIntent.setType("image/jpeg");
		}
		else
		{
			sendIntent.setType("text/plain");
		}
		List activities = activity.getPackageManager().queryIntentActivities(sendIntent, 0);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Share with...");
		adapter = new ShareIntentListAdapter(activity, R.layout.share_list_item, ShareIntentListAdapter.sortSharingOptions(activities.toArray(), activity));
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				ResolveInfo info = (ResolveInfo) adapter.getItem(which);
				if(info.activityInfo.packageName.contains("facebook")) {
					
					FlurryAgent.logEvent("SharingToFacebook");
					
					activity.shareEventOnFacebook(body, uri);
					adapter.clearContext();
					adapter = null;
					dialog.cancel();
				} else {
					
					FlurryAgent.logEvent("SharingTo" + info.activityInfo.applicationInfo.loadLabel(activity.getPackageManager()).toString());
					
					Intent intent = new Intent(android.content.Intent.ACTION_SEND);
					intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
					if (uri != null)
					{
						intent.setType("image/jpeg");
						intent.putExtra(Intent.EXTRA_STREAM, uri);
					}
					else
					{
						intent.setType("text/plain");
					}
					intent.putExtra(Intent.EXTRA_SUBJECT, subject);
					intent.putExtra(Intent.EXTRA_TEXT, body);

					activity.startActivity(intent);
					
					adapter.clearContext();
					adapter = null;
					dialog.cancel();
				}
			}
		});
		builder.setOnCancelListener(new OnCancelListener(){

			public void onCancel(DialogInterface dialog) {
				
				if (adapter != null)
				{
					adapter.clearContext();
					adapter = null;
				}
				
			}
			
		});
		builder.create().show();
		return true;
	}
}
