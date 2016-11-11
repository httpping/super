package com.vp.loveu.index.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月4日下午5:48:09
 * @功能 我的情感求助的数据bean
 * @作者 mi
 */

public class MySeekHelpBean extends VPBaseBean{
	public int code;
	public List<MySeekDataBean> data;
	public int is_encrypt;
	public String msg;

	public class MySeekDataBean extends VPBaseBean{
		public List<MySeekAudioBean> audios;
		public String cont;
		public String create_time;
		public int id;
		public List<String> pics;
		public double price;
		public List<MySeekDataBean> replys;
		public int status;
		public int type;
		public int uid;
		public String nickname;
		public String portrait;
	}

	public class MySeekAudioBean extends VPBaseBean{
		public String title;
		public String url;
		public boolean isPlayer;
	}
}
