package com.vp.loveu.channel.musicplayer;

public interface MusicCallBack {
	public void onProgress(int currentPosition,int totalPosition);
	public void onBufferProgress(int currentPosition,int totalPosition,int percent);
	public void onInitProgress(int currentPosition,int totalPosition);
	public void onNext();
	public void onPre();

}
