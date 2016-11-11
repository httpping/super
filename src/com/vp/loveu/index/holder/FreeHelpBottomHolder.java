package com.vp.loveu.index.holder;

import java.util.List;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.activity.FreeHelpActivity;
import com.vp.loveu.index.bean.FreeHelpBean;
import com.vp.loveu.index.bean.MySeekHelpBean;
import com.vp.loveu.index.bean.MySeekHelpBean.MySeekDataBean;
import com.vp.loveu.index.widget.FreeHelpBottomReplyRelativiLayout;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月20日下午4:38:09
 * @功能TODO
 * @作者 mi
 */

public class FreeHelpBottomHolder extends BaseHolder<FreeHelpBean> {

	private String tag = "FreeHelpBottomHolder";
	private TextView mFreeTvProgress;
	private LinearLayout mFreeContainer;
	private FreeHelpActivity mActivity;
	private VpHttpClient mClient;
	private Handler handler;
	private Gson gson = new Gson();
	private int last_id = 0;
	private int progress = 0;

	public FreeHelpBottomHolder(Context context) {
		super(context);
		mActivity = (FreeHelpActivity) context;
	}

	@Override
	protected View initView() {
		return View.inflate(mContext, R.layout.free_help_bottom_layout, null);
	}

	@Override
	protected void findView() {
		mFreeTvProgress = (TextView) mRootView.findViewById(R.id.free_help_bottom_tv_progress);
		mFreeContainer = (LinearLayout) mRootView.findViewById(R.id.free_help_bottom_reply_container);
	}

	@Override
	protected void initData(FreeHelpBean data) {
		mFreeTvProgress.setText("等待回复中(" + 0 + "/" + mData.maxNumb + ")...");
		mClient = new VpHttpClient(mActivity);
		mClient.setShowProgressDialog(false);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					if (progress != mData.userNum) {
						startHttp();
						sendEmptyMessageDelayed(1, 15 * 1000);
					} else {
						removeCallbacksAndMessages(null);
					}
					break;

				default:
					break;
				}
			}
		};
	}

	public void startHttp() {
		if (mClient == null) {
			mClient = new VpHttpClient(mContext);
			mClient.setShowProgressDialog(false);
		}
		RequestParams params = new RequestParams();
		params.put("id", mData.src_id);
		params.put("last_id", last_id);
		mClient.get(VpConstants.GET_FREE_FELL_HELP, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				MySeekHelpBean fromJson = gson.fromJson(result, MySeekHelpBean.class);
				if (fromJson.code == 0) {
					// 说明访问成功
					List<MySeekDataBean> data = fromJson.data;
					if (data != null && data.size() > 0) {
						for (int i = 0; i < data.size(); i++) {
							MySeekDataBean mySeekDataBean = data.get(i);
							FreeHelpBottomReplyRelativiLayout item = new FreeHelpBottomReplyRelativiLayout(mContext);
							item.setData(mySeekDataBean);
							mFreeContainer.addView(item);
							progress = progress + 1;
							if (i == data.size() - 1) {
								last_id = mySeekDataBean.id;
							}
						}
						Toast.makeText(mContext, "您收到了新的消息回复!", Toast.LENGTH_LONG).show();
						// mActivity.notifyDataSetChanged(progress);
						mFreeTvProgress.setText("等待回复中(" + progress + "/" + mData.maxNumb + ")...");
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
			}

		});
	}

	public void onStart() {
		handler.sendEmptyMessageDelayed(1, 15 * 1000);
	}

	public void onStop() {
		handler.removeCallbacksAndMessages(null);
	}

	public void onDestroy() {
		handler.removeCallbacksAndMessages(null);
	}
}
