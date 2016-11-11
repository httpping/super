package com.vp.loveu.my.adapter;

import android.content.Context;
import android.view.View;

import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.my.bean.WalletBean.WalletDataBean;
import com.vp.loveu.my.widget.WalletBottomItemRelativeLayout;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年1月5日上午10:41:30
 * @功能TODO
 * @作者 mi
 */

public class WalletHolder extends BaseHolder<WalletDataBean> {

	private WalletBottomItemRelativeLayout itemView;

	public WalletHolder(Context context) {
		super(context);
	}

	@Override
	protected View initView() {
		return new WalletBottomItemRelativeLayout(mContext);
	}

	@Override
	protected void findView() {
		itemView = (WalletBottomItemRelativeLayout) mRootView;
	}

	@Override
	protected void initData(WalletDataBean bean) {
		itemView.setData(bean);
	}
}
