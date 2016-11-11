package com.vp.loveu.index.bean;

import java.io.Serializable;
import java.util.List;

import com.vp.loveu.bean.VPBaseBean;

/**
 * 活动详情bean
 * 
 * @author tanping 2015-12-11
 */
public class ActivityDetailBean extends VPBaseBean{
	public int code;
	public ActivityDetailData data;
	public int is_encrypt;
	public String msg;

	public class ActivityDetailData extends VPBaseBean {
		public int agin_num;
		public String apply_end_time;
		public String begin_time;
		public String end_time;
		public int female_join_num;
		public int female_limit;
		public double female_price;
		public int id;
		public int male_join_num;
		public int male_limit;
		public double male_price;
		public String name;
		public String pic;
		public double progress;
		public String small_pic;
		public List<UserBean> users;
	}
	public class UserBean extends VPBaseBean {
		public int uid;
		public String nickname;
		public String portrait;
	}
}
