package com.vp.loveu.index.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.bean.CityActiveBean;
import com.vp.loveu.index.bean.CityActiveBean.ActBean;
import com.vp.loveu.index.bean.CityActiveBean.DataBean;
import com.vp.loveu.index.holder.CityActiveListHolder;
import com.vp.loveu.index.holder.CityHistoryListItemHolder;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.index.widget.EmptyTextView;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.login.bean.UserBaseInfoBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.widget.ZanAllHeadView;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月17日下午4:35:12
 * @功能 同城活动的activity
 * @作者 mi
 */

public class CityActiveActivity extends VpActivity implements OnRefreshListener2<ScrollView> {

	private FrameLayout mActiveListContainer;
	private FrameLayout mActiveAttachContainer;
	private FrameLayout mActiveHistoryContainer;
	private LinearLayout mLyContainer;
	private TextView mTvFlag;
	private static final String FILE_NAME = "city_active.text";
	private DataBean mDatas;
	public Gson gson = new Gson();
	private CityActiveListHolder cityActiveListHolder;
	private CityHistoryListItemHolder cityHistoryListItemHolder;
	private ZanAllHeadView zanAllHeadView;
	private PullToRefreshScrollView mScrollView;
	public final static String KEY_ISSHOWMORE = "isShowMoreContent";
	private boolean isShowMoreContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cityactive_activity);
		isShowMoreContent = getIntent().getBooleanExtra(KEY_ISSHOWMORE, true);
		initPublicTitle();
		initView();
		initData();
	}

	private void initData() {
		// 先走缓存后走网络
		String readCache = CacheFileUtils.readCache(FILE_NAME);
		if (!TextUtils.isEmpty(readCache)) {
			setData(readCache);
		} else {
			/*String cacheJsonFromLocal = CacheFileUtils.getCacheJsonFromLocal(this, "cityactivecache.txt");
			setData(cacheJsonFromLocal);*/
		}

		if (MyUtils.isNetword(this)) {
			startHttp();
		}
	}

	private void startHttp() {
		mClient = new VpHttpClient(this);
		RequestParams params = new RequestParams();
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo != null && !TextUtils.isEmpty(loginInfo.getAdCode())) {
			params.put("area_code", loginInfo.getAdCode());
		}else{
			params.put("area_code", 4403 + "");
		}
		
		mClient.get(VpConstants.CITY_ACTIVE_LIST_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mScrollView.onRefreshComplete();
				String deAesResult = ResultParseUtil.deAesResult(responseBody);
				CityActiveBean fromJson = gson.fromJson(deAesResult, CityActiveBean.class);
				if (fromJson.code == 0) {
					// 说明访问成功
					CacheFileUtils.writeCache(FILE_NAME, deAesResult);
					setData(deAesResult);
				} else {
					Toast.makeText(CityActiveActivity.this, fromJson.msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mScrollView.onRefreshComplete();
				Toast.makeText(CityActiveActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}

	
	protected void setData(String deAesResult) {
		CityActiveBean fromJson = gson.fromJson(deAesResult, CityActiveBean.class);
		
		if (mLyContainer.getChildAt(0) instanceof EmptyTextView) {
			mLyContainer.removeViewAt(0);
		}
		
		if (fromJson == null || fromJson.data == null || 
				((fromJson.data.in_progress == null || fromJson.data.in_progress.activites == null 
				|| fromJson.data.in_progress.activites.size() == 0
				)&& (fromJson.data.history == null || fromJson.data.history.size() == 0))) {
			
			mActiveListContainer.setVisibility(View.GONE);
			mActiveHistoryContainer.setVisibility(View.GONE);
			mActiveAttachContainer.setVisibility(View.GONE);
			mTvFlag.setVisibility(View.GONE);
			
			EmptyTextView tv = new EmptyTextView(this);
			int[] screenWidthAndHeight = MyUtils.getScreenWidthAndHeight(this);
			tv.setWidth(screenWidthAndHeight[0]);
			tv.setHeight(screenWidthAndHeight[1]);
			tv.setGravity(Gravity.CENTER);
			tv.setText("暂时没有任何活动");
			mLyContainer.addView(tv, 0);
			return;
		}
		
		
		mActiveListContainer.setVisibility(View.VISIBLE);
		mActiveHistoryContainer.setVisibility(View.VISIBLE);
		mActiveAttachContainer.setVisibility(View.VISIBLE);
		
		mDatas = fromJson.data;
		// 正在进行的活动
		if (mDatas != null && mDatas.in_progress != null && mDatas.in_progress.activites != null
				&& mDatas.in_progress.activites.size() > 0) {
			mActiveListContainer.removeAllViews();
			cityActiveListHolder = new CityActiveListHolder(this);
			cityActiveListHolder.setData(mDatas.in_progress);
			mActiveListContainer.addView(cityActiveListHolder.getView());
			mActiveListContainer.setVisibility(View.VISIBLE);
			
			setHot(true);//显示报名用户
		} else {
			mActiveListContainer.removeAllViews();
			mActiveListContainer.setVisibility(View.GONE);
			
			setHot(false);//隐藏报名用户
		}
		
		
		if (isShowMoreContent) {
			// 往期活动 --- 需要显示的话才显示 否则隐藏
			mTvFlag.setVisibility(View.VISIBLE);
			if (mDatas != null && mDatas.history != null && mDatas.history.size() > 0) {
				mActiveHistoryContainer.removeAllViews();
				cityHistoryListItemHolder = new CityHistoryListItemHolder(this);
				cityHistoryListItemHolder.setData(mDatas.history);
				mActiveHistoryContainer.addView(cityHistoryListItemHolder.getView());
				mActiveHistoryContainer.setVisibility(View.VISIBLE);
			}else{
				mActiveHistoryContainer.removeAllViews();
				mActiveHistoryContainer.setVisibility(View.GONE);
				mTvFlag.setVisibility(View.GONE);
			}
		}else{
			mTvFlag.setVisibility(View.GONE);
		}
		
	}
	

	private void setHot(boolean isShow) {
		// 参加活动的用户
		if (isShow && mDatas != null && mDatas.in_progress!= null && mDatas.in_progress.users!= null && mDatas.in_progress.users.size() > 0) {
			mActiveAttachContainer.removeAllViews();
			List<ActBean> users = mDatas.in_progress.users;
			zanAllHeadView = new ZanAllHeadView(this);
			zanAllHeadView.setmImageSize(UIUtils.dp2px(40));
			zanAllHeadView.mPortraitCount.setTextColor(Color.parseColor("#FF7A00"));
			zanAllHeadView.setPadding(UIUtils.dp2px(15), 0, UIUtils.dp2px(10), 0);
			zanAllHeadView.setmIsShowCutline(false);
			ArrayList<UserBaseInfoBean> list = new ArrayList<UserBaseInfoBean>();
			LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
			if (loginInfo == null) {
				return;
			}
			for (int i = 0; i < users.size(); i++) {
				ActBean info = users.get(i);
				String nickName ="";
				String portrait = "";
				if (info.uid == loginInfo.getUid()) {
					nickName = loginInfo.getNickname();
					portrait = loginInfo.getPortrait();
				}else{
					nickName = info.nickname;
					portrait = info.portrait;
				}
				UserBaseInfoBean bean = new UserBaseInfoBean();
				bean.setName(nickName);
				bean.setPortrait(portrait);
				bean.setUid(info.uid);
				list.add(bean);
			}
			zanAllHeadView.setDatas(list);
			zanAllHeadView.mPortraitCount.setText(mDatas.in_progress.num + "");
			mActiveAttachContainer.addView(zanAllHeadView);
			mActiveAttachContainer.setVisibility(View.VISIBLE);
		}else{
			mActiveAttachContainer.removeAllViews();
			mActiveAttachContainer.setVisibility(View.GONE);
		}
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText("同城活动");
		mPubTitleView.mBtnRight.setText("深圳");
		mActiveListContainer = (FrameLayout) findViewById(R.id.city_active_list_container);
		mActiveAttachContainer = (FrameLayout) findViewById(R.id.city_active_attach_container);
		mActiveHistoryContainer = (FrameLayout) findViewById(R.id.city_active_history_container);
		mScrollView = (PullToRefreshScrollView) findViewById(R.id.city_active_scrollview);
		mLyContainer = (LinearLayout) findViewById(R.id.cityactive_container);
		mTvFlag = (TextView) findViewById(R.id.city_active_history_flag);
		mScrollView.setOnRefreshListener(this);
		ILoadingLayout up = mScrollView.getLoadingLayoutProxy(false, true);
		ILoadingLayout down = mScrollView.getLoadingLayoutProxy(true, false);
		 
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		startHttp();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

	}
}
