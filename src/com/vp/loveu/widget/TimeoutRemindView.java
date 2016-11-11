package com.vp.loveu.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.message.utils.DensityUtil;
/**
 * 超时webview
 * @author tanping
 * 2016-1-26
 */
public class TimeoutRemindView extends RelativeLayout {

	public TimeoutRemindView(Context context ) {
		super(context );
		init();
	}
	

	public TimeoutRemindView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public TextView mRemindTextView;//提醒
	private void init() {
		setGravity(Gravity.CENTER);//
		mRemindTextView = new TextView(getContext());
		
		mRemindTextView.setText("点击屏幕 重新加载");
		mRemindTextView.setTextColor(getResources().getColor(R.color.vlink_item_msg_remind_text_color));
		mRemindTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		
		
		Drawable drawable= getResources().getDrawable(R.drawable.webview_timeout);
		/// 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		mRemindTextView.setCompoundDrawables(null,drawable,null,null);
		mRemindTextView.setCompoundDrawablePadding(DensityUtil.dip2px(getContext(), 20));
		addView(mRemindTextView);
	}

}
