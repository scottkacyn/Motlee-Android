package com.motlee.android;

import android.app.Application;
import org.acra.*;
import org.acra.annotation.*;

import com.motlee.android.object.StreamListHandler;

@ReportsCrashes(formKey = "dFB0cXJfY1RESFlwaG03SWFvb1ZfS1E6MQ") 
public class MotleeApplication extends Application {

	private StreamListHandler streamHandler = new StreamListHandler();
	
	public StreamListHandler getStreamListHandler()
	{
		return streamHandler;
	}
	
	@Override
	public void onCreate() {

		// The following line triggers the initialization of ACRA
	    ACRA.init(this);
	    super.onCreate();
	}
}
