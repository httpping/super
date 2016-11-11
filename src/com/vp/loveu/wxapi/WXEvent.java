package com.vp.loveu.wxapi;

public class WXEvent {
	private String mMsg;
	public WXEvent(String msg) {
		mMsg = msg;
	}
	public String getMsg(){
		return mMsg;
	}
}
