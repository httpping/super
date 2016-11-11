package com.vp.loveu.message.view;

import com.vp.loveu.message.bean.ChatMessage;

/**
 * 聊天消息 adapter 用到的相关view
 * @author tanping
 *
 */
public interface IMsgUpdater {
	
	public void setChatData(ChatMessage message); //设置data 数据
	
	public void drawView(); //重画view

	
}
