package com.vp.loveu.channel.musicplayer;

import com.loopj.android.http.VpHttpClient;

public interface IMusicService {
	public void play(String path,int currentPosition,String raidoName,String tutor,String conver);
	public void stop();
	public void startOrpause();
	public void setDatas(int mRadioId,MusicCallBack callBack,VpHttpClient client);
	public void seekToPosition(int position);
	public boolean isPlaying();
	public int getCurrentPosition();
	public int getTotalPosition();
	public boolean mpIsNull();
	public String getCurrentRadioName();
	public String getCurrentTutor();
	public int getCurrentRadioId();
	public String getCurrentCover();
	

}
