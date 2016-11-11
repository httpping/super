package com.vp.loveu.channel.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.vp.loveu.channel.musicplayer.IMusicService;
import com.vp.loveu.channel.musicplayer.MusicService;
import com.vp.loveu.util.ServiceStateUtils;

/**
 * @author：pzj
 * @date: 2016年1月4日 下午2:59:31
 * @Description:
 */
public class MusicUtils {
	private Context mContext;
	private boolean mIsBind;
	private Intent musicServiceIntent;
	private MusicConnection mMusicConn;
	private IMusicService iMusicService;
	
	public MusicUtils(Context context) {
		this.mContext=context;
	}
	
	/**
	 * 停止电台播放服务
	 * @param isBind
	 */
	public void stopMusicServices(boolean isBind){
		this.mIsBind=isBind;
		if(ServiceStateUtils.isRunging(mContext, MusicService.class)){
		    //绑定服务
			musicServiceIntent = new Intent(mContext,MusicService.class);
			mContext.startService(musicServiceIntent);
			mMusicConn=new MusicConnection();
			mContext.bindService(musicServiceIntent, mMusicConn, mContext.BIND_AUTO_CREATE);
		    
		}
	}
	
	private class MusicConnection implements ServiceConnection{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iMusicService=(IMusicService) service;
			stopMusicServices();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
	}
	
	private void stopMusicServices(){
		if(iMusicService!=null){
			//停止电台播放
			iMusicService.stop();
			
			//解绑服务
			if(mIsBind)
				mContext.unbindService(mMusicConn);
			
			//停止服务
			mContext.stopService(musicServiceIntent);
			
		}
	}
	
	

}
