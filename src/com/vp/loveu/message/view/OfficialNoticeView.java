package com.vp.loveu.message.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import com.vp.loveu.R;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.db.PushNoticeBeanDao;
import com.vp.loveu.message.ui.PushNoticeListActivity;
import com.vp.loveu.util.LoginStatus;

/**
 * 官方动态view
 * 管理 自己的红点信息
 * @author tanping
 * 2015-11-16
 */
public class OfficialNoticeView extends NoticeItemView {

	public OfficialNoticeView(Context context) {
		super(context);
	}
	
	public OfficialNoticeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	
	
	public void initData(){
		
		if (isInEditMode()) {
			return ;
		}
		leftImageView.setImageResource(R.drawable.message_official_notice_icon);
		centerTextView.setText("爱U官方通知");
		redPoint.setText("5");
		
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LoginUserInfoBean mine = LoginStatus.getLoginInfo();
				if (mine == null) {
					mine = new LoginUserInfoBean(getContext());
				}
				PushNoticeBeanDao.getInstance(getContext()).setReadMsg(mine.getUid()+"");
				updateRedPoint(0);
				Intent intent = new Intent(getContext(),PushNoticeListActivity.class);
				getContext().startActivity(intent);
			}
		});
	}
	
	
}
