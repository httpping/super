package com.vp.loveu.message.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smackx.chatstates.ChatState;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vp.loveu.message.utils.XmppUtils;

/**
 * 聊天用户信息
 * @author tanping
 * 2015-11-2
 */
public class UserInfo implements Serializable {

	public String userId="0";
	public String userName;
	public String headImage;
	public String xmppUser; //不包含 @service 的名称 
	
	
	public int onLineRemind =-1; //在线提醒
	public int black =-1; // 加入黑名单 0.没有处理，不是黑 ， -1， >0 黑，  其他值都不黑
	
	
	public ChatState mState;//聊天状态
	
	public static final String TABLE_NAME = "user_info";
	public static final String USER_ID = "uid";
	public static final String USER_NAME = "name";
	public static final String HEAD_IMAGE = "portrait";
	public static final String XMPP_USER = "xmpp_user";
	public static final String BLACK = "user_black";
	public static final String ONLINE_REMIND = "online_remind";


	public static UserInfo parseJson(String json) {
		UserInfo objectBean = new UserInfo();
		try {
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject.has(USER_ID)) {
				objectBean.userId = jsonObject.getString(USER_ID);
			}
			if (jsonObject.has(USER_NAME)) {
				objectBean.userName = jsonObject.getString(USER_NAME);
			}
			if (jsonObject.has(HEAD_IMAGE)) {
				objectBean.headImage = jsonObject.getString(HEAD_IMAGE);
			}
			
			objectBean.xmppUser = XmppUtils.getJidToUsername(jsonObject.optString(XMPP_USER)).toLowerCase();
		
		} catch (Exception e) {
			return null;
		}
		return objectBean;
	}

	public static List<UserInfo> createFromJsonArray(String json) {
		List<UserInfo> modeBens = new ArrayList<UserInfo>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				UserInfo bean = UserInfo.parseJson(jsonArray.getString(i));
				if (bean != null) {
					modeBens.add(bean);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return modeBens;
	}
	
	public JSONObject toJsonObject(){
		JSONObject data = new JSONObject();
		
		try {
			data.put(USER_ID, userId);
			data.put(USER_NAME, userName);
			data.put(HEAD_IMAGE, headImage);
			data.put(XMPP_USER, xmppUser);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		
		return data;
	}

	@Override
	public String toString() {
		return "UserInfo [userId=" + userId + ", userName=" + userName
				+ ", headImage=" + headImage + ", xmppUser=" + xmppUser
				+ ", onLineRemind=" + onLineRemind + ", black=" + black
				+ ", mState=" + mState + "]";
	}
	

 
	
 

}
