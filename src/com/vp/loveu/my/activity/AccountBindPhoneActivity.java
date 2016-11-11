package com.vp.loveu.my.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.LoginStatus;

import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015-10-23 上午10:50:38
 * @Description: 第三方登录绑定手机
 */
public class AccountBindPhoneActivity extends VpActivity implements OnClickListener{
	protected static final String TAG = "LoginActivity";
	private EditText mEtNumber;
	private EditText mEtValidCode;
	private Button mBtnGetVerify;
	private Button mBtnBind;
	private int second = 60;
	private Timer timer;
	private TextView mTvValidCodeTime;
	
	public static final String BIND_TYPE="bind_type";
	public static final String BIND_PHONE_NUM="bind_phone_num";
	public static final int BIND_SUCCESS_CODE=1;
	public static final int UNBIND_SUCCESS_CODE=2;
	
	private boolean isBind=true;//绑定手机or 取消绑定
	private String mPhoneNumber;
	private boolean isGettingVaildCode;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_account_bind_activity);
		mClient=new VpHttpClient(this);
		isBind=getIntent().getBooleanExtra(BIND_TYPE, true);
		mPhoneNumber=getIntent().getStringExtra(BIND_PHONE_NUM);
		isGettingVaildCode=false;
		initView();

	}
		
	private void initView() {
		initPublicTitle();
		this.mPubTitleView.mBtnLeft.setVisibility(View.GONE);
		if(isBind)
			this.mPubTitleView.mTvTitle.setText("手机绑定");
		else
			this.mPubTitleView.mTvTitle.setText("解除绑定");
		mEtNumber=(EditText) findViewById(R.id.bind_et_number);
		mEtValidCode=(EditText) findViewById(R.id.bind_et_valid_code);
		mBtnGetVerify=(Button) findViewById(R.id.bind_btn_getvalidcode);
		mBtnBind=(Button) findViewById(R.id.bind_btn_login);
		mTvValidCodeTime=(TextView) findViewById(R.id.bind_validcode_tv_timmer);
		mBtnGetVerify.setOnClickListener(this);
		mBtnBind.setOnClickListener(this);
		createEditTextChangeListener(mEtNumber);
		createEditTextChangeListener(mEtValidCode);
		
		if(isBind){
			mBtnBind.setText("绑定");	
		}else{
			mBtnBind.setText("解除绑定");
			String showPhone = mPhoneNumber.substring(0, 3) + "****" + mPhoneNumber.substring(7, mPhoneNumber.length());
			mEtNumber.setText(showPhone);
			mEtNumber.setEnabled(false);
			mBtnGetVerify.setEnabled(true);
		}
		
		
	}
	private void createEditTextChangeListener(final EditText etNumber) {
		etNumber.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//登录按钮默认情况为置灰，在输入手机号码和验证码的情况下，变为可点击状态
				String number=mEtNumber.getText().toString();
				setLoginButtonEnable();
				
				if(etNumber.getId()==R.id.bind_et_number){
					if(isPhoneNumberValid(number)){
						mBtnGetVerify.setEnabled(true);
					}else{
						mBtnGetVerify.setEnabled(false);
					}
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
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
	 * @param v
	 */
	private void getValidCode(){
		String phonevalue;
		if(isBind)
			phonevalue = mEtNumber.getText().toString();
		else
			phonevalue=this.mPhoneNumber;
		if (TextUtils.isEmpty(phonevalue)) {
			Toast.makeText(AccountBindPhoneActivity.this, "请输入手机号", Toast.LENGTH_LONG)
					.show();
		} else if (!TextUtils.isEmpty(phonevalue)) {
			if (isPhoneNumberValid(phonevalue)) {			
				//发送网络请求		
				JSONObject jsonObj=new JSONObject();
				try {
					jsonObj.put("mt", Long.parseLong(phonevalue));
					jsonObj.put("name", "auth_code");
					jsonObj.put("src", 3);//绑定手机号
				} catch (Exception e) {
					Toast.makeText(AccountBindPhoneActivity.this, "请求参数有误", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					return;
				}
				if(isGettingVaildCode)//已经在获取验证码
					return;
				isGettingVaildCode=true;
				mBtnGetVerify.setEnabled(false);
				startTimer();
				mClient.post(AccountBindPhoneActivity.this, VpConstants.USER_SEND_SMS_CODE, new RequestParams(), jsonObj.toString(), true, new AsyncHttpResponseHandler() {
					
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						isGettingVaildCode=false;
						String result=ResultParseUtil.deAesResult(responseBody);
						try {
							JSONObject json  = new JSONObject(result);
							String code = json.getString(VpConstants.HttpKey.CODE);
							String message = json.getString(VpConstants.HttpKey.MSG);

							if ("0".equals(code)) {//返回成功
//								Toast.makeText(AccountBindPhoneActivity.this, message,
//										Toast.LENGTH_LONG).show();
//								mBtnGetVerify.setEnabled(false);
//								startTimer();
							} else {
								mBtnGetVerify.setEnabled(true);
								stopTimer();
								Toast.makeText(AccountBindPhoneActivity.this, message,
										Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							mBtnGetVerify.setEnabled(true);
							stopTimer();
							e.printStackTrace();
						}
						
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						isGettingVaildCode=false;
						mBtnGetVerify.setEnabled(true);
						stopTimer();
						Toast.makeText(AccountBindPhoneActivity.this, "网络异常,获取验证码失败",
								Toast.LENGTH_LONG).show();
						
					}
				});
				
			} else {
				Toast.makeText(AccountBindPhoneActivity.this, "请输入正确的手机号",
						Toast.LENGTH_LONG).show();
			}
		}
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
//					mBtnGetVerify.setText("已发送（"+second+"s）");
					mTvValidCodeTime.setVisibility(View.VISIBLE);
					mTvValidCodeTime.setText("已发送("+second+"s)");
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
		case R.id.bind_btn_getvalidcode:
			getValidCode();
			break;
		case R.id.bind_btn_login:
			//登录
			bind();
			break;

		default:
			break;
		}
	}
	

	/**
	 * 绑定号码or解除绑定
	 */
	private void bind(){
		String phone;
		if(isBind)
			phone = mEtNumber.getText().toString();
		else
			phone=this.mPhoneNumber;
		String validCode=mEtValidCode.getText().toString();		
		if((TextUtils.isEmpty(phone) || TextUtils.isEmpty(validCode))){
			Toast.makeText(this, "号码和验证码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		String url=null;
		if(isBind)
			url=VpConstants.USER_BINDING;
		else
			url=VpConstants.USER_UNBINDING;
				
		//发送网络请求		
		JSONObject jsonObj=new JSONObject();
		try {
			LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
			if (loginInfo == null) {
				return;
			}
			jsonObj.put("uid", loginInfo.getUid());
			jsonObj.put("login_type", LoginUserInfoBean.LOGINTYPE_PHONE);
			if(isBind){
				jsonObj.put("mt", phone);				
				jsonObj.put("auth_code", validCode);
			}
		} catch (Exception e) {
			Toast.makeText(this, "请求参数有误", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		}
		final String tempPhone=phone;
		mClient.post(url, new RequestParams(), jsonObj.toString(), true, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result=ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json  = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					String message=null;
					if ("0".equals(code)) {//返回成功
						if(isBind){
							message="绑定成功";
							Intent data=new Intent();
							data.putExtra(AccountBindActivity.BIND_PHONE, tempPhone);
							setResult(BIND_SUCCESS_CODE, data);							
						}else{
							message="解除绑定成功";
							setResult(UNBIND_SUCCESS_CODE);
						}
						Toast.makeText(AccountBindPhoneActivity.this, message,
								Toast.LENGTH_LONG).show();
						finish();
					} else {
						 message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(AccountBindPhoneActivity.this, message,
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				System.out.println(statusCode);
				
			}
		});
		
	
	}



	
	private void setLoginButtonEnable(){
		//登录按钮默认情况为置灰，在输入手机号码和验证码的情况下，变为可点击状态
		String number=mEtNumber.getText().toString();
		String validCode=mEtValidCode.getText().toString();
		if(!TextUtils.isEmpty(number) && !TextUtils.isEmpty(validCode)){
			mBtnBind.setEnabled(true);
		}else{
			mBtnBind.setEnabled(false);
			
		}
	}
}
