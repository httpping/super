package com.vp.loveu.my.adapter;

import android.content.Context;
import android.view.View;

import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.my.bean.ExpHistoryBean;
import com.vp.loveu.my.widget.WalletBottomItemRelativeLayout;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年1月5日上午9:43:59
 * @功能 1.0版本我的积分的holder  暂时没有使用
 * @作者 mi
 */

public class IntergralHolder extends BaseHolder<ExpHistoryBean> {

	private WalletBottomItemRelativeLayout itemView;

	public IntergralHolder(Context context) {
		super(context);
	}

	@Override
	protected View initView() {
		return new WalletBottomItemRelativeLayout(mContext);
	}

	@Override
	protected void findView() {
		itemView = (WalletBottomItemRelativeLayout) mRootView;
		itemView.setIsShowTvTime(false);
	}

	@Override
	protected void initData(ExpHistoryBean bean) {
		itemView.setTvTitle(bean.remark);
		if (bean.exp >= 0) {
			itemView.setTvIntergral("+" + bean.exp);
		} else {
			itemView.setTvIntergral("" + bean.exp);
		}
		if (mPosition == mDatas.size() - 1) {
			itemView.setIsShowLine(false);
		}
	}
}
