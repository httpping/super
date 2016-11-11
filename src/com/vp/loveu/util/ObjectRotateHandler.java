package com.vp.loveu.util;

import android.os.Handler;
import android.util.Log;
import android.view.View;
/**
 * 播放帧，让view 旋转
 * @author tanping
 * 2015-12-14
 */
public class ObjectRotateHandler  {

	public View mView;
	public long duration=3000;//默认一圈
	public int degree;
	public boolean isRun ;
	private boolean mPaused;
	public int speed =100;//帧

    public void setDuration(long duration) {
    	this.duration = duration;
    }
    
    public ObjectRotateHandler(View view){
    	this.mView = view;
    }
    
    
    public static final int TIME_RUN_View = 178178;
	Handler timeHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TIME_RUN_View:
				if (duration == 0) {
					duration = 2000;
				}
				degree+= 360*1000/speed/duration;
				degree%=360; //小宇360度
				updateView();
				if (isRun) {
					timeHandler.sendEmptyMessageDelayed(TIME_RUN_View, duration/speed );//30针
				}
				break;

			default:
				break;
			}
		};
	};
	
	private void updateView(){
		Log.d("d", "d:"+degree);
		if (mView!=null) {
			mView.setRotation(degree);
		}
	}
    
	 public boolean isPaused() {
	        return mPaused;
	 }
    
    public void start() {
    	isRun = true;
    	mPaused = false;
    	timeHandler.sendEmptyMessage(TIME_RUN_View);
    }
    public void pause(){
    	isRun = false;
    	mPaused = true;
    	timeHandler.removeMessages(TIME_RUN_View);
    }
    public void resume() {
    	if(!isRun){  		
    		isRun = true;
    		mPaused = false;
    		timeHandler.sendEmptyMessage(TIME_RUN_View);
    	}
    }

    public void stop(){
    	mPaused = true;
    	degree = 0;
    	isRun = false;
    	mPaused = true;
    	timeHandler.removeMessages(TIME_RUN_View);
    }
		
}
