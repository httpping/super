package com.vp.loveu.pay.bean;

import java.io.Serializable;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;


/**
 * bind view bean
 * 去实现它 
 * @author tanping
 * 2015-11-23
 */
public abstract class PayBindViewBean  implements Serializable{

	public String payTitle ;
	public double payMoney;
	public PayType payType;
	public boolean isRequestPhone  ;//是否需要手机号码
	
	public boolean isWxPay = false ;//微信支付
	public boolean payResult = false ; //支付结果 true成功
	
	
	//other 
	public String orderId ;
	public int pay_platfrom ; // 1，2，3 支付宝 微信
	/**
	 * pay 方式
	 * activity 活动 ,  flee_help()情感求助  classroom_pay课程购买
	 * @author tanping  
	 * 2015-11-23
	 */
	public enum PayType {
		activity(1),flee_help(2),classroom_pay(3);
		
		PayType(int value){
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
