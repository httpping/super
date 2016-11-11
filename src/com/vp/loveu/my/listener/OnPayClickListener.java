package com.vp.loveu.my.listener;

import android.view.View;

import com.vp.loveu.my.bean.MyCenterPayBean.MyCenterPayDataBean;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月16日下午12:39:22
 * @功能TODO
 * @作者 mi
 */

public interface OnPayClickListener {
	void success(View v,MyCenterPayDataBean data);
	void failed(View v,MyCenterPayDataBean data);
}
