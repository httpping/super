package com.vp.loveu.channel.bean;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.vp.loveu.channel.bean.ChannelHomeBean.Radio;
import com.vp.loveu.channel.bean.ChannelHomeBean.Topic;
import com.vp.loveu.channel.bean.ChannelHomeBean.Video;

/**
 * @author：pzj
 * @date: 2015-11-16 下午3:34:30
 * @Description:
 */
public class ChannelHomeBeanVo {
	private ArrayList<Video> videos;
	private ArrayList<Radio> radios;
	private ArrayList<Topic> topics;
	
	
	public static ChannelHomeBeanVo parseJson(String json) {
		Gson gson=new Gson();
		ChannelHomeBeanVo bean=gson.fromJson(json, ChannelHomeBeanVo.class);
		return bean;
	}
	
	public ArrayList<Video> getVideos() {
		return videos;
	}
	public void setVideos(ArrayList<Video> videos) {
		this.videos = videos;
	}
	public ArrayList<Radio> getRadios() {
		return radios;
	}
	public void setRadios(ArrayList<Radio> radios) {
		this.radios = radios;
	}
	public ArrayList<Topic> getTopics() {
		return topics;
	}
	public void setTopics(ArrayList<Topic> topics) {
		this.topics = topics;
	}
	

}
