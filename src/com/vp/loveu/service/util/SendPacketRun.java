package com.vp.loveu.service.util;

import org.jivesoftware.smackx.chatstates.ChatState;


/**
 * 
 * @author tanping
 * 2015-10-30
 */
public abstract class SendPacketRun implements Runnable{
	
	public SendPacketRun.VpSendPacket mVpPacket;
	
	public SendPacketRun(VpSendPacket vpPacket) {
		this.mVpPacket = vpPacket;
	}
	
	
	
	
	
	
	
	/**
	 * 发送包裹 封装
	 * @author tanping
	 * 2015-10-30
	 */
	public static class VpSendPacket{
		
		public String packet; //包体
		public String command;// 命令 标识
		public SendPacketListener mSendPacketListener;//结果回调
		public String to;
		public ChatState mChatState;//聊天状态
		public int result ;//结果
		
		
		public VpSendPacket(String to,String packet ,String command , SendPacketListener listener){
			this.packet = packet;
			this.command = command;
			this.mSendPacketListener = listener;
			this.to = to;
		}
		
		public VpSendPacket(String to,String packet,ChatState state ,String command , SendPacketListener listener){
			this.packet = packet;
			this.command = command;
			this.mSendPacketListener = listener;
			this.to = to;
			this.mChatState = state;
		}

		@Override
		public String toString() {
			return "VpSendPacket [packet=" + packet + ", command=" + command
					+ ", mSendPacketListener=" + mSendPacketListener + ", to="
					+ to + ", mChatState=" + mChatState + ", result=" + result
					+ "]";
		}

		 
	}
 
	
	/**
	 * sendback 发送消息包体 操作
	 * @author tanping
	 *
	 */
	public interface SendPacketListener {

		/**
		 *  result >=0 成功   <=0失败 
		 * @param command
		 * @param result
		 * @param packet
		 */
		public void onFinish(String command,int result,String packet);//完成
		
	}

}
