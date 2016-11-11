package com.vp.loveu.index.myutils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.vp.loveu.bean.AreaBean;
import com.vp.loveu.util.FileUtils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年1月23日上午10:31:47
 * @功能TODO
 * @作者 mi
 */

public class ArtUtils implements Runnable {

	public static SoftReference<JSONArray> softJsonArray;
	public static SoftReference<List<AreaBean>> areaBean;
	
	private Context context;
	private OnArtFindCompleteListener listener;
	private String artCode;

	public ArtUtils(Context context) {
		this.context = context;
	}

	public void startFindArtCode(String artCode, OnArtFindCompleteListener listener) {
		this.artCode = artCode;
		this.listener = listener;
		new Thread(this).start();
	}

	@Override
	public void run() {
		if (TextUtils.isEmpty(artCode)) {
			return;
		}
		if (softJsonArray == null) {
			JSONArray locationArae = FileUtils.getLocationArae(context.getAssets());
			softJsonArray = new SoftReference<JSONArray>(locationArae);
		}
		
		if (areaBean == null) {
			List<AreaBean> praseArea = AreaBean.praseAreaCity(softJsonArray.get(), null,0);
			areaBean = new SoftReference<List<AreaBean>>(praseArea);
		}
		
		AreaBean findArea = AreaBean.findArea(areaBean.get(), artCode);
		
		String provinceName ="";
		if (findArea != null && findArea.father != null) {
			provinceName = findArea.father.name;
		}
		String cityName = "";
		if (findArea!=null) {
			cityName = findArea.name;
		}
		listener.complete(provinceName,cityName);
	}

	public interface OnArtFindCompleteListener {
		void complete(String provinceName,String cityName);
	}
}
