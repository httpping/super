package com.vp.loveu.index.holder;

import java.util.List;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.bean.MapLoactionBean;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.activity.HotUserActivity;
import com.vp.loveu.index.bean.IndexHotBean;
import com.vp.loveu.index.bean.IndexHotBean.IndexHotDataBean;
import com.vp.loveu.index.fragment.IndexFragment;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.widget.IndexHotItemView;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.MapLocationNetwork;
import com.vp.loveu.util.ScreenUtils;
import com.vp.loveu.util.UIUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月17日上午11:40:07
 * @功能 热门用户模块的holder
 * @作者 mi
 */

public class IndexHotHolder extends BaseHolder<List<IndexHotDataBean>> implements OnClickListener {

	private LinearLayout mHotContainer;
	private TextView mHotTvRank;
	private TextView mTvNews;
	
	public IndexHotHolder(Context context) {
		super(context);
	}

	@Override
	protected View initView() {
		return  View.inflate(mContext, R.layout.index_public_hot_show, null);
	}
	
	@Override
	protected void findView() {
		mHotContainer = (LinearLayout) mRootView.findViewById(R.id.index_hot_container);
		mHotTvRank = (TextView) mRootView.findViewById(R.id.index_hot_tv_rank);
		mTvNews = (TextView) mRootView.findViewById(R.id.index_hot_news);
		initClick();
	}

	private void initClick() {
		mHotTvRank.setOnClickListener(this);
		mTvNews.setOnClickListener(this);
	}
	
	
	@Override
	protected void initData(List<IndexHotDataBean> data) {
		mHotContainer.removeAllViews();
		mHotContainer.setPadding(UIUtils.dp2px(15), UIUtils.dp2px(20),UIUtils.dp2px(15),UIUtils.dp2px(20));
		for (int i = 0; i < data.size(); i++) {
			IndexHotDataBean bean = data.get(i);
			IndexHotItemView item = new IndexHotItemView(mContext);
			item.setData(bean);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1);
			layoutParams.gravity=Gravity.CENTER_HORIZONTAL;
			mHotContainer.addView(item,layoutParams);
		}
	}

	/**
	 * 可见时候的回掉
	 * @param loginInfo
	 * void
	 */
	public void onStart(LoginUserInfoBean loginInfo) {
			for (int i = 0; i < mData.size(); i++) {
				if(mData.get(i).uid == loginInfo.getUid()){
					IndexHotItemView item = (IndexHotItemView) mHotContainer.getChildAt(i);
					item.setData(mData.get(i));
				}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.index_hot_tv_rank:
			Intent intent = new Intent(mContext, HotUserActivity.class);
			mContext.startActivity(intent);
			break;
		case R.id.index_hot_news:
			//换一批
			startHotHttp();
			break;

		default:
			break;
		}
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MapLocationNetwork.MAP_RESULT_WHAT:
				MapLoactionBean mLoactionBean = (MapLoactionBean) msg.obj;
				try {
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
	
	private VpHttpClient client;
	private Gson gson = new Gson();
	
	private void startHotHttp() {
		if (client == null) {
			client = new VpHttpClient(mContext);
			client.setShowProgressDialog(true);
		}
		// http://api.loveu.com/api/v1/home/index_user?area_code={area_code}&lat={lat}&lng={lng}&uid={uid}
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		// 获取经纬度
		new MapLocationNetwork(handler, mContext);
		RequestParams params = new RequestParams();
		params.put("area_code", TextUtils.isEmpty(loginInfo.getAdCode()) ? "4403" : loginInfo.getAdCode());
		params.put("lat", loginInfo.lat);// 维度
		params.put("lng", loginInfo.lng);// 精度
		params.put("uid", loginInfo.getUid());// uid
		client.get(VpConstants.INDEX_HOT_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				Log.d("aaa", result);
				IndexHotBean fromJson = gson.fromJson(result, IndexHotBean.class);
				if (fromJson != null && fromJson.code == 0) {
					CacheFileUtils.writeCache(IndexFragment.FILE_NAME+"hot", result);//写入缓存
					List<IndexHotDataBean> data = fromJson.data;
					if (data != null) {
						setData(data);
					}
				}else{
					Toast.makeText(mContext, fromJson.msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
