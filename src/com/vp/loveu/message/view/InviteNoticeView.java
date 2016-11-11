package com.vp.loveu.message.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import com.vp.loveu.R;
import com.vp.loveu.message.bean.PushNoticeBean;
import com.vp.loveu.message.ui.ReplyFellHelpActivity;
import com.vp.loveu.util.MsgSharePreferenceUtil;

/**
 * 邀请
 * @author tanping
 * 2015-11-16
 */
public class InviteNoticeView extends NoticeItemView {

	public InviteNoticeView(Context context) {
		super(context);
	}
	
	public InviteNoticeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	
	
	public void initData(){
		if (isInEditMode()) {
			return ;
		}
		leftImageView.setImageResource(R.drawable.message_invite_requst_icon);
		centerTextView.setText("邀请情感解答");
		redPoint.setText("1");
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {//删除气泡
					MsgSharePreferenceUtil msgSharePreferenceUtil = new MsgSharePreferenceUtil(getContext(), "push_bubble");
					msgSharePreferenceUtil.removeKey(PushNoticeBean.BUBBLE_TYPE_KEY + PushNoticeBean.BubbleType.invite_reply.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}//气泡
				Intent intent = new Intent(getContext(),ReplyFellHelpActivity.class);
				getContext().startActivity(intent);
			}
		});
		
	}
	
}
