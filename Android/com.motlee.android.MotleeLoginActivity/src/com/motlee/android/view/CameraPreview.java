package com.motlee.android.view;

import java.io.IOException;

import android.content.Context;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.hardware.Camera;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;

	// Constructor that obtains context and camera
	@SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera) 
	{
		super(context);
		this.mCamera = camera;
		this.mSurfaceHolder = this.getHolder();
		this.mSurfaceHolder.addCallback(this);
		this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		try {
		    mCamera.setPreviewDisplay(surfaceHolder);
		    mCamera.startPreview();
		} catch (IOException e) {
		    // left blank for now
		}
	}

	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		mCamera.stopPreview();
		mCamera.release();
	}

	public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
		// start preview with new settings
		try {
		    mCamera.setPreviewDisplay(surfaceHolder);
		    mCamera.startPreview();
		} catch (Exception e) {
		    // intentionally left blank for a test
		}
	}
}