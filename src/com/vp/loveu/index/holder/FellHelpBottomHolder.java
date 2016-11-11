package com.vp.loveu.index.holder;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.vp.loveu.R;
import com.vp.loveu.index.bean.FellHelpBean.FellHelpBeanAskedBean;
import com.vp.loveu.index.bean.FellHelpBean.FellHelpBeanData;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.index.widget.EmptyTextView;
import com.vp.loveu.index.widget.FellHelpBottomItem;
import com.vp.loveu.login.bean.LoginUserInfoBean;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月19日上午9:26:57
 * @功能 情感求助底部的holder
 * @作者 mi
 */

public class FellHelpBottomHolder extends BaseHolder<FellHelpBeanData> {
	
	private LinearLayout mFellLyContainer;
	private List<FellHelpBeanAskedBean> askListData;
	FellHelpBeanData mData;
	public FellHelpBottomHolder(Context context) {
		super(context);
	}
	
	@Override
	protected View initView() {
		return View.inflate(mContext, R.layout.fell_help_bottom_holder, null);
	}

	@Override
	protected void findView() {
		mFellLyContainer = (LinearLayout) mRootView.findViewById(R.id.fell_help_bootom_ly_container);
	}
	
	
	
	@Override
	protected void initData(FellHelpBeanData data) {
		mData = data;
		mFellLyContainer.removeAllViews();
		if (data.asked == null || data.asked.size() == 0) {
			//没有数据
			EmptyTextView textView = new EmptyTextView(mContext);
			textView.setWidth(LinearLayout.LayoutParams.FILL_PARENT);
			textView.setHeight(LinearLayout.LayoutParams.FILL_PARENT);
			textView.setGravity(Gravity.CENTER);
			//textView.setPadding(0, 50, 0, 0);
			textView.setText("目前还没有求助过的人哦");
			mFellLyContainer.addView(textView);
			return;
		}
		askListData = data.asked;
		if (askListData != null && askListData.size() > 0) {
			for (int i = 0; i < askListData.size(); i++) {
				FellHelpBeanAskedBean bean = askListData.get(i);
				FellHelpBottomItem item = new FellHelpBottomItem(mContext);
				item.setData(data,bean);
				mFellLyContainer.addView(item);
			}
		}
	}
}
