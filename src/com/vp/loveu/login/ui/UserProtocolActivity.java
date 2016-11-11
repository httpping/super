package com.vp.loveu.login.ui;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;

/**
 * @author：pzj
 * @date: 2015年12月11日 上午9:16:58
 * @Description:
 */
public class UserProtocolActivity extends VpActivity {
	private WebView mWebView;
	private ProgressBar mProgress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_user_protocol_activity);
		mWebView=(WebView) findViewById(R.id.login_user_protocol_wv);
		mProgress = (ProgressBar) findViewById(R.id.protocol_web_progress);
		int versionCode=-1;
		try {
			versionCode = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		mWebView.loadUrl(VpConstants.USER_LOGIN_USER_PROTOTOL+versionCode);
		mWebView.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mProgress.setVisibility(View.GONE);
			}

			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
				return super.shouldInterceptRequest(view, url);
			}

		});
	}
}
