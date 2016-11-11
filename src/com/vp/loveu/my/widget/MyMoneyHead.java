package com.vp.loveu.my.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.message.utils.DensityUtil;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月12日下午2:23:11
 * @功能 我的钱包的头部
 * @作者 mi
 */

public class MyMoneyHead extends LinearLayout {
	
	private TextView mTvMoney;

	public MyMoneyHead(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.mycenter_pay_head, this);
		mTvMoney = (TextView) findViewById(R.id.wallet_tv_money);
	}

	public MyMoneyHead(Context context) {
		this(context,null);
	}
	
	public void setMoney(double money){
		
		mTvMoney.setSelected(true);
		
		String mString = money +"元";
		
		int len = mString.length();
		
		int size = DensityUtil.dip2px(getContext(), 120)/(len/2+1);
		mTvMoney.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
		mTvMoney.setText(mString);
		
	}
}
