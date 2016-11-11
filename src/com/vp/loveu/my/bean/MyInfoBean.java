package com.vp.loveu.my.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月7日下午6:17:07
 * @功能TODO
 * @作者 获取用户基本资料Bean
 */

public class MyInfoBean extends VPBaseBean{
	public int code;
	public String data;
	public int is_encrypt;
	public String msg;
	
	public class MyInfoDataBean extends VPBaseBean{
		public String area_code;
		public String birthday;
		public int dating_status;
		public int edu;
		public int height;
		public int income;
		public String mt;
		public String nickname;
		public List<String> photos;
		public String portrait;
		public int sex;
		public int type;
		public int uid;
		public int weight;
	}
}
