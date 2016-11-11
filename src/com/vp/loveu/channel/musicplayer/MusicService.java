package com.vp.loveu.channel.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;

public class MusicService extends Service implements OnBufferingUpdateListener {
	private MediaPlayer mPlayer;
	private static final String TAG = "MusicService";
	private MusicCallBack mCallBack;
	private String currentPlayPath;
	private String currentRadioName;
	private String currentTutor;
	private String currentRadioCover;
	private int mCurrentRadioId=-1;
	private VpHttpClient mClient;
	
	private class MusicBinder extends Binder implements IMusicService{

		@Override
		public void play(String path,int currentPosition,String raidoName,String tutor,String conver) {
			
			if(mPlayer==null || (!path.equals(currentPlayPath))){
				playMusic(path,currentPosition,raidoName,tutor,conver);
				VpApplication.getInstance().setmRadioId(mCurrentRadioId);
			}else{
				startOrPause();
			}
			
		}

		@Override
		public void stop() {
			stopMusic();
			
		}

		@Override
		public void startOrpause() {
			startOrPause();
			
		}


		@Override
		public void setDatas(int radioId,MusicCallBack callBack,VpHttpClient client) {
			mClient=client;
			mCallBack=callBack;
			//正在播放当前电台
			if(mCurrentRadioId==radioId && mPlayer!=null && mCallBack!=null && mPlayer.isPlaying()){
				//继续播放
				mCallBack.onInitProgress(mPlayer.getCurrentPosition(), mPlayer.getDuration());
			}else{
				//停止播放
				stopMusic();
				mCallBack.onInitProgress(0,0);
				VpApplication.getInstance().setmRadioId(-1);
			}
			mCurrentRadioId=radioId;
			
		}

		@Override
		public void seekToPosition(int position) {
			seekToPositionMusic(position);
			
		}

		@Override
		public boolean isPlaying() {
			return getPlayStatus();
		}

		@Override
		public int getCurrentPosition() {
			return getCurrentMusicPosition();
		}

		@Override
		public int getTotalPosition() {
			return  getTotalMusicPosition();
		}

		@Override
		public boolean mpIsNull() {
			return mPlayer==null;
		}

		@Override
		public String getCurrentRadioName() {
			return currentRadioName;
		}

		@Override
		public String getCurrentTutor() {
			return currentTutor;
		}

		@Override
		public int getCurrentRadioId() {
			return mCurrentRadioId;
		}

		@Override
		public String getCurrentCover() {
			return currentRadioCover;
		}


		
	}

	@Override
	public IBinder onBind(Intent intent) {
		
		return new MusicBinder();
	}
	
	public int getCurrentMusicPosition() {
		if(mPlayer!=null){
			return mPlayer.getCurrentPosition();
		}
		return 0;
	}
	public int getTotalMusicPosition() {
		if(mPlayer!=null){
			return mPlayer.getDuration();
		}
		return 0;
	}

	public boolean getPlayStatus() {
		if(mPlayer!=null && mPlayer.isPlaying()){
			return true;
		}
		return false;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
		
	};
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	

    @Override
    public boolean onUnbind(Intent intent) {
    	Log.i(TAG, "onUnbind");
    	return super.onUnbind(intent);
    }
    
    //���²���
    private void playMusic(final String path,final int currentPosition,String raidoName,String tutor,String conver){
    	this.currentPlayPath=path;
    	this.currentRadioName=raidoName;
    	this.currentTutor=tutor;
    	this.currentRadioCover=conver;
    	stopMusic();
    	if(mPlayer==null)
    		mPlayer=new MediaPlayer();
    	mPlayer.reset();
    	if(mClient!=null)
    		mClient.startProgressDialog();
    	try {
    		mPlayer.setOnBufferingUpdateListener(this);
			mPlayer.setDataSource(path);
			mPlayer.prepareAsync();
			mPlayer.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					seekToPositionMusic(currentPosition);
					mPlayer.start();
			    	if(mClient!=null)
			    		mClient.stopProgressDialog();
				}
			});
			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					if(mCallBack!=null){
						mCallBack.onNext();
//						isFirstPlay=true;
						VpApplication.getInstance().setmRadioId(-1);
					}
					
					//发送广播
					Intent intent = new Intent();
					intent.setAction(VpActivity.ACTION_MUSIC);
					sendBroadcast(intent);
					
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    //��ͣor����
    private void startOrPause(){
    	if(mPlayer.isPlaying()){
    		mPlayer.pause();
    		VpApplication.getInstance().setmRadioId(-1);
    	}else{
    		mPlayer.start();
    		VpApplication.getInstance().setmRadioId(mCurrentRadioId);
    	}
    }
    
    //ֹͣ����
    private void stopMusic(){
    	if(mPlayer!=null){
    		mPlayer.stop();
    		mPlayer.release();
    		mPlayer=null;
//    		isFirstPlay=true;
    		VpApplication.getInstance().setmRadioId(-1);
    	}
    }
    

    
    private void seekToPositionMusic(int position){
    	if(mPlayer!=null){
    		mPlayer.seekTo(position);
    	}
    }
    
    @Override
    public void onDestroy() {
    	System.out.println("ondestroy");
    }

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		if(mp!=null && mCallBack!=null && mp.isPlaying()){
			mCallBack.onBufferProgress(mp.getCurrentPosition(), mp.getDuration(),percent);
		}
		
	}

}
