package com.motlee.android;

import android.app.Application;
import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(formKey = "dFB0cXJfY1RESFlwaG03SWFvb1ZfS1E6MQ") 
public class MotleeApplication extends Application {

	@Override
	  public void onCreate() {

	      // The following line triggers the initialization of ACRA
	      ACRA.init(this);
	      super.onCreate();
	  }
}
