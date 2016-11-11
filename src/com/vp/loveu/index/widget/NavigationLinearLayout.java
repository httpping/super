package com.vp.loveu.index.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.bean.InwardAction;
import com.vp.loveu.index.bean.IndexBean.IndexServiceBean;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月17日下午4:01:14
 * @功能TODO
 * @作者 mi
 */

public class NavigationLinearLayout extends LinearLayout {
	
	private ImageView mIvIcon;
	private TextView mTvText;
	private InwardAction parseAction;
	
	public NavigationLinearLayout(Context context) {
		this(context,null);
	}
	public NavigationLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.navigation_item, this);
		mIvIcon = (ImageView) findViewById(R.id.index_navigation_iv_one);
		mTvText = (TextView) findViewById(R.id.index_navigation_tv_one);
	}
	public void setData(final IndexServiceBean bean) {
		ImageLoader.getInstance().displayImage(bean.icon, mIvIcon, DisplayOptionsUtils.getOptionsConfig());
		mTvText.setText(bean.name);
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				parseAction = InwardAction.parseAction(bean.url);
				if (parseAction != null) {
					parseAction.toStartActivity(getContext());
				}
			}
		});
	}
}
