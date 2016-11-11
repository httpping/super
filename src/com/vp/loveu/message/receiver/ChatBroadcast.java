package com.vp.loveu.message.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.db.ChatMessageDao;
import com.vp.loveu.message.ui.PrivateChatActivity;

/**
 * 接收指定人的聊天信息
 * 
 * @author Administrator
 * 
 */
public class ChatBroadcast extends BroadcastReceiver {
	public static final String TAG = "ChatBroadcast";
	private Handler handler;// handler
	private String to;// 发给谁 接收人
	
	ChatMessageDao mChatMessageDao ;
	
	
	public void onReceive(Context context, Intent intent) {

		//消息内容
		ChatMessage chatMessage = (ChatMessage) intent.getSerializableExtra("chat_message");
		Log.d("ChatBroadcast", "chat:"+chatMessage);
		if (chatMessage == null || handler == null) {
			return ;
		}
		//暂时不需要扩展
		//chatMessage.showType = ChatMessage.MsgShowType.in.ordinal();// 输入
		
		chatMessage.readStatus = ChatMessage.MsgReadStatus.read.ordinal();
		
		if (mChatMessageDao== null) {
			mChatMessageDao = ChatMessageDao.getInstance(context);
		}
		mChatMessageDao.saveOrUpdate(chatMessage);//更新数据库，已读
		
		Message msg = new Message();
		msg.what = PrivateChatActivity.RECEIVE_MSG;
		msg.obj = chatMessage;
		handler.sendMessage(msg);//发出来了
	}

	public ChatBroadcast(Handler handler,String to) {
		super();
		this.handler = handler;
		this.to = to;
	
	}
	
	@Override
	public boolean equals(Object o) {
		 if (o instanceof ChatBroadcast) {
			 ChatBroadcast broadcast = (ChatBroadcast) o;
			 if ( to ==broadcast.to ) {
				return true;
			 }
		 } 
		 return false;
	}
}