package com.vp.loveu.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.vp.loveu.login.bean.LoginUserInfoBean;

/**
 * 消息中心的 sharePerfernce 
 * 用来存储一些 必要的信息
 * 
 * @author tanping
 */
public class MsgSharePreferenceUtil {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private String file = "loveu_info";

	public MsgSharePreferenceUtil(Context context,String fileName) throws Exception {
		if (context == null) {
			throw new Exception();
		}
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			loginInfo = new LoginUserInfoBean(context);
		}
		sp = context.getSharedPreferences(file + fileName + loginInfo.getUid(),
				Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public String getValueForKey(String key) {
		if (key == null) {
			return null;
		}
		return sp.getString(key, null);
	}
	public int getIntValueForKey(String key) {
		if (key == null) {
			return -1;
		}
		return sp.getInt(key, -1);
	}

	public void removeKey(String key) {
		editor.remove(key).commit();
	}

	public void addKey(String key, String value) {
		editor.putString(key, value).commit();
	}
	public void addKey(String key, int value) {
		editor.putInt(key, value).commit();
	}
	 
}
