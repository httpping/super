package com.vp.loveu.message.bean;

import java.io.Serializable;

/**
 * @author Administrator
 * 
 */
public class MineInfo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String userId ="123";
    private String userNick ="小王";
    private String userPwd;
    private int userSex;
    private int userAge;
    private String userImage ="http://baidu.com";
    private String mobile;
    private boolean isMobileBind;
    private boolean isStaff;// 区分账号是否为酒水顾问
    private boolean isLightLogin;// 区分普通登录和轻登陆
    private boolean isEgAccount;// 区分一起吧和第三方账号
    private String token;
    private int showNum;// 发布show的数量
    private String ppAvatar;

    public MineInfo() {
	super();
	// TODO Auto-generated constructor stub
    }

     
    public String getUserId() {
	return userId;
    }

   

    public String getUserNick() {
	return userNick;
    }

    public void setUserNick(String userNick) {
	this.userNick = userNick;
    }

    public String getUserPwd() {
	return userPwd;
    }

    public void setUserPwd(String userPwd) {
	this.userPwd = userPwd;
    }

    public int getUserSex() {
	return userSex;
    }

    public void setUserSex(int userSex) {
	this.userSex = userSex;
    }

    public int getUserAge() {
	return userAge;
    }

    public void setUserAge(int userAge) {
	this.userAge = userAge;
    }

    public String getUserImage() {
	return userImage;
    }

    public void setUserImage(String userImage) {
	this.userImage = userImage;
    }

    public String getMobile() {
	return mobile;
    }

    public void setMobile(String mobile) {
	this.mobile = mobile;
    }

    public boolean isMobileBind() {
	return isMobileBind;
    }

    public void setMobileBind(boolean isMobileBind) {
	this.isMobileBind = isMobileBind;
    }

    public boolean isStaff() {
	return isStaff;
    }

    public void setStaff(boolean isStaff) {
	this.isStaff = isStaff;
    }

    public boolean isLightLogin() {
	return isLightLogin;
    }

    public void setLightLogin(boolean isLightLogin) {
	this.isLightLogin = isLightLogin;
    }

    public boolean isEgAccount() {
	return isEgAccount;
    }

    public void setEgAccount(boolean isEgAccount) {
	this.isEgAccount = isEgAccount;
    }

    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    public int getShowNum() {
	return showNum;
    }

    public void setShowNum(int showNum) {
	this.showNum = showNum;
    }

   

    public String getPpAvatar() {
		return ppAvatar;
	}

	public void setPpAvatar(String ppAvatar) {
		this.ppAvatar = ppAvatar;
	}

	@Override
    public String toString() {
	return "MineInfo [userId=" + userId + ", userNick=" + userNick + ", userPwd=" + userPwd + ", userSex="
		+ userSex + ", userAge=" + userAge + ", userImage=" + userImage + ", mobile=" + mobile
		+ ", isMobileBind=" + isMobileBind + ", isStaff=" + isStaff + ", isLightLogin=" + isLightLogin
		+ ", isEgAccount=" + isEgAccount + ", token=" + token + ", showNum=" + showNum +  "]";
    }

}
