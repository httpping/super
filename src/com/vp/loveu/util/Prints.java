package com.vp.loveu.util;

/**
 * @ClassName: Prints
 * @Description:打印控制类
 * @author zeus
 * @date 2015-8-6 上午11:24:26
 * 
 */
public class Prints {

	public static final String TAG = "PczyLove";
	public static boolean DEBUG = true;

	public static void v(String tag, String msg) {
		if (DEBUG) {
			android.util.Log.v(tag, msg);
		}
	}

	public static void v(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			android.util.Log.v(tag, msg, tr);
		}
	}

	public static void d(String tag, String msg) {
		if (DEBUG) {
			android.util.Log.d(tag, msg);
		}
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			android.util.Log.d(tag, msg, tr);
		}
	}

	public static void i(String tag, String msg) {
		if (DEBUG) {
			android.util.Log.i(tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			android.util.Log.i(tag, msg, tr);
		}
	}

	public static void w(String tag, String msg) {
		if (DEBUG) {
			android.util.Log.w(tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			android.util.Log.w(tag, msg, tr);
		}
	}

	public static void w(String tag, Throwable tr) {
		if (DEBUG) {
			android.util.Log.w(tag, tr);
		}
	}

	public static void e(String tag, String msg) {
		if (DEBUG) {
			android.util.Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			android.util.Log.e(tag, msg, tr);
		}
	}

	public static void i(String string) {
		android.util.Log.i(TAG, string);
	}
}
