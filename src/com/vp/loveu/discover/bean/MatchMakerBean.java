package com.vp.loveu.discover.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;

/**
 * @author：pzj
 * @date: 2015年11月23日 下午4:32:44
 * @Description:
 */
public class MatchMakerBean {
	private int uid;
	private String nickname;
	private String portrait;
	private int user_num;
	private String xmpp_user;
	
	public static MatchMakerBean parseJson(String json){
		Gson gson=new Gson();
		return gson.fromJson(json, MatchMakerBean.class);
	}
	
	public static ArrayList<MatchMakerBean> parseArrayJson(JSONArray jsonData){
		ArrayList<MatchMakerBean> list=new ArrayList<MatchMakerBean>();
		if(jsonData!=null){
			for(int i=0;i<jsonData.length();i++){
				try {
					String json = jsonData.getString(i);
					MatchMakerBean bean =MatchMakerBean.parseJson(json);
					if(bean!=null)
						list.add(bean);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
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
	public int getUser_num() {
		return user_num;
	}
	public void setUser_num(int user_num) {
		this.user_num = user_num;
	}

	public String getXmpp_user() {
		return xmpp_user;
	}

	public void setXmpp_user(String xmpp_user) {
		this.xmpp_user = xmpp_user;
	}
	
	
}
