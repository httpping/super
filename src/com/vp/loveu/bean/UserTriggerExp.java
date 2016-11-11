package com.vp.loveu.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.VPLog;

import cz.msebera.android.httpclient.Header;

/**
 * 聊天触发积分
 * @author tanping
 * 2015-12-18
 */
public class UserTriggerExp  implements Serializable{

	
	
	/**
	 * 触发积分
	 * @param type
	 * @param responseHandler
	 */
	public void triggerExp(int type,String fromUid,String toUid,AsyncHttpResponseHandler responseHandler){
		
		String url = VpConstants.USER_CHAT_TRIGGER_EXP;
		LoginUserInfoBean mine = LoginStatus.getLoginInfo();
		if (mine == null) {
			return ;
		}
		JSONObject data = new JSONObject();
		try {
			data.put("uid", mine.getUid());
			data.put("type", type);
			data.put("from_uid", fromUid);
			data.put("to_uid", toUid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		VPLog.d("chat", ""+data);
		//触发积分 handle 可为null
		new VpHttpClient(null).post(url, new RequestParams(), data.toString(), new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ResultParseUtil.deAesResult(responseBody);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				
			}
		});
	}
	
}
