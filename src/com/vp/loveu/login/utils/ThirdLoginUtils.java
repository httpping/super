package com.vp.loveu.login.utils;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.vp.loveu.login.bean.ThirdAppUserBean;

/**
 * @author：pzj
 * @date: 2015年12月15日 下午2:17:26
 * @Description: 第三方登录
 */
public class ThirdLoginUtils implements PlatformActionListener {
	private Handler mHandler;
	private Context mContext;
	public static final int AUTHORIZE_CANCEL=100;//取消授权
	public static final int AUTHORIZE_COMPLETE=101;//授权完成
	public static final int AUTHORIZE_ERROR=102;//授权失败
	
	public static final String WECHAT_APP_ID="wx3fe5995cacb9b63e";
	public static final String WECHAT_APP_SECRET="d4624c36b6795d1d99dcf0547af5443d";
	private IWXAPI mApi;
	
	public ThirdLoginUtils(Handler handle,Context context) {
		this.mHandler=handle;
		this.mContext=context;
		this.mApi=WXAPIFactory.createWXAPI(context, WECHAT_APP_ID, true);
		this.mApi.registerApp(WECHAT_APP_ID);
	}
	
	/**
	 * 授权
	 */
	public void authorize(Platform platForm){
		//ShareSDK.initSDK(context);//已在vpApplication初始化
		if (platForm == null) {
			return;
		}
		
		//1.微信授权
//		if(platForm.getName()==Wechat.NAME){
//			SendAuth.Req req = new SendAuth.Req();
//		    req.scope = "snsapi_userinfo";
////		    req.state = "wechat_sdk_demo_test";
//		    this.mApi.sendReq(req);
//			
//			return;
//		}
		
		platForm.setPlatformActionListener(this);
		// true表示不使用SSO方式授权
//		platForm.SSOSetting(true);
//		platForm.showUser(null);
		
		platForm.SSOSetting(false);
		platForm.authorize();
		//移除授权
		//platForm.removeAccount(true);
	}

	/**
	 * 授权取消
	 * @param arg0
	 * @param arg1
	 */
	@Override
	public void onCancel(Platform arg0, int action) {
		if(action==Platform.ACTION_USER_INFOR){
			this.mHandler.sendEmptyMessage(AUTHORIZE_CANCEL);
		}
		
		
	}

	/**
	 *  platform - 回调的平台
	 *	action - 操作的类型
	 *	res - 请求的数据通过res返回
	 */
	@Override
	public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
//		if(action==Platform.ACTION_USER_INFOR){//获取用户资料事件
//			Message msg=Message.obtain();
//			msg.what=AUTHORIZE_COMPLETE;
//			msg.obj=new Object[]{platform.getName(),res};//key:平台名称:vlaue:数据
//			this.mHandler.sendMessage(msg);
//		}
		
		PlatformDb platDB = platform.getDb();//获取数平台数据DB
		if(platform.getName()==SinaWeibo.NAME || platform.getName()==QQ.NAME){
			//新浪微博
			ThirdAppUserBean bean=new ThirdAppUserBean();
			bean.setOpenid(platDB.getUserId());
			bean.setNickname(platDB.getUserName());
			bean.setHeadimgurl(platDB.getUserIcon());
			Message msg=Message.obtain();
			msg.what=AUTHORIZE_COMPLETE;
			msg.obj=new Object[]{platform.getName(),bean};//key:平台名称:vlaue:数据
			this.mHandler.sendMessage(msg);
		}


		
	}

	@Override
	public void onError(Platform arg0, int action, Throwable arg2) {
		if(action==Platform.ACTION_USER_INFOR){
			this.mHandler.sendEmptyMessage(AUTHORIZE_ERROR);
		}
		
	}


}
