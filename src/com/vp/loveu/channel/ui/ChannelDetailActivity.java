package com.vp.loveu.channel.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.channel.bean.ChannelDetailBean;
import com.vp.loveu.channel.bean.RewardsBean;
import com.vp.loveu.channel.db.RadioDbDao;
import com.vp.loveu.channel.db.RadioHistoryBean;
import com.vp.loveu.channel.dialog.RewardsDialog;
import com.vp.loveu.channel.musicplayer.IMusicService;
import com.vp.loveu.channel.musicplayer.MusicCallBack;
import com.vp.loveu.channel.musicplayer.MusicService;
import com.vp.loveu.channel.widget.ShareView;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.login.bean.UserBaseInfoBean;
import com.vp.loveu.my.activity.IntergralActivity;
import com.vp.loveu.my.bean.NewIntergralBean.NewIntergralDataBean;
import com.vp.loveu.pay.bean.EnjoyPayBean.EnjoyPayType;
import com.vp.loveu.pay.ui.EnjoyPayActivity;
import com.vp.loveu.util.BitmapBlur;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ObjectRotateHandler;
import com.vp.loveu.util.ShareCompleteUtils;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.widget.CircleImageView;
import com.vp.loveu.widget.ZanAllHeadView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.custom.ShareDialogFragment;
import cn.sharesdk.onekeyshare.custom.ShareModel;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年11月19日 下午5:12:39
 * @Description:恋爱电台播放页面
 */
public class ChannelDetailActivity extends VpActivity implements OnClickListener, PlatformActionListener {
	public static final String RADIOID = "id";
	public static final String CHANNEL_NAME = "channel_name";
	public static final String TUTOR_NAME = "tutor_name";
	public static final String IS_LISTENED = "is_listened";
	public static final String CURRENTPOSTION = "currentposition";
	public static final String TOTALPOSTION = "totalposition";
	public static final String IS_OPEN_FROM_OTHER = "is_open_from_other";
	public int mUserId;
	NewIntergralDataBean bean;
	private int mRadioId;
	private String mChannelName;
	private String mTutorName;
	private String mChannelCover;
	private int mHistoryPosition;
	private int mTotalPosition;
	private ChannelDetailBean mChannelDetailbean;
	private ArrayList<UserBaseInfoBean> mUsers;// 在学用户列表
	private ZanAllHeadView mUsersView;

	private ImageView mIvBg;
	private TextView mTvChannelName;
	private TextView mTvTutorName;
	private TextView mTvStartTime;
	private TextView mTvEndTime;
	private ImageView mIvPlay;
	private CircleImageView mIvChannelIcon;
	private ImageView mIvBack;
	private ImageView mIvPause;
	private SeekBar mSeekBar;
	private MusicConnection mMusicConn;
	private IMusicService iMusicService;
	private ShareView mBtnShare;// 分享
	private ShareDialogFragment mShareDialog;
	private RewardsDialog mRewardsDialog;
	private ImageButton mBtnRewards;// 打赏
	private ShareModel shareModel;
	private Intent musicServiceIntent;
	private String mCurrentPath;
	// private boolean mIsPlaying;
	private RadioDbDao mDao;
	private DisplayImageOptions options;
	private boolean mPlayFlag = true;

	private boolean mAnimatorIsplay = false;

	// 判断是否是从首页根据服务器返回地址进行跳转到该页面
	private boolean isEnterFromOther = false;

	private ObjectRotateHandler mObjectRotate;
	private long mRotateTime = 3000;
	RewardsBean enjoyPayBean = new RewardsBean();
	// private int mSecondProgress;//缓冲进度条
	private SharedPreferencesHelper sharedPreferencesHelper;
	private static final String BUFFERED_COMPLETE_RID = "buffered_complete_rid";// 记录以及缓冲完毕的电台ID

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		setContentView(R.layout.channel_detail_activity);
		sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
		this.mUserId = LoginStatus.getLoginInfo().getUid();
		this.mClient = new VpHttpClient(this);
		this.mClient.setShowProgressDialog(false);
		this.mRadioId = getIntent().getIntExtra(RADIOID, 0);
		this.mChannelName = getIntent().getStringExtra(CHANNEL_NAME);
		this.mTutorName = getIntent().getStringExtra(TUTOR_NAME);
		this.mHistoryPosition = getIntent().getIntExtra(CURRENTPOSTION, 0);
		this.mTotalPosition = getIntent().getIntExtra(TOTALPOSTION, 0);
		this.isEnterFromOther = getIntent().getBooleanExtra(IS_OPEN_FROM_OTHER, false);
		this.mDao = new RadioDbDao(this);
		bean = (NewIntergralDataBean) getIntent().getSerializableExtra("obj");

