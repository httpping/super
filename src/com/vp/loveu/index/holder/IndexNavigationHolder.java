package com.vp.loveu.index.holder;

import java.util.List;

import com.vp.loveu.index.bean.IndexBean.IndexServiceBean;
import com.vp.loveu.index.widget.NavigationLinearLayout;
import com.vp.loveu.util.UIUtils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月17日上午10:37:44
 * @功能 导航模块的holder
 * @作者 mi
 */

public class IndexNavigationHolder extends BaseHolder<List<IndexServiceBean>>{
	
	private static final String TAG = "IndexNavigationHolder";
	public static final String KEY_FLAG = "key_flag";
	private LinearLayout itemView;
	
	public IndexNavigationHolder(Context context) {
		super(context);
	}

	@Override
	protected View initView() {
		return new LinearLayout(mContext);
	}
	
	@Override
	protected void findView() {
		itemView = (LinearLayout) mRootView;
		itemView.setMinimumHeight(UIUtils.dp2px(95));
		itemView.setOrientation(LinearLayout.HORIZONTAL);
		itemView.setBackgroundColor(Color.parseColor("#ffffff"));
	}

	@Override
	protected void initData(List<IndexServiceBean> data) {
		for (int i = 0; i < data.size(); i++) {
			IndexServiceBean bean = data.get(i);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
			params.gravity = Gravity.CENTER_VERTICAL;
			NavigationLinearLayout item = new NavigationLinearLayout(mContext);
			item.setData(bean);
			itemView.addView(item,params);
		}
	}
}
