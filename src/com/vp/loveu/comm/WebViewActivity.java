package com.vp.loveu.comm;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.util.UrlIsAppUtil;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.widget.TimeoutRemindView;

public class WebViewActivity extends VpActivity {

	WebView mWebView;
	TimeoutRemindView mRemindView;
	private ProgressBar mProgressbar;

	public static final String URL = "url";

	public String mUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUrl = getIntent().getStringExtra(URL);
		if (mUrl.startsWith(VpConstants.APP_SIGN_IN_GO)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		setContentView(R.layout.public_webview_activity);
		initPublicTitle();
		initView();

	}

	private void initView() {
		mWebView = (WebView) findViewById(R.id.public_webview);
		mPubTitleView.mTvTitle.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mProgressbar = (ProgressBar) findViewById(R.id.webview_progressBar);
		WebSettings settings = mWebView.getSettings();
		
		mRemindView = (TimeoutRemindView) findViewById(R.id.timeoutRemindView1);
		mRemindView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( !MyUtils.isNetword(v.getContext())){//没网
					return;
				}
				mRemindView.setVisibility(View.GONE);
				mWebView.reload();
			}
		});
		mWebView.clearCache(true);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		settings.setBuiltInZoomControls(false);
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);

		WebViewClient wn = new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mHandler.removeMessages(TIMEOUT_MARKER);
				//mRemindView.setVisibility(View.GONE);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				VPLog.d("web", "url:"+url);
				if (!TextUtils.isEmpty(url)&& url.startsWith("loveu")){
					Uri uri = Uri.parse(url);
					Intent it = new Intent(Intent.ACTION_VIEW, uri);  
					startActivity(it);
					mUrl =  view.getUrl();
					view.loadUrl(mUrl);
					return true;
				}
				mUrl = url;
				
				mUrl = UrlIsAppUtil.appendAppIsParam(url);
				
				view.loadUrl(mUrl);
				return true;
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				//super.onReceivedError(view, errorCode, description, failingUrl);
				//Toast.makeText(WebViewActivity.this, "error:" +errorCode, 1).show();
				VPLog.d(tag, "onReceivedError");
			//	 view.loadUrl("javascript:document.body.innerHTML=\"" + errorCode + "\"");  
				mHandler.removeMessages(TIMEOUT_MARKER);
				mRemindView.setVisibility(View.VISIBLE);
			}
			
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				VPLog.d(tag, "onPageStarted");
				mHandler.sendEmptyMessageDelayed(TIMEOUT_MARKER, 60*1000);
			}
		};
		mWebView.setWebViewClient(wn);

		WebChromeClient wvcc = new WebChromeClient() {

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				VPLog.i("ANDROID_LAB", "TITLE=" + title);
				mPubTitleView.mTvTitle.setText(title);
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					mProgressbar.setVisibility(View.GONE);
				} else {
					if (mProgressbar.getVisibility() == View.GONE)
						mProgressbar.setVisibility(View.VISIBLE);
					mProgressbar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
			
			

		};
		// 设置setWebChromeClient对象
		mWebView.setWebChromeClient(wvcc);

		mWebView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				// 实现下载的代码
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
		
		VPLog.d("web", "murl:" +mUrl);
		
		mWebView.loadUrl(mUrl);
	}
	
	
	/**
	 * 回调处理
	 */
	public static final int TIMEOUT_MARKER = 400;
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case TIMEOUT_MARKER: //超时
				//Toast.makeText(WebViewActivity.this, "超时", 1).show();
				mRemindView.setVisibility(View.VISIBLE);
				break;

			default:
				break;
			}
			
		};
	};

	@Override
	public void onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mWebView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mWebView.onResume();
	}
	@Override
	public void finish() {
		super.finish();
		//mWebView.onPause();
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebView.destroy();
	}
}
