package com.vp.loveu.message.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;

/**
 * @author：pzj
 * @date: 2015年12月7日 下午5:15:03
 * @Description:
 */
public class ReplyFellHelpBean {
	
	private int id	;
	private int uid	;
	private String nickname	;
	private String portrait	;
	private String cont	;
	private ArrayList<Audio> audios;
	private ArrayList<String> pics;
	private int type	;
	private String create_time	;
	private int status;
	private ArrayList<Reply> replys	;
	private double price;
	
	private boolean isReject;//是否已被拒绝回答
	
	private int viewType=0;//default
	
	public static ReplyFellHelpBean parseJson(String json){
		Gson gson=new Gson();
		return gson.fromJson(json, ReplyFellHelpBean.class);
	}
	
	public static  List<ReplyFellHelpBean> createFromJsonArray(String json) {
		List<ReplyFellHelpBean> modeBens = new ArrayList<ReplyFellHelpBean>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				ReplyFellHelpBean bean =parseJson(jsonArray.getString(i));
				if (bean != null) {
					modeBens.add(bean);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return modeBens;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getCont() {
		return cont;
	}
	public void setCont(String cont) {
		this.cont = cont;
	}
	public ArrayList<Audio> getAudios() {
		return audios;
	}
	public void setAudios(ArrayList<Audio> audios) {
		this.audios = audios;
	}
	public ArrayList<String> getPics() {
		return pics;
	}
	public void setPics(ArrayList<String> pics) {
		this.pics = pics;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public ArrayList<Reply> getReplys() {
		return replys;
	}
	public void setReplys(ArrayList<Reply> replys) {
		this.replys = replys;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	
	
	public int getViewType() {
		return viewType;
	}

	public void setViewType(int viewType) {
		this.viewType = viewType;
	}



	public boolean isReject() {
		return isReject;
	}

	public void setReject(boolean isReject) {
		this.isReject = isReject;
	}



	public class Audio{
		private String url;
		private String title;
		private boolean isSendNow;//是否为当前发送
		private boolean isSendSuccess=false;//记录是否发送成功
		
		public Audio() {
			// TODO Auto-generated constructor stub
		}
		public Audio(String url, String title,boolean sendNow,boolean sendSuccess) {
			super();
			this.url = url;
			this.title = title;
			this.isSendNow=sendNow;
			this.isSendSuccess=sendSuccess;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public boolean isSendNow() {
			return isSendNow;
		}
		public void setSendNow(boolean isSendNow) {
			this.isSendNow = isSendNow;
		}
		public boolean isSendSuccess() {
			return isSendSuccess;
		}
		public void setSendSuccess(boolean isSendSuccess) {
			this.isSendSuccess = isSendSuccess;
		}
		
		
		
	}
	
	public class Reply{
		private int id	;
		private String cont	;
		private ArrayList<Audio> audios;
		private ArrayList<String> pics;	
		private int type;
		private String create_time	;
		private int status	;
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
		public ArrayList<Audio> getAudios() {
			return audios;
		}
		public void setAudios(ArrayList<Audio> audios) {
			this.audios = audios;
		}
		public ArrayList<String> getPics() {
			return pics;
		}
		public void setPics(ArrayList<String> pics) {
			this.pics = pics;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public String getCreate_time() {
			return create_time;
		}
		public void setCreate_time(String create_time) {
			this.create_time = create_time;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		
		
	}
	

}
