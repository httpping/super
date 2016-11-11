package com.vp.loveu.bean;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;

/**
 * @author：pzj
 * @date: 2015-11-13 下午5:41:52
 * @Description:软件基本配置
 */
public class AppconfigBean {
	private String name;

	private String val;

	private String code;

	private int status;
	
	
	
	public static AppconfigBean parseJson(String json) {
		Gson gson=new Gson();
		AppconfigBean bean=gson.fromJson(json, AppconfigBean.class);
		return bean;
	}
	
	public static Map<String, AppconfigBean> createFromJsonArray(String json){
		Map<String, AppconfigBean> appInfoMap=new HashMap<String,AppconfigBean>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				AppconfigBean bean = AppconfigBean
						.parseJson(jsonArray.getString(i));
				if (bean != null) {
					appInfoMap.put(bean.getCode(), bean);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return appInfoMap;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getVal() {
		return this.val;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return this.status;
	}

	@Override
	public String toString() {
		return "AppconfigBean [name=" + name + ", val=" + val + ", code="
				+ code + ", status=" + status + "]";
	}
	
	
}
