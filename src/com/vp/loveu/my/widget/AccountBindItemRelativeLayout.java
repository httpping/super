package com.vp.loveu.my.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.my.activity.AccountBindActivity;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月1日下午1:06:57
 * @功能 账号绑定的item
 * @作者 mi
 */

public class AccountBindItemRelativeLayout extends RelativeLayout implements OnClickListener {
	
	public ImageView mIvIcon;
	public TextView mTvName;
	public TextView mTvState;
	public TextView mTvBind;
	private View mLine;
	
	public AccountBindItemRelativeLayout(Context context) {
		this(context, null);
	}

	public AccountBindItemRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.account_bind_item, this);
		initView();
	}

	private void initView() {
		mIvIcon = (ImageView) findViewById(R.id.account_bind_item_iv_icon);
		mTvName = (TextView) findViewById(R.id.account_bind_item_tv_name);
		mTvState = (TextView) findViewById(R.id.account_bind_item_tv_state);
		mTvBind = (TextView) findViewById(R.id.account_bind_item_tv_bind);
		mLine = findViewById(R.id.account_bind_item_line);
		mTvBind.setOnClickListener(this);
	}
	
//	public void setIvIcon(int result){
//		mIvIcon.setImageResource(result);
//	}
//	
//	public void setTvName(String name){
//		mTvName.setText(name);
//	}
//	
//	public void setTvState(String state){
//		mTvState.setText(state);
//	}
	
	public void setIsShowLine(boolean isShow){
		mLine.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		String name = mTvName.getText().toString().trim();
		if (name.equals(AccountBindActivity.names[0])) {
			
		}else if(name.equals(AccountBindActivity.names[1])){
			
		}else if(name.equals(AccountBindActivity.names[2])){
			
		}else if(name.equals(AccountBindActivity.names[3])){
			
		}
	}
}
