package com.vp.loveu.util;


import android.content.res.Resources;
import android.graphics.BitmapFactory;

/**
 * 类描述：屏幕信息Bean 封装屏幕的相关信息
 * @author 谭平
 *
 */
public class ScreenBean {
	public static int screenWidth;//屏幕宽
	public static int scrrenHeight;//屏幕高
	public static  BitmapFactory.Options opt;//图片解析设置
	public static Resources resources;
	public static int screenDensity;//屏幕密度
	public static float displayCoefficient;//dpi系数
	public static int controlAreaWidth;
	public static int titleHeight;
	public static int dropDownWidth;
	public static final int DEVICE_TYPE_PHONE  = 3;
	public static final int DEVICE_TYPE_TABLET = 4;
	public static int curDeviceType;
	public static int screenOrientation;
	public static int indexAreaWidth;
	public static float getDisplayCoefficient() {
		switch (screenDensity) {
		case 80:
			return 0.5f;
		case 120:
			return 0.75f;
		case 160:
			return 1.0f;
		case 240:
			return 1.5f;
		case 320:
			return 2.0f;
		case 400:
			return 2.5f;
		case 480:
			return 3.0f;
		default:
			return 1.0f;
		}
	}
}
