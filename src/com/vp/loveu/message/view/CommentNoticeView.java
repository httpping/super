package com.vp.loveu.message.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import com.vp.loveu.R;
import com.vp.loveu.message.bean.PushNoticeBean;
import com.vp.loveu.message.ui.CommenNoticListActivity;
import com.vp.loveu.util.MsgSharePreferenceUtil;

/**
 *评论回复
 * @author tanping
 * 2015-11-16
 */
public class CommentNoticeView extends NoticeItemView {

	public CommentNoticeView(Context context) {
		super(context);
	}
	
	public CommentNoticeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void initData(){
		if (isInEditMode()) {
			return ;
		}
		leftImageView.setImageResource(R.drawable.message_comment_request_icon);
		centerTextView.setText("评论回复");
		redPoint.setVisibility(GONE);
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {//删除气泡
					MsgSharePreferenceUtil msgSharePreferenceUtil = new MsgSharePreferenceUtil(getContext(), "push_bubble");
					msgSharePreferenceUtil.removeKey(PushNoticeBean.BUBBLE_TYPE_KEY + PushNoticeBean.BubbleType.comment.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}//气泡
				getContext().startActivity(new Intent(getContext(), CommenNoticListActivity.class));
			}
		});
	}
	
}
