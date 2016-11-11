package com.vp.loveu.my.fragment;

import java.util.List;

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
import com.vp.loveu.my.adapter.FollowHolder;
import com.vp.loveu.my.bean.FollowBean;
import com.vp.loveu.my.bean.FollowBean.FollowData;
import com.vp.loveu.my.listener.OnFollowUserListener;
import com.vp.loveu.util.LoginStatus;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月30日下午3:25:50
 * @功能 关注的fragment对象
 * @作者 mi
 */

public class FollowFragment extends VpFragment implements OnRefreshListener2<ListView> {

	private static final String FILE_NAME = "follow_fragment.text";
	private Context mContext;
	private PullToRefreshListView mListView;
	private VpHttpClient mClient;
	private List<FollowData> mData;
	private MyAdapter myAdapter;
	private Gson gson = new Gson();
	private OnFollowUserListener listener;
	private int pageCode = 1;// 分页页码
	private int limit = 10;

	public FollowFragment(OnFollowUserListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		mListView = new PullToRefreshListView(mContext);
		initData();
		return mListView;
	}

	private void initData() {
		mListView.setMode(Mode.BOTH);
		mListView.setBackgroundColor(getResources().getColor(R.color.bg_gray));
		ILoadingLayout up = mListView.getLoadingLayoutProxy(false, true);
		ILoadingLayout down = mListView.getLoadingLayoutProxy(true, false);
	 
		EmptyTextView tv = new EmptyTextView(mContext);
		tv.setWidth(MyUtils.getScreenWidthAndHeight(mContext)[0]);
		tv.setHeight(MyUtils.getScreenWidthAndHeight(mContext)[1]);
		tv.setGravity(Gravity.CENTER);
		tv.setText("你还没有任何关注的用户");
		mListView.getRefreshableView().setDividerHeight(1);
		mListView.setEmptyView(tv);
		mListView.setOnRefreshListener(this);

		String readCache = CacheFileUtils.readCache(FILE_NAME);
		if (readCache != null && !TextUtils.isEmpty(readCache)) {
			setData(readCache); // 走缓存
		}

		if (com.vp.loveu.index.myutils.MyUtils.isNetword(mContext)) {
			startHttp(pageCode);// 如果网络畅通 走网络
		}
	}

	private void startHttp(int page) {
		if (mClient == null) {
			mClient = new VpHttpClient(mContext);
		}
		RequestParams params = new RequestParams();
		params.put("page", page);
		params.put("limit", limit);
		params.put("id", LoginStatus.getLoginInfo().getUid());
		mClient.get(VpConstants.MY_ME_FOLLOW_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String deAesResult = ResultParseUtil.deAesResult(responseBody);
				FollowBean fromJson = gson.fromJson(deAesResult, FollowBean.class);
				if (fromJson.code == 0) {
					List<FollowData> data = fromJson.data;
					// 说明访问成功
					if (pageCode == 1) {
						CacheFileUtils.writeCache(FILE_NAME, deAesResult);
						if (mData == null) {
							setData(deAesResult);
						} else {
							mData.clear();
							mData.addAll(data);
							myAdapter.notifyDataSetChanged();
						}
					} else {
						if (data == null || data.size() <= 0) {
							Toast.makeText(mContext, R.string.not_more_data, Toast.LENGTH_SHORT).show();
						} else {
							mData.removeAll(data);
							mData.addAll(data);
							myAdapter.notifyDataSetChanged();
						}
					}
				} else {
					Toast.makeText(mContext, fromJson.msg, Toast.LENGTH_SHORT).show();
				}
				mListView.onRefreshComplete();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
				mListView.onRefreshComplete();
			}
		});
	}

	protected void setData(String deAesResult) {
		mData = gson.fromJson(deAesResult, FollowBean.class).data;
		if (myAdapter == null) {
			myAdapter = new MyAdapter(mContext, mListView.getRefreshableView(), mData);
			mListView.setAdapter(myAdapter);
		} else {
			myAdapter.notifyDataSetChanged();
		}
	}

	private class MyAdapter extends VpBaseAdapter<FollowData> {

		public MyAdapter(Context context, AbsListView listView, List<FollowData> data) {
			super(context, listView, data);
		}

		@Override
		public BaseHolder<FollowData> getHolder() {
			return new FollowHolder(mContext, listener);
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
	public void onDestroy() {
		super.onDestroy();
		if (mClient != null) {
			mClient.cancelAllRequests(true);
		}
	}

	public void addNotifyDataSetChanged(FollowData bean) {
		if (!mData.contains(bean)) {
			bean.isCancle = false;
			mData.add(0, bean);
		} else {
			for (int i = 0; i < mData.size(); i++) {
				if (mData.get(i).equals(bean)) {
					mData.get(i).isCancle = false;
				}
			}
		}
		myAdapter.notifyDataSetChanged();
	}

	public void cancleNotifyDatsetChanged(FollowData bean) {
		if (!mData.contains(bean)) {
		} else {
			for (int i = 0; i < mData.size(); i++) {
				if (mData.get(i).equals(bean)) {
					mData.get(i).isCancle = true;
				}
			}
		}
		myAdapter.notifyDataSetChanged();
	}
}
