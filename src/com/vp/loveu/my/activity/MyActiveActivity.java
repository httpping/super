package com.vp.loveu.my.activity;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.vp.loveu.index.adapter.VpBaseAdapter;
import com.vp.loveu.index.bean.CityActiveBean.ActBean;
import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.index.widget.EmptyTextView;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.my.adapter.MyActiveHolder;
import com.vp.loveu.my.bean.MyActiveBean;
import com.vp.loveu.util.LoginStatus;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月9日上午11:07:57
 * @功能 我的活动列表接口
 * @作者 mi
 */

public class MyActiveActivity extends VpActivity implements OnRefreshListener2<ListView> {

	private PullToRefreshListView mListView;
	private Gson gson = new Gson();
	private int limit = 10;
	private int pageCode = 1;// 分页页码
	private static final String FILE_NAME = "my_active.text";
	private List<ActBean> mDatas;
	private MyAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myactive_activie);
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
		mPubTitleView.mTvTitle.setText("我的活动");
		mListView = (PullToRefreshListView) findViewById(R.id.my_active_listview);
	}

	private void initData() {
		mListView.setMode(Mode.BOTH);
		ILoadingLayout up = mListView.getLoadingLayoutProxy(false, true);
		ILoadingLayout down = mListView.getLoadingLayoutProxy(true, false);
		EmptyTextView tv = new EmptyTextView(this);
		tv.setWidth(MyUtils.getScreenWidthAndHeight(this)[0]);
		tv.setHeight(MyUtils.getScreenWidthAndHeight(this)[1]);
		tv.setGravity(Gravity.CENTER);
		tv.setText("你还没有任何活动");
		mListView.setEmptyView(tv);
		mListView.setOnRefreshListener(this);

		String readCache = CacheFileUtils.readCache(FILE_NAME);
		if (readCache != null && !TextUtils.isEmpty(readCache)) {
			setData(readCache);// 走缓存
		}
		if (MyUtils.isNetword(this)) {
			startHttp(pageCode);// 走网络
		}
	}

	private void setData(String readCache) {
		mDatas = gson.fromJson(readCache, MyActiveBean.class).data;
		if (myAdapter == null) {
			myAdapter = new MyAdapter(this, mListView.getRefreshableView(), mDatas);
			mListView.setAdapter(myAdapter);
		} else {
			myAdapter.notifyDataSetChanged();
		}
	}

	private class MyAdapter extends VpBaseAdapter<ActBean> {

		public MyAdapter(Context context, AbsListView listView, List<ActBean> data) {
			super(context, listView, data);
		}

		@Override
		public BaseHolder<ActBean> getHolder() {
			return new MyActiveHolder(MyActiveActivity.this);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageCode = 1;
		startHttp(pageCode);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// 上拉加载
		startHttp(++pageCode);
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
		mClient.get(VpConstants.MY_ACTIVE_LIST_RUL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = new String(responseBody);
				MyActiveBean fromJson = gson.fromJson(result, MyActiveBean.class);
				if (fromJson.code == 0) {
					// 访问成功
					List<ActBean> data = fromJson.data;
					if (pageCode == 1) {
						CacheFileUtils.writeCache(FILE_NAME, result);
						if (mDatas != null) {
							mDatas.clear();
							mDatas.addAll(data);
							myAdapter.notifyDataSetChanged();
						} else {
							setData(result);
						}
					} else {
						// 加载更多了
						if (data == null || data.size() <= 0) {
							Toast.makeText(getApplicationContext(), R.string.not_more_data, Toast.LENGTH_SHORT).show();
						} else {
							mDatas.addAll(data);
							myAdapter.notifyDataSetChanged();
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
