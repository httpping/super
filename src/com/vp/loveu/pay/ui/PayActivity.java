package com.vp.loveu.pay.ui;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.sdk.app.PayTask;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.bean.AppconfigBean;
import com.vp.loveu.bean.NetBaseBean;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.pay.bean.PayBindViewBean;
import com.vp.loveu.pay.bean.PayBindViewBean.PayType;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.ToastUtil;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.wxapi.WxConsants;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cz.msebera.android.httpclient.Header;

/**
 * 支付界面 ，所有的支付都调用这个页面，
 * 
 * @author tanping 2015-11-23
 */
public class PayActivity extends VpActivity implements OnClickListener {

	public static final String PAY_PARAMS = "pay_params"; // 请求参数

	private EditText phoneNumber;
	private RelativeLayout wxPay;
	private ToggleButton wxPayToggle;
	private RelativeLayout alipay;
	private ToggleButton alipayToggle;
	private Button goPay;
	
	ImageView deletePhoneImageView;
	
	TextView remindTextView;
	
	TextView phoneTitle;
	RelativeLayout phoneRLayout;
	private SharedPreferencesHelper sp;
	LinearLayout mPayContentLayout;
	RelativeLayout mAuthCodeContainer;//验证码容器
	private EditText authCode;
	private Button btnSendAuthCode;

	public PayBindViewBean mBindViewBean;//

