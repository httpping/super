package com.vp.loveu.my.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.adapter.VpBaseAdapter;
import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.index.widget.EmptyTextView;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.my.adapter.WalletHolder;
import com.vp.loveu.my.bean.WalletBean;
import com.vp.loveu.my.bean.WalletBean.WalletDataBean;
import com.vp.loveu.my.widget.MyMoneyHead;
import com.vp.loveu.util.LoginStatus;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月1日上午10:35:37
 * @功能 我的钱包
 * @作者 mi
 */

public class WalletActivity extends VpActivity implements OnRefreshListener2<ListView> {

	private PullToRefreshListView mListView;
	private final String FILE_NAME = "wallet";
	private int pageCode = 1;
	private int limit = 10;
	private Gson gson = new Gson();
	private ArrayList<WalletDataBean> mData;
	private MyMoneyHead mHeadView;
	private MyAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wallet_activity);
		initPublicTitle();
		initView();
		initData();
	}

	private void setEmptyView(){
		EmptyTextView tv = new EmptyTextView(this);
		tv.setWidth(MyUtils.getScreenWidthAndHeight(this)[0]);
		tv.setHeight(MyUtils.getScreenWidthAndHeight(this)[1]);
		tv.setGravity(Gravity.CENTER);
		tv.setText("你还没有任何记录");
		mListView.setEmptyView(tv);
	}
	
	private void initData() {
		mListView.setMode(Mode.BOTH);
		mListView.setOnRefreshListener(this);
		setEmptyView();

		String readCache = CacheFileUtils.readCache(FILE_NAME);
		if (!TextUtils.isEmpty(readCache)) {
			setData(readCache);
		}
		if (MyUtils.isNetword(this)) {
			startHttp(pageCode);
		}
	}

	private void startHttp(int page) {
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		RequestParams params = new RequestParams();
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		params.put("id", loginInfo.getUid());
		params.put("limit", limit);
		params.put("page", page);
		mClient.get(VpConstants.MY_CENTER_PAY, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mListView.onRefreshComplete();
				String result = ResultParseUtil.deAesResult(responseBody);
				WalletBean fromJson = gson.fromJson(result, WalletBean.class);
				if (fromJson.code == 0) {
					ArrayList<WalletDataBean> data = WalletBean.parserJson(fromJson.data);
					if (pageCode == 1) {
						// 写入缓存
						CacheFileUtils.writeCache(FILE_NAME, result);
						setData(result);
					} else {
						if (data == null || data.size() <= 0) {
							Toast.makeText(getApplicationContext(), R.string.not_more_data, Toast.LENGTH_SHORT).show();
						} else {
							mData.addAll(data);
							mAdapter.notifyDataSetChanged();
						}
					}

				} else {
					Toast.makeText(getApplicationContext(), fromJson.msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mListView.onRefreshComplete();
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected void setData(String result) {
		WalletBean fromJson = gson.fromJson(result, WalletBean.class);
		mData = WalletBean.parserJson(fromJson.data);

		if (mData == null || mData.size() == 0) {
			mHeadView.setMoney(0.00);
			//数据为空 需要重置
			return;
		}

		double balance = mData.get(0).balance;
		mHeadView.setMoney(balance);
		

		if (mAdapter == null) {
			mAdapter = new MyAdapter(this, mListView.getRefreshableView(), mData);
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText("我的钱包");
		mPubTitleView.mBtnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mListView = (PullToRefreshListView) findViewById(R.id.mycenter_pay_pulltorefresh_listview);
		mHeadView = new MyMoneyHead(this);
		mListView.getRefreshableView().addHeaderView(mHeadView);
		mListView.setOnRefreshListener(this);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		startHttp(pageCode = 1);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		startHttp(++pageCode);
	}

	private class MyAdapter extends VpBaseAdapter<WalletDataBean> {

		public MyAdapter(Context context, AbsListView listView, List<WalletDataBean> data) {
			super(context, listView, data);
		}

		@Override
		public BaseHolder<WalletDataBean> getHolder() {
			return new WalletHolder(WalletActivity.this);
		}
	}
}
