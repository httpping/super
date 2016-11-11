package com.vp.loveu.my.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.login.bean.ThirdAppUserBean;
import com.vp.loveu.login.bean.WechatUserBean;
import com.vp.loveu.login.utils.ThirdLoginUtils;
import com.vp.loveu.my.widget.AccountBindItemRelativeLayout;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.widget.IOSActionSheetDialog;
import com.vp.loveu.widget.IOSActionSheetDialog.OnSheetItemClickListener;
import com.vp.loveu.widget.IOSActionSheetDialog.SheetItemColor;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月1日下午12:04:50
 * @功能 账号绑定
 * @作者 mi
 */

public class AccountBindActivity extends VpActivity {

	private LinearLayout layout;
	public static final String[] names = new String[] { "手机号", "QQ账号", "微信账号", "微博账号" };
	public static final int[] icons = new int[] { R.drawable.icon_phonenumber, R.drawable.icon_qq,
			R.drawable.icon_weixin, R.drawable.icon_sina };
	private int mCurrentClickPosition;
	private static final int REQUEST_BIND_PHONE = 100;//绑定
	private static final int REQUEST_UNBIND_PHONE = 101;//解除绑定
	public static final String BIND_PHONE = "bind_phone";
	//第三方登录
	private ThirdLoginUtils mThirdLoginUtils;
	private String mOpenUid; 
	public static final int LOGINTYPE_PHONE=1;
	public static final int LOGINTYPE_SINA=2;
	public static final int LOGINTYPE_QQ=3;
	public static final int LOGINTYPE_WECHAT=4;
	private int mLoginType=-1;//登陆类型 1=手机号登陆,2=新浪微博登陆，3=QQ登陆,4=微信登录
	private WechatUserBean mWechatBean;
	private String mPhoneNumber;
	
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {			
			case ThirdLoginUtils.AUTHORIZE_COMPLETE:
				// 授权成功
				Object[] objs = (Object[]) msg.obj;
				String platform = (String) objs[0];
				if(SinaWeibo.NAME.equals(platform) || QQ.NAME.equals(platform)){
					ThirdAppUserBean sinaBean=(ThirdAppUserBean)(objs[1]);
					mOpenUid=sinaBean.getOpenid();
					if(SinaWeibo.NAME.equals(platform)){
						mLoginType=LOGINTYPE_SINA;
					}else{
						mLoginType=LOGINTYPE_QQ;
					}
					bind(mLoginType, mOpenUid);
				}
				break;
			case ThirdLoginUtils.AUTHORIZE_CANCEL:
				// 取消授权
				Toast.makeText(AccountBindActivity.this, "取消授权",
						Toast.LENGTH_SHORT).show();
				break;
			case ThirdLoginUtils.AUTHORIZE_ERROR:
				// 授权失败
				Toast.makeText(AccountBindActivity.this, "授权失败",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_activity);
		mClient = new VpHttpClient(this);
		mThirdLoginUtils=new ThirdLoginUtils(mHandler,this);
		initPublicTitle();
		initView();
		initData();
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mBtnLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mPubTitleView.mTvTitle.setText("账号绑定");
		layout = (LinearLayout) findViewById(R.id.account_container);

		for (int i = 0; i < names.length; i++) {
			final AccountBindItemRelativeLayout item = new AccountBindItemRelativeLayout(this);
			item.mTvName.setText(names[i]);
			item.mIvIcon.setImageResource(icons[i]);
			if (i == names.length - 1) {
				item.setIsShowLine(false);
			}
			layout.addView(item);
			// 绑定or接触绑定
			final int tempI = i;
			item.mTvBind.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mCurrentClickPosition = tempI;
					if ("手机号".equals(names[mCurrentClickPosition])) {
						if ("绑定".equals(item.mTvBind.getText().toString())) {							
							if (LoginStatus.getLoginInfo().getLoginType() != LoginUserInfoBean.LOGINTYPE_PHONE
									&& TextUtils.isEmpty(item.mTvState.getText().toString())) {
								// 第三方登录，去绑定手机
								startActivityForResult(new Intent(AccountBindActivity.this, AccountBindPhoneActivity.class),
										REQUEST_BIND_PHONE);
							}
						}else{
							//若是手机登录，不能解绑手机
							if (LoginStatus.getLoginInfo().getLoginType() != LoginUserInfoBean.LOGINTYPE_PHONE){
								//解绑手机
								unBindOperation(LOGINTYPE_PHONE);
							}
						}
					} else if ("QQ账号".equals(names[mCurrentClickPosition])) {
						if ("绑定".equals(item.mTvBind.getText().toString())) {
							//QQ登录授权
							Platform qq=ShareSDK.getPlatform(QQ.NAME);
							mThirdLoginUtils.authorize(qq);
						} else {
							// 去解绑
							unBindOperation(LOGINTYPE_QQ);
						}
					} else if ("微信账号".equals(names[mCurrentClickPosition])) {
						if ("绑定".equals(item.mTvBind.getText().toString())) {
							//微信登录
							Platform wechat=ShareSDK.getPlatform(Wechat.NAME);
							mThirdLoginUtils.authorize(wechat);
						}else{
							// 去解绑

							unBindOperation(LOGINTYPE_WECHAT);
						}
					} else if ("微博账号".equals(names[mCurrentClickPosition])) {
						if ("绑定".equals(item.mTvBind.getText().toString())) {
							//微博登录授权
							Platform sina=ShareSDK.getPlatform(SinaWeibo.NAME);
							mThirdLoginUtils.authorize(sina);
						} else {
							// 去解绑
							unBindOperation(LOGINTYPE_SINA);
						}
					}

				}
			});
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if(VpApplication.getInstance().getWechatUserBean()!=null){
			mWechatBean = VpApplication.getInstance().getWechatUserBean();
			if(mWechatBean!=null){
				//微信登录
				mOpenUid=mWechatBean.getOpenid();
				mLoginType=LOGINTYPE_WECHAT;

				bind(mLoginType, mOpenUid);
			}
			VpApplication.getInstance().setWechatUserBean(null);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent data) {
		super.onActivityResult(requestCode, responseCode, data);
		if (requestCode == REQUEST_BIND_PHONE && responseCode==AccountBindPhoneActivity.BIND_SUCCESS_CODE) {
			if (data != null) {
				String phone = data.getStringExtra(BIND_PHONE);
				AccountBindItemRelativeLayout item = (AccountBindItemRelativeLayout) layout
						.getChildAt(mCurrentClickPosition);
				item.mTvState.setVisibility(View.VISIBLE);
				phone = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
				item.mTvState.setText("已绑定" + phone);
				item.mTvBind.setText("已绑定");
				item.mTvBind.setTextColor(getResources().getColor(R.color.sub_textView_color99));
			}
		}else if(requestCode == REQUEST_UNBIND_PHONE && responseCode==AccountBindPhoneActivity.UNBIND_SUCCESS_CODE){
			AccountBindItemRelativeLayout item = (AccountBindItemRelativeLayout) layout
					.getChildAt(mCurrentClickPosition);
			item.mTvState.setVisibility(View.GONE);
			item.mTvState.setText("");
			item.mTvBind.setText("绑定");
			item.mTvBind.setTextColor(getResources().getColor(R.color.normal_textView_color));
		}
	}

	private void initData() {
		String url = VpConstants.USER_BIND_INFO;
		RequestParams params = new RequestParams();
		int uid = LoginStatus.getLoginInfo().getUid();
		params.put("id", uid);
		mClient.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {// 返回成功
						String data = json.getString(VpConstants.HttpKey.DATA);
						JSONObject jsonData = new JSONObject(data);
						String wxOpenId = jsonData.getString("wx_open_id");
						String wbOpenId = jsonData.getString("wb_open_id");
						String qqOpenId = jsonData.getString("qq_open_id");
						String mt = jsonData.getString("mt");
						mPhoneNumber=mt;
						// setvalue
						for (int i = 0; i < layout.getChildCount(); i++) {
							AccountBindItemRelativeLayout item = (AccountBindItemRelativeLayout) layout.getChildAt(i);
							if ("手机号".equals(item.mTvName.getText())) {
								if (!TextUtils.isEmpty(mt)) {
									item.mTvState.setVisibility(View.VISIBLE);
									mt = mt.substring(0, 3) + "****" + mt.substring(7, mt.length());
									item.mTvState.setText("已绑定" + mt);
									item.mTvBind.setText("已绑定");
									item.mTvBind.setTextColor(getResources().getColor(R.color.sub_textView_color99));
								} else {
									item.mTvBind.setText("绑定");
									item.mTvBind.setTextColor(getResources().getColor(R.color.normal_textView_color));
								}
							} else if ("QQ账号".equals(item.mTvName.getText())) {
								if (TextUtils.isEmpty(qqOpenId)) {
									item.mTvBind.setText("绑定");
									item.mTvBind.setTextColor(getResources().getColor(R.color.normal_textView_color));
								} else {
									item.mTvBind.setText("已绑定");
									item.mTvBind.setTextColor(getResources().getColor(R.color.sub_textView_color99));
								}
							} else if ("微信账号".equals(item.mTvName.getText())) {
								if (TextUtils.isEmpty(wxOpenId)) {
									item.mTvBind.setText("绑定");
									item.mTvBind.setTextColor(getResources().getColor(R.color.normal_textView_color));
								} else {
									item.mTvBind.setText("已绑定");
									item.mTvBind.setTextColor(getResources().getColor(R.color.sub_textView_color99));
								}
							} else if ("微博账号".equals(item.mTvName.getText())) {
								if (TextUtils.isEmpty(wbOpenId)) {
									item.mTvBind.setText("绑定");
									item.mTvBind.setTextColor(getResources().getColor(R.color.normal_textView_color));
								} else {
									item.mTvBind.setText("已绑定");
									item.mTvBind.setTextColor(getResources().getColor(R.color.sub_textView_color99));
								}
							}
						}
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(AccountBindActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(AccountBindActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();

			}
		});

	}

	private void bind(int loginType,String openId) {
		// 发送网络请求
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("uid", LoginStatus.getLoginInfo().getUid());
			jsonObj.put("login_type", loginType);
			jsonObj.put("open_web_uid", openId);
		} catch (Exception e) {
			Toast.makeText(this, "请求参数有误", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		}
		mClient.post(VpConstants.USER_BINDING, new RequestParams(), jsonObj.toString(), true,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						String result = ResultParseUtil.deAesResult(responseBody);
						try {
							JSONObject json = new JSONObject(result);
							String code = json.getString(VpConstants.HttpKey.CODE);
							if ("0".equals(code)) {// 返回成功
								Toast.makeText(AccountBindActivity.this, "绑定成功", Toast.LENGTH_LONG).show();
								AccountBindItemRelativeLayout item = (AccountBindItemRelativeLayout) layout
										.getChildAt(mCurrentClickPosition);
								item.mTvBind.setText("已绑定");
								item.mTvBind.setTextColor(getResources().getColor(R.color.sub_textView_color99));
							} else {
								String message = json.getString(VpConstants.HttpKey.MSG);
								Toast.makeText(AccountBindActivity.this, message, Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						System.out.println(statusCode);

					}
				});
	}
	private void unBind(final int loginType) {
		// 发送网络请求
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("uid", LoginStatus.getLoginInfo().getUid());
			jsonObj.put("login_type", loginType);
		} catch (Exception e) {
			Toast.makeText(this, "请求参数有误", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		}
		mClient.post(VpConstants.USER_UNBINDING, new RequestParams(), jsonObj.toString(), true,
				new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {// 返回成功
						Toast.makeText(AccountBindActivity.this, "解除绑定成功", Toast.LENGTH_LONG).show();
						AccountBindItemRelativeLayout item = (AccountBindItemRelativeLayout) layout
								.getChildAt(mCurrentClickPosition);
						item.mTvBind.setText("绑定");
						item.mTvBind.setTextColor(getResources().getColor(R.color.normal_textView_color));
						if(loginType==LOGINTYPE_PHONE){
							item.mTvState.setText("");
							item.mTvState.setVisibility(View.GONE);
						}
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(AccountBindActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				System.out.println(statusCode);
				
			}
		});
	}
	
	/**
	 * 是否可以取消绑定,账号不能全部取消绑定
	 * @return
	 */
	private void unBindOperation(int unBindType){
		int bindCount=0;
		for (int i = 0; i < layout.getChildCount(); i++) {
			AccountBindItemRelativeLayout item = (AccountBindItemRelativeLayout) layout.getChildAt(i);
			if("已绑定".equals(item.mTvBind.getText().toString())){
				bindCount++;
			}
		}
		if(bindCount>1){
			switch (unBindType) {
			case LOGINTYPE_PHONE:
				//需求改动，不需要手机验证码参数
//				Intent intent=new Intent(AccountBindActivity.this, AccountBindPhoneActivity.class);
//				intent.putExtra(AccountBindPhoneActivity.BIND_TYPE,false);
//				intent.putExtra(AccountBindPhoneActivity.BIND_PHONE_NUM,mPhoneNumber);
//				startActivityForResult(intent,REQUEST_UNBIND_PHONE);
				showOperation(unBindType);
				break;
			case LOGINTYPE_QQ:
			case LOGINTYPE_SINA:
			case LOGINTYPE_WECHAT:
				showOperation(unBindType);
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 解除绑定
	 */
	private void showOperation(final int unBindType){	
		new IOSActionSheetDialog(this)
		.builder()
		.setTitle("确定取消绑定吗？")
		.setCancelable(false)
		.setCanceledOnTouchOutside(false)
		.addSheetItem("确定", SheetItemColor.Green,
				new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						unBind(unBindType);
					}
	 })
	 .show();
	}

}
