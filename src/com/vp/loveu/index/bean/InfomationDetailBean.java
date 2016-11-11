package com.vp.loveu.index.bean;

import com.google.gson.Gson;
import com.vp.loveu.bean.VPBaseBean;

/**
 * @author：pzj
 * @date: 2015年12月11日 上午11:56:30
 * @Description:
 */
public class InfomationDetailBean extends VPBaseBean{
	
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
	private int fav_num	;
	private int like_num	;
	private int is_fav	;
	private int is_like	;
	private int type	;
	
	public static InfomationDetailBean parseJson(String json){
		return new Gson().fromJson(json, InfomationDetailBean.class);
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	

}
