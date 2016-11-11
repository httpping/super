package com.vp.loveu.login.bean;

import java.util.ArrayList;

/**
 * @author：pzj
 * @date: 2015-11-15 下午3:30:56
 * @Description:用户完善资料界面随机 昵称和头像集合
 */
public class UserOtherInfo {
	private ArrayList<String> nicknames;
	private ArrayList<String> portraits;
	public ArrayList<String> getNicknames() {
		return nicknames;
	}
	public void setNicknames(ArrayList<String> nicknames) {
		this.nicknames = nicknames;
	}
	public ArrayList<String> getPortraits() {
		return portraits;
	}
	public void setPortraits(ArrayList<String> portraits) {
		this.portraits = portraits;
	}
	
	
}
