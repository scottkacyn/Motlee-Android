package com.motlee.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.motlee.android.object.GlobalVariables;
import com.motlee.android.object.SharePref;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

public class TempTakePhotoActivity extends Activity {
	
	Integer mEventId;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.temp_take_photo);
        
        byte[] data = getIntent().getExtras().getByteArray("Photo");
        mEventId = getIntent().getExtras().getInt("EventId");
        
        File pictureFile = null;
		try {
			pictureFile = GlobalVariables.createImageFile(getApplicationContext());
			Log.d("CameraActivity", "File Name: " + pictureFile.getAbsolutePath());
		} catch (IOException e1) {
			Log.e("CameraActivity", "Error loading image file");
			Toast.makeText(this, "Cannot access your files can't capture photos.", 2000).show();
			finish();
		}
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            
            fos.write(data, 0, data.length);
            fos.close();
            
            Bitmap mBitmap = getBitmap(pictureFile);
            
            Rect r = null;
            
            if (mBitmap.getWidth() > mBitmap.getHeight())
            {
            	r = new Rect(0, 0, mBitmap.getHeight(), mBitmap.getHeight());
            }
            else
            {
            	r = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getWidth());
            }

    		int width = r.width();
    		int height = r.height();

    		// If we are circle cropping, we want alpha channel, which is the
    		// third param here.
    		Bitmap croppedImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    		{
    			Canvas canvas = new Canvas(croppedImage);
    			Rect dstRect = new Rect(0, 0, width, height);
    			canvas.drawBitmap(mBitmap, r, dstRect, null);
    		}

    		saveOutput(croppedImage, pictureFile);
    		
            return;
            
        } catch (FileNotFoundException e) {
			Log.e("CameraActivity", "File Not Found", e);
			Toast.makeText(this, "Cannot access your files can't capture photos.", 2000).show();
			finish();

        } catch (IOException e) {
			Log.e("CameraActivity", "IOException", e);
			Toast.makeText(this, "Error saving photo to phone. Sorry :(", 2000).show();
			finish();
        }
	}
	
	private void saveOutput(Bitmap croppedImage, File file) {
		
		Uri mSaveUri = Uri.fromFile(file);
		
		if (mSaveUri != null) {
			OutputStream outputStream = null;
			try {
				outputStream = getContentResolver().openOutputStream(mSaveUri);
				if (outputStream != null) {
					croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
				}
			} catch (IOException ex) {

			} finally {
		        if (outputStream == null) return;
		        try {
		        	outputStream.close();
		        } catch (Throwable t) {
		            // do nothing
		        }
			}
		} else {

		}
		croppedImage.recycle();
		
		Intent intent = new Intent(this, PreviewImageActivity.class);
		
		intent.putExtra("EventId", mEventId);
		intent.putExtra("PhotoPath", file.getPath());
		
		startActivity(intent);
		
		finish();
	}
	
	private Bitmap getBitmap(File file) {
		Uri uri = Uri.fromFile(file);
		InputStream in = null;
		try {
			int IMAGE_MAX_SIZE = 2048;
			Display display = getWindowManager().getDefaultDisplay();
			if (display != null && display.getWidth() > 0)
			{
				IMAGE_MAX_SIZE = SharePref.getIntPref(getApplicationContext(), SharePref.DISPLAY_HEIGHT);
			}
			
			in = getContentResolver().openInputStream(uri);
			
			//Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;

	        BitmapFactory.decodeStream(in, null, o);
	        in.close();

	        int scale = 1;
	        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
	            scale = (int)Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
	        }

	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize = scale;
	        o2.inPurgeable = true;
	        o2.inInputShareable = true;
	        in = getContentResolver().openInputStream(uri);
	        Bitmap b = BitmapFactory.decodeStream(in, null, o2);
	        in.close();
	        
	        if (b.getWidth() > b.getHeight())
	        {
	        	Matrix matrix = new Matrix();
	            matrix.postScale(1, 1);
	            matrix.postRotate(90);
	        	
	        	b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
	        }
			
			return b;
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			
		}
		return null;
	}
}
