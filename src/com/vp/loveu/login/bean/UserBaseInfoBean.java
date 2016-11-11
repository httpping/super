package com.vp.loveu.login.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author：pzj
 * @date: 2015年11月19日 下午5:01:53
 * @Description:
 */
public class UserBaseInfoBean {

	private int uid;
	private String name;
	private String portrait;

	public static final String UID = "uid";
	public static final String NICKNAME = "nickname";
	public static final String PORTRAIT = "portrait";

	public static UserBaseInfoBean parseJson(String json) {
		UserBaseInfoBean objectBean = new UserBaseInfoBean();
		try {
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject.has(UID)) {
				objectBean.uid = jsonObject.getInt(UID);
			}
			if (jsonObject.has(NICKNAME)) {
				objectBean.name = jsonObject.getString(NICKNAME);
			}
			if (jsonObject.has(PORTRAIT)) {
				objectBean.portrait = jsonObject.getString(PORTRAIT);
			}
		} catch (Exception e) {
			return null;
		}
		return objectBean;
	}

	public static List<UserBaseInfoBean> createFromJsonArray(String json) {
		List<UserBaseInfoBean> modeBens = new ArrayList<UserBaseInfoBean>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				UserBaseInfoBean bean = UserBaseInfoBean.parseJson(jsonArray
						.getString(i));
				if (bean != null) {
					modeBens.add(bean);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return modeBens;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

}
