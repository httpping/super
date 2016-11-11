package com.vp.loveu.channel.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;

/**
 * @author：pzj
 * @date: 2015年12月7日 下午2:11:13
 * @Description:
 */
public class LikeMsgBean {
	private int id;	
	private int type	;
	private int type_id	;
	private int uid	;
	private String nickname	;
	private String portrait	;
	private String summary	;
	private String create_time	;
	private ArrayList<String> pics;
	private boolean isRead;//标志是否已读
	private String audio;
	private String audio_title;
	
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

	public static LikeMsgBean parseJson(String json){
		Gson gson=new Gson();
		return gson.fromJson(json, LikeMsgBean.class);
	}
	
	public static  ArrayList<LikeMsgBean> parseArrayJson(JSONArray jsonData){
		return parseArrayJson(jsonData,0);
	}
	public static  ArrayList<LikeMsgBean> parseArrayJson(JSONArray jsonData,int readId){
		ArrayList<LikeMsgBean> list=new ArrayList<LikeMsgBean>();
		if(jsonData!=null){
			for(int i=0;i<jsonData.length();i++){
				try {
					String json = jsonData.getString(i);
					LikeMsgBean bean =parseJson(json);
					if(bean!=null){
						bean.setRead(bean.getId()<=readId);
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getType_id() {
		return type_id;
	}
	public void setType_id(int type_id) {
		this.type_id = type_id;
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
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public ArrayList<String> getPics() {
		return pics;
	}
	public void setPics(ArrayList<String> pics) {
		this.pics = pics;
	}
	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	
	
}
