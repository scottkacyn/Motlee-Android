package com.motlee.android.object;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class EqualityIntent extends Intent {

	public EqualityIntent() {
		// TODO Auto-generated constructor stub
	}

	public EqualityIntent(Intent o) {
		super(o);
		// TODO Auto-generated constructor stub
	}

	public EqualityIntent(String action) {
		super(action);
		// TODO Auto-generated constructor stub
	}

	public EqualityIntent(String action, Uri uri) {
		super(action, uri);
		// TODO Auto-generated constructor stub
	}

	public EqualityIntent(Context packageContext, Class<?> cls) {
		super(packageContext, cls);
		// TODO Auto-generated constructor stub
	}

	public EqualityIntent(String action, Uri uri, Context packageContext,
			Class<?> cls) {
		super(action, uri, packageContext, cls);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean equals(Object other){
		
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof EqualityIntent))return false;
	    EqualityIntent b = (EqualityIntent)other;
	    EqualityIntent a = this;
	    
        if (a.filterEquals(b)) {
            if (a.getExtras() != null && b.getExtras() != null) {
                // check if the keysets are the same size
                if (a.getExtras().keySet().size() != b.getExtras().keySet().size()) return false;
                // compare all of a's extras to b
                for (String key : a.getExtras().keySet()) {
                    if (!b.getExtras().containsKey(key)) {
                        return false;
                    } else if (!a.getExtras().get(key).equals(b.getExtras().get(key))) {
                        return false;
                    }
                }
                // compare all of b's extras to a
                for (String key : b.getExtras().keySet()) {
                    if (!a.getExtras().containsKey(key)) {
                        return false;
                    } else if (!b.getExtras().get(key).equals(a.getExtras().get(key))) {
                        return false;
                    }
                }
            }
            if (a.getExtras() == null && b.getExtras() == null) return true;
            // either a has extras and b doesn't or b has extras and a doesn't
            return false;
        } else {
            return false;
        }
	}
}
