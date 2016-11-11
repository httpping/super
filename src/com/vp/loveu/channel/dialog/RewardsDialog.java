package com.vp.loveu.channel.dialog;

import com.vp.loveu.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * @项目名称nameloveu1.0
 * @时间2016年3月1日下午12:13:04
 * @功能 打赏的dialog
 * @作者 mi
 */

public class RewardsDialog extends Dialog implements android.view.View.OnClickListener {
	
	Button mGoPay;

	public RewardsDialog(Context context, int theme) {
		super(context, theme);
	}

	public RewardsDialog(Context context) {
		this(context,R.style.dialog);
		View view = LayoutInflater.from(context).inflate(R.layout.rewards_dialog, null);
		initView(view);
		setContentView(view);
	}

	private void initView(View view) {
		mGoPay = (Button) view.findViewById(R.id.rewards_btn_gopay);
		
		mGoPay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rewards_btn_gopay:
			//TODO: 去支付
			break;

		default:
			break;
		}
	}
}
