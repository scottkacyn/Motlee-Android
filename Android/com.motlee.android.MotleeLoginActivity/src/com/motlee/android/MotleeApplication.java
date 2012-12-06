package com.motlee.android;

import android.app.Application;
import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(formKey = "dGthMjI0Q29YUTg4YmRtTkRqOG1FM1E6MQ") 
public class MotleeApplication extends Application {

	@Override
	  public void onCreate() {
	      super.onCreate();

	      // The following line triggers the initialization of ACRA
	      ACRA.init(this);
	  }
}
