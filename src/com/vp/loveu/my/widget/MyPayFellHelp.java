package com.vp.loveu.my.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.index.activity.MyFellHelpActivity;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.my.bean.MyCenterPayBean.MyCenterPayDataBean;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月14日上午10:08:31
 * @功能TODO
 * @作者 mi
 */

public class MyPayFellHelp extends LinearLayout {

	private TextView mTvTime;
	private TextView mTvContent;
	private Button mBtPacket;
	private ImageView mIvIn;
	
	public MyPayFellHelp(Context context) {
		this(context,null);
	}
	
	public MyPayFellHelp(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.my_pay_item_two, this);
		initView();
	}

	private void initView() {
		mTvTime = (TextView) findViewById(R.id.my_pay_item_two_tv_time);
		mTvContent = (TextView) findViewById(R.id.my_pay_item_two_tv_content);
		mBtPacket = (Button) findViewById(R.id.my_pay_item_two_btn_packet);
		mIvIn = (ImageView) findViewById(R.id.my_pay_item_two_iv_in);
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), MyFellHelpActivity.class);
				getContext().startActivity(intent);
			}
		});
	}

	public void setData(final MyCenterPayDataBean data) {
		if (data == null) {
			Log.d("aaa", "aaa");
			return ;
		}
		mBtPacket.setText(data.price+"元");
		mTvTime.setText(MyUtils.dateFromLong(data.create_time));
	}
}