		mShareDialog = new ShareDialogFragment();
		mShareDialog.setShowCopy(false);
		mShareDialog.setPlatformActionListener(this);
		mRewardsDialog = new RewardsDialog(this);
		shareModel = new ShareModel();

		initView();
		initDatas();

	}

	private void initView() {
		this.mIvBg = (ImageView) findViewById(R.id.channel_iv_bg);
		this.mUsersView = (ZanAllHeadView) findViewById(R.id.channel_detail_users);
		mUsersView.mPortraitCount.setBackgroundResource(R.drawable.circle_text_shapecccc);
		// mUsersView.mPortraitCount.setTextColor(Color.parseColor("#FF7A00"));
		this.mTvChannelName = (TextView) findViewById(R.id.channel_detail_title);
		this.mTvTutorName = (TextView) findViewById(R.id.channel_detail_tutor);
		this.mTvStartTime = (TextView) findViewById(R.id.channel_detail_starttime);
		this.mTvEndTime = (TextView) findViewById(R.id.channel_detail_endtime);
		this.mIvPlay = (ImageView) findViewById(R.id.channel_detail_play);
		this.mIvChannelIcon = (CircleImageView) findViewById(R.id.channel_detail_icon);
		this.mSeekBar = (SeekBar) findViewById(R.id.channel_detail_seekbar);
		this.mIvBack = (ImageView) findViewById(R.id.channel_radio_iv_back);
		this.mIvPause = (ImageView) findViewById(R.id.channel_radio_iv_pause);
		mBtnShare = (ShareView) findViewById(R.id.channel_btn_share);
		mBtnShare.setDrawable(R.drawable.channel_lovestation_share);
		mBtnRewards = (ImageButton) findViewById(R.id.channel_btn_rewards);
		this.mIvChannelIcon.setOnClickListener(this);
		this.mIvPlay.setOnClickListener(this);
		this.mIvBack.setOnClickListener(this);
		this.mIvPause.setOnClickListener(this);
		mBtnShare.setOnClickListener(this);
		mBtnRewards.setOnClickListener(this);
		this.mObjectRotate = new ObjectRotateHandler(mIvChannelIcon);
		this.mObjectRotate.setDuration(this.mRotateTime);

		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					iMusicService.seekToPosition(progress);
				}
			}
		});

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image_loading_fail) // resource
																												// or
				.showImageForEmptyUri(R.drawable.default_image_loading_fail) // resource
																				// or
				.showImageOnFail(R.drawable.default_image_loading_fail) // resource
																		// or
				.resetViewBeforeLoading(false) // default
				.cacheInMemory(true) // default
				.cacheOnDisk(true) // default
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(false) // default
				.displayer(new SimpleBitmapDisplayer()).build();

	}

	private void initDatas() {

		this.mTvChannelName.setText(this.mChannelName);
		this.mTvTutorName.setText("导师 :" + this.mTutorName);

		String url = VpConstants.CHANNEL_RADIO_DETAIL;
		RequestParams params = new RequestParams();
		int uid = LoginStatus.getLoginInfo().getUid();
		params.put("id", this.mRadioId);
		params.put("uid", uid);
		mClient.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {// 返回成功
						JSONObject jsonData = json.getJSONObject(VpConstants.HttpKey.DATA);
						mChannelDetailbean = ChannelDetailBean.parseJson(jsonData.toString());
						if (mChannelDetailbean != null) {
							mUsers = mChannelDetailbean.getUsers();
							mCurrentPath = mChannelDetailbean.getUrl();
							setViewDatas(mChannelDetailbean);
						}
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(ChannelDetailActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(ChannelDetailActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();

			}
		});

	}

	/**
	 * 设置收听用户数据
	 */
	protected void setViewDatas(ChannelDetailBean bean) {
		// 收听的用户中，如果包含当前用户，移动添加到首个（服务器中有缓存，退出后头像用户还存在）

		JSONObject obj = new JSONObject();
		try {
			obj.put("username", mChannelDetailbean.getNickname());
			obj.put("user_avatar", mChannelDetailbean.getPortrait());
			obj.put("to_uid", mChannelDetailbean.getUid());
			obj.put("type", EnjoyPayType.love_radio.getValue());
			obj.put("remark", mChannelDetailbean.getName());
			obj.put("src_id", mChannelDetailbean.getId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		enjoyPayBean.setObj(obj.toString());
		
		
		UserBaseInfoBean currentUser = null;
		if (mUsers != null) {
			Iterator<UserBaseInfoBean> iterator = mUsers.iterator();
			while (iterator.hasNext()) {
				UserBaseInfoBean user = iterator.next();
				if (user.getUid() == mUserId) {
					currentUser = user;
					iterator.remove();
					break;
				}
			}
			if (currentUser == null) {
				currentUser = new UserBaseInfoBean();
				currentUser.setUid(LoginStatus.getLoginInfo().getUid());
				currentUser.setName(LoginStatus.getLoginInfo().getNickname());
				currentUser.setPortrait(LoginStatus.getLoginInfo().getPortrait());
			}
			mUsers.add(0, currentUser);
			mUsersView.setMakeAnnimation(true);
			mUsersView.setDatas(mUsers);
			doAnimation(0, bean.getUser_num());

			// 判断是否可以打赏
			if (bean.getReward_able() == 1) {// 是否可打赏，1=可以，0=不可以
				mBtnRewards.setEnabled(true);
				mBtnRewards.setAlpha(1.0f);
			} else {
				mBtnRewards.setEnabled(false);
			}
			mBtnShare.setText(bean.getShare_exp());// 分享可获得积分

			shareModel.setId(bean.getId());
			shareModel.setTitle(bean.getName());
			shareModel.setUrl(VpConstants.AUDIO_SHARE + bean.getId());
			if (!TextUtils.isEmpty(bean.getPortrait()) && bean.getPortrait().length() > 15) {
				shareModel.setImageUrl(bean.getPortrait());
			}
			shareModel.setType(5); // 分享类型 999=分享APP本身 1=长文推荐 2=PUA课堂 3=大家都在聊
			if (bean != null) {
				shareModel.setTag(IntergralActivity.TAG);
				shareModel.setObj(this.bean);
			}

		}
		this.mChannelCover = bean.getCover();
		ImageLoader.getInstance().displayImage(this.mChannelCover, mIvChannelIcon, options);
		// ImageLoader.getInstance().displayImage(this.mChannelCover, mIvBg,
		// options);

		ImageLoader.getInstance().loadImage(this.mChannelCover, options, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String arg0, View arg1) {

			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

			}

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				try {
					mIvBg.setImageBitmap(BitmapBlur.doBlur(arg2, 12, false));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {

			}
		});

		if (this.isEnterFromOther) {
			this.mTvChannelName.setText(bean.getName());
			this.mTvTutorName.setText("导师 :" + bean.getNickname());
		}
	}

	/**
	 * 收听用户数做滚动效果
	 * 
	 * @param start
	 * @param end
	 */
	private void doAnimation(int start, int end) {
		ValueAnimator animator = ValueAnimator.ofInt(start, end);
		if (end < 10) {
			animator.setDuration(500);
		} else if (end < 100) {
			animator.setDuration(1000);
		} else {
			animator.setDuration(2000);
		}
		animator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animator) {
				int value = (Integer) animator.getAnimatedValue();
				mUsersView.mPortraitCount.setText(value + "");

			}
		});
		animator.start();
	}

	@Override
	protected void onStart() {
		super.onStart();
		musicServiceIntent = new Intent(this, MusicService.class);
		startService(musicServiceIntent);
		mMusicConn = new MusicConnection();
		bindService(musicServiceIntent, mMusicConn, BIND_AUTO_CREATE);

		mPlayFlag = true;
		setPlayButtonView();
	}

	private void setPlayButtonView() {
		// makeAnnimation();
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (mPlayFlag) {
					refresUI();
					SystemClock.sleep(500);
				}

			}
		}) {
		}.start();

	}

	private void refresUI() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				try {

					if (iMusicService != null && iMusicService.isPlaying()) {
						mIvPlay.setImageResource(R.drawable.channel_vadio_pause);
						restartAnnimation();
						//updateProgressView(iMusicService.getCurrentPosition(), iMusicService.getTotalPosition());
					} else {
						mIvPlay.setImageResource(R.drawable.channel_vadio_play);
						stopAnnimation();
					}
					updateProgressView(iMusicService.getCurrentPosition(), iMusicService.getTotalPosition());
				} catch (Exception e) {
					e.printStackTrace();
					mIvPlay.setImageResource(R.drawable.channel_vadio_play);
					stopAnnimation();
				}

			}
		});
	}

	private void makeAnnimation() {
		if (iMusicService != null && iMusicService.isPlaying()) {
			this.mObjectRotate.start();
		}
	}

	private void restartAnnimation() {
		mAnimatorIsplay = true;
		this.mObjectRotate.resume();
		// if(this.mObjectRotate.isPaused()){
		// }
	}

	private void stopAnnimation() {
		mAnimatorIsplay = false;
		this.mObjectRotate.pause();
		// if(this.mObjectRotate.isRun){
		// }
	}

	@Override
	protected void onStop() {
		if (mMusicConn != null)
			unbindService(mMusicConn);// 解绑服务
		mPlayFlag = false;
		super.onStop();
	}

	private class MusicConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iMusicService = (IMusicService) service;

			// mIsPlaying=iMusicService.isPlaying();
			// mIvPlay.setImageResource(mIsPlaying?R.drawable.channel_vadio_pause:R.drawable.channel_vadio_play);

			// 播放当前选中项目
			iMusicService.setDatas(mRadioId, new MusicCallBack() {

				@Override
				public void onProgress(int currentPosition, int totalPosition) {
					updateProgressView(currentPosition, totalPosition);
				}

				@Override
				public void onPre() {
					// 上一首

				}

				@Override
				public void onNext() {
					// 播放完毕,准备下一首
					mDao.delete(mUserId, mRadioId);
				}

				@Override
				public void onInitProgress(int currentPosition, int totalPosition) {
					// 进入页面时初始进度
					if (iMusicService.mpIsNull()) {
						updateProgressView(mHistoryPosition, mTotalPosition);
						// 回复缓冲进度
						if (sharedPreferencesHelper.getIntegerValue(BUFFERED_COMPLETE_RID) == mRadioId)
							updateProgressView(mHistoryPosition, mTotalPosition, 100);
					} else {
						updateProgressView(currentPosition, totalPosition);
						// 回复缓冲进度
						if (sharedPreferencesHelper.getIntegerValue(BUFFERED_COMPLETE_RID) == mRadioId)
							updateProgressView(currentPosition, totalPosition, 100);
					}

				}

				@Override
				public void onBufferProgress(int currentPosition, int totalPosition, int percent) {
					updateProgressView(currentPosition, totalPosition, percent);

				}
			}, mClient);

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			System.out.println(name);

		}

	}

	/**
	 * 更新进度
	 */
	private void updateProgressView(final int currentPosition, final int totalPosition) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(totalPosition >60 * 1000 *60 * 3){//超过5小时的 时间不对
					return ;
				}
				
				if (currentPosition > totalPosition) {
					mTvStartTime.setText(formatTime(totalPosition));
				}else{
					mTvStartTime.setText(formatTime(currentPosition));
				}
					
				mTvEndTime.setText(formatTime(totalPosition));
				mSeekBar.setMax(totalPosition);
				if(currentPosition >= mSeekBar.getMax()){
					mSeekBar.setProgress(mSeekBar.getMax());
				}else{
					mSeekBar.setProgress(currentPosition);
				}

			}
		});
	}

	/**
	 * 更新缓冲进度
	 * 
	 * @param position
	 * @return
	 */
	private void updateProgressView(final int currentPosition, final int totalPosition, final int percent) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				sharedPreferencesHelper.putIntegerValue(BUFFERED_COMPLETE_RID, -1);
				if (percent > 0) {
					int bufferPosition = ((new BigDecimal(percent).divide(new BigDecimal(100))).multiply(new BigDecimal(totalPosition)))
							.intValue();
					mSeekBar.setSecondaryProgress(bufferPosition);
					mSeekBar.setMax(totalPosition);
				}
				if (percent == 100)
					sharedPreferencesHelper.putIntegerValue(BUFFERED_COMPLETE_RID, mRadioId);

			}
		});
	}

	private String formatTime(int position) {
		int m = position / 1000;
		int s = m / 60;
		int add = m % 60;
		if (add < 10)
			return (s + ":0" + add);
		else
			return (s + ":" + add);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.channel_detail_play:
		case R.id.channel_detail_icon:
			if (mChannelDetailbean != null) {
				mCurrentPath = mChannelDetailbean.getUrl();
				iMusicService.play(mCurrentPath, mHistoryPosition, mChannelName, mTutorName, mChannelCover);

				RadioHistoryBean historyBean = mDao.findRadioHistory(mUserId, mRadioId);
				if (historyBean != null && historyBean.getUploadtoserver() == 0) {
					uploadChanelInfo();
				}

			}
			break;
		case R.id.channel_radio_iv_back:
			finish();
			break;
		case R.id.channel_radio_iv_pause:
			// if(iMusicService.isPlaying()){
			// iMusicService.startOrpause();
			// exitRadio();
			// }
			iMusicService.stop();
			exitRadio();

			break;
		case R.id.channel_btn_share:
			// 分享
			// 临时记录当前分享的内容信息
			VpApplication.getInstance().setShareModel(shareModel);
			mShareDialog.show(getSupportFragmentManager(), "tag", shareModel);
			break;
		case R.id.channel_btn_rewards:
			// 打赏
			// if (mRewardsDialog.isShowing()) {
			// break;
			// }
			// mRewardsDialog.show();

			Intent intent = new Intent(this, EnjoyPayActivity.class);
			intent.putExtra(EnjoyPayActivity.PAY_PARAMS, enjoyPayBean);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	private void exitRadio() {
		String url = VpConstants.CHANNEL_RADIO_LEAVE;
		JSONObject body = new JSONObject();
		try {
			body.put("uid", this.mUserId);
			body.put("id", this.mRadioId);
		} catch (Exception e) {
			Toast.makeText(ChannelDetailActivity.this, "请求参数有误", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		}
		this.mClient.post(url, new RequestParams(), body.toString(), false, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {// 返回成功
						finish();
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(ChannelDetailActivity.this, message, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				;
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				// Toast.makeText(ChannelDetailActivity.this, "网络访问错误",
				// Toast.LENGTH_SHORT).show();
				finish();
			}
		});

	}

	/**
	 * 上报用户收听电台
	 */
	private void uploadChanelInfo() {
		String url = VpConstants.CHANNEL_RADIO_LISTEN;
		JSONObject body = new JSONObject();
		try {
			body.put("uid", mUserId);
			body.put("id", mRadioId);
		} catch (Exception e) {
			Toast.makeText(ChannelDetailActivity.this, "请求参数有误", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		}
		this.mClient.post(url, new RequestParams(), body.toString(), false, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {// 返回成功
						mDao.updateUploadInfo(mUserId, mRadioId);
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(ChannelDetailActivity.this, message, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				;
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(ChannelDetailActivity.this, "网络访问错误", Toast.LENGTH_SHORT).show();
			}
		});

	}

	/**
	 * 暂停并退出时记录下当前进度
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!iMusicService.mpIsNull() && !iMusicService.isPlaying()) {
			int totalPosition = iMusicService.getTotalPosition();
			int currentPosition = iMusicService.getCurrentPosition();
			if (currentPosition > 0) {
				boolean update = mDao.update(mUserId, mRadioId, currentPosition, totalPosition);
			}

			iMusicService.stop();
		}
		if (mObjectRotate != null)
			mObjectRotate.stop();
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {
		Toast.makeText(this, "分享取消", 0).show();
	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		// 微信分享成功回调需要在WXEntryActivity 处理
		if (arg0.getName().equals(SinaWeibo.NAME)) {
			Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
			LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
			ShareModel tempModel = VpApplication.getInstance().getShareModel();
			if (tempModel != null && loginInfo != null) {
				if (IntergralActivity.TAG.equals(tempModel.getTag()) && tempModel.getObj() != null) {// ==0 新手任务  ==1 已完成新手任务 不需要发送广播
					//发送广播通知
					Intent intent = new Intent(IntergralActivity.ACTION);
					intent.putExtra("obj", tempModel.getObj());
					UIUtils.getContext().sendBroadcast(intent);
				}
				ShareCompleteUtils utils = new ShareCompleteUtils(this);
				utils.reportData(loginInfo.getUid(), tempModel.getId(), tempModel.getType());
				VpApplication.getInstance().setShareModel(null);
			}
		}
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		Toast.makeText(this, "分享错误", 0).show();
	}
}
