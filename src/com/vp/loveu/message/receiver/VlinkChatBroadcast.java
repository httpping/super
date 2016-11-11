package com.vp.loveu.message.receiver;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.vp.loveu.R;
import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.db.ChatMessageDao;
import com.vp.loveu.message.db.ChatSessionDao;
import com.vp.loveu.message.provider.MessageSessionProvider;

/**
 * 接收私聊消息处理系统消息,接收统一的消息
 * @author  ping
 *
 */
public class VlinkChatBroadcast extends BroadcastReceiver {

	public static final String TAG ="VlinkChatBroadcast";
	
	AsyncPlayer asyncPlayer;
	 ExecutorService pools;
	 ChatSessionDao mChatSessionDao;
	 ChatMessageDao mChatMessageDao;
	//更新相应的 内容提供者view
	 ContentResolver contentProvider;
	 
	 AudioManager mAudioManager ;
	 Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (pools == null) {
			pools = Executors.newFixedThreadPool(1);
		}
		
		if (mChatMessageDao == null) {
			mChatMessageDao = ChatMessageDao.getInstance(context);
		}
		
		if (asyncPlayer == null) {
			asyncPlayer = new AsyncPlayer("chat");
		}
		
		if (mChatSessionDao==null) {
			mChatSessionDao = ChatSessionDao.getInstance(context);
		}
		
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		}
		
		//消息内容
		ChatMessage message = (ChatMessage) intent.getSerializableExtra("chat_message");
		Log.i("chat", "vlink:"+message);
		if (message == null) {
			return ;
		}
		
		int offline = intent.getIntExtra("off_line", -1);
		
		Log.i("chat", "offline:"+offline);

		
		//顺风包邮了  分发广播，如果有正在聊天的将会接收到此广播，为动态配置
		pools.execute(new ExecutorSend(message,offline));//派送员派送了，有1个线程派送，
	  
		//更新相应的 内容提供者view
		if (contentProvider == null) {
			contentProvider = context.getContentResolver();
		}
		
		
	}
	
	/**
	 * 遍地开发， 全城派送。
	 * @author tanping
	 *
	 */
	public  class ExecutorSend implements Runnable{
		
		public ChatMessage message ;
		public int offline ;
		public ExecutorSend(ChatMessage msg,int offline){
			this.message = msg;
			this.offline = offline;
		}
		
		@Override
		public void run() {
			
			if (message== null) {//没有内容
				return;
			}
			
			
			if (offline == 1) { // 离线
				Log.i("chat", Thread.currentThread()+ "vlink:");
				//mHandler.removeMessages(REFRESH_DATA);
				Message message = new Message();
				message.what = REFRESH_DATA;
				mHandler.sendMessageDelayed(message, 500);//500ms钟刷新一次，不要无限的刷
				//mHandler.sendEmptyMessage(CHECK_REFRESH_DATA);//检查上次刷新的时间，最长不能超过5s,一定要刷
			}else {
				int max = mAudioManager.getStreamVolume( AudioManager.STREAM_SYSTEM );
				Log.d("audio", "max:" +max);
				int lmax = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
				Log.d("audio", "lmax:" +lmax);
				int rmax = mAudioManager.getStreamVolume( AudioManager.STREAM_ALARM );
				Log.d("audio", "rmax:" +rmax);
				int cmax = mAudioManager.getStreamVolume( AudioManager.STREAM_VOICE_CALL );
				Log.d("audio", "cmax:" +cmax);
				int smax = mAudioManager.getStreamVolume( AudioManager.STREAM_RING );
				Log.d("audio", "smax:" +smax);

				if (lmax >0) {
					//asyncPlayer.play(getco, uri, looping, stream)
					Uri uri = Uri.parse("android.resource://com.eachgame.android/" + R.raw.msg_receive);
					asyncPlayer.play(mContext, uri , false, AudioManager.STREAM_MUSIC);
					Log.i("chat", "chat :" + uri);
					
				}
				if (contentProvider!=null) {//在线
					contentProvider.update(MessageSessionProvider.CONTENT_MSG_SESSION_URI, null, null, null);
				}
				
				//发送更新tab消息
				/*if (EGApplication.getInstance()!=null) {
					BroadcastHelper.sendBroadcast(EGApplication.getInstance(), BROADCAST_TYPE.UPDATE_TAB);
				}*/
			}
			

		}
		
	}
	
	
	
	public static final int REFRESH_DATA =178178;
	public static final int CHECK_REFRESH_DATA =1781780;//监测防治 不停发消息导致一直不帅新界面
	
	public long timestamp ;
	Handler mHandler =new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CHECK_REFRESH_DATA:
				 if (timestamp -System.currentTimeMillis() > 5000) {
					//null 让swatch穿透
					 timestamp= System.currentTimeMillis();
				 }else {
					 return;
				 }
			case REFRESH_DATA:	
				if (contentProvider!=null) {
					timestamp = System.currentTimeMillis();
					 
				}
				
				break;

			default:
				break;
			}
		};
	};
}
