package com.vp.loveu.message.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.SpannableString;

import com.loopj.android.http.ResultParseUtil;
import com.vp.loveu.message.utils.XmppUtils;
import com.vp.loveu.message.view.IMsgUpdater;
import com.vp.loveu.util.VPLog;

/**
 * 消息格式
 * 
 * @author 谭平
 * 
 */
public class ChatMessage implements Serializable {
	public static final String TAG = "ChatMessage";

	public static final String TABLE_NAME = "loveu_chat_msg";

	public String txt; // 消息内容
	public long id;// 消息id
	public UserInfo fromUserInfo; //只关注from的用户信息，对于view就是展现图片内容的信息 user ,to的用户信息 自己关注
	

	public long timestamp = System.currentTimeMillis()/1000; // 时间戳
	public int readStatus = MsgReadStatus.read.ordinal();// 默认未读
	public int sendStatus = MsgSendStatus.send.ordinal();//
	public int msgType = MsgType.txt.value;// 　默认为不同文本
	public int showType;// show 类型
	public String aduioUrl; //  url
	public String imgUrl; //  url
	public String sendImgUrl ;// 发送时上传文件得到的url
	public String locImgUrl;
	
	public String from; //   发送人 xmppuser
	public String to;// 接收人 xmppuser

	
	public double lon;
	public double lat ;

	//other 
	
	public String body;

	public String loginUser;// xmpp user
	public String otherUser;// xmpp user
	
	public int postion;
	
	public IMsgUpdater viewUpdate;//更新view

	public int width;
	public int height;
	
	public SpannableString mSpannableString;
	
	public static final int MAX_WIGHT = 600;
	public static final int MAX_HEIGHT = 480;

	public ChatMessage(){
		VPLog.d(TAG, "time:"+ ResultParseUtil.timeinterval);
		  timestamp =  System.currentTimeMillis()/1000 + ResultParseUtil.timeinterval/1000;//时间矫正
	}
	
	/**
	 * 消息类型,0、推送,1、文本、2语音，3、图片，4、评论, .....
	 */
	public static enum MsgType {
		txt(1), audio(2),img(3),exchange_user_info(4),maps(5),report_status(200),welcome_service(201),get_online_remind(202),clear_all_msg(10086);
		private final int value;

		private MsgType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	/**
	 * 消息读取状态
	 * 
	 * @author tanping
	 * 
	 */
	public enum MsgReadStatus {
		none, unread, read;
	}

	public enum MsgSendStatus {
		draft,send, fail, success;
	}

	/**
	 * view 的 show 类型
	 * 
	 * @author tanping
	 */
	public enum MsgShowType {
		in,in_img,in_map, out,out_img,out_map, system_txt,timestamp
	}

 
	public static final String FROM_NAME = "from_name";
	public static final String TO_NAME = "to_name";
	public static final String MSG_TYPE = "msg_type";
	public static final String BODY = "body";
	public static final String BODY_TXT = "txt";
	public static final String URL = "url";
	public static final String SHOW_TYPE = "show_type";//
	public static final String TIMESTAMP = "timestamp";//
	public static final String READ_STATUE = "read_statue";//
	public static final String SEND_MSG_STATUS = "send_status";
	public static final String SHOW_ID = "show_id";//
	public static final String LOGIN_USER_NAME = "login_user_name";//
	public static final String CHAT_OTHER_USER_NAME = "chat_user_name";// 聊天的对象id
																	// 用来做group

	public static final String ID = "_id";

	public static final int NEED_WIDTH = 600; // 图片最大的高度和宽度
	public static final int NEED_HEIGTH = 600;

	public static final String FROM_JSON = "from";

 


	 

