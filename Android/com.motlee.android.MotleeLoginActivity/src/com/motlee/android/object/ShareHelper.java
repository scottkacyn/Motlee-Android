package com.motlee.android.object;

import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import com.facebook.android.Facebook;
import com.motlee.android.R;
import com.motlee.android.adapter.ShareIntentListAdapter;

public class ShareHelper {
	
	Context context;
	String subject;
	String body;
	Facebook facebook;
	Uri uri;
	public ShareHelper(Context context, String subject, String body, Uri uri) {
		this.context = context;
		this.subject = subject;
		this.body = body;
		facebook = null;
		this.uri = uri;
	}
	
	public boolean share() {
	Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
	sendIntent.setType("image/jpeg");
	List activities = context.getPackageManager().queryIntentActivities(sendIntent, 0);
	AlertDialog.Builder builder = new AlertDialog.Builder(context);
	builder.setTitle("Share with...");
	final ShareIntentListAdapter adapter = new ShareIntentListAdapter((Activity)context, R.layout.share_list_item, activities.toArray());
	builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
		
		public void onClick(DialogInterface dialog, int which) {
			ResolveInfo info = (ResolveInfo) adapter.getItem(which);
			if(info.activityInfo.packageName.contains("facebook")) {
				//return false;
			} else {
				Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
				intent.setType("image/jpeg");
				intent.putExtra(Intent.EXTRA_SUBJECT, subject);
				intent.putExtra(Intent.EXTRA_TEXT, body);
				intent.putExtra(Intent.EXTRA_STREAM, uri);
				((Activity)context).startActivity(intent);
			}
		}
	});
	builder.create().show();
	return true;
	}
}