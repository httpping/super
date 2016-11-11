package com.vp.loveu.my.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.my.bean.MyCenterPayBean.MyCenterPayDataBean;
import com.vp.loveu.util.LoginStatus;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月25日下午3:02:45
 * @功能 我的支付列表 支付失效的dialog
 * @作者 mi
 */

public class PlayFailedDialog extends Dialog implements android.view.View.OnClickListener {

	private ImageView mIvClose;
	private TextView mTvTitle;
	private TextView mTime;
	private TextView mTvFlag;
	private MyCenterPayDataBean data;
	private LinearLayout mLyTime;
	private LoginUserInfoBean loginInfo;
	
	public PlayFailedDialog(Context context) {
		super(context, R.style.dialog);
	}
	
	public PlayFailedDialog(Context context, MyCenterPayDataBean data) {
		this(context);
		this.data = data;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		setContentView(R.layout.play_failed_layout);
		setCanceledOnTouchOutside(false);
		setCancelable(false);
		LayoutParams attributes = getWindow().getAttributes();
		attributes.width = getScreenWidth() - 36 * 2;
		getWindow().setAttributes(attributes);
		initView();
	}
	
	private void initView() {
		mIvClose = (ImageView) findViewById(R.id.play_failed_iv_close);
		mTvTitle =(TextView) findViewById(R.id.play_failed_tv_title);
		mTime =(TextView) findViewById(R.id.play_failed_tv_time);
		mTvFlag =(TextView) findViewById(R.id.play_failed_tv_flag);
		mLyTime = (LinearLayout) findViewById(R.id.play_failed_ly_time);
		mIvClose.setOnClickListener(this);
		mTvTitle.setText(data.product_name);
		mLyTime.setVisibility(View.GONE);
		mTvFlag.setText("已失效");
	}

	private int getScreenWidth(){
		return getWindow().getWindowManager().getDefaultDisplay().getWidth();
	}
	
	public int getOrderNumber(){
		return data.id;
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mIvClose)) {
			if (isShowing()) {
				dismiss();
			}
		}
	}
}
