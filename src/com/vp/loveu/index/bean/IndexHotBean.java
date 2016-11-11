package com.vp.loveu.index.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * @时间2016年1月25日下午4:13:25
 * @功能 首页hot bean
 * @作者 mi
 */

public class IndexHotBean extends VPBaseBean{
	public int code;
	public List<IndexHotDataBean> data;
	public int is_encrypt;
	public String msg;
	
	public class IndexHotDataBean extends VPBaseBean{
		public String nickname;
		public String portrait;
		public String reason;
		public int sex;
		public int uid;
	}
}
