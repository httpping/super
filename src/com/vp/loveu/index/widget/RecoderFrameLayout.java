package com.vp.loveu.index.widget;

import com.vp.loveu.R;
import com.vp.loveu.index.bean.FellHelpBean.FellHelpBeanAudiosBean;
import com.vp.loveu.index.bean.MySeekHelpBean;
import com.vp.loveu.index.bean.MySeekHelpBean.MySeekAudioBean;
import com.vp.loveu.index.myutils.MediaPlayerRecordUtils;
import com.vp.loveu.index.myutils.MediaPlayerRecordUtils.OnCompletionListenerCallBack;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月11日上午9:58:29
 * @功能 播放的item
 * @作者 mi
 */

public class RecoderFrameLayout extends RelativeLayout implements OnClickListener, OnCompletionListenerCallBack {

	private String tag = "RecoderFrameLayout";
	private ImageView mIvBackGroun;
	private TextView mTvTime;
	private RelativeLayout mBackGroun;
	public MediaPlayerRecordUtils mediaPlayer;
	private ImageView mIvPlayering;
	private boolean isPlayer = true;// 是否可以播放 默认为true
	private ScaleAnimation scaleAnimation;
	private MySeekAudioBean mData;
	public static RecoderFrameLayout playerRecoderFrameLayout;// 默认为空

	public RecoderFrameLayout(Context context) {
		this(context, null);
	}

	public RecoderFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.recoder_item, this);
		mIvBackGroun = (ImageView) findViewById(R.id.free_help_bottom_item_iv_recoder);
		mTvTime = (TextView) findViewById(R.id.free_help_bottom_item_tv_recoder_time);
		mBackGroun = (RelativeLayout) findViewById(R.id.free_help_bottom_recoder_container);
		mIvPlayering = (ImageView) findViewById(R.id.free_help_bottom_item_iv_playering);
		mediaPlayer = new MediaPlayerRecordUtils();
		mediaPlayer.setmCallback(this);
		
	}

	public void setData(MySeekAudioBean bean) {
		this.mData = bean;
		mTvTime.setText(bean.title + "''");
		setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (!isPlayer) {
			return;
		}
		if (mediaPlayer.isPlaying() || mIvPlayering.getVisibility() == View.VISIBLE) {
			stopAnimation();
			mediaPlayer.stopPlayer();
		} else {
			stopExtraPlay();// 停止其他正在播放
			startAnimation();
			mediaPlayer.startPlay(mData.url);
		}
	}
	
	public void startAnimation() {
		scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(600);
		scaleAnimation.setRepeatCount(Animation.INFINITE);
		mIvPlayering.setVisibility(View.VISIBLE);
		mIvPlayering.setAnimation(scaleAnimation);
		scaleAnimation.start();
	}

	public void stopAnimation() {
		mIvPlayering.clearAnimation();
		scaleAnimation = null;
		mIvPlayering.setVisibility(View.GONE);
	}

	protected void stopExtraPlay() {
		// 之前有播放的
		if (playerRecoderFrameLayout != null) {
			try {
				playerRecoderFrameLayout.stopAnimation();
				playerRecoderFrameLayout.mediaPlayer.stopPlayer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setIsplayer(boolean isplayer) {
		this.isPlayer = isplayer;
	}

	public void setData(FellHelpBeanAudiosBean bean) {
		MySeekAudioBean mySeekAudioBean = new MySeekHelpBean().new MySeekAudioBean();
		mySeekAudioBean.title = bean.title;
		mySeekAudioBean.url = bean.url;
		setData(mySeekAudioBean);
	}

	@Override
	public void onCompletionListenerCallBack() {
		stopAnimation();
	}

	@Override
	public void onError() {
		Toast.makeText(getContext(), "播放出错,请重试", Toast.LENGTH_SHORT).show();
		playerRecoderFrameLayout = null;
		stopAnimation();
	}

	@Override
	public void onPrepared() {
		playerRecoderFrameLayout = this;
	}
}
