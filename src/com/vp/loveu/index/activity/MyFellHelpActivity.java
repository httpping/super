package com.vp.loveu.index.activity;

import java.util.List;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.adapter.MyFellHelpHolder;
import com.vp.loveu.index.adapter.VpBaseAdapter;
import com.vp.loveu.index.bean.MySeekHelpBean;
import com.vp.loveu.index.bean.MySeekHelpBean.MySeekDataBean;
import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.index.widget.EmptyTextView;
import com.vp.loveu.index.widget.FreeHelpBottomReplyRelativiLayout;
import com.vp.loveu.index.widget.RecoderFrameLayout;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.LoginStatus;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月23日上午11:33:16
 * @功能 我的情感求助
 * @作者 mi
 */

public class MyFellHelpActivity extends VpActivity implements OnRefreshListener2<ListView> {

	private static final String FILE_NAME = "fell_list.text";
	private PullToRefreshListView mListView;
	private Gson gson = new Gson();
	private int pageCode = 1;// 默认为1
	private int limit = 10;// 多少条
	private List<MySeekDataBean> mDatas;
	private MyAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_fell_help_activity);
		initPublicTitle();
		initView();
		initData();
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mBtnRight.setText("");
		mPubTitleView.mTvTitle.setText("我的求助");
		mListView = (PullToRefreshListView) findViewById(R.id.my_fell_help_listview);
		mListView.setMode(Mode.BOTH);
		ILoadingLayout up = mListView.getLoadingLayoutProxy(false, true);
		ILoadingLayout down = mListView.getLoadingLayoutProxy(true, false);
 
		EmptyTextView tv = new EmptyTextView(this);
		tv.setWidth(MyUtils.getScreenWidthAndHeight(this)[0]);
		tv.setHeight(MyUtils.getScreenWidthAndHeight(this)[1]);
		tv.setGravity(Gravity.CENTER);
		tv.setText("您还没有任何求助信息哦");
		mListView.setEmptyView(tv);
		mListView.setOnRefreshListener(this);
	}

	private void initData() {
		String readCache = CacheFileUtils.readCache(FILE_NAME);
		if (readCache != null && !TextUtils.isEmpty(readCache)) {
			setData(readCache);// 先走缓存
		}

		if (MyUtils.isNetword(this)) {
			// 如果网络畅通走网络
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
		mClient.get(VpConstants.MY_FELL_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = new String(responseBody);
				MySeekHelpBean fromJson = gson.fromJson(result, MySeekHelpBean.class);
				if (fromJson.code == 0) {
					// 网络访问成功
					if (pageCode == 1) {
						CacheFileUtils.writeCache(FILE_NAME, result);
						if (mDatas == null) {
							setData(result);
						} else {
							mDatas.clear();
							mDatas.addAll(fromJson.data);
							mAdapter.notifyDataSetChanged();
						}
					} else {
						// 加载更多
						if (fromJson.data == null || fromJson.data.size() <= 0) {
							Toast.makeText(getApplicationContext(), R.string.not_more_data, Toast.LENGTH_SHORT).show();
						} else {
							mDatas.addAll(fromJson.data);
							mAdapter.notifyDataSetChanged();
						}
					}
				} else {
					Toast.makeText(getApplicationContext(), fromJson.msg, Toast.LENGTH_SHORT).show();
				}
				mListView.onRefreshComplete();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
				mListView.onRefreshComplete();
			}
		});
	}

	private void setData(String readCache) {
		mDatas = gson.fromJson(readCache, MySeekHelpBean.class).data;
		if (mAdapter == null) {
			mAdapter = new MyAdapter(this, mListView.getRefreshableView(), mDatas);
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if (mAdapter!=null) {
				MyFellHelpHolder holder = (MyFellHelpHolder) mAdapter.getHolder();
				FreeHelpBottomReplyRelativiLayout holderItem = holder.getHolderItem();
				if (holderItem!= null) {
					RecoderFrameLayout recoderItem = holderItem.getRecoderItem();
					if (recoderItem!=null && recoderItem.playerRecoderFrameLayout != null) {
						recoderItem.playerRecoderFrameLayout.mediaPlayer.stopPlayer();
						recoderItem.playerRecoderFrameLayout =null;
					}
				}
			}
		} catch (Exception e) {
		}
	}

	private class MyAdapter extends VpBaseAdapter<MySeekDataBean> {

		public MyAdapter(Context context, AbsListView listView, List<MySeekDataBean> data) {
			super(context, listView, data);
		}

		@Override
		public BaseHolder<MySeekDataBean> getHolder() {
			return new MyFellHelpHolder(MyFellHelpActivity.this);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageCode = 1;
		startHttp(pageCode);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		startHttp(++pageCode);
	}
}
