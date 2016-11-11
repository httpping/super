/**   
* @Title: VpActivity.java 
* @Package com.vp.loveu.base 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zeus   
* @date 2015-10-20 下午4:26:30 
* @version V1.0   
*/
package com.vp.loveu.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.loopj.android.http.VpHttpClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.vp.loveu.R;
import com.vp.loveu.bean.InwardAction;
import com.vp.loveu.channel.musicplayer.IMusicService;
import com.vp.loveu.channel.musicplayer.MusicService;
import com.vp.loveu.channel.ui.ChannelDetailActivity;
import com.vp.loveu.comm.InWardActionActivity;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ServiceStateUtils;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.widget.PublicTitleView;


/**

 * 基础activity
 * @ClassName: 
 * @Description:
 * @author ping
 * @date 
 */
public abstract class  VpActivity extends FragmentActivity {
	protected String tag;	
	public VpHttpClient mClient;//http请求
	
	public PublicTitleView mPubTitleView;
	
	private Intent musicServiceIntent;
	private MusicConnection mMusicConn;
	private IMusicService iMusicService;
	
	private boolean isShowRadioIcon=true;
	public static final String ACTION_MUSIC="com.vp.loveu.music";
	
	private BroadcastReceiver musicReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			//监听电台停止广播
			if(intent.getAction().equals(ACTION_MUSIC)){
				if(mPubTitleView!=null){
					mPubTitleView.mTvTitle.setVisibility(View.VISIBLE);
					mPubTitleView.mRadioContainer.setVisibility(View.GONE);
					mPubTitleView.mRadioIv.clearAnimation();
				}
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tag = "aiu"+ this.getComponentName().getShortClassName();	
		
		VPLog.d(tag, "oncreate");
		
		IntentFilter musicFilter=new IntentFilter();
		musicFilter.addAction(ACTION_MUSIC);
		registerReceiver(musicReceiver, musicFilter);
	}

	
	public void initPublicTitle(){
		try {
			mPubTitleView= (PublicTitleView)findViewById(R.id.public_title_view);
			
		} catch (Exception e) {
		}
	}
	
	@Override
	public void finish() {
		super.finish();
		if (mClient!=null) {
			mClient.cancelRequests(this, true);
		}
	}
	
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		String expression = "^[1](([3-9])[0-9])[0-9]{8}$";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		VPLog.d(tag, "onResume");
		if(mPubTitleView!=null && isShowRadioIcon){
			if(ServiceStateUtils.isRunging(this, MusicService.class)){
			    //绑定服务
				musicServiceIntent = new Intent(this,MusicService.class);
				startService(musicServiceIntent);
				mMusicConn=new MusicConnection();
				bindService(musicServiceIntent, mMusicConn, BIND_AUTO_CREATE);
			    
			}else{
				mPubTitleView.mTvTitle.setVisibility(View.VISIBLE);
				mPubTitleView.mRadioContainer.setVisibility(View.GONE);
				mPubTitleView.mRadioIv.clearAnimation();
			}
		}
		
		MobclickAgent.onPageStart(tag); //统计页面
		MobclickAgent.onResume(this);

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(mMusicConn!=null)
			unbindService(mMusicConn);//解绑服务
		
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(tag); 
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		/*musicServiceIntent = new Intent(this,MusicService.class);
		stopService(musicServiceIntent);*/
//		musicServiceIntent = new Intent(this,MusicService.class);
//		stopService(musicServiceIntent);
		VPLog.d(tag, "onDestroy");
		if(musicReceiver!=null){
			unregisterReceiver(musicReceiver);
		}
	}
	
	private class MusicConnection implements ServiceConnection{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iMusicService=(IMusicService) service;
			if(iMusicService.isPlaying()){
				mPubTitleView.mTvTitle.setVisibility(View.GONE);
				mPubTitleView.mRadioContainer.setVisibility(View.VISIBLE);
				DisplayImageOptions options = new DisplayImageOptions.Builder()
				         .showImageOnLoading(R.drawable.default_portrait) // resource or
				        .showImageForEmptyUri(R.drawable.default_portrait) // resource or
				        .showImageOnFail(R.drawable.default_portrait) // resource or
				        .resetViewBeforeLoading(false) // default
				        .cacheInMemory(true) // default
				        .cacheOnDisk(true) // default
				        .bitmapConfig(Bitmap.Config.RGB_565)  
				        .considerExifParams(false) // default
				        .displayer(new SimpleBitmapDisplayer())
				        .build();
				ImageLoader.getInstance().displayImage(iMusicService.getCurrentCover(), mPubTitleView.mRadioIv,options);
			    Animation operatingAnim = AnimationUtils.loadAnimation(VpActivity.this, R.anim.channel_radio_image_rotate);  
			    LinearInterpolator lin = new LinearInterpolator();  
			    operatingAnim.setInterpolator(lin);  
			    mPubTitleView.mRadioIv.startAnimation(operatingAnim);
			    
			    mPubTitleView.mRadioIvPlay.setBackgroundResource(R.drawable.channel_playing_animation);
				((AnimationDrawable) mPubTitleView.mRadioIvPlay.getBackground()).start();
			    
			    mPubTitleView.mRadioContainer.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent=new Intent(VpActivity.this,ChannelDetailActivity.class);
						intent.putExtra(ChannelDetailActivity.RADIOID, iMusicService.getCurrentRadioId());
						intent.putExtra(ChannelDetailActivity.CHANNEL_NAME,iMusicService.getCurrentRadioName());
						intent.putExtra(ChannelDetailActivity.TUTOR_NAME, iMusicService.getCurrentTutor());
						startActivity(intent);
						
					}
				});
			}else{
				mPubTitleView.mTvTitle.setVisibility(View.VISIBLE);
				mPubTitleView.mRadioContainer.setVisibility(View.GONE);
				mPubTitleView.mRadioIv.clearAnimation();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
	}

	public boolean isShowRadioIcon() {
		return isShowRadioIcon;
	}


	public void setShowRadioIcon(boolean isShowRadioIcon) {
		this.isShowRadioIcon = isShowRadioIcon;
	}
	
	
	/**
	 * 他的主页
	 * @param id
	 */
	public void goOtherUserInfo(int id){
		LoginUserInfoBean mine = LoginStatus.getLoginInfo();
		if (mine == null) {
			return ;
		}
		String spec = "loveu://person_index";
		if (id == 0 || id == mine.getUid()) {
			
		}else {
			spec +="?id="+id;//他的主页
		}
		InwardAction.parseAction(spec).toStartActivity(this);
	}
	/**
	 * 他的主页
	 * @param id
	 */
	public void goOtherUserInfo(String id){
		int uid = 0;
		try {
			uid = Integer.parseInt(id);
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
				
		LoginUserInfoBean mine = LoginStatus.getLoginInfo();
		if (mine == null) {
			return ;
		}
		String spec = "loveu://person_index";
		if (uid == 0 || uid == mine.getUid()) {
			
		}else {
			spec +="?id="+id;//他的主页
		}
		InwardAction.parseAction(spec).toStartActivity(this);
	}
	
	
	/**
	 * 保证头像修改后及时刷新界面
	 * @param id
	 */
	public String getUserInfoHead(int id){
				
		LoginUserInfoBean mine = LoginStatus.getLoginInfo();
		if (mine == null) {
			return  null;
		}
		if (id == mine.getUid()) {
			return mine.getPortrait();
		} 
		return null ;
	}
	
}
