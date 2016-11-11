package com.vp.loveu.channel.bean;

import org.json.JSONException;
import org.json.JSONObject;

import com.vp.loveu.pay.bean.EnjoyPayBean;

import android.content.Context;
import android.view.View;

/**
 * @项目名称nameloveu1.0
 * @时间2016年3月4日下午3:47:15
 * @功能TODO
 * @作者 mi
 */

public class RewardsBean extends EnjoyPayBean {
	
	String obj;
	
	@Override
	public View createShowView(Context context) {
		return null;
	}

	@Override
	public JSONObject getParams() {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	public void setObj(String obj) {
		this.obj =obj;
	}
}
