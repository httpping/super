package com.vp.loveu.my.activity;

import java.util.List;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.index.widget.EmptyTextView;
import com.vp.loveu.message.bean.PushNoticeBean;
import com.vp.loveu.message.bean.PushNoticeBean.BubbleType;
import com.vp.loveu.message.utils.BroadcastType;
import com.vp.loveu.my.adapter.MyCenterPlayListAdapter;
import com.vp.loveu.my.bean.MyCenterPayBean;
import com.vp.loveu.my.bean.MyCenterPayBean.MyCenterPayDataBean;
import com.vp.loveu.my.dialog.PlayFailedDialog;
import com.vp.loveu.my.dialog.PlaySuccessDialog;
import com.vp.loveu.my.listener.OnPayClickListener;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.MsgSharePreferenceUtil;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月20日上午10:20:18
 * @功能 我的支付页面
 * @作者 mi
 */

public class MyCenterPlayListActivity extends VpActivity implements OnRefreshListener2<ListView> {

	private final String FILE_NAME = "mycenter_pay";
	private PullToRefreshListView mListView;
	private Gson gson = new Gson();
	private List<MyCenterPayDataBean> mData;
	private MyCenterPlayListAdapter mAdapter;
	private int pageCode = 1;
	private int limit = 10;
	private MyReciive myReciive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_center_player_list_activity);
		initPublicTitle();
		initView();
		initData();
	}

	private void initData() {
		String readCache = CacheFileUtils.readCache(FILE_NAME);
		if (!TextUtils.isEmpty(readCache)) {
			setData(readCache);
		}
		if (MyUtils.isNetword(this)) {
			startHttp(pageCode);
		}
	}

	private void setData(String readCache) {
		mData = gson.fromJson(readCache, MyCenterPayBean.class).data;
		if (mAdapter == null) {
			mAdapter = new MyCenterPlayListAdapter(this, mListView.getRefreshableView(), mData, listener);
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

	private void startHttp(int page) {
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		RequestParams params = new RequestParams();
		params.put("id", LoginStatus.getLoginInfo().getUid());
		params.put("page", pageCode);
		params.put("limit", limit);
		mClient.get(VpConstants.MY_PALYER_LIST_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mListView.onRefreshComplete();
				String result = ResultParseUtil.deAesResult(responseBody);
				MyCenterPayBean fromJson = gson.fromJson(result, MyCenterPayBean.class);
				if (fromJson.code == 0) {
					if (pageCode == 1) {
						CacheFileUtils.writeCache(FILE_NAME, result);
						if (mData == null) {
							setData(result);
						} else {
							mData.clear();
							mData.addAll(fromJson.data);
							mAdapter.notifyDataSetChanged();
						}
					} else {
						if (fromJson.data == null || fromJson.data.size() <= 0) {
							Toast.makeText(getApplicationContext(), R.string.not_more_data, Toast.LENGTH_SHORT).show();
						} else {
							mData.addAll(fromJson.data);
							mAdapter.notifyDataSetChanged();
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

	private void initView() {
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText("我的支付");
		mListView = (PullToRefreshListView) findViewById(R.id.my_center_player_listview);
		mListView.setMode(Mode.BOTH);
		mListView.setOnRefreshListener(this);
		EmptyTextView tv = new EmptyTextView(this);
		tv.setWidth(MyUtils.getScreenWidthAndHeight(this)[0]);
		tv.setHeight(MyUtils.getScreenWidthAndHeight(this)[1]);
		tv.setGravity(Gravity.CENTER);
		tv.setText("你还没有任何支付信息");
		mListView.setEmptyView(tv);
		myReciive = new MyReciive();
		IntentFilter intentFilter = new IntentFilter(BroadcastType.PUSH_NOTICE_RECEVIE);
		registerReceiver(myReciive, intentFilter);
		listener = new OnPayClickListener() {
			@Override
			public void success(View v, MyCenterPayDataBean data) {
				playSuccessDialog = new PlaySuccessDialog(MyCenterPlayListActivity.this, data);
				playSuccessDialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						playSuccessDialog = null;
						dialog = null;
					}
				});
				playSuccessDialog.show();
			}

			@Override
			public void failed(View v, MyCenterPayDataBean data) {
				playFailedDialog = new PlayFailedDialog(MyCenterPlayListActivity.this, data);
				playFailedDialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						playFailedDialog = null;
						dialog = null;
					}
				});
				playFailedDialog.show();
			}
		};
	}

	public OnPayClickListener listener;
	private PlaySuccessDialog playSuccessDialog;
	private PlayFailedDialog playFailedDialog;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myReciive);
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

	public class MyReciive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String key = PushNoticeBean.BUBBLE_TYPE_KEY + BubbleType.join_activity.getValue();
			try {
				MsgSharePreferenceUtil msgSharePreferenceUtil = new MsgSharePreferenceUtil(MyCenterPlayListActivity.this, "push_bubble");
				String json = msgSharePreferenceUtil.getValueForKey(key);
				if (!TextUtils.isEmpty(json)) {
					JSONObject obj = new JSONObject(json);
					int status = obj.getInt("status");
					int id = obj.getInt("id");
					if (status == 90) {
						if (playSuccessDialog != null && playSuccessDialog.getOrderNumber() == id) {
							Toast.makeText(MyCenterPlayListActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
							MyCenterPayDataBean data = playSuccessDialog.getData();
							data.status = status;
							mAdapter.notifyDataSetChanged();
							if (playSuccessDialog.isShowing()) {
								playSuccessDialog.dismiss();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
