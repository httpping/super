package com.vp.loveu.util;

import android.content.ClipboardManager;
import android.content.Context;

/**
 * 简单的复制版功能
 * @author tanping
 * 2015-11-26
 */
public class ClipboardUtil {

	/** 
	* 实现文本复制功能 
	* @param content 
	*/  
	public static void copy(String content, Context context)  
	{ 
	// 得到剪贴板管理器  
	ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
	cmb.setText(content.trim());  
	}  
	/** 
	* 实现粘贴功能 
	* @param context 
	* @return 
	*/  
	public static String paste(Context context)  
	{  
	// 得到剪贴板管理器  
	ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
	return cmb.getText().toString().trim();  
	}  

	
}
