package com.vp.loveu.index.adapter;

import android.content.Context;
import android.view.View;

import com.vp.loveu.index.bean.IndexBean.IndexArtBean;
import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.index.widget.IndexVideoRelativeLayout;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年1月5日上午9:11:16
 * @功能TODO
 * @作者 mi
 */

public class MoreContentHolder extends BaseHolder<IndexArtBean> {

	private IndexVideoRelativeLayout itemView;

	public MoreContentHolder(Context context) {
		super(context);
	}

	@Override
	protected View initView() {
		return new IndexVideoRelativeLayout(mContext);
	}

	@Override
	protected void findView() {
		itemView = (IndexVideoRelativeLayout) mRootView;
	}

	@Override
	protected void initData(IndexArtBean bean) {
		itemView.setdata(bean);
	}
}
