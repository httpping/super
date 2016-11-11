/**   
* @Title: LoginStatue.java 
* @Package com.vp.loveu.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zeus   
* @date 2015-10-21 下午3:49:26 
* @version V1.0   
*/
package com.vp.loveu.util;

import com.vp.loveu.channel.utils.MusicUtils;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;

/**

 *
 * @ClassName:
 * @Description: 登录用户状态的信息获取工具类
 * @author ping 
 * @date 
 */
public class LoginStatus {

	
	/**
	 * 获取用户的ID
	 * @return
	 */
	public String getAPIUserID(){
		if (VpConstants.PORT !=80) {
			return "test";
		}
		return "client_android";
	}
	
	/**
	 * 获取用户的ID
	 * @return
	 */
	public String getAPIPassword(){
		if (VpConstants.PORT !=80) {
			return "123456";
		}
		return "lvu@160115&!*^#";
	}
	
	
	
	/**
	 * 是否登录
	 * @return
	 */
	public static boolean isLogin() {
		return new LoginUserInfoBean(UIUtils.getContext()).isLogin();
	}
	
	/**
	 * 获取登录用户信息
	 * @return
	 */
	public static LoginUserInfoBean getLoginInfo(){
		
		return new LoginUserInfoBean(UIUtils.getContext()).getLoginInfo();
	}
	
	/**
	 * 登出清除登录用户信息
	 */
	
	public static void loginOut(){
		//停止电台播放服务
		new MusicUtils(UIUtils.getContext()).stopMusicServices(true);
		//清除登录用户信息
		new LoginUserInfoBean(UIUtils.getContext()).cleanAllUserInfo();
	}
	
	/**
	 * 保存用户头像到本地
	 */
	public static void saveUserPortrait(String portraitUrl,String localFilePath){
		new LoginUserInfoBean(UIUtils.getContext()).saveUserPortrait(portraitUrl,localFilePath);
	}
	/**
	 * 保存用户昵称到本地
	 */
	public static void saveUserNickName(String nickName){
		new LoginUserInfoBean(UIUtils.getContext()).saveUserNickName(nickName);
	}
	
//	/**
//	 * 保存用户信息
//	 * @param user
//	 */
//	public static void saveLoginInfo(LoginUserInfoBean user,AsyncHttpResponseHandler responseHandler,VpHttpClient client){
//		// save
//		LoginUserInfoBean.saveLoginInfo(user,responseHandler,client);
//	}
}
