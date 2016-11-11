package com.vp.loveu.pay.bean;

import java.io.Serializable;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;


/**
 * 打赏
 * @author tanping
 * 2016-3-4
 */
public abstract class EnjoyPayBean implements Serializable{
	 
	
	public double payMoney;
	public EnjoyPayType payType;
	public boolean isRequestPhone  ;//是否需要手机号码
	
	//other 
	public String orderId ;
	public int pay_platfrom ; // 1，2，3 支付宝 微信
	public boolean isWxPay = false ;//微信支付
	public boolean payResult = false ; //支付结果 true成功
	
	/**
	 *  pay 方式
	 * 打赏类型
	 * 
		1=新手视频
		2=恋爱电台
	 * @author tanping  
	 * 2015-11-23
	 */
	public enum EnjoyPayType {
		new_vedio(1),love_radio(2);
		
		EnjoyPayType(int value){
			this.value = value;
		}
		
		private final int value;
		public int getValue(){
			return value;
		}
		 
	};
	
	
	/**
	 * 创建用来展示的  view 
	 * @param context
	 * @return
	 */
	public abstract View createShowView(Context context);
	/**
	 * 获取请求参数 ，json  key 必须和 接口的key 一致
	 * @return
	 */
	
	public abstract JSONObject getParams();
	
	
	
}

