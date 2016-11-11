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
import com.vp.loveu.index.adapter.HotUserHolder;
import com.vp.loveu.index.adapter.VpBaseAdapter;
import com.vp.loveu.index.bean.HotUserBean;
import com.vp.loveu.index.bean.HotUserBean.HotDataBean;
import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.index.widget.EmptyTextView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月20日上午11:33:54
 * @功能 活跃用户排行榜的activity
 * @作者 mi
 */

public class HotUserActivity extends VpActivity implements OnRefreshListener2<ListView> {

	private PullToRefreshListView mListView;
	private static final String FILE_NAME = "hot_user.text";
	private Gson gson = new Gson();
	private List<HotDataBean> mData;
	private MyAdapter myAdapter;
	private int mLimit = 10;
	private int mPage = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotuser_activity);
		initPublicTitle();
		initView();
		initData();
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mBtnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mPubTitleView.mTvTitle.setText("排行榜");
		mListView = (PullToRefreshListView) findViewById(R.id.hot_listview);
		mListView.setMode(Mode.BOTH);
		ILoadingLayout up = mListView.getLoadingLayoutProxy(false, true);
		ILoadingLayout down = mListView.getLoadingLayoutProxy(true, false);
		 
		EmptyTextView tv = new EmptyTextView(this);
		tv.setWidth(MyUtils.getScreenWidthAndHeight(this)[0]);
		tv.setHeight(MyUtils.getScreenWidthAndHeight(this)[1]);
		tv.setGravity(Gravity.CENTER);
		tv.setText("暂时没有用户排名哦");
		tv.setTextSize(14);
		mListView.setEmptyView(tv);
		mListView.setOnRefreshListener(this);
	}

	private void initData() {
		// 先走缓存
		String readCache = CacheFileUtils.readCache(FILE_NAME);
		if (readCache != null && !TextUtils.isEmpty(readCache)) {
			setData(readCache);
		}
		if (MyUtils.isNetword(this)) {
			// 如果网络畅通 走网络
			startHttp(mPage);
		}
	}

	private void startHttp(final int page) {
		if (mClient == null) {// http://api.loveu.com/api/v1/discover/exp_rank?limit={limit}&page={page}
			mClient = new VpHttpClient(this);
		}
		RequestParams params = new RequestParams();
		params.put("limit", mLimit);
		params.put("page", page);
		mClient.get(VpConstants.HOT_USER_SORT, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mListView.onRefreshComplete();
				String result = new String(responseBody);
				HotUserBean fromJson = gson.fromJson(result, HotUserBean.class);
				List<HotDataBean> data = gson.fromJson(result, HotUserBean.class).data;
				if (fromJson.code == 0) {
					if (page == 1) {
						if (mData == null) {
							CacheFileUtils.writeCache(FILE_NAME, result);
							setData(result);
						} else {
							mData.clear();
							mData.addAll(data);
							myAdapter.notifyDataSetChanged();
						}
					} else {
						if (data == null || data.size() <= 0) {
							Toast.makeText(getApplicationContext(), R.string.not_more_data, Toast.LENGTH_SHORT).show();
						} else {
							mData.addAll(data);
							myAdapter.notifyDataSetChanged();
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

	private void setData(String readCache) {
		HotUserBean fromJson = gson.fromJson(readCache, HotUserBean.class);
		mData = fromJson.data;
		if (myAdapter == null) {
			myAdapter = new MyAdapter(this, mListView.getRefreshableView(), mData);
			mListView.setAdapter(myAdapter);
		} else {
			myAdapter.notifyDataSetChanged();
		}
	}

	private class MyAdapter extends VpBaseAdapter<HotDataBean> {

		public MyAdapter(Context context, AbsListView listView, List<HotDataBean> data) {
			super(context, listView, data);
		}

		@Override
		public BaseHolder<HotDataBean> getHolder() {
			return new HotUserHolder(HotUserActivity.this);
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		mPage = 1;
		startHttp(mPage);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		startHttp(++mPage);
	}
}
