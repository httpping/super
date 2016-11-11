package com.vp.loveu.channel.ui;

import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.channel.bean.VideoDetailBean.Video;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.util.AESUtil;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年2月19日下午2:07:30
 * @功能TODO
 * @作者 mi
 */

public class VideoViewActivity extends VpActivity
		implements OnPreparedListener, OnErrorListener, OnSeekBarChangeListener, OnCompletionListener, OnClickListener, OnInfoListener {

	private final String PROCESS_NAME = "com.vp.loveu:vpvideo";
	private VideoView mVideoView;
	private RelativeLayout mProgressBar;
	private GestureDetector mGestureDetector;
	private ImageButton mIvPlayer;
	private SeekBar mSeekBar;
	private LinearLayout mController;
	private TextView mCurrentTimes;
	private TextView mDurationTimes;
	private TextView mTvTitle;
	private int screenWidth;
	private int screenHeight;
	private AudioManager am;
	private int maxVolume;
	private int currentVolume;
	private Video data;
	private int totalDuration;
	private int downFlag = -1; // 1 -- 左边 2 -- 右边 -1 -- 错误
	private float volumeScale;// 音量和屏幕的比例
	private int tempVolume;
	private boolean mControllerIsShow = true;
	private int measuredHeight;
	private int screenBrightness;
	private float screenBrightnessScale;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				updatePlayTimes();
				break;

			case 1:
				updateBufferProgress();
				break;
			case 3:
				if (mControllerIsShow && isPlayering()) {
					hideController();
					mControllerIsShow = false;
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_videoview);
		data = (Video) getIntent().getSerializableExtra(VideoPlayActivity.URL);
		initView();
		initData();
	}

	private void initView() {
		mVideoView = (VideoView) findViewById(R.id.v_videoview);
		mProgressBar = (RelativeLayout) findViewById(R.id.video_progress_container);
		mIvPlayer = (ImageButton) findViewById(R.id.mediacontroller_play_pause);
		mCurrentTimes = (TextView) findViewById(R.id.mediacontroller_time_current);
		mDurationTimes = (TextView) findViewById(R.id.mediacontroller_time_total);
		mSeekBar = (SeekBar) findViewById(R.id.mediacontroller_seekbar);
		mTvTitle = (TextView) findViewById(R.id.mediacontroller_file_name);
		mController = (LinearLayout) findViewById(R.id.video_bottom_container);

		mController.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				measuredHeight = mController.getMeasuredHeight();
				mController.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});
		
		noAllClick();
		//initVideoViewParams();
		String qiniuPrivateUrl = AESUtil.getQiniuPrivateUrl(this, data.getUrl(),data.getTimestamp());
		mTvTitle.setText(data.getName());
		mVideoView.setVideoPath(qiniuPrivateUrl);
		mVideoView.setKeepScreenOn(true);
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnInfoListener(this);
		mSeekBar.setOnSeekBarChangeListener(this);
		mIvPlayer.setOnClickListener(this);
	}

	/**
	 * 禁止视频未初始化完点击 void
	 */
	private void noAllClick() {
		mSeekBar.setEnabled(false);
		mIvPlayer.setEnabled(false);
	}

	/**
	 * 激活点击 void
	 */
	private void activeAllClick() {
		mSeekBar.setEnabled(true);
		mIvPlayer.setEnabled(true);
	}

	private void initData() {
		
		int[] screenWidthAndHeight = MyUtils.getScreenWidthAndHeight(this);
		screenWidth = screenWidthAndHeight[0];
		screenHeight = screenWidthAndHeight[1];
		am = (AudioManager) getSystemService(Activity.AUDIO_SERVICE);
		maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volumeScale = maxVolume * 1.0f / screenHeight;
		currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

		// 关闭屏幕自动亮度功能
		try {
			if (Settings.System.getInt(getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
				// 屏幕自动亮度已打开 帮他关闭掉
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
						Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			}
		} catch (Exception e) {
		}
		// 获得最大屏幕亮度
		screenBrightnessScale = 255 * 1.0f / screenHeight;

		mGestureDetector = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				float y = e1.getY() - e2.getY();
				float xx = e1.getX() - e2.getX();
				if (Math.abs(xx) > 20) { // 判断斜角触摸屏幕
					return true;
				}

				if (downFlag == 1) {
					// 改变音量
					int dy = (int) (y * volumeScale + 0.5f);
					am.setStreamVolume(AudioManager.STREAM_MUSIC, tempVolume + dy, 1);
				} else if (downFlag == 2) {
					// 改变亮度
					float x = y * screenBrightnessScale;
					screenBrightness += x;
					setScreenBritness(screenBrightness);
				}
				return true;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				float x = e.getX();
				if (x <= screenWidth / 2 - screenWidth / 2 / 4) {
					downFlag = 1; // 判断手势在左边滑动还是右边
				} else if (x >= screenWidth / 2 + screenWidth / 2 / 4) {
					downFlag = 2;// 改变屏幕亮度
				}
				tempVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
				screenBrightness = Settings.System.getInt(VideoViewActivity.this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
						0);
				return true;
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				downFlag = -1;
				// 清击弹出控制面板或者隐藏
				if (e.getY() < mController.getTop() - 50 && isPlayering()) {
					handler.removeMessages(3);
					if (mControllerIsShow) {
						hideController();
					} else {
						showController();
						handler.sendEmptyMessageDelayed(3, 5000);
					}
					mControllerIsShow = !mControllerIsShow;
				}
				return true;
			}
		});
	}

	private void setScreenBritness(int brightness) {
		// 不让屏幕全暗
		if (brightness <= 1) {
			brightness = 1;
		}
		if (brightness >= 255) {
			brightness = 255;
		}
		// 设置当前activity的屏幕亮度
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		// 0到1,调整亮度暗到全亮
		lp.screenBrightness = Float.valueOf(brightness * 1.0f / 255f);
		this.getWindow().setAttributes(lp);
	}

	private void initVideoViewParams() { // 解决视频全屏显示
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mVideoView.setLayoutParams(params);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
	}

	/**
	 * 显示控制面板 void
	 */
	private void showController() {
		/*AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator alpha = ObjectAnimator.ofFloat(mController, "alpha", 0.0f, 1.0f).setDuration(500);
		ObjectAnimator translation = ObjectAnimator.ofFloat(mController, "translationY", measuredHeight, 0).setDuration(500);
		animatorSet.setDuration(500).playTogether(alpha, translation);
		animatorSet.start();*/
		
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, measuredHeight,0);
		translateAnimation.setDuration(500);
		translateAnimation.setFillAfter(true);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(500);
		alphaAnimation.setFillAfter(true);
		animationSet.addAnimation(translateAnimation);
		animationSet.addAnimation(alphaAnimation);
		animationSet.setDuration(500);
		animationSet.setFillAfter(true);
		mController.startAnimation(animationSet);
	}

	/**
	 * 隐藏控制面板
	 */
	private void hideController() {
		/*AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator alpha = ObjectAnimator.ofFloat(mController, "alpha", 1.0f, 0.0f).setDuration(500);
		ObjectAnimator translation = ObjectAnimator.ofFloat(mController, "translationY", 0, measuredHeight).setDuration(500);
		animatorSet.setDuration(500).playTogether(alpha, translation);
		animatorSet.start();*/   //属性动画不知道为什么不行  
		
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0,measuredHeight);
		translateAnimation.setDuration(500);
		translateAnimation.setFillAfter(true);
		AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
		alphaAnimation.setDuration(500);
		alphaAnimation.setFillAfter(true);
		animationSet.addAnimation(translateAnimation);
		animationSet.addAnimation(alphaAnimation);
		animationSet.setDuration(500);
		animationSet.setFillAfter(true);
		mController.startAnimation(animationSet);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {

		Toast.makeText(this, "播放出错" + what + "extra" + extra, 0).show();
		return true;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		hideProgressLoadding();
		activeAllClick();
		totalDuration = mVideoView.getDuration();
		mDurationTimes.setText(MyUtils.millisToVideoDuration(totalDuration));
		mSeekBar.setMax(totalDuration / 1000);
		updatePlayTimes();
		updateBufferProgress();

		mp.start();
		handler.sendEmptyMessageDelayed(3, 5000);// 5秒自动以藏控制面板
	}

	private void hideProgressLoadding() {
		mProgressBar.animate().alpha(1.0f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mProgressBar.setAlpha(0.0f);
				mProgressBar.setVisibility(View.GONE);
			}
		}).start();
	}

	/**
	 * 更新二级缓冲 void
	 */
	private void updateBufferProgress() {
		int bufferPercentage = mVideoView.getBufferPercentage();
		bufferPercentage *= totalDuration;
		mSeekBar.setSecondaryProgress(bufferPercentage);
		handler.sendEmptyMessageDelayed(1, 1000);
	}

	/**
	 * 更新当前播放的时间 void
	 */
	private void updatePlayTimes() {
		int currentPosition = mVideoView.getCurrentPosition();
		mCurrentTimes.setText(MyUtils.millisToVideoDuration(currentPosition));
		mSeekBar.setProgress(currentPosition / 1000);
		handler.sendEmptyMessageDelayed(0, 1000);
	}

	private boolean isPlayering() {
		return mVideoView.isPlaying();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mVideoView.stopPlayback(); // 释放播放资源
		handler.removeCallbacksAndMessages(null);// 移除所有消息
		killProcess();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			if (isPlayering()) {
				mVideoView.seekTo(progress * 1000);
			} else {
				mVideoView.start();
				mVideoView.seekTo(progress * 1000);
			}
			mCurrentTimes.setText(MyUtils.millisToVideoDuration(progress * 1000));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		handler.removeMessages(3);
		if (!isPlayering()) {
			handler.sendEmptyMessage(0);
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		handler.sendEmptyMessageDelayed(3, 5000);
		updateImageButtonStatus();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// 播放完成后的回调
		updateImageButtonStatus();
		showController();
		mControllerIsShow = true;
	}

	/**
	 * 更新播放按钮状态 void
	 */
	private void updateImageButtonStatus() {
		if (isPlayering()) {
			mIvPlayer.setImageResource(R.drawable.mediacontroller_pause);
		} else {
			mIvPlayer.setImageResource(R.drawable.mediacontroller_play);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mediacontroller_play_pause:
			handler.removeMessages(3);
			if (isPlayering()) {
				mVideoView.pause();
				handler.removeMessages(0);
				handler.removeMessages(1);
			} else {
				mVideoView.start();
				handler.sendEmptyMessageDelayed(3, 5000);
				handler.sendEmptyMessage(0);
				handler.sendEmptyMessage(1);
			}
			updateImageButtonStatus();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			mProgressBar.setVisibility(View.VISIBLE);
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			hideProgressLoadding();// 隐藏加载
			break;

		default:
			break;
		}
		return true;
	}
	
	/**
	 * 退出杀死进程
	 * void
	 */
	private void killProcess(){
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(PROCESS_NAME);
	}
}
