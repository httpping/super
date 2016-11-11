package com.zbar.lib;

import java.io.IOException;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.bean.InwardAction;
import com.vp.loveu.bean.InwardAction.ActionType;
import com.vp.loveu.bean.NetBaseBean;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.AESUtil;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ToastUtil;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.widget.IOSAlertDialog;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import cz.msebera.android.httpclient.Header;

/**
 * @Title: CaptureActivity.java
 * @Package com.zbar.lib
 * @Description: 扫描界面
 * @version tanping
 */
public class CaptureActivity extends VpActivity implements Callback {
	// private static final int OPEN = 100;
	// private static final int FINISH = 101;
	private final static String TAG = CaptureActivity.class.getSimpleName();
	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private RelativeLayout mContainer = null;
	private RelativeLayout mCropLayout = null;
	private boolean isNeedCapture = false;
	private boolean isDecode = true;
	private Activity mActivity;
	// private View closeBtn;
	private int mType;
	private int shopId;

	public boolean isNeedCapture() {
		return isNeedCapture;
	}

	public void setNeedCapture(boolean isNeedCapture) {
		this.isNeedCapture = isNeedCapture;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity=CaptureActivity.this;
		initView();

	}

	private void initView() {
		setContentView(R.layout.activity_qr_scan);
		
		initPublicTitle();
		mPubTitleView.mTvTitle.setText("扫一扫");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);

		// 初始化 CameraManager
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
		mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);


		View mQrLineView = (View) findViewById(R.id.capture_scan_line);
		TranslateAnimation mAnimation = new TranslateAnimation(
				TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE,
				0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 1f);
		mAnimation.setDuration(2500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.RESTART);
		mAnimation.setInterpolator(new LinearInterpolator());
		mQrLineView.setAnimation(mAnimation);

		// TitlebarHelper.TitleBarInit(mActivity,
		// mActivity.getString(R.string.txt_qr_scan),
		// TITLEBAR_ACTION_TYPE.TXT, R.string.txt_input_code);
		mType= this.getIntent().getIntExtra("type", 1);
		
	
	}

	boolean flag = true;

	protected void light() {
		if (flag == true) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();
		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		isDecode = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	// 二维码扫描，点击打开二维码扫描界面；
	// 可进行扫描签到二维码、

	public void handleDecode(String result) {
		
		if (isDecode) {
			inactivityTimer.onActivity();
			
			//playBeepSoundAndVibrate();
			// 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
			// handler.sendEmptyMessage(R.id.restart_preview);
			 VPLog.d(TAG, "---二维码结果---" + result);
			if (TextUtils.isEmpty(result)) {
				// warren toast
				// ToastHelper.showInfoToast(CaptureActivity.this,
				// "Scan failed,Please have a try!");
//				Toast.makeText(CaptureActivity.this,
//						"Scan failed,Please have a try!", Toast.LENGTH_SHORT)
//						.show();
			} else {
				activitySignIn(result);
			}
			
		}

	}

 
	public void activitySignIn(String result){
		
		//获取用户信息
		 LoginUserInfoBean loginuser = LoginStatus.getLoginInfo();
		if (loginuser== null) {
			finish();
			return;
		}
		//活动二维码签到
		String url = VpConstants.ACTIVITY_SIGN_IN;
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		
		mClient.setShowProgressDialog(true);
		JSONObject data = new JSONObject();
		try {
			 String deresult = AESUtil.Decrypt(result, VpConstants.KEY_QR_PASS);
			 //VPLog.d("qr", ""+result);
			InwardAction action = InwardAction.parseAction(deresult);
			data.put("scan_uid", loginuser.getUid());
			//data.put("scan_uid",22);
			data.put("activity_id", action.getValueForKey("activity_id") );
			data.put("uid", action.getValueForKey("uid") );
			data.put("order_id", action.getValueForKey("order_id") );
		} catch (Exception e) {
			//ToastUtil.showToast(CaptureActivity.this, result+"", 1);
			//handler.restartPreviewAndDecode();
			final InwardAction action =  InwardAction.parseAction(result);
			final IOSAlertDialog alertDialog = new IOSAlertDialog(CaptureActivity.this);
			if (action !=null) {
				
				if (action.mActionType == ActionType.app_sign_in) {//app签到
					String data1 = action.getValueForKey("d");
					String mnt =getIntent().getStringExtra("mt");//外部传入
					String to = "d="+data1 +"&mt="+mnt;
					try {
						VPLog.d("qr", to+"");
						to = AESUtil.Encrypt(to, VpConstants.KEY_QR_PASS);
						String url1 = VpConstants.APP_SIGN_IN_GO + URLEncoder.encode(to);
						VPLog.d("qr", url1+"");
						InwardAction.parseAction(url1).toStartActivity(CaptureActivity.this);
						finish();
						return ;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					return ;
				}
				
				if (action.mActionType != ActionType.http_web_url) {//非网页直接跳转
					action.toStartActivity(CaptureActivity.this);
					return ;
				}
				
				alertDialog.builder().setTitle("扫一扫").setMsg(result).setPositiveButton("取消", new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						try {
							alertDialog.dialog.dismiss();
						} catch (Exception e2) {
							e2.printStackTrace();
						}
						
					}
				}).setNegativeButton("前往", new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						action.toStartActivity(CaptureActivity.this);
					}
				}).setCancelable(false).show();
			}else {
				alertDialog.builder().setTitle("扫一扫").setMsg(result).setCancelable(false).show();
			}
			alertDialog.dialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					try {
						handler.restartPreviewAndDecode();
					} catch (Exception e2) {
					}
				}
			});
			e.printStackTrace();
			return ;
		}
		
		mClient.setShowProgressDialog(true);
		mClient.post(url, new RequestParams(), data.toString(), true, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				   String result = ResultParseUtil.deAesResult(responseBody);
				   NetBaseBean baseBean  = NetBaseBean.parseJson(result);
				   IOSAlertDialog alertDialog = new IOSAlertDialog(CaptureActivity.this);
					alertDialog.builder().setTitle("活动签到").setMsg(baseBean.msg).setCancelable(false).show();
					alertDialog.dialog.setOnDismissListener(new OnDismissListener() {
						RequestParams s;
						
						@Override
						public void onDismiss(DialogInterface dialog) {
							try {
								handler.restartPreviewAndDecode();
							} catch (Exception e2) {
								// TODO: handle exception
							}
						}
					});
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				IOSAlertDialog alertDialog = new IOSAlertDialog(CaptureActivity.this);
				alertDialog.builder().setTitle("签到失败").setMsg(new NetBaseBean().msg).setCancelable(false).show();
				alertDialog.dialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						try {
							handler.restartPreviewAndDecode();
						} catch (Exception e2) {
							// TODO: handle exception
						}
					}
				});
			}
		});
		
		
	}
	

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);

			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();

			int cropWidth = mCropLayout.getWidth() * width
					/ mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height
					/ mContainer.getHeight();

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);
			// 设置是否需要截图
			setNeedCapture(true);

		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(CaptureActivity.this);
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public Handler getHandler() {
		return handler;
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		// 异常？
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

 
}