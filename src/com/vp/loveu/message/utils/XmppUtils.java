package com.vp.loveu.message.utils;

import com.vp.loveu.service.XmppService;

/**
 * 工具类
 */
public class XmppUtils {
	
	/**
	 * 根据jid获取用户名
	 */
	public static String getJidToUsername(String jid){
		if (jid ==null) {
			return null;
		}
		return jid.split("@")[0];
	}
	
	public static String getUserNameToJid(String username){
		if (username==null) {
			return null;
		}
		
		if (username.indexOf("@")>0) {
			return username;
		}
		return username + "@" + XmppService.SERVICE_NAME+"/"+XmppService.RESOURCE;
	}
	 
}
