package com.vp.loveu.index.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.bean.NetBaseBean;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.bean.ActiveVipBean;
import com.vp.loveu.index.bean.ActivityDetailBean;
import com.vp.loveu.index.bean.ActivityDetailBean.ActivityDetailData;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.pay.bean.PayBindViewBean.PayType;
import com.vp.loveu.pay.ui.PayActivity;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.UrlIsAppUtil;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.widget.TimeoutRemindView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月3日下午12:59:40
 * @功能 活动详情的activity
 * @作者 mi
 */

public class ActiveWebActivity extends VpActivity {

	public static final String KEY_WEB = "key_web";
	private WebView mWebview;
	private ProgressBar mProgressBar;
	private Button mCommit;
	private ActivityDetailData mDatas;
	private int id;
	private long mTimes;
	private String mUrl;
	private Gson gson = new Gson();
	private String mCurrentLoadPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		id = getIntent().getIntExtra(KEY_WEB, -1);
		if (id == -1) {
			return;
		}
		VpApplication.getInstance().mPayBindViewBean = null;
		VpApplication.getInstance().payresult = false;
		setContentView(R.layout.web_activity);
		initPublicTitle();
		initView();
		initData();
	}

	public static final int TIMEOUT_MARKER = 400;
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case TIMEOUT_MARKER: // 超时
				// Toast.makeText(WebViewActivity.this, "超时", 1).show();
				mRemindView.setVisibility(View.VISIBLE);
				break;

			default:
				break;
			}

		};
	};

	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mBtnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mPubTitleView.mTvTitle.setText("活动详情");
		mWebview = (WebView) findViewById(R.id.active_web_webview);
		mProgressBar = (ProgressBar) findViewById(R.id.active_web_progress);
		mCommit = (Button) findViewById(R.id.active_web_bt_commit);
		mRemindView = (TimeoutRemindView) findViewById(R.id.timeoutRemindView1);
		mRemindView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (MyUtils.isNetword(ActiveWebActivity.this)) {
					mRemindView.setVisibility(View.GONE);
					mWebview.loadUrl(mCurrentLoadPath);
				}
			}
		});
	}

	private TimeoutRemindView mRemindView;

	private void initData() {
		startHttp();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void startWeb() {
		String endTime = mDatas.end_time;
		if (Long.valueOf(endTime) > mTimes / 1000) {
			// 说明活动没有过期 是正在举办中的
			mCommit.setText("立即报名");
			mCommit.setBackgroundColor(Color.parseColor("#FF8000"));
			mCommit.setTextColor(Color.parseColor("#ffffff"));
			mCommit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startApplay();
				}
			});
		} else {
			// 说明活动已经过期
			mCommit.setText("希望再次举办  (" + mDatas.agin_num + ")");
			mCommit.setBackgroundColor(Color.parseColor("#D8D8D8"));
			mCommit.setTextColor(Color.parseColor("#10BB7D"));
			mCommit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					continueActive();
				}
			});
		}

		WebSettings settings = mWebview.getSettings();
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		settings.setBuiltInZoomControls(false);
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		mWebview.setWebViewClient(wn);
		// 设置setWebChromeClient对象
		mWebview.setWebChromeClient(wvcc);
		mCurrentLoadPath = VpConstants.ACTIVE_WEB_URL + id;
		mWebview.loadUrl(mCurrentLoadPath);

	}

	private WebViewClient wn = new WebViewClient() {

		@Override
		public void onPageFinished(WebView view, String url) {

			super.onPageFinished(view, url);
			// setButtonEnable(true);
			mProgressBar.setVisibility(View.GONE);
			mCommit.setEnabled(true);
			mHandler.removeMessages(TIMEOUT_MARKER);

		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!TextUtils.isEmpty(url) && url.startsWith("loveu")) {
				Uri uri = Uri.parse(url);
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
				mUrl = view.getUrl();
				view.loadUrl(mUrl);
				return true;
			}
			mUrl = url;
			mUrl = UrlIsAppUtil.appendAppIsParam(url);

			view.loadUrl(mUrl);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			VPLog.d(tag, "onPageStarted");
			mHandler.sendEmptyMessageDelayed(TIMEOUT_MARKER, 60 * 1000);
			mCommit.setEnabled(false);
		}

		
		
		
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			VPLog.d(tag, "onReceivedError");
			mHandler.removeMessages(TIMEOUT_MARKER);
			mRemindView.setVisibility(View.VISIBLE);
		}
	};

	WebChromeClient wvcc = new WebChromeClient() {

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				mProgressBar.setVisibility(View.GONE);
			} else {
				if (mProgressBar.getVisibility() == View.GONE)
					mProgressBar.setVisibility(View.VISIBLE);
				mProgressBar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}
	};

	/**
	 * 继续举办 void
	 */
	protected void continueActive() {
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		JSONObject obj = new JSONObject();
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		try {
			obj.put("id", id);
			obj.put("uid", loginInfo.getUid());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mClient.post(VpConstants.CONTINUE_ACTIVE, new RequestParams(), obj.toString(), new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ActivityDetailBean fromJson = gson.fromJson(ResultParseUtil.deAesResult(responseBody), ActivityDetailBean.class);
				if (fromJson.code == 0) {
					// 提交成功
					mCommit.setText("希望再次举办  (" + (mDatas.agin_num + 1) + ")");
					Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_SHORT).show();
					mCommit.setTextColor(Color.parseColor("#999999"));
					mCommit.setEnabled(false);
				} else {
					Toast.makeText(getApplicationContext(), fromJson.msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 开始报名活动 void
	 */
	protected void startApplay() {
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			loginInfo = new LoginUserInfoBean(this);
		}
		double money = loginInfo.getSex() == 1 ? mDatas.male_price : mDatas.female_price;
		
		if (money == 0) {
			// 免费活动---直接报名
			startApply(money);
		} else {
			Intent intent = new Intent(this, PayActivity.class);
			ActiveVipBean bean = new ActiveVipBean();
			bean.bean = mDatas;
			bean.payType = PayType.activity;
			bean.isRequestPhone = true;
			bean.payTitle = "活动报名";
			bean.payMoney = money;
			intent.putExtra(PayActivity.PAY_PARAMS, bean);
			startActivity(intent);// 支付结果
		}
	}

	/**
	 * 免费活动 
	 * @param money
	 * void
	 */
	private void startApply(double money) {
		Intent intent = new Intent(this, FreeApplayActivity.class);
		ActiveVipBean bean = new ActiveVipBean();
		bean.bean = mDatas;
		bean.payTitle = "活动报名";
		intent.putExtra(PayActivity.PAY_PARAMS, bean);
		startActivity(intent);
	}

	private void startHttp() {
		mClient = new VpHttpClient(this);
		mClient.setShowProgressDialog(false);
		RequestParams params = new RequestParams();
		params.put("id", id);
		mClient.get(VpConstants.GET_ACTIVE_INFO, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				mTimes = ResultParseUtil.getServerDate().getTime();
				ActivityDetailBean fromJson = gson.fromJson(result, ActivityDetailBean.class);
				if (fromJson.code == 0) {
					mDatas = fromJson.data;
					startWeb();
				} else {
					Toast.makeText(getApplicationContext(), NetBaseBean.parseJson(result).msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(getApplicationContext(), R.string.network_error,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
			mWebview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
