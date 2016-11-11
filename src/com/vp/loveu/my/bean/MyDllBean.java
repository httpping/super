package com.vp.loveu.my.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;
import com.vp.loveu.bean.VPBaseBean;

/**
 * @author：pzj
 * @date: 2015年12月11日 下午5:12:01
 * @Description:
 */
public class MyDllBean extends VPBaseBean{
	private int id	;
	private String cont	;
	private String create_time	;
	private int view_num	;
	private ArrayList<String> pics;
	private int uid	;
	private String nickname	;
	private String portrait	;
	private int floor_num	;
	private String xmpp_user	;
	
	private int is_fav;
	private int is_like;
	private int fav_num;
	private int like_num;
	private int topic_id;
	
	private String audio	;
	private String audio_title	;
	
	
	public String getAudio() {
		return audio;
	}


	public void setAudio(String audio) {
		this.audio = audio;
	}


	public String getAudio_title() {
		return audio_title;
	}


	public void setAudio_title(String audio_title) {
		this.audio_title = audio_title;
	}


	public static MyDllBean parseJson(String json){
		Gson gson=new Gson();
		return gson.fromJson(json, MyDllBean.class);
	}
	

	public static  ArrayList<MyDllBean> parseArrayJson(JSONArray jsonData){
		ArrayList<MyDllBean> list=new ArrayList<MyDllBean>();
		if(jsonData!=null){
			for(int i=0;i<jsonData.length();i++){
				try {
					String json = jsonData.getString(i);
					MyDllBean bean =parseJson(json);
					if(bean!=null){
						list.add(bean);
					}
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
	public String getCont() {
		return cont;
	}
	public void setCont(String cont) {
		this.cont = cont;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public int getView_num() {
		return view_num;
	}
	public void setView_num(int view_num) {
		this.view_num = view_num;
	}
	public ArrayList<String> getPics() {
		return pics;
	}
	public void setPics(ArrayList<String> pics) {
		this.pics = pics;
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
	public int getFloor_num() {
		return floor_num;
	}
	public void setFloor_num(int floor_num) {
		this.floor_num = floor_num;
	}
	public String getXmpp_user() {
		return xmpp_user;
	}
	public void setXmpp_user(String xmpp_user) {
		this.xmpp_user = xmpp_user;
	}


	public int getIs_fav() {
		return is_fav;
	}


	public void setIs_fav(int is_fav) {
		this.is_fav = is_fav;
	}


	public int getIs_like() {
		return is_like;
	}


	public void setIs_like(int is_like) {
		this.is_like = is_like;
	}


	public int getFav_num() {
		return fav_num;
	}


	public void setFav_num(int fav_num) {
		this.fav_num = fav_num;
	}


	public int getLike_num() {
		return like_num;
	}


	public void setLike_num(int like_num) {
		this.like_num = like_num;
	}


	public int getTopic_id() {
		return topic_id;
	}


	public void setTopic_id(int topic_id) {
		this.topic_id = topic_id;
	}
	
	
	
	
}
