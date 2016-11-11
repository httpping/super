package com.vp.loveu.index.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.bean.ActiveVipBean;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.pay.ui.ActivityPaySuccessActivity;
import com.vp.loveu.util.LoginStatus;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * 免费活动报名
 * @author tanping 2015-11-23
 */
public class FreeApplayActivity extends VpActivity implements OnClickListener {

	public static final String PAY_PARAMS = "pay_params"; // 请求参数

	private EditText phoneNumber;
	private EditText authCode;
	private Button btnSendAuthCode;
	private Button goPay;
	ImageView deletePhoneImageView;
	TextView remindTextView;
	LinearLayout mPayContentLayout;

	String phone;
	String auth;
	boolean isGettingVaildCode;
	Timer timer;
	int second = 60;

	public ActiveVipBean mBindViewBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.free_pay_activity);
		initPublicTitle();
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mBindViewBean = (ActiveVipBean) getIntent().getSerializableExtra(PAY_PARAMS);

		initView();
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mBtnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		phoneNumber = (EditText) findViewById(R.id.phone_number);
		goPay = (Button) findViewById(R.id.go_pay);
		mPayContentLayout = (LinearLayout) findViewById(R.id.pay_content);
		remindTextView = (TextView) findViewById(R.id.remind_text);
		deletePhoneImageView = (ImageView) findViewById(R.id.delete_phone_num);
		btnSendAuthCode = (Button) findViewById(R.id.send_auth_code);
		authCode = (EditText) findViewById(R.id.edit_auth_code);

		goPay.setOnClickListener(this);
		btnSendAuthCode.setOnClickListener(this);

		if (mBindViewBean != null) {
			mPubTitleView.mTvTitle.setText(mBindViewBean.payTitle);
			View showView = mBindViewBean.createShowView(this);
			if (showView != null) {
				mPayContentLayout.addView(showView, 0);// 最上面
			}
		}

		phoneNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() == 0) {
					deletePhoneImageView.setVisibility(View.INVISIBLE);
				} else {
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

	@Override
	public void onClick(View v) {
		phone = phoneNumber.getText().toString().trim();
		auth = authCode.getText().toString().trim();

		if (!isPhoneNumberValid(phone)) {
			Toast.makeText(this, "请正确填写手机号码", Toast.LENGTH_SHORT).show();
			return;
		}

		switch (v.getId()) {
		case R.id.send_auth_code:
			// 发送验证码
			sendAuthCode();
			break;

		case R.id.go_pay:
			if (TextUtils.isEmpty(auth)) {
				Toast.makeText(this, "验证码,不能为空", Toast.LENGTH_SHORT).show();
				break;
			}

			toPay();

			break;
		default:
			break;
		}
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
					btnSendAuthCode.setEnabled(false);
					btnSendAuthCode.setText("已发送(" + second + "s)");
				}
				break;

			default:
				break;
			}
		};
	};

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
						Toast.makeText(FreeApplayActivity.this, "验证码发送成功", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(FreeApplayActivity.this, "验证码发送失败,稍后请重试", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				isGettingVaildCode = false;
				Toast.makeText(FreeApplayActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
			}
		});
	}

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
	 * 去报名
	 */
	public void toPay() {
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		mClient.setShowProgressDialog(true);
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		int uid = 5;
		if (loginInfo != null) {
			uid = loginInfo.getUid();
		}

		JsonObject obj = new JsonObject();
		obj.addProperty("uid", uid);
		obj.addProperty("mt", phone);
		obj.addProperty("id", mBindViewBean.getBean().id);
		obj.addProperty("price", 0);
		obj.addProperty("pay_type", 0);
		obj.addProperty("auth_code", auth);

		mClient.post(VpConstants.ACTIVITY_PAY, new RequestParams(), obj.toString(), new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String deAesResult = ResultParseUtil.deAesResult(responseBody);
				JSONObject obj = null;
				try {
					obj = new JSONObject(deAesResult);
					int state = obj.optInt("code");
					if (state == 0) {
						// 报名成功
						Intent intent = new Intent(FreeApplayActivity.this, ActivityPaySuccessActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(getApplicationContext(), obj.optString("msg"), Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
