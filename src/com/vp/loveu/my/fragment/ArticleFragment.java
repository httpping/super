package com.vp.loveu.my.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpFragment;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.adapter.VpBaseAdapter;
import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.index.widget.EmptyTextView;
import com.vp.loveu.my.adapter.ArticleHolder;
import com.vp.loveu.my.bean.ArticleBean;
import com.vp.loveu.my.bean.ArticleBean.ArticleDataBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.SharedPreferencesHelper;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月4日下午2:49:11
 * @功能 收藏文章的fragment界面
 * @作者 mi
 */

public class ArticleFragment extends VpFragment implements OnRefreshListener2<ListView> {

	private static final String FILE_NAME = "article_collect.text";
	private PullToRefreshListView mListView;
	private Context mContext;
	private Gson gson = new Gson();
	private List<ArticleDataBean> mData;
	private VpHttpClient mClient;
	public static final String KEY = "articleKey_uid";
	private int pageCode = 1;
	private int limit = 10;
	private MyAdapter mAdapter;
	private SharedPreferencesHelper sp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		mListView = new PullToRefreshListView(mContext);
		mListView.getRefreshableView().setDividerHeight(0);
		initData();
		return mListView;
	}

	private void initData() {
		mListView.setBackgroundColor(getResources().getColor(R.color.bg_gray));
		mListView.setMode(Mode.BOTH);
		ILoadingLayout up = mListView.getLoadingLayoutProxy(false, true);
		ILoadingLayout down = mListView.getLoadingLayoutProxy(true, false);
		 
		EmptyTextView tv = new EmptyTextView(mContext);
		tv.setWidth(MyUtils.getScreenWidthAndHeight(mContext)[0]);
		tv.setHeight(MyUtils.getScreenWidthAndHeight(mContext)[1]);
		tv.setGravity(Gravity.CENTER);
		tv.setText("你还没有收藏任何文章");
		tv.setTextSize(14);
		mListView.setEmptyView(tv);
		mListView.setOnRefreshListener(this);
		sp = SharedPreferencesHelper.getInstance(mContext);

		String readCache = CacheFileUtils.readCache(FILE_NAME);
		if (readCache != null && !TextUtils.isEmpty(readCache)) {
			setData(readCache);// 走缓存
		}
		if (com.vp.loveu.index.myutils.MyUtils.isNetword(mContext)) {
			startHttp(pageCode);// 走网络
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sp.removeKey(KEY);
	}

	private void startHttp(int page) {
		if (mClient == null) {
			mClient = new VpHttpClient(mContext);
		}
		RequestParams params = new RequestParams();
		params.put("page", page);
		params.put("limit", limit);
		params.put("type", 1);// 1是文章 2是动态
		mClient.get(VpConstants.COLLECT_ARTICLE_URL + "/" + LoginStatus.getLoginInfo().getUid(), params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mListView.onRefreshComplete();
				String result = ResultParseUtil.deAesResult(responseBody);
				ArticleBean fromJson = gson.fromJson(result, ArticleBean.class);
				ArrayList<ArticleDataBean> list = new ArrayList<ArticleDataBean>();
				try {
					JSONArray jsonArray = new JSONArray(fromJson.data);
					for (int i = 0; i < jsonArray.length(); i++) {
						String string = jsonArray.getString(i);
						ArticleDataBean fromJson2 = gson.fromJson(string, ArticleDataBean.class);
						list.add(fromJson2);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (fromJson.code == 0) {
					if (pageCode == 1) {
						CacheFileUtils.writeCache(FILE_NAME, result);
						if (mData == null) {
							setData(result);
						} else {
							mData.clear();
							mData.addAll(list);
							mAdapter.notifyDataSetChanged();
						}
					} else {
						if (list == null || list.size() <= 0) {
							Toast.makeText(mContext, R.string.not_more_data, Toast.LENGTH_SHORT).show();
						} else {
							mData.addAll(list);
							mAdapter.notifyDataSetChanged();
						}
					}
				} else {
					Toast.makeText(mContext, fromJson.msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mListView.onRefreshComplete();
				Toast.makeText(mContext, R.string.network_error, 0).show();
			}
		});
	}

	private void setData(String readCache) {
		ArticleBean fromJson = gson.fromJson(readCache, ArticleBean.class);
		try {
			JSONArray jsonArray = new JSONArray(fromJson.data);
			for (int i = 0; i < jsonArray.length(); i++) {
				String string = jsonArray.getString(i);
				ArticleDataBean fromJson2 = gson.fromJson(string, ArticleDataBean.class);
				if (mData == null) {
					mData = new ArrayList<>();
				}
				mData.add(fromJson2);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (mAdapter == null) {
			mAdapter = new MyAdapter(mContext, mListView.getRefreshableView(), mData);
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

	private class MyAdapter extends VpBaseAdapter<ArticleDataBean> {

		public MyAdapter(Context context, AbsListView listView, List<ArticleDataBean> data) {
			super(context, listView, data);
		}

		@Override
		public BaseHolder<ArticleDataBean> getHolder() {
			return new ArticleHolder(mContext);
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

	@Override
	public void onStart() {
		super.onStart();
		int uid = sp.getIntegerValue(KEY);
		/*
		 * if (uid != 0) { ArticleDataBean articleDataBean = new
		 * ArticleBean().new ArticleDataBean(); articleDataBean.target_id = uid;
		 * mData.remove(articleDataBean); mAdapter.notifyDataSetChanged(); }
		 */
	}
}
