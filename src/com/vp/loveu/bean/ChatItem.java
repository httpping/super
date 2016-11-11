package com.vp.loveu.bean;

/**
 * 会话对象
 * 
 * @author dell
 * 
 */
public class ChatItem {
	private long Id;
	private String msgBody;
	private String msgFrom;
	private String isRead;
	private String msgTime;
	private String msgTo;
	private String Avatar;
	private String NickName;
	private String MsgCount;

	public String getMsgCount() {
		return MsgCount;
	}

	public void setMsgCount(String msgCount) {
		MsgCount = msgCount;
	}

	public String getAvatar() {
		return Avatar;
	}

	public void setAvatar(String avatar) {
		Avatar = avatar;
	}

	public String getNickName() {
		return NickName;
	}

	public void setNickName(String nickName) {
		NickName = nickName;
	}

	public String getMsgTo() {
		return msgTo;
	}

	public void setMsgTo(String msgTo) {
		this.msgTo = msgTo;
	}

	public String getMsgTime() {
		return msgTime;
	}

	public void setMsgTime(String msgTime) {
		this.msgTime = msgTime;
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public String getIsRead() {
		return isRead;
	}

	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

	public String getMsgBody() {
		return msgBody;
	}

	public void setMsgBody(String msgBody) {
		this.msgBody = msgBody;
	}

	public String getMsgFrom() {
		return msgFrom;
	}

	public void setMsgFrom(String msgFrom) {
		this.msgFrom = msgFrom;
	}

}
