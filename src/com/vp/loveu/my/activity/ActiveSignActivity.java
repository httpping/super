package com.vp.loveu.my.activity;

import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.zbar.lib.CaptureActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @项目名称nameloveu1.0
 * @时间2016年3月24日下午4:04:15
 * @功能 活动签到
 * @作者 mi
 */

public class ActiveSignActivity extends VpActivity implements OnClickListener {

	EditText phoneNumber;
	Button mStartScan;
	TextView mTvHint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_activesign);
		initPublicTitle();
		initView();
	}

	private void initView() {
		mPubTitleView.mTvTitle.setText("活动签到");
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		phoneNumber = (EditText) findViewById(R.id.phone_number);
		mStartScan = (Button) findViewById(R.id.start_scan);
		mTvHint = (TextView) findViewById(R.id.tv_hint);
		mStartScan.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String trim = phoneNumber.getText().toString().trim();
		if (TextUtils.isEmpty(trim)) {
			mTvHint.setText("请输入手机号码");
			return;
		}
		if(!isPhoneNumberValid(trim)){
			mTvHint.setText("手机号码格式不正确");
			return;
		};
		Intent intent = new Intent(this, CaptureActivity.class);
		intent.putExtra("mt", trim);
		startActivity(intent);
		mTvHint.setText("");
	}
}
