package com.vp.loveu.my.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.vp.loveu.R;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月12日下午3:26:25
 * @功能TODO
 * @作者 mi
 */

public class MyCenterPlayItem extends LinearLayout {

	public MyCenterPlayItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.my_center_player_list_item, this);
	}

	public MyCenterPlayItem(Context context) {
		this(context,null);
	}

}
