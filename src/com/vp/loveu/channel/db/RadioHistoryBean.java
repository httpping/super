package com.vp.loveu.channel.db;
/**
 * @author：pzj
 * @date: 2015年12月2日 下午3:21:45
 * @Description:
 */
public class RadioHistoryBean {
	
	private int _id;
	private int uid;
	private int rid;
	private String name;
	private int user_num;
	private int ruid;
	private String nickname;
	private String url;
	private int totalPosition;
	private int currentPosition;
	private int uploadtoserver;
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
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
	public int getRuid() {
		return ruid;
	}
	public void setRuid(int ruid) {
		this.ruid = ruid;
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
	public int getTotalPosition() {
		return totalPosition;
	}
	public void setTotalPosition(int totalPosition) {
		this.totalPosition = totalPosition;
	}
	public int getCurrentPosition() {
		return currentPosition;
	}
	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}
	public int getUploadtoserver() {
		return uploadtoserver;
	}
	public void setUploadtoserver(int uploadtoserver) {
		this.uploadtoserver = uploadtoserver;
	}
	
	

}
