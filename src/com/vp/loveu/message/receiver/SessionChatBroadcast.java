package com.vp.loveu.message.receiver;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.bean.ChatSessionBean;

/**
 * session 会话界面
 * @author  ping
 *
 */
public class SessionChatBroadcast extends BroadcastReceiver {

	public static final String TAG ="SessionChatBroadcast";
	
	ExecutorService pools;
	public  Handler mHandler;
	private List<ChatSessionBean> sessionBeans;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (pools == null) {
			pools = Executors.newFixedThreadPool(1);
		}
		//消息内容
		 ChatMessage chatMessage = (ChatMessage) intent.getSerializableExtra("chat_message");
		//Log.i("chat", "vlink:"+message);
		if (chatMessage == null) {
			return ;
		}
		//处理
		pools.execute(new ExecutorSend(chatMessage));//派送员派送了，有1个线程派送，
	}
	
	
	
	/**
	 * 遍地开发， 全城派送。
	 * @author tanping
	 *
	 */
	public  class ExecutorSend implements Runnable{
		
		public ChatMessage message ;
		public ExecutorSend(ChatMessage message){
			this.message = message;
		}
		
		@Override
		public void run() {
			ChatSessionBean bean = ChatSessionBean.chatToSession(message);
			if (bean== null || sessionBeans == null) {
				return;
			}
			
//		  	for (int i = 0; i < sessionBeans.size(); i++) {
//				ChatSessionBean sessionBean = sessionBeans.get(i);
//				if (bean.equals(sessionBean)) {
//					bean.count = sessionBean.count + 1;
//					mHandler.sendEmptyMessage(what)
//				}
//			}
			
			
		}
		
	}
}
