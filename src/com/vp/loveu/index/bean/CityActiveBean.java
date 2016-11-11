package com.vp.loveu.index.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月18日下午5:23:25
 * @功能 同城活动的数据bean
 * @作者 mi
 */

public class CityActiveBean extends VPBaseBean{
	
	// type 1 == inprogress 2 == hotuser 3== history

	public int code;
	public DataBean data;
	public int is_encrypt;
	public String msg;

	public class DataBean extends VPBaseBean{
		public List<ActBean> history;
		public InProgress in_progress;
	}

	public class InProgress extends VPBaseBean{
		public List<ActBean> activites;
		public int num;
		public List<ActBean> users;
	}
	
	public static class ActBean extends VPBaseBean{
		public int num;
		public int type;
		public String apply_end_time;
		public String begin_time;
		public String end_time;
		public int id;
		public String name;
		public String pic;
		public double progress;
		public int remaining_num;
		public String small_pic;
		
		public String nickname;
		public String portrait;
		public int uid;
		
		public List<ActBean> users;//用户集
		public List<ActBean> historyList;//历史活动集
	}
}
