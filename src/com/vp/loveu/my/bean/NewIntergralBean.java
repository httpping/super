package com.vp.loveu.my.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vp.loveu.bean.VPBaseBean;

import android.text.TextUtils;

/**
 * @项目名称nameloveu1.0
 * @时间2016年3月1日下午3:35:50
 * @功能TODO
 * @作者 mi
 */

public class NewIntergralBean extends VPBaseBean{
	
	public List<NewIntergralDataBean> data;
	public int code;
	public int is_encrypt;
	public String msg;
	
	public static class NewIntergralDataBean extends VPBaseBean{
		public String create_time;
		public int exp;
		public String remark;
		public int exe_type;
		public int is_retrieved;
		public String url;
		public int type;
		public String titile;
		
		public static NewIntergralDataBean parserJson(String string) throws JSONException {
			JSONObject obj = new JSONObject(string);
			NewIntergralDataBean bean = new NewIntergralDataBean();
			bean.exe_type = obj.optInt("exe_type");
			bean.exp = obj.optInt("exp");
			bean.is_retrieved = obj.optInt("is_retrieved");
			bean.remark = obj.optString("remark");
			bean.url = obj.optString("url");
			bean.create_time = obj.optString("create_time");
			return bean;
		}

		
		
		@Override
		public boolean equals(Object o) {
			if (o != null && o instanceof NewIntergralDataBean) {
				NewIntergralDataBean bean = (NewIntergralDataBean) o;
				return this.url.equals(bean.url);
			}
			return super.equals(o);
		}

		@Override
		public String toString() {
			return "NewIntergralDataBean [create_time=" + create_time + ", exp=" + exp + ", remark=" + remark + ", exe_type=" + exe_type
					+ ", is_retrieved=" + is_retrieved + ", url=" + url + "]";
		}
	}
	
	public static List<NewIntergralDataBean> parserJson(String json){
		List<NewIntergralDataBean> list = null;
		try {
			list = new ArrayList<>();
			JSONObject obj = new JSONObject(json);
			JSONArray tasksArray = obj.getJSONArray("tasks");
			add(list,0,"新手任务");
			for (int i = 0; i < tasksArray.length(); i++) {
				NewIntergralDataBean bean = NewIntergralDataBean.parserJson(tasksArray.getString(i));
				if (!TextUtils.isEmpty(bean.url) && bean.url.contains("loveu://favour_app")) {//给app好评暂时不做  所以
					continue;
				}
				bean.type = 1;
				list.add(bean);
			}
			add(list,0,"积分历史");
			JSONArray jsonArray = obj.getJSONArray("history");
			for (int i = 0; i < jsonArray.length(); i++) {
				NewIntergralDataBean bean = NewIntergralDataBean.parserJson(jsonArray.getString(i));
				bean.type = 2;
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private static void add(List<NewIntergralDataBean> list, int type, String title) {
		NewIntergralDataBean bean = new NewIntergralDataBean();
		bean.type = type;
		bean.titile = title;
		list.add(bean);
	}
}
