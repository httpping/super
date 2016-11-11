package com.vp.loveu.my.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.vp.loveu.R;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月28日下午1:10:03
 * @功能TODO
 * @作者 mi
 */

public class IntegralHeadView extends RelativeLayout {

	public IntegralHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.integral_head_view, this);
	}

	public IntegralHeadView(Context context) {
		this(context,null);
	}
}
