package com.vp.loveu.index.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.index.bean.FreeHelpBean;
import com.vp.loveu.index.holder.FellHelpTopHolder;
import com.vp.loveu.index.holder.FreeHelpBottomHolder;
import com.vp.loveu.index.holder.FreeHelpTopHolder;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月20日下午3:33:05
 * @功能 免费求助的activity
 * @作者 mi
 */

public class FreeHelpActivity extends VpActivity {
	
	private FrameLayout mFreeTopContainer;
	public FrameLayout mFreeBottomContainer;
	public FreeHelpTopHolder freeHelpTopHolder;
	private String content;
	private ArrayList<String> photoList;
	private int maxNum;
	private int src_id;
	private int userNum;
	private FreeHelpBottomHolder freeHelpBottomHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.free_help_activity);
		initPublicTitle();
		initView();
	}
	
	private void initView() {
		//获取数据
		Bundle bundle = getIntent().getBundleExtra(FellHelpTopHolder.KEY_FLAG_BUNDLE);
		content = bundle.getString(FellHelpTopHolder.KEY_FLAG_CONTENT);
		photoList = bundle.getStringArrayList(FellHelpTopHolder.KEY_FLAG_PHOTO);
		maxNum = bundle.getInt(FellHelpTopHolder.KEY_MAX_FLAG);
		src_id = bundle.getInt(FellHelpTopHolder.KEY_SRC_ID);
		userNum = bundle.getInt(FellHelpTopHolder.KEY_USER_NUM);
		FreeHelpBean bean = new FreeHelpBean();
		bean.content = content;
		bean.photoList = photoList;
		bean.maxNumb = maxNum;
		bean.src_id = src_id;
		bean.userNum = userNum;
		
		//初始化title
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mBtnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mPubTitleView.mTvTitle.setText("情感求助");
		mFreeTopContainer = (FrameLayout) findViewById(R.id.free_help_top_container);
		mFreeBottomContainer = (FrameLayout) findViewById(R.id.free_help_bottom_container);
		
		//顶部
		freeHelpTopHolder = new FreeHelpTopHolder(this);
		freeHelpTopHolder.setData(bean);
		mFreeTopContainer.addView(freeHelpTopHolder.getView());
		//顶部
		freeHelpBottomHolder = new FreeHelpBottomHolder(this);
		freeHelpBottomHolder.setData(bean);
		mFreeBottomContainer.addView(freeHelpBottomHolder.getView());
	}
	
	public void notifyDataSetChanged(int progress){
		Toast.makeText(this, "收到新的回复了", 0).show();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (freeHelpBottomHolder!=null) {
			freeHelpBottomHolder.onStart();
		}
	}
	@Override
	protected void onStop() {
		super.onStop();
		if (freeHelpBottomHolder!=null) {
			freeHelpBottomHolder.onStop();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (freeHelpBottomHolder!=null) {
			freeHelpBottomHolder.onDestroy();
		}
		if (freeHelpTopHolder != null) {
			freeHelpTopHolder.stopAnimation();
		}
	}
}
