package com.vp.loveu.channel.bean;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.vp.loveu.login.bean.UserBaseInfoBean;

/**
 * @author：pzj
 * @date: 2015年11月19日 下午5:35:18
 * @Description:
 */
public class ChannelDetailBean {
	private int id;	
	private String name;	
	private int user_num;	
	private int online_num;	
	private int uid;	
	private String nickname;	
	private String url;	
	private ArrayList<UserBaseInfoBean> users;
	private String cover;
	private String portrait;//作者头像
	
    public String getPortrait() {
		return portrait;
	}


	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}


	public int getReward_able() {
		return reward_able;
	}


	public void setReward_able(int reward_able) {
		this.reward_able = reward_able;
	}


	public int getShare_exp() {
		return share_exp;
	}


	public void setShare_exp(int share_exp) {
		this.share_exp = share_exp;
	}


	private int reward_able;//是否可打赏，1=可以，0=不可以
    private int share_exp;//分享可获得积分
	
	public static ChannelDetailBean parseJson(String json){
		Gson gson=new Gson();
		return gson.fromJson(json, ChannelDetailBean.class);
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getUser_num() {
		return user_num;
	}
	public void setUser_num(int user_num) {
		this.user_num = user_num;
	}
	public int getOnline_num() {
		return online_num;
	}
	public void setOnline_num(int online_num) {
		this.online_num = online_num;
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public ArrayList<UserBaseInfoBean> getUsers() {
		return users;
	}
	public void setUsers(ArrayList<UserBaseInfoBean> users) {
		this.users = users;
	}


	public String getCover() {
		return cover;
	}


	public void setCover(String cover) {
		this.cover = cover;
	}
	
	
}
