package com.vp.loveu.my.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月2日上午10:11:50
 * @功能TODO
 * @作者 mi
 */

public class CustomGridView extends GridView {

	public CustomGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int makeMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, makeMeasureSpec);
	}
}
