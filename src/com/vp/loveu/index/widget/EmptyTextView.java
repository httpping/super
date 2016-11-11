package com.vp.loveu.index.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.vp.loveu.R;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年1月8日下午3:10:00
 * @功能 404 页面
 * @作者 mi
 */

public class EmptyTextView extends TextView {

	public EmptyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTextColor(getContext().getResources().getColor(R.color.text_bg_gray));
	}

	public EmptyTextView(Context context) {
		this(context,null);
		
	}

	@Override
	public void setTextSize(float size) {
		super.setTextSize(size);
	}
}
