package com.motlee.android;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

public class TempTakePhotoActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.temp_take_photo);
        
        String photoURI = getIntent().getExtras().getString("PhotoPath");
        
		MediaScannerConnection.scanFile(this,
		          new String[] { photoURI }, null,
		          new MediaScannerConnection.OnScanCompletedListener() {
		      public void onScanCompleted(String path, Uri uri) {
		          setResult(RESULT_OK);
		          finish();
		      }
		 });
	}
}
