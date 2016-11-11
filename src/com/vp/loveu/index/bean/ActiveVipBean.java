package com.vp.loveu.index.bean;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.vp.loveu.index.bean.ActivityDetailBean.ActivityDetailData;
import com.vp.loveu.index.widget.IndexActiveShowsRelativeLayout;
import com.vp.loveu.pay.bean.PayBindViewBean;
import com.vp.loveu.util.LoginStatus;

import android.content.Context;
import android.view.View;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月11日下午1:50:31
 * @功能 报名活动的支付界面
 * @作者 mi
 */


public class ActiveVipBean extends PayBindViewBean {

	public ActivityDetailData bean;

	@Override
	public View createShowView(Context context) {
		IndexActiveShowsRelativeLayout view = new IndexActiveShowsRelativeLayout(context);
		view.setData(bean);
		view.setEnabled(false);//设置不可点击
		return view;
	}

	@Override
	public JSONObject getParams() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("uid", LoginStatus.getLoginInfo().getUid());
			obj.put("id", bean.id);
			obj.put("price", LoginStatus.getLoginInfo().getSex() == 1 ? bean.male_price : bean.female_price);// 有男性用户的价格
																											// 和女性的价格区分
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public ActivityDetailData getBean() {
		return bean;
	}
	
	
}
