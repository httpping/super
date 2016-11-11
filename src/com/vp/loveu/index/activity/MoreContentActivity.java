package com.vp.loveu.index.activity;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

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
import com.vp.loveu.index.adapter.MoreContentHolder;
import com.vp.loveu.index.adapter.VpBaseAdapter;
import com.vp.loveu.index.bean.IndexBean.IndexArtBean;
import com.vp.loveu.index.bean.MoreContentBean;
import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.index.holder.IndexNavigationHolder;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.index.widget.EmptyTextView;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月18日下午3:50:57
 * @功能 更多精选常文的activity
 * @作者 mi
 */

public class MoreContentActivity extends VpActivity implements OnRefreshListener2<ListView> {

	protected static final String TAG = "MoreContentActivity";
	private static final String FILE_NAME = "moreArt.text";
	private PullToRefreshListView mListView;
	private List<IndexArtBean> mDatas;
	private int pageCode = 1;// 默认是第一页
	private int id = 0;
	private int limit = 10;
	private MyAdapter mAdapter;
	private Gson gson = new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_content_activity);
		initPublicTitle();
		initView();
		initData();
	}

	private void initData() {
		String readCache = CacheFileUtils.readCache(FILE_NAME);
		if (readCache != null && !TextUtils.isEmpty(readCache)) {
			// 走缓存
			setData(readCache);
		}

		if (MyUtils.isNetword(this)) {
			startHttp(pageCode);// 如果网络畅通走网络
		}
	}

	private void setData(String readCache) {
		MoreContentBean fromJson = gson.fromJson(readCache, MoreContentBean.class);
		mDatas = fromJson.data;
		if (mAdapter == null) {
			mAdapter = new MyAdapter(this, mListView.getRefreshableView(), mDatas);
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

	private void initView() {
		int intExtra = getIntent().getIntExtra(IndexNavigationHolder.KEY_FLAG, 1);
		String title = getIntent().getStringExtra("title");
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText(intExtra == 1 ? "精选长文" : title);
		mListView = (PullToRefreshListView) findViewById(R.id.more_listview);
		mListView.setMode(Mode.BOTH);
		mListView.getRefreshableView().setDividerHeight(0);
		ILoadingLayout up = mListView.getLoadingLayoutProxy(false, true);
		ILoadingLayout down = mListView.getLoadingLayoutProxy(true, false);
 
		EmptyTextView tv = new EmptyTextView(this);
		tv.setWidth(MyUtils.getScreenWidthAndHeight(this)[0]);
		tv.setHeight(MyUtils.getScreenWidthAndHeight(this)[1]);
		tv.setGravity(Gravity.CENTER);
		tv.setText("没有更多内容");
		mListView.setEmptyView(tv);
		mListView.setOnRefreshListener(this);
	}

	private class MyAdapter extends VpBaseAdapter<IndexArtBean> {

		public MyAdapter(Context context, AbsListView listView, List<IndexArtBean> data) {
			super(context, listView, data);
		}

		@Override
		public BaseHolder<IndexArtBean> getHolder() {
			return new MoreContentHolder(MoreContentActivity.this);
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

	private void startHttp(final int pages) {
		if (mClient == null) {
			mClient = new VpHttpClient(this);
			mClient.setShowProgressDialog(false);
		}
		// http://api.loveu.com/api/v1/news/list/{id[1、修改接口名称2、增加id的请求参数]}?page={page}&limit={limit}
		RequestParams params = new RequestParams();
		params.put("id", id);
		params.put("page", pages);
		params.put("limit", limit);
		mClient.get(VpConstants.MORE_CONTENT_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = new String(responseBody);
				MoreContentBean fromJson = gson.fromJson(result, MoreContentBean.class);
				if (fromJson.code == 0) {
					List<IndexArtBean> data2 = fromJson.data;
					if (pageCode == 1) {
						CacheFileUtils.writeCache(FILE_NAME, result);
						// 下拉刷新 重新缓存数据 并清除list集合 notifidatasetchanged page置为1
						if (mDatas == null) {
							setData(result);
						} else {
							mDatas.clear();
							mDatas.addAll(data2);
							mAdapter.notifyDataSetChanged();
						}
					} else {
						// 上拉加载
						if (data2 == null || data2.size() <= 0) {
							Toast.makeText(getApplicationContext(), R.string.not_more_data, Toast.LENGTH_SHORT).show();
						} else {
							mDatas.addAll(data2);
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
}
