package com.vp.loveu.index.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.me.nereo.multi_image_selector.MultiImageSelectorActivity;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.comm.ShowImagesViewPagerActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.bean.FellHelpBean;
import com.vp.loveu.index.bean.FellHelpBean.FellHelpBeanData;
import com.vp.loveu.index.holder.FellHelpBottomHolder;
import com.vp.loveu.index.holder.FellHelpTopHolder;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.widget.IOSActionSheetDialog;
import com.vp.loveu.widget.IOSActionSheetDialog.OnSheetItemClickListener;
import com.vp.loveu.widget.IOSActionSheetDialog.SheetItemColor;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月19日上午9:14:40
 * @功能 情感求助的activity
 * @作者 mi
 */

public class FellHelpActivity extends VpActivity implements OnTouchListener {

	protected static final String TAG = "FellHelpActivity";
	public static final int REQUEST_FLAG = 100;
	public static final int REQUESTCODE = 205;
	private final String FILE_NAME = "FellHelpActivity";
	private FrameLayout mTopContainer;
	private FrameLayout mBootomContainer;
	private Gson gson = new Gson();
	private FellHelpTopHolder fellHelpTopHolder;
	private SharedPreferencesHelper sp;
	private ScrollView mScrollView;
	private boolean isScroll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		VpApplication.getInstance().payresult = false;
		VpApplication.getInstance().mPayBindViewBean = null;
		setContentView(R.layout.fell_help_activity);
		initPublicTitle();
		initView();
		initData();
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText("情感求助");
		mPubTitleView.mBtnRight.setText("我的求助");
		mPubTitleView.mBtnRight.setTextColor(getResources().getColor(R.color.green_public));
		mPubTitleView.mBtnRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 我的求助
				startActivity(new Intent(FellHelpActivity.this, MyFellHelpActivity.class));
			}
		});
		mPubTitleView.mBtnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (fellHelpTopHolder!= null && !fellHelpTopHolder.getContentIsEmpty()) {
					showTipDialog();
				}else{
					finish();
				}
			}
		});

		mTopContainer = (FrameLayout) findViewById(R.id.fell_help_top_ask_edit);
		mBootomContainer = (FrameLayout) findViewById(R.id.fell_help_bootom_list);
		mScrollView = (ScrollView) findViewById(R.id.fellhelp_scrollview);
		mScrollView.setOnTouchListener(this);
	}

	private void initData() {
		sp = SharedPreferencesHelper.getInstance(this);
		String readCache = CacheFileUtils.readCache(FILE_NAME);
		if (!TextUtils.isEmpty(readCache)) {
			setData(readCache);
		}
		
		if (MyUtils.isNetword(this)) {
			startHttp();
		}
	}

	private void startHttp() {
		mClient = new VpHttpClient(this);
		mClient.get(VpConstants.FELL_HELP_URL, new RequestParams(), new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String deAesResult = ResultParseUtil.deAesResult(responseBody);
				FellHelpBean fromJson = gson.fromJson(deAesResult, FellHelpBean.class);
				if (fromJson != null && fromJson.code == 0) {
					CacheFileUtils.writeCache(FILE_NAME, deAesResult);
					setData(deAesResult);
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
	
	private void setData(String readCache) {
		FellHelpBeanData data = gson.fromJson(readCache, FellHelpBean.class).data;
		if (data == null) {
			return;
		}
		// 情感求助顶部位置
		fellHelpTopHolder = new FellHelpTopHolder(this);
		fellHelpTopHolder.setData(data);
		mTopContainer.addView(fellHelpTopHolder.getView());
		fellHelpTopHolder.mFellImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectPohto();// 选择相片
			}
		});
		if (data.asked == null || data.asked.size() == 0) {
			//没有数据
			isScroll = true;
		}else{
			isScroll = false;
		}
		fellHelpBottomHolder = new FellHelpBottomHolder(this);
		fellHelpBottomHolder.setData(data);
		mBootomContainer.addView(fellHelpBottomHolder.getView());
	}
	

	/**
	 * 选择相片
	 * 
	 * void 
	 */
	public void selectPohto() {
		Intent intent = new Intent(this, MultiImageSelectorActivity.class);
		// 是否显示拍摄图片
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
		// 最大可选择图片数量
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 3);
		// 选择模式
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
		// 默认选择
		if (fellHelpTopHolder.mSelectPath != null && fellHelpTopHolder.mSelectPath.size() > 0) {
			intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, fellHelpTopHolder.mSelectPath);
		}
		startActivityForResult(intent, REQUEST_FLAG);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_FLAG && resultCode == Activity.RESULT_OK) {
			fellHelpTopHolder.mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
			fellHelpTopHolder.notifySelectImageView();// 刷新ui
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (VpApplication.getInstance().payresult && fellHelpTopHolder!=null) {
			fellHelpTopHolder.clearData();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		sp.removeKey("payResult");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			//判断输入框是否有内容
			if (fellHelpTopHolder!= null && !fellHelpTopHolder.getContentIsEmpty()) {
				showTipDialog();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private IOSActionSheetDialog addSheetItem;
	private FellHelpBottomHolder fellHelpBottomHolder;
	private void showTipDialog() {
		addSheetItem = new IOSActionSheetDialog(this).builder().setTitle("是否放弃情感求助").addSheetItem("确定", SheetItemColor.Green,new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				addSheetItem.setCancelable(true);
				if (fellHelpTopHolder!= null && !fellHelpTopHolder.getContentIsEmpty()) {
					fellHelpTopHolder.clearData();
				}
				finish();
			}
		} );
		addSheetItem.show();
	}

	// 浏览相片
	public void showPhoto(ArrayList<String> mSelectPath, int position) {
		Intent intent = new Intent(this, ShowImagesViewPagerActivity.class);
		intent.putStringArrayListExtra(ShowImagesViewPagerActivity.IMAGES,mSelectPath);
		intent.putExtra(ShowImagesViewPagerActivity.POSITION, position);
		startActivity(intent);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return isScroll;
	}
}
