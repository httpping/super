package com.vp.loveu.message.bean;



/**
 * 消息会话
 * @author tanping
 *
 */
public class ChatSessionBean  extends ChatMessage {
	
	public int count ;//计数器
	public int is_stick = -1;//置顶 >0 即置顶
	
	public static final String TABLE_NAME ="chat_msg_session";
	public static final String  MESSAGE_COUNT="msg_count";//
	public static final String  IS_STICK="msg_is_stick";//

	
	
	public static ChatSessionBean chatToSession(ChatMessage message){
		
		ChatSessionBean bean = new ChatSessionBean();
		
		bean.from = message.from;
		bean.to = message.to;
		bean.timestamp = message.timestamp;
		bean.id  =message.id;
		
		//存储的 相关人
		bean.loginUser = message.loginUser;
		bean.otherUser = message.otherUser;
		//z状态
		bean.sendStatus = message.sendStatus;
		bean.readStatus = message.readStatus;
		bean.showType = message.showType;
		
		//内容 json
		bean.body = message.body;
		
		return bean;
		
	}


	@Override
	public boolean equals(Object o) {
		if (o== null) {
			return false ;
		}
		
		if (o instanceof ChatSessionBean) {
			ChatSessionBean bean = (ChatSessionBean) o;
			if ( bean.loginUser == loginUser && bean.otherUser == otherUser) {
				return true;
			}
		}
		
		
		return false;
	}


	
	
}
