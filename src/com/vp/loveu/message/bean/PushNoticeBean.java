package com.vp.loveu.message.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.vp.loveu.message.utils.BroadcastType;
import com.vp.loveu.util.VPLog;

/**
 * 推送通知bean
 * @author tanping
 * 2015-11-25
 */
public class PushNoticeBean implements Serializable {
	
	public static final String TABLE_NAME ="push_notice_table";
	public static final String ID ="_id";
	public static final String TIMESTAMP ="timestamp";
	public static final String BODY ="body";
	public static final String LOGIN_USER_ID ="login_user_id";
	public static final String READ_STATUE ="read_status";
	
	
	public static final String  COMMENT_REPAY_BUBBLE="comment_repay_bubble";//评论回复气泡
	public static final String  INVITE_ANSWER_BUBBLE="Invite_Answer_BUBBLE";//邀请解答气泡
	public static final String  BUBBLE_TYPE_KEY="bubble_type_key_";//存储 气泡数据的key 前缀 加上 type的值


	

	public long timestamp;
	public long expire_time;// 过期
	public String body ; 
	
	public int noticeType ;
	public int pushType ;
	public int bubbleType ;
	
	public String txt ;
	public String url ;
	public String loginId;
	public int id ;
	public int readStatus =0;
	
	
	public static PushNoticeBean parsePushJson(String strJson){
		PushNoticeBean bean = new PushNoticeBean();
		
		try {
			
			VPLog.d("push", strJson+"");
			JSONObject json = new JSONObject(strJson);
			
			
			bean.pushType = json.getInt("type"); //推送类型
			JSONObject subjson = new JSONObject(json.getString("body"));

			if (bean.pushType == PushType.bubble.value) {//气泡
				bean.bubbleType = subjson.getInt("type");
				bean.txt = subjson.optString("txt");//文本描述
			}else {
				bean.paseNotcie(subjson.toString());
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return bean;
	}
	
	public void paseNotcie(String strJson){
		JSONObject json = null;
		try {
			json = new JSONObject(strJson);
			noticeType =  json.getInt("type");
			this.body = json.toString(); //用于存数据库
			txt = json.optString("txt");
			url = json.optString("url");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
	}
	
	
	/**
	 * 推送类型
	 * @author tanping
	 * 2015-11-25
	 */
	public enum PushType {
		notice(1),bubble(2);
		private final int value ;
		PushType(int value){
			this.value = value;
		}
		public int getValue() {
			return value;
		}
	};
	
	
	/**
	 * 气泡类型
	 * @author tanping
	 * 2015-11-25
	 */
	public enum BubbleType {
		comment(1),invite_reply(2),praise(3),join_activity(4),close_app(5);
		private final int value ;
		BubbleType(int value){
			this.value = value;
		}
		public int getValue() {
			return value;
		}
	};
	
	/**
	 * 通知类型 文本，网页 等其他
	 * @author tanping
	 * 2015-11-25
	 */
	public enum NoticeType {
		text(1),net_url(2);
		private final int value ;
		NoticeType(int value){
			this.value = value;
		}
		public int getValue() {
			return value;
		}
	}

	@Override
	public String toString() {
		return "PushNoticeBean [timestamp=" + timestamp + ", expire_time="
				+ expire_time + ", body=" + body + ", noticeType=" + noticeType
				+ ", pushType=" + pushType + ", bubbleType=" + bubbleType
				+ ", txt=" + txt + ", url=" + url + ", loginId=" + loginId
				+ ", id=" + id + ", readStatus=" + readStatus + "]";
	};
	
	
	/**
	 * 发送更新update广播
	 * @param mContext
	 */
	public static void sendUpdateBroadcast(Context mContext){
		if (mContext==null) {
			return ;
		}
		//发广播
		String action = BroadcastType.PUSH_NOTICE_RECEVIE;
		Intent intent = new Intent(action);
		mContext.sendBroadcast(intent);
	}
	
	
}
