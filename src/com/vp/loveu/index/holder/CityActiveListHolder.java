package com.vp.loveu.index.holder;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.vp.loveu.R;
import com.vp.loveu.index.bean.CityActiveBean.ActBean;
import com.vp.loveu.index.bean.CityActiveBean.InProgress;
import com.vp.loveu.index.widget.IndexActiveShowsRelativeLayout;
import com.vp.loveu.util.UIUtils;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月17日下午5:30:35
 * @功能 正在举办的活动列表
 * @作者 mi
 */

public class CityActiveListHolder extends BaseHolder<InProgress> {

	public CityActiveListHolder(Context context) {
		super(context);
	}

	private LinearLayout layout;

	@Override
	protected View initView() {
		layout = new LinearLayout(UIUtils.getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundColor(layout.getContext().getResources().getColor(R.color.white));
		return layout;
	}

	@Override
	protected void initData(InProgress data) {
		if (data == null) {
			return;
		}
		LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(UIUtils.dp2px(10), UIUtils.dp2px(10), UIUtils.dp2px(10),  UIUtils.dp2px(10));
		List<ActBean> actList = data.activites;
		for (int i = 0; i < actList.size(); i++) {
			ActBean actBean = actList.get(i);
			IndexActiveShowsRelativeLayout indexActiveShowsRelativeLayout = new IndexActiveShowsRelativeLayout(mContext);
			indexActiveShowsRelativeLayout.setData(actBean);
			layout.addView(indexActiveShowsRelativeLayout, params);
		}
	}
}
