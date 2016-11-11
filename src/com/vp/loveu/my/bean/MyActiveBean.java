package com.vp.loveu.my.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;
import com.vp.loveu.index.bean.CityActiveBean.ActBean;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月9日上午11:36:37
 * @功能 我的活动的bean
 * @作者 mi
 */

public class MyActiveBean extends VPBaseBean{

	public int code;
	public List<ActBean> data;
	public int is_encrypt;
	public String msg;

}
