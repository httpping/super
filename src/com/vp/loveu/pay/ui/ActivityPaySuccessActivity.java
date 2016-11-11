package com.vp.loveu.pay.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.vp.loveu.MainActivity;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.my.activity.MyCenterPlayListActivity;

public class ActivityPaySuccessActivity extends VpActivity implements OnClickListener{

	public TextView indexBtnTextView;
	public TextView myOrders;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_activity_activity_pay_success);
		
		initView();
	}

	private void initView() {
		initPublicTitle();
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText("支付报名");
		
		myOrders = (TextView) findViewById(R.id.order_info_btn);
		indexBtnTextView = (TextView) findViewById(R.id.index_go_btn);
		
		myOrders.setOnClickListener(this);
		indexBtnTextView.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		if (v.equals( myOrders)) {
			Intent intent = new Intent(this,MyCenterPlayListActivity.class);
			startActivity(intent);
			finish();
		}else if (v.equals(indexBtnTextView)) {
			Intent intent = new Intent(this,MainActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
