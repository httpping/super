package com.vp.loveu.my.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.vp.loveu.index.bean.CityActiveBean.ActBean;
import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.index.widget.IndexActiveShowsRelativeLayout;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年1月5日上午10:37:25
 * @功能TODO
 * @作者 mi
 */

public class MyActiveHolder extends BaseHolder<ActBean> {

	private IndexActiveShowsRelativeLayout itemView;

	public MyActiveHolder(Context context) {
		super(context);
	}

	@Override
	protected View initView() {
		
		return new IndexActiveShowsRelativeLayout(mContext);
	}

	@Override
	protected void findView() {
		itemView = (IndexActiveShowsRelativeLayout) mRootView;
	}

	@Override
	protected void initData(ActBean bean) {
		itemView.setMarginBotton(true);
		itemView.setData(bean);
	}
}
