package com.vp.loveu.index.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月4日上午10:49:46
 * @功能 用户积分排名的bean
 * @作者 mi
 */

public class HotUserBean extends VPBaseBean{

	public int code;
	public List<HotDataBean> data;
	public int is_encrypt;
	public String msg;

	public class HotDataBean extends VPBaseBean{
		public int exp;
		public String nickname;
		public String portrait;
		public int rank;
		public int uid;
	}
}
