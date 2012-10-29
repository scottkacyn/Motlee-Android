package com.motlee.android;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.motlee.android.adapter.CurrentEventWheelAdapter;
import com.motlee.android.fragment.TakePhotoFragment;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class TakePhotoActivity extends BaseMotleeActivity {

	public static boolean mTakenPhoto;
	
	public static final int TAKE_PHOTO = 10;
	public static final int GET_PHOTO_LIBRARY = 11;
	
	private static final int ACTION_TAKE_PHOTO = 1;
	private static final int PIC_CROP = 2;
	private static final int ACTION_GET_PHOTO = 3;
	
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final String TAKEN_PHOTO = "TakenPhoto";
	
	private boolean recropping = false;
	
	private String mCurrentPhotoPath;
	private Uri picUri;
	
	private static final String CAMERA_DIR = "/dcim/";
	private static final String TAKE_PHOTO_FRAGMENT = "TakePhotoFragment";
	
	private Handler mHandler;
	private ProgressDialog progressDialog;
	
	private CurrentEventWheelAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
        	mTakenPhoto = savedInstanceState.getBoolean(TAKEN_PHOTO);
        } else {
            mTakenPhoto = false;
        }
        
        
        setContentView(R.layout.main);
        
        findViewById(R.id.menu_buttons).setVisibility(View.GONE);
        
        Intent intent = getIntent();
        
        int actionToTake = intent.getIntExtra("Action", TAKE_PHOTO);
        
        mHandler = new Handler();
        
        if (!mTakenPhoto && actionToTake == TAKE_PHOTO)
        {
        	mTakenPhoto = true;
        	
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	
			File f = null;
			
			try 
			{
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
				
		    startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO);
        }
        else if (!mTakenPhoto && actionToTake == GET_PHOTO_LIBRARY)
        {
        	mTakenPhoto = true;
        	
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, ACTION_GET_PHOTO);
        }
        	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ACTION_TAKE_PHOTO: {
			if (resultCode == RESULT_OK) {
				
				picUri = data.getData();
				
				performCrop();
			}
			break;
		} // ACTION_TAKE_PHOTO_B
		case ACTION_GET_PHOTO: {
			if (resultCode == RESULT_OK) {
				
				picUri = data.getData();
				
				performCrop();
			}
			break;
		}
		case PIC_CROP :
		{
			handleCameraPic();
			
			Bundle extras = data.getExtras();
			System.gc();
			//get the cropped bitmap
			final Bitmap thePic = extras.getParcelable("data");
			
			mHandler.post(new Runnable() {
			    
			    public void run() {
			    	setUpFragmentWithPicture(thePic);
			    	mTakenPhoto = false;
			    }
			});
			break;
		}
		} // switch
	}
	
	public void onRecropPicture(View view)
	{
		this.recropping = true;
		
		performCrop();
	}
	
	private void setUpFragmentWithPicture(final Bitmap thePic)
	{
		if (recropping)
		{
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			
			TakePhotoFragment takePhotoFragment = new TakePhotoFragment();
			takePhotoFragment.setHeaderView(findViewById(R.id.header));
			takePhotoFragment.setScrollWheelAdapter(mAdapter);
			takePhotoFragment.setBitmap(thePic);
			
			ft.replace(R.id.fragment_content, takePhotoFragment, TAKE_PHOTO_FRAGMENT)
			.commit();
			
			recropping = false;
		}
		else
		{
			final Activity activity = this;
			
			EventServiceBuffer.setEventDetailListener(new UpdatedEventDetailListener(){
	
				public void myEventOccurred(UpdatedEventDetailEvent evt) {
					
					Integer[] eventIDs = evt.getEventIds().toArray(new Integer[evt.getEventIds().size()]);
					
					FragmentManager fm = getSupportFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					
					mAdapter = new CurrentEventWheelAdapter(activity, eventIDs);
					
					TakePhotoFragment takePhotoFragment = new TakePhotoFragment();
					takePhotoFragment.setHeaderView(activity.findViewById(R.id.header));
					takePhotoFragment.setScrollWheelAdapter(mAdapter);
					takePhotoFragment.setBitmap(thePic);
					
					ft.add(R.id.fragment_content, takePhotoFragment, TAKE_PHOTO_FRAGMENT)
					.commit();
					
					progressDialog.dismiss();
				}
			});
			
			EventServiceBuffer.getEventsFromService(EventServiceBuffer.MY_EVENTS);
			
	        progressDialog = ProgressDialog.show(TakePhotoActivity.this, "", "Loading");
		}
	}
	
	private void handleCameraPic() {

		EventServiceBuffer.getInstance(this);	
		
		if (mCurrentPhotoPath != null) {
			galleryAddPic();
			mCurrentPhotoPath = null;
		}
	}
	
	private void galleryAddPic() 
	{
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}
	
	/*
	 * createImageFile as a temporary placeholder for our taken image
	 */
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private File getAlbumDir() 
	{
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) 
		{
			
			storageDir = getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}
	
	/* Photo album for this application */
	private String getAlbumName() 
	{
		return getString(R.string.album_name);
	}
	
	private File setUpPhotoFile() throws IOException 
	{
		
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		
		return f;
	}
	
	private File getAlbumStorageDir(String albumName) 
	{
		return new File (
				Environment.getExternalStorageDirectory()
				+ CAMERA_DIR
				+ albumName
		);
	}
	
    /**
     * Helper method to carry out crop operation
     */
    private void performCrop(){
    	//take care of exceptions
    	try {
    		//call the standard crop action intent (the user device may not support it)
	    	Intent cropIntent = new Intent("com.android.camera.action.CROP"); 
	    	//indicate image type and Uri
	    	cropIntent.setDataAndType(picUri, "image/*");
	    	//set crop properties
	    	cropIntent.putExtra("crop", "true");
	    	//indicate aspect of desired crop
	    	cropIntent.putExtra("aspectX", 1);
	    	cropIntent.putExtra("aspectY", 1);
	    	//indicate output X and Y
	    	cropIntent.putExtra("outputX", GlobalVariables.getInstance().getDisplayWidth());
	    	cropIntent.putExtra("outputY", GlobalVariables.getInstance().getDisplayWidth());
	    	//retrieve data on return
	    	cropIntent.putExtra("return-data", true);
	    	//start the activity - we handle returning in onActivityResult
	        startActivityForResult(cropIntent, PIC_CROP);  
    	}
    	//respond to users whose devices do not support the crop action
    	catch(ActivityNotFoundException anfe){
    		//display an error message
    		String errorMessage = "Whoops - your device doesn't support the crop action!";
    		Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
    		toast.show();
    	}
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putBoolean(TAKEN_PHOTO, mTakenPhoto);
        
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
       
        // Restore state members from saved instance
        mTakenPhoto = savedInstanceState.getBoolean(TAKEN_PHOTO);
    }
}