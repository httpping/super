package com.vp.loveu.message.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.db.ChatMessageDao;
import com.vp.loveu.message.view.ChatInImageView;
import com.vp.loveu.message.view.ChatInView;
import com.vp.loveu.message.view.ChatOutImgaeView;
import com.vp.loveu.message.view.ChatOutView;
import com.vp.loveu.message.view.ChatSystemTxtView;
import com.vp.loveu.message.view.IMsgUpdater;

/**
 * 消息列表
 * 
 * @author ping
 * 
 */
public class ChatListAdapter extends BaseAdapter {
	private Context cxt;
	private LayoutInflater inflater;
	private List<ChatMessage> listChatMessage;

	protected ChatMessageDao chatMessageDao;
	private ListView listView;
	public static final int NEED_WIDTH = 128 * 2;
	public static final int NEED_HEIGTH = 128 * 2;

	private ChatMessage curPlayChatMessage;// 当前播放的msg

	public List<ChatMessage> getListChatMessage() {
		return listChatMessage;
	}

	public ChatListAdapter(Context formClient, List<ChatMessage> list,
			ListView listView) {
		this.listView = listView;
		this.listChatMessage = list;
		this.cxt = formClient;
	}

	@Override
	public int getCount() {
		if (listChatMessage == null) {
			return 0;
		}
		return listChatMessage.size();
	}

	@Override
	public Object getItem(int position) {
		return listChatMessage.get(position);
	}

	@Override
	public int getViewTypeCount() {
		return 8;
	}

	@Override
	public int getItemViewType(int position) {
		return listChatMessage.get(position).showType;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ChatMessage message = listChatMessage.get(position);
		IMsgUpdater msgUpdater = null;
		if (convertView != null) {//有缓冲 直接缓冲
			//VPLog.d("chatlist", "post:"+position);
			msgUpdater = (IMsgUpdater) convertView;
			msgUpdater.setChatData(message);
			message.viewUpdate = msgUpdater;
			return convertView;
		}

		//没有缓冲的情况 
		if (message.showType == ChatMessage.MsgShowType.in.ordinal()) {
			ChatInView chatInView = new ChatInView(cxt) ;
			chatInView.setChatData(message);
			convertView = chatInView;
			return convertView;
		} else if (message.showType == ChatMessage.MsgShowType.system_txt.ordinal()) {//系统的消息
			ChatSystemTxtView chatSystemTxtView = new ChatSystemTxtView(cxt);
			chatSystemTxtView.setChatData(message);
			return chatSystemTxtView;
		}else  if (message.showType == ChatMessage.MsgShowType.out_img.ordinal()) {// 发送图片
			ChatOutImgaeView outImgaeView = new ChatOutImgaeView(cxt);
			outImgaeView.setChatData(message);
			return outImgaeView;
		}else  if (message.showType == ChatMessage.MsgShowType.in_img.ordinal()) {// 接收图片
			ChatInImageView inImgaeView = new ChatInImageView(cxt);
			inImgaeView.setChatData(message);
			return inImgaeView;
		}else if (message.showType == ChatMessage.MsgShowType.timestamp.ordinal()) {
			ChatSystemTxtView chatSystemTxtView = new ChatSystemTxtView(cxt);
			chatSystemTxtView.setChatData(message);	
			return chatSystemTxtView;
		} else { // 发送
			ChatOutView chatOutView = new ChatOutView(cxt);
			chatOutView.setChatData(message);
			convertView = chatOutView;
			return chatOutView;
		}

	}

}