package com.vp.loveu.my.bean;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月15日上午11:15:08
 * @功能TODO
 * @作者 mi
 */

public class SelectUserFollow  extends VPBaseBean{
	public int code;
	public SelectUserBean data;
	public int is_encrypt;
	public String msg;
	public class SelectUserBean{
		public int status;
	}
}
