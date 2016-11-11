package com.vp.loveu.index.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.custom.ShareDialogFragment;
import cn.sharesdk.onekeyshare.custom.ShareModel;
import cn.sharesdk.sina.weibo.SinaWeibo;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.bean.InfomationDetailBean;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.my.fragment.ArticleFragment;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ShareCompleteUtils;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.UrlIsAppUtil;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.widget.TimeoutRemindView;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月3日下午2:59:02
 * @功能 精选长文的activity or pua课堂详情
 * @作者 mi
 */

public class ArticleActivity extends VpActivity implements OnClickListener {

	public static final String KEY_FLAG = "key_flag";
	public static final String KEY_FLAG_TYPE = "key_flag_type";
	public static final String KEY_FLAG_SHARE_TITLE = "key_flag_share_title";
	public static final String KEY_FLAG_SHARE_CONTENT = "key_flag_share_content";
	public static final String KEY_FLAG_SHARE_ICONPATH = "key_flag_share_icon";
	public static final int TYPE_ARTICLE = 1;// 精选长文
	public static final int TYPE_PUA = 2;// PUA课堂
	public static final int TYPE_TOPIC= 3;// 大家都在聊
	public static final int TYPE_APP= 999;// APP分享
	public int id;
	private WebView mWebview;
	private ImageView mIvShare;
	private ImageView mIvCollect;
	private TextView mTvZan;
	private TextView mTvAddOne;
	private ProgressBar mProgress;
	private int mType = TYPE_ARTICLE;// 类型
	private InfomationDetailBean mDetailBean;
	private static int OPERATER_LIKE = 1;// 点赞
	private static int OPERATER_COLLECT = 2;// 收藏
	private static int OPERATER_CANCELCOLLECT = 3;//取消 收藏
    private String mCurrentLoadPath;
	private SharedPreferencesHelper sp;
	private String mShareTitle;//分享标题
	private String mShareContext;//分享内容
	private String mShareIconPath;//分享展示的图片
	private int mUid;
	private String mUrl;
	private android.view.animation.Animation animation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.article_activity);
		mUid=LoginStatus.getLoginInfo().getUid();
		mClient = new VpHttpClient(this);
		mClient.setShowProgressDialog(false);
		id = getIntent().getIntExtra(KEY_FLAG, id);
		mType = getIntent().getIntExtra(KEY_FLAG_TYPE, TYPE_ARTICLE);
		mShareTitle=getIntent().getStringExtra(KEY_FLAG_SHARE_TITLE);
		mShareContext=getIntent().getStringExtra(KEY_FLAG_SHARE_CONTENT);
		mShareIconPath=getIntent().getStringExtra(KEY_FLAG_SHARE_ICONPATH);
		animation=AnimationUtils.loadAnimation(this,R.anim.topic_like_nn);
		initView();
		initData();
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(mWebview!=null){
			mWebview.onPause();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mWebview!=null){
			mWebview.stopLoading();
		}
		sp.removeKey(ArticleFragment.KEY);
	}

	
	private TimeoutRemindView mRemindView;
	private void initView() {
		mWebview = (WebView) findViewById(R.id.article_webview);
		mIvShare = (ImageView) findViewById(R.id.article_iv_share);
		mIvCollect = (ImageView) findViewById(R.id.article_iv_collect);
		mTvZan = (TextView) findViewById(R.id.article_tv_zan);
		mTvAddOne = (TextView) findViewById(R.id.article_tv_like_addone);
		mProgress = (ProgressBar) findViewById(R.id.article_progress);
		mRemindView = (TimeoutRemindView) findViewById(R.id.timeoutRemindView1);
		
		
		mIvCollect.setOnClickListener(this);
		mTvZan.setOnClickListener(this);
		mIvShare.setOnClickListener(this);
		sp = SharedPreferencesHelper.getInstance(this);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setButtonEnable(false);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initData() {
		getDatas();
		WebSettings settings = mWebview.getSettings();
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		settings.setBuiltInZoomControls(false);
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		if(mType==TYPE_ARTICLE){//默认	
			mCurrentLoadPath=VpConstants.ARTICLE_WEB_URL+id;
		}else if(mType==TYPE_PUA){//PUA课堂详情
			mCurrentLoadPath=VpConstants.DISCOVER_PUA_DETAIL+id;
		}
		
		
		mWebview.loadUrl(mCurrentLoadPath);
		
		mWebview.setWebViewClient(wn);
		// 设置setWebChromeClient对象
		mWebview.setWebChromeClient(wvcc);
		
		mRemindView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (MyUtils.isNetword(ArticleActivity.this)) {
					mRemindView.setVisibility(View.GONE);
					mWebview.loadUrl(mCurrentLoadPath);
				}
			}
		});
		
	}
	
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

	
	private boolean isOneIn;
	private WebViewClient wn = new WebViewClient() {
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mProgress.setVisibility(View.GONE);
			isOneIn = true;
			setButtonEnable(true);
			mHandler.removeMessages(TIMEOUT_MARKER);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!TextUtils.isEmpty(url)&& url.startsWith("loveu")){
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
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			
			mHandler.removeMessages(TIMEOUT_MARKER);
			mRemindView.setVisibility(View.VISIBLE);
		}
		
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			mHandler.sendEmptyMessageDelayed(TIMEOUT_MARKER, 60*1000);
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
				mProgress.setVisibility(View.GONE);
			} else {
				if (mProgress.getVisibility() == View.GONE && !isOneIn){
					mProgress.setVisibility(View.VISIBLE);
				}
				mProgress.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}
	};

	
	private void getDatas() {
		String url = VpConstants.DISCOVER_PUA_DETAIL_INFO;
		RequestParams params = new RequestParams();
		params.put("id", this.id);
		params.put("uid", mUid);
		mClient.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);

					if ("0".equals(code)) {// 返回成功
						JSONObject jsonData=new JSONObject(json.getString(VpConstants.HttpKey.DATA));
						mDetailBean = InfomationDetailBean.parseJson(jsonData.toString());
						setViewData();
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(ArticleActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(getApplicationContext(), R.string.network_error,
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	protected void setViewData() {
		if(mDetailBean!=null){
			//收藏,点赞
			mIvCollect.setImageResource(mDetailBean.getIs_fav()==1?R.drawable.channel_topic_collected:R.drawable.channel_topic_collect);			
			Drawable drawable = getResources().getDrawable(mDetailBean.getIs_like()==1?R.drawable.channel_topic_likeed:R.drawable.channel_topic_like);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
			mTvZan.setCompoundDrawables(drawable, null, null, null);//画在左边			
			mTvZan.setText("("+mDetailBean.getLike_num()+")");

		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.article_iv_share://分享
			shareOperate();
			break;
		case R.id.article_iv_collect://收藏
			if(mDetailBean==null)
				return;
			int isFav = mDetailBean.getIs_fav();
			if(0==isFav){//未收藏
				attentionOperate(OPERATER_COLLECT);
			}else{
				//取消收藏
				attentionOperate(OPERATER_CANCELCOLLECT);
			}
			
			break;
		case R.id.article_tv_zan://点赞
			if(mDetailBean==null)
				return;
			//判断当前主题是否已赞
			int like =mDetailBean.getIs_like();
			if(0==like){//未赞
				attentionOperate(OPERATER_LIKE);
			}
			break;

		default:
			break;
		}
		
	}
	
	/**
	 * 分享
	 */
	private void shareOperate(){
		ShareModel model=new ShareModel();
		model.setId(id);
		model.setTitle(mShareTitle);
		model.setText(mShareContext);
		model.setUrl(VpConstants.ARTICLE_WEB_URL+id);
		model.setImageUrl(mShareIconPath);
		model.setType(mType);
		//临时记录当前分享的内容信息
		VpApplication.getInstance().setShareModel(model);
		
		ShareDialogFragment dialog=new ShareDialogFragment();
		dialog.show(getSupportFragmentManager(), KEY_FLAG, model);
		dialog.setPlatformActionListener(new PlatformActionListener() {
			
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				Toast.makeText(ArticleActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
				
			}
			
			@Override
			public void onComplete(Platform platform, int arg1, HashMap<String, Object> arg2) {
				//微信分享成功回调需要在WXEntryActivity 处理
				if(platform.getName().equals(SinaWeibo.NAME)){
					Toast.makeText(ArticleActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
					ShareModel tempModel = VpApplication.getInstance().getShareModel();
					if(tempModel!=null){
						ShareCompleteUtils utils=new ShareCompleteUtils(ArticleActivity.this);
						utils.reportData(mUid, tempModel.getId(), tempModel.getType());
						VpApplication.getInstance().setShareModel(null);
					}
				}
				
			}
			
			@Override
			public void onCancel(Platform arg0, int arg1) {
				Toast.makeText(ArticleActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
				
			}
		});
	}

	private void attentionOperate(final int operaterType) {

		String url =null;
		JSONObject data= new JSONObject();
		try {
			if(operaterType==OPERATER_LIKE){//点赞
				url = VpConstants.CHANNEL_GENERAL_ADD_PRAISE;
			}else if(operaterType==OPERATER_COLLECT){
				url = VpConstants.CHANNEL_GENERAL_ADD_FAVORITE;//收藏
			}else if(operaterType==OPERATER_CANCELCOLLECT){
				url = VpConstants.My_DELETE_FAVORITE;//取消收藏
			}
			data.put("uid", mUid);
			data.put("id", this.id);
			data.put("type", this.mType);//点赞类型1=长文推荐 2=PUA课堂 3=大家都在聊
			final int tempUid = this.id;
			mClient.post(url, new RequestParams(), data.toString(), false, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String result = ResultParseUtil.deAesResult(responseBody);
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString(VpConstants.HttpKey.CODE);

						if ("0".equals(code)) {// 返回成功
							if(operaterType==OPERATER_LIKE){//赞
								mDetailBean.setIs_like(1);
								Drawable drawable = getResources().getDrawable(R.drawable.channel_topic_likeed);
								drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
								mTvZan.setCompoundDrawables(drawable, null, null, null);//画在左边			
								mTvZan.setText("("+(mDetailBean.getLike_num()+1)+")");
//								Toast.makeText(ArticleActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
								mTvAddOne.setVisibility(View.VISIBLE);
								mTvAddOne.startAnimation(animation);
								new Handler().postDelayed(new Runnable(){
									public void run() {
										mTvAddOne.setVisibility(View.GONE);
									} 
								}, 1000);
							}else if(operaterType==OPERATER_COLLECT){//收藏
								mDetailBean.setIs_fav(1);
								mIvCollect.setImageResource(R.drawable.channel_topic_collected);	
								Toast.makeText(ArticleActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
							}else if(operaterType==OPERATER_CANCELCOLLECT){//取消收藏
								mDetailBean.setIs_fav(0);
								mIvCollect.setImageResource(R.drawable.channel_topic_collect);	
//								Toast.makeText(ArticleActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
								sp.putIntegerValue(ArticleFragment.KEY, tempUid);
							}

						} else {
							String message = json.getString(VpConstants.HttpKey.MSG);
							Toast.makeText(ArticleActivity.this, message, Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					Toast.makeText(getApplicationContext(), R.string.network_error,
							Toast.LENGTH_SHORT).show();
					
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();

		}
		
	
		
	}
	private void setButtonEnable(boolean enable){
		mIvShare.setEnabled(enable);
		mIvCollect.setEnabled(enable);
		mTvZan.setEnabled(enable);
	}

	
	
}
