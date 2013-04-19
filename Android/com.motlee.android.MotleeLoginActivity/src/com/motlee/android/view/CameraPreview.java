package com.motlee.android.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.motlee.android.object.SharePref;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private final String TAG = "Preview";

    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
        	Camera.Parameters parameters = getOptimalPreviewSize(mCamera);
        	
        	this.setLayoutParams(new FrameLayout.LayoutParams(parameters.getPreviewSize().height, parameters.getPreviewSize().width));
            
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            
            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            mCamera.setDisplayOrientation(90);
            
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("CameraView", "Error setting camera preview: " + e.getMessage());
        }
     }
    
    public void surfaceDestroyed(SurfaceHolder holder) {
       // empty. Take care of releasing the Camera preview in your activity.
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (mHolder.getSurface() == null){
             // preview surface does not exist
             return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            Camera.Parameters parameters = getOptimalPreviewSize(mCamera);
            
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            
            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            mCamera.setDisplayOrientation(90);
            
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
       } catch (Exception e){
           Log.d("CameraView", "Error starting camera preview: " + e.getMessage());
       }
    }
    public void onPause() {
        mCamera.release();
        mCamera = null;
    }

    private Parameters getOptimalPreviewSize(Camera camera) {

        HashMap<Double, Size> pictureSizes = new HashMap<Double, Size>();
        
        Integer screenWidth = SharePref.getIntPref(getContext(), SharePref.DISPLAY_WIDTH);
        
        for (Size pictureSize : camera.getParameters().getSupportedPictureSizes())
        {
        	Double ratio = (double) pictureSize.width / (double) pictureSize.height;
        	
        	if (pictureSizes.containsKey(ratio))
        	{
        		if (pictureSize.width > pictureSizes.get(ratio).width)
        		{
        			pictureSizes.put(ratio, pictureSize);
        		}
        	}
        	else
        	{
        		if (pictureSize.width >= screenWidth || pictureSize.height >= screenWidth)
        		{
        			pictureSizes.put(ratio, pictureSize);
        		}
        	}
        }
        
        
        if (pictureSizes.size() < 1)
        {
        	Double ratio = (double) camera.getParameters().getPictureSize().width / (double) camera.getParameters().getPictureSize().height;
        	pictureSizes.put(ratio, camera.getParameters().getPictureSize());
        }
        
        //Size pictureSize = mCamera.getParameters().getPictureSize();
        
        Size finalPictureSize = null;
        Size finalPreviewSize = null;
        
        for (Camera.Size size : camera.getParameters().getSupportedPreviewSizes())
        {        	
        	double ratio = (double) size.width / (double) size.height;
        	
        	Log.d("CameraActivity", "Preview: width: " + size.width + ", height: " + size.height + ", ratio: " + ratio);
        	
        	for (Double pictureRatio : pictureSizes.keySet())
        	{
        		Log.d("CameraActivity", "    Picture: width: " + pictureSizes.get(pictureRatio).width + ", height: " + pictureSizes.get(pictureRatio).height + ", ratio: " + pictureRatio);
        		
            	if (ratio < pictureRatio + 0.05 && ratio > pictureRatio - 0.05)
            	{
            		if (finalPreviewSize == null || (finalPreviewSize.width * finalPreviewSize.height < size.width * size.height))
            		{
            			finalPreviewSize = size;
            			finalPictureSize = pictureSizes.get(pictureRatio);
            			break;
            		}
            	}
        	}
        }
        
        if (finalPreviewSize != null && finalPictureSize != null)
        {
        	Parameters params = camera.getParameters();
        	params.setPictureSize(finalPictureSize.width, finalPictureSize.height);
        	params.setPreviewSize(finalPreviewSize.width, finalPreviewSize.height);
        	return params;
        }
        else
        {
        	return camera.getParameters();
        }
    }
}