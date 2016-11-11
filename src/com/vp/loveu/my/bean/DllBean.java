package com.vp.loveu.my.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;
import com.vp.loveu.bean.VPBaseBean;

/**
 * @author：pzj
 * @date: 2015年12月9日 下午4:21:36
 * @Description:
 */
public class DllBean extends VPBaseBean{
	
	private int id	;
	private int target_type	;
	private int target_id	;
	private String title	;
	private String create_time	;
	private ArrayList<String> pics;	
	private String summary	;
	private int author_id	;
	private String author_nickname	;
	private String author_portrait	;
	private String publish_time	;
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


	
	
	private int is_fav;
	private int is_like;
	private int fav_num;
	private int like_num;
	private int pid;
	
	private String xmpp_user;
	
	public static DllBean parseJson(String json){
		Gson gson=new Gson();
		return gson.fromJson(json, DllBean.class);
	}
	

	public static  ArrayList<DllBean> parseArrayJson(JSONArray jsonData){
		ArrayList<DllBean> list=new ArrayList<DllBean>();
		if(jsonData!=null){
			for(int i=0;i<jsonData.length();i++){
				try {
					String json = jsonData.getString(i);
					DllBean bean =parseJson(json);
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
	public int getTarget_type() {
		return target_type;
	}
	public void setTarget_type(int target_type) {
		this.target_type = target_type;
	}
	public int getTarget_id() {
		return target_id;
	}
	public void setTarget_id(int target_id) {
		this.target_id = target_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public int getAuthor_id() {
		return author_id;
	}
	public void setAuthor_id(int author_id) {
		this.author_id = author_id;
	}
	public String getAuthor_nickname() {
		return author_nickname;
	}
	public void setAuthor_nickname(String author_nickname) {
		this.author_nickname = author_nickname;
	}
	public String getAuthor_portrait() {
		return author_portrait;
	}
	public void setAuthor_portrait(String author_portrait) {
		this.author_portrait = author_portrait;
	}
	public String getPublish_time() {
		return publish_time;
	}
	public void setPublish_time(String publish_time) {
		this.publish_time = publish_time;
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


	public int getPid() {
		return pid;
	}


	public void setPid(int pid) {
		this.pid = pid;
	}


	public String getXmpp_user() {
		return xmpp_user;
	}


	public void setXmpp_user(String xmpp_user) {
		this.xmpp_user = xmpp_user;
	}


	
}
