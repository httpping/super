package com.vp.loveu.login.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.vp.loveu.MainActivity;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.bean.AppconfigBean;
import com.vp.loveu.bean.MapLoactionBean;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.myutils.CheckedAppUpdate;
import com.vp.loveu.index.myutils.CheckedAppUpdate.OnCheckedListener;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.AESUtil;
import com.vp.loveu.util.MD5Util;
import com.vp.loveu.util.MapLocationNetwork;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.UIUtils;

import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015-10-21 下午3:30:56
 * @Description:欢迎页面
 */
public class WelcomeActivity extends VpActivity implements OnCheckedListener {
	private ImageView mIvLoading;
	private DisplayImageOptions options;
	private String TAG = "WelcomeActivity";
	private long mAnimationTime = 1200;// 动画效果时间,单位:ms
	private long mShowTime = 2000;
	private long mMaxShowTime = 5000;
	private long mStartTime;
	private long mEndTime;
	private static final String KEY_SPLASH_PIC_CODE = "001001";// splash_pic
																// 对应的code
	public static final String KEY_SPLASH_PIC_PATH = "splash_pic";
	public static final String KEY_QINIU_AK = "AccessKey";
	public static final String KEY_QINIU_SK = "SecretKey";
	
	private String mSplashPath;

	private SharedPreferencesHelper sharedPreferencesHelper;
	private boolean mIsFours;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MapLocationNetwork.MAP_RESULT_WHAT:
				MapLoactionBean mLoactionBean = (MapLoactionBean) msg.obj;
				try {
					VpApplication.getInstance().getUser().setLat((float) mLoactionBean.lat);
					VpApplication.getInstance().getUser().setLng((float) mLoactionBean.lon);
					VpApplication.getInstance().getUser().setAdCode(mLoactionBean.adCode);
				} catch (Exception e) {
				}
				finish();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
		setContentView(R.layout.login_welcome_activity);
		mClient = new VpHttpClient(this);
		this.mClient.setShowProgressDialog(false);
		sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
		mSplashPath = sharedPreferencesHelper.getStringValue(KEY_SPLASH_PIC_PATH);
		initView();
		loadDatas();
		
		
		
		
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				//若网络请求慢,最多在splash页面停留6s时间
				//从本地获取信息
				mHandler.removeCallbacksAndMessages(null);
				LoginUserInfoBean userInfoBean = new LoginUserInfoBean(WelcomeActivity.this).getUserInfoFromLocal();
				if(userInfoBean!=null){
					VpApplication.getInstance().setUser(userInfoBean);
					startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
				}else{					
					Toast.makeText(WelcomeActivity.this, "网络访问异常,请重新登录", Toast.LENGTH_SHORT).show();
					startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
				}
				finish();
				
			}
		}, 6000);

	}
	
	@Override
	protected void onDestroy() {
		mHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	private void initView() {
		mIvLoading = (ImageView) findViewById(R.id.iv_welcome_page);

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.color.frenchgrey) // resource
																							// or
				.showImageForEmptyUri(R.drawable.ic_launcher) // resource or
				.showImageOnFail(R.drawable.ic_launcher) // resource or
				.resetViewBeforeLoading(false) // default
				.cacheInMemory(true) // default
				.cacheOnDisk(true) // default
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(false) // default
				.displayer(new SimpleBitmapDisplayer()).build();
		mIvLoading.setScaleType(ScaleType.CENTER_CROP);
		mStartTime = System.currentTimeMillis();
	}

	private void loadDatas() {
		// 獲取軟件基本信息
		String url = VpConstants.USER_APP_CONFIG;
		RequestParams params = new RequestParams();
		params.put("code", "001");

		//new CheckedAppUpdate(this, false, this).upGradeApp();// 检测升级

		if (mSplashPath == null) {
			// 首次进入app，启动默认页面
			mIvLoading.setImageResource(R.drawable.splash);
		} else {
			// 已存在启动页
			Bitmap decodeFile = BitmapFactory.decodeFile(mSplashPath);
			mIvLoading.setImageBitmap(decodeFile);
		}
		makeAnimation();
		
		mClient.setShowProgressDialog(false);
		// 后台访问服务下载启动页
		mClient.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				// 返回成功
				String result = ResultParseUtil.deAesResult(responseBody);
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {// 获取信息成功
						String data = json.getString(VpConstants.HttpKey.DATA);
						Map<String, AppconfigBean> appInfo = AppconfigBean.createFromJsonArray(data);
						if (appInfo != null) {
							VpApplication.getInstance().setAppInfoBean(appInfo);
							String path = appInfo.get(KEY_SPLASH_PIC_CODE).getVal();
							if (path != null && !"".equals(path)) {
								String destFile = getDestFile(path);
								if (!destFile.equals(mSplashPath)) {
									// 去下载
									downloadFile(path, destFile);
								}
							} else {
								sharedPreferencesHelper.putStringValue(KEY_SPLASH_PIC_PATH, null);
							}
							if (appInfo.get("001005").getStatus() == 1 ) {
								//七牛服务启动
								sharedPreferencesHelper.putStringValue(KEY_QINIU_AK, appInfo.get("001005001").getVal());
								sharedPreferencesHelper.putStringValue(KEY_QINIU_SK, appInfo.get("001005002").getVal());
							}
							//导航栏颜色
							AppconfigBean appconfigBean = appInfo.get("001002");
							if (appconfigBean.getStatus() == 1 ) {//1启动 0不启动
								sharedPreferencesHelper.putStringValue("app_top_color", appconfigBean.getVal());
							}
							
							//上传音频可获得积分
							AppconfigBean appconfigBean2 = appInfo.get("001008");
							sharedPreferencesHelper.putStringValue("upload_integral", appconfigBean2.getVal());
							
							//分享app的描述语  以及分享的app图标地址
							sharedPreferencesHelper.putStringValue("app_share_describe", appInfo.get("001006").getVal());
							sharedPreferencesHelper.putStringValue("app_share_icon", appInfo.get("001007").getVal());
						}
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(WelcomeActivity.this, message, Toast.LENGTH_LONG).show();
						afterGetSplashPicFailed();
					}
				} catch (JSONException e1) {
					afterGetSplashPicFailed();
				}
				;

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				// afterGetSplashPicFailed();

			}
		});

	}

	protected void makeAnimation() {
		mIvLoading.setVisibility(View.VISIBLE);
		AlphaAnimation alpAn = new AlphaAnimation(0, 1);
		alpAn.setDuration(mAnimationTime);
		mIvLoading.startAnimation(alpAn);
		alpAn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画完成,跳转到首页
				Log.d(TAG, "动画完成");
				checkUpdate(mShowTime);
			}
		});
	}

	private void afterGetSplashPicFailed() {
		mEndTime = System.currentTimeMillis();
		long delayedTime = mMaxShowTime - (mEndTime - mStartTime);
		if (delayedTime < 0)
			delayedTime = 0;
		checkUpdate(delayedTime);
	}
	
	private void checkUpdate(long delayedTime){
		//检查更新
		//new CheckedAppUpdate(this, false, this).upGradeApp();// 检测升级
		
		turn2Login(0);
	}
	
	

	protected void turn2Login(long delayedTime) {						
		this.mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				LoginUserInfoBean userInfoBean = new LoginUserInfoBean(WelcomeActivity.this).getUserInfoFromLocal();
				if (userInfoBean != null) {

					VpApplication.getInstance().setUser(userInfoBean);
					// 直接静默登录并跳转到首页
					userInfoBean.saveLoginUserInfo(userInfoBean.getLoginType(), userInfoBean.getOpenId(), userInfoBean.getUid() + "",
							userInfoBean.getXmpp_user(), userInfoBean.getXmpp_pwd(), "1", 0, 0, mClient, new SaveUserInfoCallBack() {

						@Override
						public void onSuccess() {
							// 获取经纬度
							new MapLocationNetwork(mHandler, WelcomeActivity.this);

						}

						@Override
						public void onFailed() {

						}
					});
					return;
				}

				Intent loginIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
				startActivity(loginIntent);
				finish();
			}
		}, delayedTime);

	}

	private String getDestFile(String srcpath) {
//		String path = "";
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			path = new File(Environment.getExternalStorageDirectory(), MD5Util.MD516(srcpath) + ".png").getAbsolutePath();
//		} else {
//			path = new File(UIUtils.getContext().getFilesDir(), MD5Util.MD516(srcpath) + ".png").getAbsolutePath();
//		}
		String path = new File(UIUtils.getContext().getFilesDir(),MD5Util.MD516(srcpath)+".png").getAbsolutePath();
		return path;
	}

	/**
	 * 下载文件
	 * 
	 * @param url
	 * @throws Exception
	 */
	public void downloadFile(String srcUrl, final String destUrl) {
		mClient.setShowProgressDialog(false);
		mClient.get(srcUrl, new RequestParams(), new BinaryHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] binaryData) {
				Bitmap bmp = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);

				File file = new File(destUrl);
				// 压缩格式
				CompressFormat format = Bitmap.CompressFormat.JPEG;
				// 压缩比例
				int quality = 100;
				try {
					// 若存在则删除
					if (file.exists())
						file.delete();
					// 创建文件
					file.createNewFile();
					//
					OutputStream stream = new FileOutputStream(file);
					// 压缩输出
					bmp.compress(format, quality, stream);
					// 关闭
					stream.close();

					sharedPreferencesHelper.putStringValue(KEY_SPLASH_PIC_PATH, destUrl);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onProgress(long bytesWritten, long totalSize) {
				super.onProgress(bytesWritten, totalSize);
			}

			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] binaryData, Throwable error) {
				Toast.makeText(WelcomeActivity.this, "下载失败", Toast.LENGTH_LONG).show();

			}
		});

	}

	public interface SaveUserInfoCallBack {
		public void onSuccess();

		public void onFailed();
	}

	@Override
	public void onDownCancle(boolean isFours) {
		if(isFours){
			//强制升级
			finish();
		}else{
			turn2Login(0);
		}
	}

	@Override
	public void onDownError(boolean isFours) {
		if(isFours){
			//强制升级
			finish();
		}else{
			turn2Login(0);
		}
	}

	@Override
	public void onDownFinish(boolean isFours) {
		//去安装
		mIsFours=isFours;
	}

	@Override
	public void onNotUpground() {
		//不需要升级
		turn2Login(0);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if(arg1==Activity.RESULT_OK && arg0== CheckedAppUpdate.REQUEST_CODE){
			
		}else{
			//用户取消安装
			if(mIsFours){
				finish();
			}else{
				turn2Login(0);
			}
		}
	}
}
