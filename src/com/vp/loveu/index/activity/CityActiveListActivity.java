package com.vp.loveu.index.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.adapter.CityListAdapter;
import com.vp.loveu.index.bean.CityActiveBean;
import com.vp.loveu.index.bean.CityActiveBean.ActBean;
import com.vp.loveu.index.bean.CityActiveBean.DataBean;
import com.vp.loveu.index.bean.CityActiveBean.InProgress;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.index.widget.EmptyTextView;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.LoginStatus;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月30日上午10:17:23
 * @功能 同城活动list备用
 * @作者 mi
 */

public class CityActiveListActivity extends VpActivity implements Runnable, OnRefreshListener2<ListView> {

	private final String FILE_NAME = "CityActiveListActivity";
	private PullToRefreshListView mListView;
	public static final String KEY = "is_show_history";
	private Gson gson = new Gson();
	private boolean isShowHistoryActive;//是否显示历史活动
	private CityListAdapter mAdapter;
	private CityActiveBean data;
	private ArrayList<ActBean> mListDatas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cityactivelist_layout);
		isShowHistoryActive = getIntent().getBooleanExtra(KEY, true);
		initPublicTitle();
		initView();
		initData();
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText("同城活动");
		mPubTitleView.mBtnRight.setText("深圳");
		mListView = (PullToRefreshListView) findViewById(R.id.citylist_listview);
		EmptyTextView tv = new EmptyTextView(this);
		tv.setWidth(MyUtils.getScreenWidthAndHeight(this)[0]);
		tv.setHeight(MyUtils.getScreenWidthAndHeight(this)[1]);
		tv.setGravity(Gravity.CENTER);
		tv.setText("暂时没有任何活动");
		mListView.setEmptyView(tv);
		mListView.setOnRefreshListener(this);
	}

	private void initData() {
		// 先走缓存后走网络
		String readCache = CacheFileUtils.readCache(FILE_NAME);
		if (!TextUtils.isEmpty(readCache)) {
			setData(readCache);
		}

		if (MyUtils.isNetword(this)) {
			startHttp();
		}
	}

	private void startHttp() {
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		RequestParams params = new RequestParams();
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo != null && !TextUtils.isEmpty(loginInfo.getAdCode())) {
			params.put("area_code", loginInfo.getAdCode());
		} else {
			params.put("area_code", 4403 + "");
		}

		mClient.get(VpConstants.CITY_ACTIVE_LIST_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mListView.onRefreshComplete();
				String deAesResult = ResultParseUtil.deAesResult(responseBody);
				CityActiveBean fromJson = gson.fromJson(deAesResult, CityActiveBean.class);
				if (fromJson.code == 0) {
					// 说明访问成功
					CacheFileUtils.writeCache(FILE_NAME, deAesResult);
					setData(deAesResult);
				} else {
					Toast.makeText(CityActiveListActivity.this, fromJson.msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mListView.onRefreshComplete();
				Toast.makeText(CityActiveListActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void setData(String readCache) {
		data = gson.fromJson(readCache, CityActiveBean.class);
		new Thread(this).start();
	}

	@Override
	public void run() {
		mListDatas = sortListData(data);
		runOnUiThread(new Runnable() {
			public void run() {
				synchronized (CityActiveListActivity.class) {
					if (mListDatas == null) {
						return;
					}
					if (mAdapter == null) {
						mAdapter = new CityListAdapter(CityActiveListActivity.this, mListView.getRefreshableView(), mListDatas,isShowHistoryActive);
						mListView.setAdapter(mAdapter);
					}else{
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		});
	}

	/**
	 * 对数据进行分类
	 * @param data
	 * @return CityActiveBean
	 */
	private ArrayList<ActBean> sortListData(CityActiveBean data) {
		ArrayList<ActBean> newList = new ArrayList<ActBean>();
		while (true) {
			if (data != null && data.data != null) {
				DataBean tempData = data.data;

				if (tempData.in_progress != null) {

					InProgress in_progress = tempData.in_progress;
					if (in_progress != null && in_progress.activites != null) {
						for (int i = 0; i < in_progress.activites.size(); i++) {
							ActBean bean = in_progress.activites.get(i);
							bean.type = 1;
							newList.add(bean);
						}
						in_progress.activites = null;
					} else if (in_progress != null && in_progress.users != null) {
						ActBean actBean = new ActBean();
						actBean.users = new ArrayList<>();
						actBean.type = 2;
						for (int i = 0; i < in_progress.users.size(); i++) {
							ActBean bean = in_progress.users.get(i);
							bean.num = in_progress.num;
							actBean.users.add(bean);
						}
						newList.add(actBean);
						if (!isShowHistoryActive) {
							break;//如果不显示历史活动的话 直接跳出即可
						}
						tempData.in_progress = null;
					}
				} else if (tempData.history != null) {
					List<ActBean> history = tempData.history;
					for (int i = 0; i < history.size(); i += 2) {

						List<ActBean> list = new ArrayList<ActBean>();
						try {
							list.add(history.get(i));
							list.add(history.get(i + 1));
						} catch (Exception e) {
							e.printStackTrace();
							list.add(null);
						}
						ActBean actBean2 = new ActBean();
						actBean2.type = 3;
						actBean2.historyList = list;
						newList.add(actBean2);
					}
					break;
				} else {
					break;
				}
			} else {
				break;
			}
		}
		return newList;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		startHttp();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		
	}
}
