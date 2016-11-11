package com.vp.loveu.discover.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;
import com.vp.loveu.bean.VPBaseBean;

/**
 * @author：pzj
 * @date: 2015年11月23日 下午5:41:02
 * @Description:
 */
public class TutorBean extends VPBaseBean {
	private int uid	;
	private String nickname	;
	private String portrait	;
	private int grade	;
	
	
	public static TutorBean parseJson(String json) {
		Gson gson=new Gson();
		TutorBean bean=gson.fromJson(json, TutorBean.class);
		return bean;
	}
	
	public static List<TutorBean> createFromJsonArray(String json) {
		List<TutorBean> modeBens = new ArrayList<TutorBean>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				TutorBean bean =parseJson(jsonArray.getString(i));
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
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPortrait() {
		return portrait;
	}
	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}
	public int getGrade() {
		return grade;
	}
	public void setGrade(int grade) {
		this.grade = grade;
	}
	
	
}
