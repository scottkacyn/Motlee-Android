package com.motlee.android.object;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Request.Callback;
import com.facebook.Session;
import com.motlee.android.database.DatabaseWrapper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class GetFriendsFromFacebook extends AsyncTask<DatabaseWrapper, ArrayList<Long>, Boolean> {

	private DatabaseWrapper dbWrapper;
	
    private Callback graphUserListCallback = new Callback(){
		
		public void onCompleted(Response response) {
						
			JSONArray users = (JSONArray) response.getGraphObject().getProperty("data");
			try
			{
				ArrayList<Long> uids = new ArrayList<Long>();
				
				for (int i = 0; i < users.length(); i++)
				{
					JSONObject user = users.getJSONObject(i);
					uids.add(Long.valueOf(user.getLong("uid")));
				}
				
				dbWrapper.updateFriendsList(uids);
			}
			catch (JSONException e)
			{
				Log.e(this.toString(), e.getMessage());
			}
		}
    };

	@Override
	protected Boolean doInBackground(DatabaseWrapper... params) {
		
		Session facebookSession = Session.getActiveSession();
		
		dbWrapper = params[0];
		
		if (facebookSession != null && facebookSession.isOpened())
		{			
			List<String> permissions = facebookSession.getPermissions();
			String query = "select name, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) order by name";
			Bundle bundleParams = new Bundle();
			bundleParams.putString("q", query);
			
			Request request = new Request(facebookSession, "/fql", bundleParams, HttpMethod.GET, graphUserListCallback);              

			//Request.executeBatchAsync(request);
		}
		
		return true;
	}
}
