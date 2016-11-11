package com.vp.loveu.login.ui;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.bean.InwardAction;
import com.vp.loveu.bean.MapLoactionBean;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.login.bean.ThirdAppUserBean;
import com.vp.loveu.login.bean.UserInfo;
import com.vp.loveu.login.bean.WechatUserBean;
import com.vp.loveu.login.ui.WelcomeActivity.SaveUserInfoCallBack;
import com.vp.loveu.login.utils.ThirdLoginUtils;
import com.vp.loveu.util.MapLocationNetwork;
import com.vp.loveu.util.PackageHelper;
import com.vp.loveu.util.ScreenUtils;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.widget.CustomProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015-10-23 上午10:50:38
 * @Description: 登录界面
 */
public class LoginActivity extends VpActivity implements OnClickListener {
	protected static final String TAG = "LoginActivity";
	private EditText mEtNumber;
	private EditText mEtValidCode;
	private Button mBtnGetVerify;
	private Button mBtnLogin;
	private int second = 60;
	private Timer timer;
	private TextView mTvServicesDesc;
	private TextView mTvValidCodeTime;
	private CheckBox mRbServices;
	private boolean mIsCheck = true;

	private CustomProgressDialog progressDialog = null;

	// 第三方登录
	private ThirdLoginUtils mThirdLoginUtils;
	private ImageView mIvSina;
	private ImageView mIvWechat;
	private ImageView mIvQQ;

	public static final int LOGINTYPE_PHONE = 1;
	public static final int LOGINTYPE_SINA = 2;
	public static final int LOGINTYPE_QQ = 3;
	public static final int LOGINTYPE_WECHAT = 4;
	private int mLoginType = LOGINTYPE_PHONE;// 登陆类型
												// 1=手机号登陆,2=新浪微博登陆，3=QQ登陆,4=微信登录
	private String mOpenUid;
	private String mThirdPortrait;// 第三方应用头像
	private String mThirdNickName; // 第三方应用昵称
	private WechatUserBean mWechatBean;
	private boolean isGettingVaildCode;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MapLocationNetwork.MAP_RESULT_WHAT:
				MapLoactionBean mLoactionBean = (MapLoactionBean) msg.obj;
				login(mLoactionBean.adCode, (float) mLoactionBean.lat, (float) mLoactionBean.lon);
				break;

