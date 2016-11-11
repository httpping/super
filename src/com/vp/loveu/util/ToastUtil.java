package com.vp.loveu.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastUtil {
    private static final String TAG = "ToastUtil";
    private static Toast mToast = null;

    public static void showToast(final Context context, final String text, final int duration) {
	VPLog.d(TAG, "showToast: " + context + " " + text);

	if (text == null) {
	    return;
	}
	Runnable toastRunnable = new Runnable() {
	    @Override
	    public void run() {
		if (mToast == null) {
		    mToast = Toast.makeText(context, text, duration);
		} else {
		    mToast.setText(text);
		    mToast.setDuration(duration);
		}

		mToast.show();
	    }
	};
	if (context instanceof Activity) {
	    final Activity activity = (Activity) context;
	    activity.runOnUiThread(toastRunnable);
	} else {
	    Handler handler = new Handler(context.getMainLooper());
	    handler.post(toastRunnable);
	}

    }
    
    public static void showToast(final Context context, final String text, final int duration,final int gravity) {
    	VPLog.d(TAG, "showToast: " + context + " " + text);

    	if (text == null) {
    	    return;
    	}
    	Runnable toastRunnable = new Runnable() {
    	    @Override
    	    public void run() {
    		//if (mToast == null) {
    		    mToast = Toast.makeText(context, text, duration);
    	//	} else {
    		    mToast.setText(text);
    		    mToast.setDuration(duration);
    		//}
    		    mToast.setGravity(gravity, 0, 0);

    		mToast.show();
    	    }
    	};
    	if (context instanceof Activity) {
    	    final Activity activity = (Activity) context;
    	    activity.runOnUiThread(toastRunnable);
    	} else {
    	    Handler handler = new Handler(context.getMainLooper());
    	    handler.post(toastRunnable);
    	}

        }
}
