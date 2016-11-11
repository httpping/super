package com.vp.loveu.index.adapter;

import java.util.ArrayList;
import java.util.List;

import com.vp.loveu.index.bean.CityActiveBean.ActBean;
import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.index.widget.HistoryActiveLinearLayout;
import com.vp.loveu.index.widget.IndexActiveShowsRelativeLayout;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.login.bean.UserBaseInfoBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.widget.ZanAllHeadView;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年1月7日下午5:14:46
 * @功能 同城活动备用的adapter
 * @作者 mi
 */

public class CityListAdapter extends VpBaseAdapter<ActBean> {

	private boolean isShowHistoryActive;

	public CityListAdapter(Context context, AbsListView listView, List<ActBean> data, boolean isShowHistoryActive) {
		super(context, listView, data);
		this.isShowHistoryActive = isShowHistoryActive;
	}

	@Override
	public BaseHolder<ActBean> getHolder() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		if (!isShowHistoryActive) {
			return 2;
		}
		return 3;
	}

	@Override
	public int getItemType(int position) {
		ActBean actBean = mDatas.get(position);
		if (actBean.type == 1) {
			return 0;
		} else if (actBean.type == 2) {
			return 1;
		} else if (actBean.type == 3) {
			return 2;
		}
		return super.getItemType(position);
	}

	@Override
	public BaseHolder<ActBean> getItemTypeHoler(int type) {
		BaseHolder<ActBean> holder = null;
		switch (type) {
		case 0:
			// 正在进行
			holder = new InprogressActiveHolder(mContext);
			break;
		case 1:
			// 报名用户
			holder = new HotuserHolder(mContext);
			break;
		case 2:
			// 历史活动
			holder = new HistoryHolder(mContext);
			break;

		default:
			break;
		}
		return holder;
	}

	private class InprogressActiveHolder extends BaseHolder<ActBean> {

		IndexActiveShowsRelativeLayout container;

		public InprogressActiveHolder(Context context) {
			super(context);
		}

		@Override
		protected View initView() {
			FrameLayout frameLayout = new FrameLayout(mContext);
			frameLayout.setBackgroundColor(Color.WHITE);
			IndexActiveShowsRelativeLayout indexActiveShowsRelativeLayout = new IndexActiveShowsRelativeLayout(mContext);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(UIUtils.dp2px(10), UIUtils.dp2px(10), UIUtils.dp2px(10), UIUtils.dp2px(10));
			frameLayout.addView(indexActiveShowsRelativeLayout, params);
			return frameLayout;
		}

		@Override
		protected void findView() {
			ViewGroup v = (ViewGroup) mRootView;
			container = (IndexActiveShowsRelativeLayout) v.getChildAt(0);
		}

		@Override
		protected void initData(ActBean data) {
			container.setData(data);
		}
	}

	private class HotuserHolder extends BaseHolder<ActBean> {

		FrameLayout container;
		ZanAllHeadView zanAllHeadView;

		public HotuserHolder(Context context) {
			super(context);
		}

		@Override
		protected View initView() {
			return new FrameLayout(mContext);
		}

		@Override
		protected void findView() {
			container = (FrameLayout) mRootView;
			container.setBackgroundColor(Color.WHITE);
		}

		@Override
		protected void initData(ActBean data) {
			if (data == null || data.users == null || data.users.size() == 0) {
				return;
			}
			zanAllHeadView = new ZanAllHeadView(mContext);
			zanAllHeadView.setmImageSize(UIUtils.dp2px(40));
			zanAllHeadView.mPortraitCount.setTextColor(Color.parseColor("#FF7A00"));
			zanAllHeadView.setPadding(UIUtils.dp2px(15), 0, UIUtils.dp2px(10), 0);
			zanAllHeadView.setmIsShowCutline(false);

			ArrayList<UserBaseInfoBean> list = new ArrayList<UserBaseInfoBean>();
			LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
			if (loginInfo == null) {
				return;
			}

			int num = 0;

			for (int i = 0; i < data.users.size(); i++) {
				ActBean info = data.users.get(i);
				String nickName = "";
				String portrait = "";
				if (info.uid == loginInfo.getUid()) {
					nickName = loginInfo.getNickname();
					portrait = loginInfo.getPortrait();
				} else {
					nickName = info.nickname;
					portrait = info.portrait;
				}
				num = info.num;
				UserBaseInfoBean bean = new UserBaseInfoBean();
				bean.setName(nickName);
				bean.setPortrait(portrait);
				bean.setUid(info.uid);
				list.add(bean);
			}
			zanAllHeadView.setDatas(list);
			zanAllHeadView.mPortraitCount.setText(num + "");
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(0, UIUtils.dp2px(20), 0, UIUtils.dp2px(20));
			container.addView(zanAllHeadView, params);
		}

	}

	public int flag = -1;

	private class HistoryHolder extends BaseHolder<ActBean> {

		HistoryActiveLinearLayout container;

		public HistoryHolder(Context context) {
			super(context);
		}

		@Override
		protected View initView() {
			return new HistoryActiveLinearLayout(mContext);
		}

		@Override
		protected void findView() {
			container = (HistoryActiveLinearLayout) mRootView;
			container.setTvTitleFlagIsShow(flag == -1);
		}

		@Override
		protected void initData(ActBean data) {
			if (data == null || data.historyList == null) {
				return;
			}
			flag = 1;
			container.setData(data.historyList);
		}
	}

}
