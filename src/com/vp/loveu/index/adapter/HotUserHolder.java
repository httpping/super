package com.vp.loveu.index.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.index.bean.HotUserBean.HotDataBean;
import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.my.activity.MyCenterActivity;
import com.vp.loveu.my.activity.UserIndexActivity;
import com.vp.loveu.util.LoginStatus;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年1月5日上午9:17:06
 * @功能TODO
 * @作者 mi
 */

public class HotUserHolder extends BaseHolder<HotDataBean> {

	private TextView mTvRank;
	private TextView mTvNickName;
	private TextView mTvIntegral;
	private ImageView mIvIcon;

	public HotUserHolder(Context context) {
		super(context);
	}

	@Override
	protected View initView() {
		return View.inflate(mContext, R.layout.hotuser_listview_item, null);
	}

	@Override
	protected void findView() {
		mIvIcon = (ImageView) mRootView.findViewById(R.id.hot_item_iv_icon);
		mTvRank = (TextView) mRootView.findViewById(R.id.hot_item_tv_rank);
		mTvNickName = (TextView) mRootView.findViewById(R.id.hot_item_tv_name);
		mTvIntegral = (TextView) mRootView.findViewById(R.id.hot_item_tv_intergral);
	}

	@Override
	protected void initData(HotDataBean bean) {
		final LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (bean == null || loginInfo == null) {
			return;
		}
		
		String nickname ="";
		String portrait ="";
		if (bean.uid == loginInfo.getUid()) {
			nickname=loginInfo.getNickname();
			portrait=loginInfo.getPortrait();
		}else{
			nickname=bean.nickname;
			portrait=bean.portrait;
		}
		
		ImageLoader.getInstance().displayImage(portrait, mIvIcon, DisplayOptionsUtils.getOptionsConfig());
		mTvRank.setText(bean.rank + "");
		mTvNickName.setText(nickname);
		mTvIntegral.setText(bean.exp + "");
		mIvIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转到个人主页
				if (mData.uid == loginInfo.getUid()) {
					Intent intent = new Intent(mContext, MyCenterActivity.class);
					mContext.startActivity(intent);
				} else {
					Intent intent = new Intent(mContext, UserIndexActivity.class);
					intent.putExtra(UserIndexActivity.KEY_UID, mData.uid);
					mContext.startActivity(intent);
				}
			}
		});
	}
}
