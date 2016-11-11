package com.vp.loveu.channel.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;

/**
 * @author：pzj
 * @date: 2015-11-16 下午4:06:07
 * @Description:
 */
public class ChannelHomeBean {
	private int Type;
	

	private int id;
	private String name;
	private String pic;
	private String create_time;
	private double price;
	private int video_num;
	private int learn_num;
	
	private int id_right;
	private String name_right;
	private String pic_right;
	private String create_time_right;
	private double price_right;
	private int video_num_right;
	private int learn_num_right;
	
//	private int id;
//	private String name;
	private int online_num;
	private int uid;
	private String nickname;
	private String url;
	private String cover;
	private boolean isPlaying;//电台是否正在播放
	
	private boolean isListened;
	private int totalPosition;
	private int currentPosition;
	
//	private int id;
//	private String name;
	private int join_num;
//	private String create_time;
	private int unread_num;
	private String cont;
	
	
	public int getType() {
		return Type;
	}
	public void setType(int type) {
		Type = type;
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
	public int getLearn_num() {
		return learn_num;
	}
	public void setLearn_num(int learn_num) {
		this.learn_num = learn_num;
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
	public int getJoin_num() {
		return join_num;
	}
	public void setJoin_num(int join_num) {
		this.join_num = join_num;
	}
	public int getUnread_num() {
		return unread_num;
	}
	public void setUnread_num(int unread_num) {
		this.unread_num = unread_num;
	}
	public String getCont() {
		return cont;
	}
	public void setCont(String cont) {
		this.cont = cont;
	}

	public int getId_right() {
		return id_right;
	}
	public void setId_right(int id_right) {
		this.id_right = id_right;
	}
	public String getName_right() {
		return name_right;
	}
	public void setName_right(String name_right) {
		this.name_right = name_right;
	}
	public String getPic_right() {
		return pic_right;
	}
	public void setPic_right(String pic_right) {
		this.pic_right = pic_right;
	}
	public String getCreate_time_right() {
		return create_time_right;
	}
	public void setCreate_time_right(String create_time_right) {
		this.create_time_right = create_time_right;
	}
	public double getPrice_right() {
		return price_right;
	}
	public void setPrice_right(double price_right) {
		this.price_right = price_right;
	}
	public int getVideo_num_right() {
		return video_num_right;
	}
	public void setVideo_num_right(int video_num_right) {
		this.video_num_right = video_num_right;
	}
	public int getLearn_num_right() {
		return learn_num_right;
	}
	public void setLearn_num_right(int learn_num_right) {
		this.learn_num_right = learn_num_right;
	}

	public boolean isListened() {
		return isListened;
	}
	public void setListened(boolean isListened) {
		this.isListened = isListened;
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

	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public boolean isPlaying() {
		return isPlaying;
	}
	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}





	public class Video{
		private int id;
		private String name;
		private String pic;
		private String create_time;
		private double price;
		private int video_num;
		private int learn_num;
		
		
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
		public int getLearn_num() {
			return learn_num;
		}
		public void setLearn_num(int learn_num) {
			this.learn_num = learn_num;
		}
		
		

	}

	public class Radio{
		private int id;
		private String name;
		private int online_num;
		private int uid;
		private String nickname;
		private String url;
		private boolean isListened;
		private int totalPosition;
		private int currentPosition;
		private String cover;
		
		private boolean isPlaying;
		
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
		public int getOnline_num() {
			return online_num;
		}
		public void setOnline_num(int online_num) {
			this.online_num = online_num;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public boolean isListened() {
			return isListened;
		}
		public void setListened(boolean isListened) {
			this.isListened = isListened;
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
		public String getCover() {
			return cover;
		}
		public void setCover(String cover) {
			this.cover = cover;
		}
		public boolean isPlaying() {
			return isPlaying;
		}
		public void setPlaying(boolean isPlaying) {
			this.isPlaying = isPlaying;
		}
		
		

	}
	public class Topic{
		private int id;
		private String name;
		private int join_num;
		private String create_time;
		private int unread_num;
		private String cont;
		
		public  Topic parseJson(String json){
			Gson gson=new Gson();
			return gson.fromJson(json, Topic.class);
		}
		
		public  ArrayList<Topic> parseArrayJson(JSONArray jsonData){
			ArrayList<Topic> list=new ArrayList<Topic>();
			if(jsonData!=null){
				for(int i=0;i<jsonData.length();i++){
					try {
						String json = jsonData.getString(i);
						Topic bean =parseJson(json);
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
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getJoin_num() {
			return join_num;
		}
		public void setJoin_num(int join_num) {
			this.join_num = join_num;
		}
		public String getCreate_time() {
			return create_time;
		}
		public void setCreate_time(String create_time) {
			this.create_time = create_time;
		}
		public int getUnread_num() {
			return unread_num;
		}
		public void setUnread_num(int unread_num) {
			this.unread_num = unread_num;
		}
		public String getCont() {
			return cont;
		}
		public void setCont(String cont) {
			this.cont = cont;
		}
		
		
		
	}
	

}
