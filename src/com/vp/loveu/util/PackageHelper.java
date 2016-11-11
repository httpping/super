package com.vp.loveu.util;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/*
 * 工具类 检测 应用是否安装
 * eg
 * PACKAGE_APP.QQ
 * 
 * */
public class PackageHelper {
	/*
	 * 工具类 检测 应用是否安装 eg PACKAGE_APP.QQ
	 */
	public static boolean isPackage(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		for (int i = 0; i < pinfo.size(); i++) {
			if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
				return true;
		}
		return false;
	}
	
	

	public static boolean isQQInstalled(Context context) {
		return PackageHelper.isPackage(context, PACKAGE_APP.QQ);
	}

	public static boolean isSinaInstalled(Context context) {
		return PackageHelper.isPackage(context, PACKAGE_APP.SINA);
	}

	public static boolean isWeChatInstalled(Context context) {
		return PackageHelper.isPackage(context, PACKAGE_APP.WX);
	}

	public static boolean isBDInstalled(Context context) {
		return PackageHelper.isPackage(context, PACKAGE_APP.BDMAP);
	}

	public static boolean isGDChatInstalled(Context context) {
		return PackageHelper.isPackage(context, PACKAGE_APP.GDMAP);
	}
	
	public static final class PACKAGE_APP {
		public final static String QQ = "com.tencent.mobileqq";
		public final static String WX = "com.tencent.mm";
		public final static String SINA = "com.sina.weibo";
		public final static String BDMAP = "com.baidu.baidumap";
		public final static String GDMAP = "com.autonavi.minimap";
	    }
}
