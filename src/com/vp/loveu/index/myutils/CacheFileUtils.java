package com.vp.loveu.index.myutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;

import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.MD5Util;
import com.vp.loveu.util.UIUtils;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月27日下午1:26:37
 * @功能 写缓存的工具类
 * @作者 mi
 */

public class CacheFileUtils {

	/**
	 * @param fileName 文件名 
	 * @param content 内容
	 * void
	 * TODO
	 */
	public static void writeCache(String fileName,String content){
		if (content == null || content.equals("") || fileName == null || fileName.equals("")) {
			return ;
		}
		BufferedWriter bw = null;
		try {
			LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
			if (loginInfo  == null) {
				loginInfo = new LoginUserInfoBean(UIUtils.getContext());
			}
			bw = new BufferedWriter(new FileWriter(new File(UIUtils.getContext().getCacheDir(),MD5Util.MD516(fileName+"_"+loginInfo.getUid()))));
			bw.write(System.currentTimeMillis()+"");
			bw.newLine();
			bw.write(content);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			closeIO(bw);
		}
	}
	
	/**
	 * @param fileName
	 * @return
	 * String
	 * TODO
	 */
	public static String readCache(String fileName){
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo  == null) {
			loginInfo = new LoginUserInfoBean(UIUtils.getContext());
		}
		File file = new File(UIUtils.getContext().getCacheDir(),MD5Util.MD516(fileName+"_"+loginInfo.getUid()));
		if (!file.exists()) {
			return null;
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			br.readLine();
			return br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			closeIO(br);
		}
		return null;
	}
	
	public static  String getCacheJsonFromLocal(Context context,String fileName){
		AssetManager assets = context.getAssets();
		InputStream open = null;
		String json = "";
		try {
			open = assets.open(fileName);
			byte[] bys = new byte[open.available()];
			open.read(bys);
			json = new String(bys, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			closeIO(open);
		}
		return json;
	}
	
	public static void closeIO(Closeable close) {
		if (close != null) {
			try {
				close.close();
				close = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
