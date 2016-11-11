package com.vp.loveu.my.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vp.loveu.bean.VPBaseBean;

/**
 * 历史积分
 * @author tanping
 * 2015-12-8
 */
public class ExpHistoryBean extends VPBaseBean{

	public String remark;
	public int exp;
	public String createTime;
	public static final String REMARK = "remark";
	public static final String EXP = "exp";
	public static final String CREATE_TIME = "create_time";

	public static ExpHistoryBean parseJson(String json) {
		ExpHistoryBean objectBean = new ExpHistoryBean();
		try {
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject.has(REMARK)) {
				objectBean.remark = jsonObject.getString(REMARK);
			}
			if (jsonObject.has(EXP)) {
				objectBean.exp = jsonObject.getInt(EXP);
			}
			if (jsonObject.has(CREATE_TIME)) {
				objectBean.createTime = jsonObject.getString(CREATE_TIME);
			}
		} catch (Exception e) {
			return null;
		}
		return objectBean;
	}

	public static List<ExpHistoryBean> createFromJsonArray(String json) {
		List<ExpHistoryBean> modeBens = new ArrayList<ExpHistoryBean>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				ExpHistoryBean bean = ExpHistoryBean.parseJson(jsonArray
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