			case ThirdLoginUtils.AUTHORIZE_COMPLETE:
				// 授权成功
				Toast.makeText(LoginActivity.this, "授权成功,正在登陆", Toast.LENGTH_SHORT).show();
				Object[] objs = (Object[]) msg.obj;
				String platform = (String) objs[0];
				if (SinaWeibo.NAME.equals(platform) || QQ.NAME.equals(platform)) {
					ThirdAppUserBean sinaBean = (ThirdAppUserBean) (objs[1]);
					mOpenUid = sinaBean.getOpenid();
					mThirdPortrait = sinaBean.getHeadimgurl();
					mThirdNickName = sinaBean.getNickname();
					if (SinaWeibo.NAME.equals(platform))
						mLoginType = LOGINTYPE_SINA;
					else
						mLoginType = LOGINTYPE_QQ;
					// 登陆
					login();
				}
				break;
			case ThirdLoginUtils.AUTHORIZE_CANCEL:
				// 取消授权
				Toast.makeText(LoginActivity.this, "取消授权", Toast.LENGTH_SHORT).show();
				break;
			case ThirdLoginUtils.AUTHORIZE_ERROR:
				// 授权失败
				Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScreenUtils.initScreen(this);
		setContentView(R.layout.login_index_activity);
		mClient = new VpHttpClient(this);
		mThirdLoginUtils = new ThirdLoginUtils(mHandler, this);
		isGettingVaildCode = false;
		initView();
		progressDialog = CustomProgressDialog.createDialog(this);
	}

	@Override
	protected void onStart() {
		VPLog.d(tag, "onStart");
		super.onStart();

	}

	@Override
	protected void onResume() {
		super.onResume();
		hiedDialog();
		if (VpApplication.getInstance().getWechatUserBean() != null) {
			mWechatBean = VpApplication.getInstance().getWechatUserBean();
			VpApplication.getInstance().setWechatUserBean(null);
			if (mWechatBean != null) {
				Toast.makeText(this, "授权成功,正在登陆", Toast.LENGTH_SHORT).show();
				// 微信登录
				// mOpenUid=mWechatBean.getOpenid();
				mOpenUid = mWechatBean.getUnionid();
				mLoginType = LOGINTYPE_WECHAT;
				mThirdPortrait = mWechatBean.getHeadimgurl();
				mThirdNickName = mWechatBean.getNickname();
				// 登陆
				login();
			}
		}

	}

	private void initView() {
		initPublicTitle();
		this.mPubTitleView.mBtnLeft.setVisibility(View.GONE);
		this.mPubTitleView.mTvTitle.setText("登录");
		mEtNumber = (EditText) findViewById(R.id.login_et_number);
		mEtValidCode = (EditText) findViewById(R.id.login_et_valid_code);
		mBtnGetVerify = (Button) findViewById(R.id.login_btn_getvalidcode);
		mBtnLogin = (Button) findViewById(R.id.login_btn_login);
		mTvServicesDesc = (TextView) findViewById(R.id.login_services_tv_desc);
		mTvValidCodeTime = (TextView) findViewById(R.id.login_validcode_tv_timmer);
		mTvServicesDesc.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mTvServicesDesc.getPaint().setAntiAlias(true);// 抗锯齿
		mTvServicesDesc.setTextColor(Color.parseColor("#76A0F6"));
		mRbServices = (CheckBox) findViewById(R.id.login_services_rb_desc);
		mIvSina = (ImageView) findViewById(R.id.login_iv_sina);
		mIvWechat = (ImageView) findViewById(R.id.login_iv_wechat);
		mIvQQ = (ImageView) findViewById(R.id.login_iv_qq);
		mRbServices.setChecked(mIsCheck);
		// mRbServices.setOnCheckedChangeListener(this);
		mBtnGetVerify.setOnClickListener(this);
		mBtnLogin.setOnClickListener(this);
		mTvServicesDesc.setOnClickListener(this);
		mRbServices.setOnClickListener(this);
		mIvSina.setOnClickListener(this);
		mIvWechat.setOnClickListener(this);
		mIvQQ.setOnClickListener(this);

		createEditTextChangeListener(mEtNumber);
		createEditTextChangeListener(mEtValidCode);

	}

	private void createEditTextChangeListener(final EditText etNumber) {
		etNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 登录按钮默认情况为置灰，在输入手机号码和验证码的情况下，变为可点击状态
				String number = mEtNumber.getText().toString();
				setLoginButtonEnable();

				if (etNumber.getId() == R.id.login_et_number) {
					if (isPhoneNumberValid(number)) {
						mBtnGetVerify.setEnabled(true);
					} else {
						mBtnGetVerify.setEnabled(false);
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * 获取验证码
	 * 
	 * @param v
	 */
	private void getValidCode() {
		String phonevalue = mEtNumber.getText().toString();
		if (TextUtils.isEmpty(phonevalue)) {
			Toast.makeText(LoginActivity.this, "请输入手机号", Toast.LENGTH_LONG).show();
		} else if (!TextUtils.isEmpty(phonevalue)) {
			if (isPhoneNumberValid(phonevalue)) {
				// 发送网络请求
				JSONObject jsonObj = new JSONObject();
				try {
					jsonObj.put("mt", Long.parseLong(phonevalue));
					jsonObj.put("name", "auth_code");
					jsonObj.put("src", 5);
				} catch (Exception e) {
					Toast.makeText(LoginActivity.this, "请求参数有误", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					return;
				}
				if (isGettingVaildCode)// 已经在获取验证码
					return;
				isGettingVaildCode = true;
				mBtnGetVerify.setEnabled(false);
				startTimer();
				mClient.post(LoginActivity.this, VpConstants.USER_SEND_SMS_CODE, new RequestParams(), jsonObj.toString(), true,
						new AsyncHttpResponseHandler() {

							@Override
							public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
								isGettingVaildCode = false;
								String result = ResultParseUtil.deAesResult(responseBody);
								try {
									JSONObject json = new JSONObject(result);
									String code = json.getString(VpConstants.HttpKey.CODE);
									String message = json.getString(VpConstants.HttpKey.MSG);

									if ("0".equals(code)) {// 返回成功
										// Toast.makeText(LoginActivity.this,
										// message,
										// Toast.LENGTH_LONG).show();
										// mBtnGetVerify.setEnabled(false);
										// startTimer();
									} else {
										resetView();
										Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
									}
								} catch (JSONException e) {
									resetView();
									e.printStackTrace();
								}

							}

							@Override
							public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
								isGettingVaildCode = false;
								resetView();
								Toast.makeText(LoginActivity.this, "网络异常,获取验证码失败", Toast.LENGTH_LONG).show();

							}
						});

			} else {
				Toast.makeText(LoginActivity.this, "请输入正确的手机号", Toast.LENGTH_LONG).show();
			}
		}
	}

	public void resetView() {
		stopTimer();
		mBtnGetVerify.setText("重新发送");
		mBtnGetVerify.setEnabled(true);
		mTvValidCodeTime.setVisibility(View.GONE);
		second = 60;
	}

	private void startTimer() {
		stopTimer();
		timer = new Timer();
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				second--;
				viewhandler.sendEmptyMessage(0);
			}
		};
		timer.schedule(timerTask, 0, 1000);
	}

	private void stopTimer() {
		if (null != timer) {
			timer.cancel();
			timer = null;
			second = 0;
		}
	}

	Handler viewhandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (second <= 0) {
					stopTimer();
					mBtnGetVerify.setText("重新发送");
					mBtnGetVerify.setEnabled(true);
					mTvValidCodeTime.setVisibility(View.GONE);
					second = 60;
				} else {
					// mBtnGetVerify.setText("已发送（"+second+"s）");
					mTvValidCodeTime.setVisibility(View.VISIBLE);
					mTvValidCodeTime.setText("已发送(" + second + "s)");
					mBtnGetVerify.setEnabled(false);
				}
				break;

			case 1:
				break;
			}
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btn_getvalidcode:
			getValidCode();
			break;
		case R.id.login_btn_login:
			// 登录
			mBtnLogin.setEnabled(false);
			login();
			break;
		case R.id.login_services_tv_desc:
			// 用户协议
			turnToUserProtocol();
			break;
		case R.id.login_services_rb_desc:
			mRbServices.setChecked(!mIsCheck);
			mIsCheck = !mIsCheck;
			setLoginButtonEnable();
			break;

		case R.id.login_iv_sina:
			showDialog();
			// 新浪微博登录
			Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
			mThirdLoginUtils.authorize(sina);
			break;
		case R.id.login_iv_wechat:
			if (!PackageHelper.isWeChatInstalled(this)) {
				Toast.makeText(this, "请安装微信客户端", 0).show();
				return;
			}

			showDialog();
			// 微信登录
			Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
			mThirdLoginUtils.authorize(wechat);
			break;
		case R.id.login_iv_qq:
			showDialog();
			// QQ登录
			Platform qq = ShareSDK.getPlatform(QQ.NAME);
			mThirdLoginUtils.authorize(qq);
			break;
		default:
			break;
		}

	}

	public void showDialog() {
		try {
			progressDialog.show();
		} catch (Exception e) {
		}
	}

	public void hiedDialog() {
		try {
			progressDialog.dismiss();
		} catch (Exception e) {
		}
	}

	private void turnToUserProtocol() {
		int versionCode = -1;
		try {
			versionCode = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		InwardAction.parseAction(VpConstants.USER_LOGIN_USER_PROTOTOL + versionCode).toStartActivity(this);
	}

	private void login() {
		new MapLocationNetwork(mHandler, this);
	}

	/**
	 * 登录
	 * 
	 * @param areaCode
	 *            区域编码
	 * @param lat
	 *            登录纬度
	 * @param lng
	 *            登录经度
	 */
	private void login(String areaCode, final float lat, final float lng) {

		String phone = mEtNumber.getText().toString();
		String validCode = mEtValidCode.getText().toString();

		if ((this.mLoginType == LOGINTYPE_PHONE) && (TextUtils.isEmpty(phone) || TextUtils.isEmpty(validCode))) {
			Toast.makeText(this, "账号和验证码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		UserInfo user = new UserInfo();
		user.loginType = this.mLoginType;
		if (this.mLoginType == LOGINTYPE_PHONE) {//如果登陆类型==1  phone  authcode 必填
			user.mt = phone;
			user.authCode = validCode;
		}
		user.areaCode = areaCode;
		user.lat = lat;
		user.lng = lng;
		if (this.mLoginType != LOGINTYPE_PHONE) {
			user.openWebUid = this.mOpenUid;
		}

		if (mClient == null) {
			mClient = new VpHttpClient(LoginActivity.this);
		}

		user.login(new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);

				JSONObject json = null;
				try {
					
					json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {
						String data = json.getString(VpConstants.HttpKey.DATA);
						JSONObject dataobj = new JSONObject(data);
						String uid = dataobj.getString(LoginUserInfoBean.UID);
						String xmppUser = dataobj.getString(LoginUserInfoBean.XMPPUSER);
						String xmppPwd = dataobj.getString(LoginUserInfoBean.XMPPPWD);
						String isFilledInfo = dataobj.getString(LoginUserInfoBean.ISFILLEINFO);// 是否已设置过登陆完善资料1=是，0=不是
						if (uid != null && xmppUser != null && xmppPwd != null) {// 登录成功

							if ("0".equals(isFilledInfo)) {// 跳转到完善资料界面
								Intent intent = new Intent(LoginActivity.this, AddOtherUserInfoActivity.class);
								intent.putExtra(LoginUserInfoBean.LOGIN_UID, uid);
								intent.putExtra(LoginUserInfoBean.XMPPUSER, xmppUser);
								intent.putExtra(LoginUserInfoBean.XMPPPWD, xmppPwd);
								intent.putExtra(LoginUserInfoBean.LAT, lat);
								intent.putExtra(LoginUserInfoBean.LNG, lng);
								if (mLoginType != LOGINTYPE_PHONE) {
									// 第三方登录
									intent.putExtra(LoginUserInfoBean.THIRDNICKNAME, mThirdNickName);
									intent.putExtra(LoginUserInfoBean.THIRDPORTRAIT, mThirdPortrait);
									intent.putExtra(LoginUserInfoBean.LOGINTYPE, mLoginType);
									intent.putExtra(LoginUserInfoBean.THIRDOPENID, mOpenUid);
								}

								startActivity(intent);
								finish();
							} else {
								// 获取用户的基本信息
								LoginUserInfoBean bean = new LoginUserInfoBean(LoginActivity.this);
								bean.saveLoginUserInfo(mLoginType, mOpenUid, uid, xmppUser, xmppPwd, isFilledInfo, lat, lng, mClient,
										new SaveUserInfoCallBack() {

									@Override
									public void onSuccess() {
										finish();

									}

									@Override
									public void onFailed() {
										if (mLoginType == LOGINTYPE_PHONE)
											mBtnLogin.setEnabled(true);

									}
								});
							}
						} else {
							if (mLoginType == LOGINTYPE_PHONE)
								mBtnLogin.setEnabled(true);
							String message = json.getString(VpConstants.HttpKey.MSG);
							Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
						}
					} else {
						if (mLoginType == LOGINTYPE_PHONE)
							mBtnLogin.setEnabled(true);
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e1) {
					if (mLoginType == LOGINTYPE_PHONE)
						mBtnLogin.setEnabled(true);
					e1.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if (mLoginType == LOGINTYPE_PHONE)
					mBtnLogin.setEnabled(true);
				Toast.makeText(LoginActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();

			}
		}, mClient);

	}

	private void setLoginButtonEnable() {
		// 登录按钮默认情况为置灰，在输入手机号码和验证码的情况下，变为可点击状态
		String number = mEtNumber.getText().toString();
		String validCode = mEtValidCode.getText().toString();
		if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(validCode) && mRbServices.isChecked()) {
			mBtnLogin.setEnabled(true);
		} else {
			mBtnLogin.setEnabled(false);

		}
	}
}
