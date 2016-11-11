package com.vp.loveu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vp.loveu.R;

/**
 * @author：pzj
 * @date: 2015年12月23日 下午6:38:08
 * @Description:
 */
public class TextViewPoint extends RelativeLayout{
	public TextView tvTitle;
	public TextView tvPoint;

	public TextViewPoint(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}



	public TextViewPoint(Context context) {
		super(context);
		initView(context);
	}
	
	
	private void initView(Context context) {
		View.inflate(context, R.layout.public_point_textview, this);
		tvTitle=(TextView) findViewById(R.id.public_point_tv_title);
		tvPoint=(TextView) findViewById(R.id.public_point_tv_point);
	}
	

}
