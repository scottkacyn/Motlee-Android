package com.motlee.android.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.motlee.android.object.GlobalVariables;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadImage extends IntentService {

	private static String TAG = "DownloadImage";
	
	private Handler handler = new Handler();
	
	public DownloadImage(String name) {
		super(name);
	}

	public DownloadImage()
	{
		super(TAG);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		String pictureURL = intent.getStringExtra("PictureURL");
		
		try {
			//set the download URL, a url that points to a file on the internet
			//this is the file to be downloaded
			URL url = new URL(pictureURL);

			HttpGet httpRequest = null;

            httpRequest = new HttpGet(url.toURI());

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);

            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            InputStream input = bufHttpEntity.getContent();

            File file = GlobalVariables.createImageFile(getApplicationContext());
            
			//this will be used to write the downloaded data into the file we created
			FileOutputStream fileOutput = new FileOutputStream(file);

			//this is the total size of the file
			long totalSize = bufHttpEntity.getContentLength();
			//variable to store total downloaded bytes
			int downloadedSize = 0;

			//create a buffer...
			byte[] buffer = new byte[1024];
			int bufferLength = 0; //used to store a temporary size of the buffer

			//now, read through the input buffer and write the contents to the file
			while ( (bufferLength = input.read(buffer)) > 0 ) {
				//add the data in the buffer to the file in the file output stream (the file on the sd card
				fileOutput.write(buffer, 0, bufferLength);
				//add up the size so we know how much is downloaded
				downloadedSize += bufferLength;
				//this is where you would do something to report the prgress, like this maybe
				//updateProgress(downloadedSize, totalSize);

			}
			//close the output stream when done
			
			handler.post(new Runnable(){

				public void run() {
					Toast toast = Toast.makeText(getApplicationContext(), "Finished downloading!", Toast.LENGTH_SHORT);
					TextView v = (TextView)toast.getView().findViewById(android.R.id.message);
			    	if( v != null) v.setGravity(Gravity.CENTER);
			    	toast.show();
			    	
			    	sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
				}
			
			});
			
			fileOutput.close();

		//catch some possible errors...
		} catch (MalformedURLException e) {
			Log.e("DownloadImage", "Failed to parse URL Exception");
			handler.post(new Runnable(){

				public void run() {
					Toast.makeText(getApplicationContext(), "Failed to download photo", 2000).show();
				}
			
			});

		} catch (IOException e) {
			Log.e("DownloadImage", "IO Exception");
			handler.post(new Runnable(){

				public void run() {
					Toast.makeText(getApplicationContext(), "Failed to download photo", 2000).show();
				}
			
			});
		} catch (URISyntaxException e)
		{
			Log.e("DownloadImage", "URISyntaxException");
			handler.post(new Runnable(){

				public void run() {
					Toast.makeText(getApplicationContext(), "Failed to download photo", 2000).show();
				}
			
			});
		}
		// see http://androidsnippets.com/download-an-http-file-to-sdcard-with-progress-notification

	}

}