	/**
	 * 解析json
	 * 
	 * @param value
	 * @return
	 * 
	 *         "msg_id": 9341, "msg_type": 105001, "msg": "abcdefg",
	 *         "timestamp": 1434703781, "recv_id": 100127, "send_id": 100188,
	 *         "send_username": "abc", "send_headimg": "www.headimg.com",
	 *         "show_pic": "www.show.com"
	 */
	public static ChatMessage parseJson(String value) {
		ChatMessage message = new ChatMessage();

		try { 
			JSONObject data = new JSONObject(value);
			message.fromUserInfo = UserInfo.parseJson(data.getString(FROM_JSON));
			message.to = data.optString("to");
			message.msgType = data.getInt("msg_type");
			message.timestamp = data.getLong(TIMESTAMP);
			message.body = data.getString("body");
			
			message.otherUser = XmppUtils.getJidToUsername(message.fromUserInfo.xmppUser).toLowerCase();
			message.parseBody();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return message;
	}

	public static List<ChatMessage> parseJsonArr(String arr) {
		List<ChatMessage> messages = new LinkedList<ChatMessage>();
		try {

			JSONArray jsonArray = new JSONArray(arr);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				messages.add(parseJson(object.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return messages;
	}

	/**
	 * 通过 时间戳 查找 聊天消息
	 * 
	 * @param timestamp
	 * @param messages
	 * @return
	 */
	public static ChatMessage findMessageByTimestamp(long timestamp,
			List<ChatMessage> messages) {
		if (messages == null) {
			return null;
		}

		for (int i = messages.size() - 1; i >= 0; i--) {
			ChatMessage message2 = messages.get(i);
			if (message2.timestamp == timestamp) {
				return message2;
			}
		}

		return null;
	}

	/**
	 * 通过 时间戳 查找 聊天消息
	 * 
	 * @param timestamp
	 * @param messages
	 * @return
	 */
	public static String getLastTimeStamp(List<ChatMessage> messages) {
		if (messages == null) {
			return null;
		}

		try {
			ChatMessage message = messages.get(messages.size() - 1);
			return message.timestamp + "";
		} catch (Exception e) {
		}

		return null;
	}
	
	/**
	 * 通过 时间戳 查找 聊天消息
	 * 
	 * @param timestamp
	 * @param messages
	 * @return
	 */
	public static ArrayList<String> findImgages(List<ChatMessage> messages) {
		if (messages == null) {
			return null;
		}
		
		ArrayList<String> results = new ArrayList<String>();
		for (int i = 0; i < messages.size(); i++) {
			if (messages.get(i).msgType == MsgType.img.value) {
				 results.add(messages.get(i).imgUrl);
			}
		}

		return results;
	}

	/**
	 * 查找最后一条消息 不为系统类时间戳
	 * 
	 * @param timestamp
	 * @param messages
	 * @return
	 */
	public static ChatMessage getLastMessageIsNotTimesteamp(
			List<ChatMessage> messages) {
		if (messages == null || messages.size() == 0) {
			return null;
		}

		try {
			ChatMessage message = messages.get(messages.size() - 1);
			if (message.showType == MsgShowType.system_txt.ordinal()) {// 系统消息，不要加时间
				try {
					Long.parseLong(message.txt); // 能解析表示是时间。
					return null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return message;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return messages.get(messages.size() -1);

	}

	/**
	 * 获取最大的消息id
	 * 
	 * @param timestamp
	 * @param messages
	 * @return
	 */
	public static long getMaxMessageId(List<ChatMessage> messages) {
		if (messages == null) {
			return 0;
		}

		for (int i = messages.size() - 1; i >= 0; i--) {
			ChatMessage message2 = messages.get(i);
			if (message2.id != 0) {
				return message2.id;
			}
		}

		return 0;
	}

	/**
	 * 获取最后一条非系统的消息
	 * 
	 * @param timestamp
	 * @param messages
	 * @return
	 */
	public static ChatMessage getLastMesageNoSystem(List<ChatMessage> messages) {
		if (messages == null) {
			return null;
		}

		for (int i = messages.size() - 1; i >= 0; i--) {
			ChatMessage message2 = messages.get(i);
			if (message2.showType == MsgShowType.in.ordinal()
					|| message2.showType == MsgShowType.out.ordinal()) {
				return message2;
			}
		}

		return null;
	}


	/**
	 * 创建提醒类消息
	 * 
	 * @param loginUser
	 * @param chatUser
	 * @param body
	 * @return
	 */
	public static ChatMessage createRemindMessage(String loginUser, String chatUser,
			String body) {
		ChatMessage remindChatMessage = new ChatMessage();
		remindChatMessage.loginUser = loginUser;
		remindChatMessage.otherUser = chatUser;
		remindChatMessage.txt = body;
		remindChatMessage.body = body;
		remindChatMessage.readStatus = MsgReadStatus.read.ordinal();// 已读
		remindChatMessage.showType = ChatMessage.MsgShowType.timestamp
				.ordinal();// 提醒信息
		remindChatMessage.timestamp = System.currentTimeMillis();

		return remindChatMessage;
	}
	
	/**
	 * 复制消息
	 * 
	 * @param loginUser
	 * @param chatUser
	 * @param body
	 * @return
	 */
	public static ChatMessage copyMessage(ChatMessage message) {
		ChatMessage paopaoMessage = new ChatMessage();
		paopaoMessage.loginUser = message.loginUser;
		paopaoMessage.to = message.to;
		paopaoMessage.txt = message.txt;
		paopaoMessage.readStatus = message.readStatus;// 已读
		paopaoMessage.showType = message.showType;
		
		paopaoMessage.aduioUrl = message.aduioUrl;
		paopaoMessage.imgUrl = message.imgUrl;
		 
		paopaoMessage.timestamp = System.currentTimeMillis();

		return paopaoMessage;
	}

	/**
	 * to json,发送文本，图片消息
	 * @return
	 */
	public  JSONObject toJsonObject(){
		JSONObject data = new JSONObject();
		try {
			data.put(FROM_JSON, fromUserInfo.toJsonObject());
			data.put(TIMESTAMP, timestamp);
			data.put(TO_NAME, to);
			data.put(MSG_TYPE, msgType);
			
			createBody();
			 
			data.put(BODY, new JSONObject(body));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return data;
	}
	
	public void createBody(){
		
		try {
			JSONObject body = new JSONObject();
			if (msgType == MsgType.txt.value || msgType == MsgType.img.value || msgType == MsgType.audio.value || msgType == MsgType.maps.value) {
				body.put("txt", txt);
				body.put("img", imgUrl);
				body.put("audio", aduioUrl);
				
				if (msgType == MsgType.img.value|| msgType == MsgType.maps.value) {
					body.put("w", width);
					body.put("h", height);
				}
				
				if (msgType == MsgType.maps.value) {
					body.put("lon", lon);
					body.put("lat", lat);
				}
			}
			this.body = body.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void parseBody(){
		ChatMessage message = this;
		//解析json
		if (message.msgType == MsgType.txt.value || message.msgType == MsgType.img.value || message.msgType == MsgType.audio.value  || msgType == MsgType.maps.value) {
			JSONObject bodyJsonObject;
			try {
				bodyJsonObject = new JSONObject(body);
				message.txt = bodyJsonObject.optString("txt");
				message.imgUrl = bodyJsonObject.optString("img");
				message.aduioUrl = bodyJsonObject.optString("audio");
				message.lon = bodyJsonObject.optDouble("lon");
				message.lat = bodyJsonObject.optDouble("lat");
				message.width = bodyJsonObject.optInt("w");
				message.height = bodyJsonObject.optInt("h");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * 根据id find message
	 * @param messages
	 * @param id
	 * @return
	 */
	public static ChatMessage findById(List<ChatMessage> messages,long id){
		if (messages == null) {
			return null;
		}
		for (ChatMessage message : messages){
			if (message!=null && message.id == id) {
				return message;
			}
		}
		return null;
	}

 
	
	@Override
	public String toString() {
		return "ChatMessage [txt=" + txt + ", id=" + id + ", fromUserInfo="
				+ fromUserInfo + ", timestamp=" + timestamp + ", readStatus="
				+ readStatus + ", sendStatus=" + sendStatus + ", msgType="
				+ msgType + ", showType=" + showType + ", aduioUrl=" + aduioUrl
				+ ", imgUrl=" + imgUrl + ", sendImgUrl=" + sendImgUrl
				+ ", locImgUrl=" + locImgUrl + ", from=" + from + ", to=" + to
				+ ", lon=" + lon + ", lat=" + lat + ", body=" + body
				+ ", loginUser=" + loginUser + ", otherUser=" + otherUser
				+ ", postion=" + postion + "]";
	}

	@Override
	public boolean equals(Object o) {
		 
		if (!super.equals(o)) {
			return false;
		}else {
			ChatMessage message = (ChatMessage) o;
			
			return message.id == id;
		}
	}
}
