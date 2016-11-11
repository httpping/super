package com.vp.loveu.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * @项目名称nameloveu1.0
 * @时间2016年2月26日下午6:06:26
 * @功能 录音的button
 * @作者 mi
 */

public class RecoderButton extends Button {
	
	public final int STATE_NORMAL = 0;
	public final int STATE_RECODING = 1;
	public final int STATE_CANCLE = 2;
	public final int STATE_TIME_SORT = 3;
	public int STATE_CURRENT = STATE_NORMAL;
	

	public RecoderButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setBackgroundColor(Color.TRANSPARENT);
	}

	public RecoderButton(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public RecoderButton(Context context) {
		this(context,null);
	}

}
