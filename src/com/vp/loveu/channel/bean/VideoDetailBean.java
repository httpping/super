package com.vp.loveu.channel.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.vp.loveu.login.bean.UserBaseInfoBean;
import com.vp.loveu.my.bean.NewIntergralBean.NewIntergralDataBean;

/**
 * @author：pzj
 * @date: 2015年11月24日 下午3:15:46
 * @Description:
 */
public class VideoDetailBean implements Serializable{
	
	private int id	;
	private String name	;
	private String  pic	;
	private String  create_time	;
	private double price	;
	private int video_num	;
	private ArrayList<Video> videos	;
	private int learn_num	;
	private ArrayList<UserBaseInfoBean> users;
	private int uid;
	private String nickname;
	private String portrait;
	private int reward_able;
	private int share_exp;
	
	public int getShare_exp() {
		return share_exp;
	}

	public void setShare_exp(int share_exp) {
		this.share_exp = share_exp;
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

	public int getReward_able() {
		return reward_able;
	}

	public void setReward_able(int reward_able) {
		this.reward_able = reward_able;
	}

	public static VideoDetailBean parseJson(String json){
		return new Gson().fromJson(json, VideoDetailBean.class);
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
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getVideo_num() {
		return video_num;
	}
	public void setVideo_num(int video_num) {
		this.video_num = video_num;
	}
	public ArrayList<Video> getVideos() {
		return videos;
	}
	public void setVideos(ArrayList<Video> videos) {
		this.videos = videos;
	}
	public int getLearn_num() {
		return learn_num;
	}
	public void setLearn_num(int learn_num) {
		this.learn_num = learn_num;
	}
	public ArrayList<UserBaseInfoBean> getUsers() {
		return users;
	}
	public void setUsers(ArrayList<UserBaseInfoBean> users) {
		this.users = users;
	}
	
	
	
	public class Video implements Serializable{
		public long getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		private int id	;
		public NewIntergralDataBean bean;
		private String name	;
		private String url	;
		private int is_learned	;
		private int learn_num	;
		private int degree	;
		private String pic;
		private long  timestamp;
		public int videoId;
		public int vid;
		public int share_exp;
		
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
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public int getIs_learned() {
			return is_learned;
		}
		public void setIs_learned(int is_learned) {
			this.is_learned = is_learned;
		}
		public int getLearn_num() {
			return learn_num;
		}
		public void setLearn_num(int learn_num) {
			this.learn_num = learn_num;
		}
		public int getDegree() {
			return degree;
		}
		public void setDegree(int degree) {
			this.degree = degree;
		}
		public String getPic() {
			return pic;
		}
		public void setPic(String pic) {
			this.pic = pic;
		}
		@Override
		public String toString() {
			return "Video [id=" + id + ", bean=" + bean + ", name=" + name + ", url=" + url + ", is_learned=" + is_learned + ", learn_num="
					+ learn_num + ", degree=" + degree + ", pic=" + pic + ", timestamp=" + timestamp + ", videoId=" + videoId + ", vid="
					+ vid + ", share_exp=" + share_exp + "]";
		}
		
		
	}

}
