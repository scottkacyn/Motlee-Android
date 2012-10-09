package com.motlee.android.fragment.responder;

import com.motlee.android.service.RubyService;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;

public abstract class ResponderFragment extends Fragment {
    
    private ResultReceiver mReceiver;
    
    // We are going to use a constructor here to make our ResultReceiver,
    // but be careful because Fragments are required to have only zero-arg
    // constructors. Normally you don't want to use constructors at all
    // with Fragments.
    public ResponderFragment() {
        mReceiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultData != null && resultData.containsKey(RubyService.REST_RESULT)) {
                    onRESTResult(resultCode, resultData.getString(RubyService.REST_RESULT));
                }
                else {
                    onRESTResult(resultCode, null);
                }
            }
            
        };
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // This tells our Activity to keep the same instance of this
        // Fragment when the Activity is re-created during lifecycle
        // events. This is what we want because this Fragment should
        // be available to receive results from our RESTService no
        // matter what the Activity is doing.
        setRetainInstance(true);
    }
    
    public ResultReceiver getResultReceiver() {
        return mReceiver;
    }

    // Implementers of this Fragment will handle the result here.
    abstract public void onRESTResult(int code, String result);
}
