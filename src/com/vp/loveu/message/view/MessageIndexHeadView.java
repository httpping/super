package com.vp.loveu.message.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.bean.PushNoticeBean;
import com.vp.loveu.message.bean.PushNoticeBean.BubbleType;
import com.vp.loveu.message.db.PushNoticeBeanDao;
import com.vp.loveu.message.utils.BroadcastType;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.MsgSharePreferenceUtil;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.util.VPLog;

/***
 * 消息中心headview ，管理动态 邀请，评论等view
 * @author tanping
 * 2015-11-16
 */
public class MessageIndexHeadView  extends LinearLayout{
	
	/**
	 * 官方信息
	 */
	public OfficialNoticeView mOfficialNoticeView;
	
	/**
	 * 邀请回复
	 */
	public InviteNoticeView mInviteNoticeView;
	
	/**
	 * 评论回复
	 */
	public CommentNoticeView mCommentNoticeView;
	
	
	public UpdateViewBroast mBroast;
	public RunCheckUpdateThread updateThread;
	
	public MessageIndexHeadView(Context context) {
		super(context);
		initView();
	}

	
	public MessageIndexHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		
		if (isInEditMode()) {
			return ;
		}
		
		/*View padView  =new View(getContext());
		addView(padView);
		padView.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.public_title_height);
*/		
		setOrientation(VERTICAL);
		mOfficialNoticeView = new OfficialNoticeView(getContext());
		mOfficialNoticeView.initData();
		
		addView(mOfficialNoticeView);
		
		addView(createDivier());
		
		mInviteNoticeView = new InviteNoticeView(getContext());
		mInviteNoticeView.initData();
		addView(mInviteNoticeView);
		
		addView(createDivier());
		
		mCommentNoticeView = new CommentNoticeView(getContext());
		mCommentNoticeView.initData();
		addView(mCommentNoticeView);
		
		TextView remindView = new TextView(getContext());
		remindView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		remindView.setTextColor(Color.parseColor("#666666"));
		remindView.setText("最近联系人");
		remindView.setGravity(Gravity.LEFT|Gravity.BOTTOM);
		remindView.setPadding(UIUtils.dp2px(15), 0, 0, UIUtils.dp2px(5));
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, UIUtils.dp2px(40));
		remindView.setLayoutParams(params);
		
		addView(remindView);
		
		mBroast = new UpdateViewBroast();
		handler.sendEmptyMessage(UPDATE_THREAD);
	}
	
	
	private View createDivier(){
		View view = new View(getContext());
		view.setBackgroundResource(R.color.divide_line_bg);
		LayoutParams params = new LayoutParams(-1, 1);
		view.setLayoutParams(params);
		return view;
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		if (mBroast == null) {
			mBroast = new UpdateViewBroast();
		}
		getContext().registerReceiver(mBroast, new IntentFilter( BroadcastType.PUSH_NOTICE_RECEVIE));

	}
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		if (mBroast!=null) {
			getContext().unregisterReceiver(mBroast);
		}
	}
	
	/**
	 * 接收广播更新界面
	 * @author tanping
	 * 2015-12-1
	 */
	private class UpdateViewBroast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			 	 handler.removeMessages(UPDATE_THREAD);
			 	 handler.sendEmptyMessageDelayed(UPDATE_THREAD,300);//延迟300 防止不停的来消息，概率太小 。。
		}
		
	}
	 static final int UPDATE_THREAD =190;
	Handler handler =new Handler(){
		public void handleMessage(android.os.Message msg) {
			 if (msg.what == UPDATE_THREAD) {
				 updateThread = new RunCheckUpdateThread();
			 	 updateThread.start();//运行
			 }
		};
	};
	
	/**
	 * 检查更新线程
	 * @author tanping
	 * 2015-12-1
	 */
	private class RunCheckUpdateThread extends Thread{

		@Override
		public void run() {
			
			VPLog.d("push", "run === ");
			//查找通知，邀请解答，评论回复，的数字
			//气泡存储
			MsgSharePreferenceUtil msgSharePreferenceUtil;
			try {
				msgSharePreferenceUtil = new MsgSharePreferenceUtil(getContext(), "push_bubble");
				String key = PushNoticeBean.BUBBLE_TYPE_KEY +"" +BubbleType.comment.getValue();//评论
				final int value = msgSharePreferenceUtil.getIntValueForKey(key);
				
				LoginUserInfoBean mine = LoginStatus.getLoginInfo();
				if (mine == null) {
					mine = new LoginUserInfoBean(getContext());
				}
				final long noticeValue = PushNoticeBeanDao.getInstance(getContext()).getUnreadCount(mine.getUid()+"");//官方
				
				key = PushNoticeBean.BUBBLE_TYPE_KEY +"" +BubbleType.invite_reply.getValue();//邀请
				final int invValue = msgSharePreferenceUtil.getIntValueForKey(key);
				
				mInviteNoticeView.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mInviteNoticeView.updateRedPoint(invValue);
						mCommentNoticeView.updateRedPoint(value);
						mOfficialNoticeView.updateRedPoint((int)noticeValue);
					}
				}, 10);

			} catch (Exception e1) {
				e1.printStackTrace();
			}finally{
				updateThread = null;
			}
		}
		
	}
	
}
