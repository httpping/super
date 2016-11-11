package com.vp.loveu.my.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月12日下午2:31:47
 * @功能TODO
 * @作者 mi
 */

public class WalletBean extends VPBaseBean {
	public int code;
	public String data;
	public int is_encrypt;
	public String msg;

	public static ArrayList<WalletDataBean> parserJson(String json){
		ArrayList<WalletDataBean> list = new ArrayList<WalletDataBean>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			if (jsonArray == null || jsonArray.length() <= 0) {
				return list;
			}
			for (int i = 0; i < jsonArray.length(); i++) {
				String result = jsonArray.getString(i);
				WalletDataBean item = WalletDataBean.parserJson(result);
				list.add(item);
			}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static class WalletDataBean extends VPBaseBean{
		public double balance;
		public String create_time;
		public double money;
		public String remark;
		public int type;
		
		public static WalletDataBean parserJson(String json){
			WalletDataBean bean = new WalletDataBean();
			try {
				JSONObject obj = new JSONObject(json);
				bean.create_time = obj.getString("create_time");
				bean.balance = obj.getDouble("balance");
				bean.money = obj.getDouble("money");
				bean.remark = obj.getString("remark");
				bean.type = obj.getInt("type");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return bean;
		}
	}
}
