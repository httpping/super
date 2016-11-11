package com.vp.loveu.pay.ui;

import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alipay.sdk.app.PayTask;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
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
import com.vp.loveu.message.bean.PushNoticeBean;
import com.vp.loveu.message.utils.DensityUtil;
import com.vp.loveu.pay.bean.EnjoyPayBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.ToastUtil;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.wxapi.WxConsants;

import cz.msebera.android.httpclient.Header;

public class EnjoyPayActivity extends VpActivity implements OnClickListener,OnCheckedChangeListener {
	public static final String PAY_PARAMS = "pay_params"; // 请求参数
	
	
	
	private RelativeLayout wxPay;
	private ToggleButton wxPayToggle;
	private RelativeLayout alipay;
	private ToggleButton alipayToggle;
	private Button goPay;
	private SharedPreferencesHelper sp;
	ImageView goBackImageView;
	ToggleButton firstButton;
	ToggleButton secondButton;
	ToggleButton threeButton;
	
	TextView mUserNameTextView;
	ImageView mHeadImageView;
	
	EditText enjoyPriceEditText;
	

	private IWXAPI api;// 微信支付接口
	private boolean isPayActive;//是否是活动报名进来的
	private TextView remindTextView;
	private JSONObject mRequestBean;
	


	private String mUserName;
	private String mUserAvatar;

	//选项价格
	double select_prices[] ={2,10,50};
	

	private EnjoyPayBean mBindViewBean;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		
		setContentView(R.layout.pay_enjoy_activity);
		
		mBindViewBean = (EnjoyPayBean) getIntent().getSerializableExtra(
				PAY_PARAMS);
		
		try {
			mRequestBean = mBindViewBean.getParams();
			mUserName = mRequestBean.getString("username");
			mUserAvatar = mRequestBean.getString("user_avatar");
			mRequestBean.remove("username");
			mRequestBean.remove("user_avatar");
		} catch (Exception e) {
			ToastUtil.showToast(this, "参数不正确", 0);
			finish();
		}
		
		api = WXAPIFactory.createWXAPI(this, WxConsants.APP_ID);
		api.registerApp(WxConsants.APP_ID);
		sp = SharedPreferencesHelper.getInstance(this);
		initView();
	}
	
	private void initView() {
	 
		goBackImageView = (ImageView) findViewById(R.id.btn_close);
		goBackImageView.setOnClickListener(this);
		firstButton = (ToggleButton) findViewById(R.id.first_price);
		secondButton = (ToggleButton) findViewById(R.id.second_price);
		threeButton  = (ToggleButton) findViewById(R.id.three_price);
		
		firstButton.setOnCheckedChangeListener(this);
		secondButton.setOnCheckedChangeListener(this);
		threeButton.setOnCheckedChangeListener(this);
		
		wxPay = (RelativeLayout) findViewById(R.id.wx_pay);
		wxPayToggle = (ToggleButton) findViewById(R.id.wx_pay_toggle);
		alipay = (RelativeLayout) findViewById(R.id.alipay);
		alipayToggle = (ToggleButton) findViewById(R.id.alipay_toggle);
		goPay = (Button) findViewById(R.id.go_pay);
		remindTextView = (TextView) findViewById(R.id.remind_text);
		
		enjoyPriceEditText =(EditText) findViewById(R.id.edit_enjoy_price);
		
		mUserNameTextView = (TextView) findViewById(R.id.enjoy_username);
		mHeadImageView = (ImageView) findViewById(R.id.enjoy_user_head_img);
		
		mUserNameTextView.setText(mUserName);
		
		
		
		wxPay.setOnClickListener(this);
		alipay.setOnClickListener(this);
		wxPayToggle.setOnClickListener(this);
		alipayToggle.setOnClickListener(this);

		goPay.setOnClickListener(this);
		
		
		//头像
		DisplayImageOptions option = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_portrait) // resource
				// or
		.showImageForEmptyUri(R.drawable.default_portrait) // resource
		// or
		.showImageOnFail(R.drawable.default_portrait) // resource or
		.resetViewBeforeLoading(false) // default
		.delayBeforeLoading(50).cacheInMemory(true) // default
		.cacheOnDisk(true) // default
		.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(false) // default
		.displayer(new RoundedBitmapDisplayer(DensityUtil.dip2px(this, 50))).build();

		ImageLoader.getInstance().displayImage(mUserAvatar, mHeadImageView, option);
		
		

	}

	@Override
	public void onClick(View v) {
		
		if (v.equals(goBackImageView)) {
			finish();
			return;
		}
		
		if (v.equals(wxPay) || v.equals(wxPayToggle)) {
			wxPayToggle.setChecked(true);
			alipayToggle.setChecked(false);
		} else if (v.equals(alipay) || v.equals(alipayToggle)) {
			wxPayToggle.setChecked(false);
			alipayToggle.setChecked(true);
		} else{
			  if (v.equals(goPay)) {
				toPay();
			  }
		}
	
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		if (isChecked == false) {
			return;
		}
		if (buttonView.equals(firstButton)) {
			secondButton.setChecked(false);
			threeButton.setChecked(false);
		}else if (buttonView.getId() == secondButton.getId()) {
			firstButton.setChecked(false);
			threeButton.setChecked(false);
		}else if (buttonView.getId() == threeButton.getId()) {
			firstButton.setChecked(false);
			secondButton.setChecked(false);
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
		
		
		
		if (!TextUtils.isEmpty(enjoyPriceEditText.getText())) {
			try {
				mBindViewBean.payMoney = Double.parseDouble(enjoyPriceEditText.getText().toString());
				//mBindViewBean.payMoney = ((int)mBindViewBean.payMoney*100)/100.0;//去掉后面2位
			} catch (Exception e) {
				ToastUtil.showToast(this, "支付金额不对", 0);
			}
		}else {
			if (firstButton.isChecked()) {
				mBindViewBean.payMoney =select_prices[0];
			}else if (secondButton.isChecked()) {
				mBindViewBean.payMoney = select_prices[1];
			}else if (threeButton.isChecked()) {
				mBindViewBean.payMoney = select_prices[2];
			}else {
				ToastUtil.showToast(this, "您确定打赏金额", 0);

			}
			
		}
		

		JSONObject json = mBindViewBean.getParams();
		try {
			json.put("uid", mine.getUid());
			
			 
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
		String url = VpConstants.ENJOY_PAY_URL;
		 
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
							ToastUtil.showToast(EnjoyPayActivity.this, "请求失败", 0);
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
			PayTask alipay = new PayTask(EnjoyPayActivity.this);
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
			VpApplication.getInstance().mEnjoyPayBindBean = mBindViewBean;
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
				ToastUtil.showToast(EnjoyPayActivity.this, message+"", 0);
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
			VpApplication.getInstance().payresult = true;
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
					//baseBean.showMsgToastInfo();
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				///服务器访问失败。。没办法发没网络无法
				//Toast.makeText(EnjoyPayActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	protected void onResume() {
		super.onResume();
		
		//微信支付成功回来了
		if (VpApplication.getInstance().mEnjoyPayBindBean!=null && VpApplication.getInstance().mEnjoyPayBindBean.isWxPay && VpApplication.getInstance().mEnjoyPayBindBean.payResult) {
			VPLog.d(tag, "wx pay success ");
			VpApplication.getInstance().mEnjoyPayBindBean = null;
			mHandler.sendEmptyMessage(200);//支付完成了
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		VpApplication.getInstance().mEnjoyPayBindBean = null;
	}
}
