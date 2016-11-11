package com.vp.loveu.index.holder;

import java.util.ArrayList;
import java.util.List;

import com.vp.loveu.index.bean.CityActiveBean.ActBean;
import com.vp.loveu.index.widget.HistoryActiveLinearLayout;
import com.vp.loveu.util.UIUtils;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月18日上午10:14:02
 * @功能 历史活动的holder
 * @作者 mi
 */

public class CityHistoryListItemHolder extends BaseHolder<List<ActBean>> {

	public CityHistoryListItemHolder(Context context) {
		super(context);
	}

	private LinearLayout layout;

	@Override
	protected View initView() {
		layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);
		return layout;
	}

	@Override
	protected void initData(List<ActBean> data) {
		if (data == null || data.size() < 0) {
			return;
		}
		List<ActBean> list = new ArrayList<ActBean>() ;
		for (int i = 0; i < data.size(); i+=2) {
			try {
				list.add(data.get(i));
				list.add(data.get(i+1));
			} catch (Exception e) {
				e.printStackTrace();
				list.add(null);
			}
			HistoryActiveLinearLayout item = new HistoryActiveLinearLayout(mContext);
			if (i != 0) {
				LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				layoutParams.topMargin = UIUtils.dp2px(10);
				item.setData(list);
				layout.addView(item,layoutParams);
			}else{
				item.setData(list);
				layout.addView(item);
			}
			list.clear();
		}
	}
}
