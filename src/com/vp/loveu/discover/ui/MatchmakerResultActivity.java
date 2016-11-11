package com.vp.loveu.discover.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;

/**
 * @author：pzj
 * @date: 2015年11月23日 下午2:09:50
 * @Description:红娘一对一发送成功页面
 */
public class MatchmakerResultActivity extends VpActivity  {
	private  TextView mTvBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discover_matchmaker_result_activity);
		initView();
	}


	private void initView() {
		initPublicTitle();
		this.mPubTitleView.mBtnLeft.setText("");
		this.mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		this.mPubTitleView.mTvTitle.setText("红娘一对一");
		mTvBack=(TextView) findViewById(R.id.discover_matchmaker_back);
		mTvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	}


	
	
}
