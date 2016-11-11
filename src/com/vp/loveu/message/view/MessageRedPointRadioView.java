package com.vp.loveu.message.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.bean.PushNoticeBean;
import com.vp.loveu.message.bean.PushNoticeBean.BubbleType;
import com.vp.loveu.message.db.ChatSessionDao;
import com.vp.loveu.message.db.PushNoticeBeanDao;
import com.vp.loveu.message.utils.BroadcastType;
import com.vp.loveu.message.utils.DensityUtil;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.MsgSharePreferenceUtil;
import com.vp.loveu.util.VPLog;

/**
 * 带红点的RadioButton
 * 
 * @author ping
 * 
 */
public class MessageRedPointRadioView extends RadioButton {
	public String rightText;
	public String redBgColor = "#FF7A00";
	
	public UpdateViewBroast mBroast;
	public RunCheckUpdateThread updateThread;
	
	public MessageRedPointRadioView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public MessageRedPointRadioView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (rightText != null) {
			
			Paint paint = new Paint();
			paint.setColor(Color.parseColor(redBgColor));
			int maxr = getWidth();
			paint.setAntiAlias(true);// 抗锯齿
			paint.setStyle(Style.FILL); // 描边
			paint.setStrokeWidth(1);
			int r = DensityUtil.dip2px(getContext(), 4.5f); // 半径

			int cx = (getWidth()+ r)/2+r;
			VPLog.d("draw_red", "cx:"+cx +" r:"+r);
			// 画圆
			canvas.drawCircle(cx, r, r, paint);
			
		}
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		if (mBroast == null) {
			mBroast = new UpdateViewBroast();
		}
		getContext().registerReceiver(mBroast, new IntentFilter( BroadcastType.PUSH_NOTICE_RECEVIE));
		handler.removeMessages(UPDATE_THREAD);
	 	handler.sendEmptyMessageDelayed(UPDATE_THREAD,100);//延迟300 防止不停的来消息，概率太小 。。

	}
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		if (mBroast!=null) {
			getContext().unregisterReceiver(mBroast);
		}
	}

	public String getRightText() {
		return rightText;
	}

	public void setRightText(String rightText) {
		this.rightText = rightText;
		invalidate();
	}

	public String getRedBgColor() {
		return redBgColor;
	}

	public void setRedBgColor(String redBgColor) {
		this.redBgColor = redBgColor;
		invalidate();
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
	 static final int UPDATE_THREAD =1990;
	 static final int UPDATE_THREAD_VIEW =1991;

	Handler handler =new Handler(){
		public void handleMessage(android.os.Message msg) {
			 if (msg.what == UPDATE_THREAD) {
				 updateThread = new RunCheckUpdateThread();
			 	 updateThread.start();//运行
			 }else {
				invalidate();
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
			
			
			//查找通知，邀请解答，评论回复，的数字
			//气泡存储
			MsgSharePreferenceUtil msgSharePreferenceUtil;
			try {
				msgSharePreferenceUtil = new MsgSharePreferenceUtil(getContext(), "push_bubble");
				String key = PushNoticeBean.BUBBLE_TYPE_KEY +"" +BubbleType.comment.getValue();//评论
				final int value = msgSharePreferenceUtil.getIntValueForKey(key);
				
				if (value>0) {
					rightText =value+"";
					return;
				}
				
				LoginUserInfoBean mine = LoginStatus.getLoginInfo();
				if (mine == null) {
					mine = new LoginUserInfoBean(getContext());
				}
				final long noticeValue = PushNoticeBeanDao.getInstance(getContext()).getUnreadCount(mine.getUid()+"");//官方
				if (noticeValue>0) {
					rightText =noticeValue+"";
					return;
				}
				key = PushNoticeBean.BUBBLE_TYPE_KEY +"" +BubbleType.invite_reply.getValue();//邀请
				final int invValue = msgSharePreferenceUtil.getIntValueForKey(key);
				if (invValue>0) {
					rightText =invValue+"";
					return;
				}
				
				long chatNum = ChatSessionDao.getInstance(getContext()).getUserUnReadChatCount(mine.getXmpp_user().toLowerCase());
				VPLog.d("draw_red", "run:" +chatNum);
				if (chatNum >0) {
					rightText = chatNum +"";
					return ;
				}
				rightText =null;
				

			} catch (Exception e1) {
				e1.printStackTrace();
			}finally{
				updateThread = null;
				handler.sendEmptyMessage(UPDATE_THREAD_VIEW);
			}
		}
		
	}

}
