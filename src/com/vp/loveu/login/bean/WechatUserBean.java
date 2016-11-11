package com.vp.loveu.login.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.Gson;

/**
 * @author：pzj
 * @date: 2015年12月17日 上午11:12:21
 * @Description:
 */
public class WechatUserBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String openid;
	private String nickname;
	private int sex;
	private String province;
	private String city;
	private String country;
	private String headimgurl;
	private ArrayList<String> privilege;
	private String unionid;
	
	public static WechatUserBean parseJson(String json){
		return new Gson().fromJson(json, WechatUserBean.class);
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public ArrayList<String> getPrivilege() {
		return privilege;
	}

	public void setPrivilege(ArrayList<String> privilege) {
		this.privilege = privilege;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	
	

}
