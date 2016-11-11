package com.vp.loveu.bean;

import org.json.JSONObject;

import com.loopj.android.http.ResultParseUtil;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.util.ToastUtil;

/**
 * 接口返回 基础bean
 * @author tanping 2015-12-11
 */
public class NetBaseBean {

	public String data;
	public int code = -1;
	public String msg ="请求失败，请检查网络连接是否正常";
	public int isEncrypt;
	public static final String DATA = "data";
	public static final String CODE = "code";
	public static final String MSG = "msg";
	public static final String IS_ENCRYPT = "is_encrypt";

	public static NetBaseBean parseJson(String json) {
		NetBaseBean objectBean = new NetBaseBean();
		try {
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject.has(DATA)) {
				objectBean.data = jsonObject.getString(DATA);
			}
			if (jsonObject.has(CODE)) {
				objectBean.code = jsonObject.getInt(CODE);
			}
			
			if (jsonObject.has(IS_ENCRYPT)) {
				objectBean.isEncrypt = jsonObject.getInt(IS_ENCRYPT);
			}
			if (jsonObject.has(MSG)) {
				objectBean.msg = jsonObject.getString(MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new NetBaseBean();
		}
		return objectBean;
	}
	
	
	public static NetBaseBean parseJson(byte[] responseBody) {
		NetBaseBean objectBean = new NetBaseBean();
		try {
			JSONObject jsonObject = new JSONObject(ResultParseUtil.deAesResult(responseBody));
			if (jsonObject.has(DATA)) {
				objectBean.data = jsonObject.getString(DATA);
			}
			if (jsonObject.has(CODE)) {
				objectBean.code = jsonObject.getInt(CODE);
			}
			
			if (jsonObject.has(IS_ENCRYPT)) {
				objectBean.isEncrypt = jsonObject.getInt(IS_ENCRYPT);
			}
			if (jsonObject.has(MSG)) {
				objectBean.msg = jsonObject.getString(MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new NetBaseBean();
		}
		return objectBean;
	}
	
	/**
	 * 显示 info msg 
	 * true 已经显示了提醒，false 没有提醒成功
	 */
	public boolean showMsgToastInfo(){
		
		if (VpApplication.getContext()!=null) {
			ToastUtil.showToast(VpApplication.getContext(), ""+msg, 0);
			return true;
		}
		
		return false ;
	}
	
	public boolean isSuccess(){
		return code==0;
	}
	

}
