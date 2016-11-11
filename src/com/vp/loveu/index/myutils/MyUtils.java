package com.vp.loveu.index.myutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月3日上午10:49:08
 * @功能
 * @作者 mi
 */

public class MyUtils {

	@SuppressLint("SimpleDateFormat")
	public static String dateFromLong(String l) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return simpleDateFormat.format(new Date(Long.valueOf(l) * 1000));
	}

	public static String millisToVideoDuration(long millis) {
		if (millis <= 0) {
			return "0:00";
		}
		int h = (int) ((millis / 1000) / 60 /60) ;
		int m = (int) ((millis / 1000) / 60);
		int s = (int) (millis / 1000 % 60);
		return h == 0 ? (m == 0 ? "0:"+(s < 10 ? "0"+s : s) : m+":"+ (s < 10 ? "0"+s : s)) : h+":"+m+":"+ (s < 10 ? "0"+s : s) ;
	}

	public static boolean isNetword(Context context) {
		ConnectivityManager net = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = net.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public static int[] getScreenWidthAndHeight(Context context) {
		int[] a = new int[2];
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display defaultDisplay = manager.getDefaultDisplay();
		a[0] = defaultDisplay.getWidth();
		a[1] = defaultDisplay.getHeight();
		return a;
	}

	public static Bitmap compressBitmap(String filePaht) {
		Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap decodeFile = BitmapFactory.decodeFile(filePaht, opts);
		opts.inJustDecodeBounds = false;
		int w = opts.outWidth;
		int h = opts.outHeight;
		int ratio = 1;
		if (w > h && w > 480) {
			ratio = (int) ((w * 1.0f / 480) + 0.5f);
		}
		if (h > w && h > 800) {
			ratio = (int) ((h * 1.0f / 800) + 0.5f);
		}
		if (ratio <= 0) {
			ratio = 1;
		}
		opts.inSampleSize = ratio;
		Bitmap bitmap = BitmapFactory.decodeFile(filePaht, opts);
		return bitmap;
	}

	public static Bitmap compressBitmap(Bitmap bit, int width, int height) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bit.compress(CompressFormat.JPEG, 100, out);
		int quality = 90;
		while (out.toByteArray().length / 1000 > 100) {
			out.reset();
			bit.compress(CompressFormat.JPEG, quality, out);
			quality -= 10;
		}
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Bitmap decodeStream = BitmapFactory.decodeStream(in);
		Bitmap createScaledBitmap = Bitmap.createScaledBitmap(decodeStream, width, height, true);
		decodeStream = null;
		return createScaledBitmap;
	}

	public static File createTmpFile(Context context) {

		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			// 已挂载
			File pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			pic = Environment.getExternalStorageDirectory();
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
			String fileName = "multi_image_" + timeStamp + "";
			File tmpFile = new File(pic, fileName + ".jpg");
			return tmpFile;
		} else {
			File cacheDir = context.getCacheDir();
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
			String fileName = "multi_image_" + timeStamp + "";
			File tmpFile = new File(cacheDir, fileName + ".jpg");
			return tmpFile;
		}
	}

	public static String getStringForResID(Context context, int resId) {
		return context.getResources().getString(resId);
	}
}
