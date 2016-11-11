package com.vp.loveu.index.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vp.loveu.bean.VPBaseBean;

/**
 * 附近的人 数据
 * @author tanping
 * 2015-12-7
 */
public class NearPersonBean extends VPBaseBean {

	public int uid;
	public String nickname;
	public String portrait;
	public int sex;
	public float distance;
	public double lat;
	public double lng;
	public static final String UID = "uid";
	public static final String NICKNAME = "nickname";
	public static final String PORTRAIT = "portrait";
	public static final String SEX = "sex";
	public static final String DISTANCE = "distance";
	public static final String LAT = "lat";
	public static final String LNG = "lng";

	public static NearPersonBean parseJson(String json) {
		NearPersonBean objectBean = new NearPersonBean();
		try {
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject.has(UID)) {
				objectBean.uid = jsonObject.getInt(UID);
			}
			if (jsonObject.has(NICKNAME)) {
				objectBean.nickname = jsonObject.getString(NICKNAME);
			}
			if (jsonObject.has(PORTRAIT)) {
				objectBean.portrait = jsonObject.getString(PORTRAIT);
			}
			if (jsonObject.has(SEX)) {
				objectBean.sex = jsonObject.getInt(SEX);
			}
			if (jsonObject.has(DISTANCE)) {
				objectBean.distance = (float) jsonObject.getDouble(DISTANCE);
			}
			if (jsonObject.has(LAT)) {
				objectBean.lat = jsonObject.getDouble(LAT);
			}
			if (jsonObject.has(LNG)) {
				objectBean.lng = jsonObject.getDouble(LNG);
			}
		} catch (Exception e) {
			return null;
		}
		return objectBean;
	}

	public static List<NearPersonBean> createFromJsonArray(String json) {
		List<NearPersonBean> modeBens = new ArrayList<NearPersonBean>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				NearPersonBean bean = NearPersonBean.parseJson(jsonArray
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

}
