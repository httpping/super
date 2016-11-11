package com.vp.loveu.channel.ui;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.FrameLayout;

import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.util.UrlIsAppUtil;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.widget.TimeoutRemindView;

/**
 * @author：pzj
 * @date: 2015年12月16日 上午10:14:22
 * @Description: 视频播放页面
 */
public class VideoPlayActivity extends VpActivity {
	public static final String URL="url";
	private WebView mWebview; 
	private String  mUrl;
	TimeoutRemindView mRemindView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel_video_play_activity);
		FrameLayout fl= (FrameLayout) findViewById(R.id.video_play_container);
		mWebview=new WebView(getApplicationContext());
		mWebview.setWebViewClient(wn);
		fl.addView(mWebview);
		mRemindView= new TimeoutRemindView(this);
		mRemindView.setVisibility(View.VISIBLE);
		//fl.addView(mRemindView);
		
		 this.mUrl=getIntent().getStringExtra(URL);
		 WebSettings settings = mWebview.getSettings();
		 settings.setJavaScriptEnabled(true);
		 mWebview.setVerticalScrollBarEnabled(false);
		 mWebview.setHorizontalScrollBarEnabled(false);
		 initSettings(mWebview);
		 if(mUrl!=null){
			// mWebview.loadData(mHtmlUrl, "text/html", "UTF-8");
			 mWebview.loadUrl(mUrl);
		 }
		 
		mRemindView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( !MyUtils.isNetword(v.getContext())){//没网
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
	
	
	private void initSettings(WebView mWebView) {

		WebSettings webSettings = mWebView.getSettings();
		//开启java script的支持
		webSettings.setJavaScriptEnabled(true);

		// 启用localStorage 和 essionStorage
		webSettings.setDomStorageEnabled(true);

		// 开启应用程序缓存
		webSettings.setAppCacheEnabled(true);
		String appCacheDir = this.getApplicationContext()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		webSettings.setAppCachePath(appCacheDir);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
	//	webSettings.setAppCacheMaxSize(1024 * 1024 * 10);// 设置缓冲大小，我设的是10M
		webSettings.setAllowFileAccess(true);

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(mWebview!=null){
			mWebview.onPause();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mWebview!=null){
			mWebview.onResume();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mWebview!=null){
			mWebview.removeAllViews();
			mWebview.destroy();
		}
	}
}
