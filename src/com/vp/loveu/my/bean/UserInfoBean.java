package com.vp.loveu.my.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月7日上午10:10:29
 * @功能 用户主页的信息bean
 * @作者 mi
 */

public class UserInfoBean extends VPBaseBean{
	public int code;
	public String data;
	public int is_encrypt;
	public String msg;
	
	public class UserDataBean extends VPBaseBean{
		public int activities_num;
		public int age;
		public int edu;
		public String grade;
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
		public String area_code;	
		public int dating_info_status;
		public int dating_status;
	}
}
