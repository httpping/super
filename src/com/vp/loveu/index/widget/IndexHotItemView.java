package com.vp.loveu.index.widget;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.index.bean.IndexHotBean.IndexHotDataBean;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.my.activity.MyCenterActivity;
import com.vp.loveu.my.activity.UserIndexActivity;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.widget.SexCircleImageView;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @项目名称nameloveu1.0
 * @时间2016年1月25日下午3:53:28
 * @功能TODO
 * @作者 mi
 */

public class IndexHotItemView extends LinearLayout implements OnClickListener {

	private SexCircleImageView  mIvIcon;
	private TextView mTvDes;
	private IndexHotDataBean mDatas;
	
	public IndexHotItemView(Context context) {
		this(context,null);
	}
	
	public IndexHotItemView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}
	
	public IndexHotItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.indexhotitemlayout, this);
		initView();
	}

	private void initView() {
		mIvIcon = (SexCircleImageView) findViewById(R.id.indexhot_iv_icon);
		mTvDes = (TextView) findViewById(R.id.indexhot_tv_des);
		setOnClickListener(this);
	}

	public void setData(IndexHotDataBean bean) {
		final LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (bean == null || loginInfo==null) {
			return;
		}
		mDatas = bean;
		String portrait = "";
		if (bean.uid == loginInfo.getUid()) {
			portrait = loginInfo.getPortrait();
		}else{
			portrait = bean.portrait;
		}
		
		ImageLoader.getInstance().displayImage(portrait, mIvIcon,DisplayOptionsUtils.getOptionsConfig());
		if (!TextUtils.isEmpty(bean.reason)) {
			mTvDes.setText(bean.reason);
		}
		mIvIcon.setSex(bean.sex);
	}

	@Override
	public void onClick(View v) {
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo!=null && mDatas != null) {
			if (mDatas.uid == loginInfo.getUid()) {
				Intent intent = new Intent(getContext(), MyCenterActivity.class);
				getContext().startActivity(intent);
			}else{
				Intent intent = new Intent(getContext(), UserIndexActivity.class);
				intent.putExtra(UserIndexActivity.KEY_UID, mDatas.uid);
				getContext().startActivity(intent);
			}
		}
	}
}
