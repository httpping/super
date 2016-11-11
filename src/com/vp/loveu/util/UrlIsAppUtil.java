package com.vp.loveu.util;

import java.io.Serializable;

import android.net.Uri;
import android.text.TextUtils;

public class UrlIsAppUtil implements Serializable {

	
	public static String appendAppIsParam(String url){
		
		if (TextUtils.isEmpty(url)) {
			return url;
		}
		int size =0;
		try {
			Uri uri = Uri.parse(url);
			size = uri.getQueryParameterNames().size();	
			
			if ( TextUtils.isEmpty(uri.getQueryParameter("app_is_installed")) ) {
				if (size == 0) {//meiyou canshu 
					url +="?app_is_installed=1";
				}else {
					url +="&app_is_installed=1";
				}
			}
		} catch (Exception e) {
		}
		
		/*if (size == 0) {//meiyou canshu 
			url +="?app_is_installed=1";
		}else {
			url +="&app_is_installed=1";
		}*/
		
		return url ;
	}
}
