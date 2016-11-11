package com.vp.loveu.index.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;
import com.vp.loveu.index.bean.IndexBean.IndexArtBean;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月24日下午4:24:05
 * @功能 更多精彩长文的数据bean
 * @作者 mi
 */

public class MoreContentBean extends VPBaseBean{
	public int code;
	public List<IndexArtBean> data;
	public int is_encrypt;
	public String msg;
}
