package com.vp.loveu.discover.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;

/**
 * @author：pzj
 * @date: 2015年11月23日 上午11:38:55
 * @Description:
 */
public class PuaCourseDetailBean {
	private int id	;
	private String title	;
	private String pic	;
	private String small_pic	;
	private String update_time	;
	private int uid	;
	private String nickname	;
	private String portrait	;
	private int praise_num	;
	private String summary	;
	private int view_num	;
	private int has_video	;
	
	public static PuaCourseDetailBean parseJson(String json){
		Gson gson=new Gson();
		return gson.fromJson(json, PuaCourseDetailBean.class);
	}
	
	public static ArrayList<PuaCourseDetailBean> parseArrayJson(JSONArray jsonData){
		ArrayList<PuaCourseDetailBean> list=new ArrayList<PuaCourseDetailBean>();
		if(jsonData!=null){
			for(int i=0;i<jsonData.length();i++){
				try {
					String json = jsonData.getString(i);
					PuaCourseDetailBean bean =PuaCourseDetailBean.parseJson(json);
					if(bean!=null)
						list.add(bean);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getSmall_pic() {
		return small_pic;
	}
	public void setSmall_pic(String small_pic) {
		this.small_pic = small_pic;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
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
	public int getPraise_num() {
		return praise_num;
	}
	public void setPraise_num(int praise_num) {
		this.praise_num = praise_num;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public int getView_num() {
		return view_num;
	}
	public void setView_num(int view_num) {
		this.view_num = view_num;
	}

	public int getHas_video() {
		return has_video;
	}

	public void setHas_video(int has_video) {
		this.has_video = has_video;
	}
	
	

}
