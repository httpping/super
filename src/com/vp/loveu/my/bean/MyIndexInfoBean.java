package com.vp.loveu.my.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月12日上午11:00:26
 * @功能 我的首页的数据bean
 * @作者mi
 */

public class MyIndexInfoBean extends VPBaseBean{

	public int code;
	public String data;	
	public int is_encrypt;
	public String msg;
	
	public class DataBean extends VPBaseBean{
		public int age;
		public int edu;
		public int mentor_grade;
		public String nickname;
		public List<String> photos;
		public String portrait;	
		public int rank;
		public int sex;
		public int type;
		public int uid;
		public int user_exp;
		public String xmpp_user;
	}
}
