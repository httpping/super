package com.vp.loveu.index.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.index.bean.IndexBean.IndexActUserBean;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.my.activity.MyCenterActivity;
import com.vp.loveu.my.activity.UserIndexActivity;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.util.VpDateUtils;
import com.vp.loveu.widget.CircleImageView;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月16日下午3:22:51
 * @功能 活动模块的holder
 * @作者 mi
 */

public class IndexActiveInfosRelativeLayout extends RelativeLayout {
	
	private TextView mTvName;
	private CircleImageView mIvIcon;
	private TextView mTvBottom;
	private TextView mTvTime;
	private Context mContext;

	public IndexActiveInfosRelativeLayout(Context context) {
		this(context,null);
	}
	
	public IndexActiveInfosRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		View.inflate(context,R.layout.index_public_active_progress_infos, this);
		initView();
	}

	private void initView() {
		mTvName = (TextView) findViewById(R.id.index_active_infos_tv_name);
		mIvIcon = (CircleImageView) findViewById(R.id.index_active_infos_iv_icon);
		mTvBottom = (TextView) findViewById(R.id.index_active_infos_tv_bottom);
		mTvTime = (TextView) findViewById(R.id.index_active_infos_tv_time);
	}
	
	public void setData(final IndexActUserBean bean) {
		final LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (bean != null && loginInfo!= null) {
			//同步头像和nickname
			String nickName = "";
			String portrait = "";
			if (bean.uid == loginInfo.getUid()) {
				nickName = loginInfo.getNickname();
				portrait = loginInfo.getPortrait();
			}else{
				nickName = bean.nickname;
				portrait = bean.portrait;
			}
			
			mTvName.setText(nickName);
			ImageLoader.getInstance().displayImage(portrait, mIvIcon, DisplayOptionsUtils.getOptionsConfig());
			mTvTime.setText(VpDateUtils.getStandardDate(bean.create_time));
			mIvIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (bean.uid == loginInfo.getUid()) {
						Intent intent = new Intent(mContext, MyCenterActivity.class);
						mContext.startActivity(intent);
					}else{
						Intent intent = new Intent(mContext, UserIndexActivity.class);
						intent.putExtra(UserIndexActivity.KEY_UID, bean.uid);
						mContext.startActivity(intent);
					}
				}
			});
		}
	}
}
