package com.vp.loveu.my.adapter;

import android.content.Context;
import android.view.View;

import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.index.widget.IndexVideoRelativeLayout;
import com.vp.loveu.my.bean.ArticleBean.ArticleDataBean;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年1月5日上午10:01:05
 * @功能TODO
 * @作者 mi
 */

public class ArticleHolder extends BaseHolder<ArticleDataBean> {

	private IndexVideoRelativeLayout itemView;

	public ArticleHolder(Context context) {
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
	protected void initData(ArticleDataBean bean) {
		itemView.setdata(bean);
	}
}
