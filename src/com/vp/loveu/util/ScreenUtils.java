package com.vp.loveu.util;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.util.DisplayMetrics;
import android.util.Log;
/**
 * 
 * 类描述：屏幕信息Bean 封装屏幕的相关信息
 * @author 谭平
 *
 */
public class ScreenUtils {
	public static int MIN_TABLET_SMALLEST_WIDTH = 600;
	public static int bottomVerticalCenter(int height,float areaScale){
		return (int)( (ScreenBean.scrrenHeight*areaScale-height)/2);
	}
 
	public static void initScreen(Activity activity){
		   DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			ScreenBean.screenWidth = dm.widthPixels;
			ScreenBean.scrrenHeight = dm.heightPixels;
			ScreenBean.screenDensity = dm.densityDpi;
			ScreenBean.resources = activity.getResources();
			ScreenBean.displayCoefficient = ScreenBean.getDisplayCoefficient();
			ScreenBean.screenOrientation = ScreenBean.resources.getConfiguration().orientation;
			Log.i("test", "屏幕宽度是："+ScreenBean.screenWidth+"屏幕高度是:"+ScreenBean.scrrenHeight);
			Log.i("test", "屏幕密度是："+ScreenBean.screenDensity+"屏幕密度系数是:"+ScreenBean.displayCoefficient);
			Options opt = new Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			opt.inJustDecodeBounds = false;
			ScreenBean.opt = opt;
			if (currenDeviceIsPhone()) {
				ScreenBean.controlAreaWidth = ScreenBean.screenWidth;
				ScreenBean.dropDownWidth = (int) (ScreenBean.screenWidth*0.85);
				ScreenBean.indexAreaWidth = ScreenBean.screenWidth;
			}else{
				if (isScreenNowPortait()) {
					ScreenBean.dropDownWidth = (int) (ScreenBean.screenWidth*0.6);
				}else if(isScreenNowLandscape()){
					ScreenBean.dropDownWidth = (int) (ScreenBean.screenWidth*0.4);
				}
			}
	}
	
	public static void initTitleHeight(int titleHeight){
			ScreenBean.titleHeight = titleHeight;
	}
	
	public static void initDropDownWidth(int width){
		ScreenBean.dropDownWidth = width;
	}
	
	public static void initControlAreaWidth(int areaWidth){
			ScreenBean.controlAreaWidth = areaWidth;
	}
	public static int getScaleScreenWidth(float scale){
		return (int)(ScreenBean.screenWidth*scale);
	}
	public static int getScaleScreenHeight(float scale){
		return (int)(ScreenBean.scrrenHeight*scale);
	}
	
	public static int getScaleScreenMin(float scale){
			return ScreenBean.screenWidth>ScreenBean.scrrenHeight?getScaleScreenHeight(scale):getScaleScreenWidth(scale);
	}
	
	public static void setOrientationByType(Activity activity){
		if (currenDeviceIsPhone()) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}else{
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		}
	}
	public static boolean currenDeviceIsPhone(){
//		float screenMinWidth = ((float)(ScreenBean.screenWidth > ScreenBean.scrrenHeight?ScreenBean.scrrenHeight:ScreenBean.screenWidth ))/ScreenBean.displayCoefficient;
//		Log.i("test", "屏幕最小宽:"+screenMinWidth);
		if (((float)(ScreenBean.screenWidth > ScreenBean.scrrenHeight?ScreenBean.scrrenHeight:ScreenBean.screenWidth ))/ScreenBean.displayCoefficient>=600) {
			return false;
		}else{
			return true;
		}
	}
	public static boolean isScreenNowPortait(){
//		long startTime = System.currentTimeMillis();
//		Log.i("test", "开始系统时间："+startTime);
		if (ScreenBean.resources.getConfiguration().orientation  == Configuration.ORIENTATION_PORTRAIT) {
//			long endTime = System.currentTimeMillis();
//			Log.i("test", "终止系统时间："+endTime);
//			Log.i("test", "时间差："+(endTime - startTime));
			return true;
		}
		return false;
	}
	
	public static boolean isScreenNowLandscape(){
		if (ScreenBean.resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		}
		return false;
	}
	public static int getDimenPixels(int dimen){
		//new Throwable().printStackTrace();
		try {
			return ScreenBean.resources.getDimensionPixelSize(dimen);	
		} catch (Exception e) {
			return 1;
		}
		
	}
	


}
