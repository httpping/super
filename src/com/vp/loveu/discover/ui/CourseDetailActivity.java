package com.vp.loveu.discover.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.discover.bean.CourseDetailBean;
import com.vp.loveu.discover.bean.CoursePayBean;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.message.ui.PrivateChatActivity;
import com.vp.loveu.pay.bean.PayBindViewBean.PayType;
import com.vp.loveu.pay.ui.PayActivity;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.util.UrlIsAppUtil;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.widget.TimeoutRemindView;

import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年11月18日 上午10:13:03
 * @Description: 在线课程详情
 */
public class CourseDetailActivity extends VpActivity implements OnClickListener {
	public static final String COURSE_ID = "course_id";
	public static final String ISPROMO_CODE = "ispromocodeIn";
	private WebView mWebview;
	TimeoutRemindView mRemindView;

	private CourseDetailBean mBean;
	private int mCourseID;
	private int mLoginUid;
	private ProgressBar mProgress;
	private TextView mTvZixun;
	private TextView mTvBuy;
	private String mUrl;
	private boolean isWebLoadFinish;
	private String mPromoCode;// 是否是优惠码进来的
	private boolean isPromoEmpty;// 是否为空

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discover_course_detail_activity);
		this.mClient = new VpHttpClient(this);
		mClient.setShowProgressDialog(false);
		mCourseID = getIntent().getIntExtra(COURSE_ID, 0);
		mLoginUid = LoginStatus.getLoginInfo().getUid();
		mPromoCode = getIntent().getStringExtra(ISPROMO_CODE);
		isPromoEmpty = TextUtils.isEmpty(mPromoCode);
		initView();
		initDatas(mCourseID);
		VpApplication.getInstance().payresult = false;
		VpApplication.getInstance().mPayBindViewBean = null;
	}

	private void initView() {
		initPublicTitle();
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText("课程详细");
		mWebview = (WebView) findViewById(R.id.discover_course_webview);
		mProgress = (ProgressBar) findViewById(R.id.course_progress);
		mTvZixun = (TextView) findViewById(R.id.discover_course_zixun);
		mTvBuy = (TextView) findViewById(R.id.discover_course_buy);
		mTvZixun.setOnClickListener(this);
		mTvBuy.setOnClickListener(this);
		WebSettings settings = mWebview.getSettings();
		settings.setJavaScriptEnabled(true);

		// http://g.loveu.com/go/104?id={id}&uid={uid}&app_is_installed=1&coupon={coupon}
		String url;
		if (!isPromoEmpty) {
			url = VpConstants.DISCOVER_COURSE_WEB_DETAIL + "id=" + mCourseID + "&uid=" + mLoginUid + "&coupon=" + mPromoCode;
		} else {
			url = VpConstants.DISCOVER_COURSE_WEB_DETAIL + "id=" + mCourseID + "&uid=" + mLoginUid;
		}
		mWebview.loadUrl(url);
		mWebview.setWebViewClient(wn);
		setButtonEnable(false);
		mRemindView = (TimeoutRemindView) findViewById(R.id.timeoutRemindView1);
		mRemindView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!MyUtils.isNetword(v.getContext())) {// 没网
					return;
				}
				mRemindView.setVisibility(View.GONE);
				mWebview.reload();
			}
		});

	}

	private WebViewClient wn = new WebViewClient() {
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mHandler.removeMessages(TIMEOUT_MARKER);
			// mRemindView.setVisibility(View.GONE);
			mProgress.setVisibility(View.GONE);
			setButtonEnable(true);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			VPLog.d("web", "url:" + url);
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
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			// super.onReceivedError(view, errorCode, description, failingUrl);
			// Toast.makeText(WebViewActivity.this, "error:" +errorCode,
			// 1).show();
			VPLog.d(tag, "onReceivedError");
			// view.loadUrl("javascript:document.body.innerHTML=\"" + errorCode
			// + "\"");
			mHandler.removeMessages(TIMEOUT_MARKER);
			mRemindView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			VPLog.d(tag, "onPageStarted");
			mHandler.sendEmptyMessageDelayed(TIMEOUT_MARKER, 60 * 1000);
		}
	};

	/**
	 * 回调处理
	 */
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

	@Override
	protected void onPause() {
		super.onPause();
		if (mWebview != null) {
			mWebview.reload();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mWebview != null) {
			mWebview.removeAllViews();
			mWebview.destroy();
		}
	}

	/**
	 * 
	 * @param coruseId
	 */
	private void initDatas(int coruseId) {

		// {id}?uid={uid}&coupon={coupon}
		int uid = LoginStatus.getLoginInfo().getUid();
		String url = VpConstants.DISCOVER_COURSE_DETAIL;
		RequestParams params = new RequestParams();
		params.put("id", coruseId);
		params.put("uid", uid);

		if (!isPromoEmpty) {
			params.put("coupon", mPromoCode);
		}

		mClient.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);

					if ("0".equals(code)) {// 返回成功
						String data = json.getString(VpConstants.HttpKey.DATA);
						mBean = CourseDetailBean.parseJson(data);
						if (mBean != null && isWebLoadFinish)
							setButtonEnable(true);
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(CourseDetailActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(CourseDetailActivity.this, "网络访问异常", Toast.LENGTH_SHORT).show();

			}
		});

	}

	@Override
	public void onClick(View v) {
		if (mBean == null)
			return;
		switch (v.getId()) {
		case R.id.discover_course_zixun:
			if (mBean.getMentor() != null) {
				Intent chatIntent = new Intent(this, PrivateChatActivity.class);
				chatIntent.putExtra(PrivateChatActivity.CHAT_USER_ID, mBean.getConsult().getUid());
				chatIntent.putExtra(PrivateChatActivity.CHAT_USER_NAME, mBean.getConsult().getNickname());
				chatIntent.putExtra(PrivateChatActivity.CHAT_USER_HEAD_IMAGE, mBean.getConsult().getPortrait());
				chatIntent.putExtra(PrivateChatActivity.CHAT_XMPP_USER, mBean.getConsult().getXmpp_user());
				startActivity(chatIntent);
			}
			break;
		case R.id.discover_course_buy:
			if (mBean == null) {
				break;
			}
			// 购买
			Intent intent = new Intent(CourseDetailActivity.this, PayActivity.class);
			// CoursePayBean payBean = new CoursePayBean(mBean.getId(),
			// mBean.getPic(), mBean.getName(), mBean.getPrice(),
			// mBean.getUser_num(), mBean.getRemark(),mBean.getRebate_day());
			// payBean.payTitle="支付报名";
			// payBean.payType=PayType.classroom_pay;
			// payBean.payMoney=mBean.getPrice();
			
			if (payBean == null) {
				payBean = new CoursePayBean(mBean);
				payBean.payTitle = "课程购买";
				payBean.payMoney = mBean.getPrice();//不管用没用优惠码  实际支付价格都是price  original_price只是原价 新增加的字段
				payBean.payType = PayType.classroom_pay;
				payBean.setIsPromoCode(mPromoCode);
			}
			intent.putExtra(PayActivity.PAY_PARAMS, payBean);
			startActivity(intent);

			break;
		default:
			break;
		}
	}
	CoursePayBean payBean;

	private void setButtonEnable(boolean enable) {
		mTvZixun.setEnabled(enable);
		mTvBuy.setEnabled(enable);
	}

}
