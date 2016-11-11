package com.vp.loveu.index.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.base.VpFragment;
import com.vp.loveu.bean.InwardAction;
import com.vp.loveu.bean.MapLoactionBean;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.bean.IndexBean;
import com.vp.loveu.index.bean.IndexBean.IndexDataBean;
import com.vp.loveu.index.bean.IndexHotBean;
import com.vp.loveu.index.bean.IndexHotBean.IndexHotDataBean;
import com.vp.loveu.index.holder.IndexActiveHolder;
import com.vp.loveu.index.holder.IndexHotHolder;
import com.vp.loveu.index.holder.IndexNavigationHolder;
import com.vp.loveu.index.holder.IndexVideoHolder;
import com.vp.loveu.index.holder.IndexViewPagerHolder;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.MapLocationNetwork;
import com.vp.loveu.util.VPLog;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称 loveu
 * @时间2015年11月6日下午3:45:29
 * @功能 index页面
 * @作者 mi
 */

public class IndexFragment extends VpFragment {

	public static final String TAG = "IndexFragment";
	public static final String FILE_NAME = "index_fragment.txt";
	private final static String INDEX_CACHE = "indexcache.txt";
	private final static String INDEX_HOT_CACHE = "indexcachehot.txt";

	private View mView;
	private Context mContext;
	private FrameLayout mViewPagerContainer;
	private FrameLayout mNavigationContainer;
	private FrameLayout mActiveContainer;
	private FrameLayout mVideoContainer;
	private FrameLayout mHotContainer;
	private VpHttpClient client;
	private IndexDataBean mData;
	public Gson gson = new Gson();
	private IndexViewPagerHolder banner;
	private IndexNavigationHolder navigation;
	private IndexActiveHolder active;
	private IndexVideoHolder video;
	private IndexHotHolder hot;
	private List<String> modelOrder;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.index_fragment, null);
		mContext = getActivity();
		initView();
		initData();
		return mView;
	};

	
	private FrameLayout[] views = new FrameLayout[5];
	private void initView() {
		mViewPagerContainer = (FrameLayout) mView.findViewById(R.id.index_viewpager_container);
		mNavigationContainer = (FrameLayout) mView.findViewById(R.id.index_method_navigation);
		mActiveContainer = (FrameLayout) mView.findViewById(R.id.index_active_shows);
		mVideoContainer = (FrameLayout) mView.findViewById(R.id.index_video_container);
		mHotContainer = (FrameLayout) mView.findViewById(R.id.index_hot_user_container);
	
		views[0] = mViewPagerContainer;
		views[1] = mNavigationContainer;
		views[2] = mActiveContainer;
		views[3] = mVideoContainer;
		views[4] = mHotContainer;
	}

	private void initData() {

		String readCache = CacheFileUtils.readCache(FILE_NAME);
		if (!TextUtils.isEmpty(readCache)) {
			setData(readCache);// 先走缓存
		} else {
			// 缓存为空 用假数据
			String cacheJsonFromLocal = CacheFileUtils.getCacheJsonFromLocal(getActivity(), INDEX_CACHE);
			setData(cacheJsonFromLocal);
		}

		String readCacheHot = CacheFileUtils.readCache(FILE_NAME + "hot");
		if (!TextUtils.isEmpty(readCacheHot)) {
			setHotData(readCacheHot);// 先走缓存
		} else {
			// 缓存为空 用假数据
			String cacheJsonFromLocal = CacheFileUtils.getCacheJsonFromLocal(getActivity(), INDEX_HOT_CACHE);
			setHotData(cacheJsonFromLocal);
		}

		if (MyUtils.isNetword(mContext)) {
			startHttp();// 走网络
			startHotHttp();// 推荐用户接口
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MapLocationNetwork.MAP_RESULT_WHAT:
				try {
					MapLoactionBean mLoactionBean = (MapLoactionBean) msg.obj;
					VpApplication.getInstance().getUser().setLat((float) mLoactionBean.lat);
					VpApplication.getInstance().getUser().setLng((float) mLoactionBean.lon);
					VpApplication.getInstance().getUser().setAdCode(mLoactionBean.adCode);
				} catch (Exception e) {
				}
				break;

			default:
				break;
			}
		};
	};

	private void startHotHttp() {
		if (client == null) {
			client = new VpHttpClient(mContext);
			client.setShowProgressDialog(false);
		}
		// http://api.loveu.com/api/v1/home/index_user?area_code={area_code}&lat={lat}&lng={lng}&uid={uid}
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		// 获取经纬度
		new MapLocationNetwork(mHandler, getActivity());
		RequestParams params = new RequestParams();
		params.put("area_code", TextUtils.isEmpty(loginInfo.getAdCode()) ? "4403" : loginInfo.getAdCode());
		params.put("lat", loginInfo.lat);// 维度
		params.put("lng", loginInfo.lng);// 精度
		params.put("uid", loginInfo.getUid());// uid
		client.get(VpConstants.INDEX_HOT_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				IndexHotBean fromJson = gson.fromJson(result, IndexHotBean.class);
				if (fromJson != null && fromJson.code == 0) {
					CacheFileUtils.writeCache(FILE_NAME + "hot", result);// 写入缓存
					setHotData(result);
				} else {
					Toast.makeText(getActivity(), fromJson.msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
			}
		});
	}

	private List<IndexHotDataBean> hotData;

	/**
	 * 加载hot数据
	 * @param data
	 *            void
	 */
	protected void setHotData(String json) {
		IndexHotBean fromJson = gson.fromJson(json, IndexHotBean.class);
		if (fromJson != null && fromJson.code == 0) {
			hotData = fromJson.data;
		}
		// 推荐用户
		if (hotData != null) {
			views[users].removeAllViews();
			hot = new IndexHotHolder(mContext);
			views[users].addView(hot.getView());
			hot.setData(hotData);
			views[users].setVisibility(View.VISIBLE);
		} else {
			views[users].removeAllViews();
			views[users].setVisibility(View.GONE);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		if (hot != null) {
			hot.onStart(loginInfo);
		}
		if (active != null) {
			active.onStart(loginInfo);
		}
	}

	private void startHttp() {
		if (client == null) {
			client = new VpHttpClient(mContext);
			client.setShowProgressDialog(false);
		}
		RequestParams params = new RequestParams();
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		params.put("uid", loginInfo.getUid());
		params.put("device_type", 1);
		client.get(VpConstants.INDEX_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = new String(responseBody);
				try {
					IndexBean fromJson = gson.fromJson(result, IndexBean.class);
					if (fromJson.code == 0) {
						// 说明数据访问成功 --- 缓存
						FileOutputStream fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory(),"aa.txt"));
						fileOutputStream.write(result.getBytes());
						fileOutputStream.close();
						CacheFileUtils.writeCache(FILE_NAME, new String(responseBody));
						setData(result);
					} else {
						Toast.makeText(getActivity(), fromJson.msg, Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}

	
	int focus = 0; //默认是正常顺序
	int services =1;
	int activities = 2;
	int articles =3;
	int users =4;
	
	private void setData(String json) {
		IndexBean fromJson = gson.fromJson(json, IndexBean.class);
		mData = fromJson.data;
		if (mData == null) {
			return;
		}

		// 模块执行数序
		modelOrder = mData.order;
		if (modelOrder!= null && modelOrder.size() > 0) {
			focus = modelOrder.indexOf("focus");
			services = modelOrder.indexOf("services");
			articles = modelOrder.indexOf("articles");
			activities =modelOrder.indexOf("activities");
			users =modelOrder.indexOf("users");
		}
		
		// 轮播图
		if (mData.focus != null && mData.focus.size() > 0) {
			views[focus].removeAllViews();
			if (banner != null) {
				banner.handlerCancleAll();
			}
			banner = new IndexViewPagerHolder(mContext);
			View viewpager = banner.getView();
			views[focus].addView(viewpager);
			banner.setData(mData.focus);
			views[focus].setVisibility(View.VISIBLE);
		} else {
			views[focus].removeAllViews();
			views[focus].setVisibility(View.GONE);
		}
		// 业务导航
		if (mData.services != null && mData.services.size() > 0) {
			views[services].removeAllViews();
			navigation = new IndexNavigationHolder(mContext);
			View navigationView = navigation.getView();
			views[services].addView(navigationView);
			navigation.setData(mData.services);
			views[services].setVisibility(View.VISIBLE);
		} else {
			views[services].removeAllViews();
			views[services].setVisibility(View.GONE);
		}
		// 推荐的活动
		if (mData.activities != null && mData.activities.size() > 0) {
			views[activities].removeAllViews();
			active = new IndexActiveHolder(mContext);
			views[activities].addView(active.getView());
			active.setData(mData.activities);
			views[activities].setVisibility(View.VISIBLE);
			active.startProgress();
		} else {
			views[activities].removeAllViews();
			views[activities].setVisibility(View.GONE);
		}
		// 精选长文
		if (mData.articles != null && mData.articles.size() > 0) {
			views[articles].removeAllViews();
			video = new IndexVideoHolder(mContext);
			views[articles].addView(video.getView());
			video.setData(mData.articles);
			views[articles].setVisibility(View.VISIBLE);
		} else {
			views[articles].removeAllViews();
			views[articles].setVisibility(View.GONE);
		}
	}


	@Override
	public void onDestroy() {
		try {
			if (banner != null) {
				banner.handlerCancleAll();
			}
			if (client != null) {
				client.cancelAllRequests(true);
			}
			active.stopProgress();

			banner = null;
			navigation = null;
			active = null;
			video = null;
			hot = null;
			views=null;
		} catch (Exception e) {
		}
		super.onDestroy();
	}
}
