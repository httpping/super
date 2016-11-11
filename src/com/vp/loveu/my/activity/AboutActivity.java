package com.vp.loveu.my.activity;

import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.util.ShareCompleteUtils;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月12日下午3:52:46
 * @功能TODO
 * @作者 mi
 */

public class AboutActivity extends VpActivity {

	public static final String KEY = "key";
	public static final int ABOUT = 1;//关于我们
	public static final int FAQ = 2;//常见问题
	public static final int INTERGRAL = 3;//积分规则
	private WebView mWebView;
	private ProgressBar mProgress;
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = getIntent().getIntExtra(KEY, -1);
		if (type == -1) {
			return;
		}
		setContentView(R.layout.aboutactivity);
		initPublicTitle();
		initView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mWebView = (WebView) findViewById(R.id.about_webview);
		mProgress = (ProgressBar) findViewById(R.id.about_progressbar);
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mProgress.setVisibility(View.GONE);
			}
		});
		if (type == ABOUT) {
			mPubTitleView.mTvTitle.setText("关于我们");
			mWebView.loadUrl(VpConstants.ABOUT_US+"&vn="+getPackageVersion());
		}else if(type == FAQ){
			mPubTitleView.mTvTitle.setText("常见问题");
			mWebView.loadUrl(VpConstants.FAQ);
		}else if(type == INTERGRAL){
			mPubTitleView.mTvTitle.setText("积分规则");
			mWebView.loadUrl(VpConstants.INTERGRAL);
		}
	}
	
	public String getPackageVersion(){
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
			int versionCode = info.versionCode;
			String versionName = info.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
}