	private IWXAPI api;// 微信支付接口
	private boolean isPayActive;//是否是活动报名进来的

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_activity);
		initPublicTitle();
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);

		mBindViewBean = (PayBindViewBean) getIntent().getSerializableExtra(
				PAY_PARAMS);

		api = WXAPIFactory.createWXAPI(this, WxConsants.APP_ID);
		api.registerApp(WxConsants.APP_ID);
		sp = SharedPreferencesHelper.getInstance(this);
		isPayActive = mBindViewBean.payType == PayType.activity;
		initView();
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mBtnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_OK);
				finish();
			}
		});
		
		phoneNumber = (EditText) findViewById(R.id.phone_number);
		wxPay = (RelativeLayout) findViewById(R.id.wx_pay);
		wxPayToggle = (ToggleButton) findViewById(R.id.wx_pay_toggle);
		alipay = (RelativeLayout) findViewById(R.id.alipay);
		alipayToggle = (ToggleButton) findViewById(R.id.alipay_toggle);
		goPay = (Button) findViewById(R.id.go_pay);
		deletePhoneImageView = (ImageView) findViewById(R.id.delete_phone_num);
		mPayContentLayout = (LinearLayout) findViewById(R.id.pay_content);
		phoneTitle = (TextView) findViewById(R.id.show_phone_title);
		phoneRLayout = (RelativeLayout) findViewById(R.id.show_phone_content);
		mAuthCodeContainer = (RelativeLayout) findViewById(R.id.pay_autocode);
		remindTextView = (TextView) findViewById(R.id.remind_text);
		btnSendAuthCode = (Button) findViewById(R.id.send_auth_code);
		authCode = (EditText) findViewById(R.id.edit_auth_code);
		
		//如果是活动进来的显示
		mAuthCodeContainer.setVisibility(isPayActive ? View.VISIBLE : View.GONE);
		
		btnSendAuthCode.setOnClickListener(this);
		wxPay.setOnClickListener(this);
		alipay.setOnClickListener(this);
		wxPayToggle.setOnClickListener(this);
		alipayToggle.setOnClickListener(this);

		goPay.setOnClickListener(this);
		
		if (mBindViewBean != null) {
			mPubTitleView.mTvTitle.setText(mBindViewBean.payTitle);
			View showView = mBindViewBean.createShowView(this);
			if (showView != null) {
				mPayContentLayout.addView(showView, 0);// 最上面
			}
			if (!mBindViewBean.isRequestPhone) {
				phoneRLayout.setVisibility(View.GONE);
				phoneTitle.setVisibility(View.GONE);
			}else {
				phoneNumber.addTextChangedListener(new TextWatcher() {
					
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						
					}
					
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						
					}
					
					@Override
					public void afterTextChanged(Editable s) {
						if (s.toString().length() ==0) {
							deletePhoneImageView.setVisibility(View.INVISIBLE);
						}else {
							deletePhoneImageView.setVisibility(View.VISIBLE);
							if(isPhoneNumberValid(s.toString())){
								btnSendAuthCode.setTextColor(Color.parseColor("#10BB7D"));
								btnSendAuthCode.setBackgroundResource(R.drawable.send_promocode_btn_shape2);//如果是手机的话  可以点击
							}
						}
					}
				});
				deletePhoneImageView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						phoneNumber.setText(null);
					}
				});
			}
			goPay.setText("确认支付：" + mBindViewBean.payMoney + "元");
			if (mBindViewBean.isWxPay) {
				onClick(wxPay);
			}
			//mBindViewBean.payType = PayType.flee_help;
			if (mBindViewBean.payType == PayType.flee_help) {//情感求助
				remindTextView.setVisibility(View.VISIBLE);
				AppconfigBean coAppconfigBean = VpApplication.getInstance().getAppInfoBean().get("001003");
				if (coAppconfigBean!=null) {
					remindTextView.setText(coAppconfigBean.getVal());
				}
			}
		}
		
		/*ImageView imageView = new ImageView(this);
		imageView.setImageBitmap(CreateQrImage.create("你好你好你好你好你好你好你好你好你好你好你好你好你好你好",300,400));
		mPayContentLayout.addView(imageView);*/
		

	}

	String phone; //手机号码
	String auth; //验证码
	boolean isGettingVaildCode;
	
	@Override
	public void onClick(View v) {
		phone = phoneNumber.getText().toString().trim();
		auth = authCode.getText().toString().trim();
		if (v.equals(wxPay) || v.equals(wxPayToggle)) {
			wxPayToggle.setChecked(true);
			alipayToggle.setChecked(false);
		} else if (v.equals(alipay) || v.equals(alipayToggle)) {
			wxPayToggle.setChecked(false);
			alipayToggle.setChecked(true);
		} else{
			if (!isPhoneNumberValid(phone) && mBindViewBean.isRequestPhone) {
				Toast.makeText(this, "请正确填写手机号码", Toast.LENGTH_SHORT).show();
			}else if(v.equals(btnSendAuthCode) && isPayActive){
				sendAuthCode();
			}else if (TextUtils.isEmpty(auth) && isPayActive) {
				Toast.makeText(this, "验证码,不能为空", Toast.LENGTH_SHORT).show();
			}else if (v.equals(goPay)) {
				toPay();
			}
		}
	}
	
	/**
	 * 发送验证码 void
	 */
	private void sendAuthCode() {
		if (!isPhoneNumberValid(phone)) {
			Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_LONG).show();
			return;
		}

		if (isGettingVaildCode) {
			return;// 已经在获取验证码
		}
		
		btnSendAuthCode.setTextColor(Color.parseColor("#999999"));
		btnSendAuthCode.setBackgroundResource(R.drawable.send_promocode_btn_shape);

		isGettingVaildCode = true; // 60秒后恢复false
		btnSendAuthCode.setEnabled(false);
		startTimer();
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		mClient.setShowProgressDialog(false);
		// 发送网络请求
		JsonObject obj = new JsonObject();
		obj.addProperty("mt", phone);
		obj.addProperty("name", "auth_code");
		obj.addProperty("src", 6);
		mClient.post(this, VpConstants.USER_SEND_SMS_CODE, new RequestParams(), obj.toString(), true, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				isGettingVaildCode = false;
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					String message = json.getString(VpConstants.HttpKey.MSG);
					if ("0".equals(code)) {// 返回成功
						Toast.makeText(PayActivity.this, "验证码发送成功", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(PayActivity.this, "验证码发送失败,稍后请重试", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				isGettingVaildCode = false;
				Toast.makeText(PayActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (second <= 0) {
					stopTimer();
					btnSendAuthCode.setTextColor(Color.parseColor("#10BB7D"));
					btnSendAuthCode.setBackgroundResource(R.drawable.send_promocode_btn_shape2);
					btnSendAuthCode.setText("重新发送");
					btnSendAuthCode.setEnabled(true);
					second = 60;
				} else {
					btnSendAuthCode.setTextColor(Color.parseColor("#999999"));
					btnSendAuthCode.setBackgroundResource(R.drawable.send_promocode_btn_shape);
					btnSendAuthCode.setText("已发送(" + second + "s)");
					btnSendAuthCode.setEnabled(false);
				}
				break;

			default:
				break;
			}
		};
	};
	
	Timer timer;
	int second = 60;
	private void startTimer() {
		stopTimer();
		timer = new Timer();
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				second--;
				handler.sendEmptyMessage(0);
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
	

	/**
	 * 去支付
	 */
	public void toPay() {

		/*Intent intent = new Intent(this, ActivityPaySuccessActivity.class);
		startActivity(intent);*/

		LoginUserInfoBean mine = LoginStatus.getLoginInfo();

		if (mine == null) {
			return;
		}

		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}

		JSONObject json = mBindViewBean.getParams();
		try {
			json.put("uid", mine.getUid());
			if (mBindViewBean.isRequestPhone) {
				if (phoneNumber.getText().length() != 11) {
					ToastUtil.showToast(this, "手机号码请认真填写", 0);
					return ;
				}else if(!isPhoneNumberValid(phone)){
					ToastUtil.showToast(this, "手机号码请认真填写", 0);
					return;
				}else if(TextUtils.isEmpty(auth) && isPayActive){
					ToastUtil.showToast(this, "验证码请认真填写", 0);
					return;
				}
				json.put("mt", phoneNumber.getText().toString());
			}
			//json.put("id", 7);
			
			if (isPayActive) {
				//活动支付的话 验证码必须传过去
				json.put("auth_code", auth);
			}
			
			json.put("price", mBindViewBean.payMoney);
			if (alipayToggle.isChecked()) {
				json.put("pay_type", 1);
			} else {
				json.put("pay_type", 2);
			}
		} catch (Exception e) {
			ToastUtil.showToast(this, "参数传人不正确", 0);
			e.printStackTrace();
			return;
		}
		
		//mBindViewBean.payType = PayType.activity;
		String url ="";
		//不同的支付类型
		if (mBindViewBean.payType == PayType.activity){
			url = VpConstants.ACTIVITY_PAY;
		}else if (mBindViewBean.payType == PayType.flee_help) {
			url = VpConstants.HELP_APPLY;
		}else if (mBindViewBean.payType == PayType.classroom_pay) {
			url = VpConstants.CLASSROOM_APPLY;
		}
		mClient.setShowProgressDialog(true);
		mClient.post(url, new RequestParams(),
				json.toString(), new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						String result = ResultParseUtil
								.deAesResult(responseBody);
						VPLog.d(tag, result + "");
						try {
							JSONObject jsonObject = new JSONObject(result);
							NetBaseBean baseBean = NetBaseBean.parseJson(result);
							if (!baseBean.isSuccess()) {//判断是否成功
								baseBean.showMsgToastInfo();
								return;
							}
							JSONObject dataJsonObject = new JSONObject(baseBean.data) ;
							String req_str = (String) dataJsonObject
									.get("req_str");
							VPLog.d(tag, "" + dataJsonObject);
							
							mBindViewBean.orderId = dataJsonObject.getString("order_id");
							mBindViewBean.pay_platfrom = dataJsonObject.getInt("pay_type");
							if (alipayToggle.isChecked()) {// alipay
								parseWapResult(req_str);
							}else {
								parseWxResult(new JSONObject(req_str));
							}
						} catch (Exception e) {
							ToastUtil.showToast(PayActivity.this, "请求失败", 0);
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						VPLog.d(tag, error + "");
						try {
							new NetBaseBean().showMsgToastInfo();
							String result = ResultParseUtil
									.deAesResult(responseBody);
							VPLog.d(tag, result + "");
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});

	}

	/**
	 * 服务器返回支付宝参数
	 * 
	 * @param result
	 */
	private void parseWapResult(String params) {
		new AliPayTask().execute(params);
	}

	/**
	 * 支付宝支付
	 */
	private class AliPayTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... arg0) {
			PayTask alipay = new PayTask(PayActivity.this);
			// 调用支付宝并传参,获得返回字符串
			String str = alipay.pay(arg0[0]);
			VPLog.d(tag, "str:" + str);
			String tradeStatus = "resultStatus={";
			int imemoStart = str.indexOf("resultStatus=");
			imemoStart += tradeStatus.length();
			int imemoEnd = str.indexOf("};memo=");
			tradeStatus = str.substring(imemoStart, imemoEnd);

			int resultEnd = str.indexOf("};result=");
			imemoEnd += "};memo=".length();
			String errMag = str.substring(imemoEnd, resultEnd).replace("{", "");

			Message msg = new Message();
			if ("9000".equals(tradeStatus)) {
				msg.what = 100;
			} else {
				msg.what = -100;
				msg.obj = errMag;
			}

			mHandler.sendMessage(msg);
			return null;
		}

	}

	/**
	 * 服务器返回微信参数
	 * 
	 * @param result
	 */
	private void parseWxResult(JSONObject dataObj) {

		if (!api.isWXAppInstalled()) {
			ToastUtil.showToast(this, "请先下载并安装微信", Toast.LENGTH_SHORT);
			return;
		}

		// 微信支付
		boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
		if (isPaySupported) {
			try {
				PayReq req = new PayReq();
				req.appId = dataObj.getString("appid");
				req.partnerId = dataObj.getString("partnerid");
				req.prepayId = dataObj.getString("prepayid");
				req.nonceStr = dataObj.getString("noncestr");
				req.timeStamp = dataObj.getString("timestamp");
				req.packageValue = dataObj.getString("package");
				req.sign = dataObj.getString("sign");

				VPLog.i("999", "微信请求支付参数 = " + "appId=" + req.appId + ","
						+ "partnerId=" + req.partnerId + "," + "prepayId="
						+ req.prepayId + "," + "nonceStr=" + req.nonceStr + ","
						+ "timeStamp=" + req.timeStamp + "," + "packageValue="
						+ req.packageValue + "," + "sign=" + req.sign);

				// 调用微信支付接口
				new WeixinTask().execute(req);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			ToastUtil.showToast(this, "请先下载最新版的微信", Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 微信支付
	 */
	private class WeixinTask extends AsyncTask<Object, Void, Void> {

		@Override
		protected Void doInBackground(Object... arg0) {
			VpApplication.getInstance().mPayBindViewBean = mBindViewBean;
			PayReq req = (PayReq) arg0[0];
			// 回调结果在.wxapi.WXPayEntryActivity
			api.sendReq(req);
			return null;
		}
	}

	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				/*
				 * AlertDialog.Builder builder = new
				 * AlertDialog.Builder(mEGActivity); builder.setTitle("错误提示");
				 * builder.setMessage("网络连接失败,请重试!");
				 * builder.setNegativeButton("确定", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * public void onClick(DialogInterface dialog, int which) {
				 * dialog.dismiss(); } }); builder.create().show();
				 */
				//break;

			case -100:
				// 支付宝支付失败
				String message = msg.obj.toString();
				VPLog.d(tag, "" + message);
				ToastUtil.showToast(PayActivity.this, message+"", 0);
				payResult(-1);
				break;
			case 100:
			case 200:
				// 结果
				VPLog.d(tag, "支付成功了");
				payResult(1);
				// TODO 卡5秒再查询
				/*
				 * mEGHttp.startProgressDialog();
				 * timeHandler.postDelayed(runnable, 5000);// 打开定时器，执行操作
				 */break;
			case 1001:
				// ToastHelper.showInfoToast(ChoosePaymentActivity.this,
				// "暂不支持该支付方式", 1000);
				break;
			default:
				break;
			}
		}

		

	};

	
	private void payResult(int i) {
		//ToastUtil.showToast(this, "支付结果"+i, 0);
		if (i >0) {
			Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
			payNotify();
			if (mBindViewBean.payType == PayType.activity) {
				Intent intent = new Intent(this,ActivityPaySuccessActivity.class);
				startActivity(intent);
			}
			VpApplication.getInstance().payresult = true;
			//sp.putBooleanValue("payResult", true);
			finish();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Intent intent = new Intent();
		intent.putExtra("result", VpApplication.getInstance().payresult);
		setResult(Activity.RESULT_OK, intent);
	}
	
	/**
	 * 通知服务器
	 */
	public void payNotify(){
		LoginUserInfoBean mine = LoginStatus.getLoginInfo();

		if (mine == null) {
			return;
		}
		String url = VpConstants.PAY_SUCCESS_NOTIFY;
		JSONObject data = new JSONObject();
		
		try {
			data.put("order_id", mBindViewBean.orderId);
			data.put("category", mBindViewBean.payType.getValue());
			data.put("status", 1);
			data.put("uid", mine.getUid());
			data.put("pay_type", mBindViewBean.pay_platfrom);
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		mClient.setShowProgressDialog(false);
		mClient.post(this, url, new RequestParams(), data.toString(), true, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				 String reString =	ResultParseUtil.deAesResult(responseBody);
				 NetBaseBean baseBean = NetBaseBean.parseJson(reString);
				 if (baseBean.isSuccess()) {
					 //成功
				 }else {
					baseBean.showMsgToastInfo();
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				///服务器访问失败。。没办法发没网络无法
				Toast.makeText(PayActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	protected void onResume() {
		super.onResume();
		
		//微信支付成功回来了
		if (VpApplication.getInstance().mPayBindViewBean!=null && VpApplication.getInstance().mPayBindViewBean.isWxPay && VpApplication.getInstance().mPayBindViewBean.payResult) {
			VPLog.d(tag, "wx pay success ");
			VpApplication.getInstance().mPayBindViewBean = null;
			mHandler.sendEmptyMessage(200);//支付完成了
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		VpApplication.getInstance().mPayBindViewBean = null;
	}
}
