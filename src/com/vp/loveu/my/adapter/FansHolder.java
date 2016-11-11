package com.vp.loveu.my.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.channel.utils.ToastUtils;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.my.activity.UserIndexActivity;
import com.vp.loveu.my.bean.FollowBean.FollowData;
import com.vp.loveu.my.bean.UserInfoBean;
import com.vp.loveu.my.listener.OnFollowUserListener;
import com.vp.loveu.util.LoginStatus;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年1月5日上午10:22:17
 * @功能TODO
 * @作者 mi
 */

public class FansHolder extends BaseHolder<FollowData> {

	private ImageView mIvIcon;
	private TextView mTvName;
	private ImageView mIvSex;
	private TextView mTvDescript;
	private ImageView mIvFlag;
	private TextView mTvditance;
	private Button mIvButton;
	private OnFollowUserListener listener;

	public FansHolder(Context context) {
		super(context);
	}

	public FansHolder(Context context, OnFollowUserListener listener) {
		super(context);
		this.listener = listener;
	}

	@Override
	protected View initView() {
		return View.inflate(mContext, R.layout.nearby_listview_item, null);
	}

	@Override
	protected void findView() {
		mIvIcon = (ImageView) mRootView.findViewById(R.id.nearby_item_iv_icon);
		mTvName = (TextView) mRootView.findViewById(R.id.nearby_item_tv_name);
		mIvSex = (ImageView) mRootView.findViewById(R.id.nearby_item_tv_sex);
		mTvDescript = (TextView) mRootView.findViewById(R.id.nearby_item_tv_describe);
		mIvFlag = (ImageView) mRootView.findViewById(R.id.nearby_item_iv_flag);
		mTvditance = (TextView) mRootView.findViewById(R.id.nearby_item_tv_distance);
		mIvButton = (Button) mRootView.findViewById(R.id.nearby_item_ib_follow);
		mIvFlag.setVisibility(View.GONE);
		mTvditance.setVisibility(View.GONE);
		mIvButton.setVisibility(View.VISIBLE);
	}

	@Override
	protected void initData(FollowData bean) {
		ImageLoader.getInstance().displayImage(bean.portrait, mIvIcon, DisplayOptionsUtils.getOptionsConfig());
		mTvName.setText(bean.nickname);
		mIvSex.setImageResource(bean.sex == 1 ? R.drawable.man : R.drawable.woman);
		mTvDescript.setText(bean.sex == 1 ? "我想找个女朋友" : "我想找个男朋友");
		int status = bean.status;
		// 是否已关注，0=未关注, 2=已关注,4=被关注，6=相互关注
		final FollowData data = bean;
		if (status == 0 || status == 4) {
			mIvButton.setText("");
			mIvButton.setBackgroundResource(R.drawable.icon_follow);
			mIvButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 关注用户
					startFollowUser(data, false);
				}
			});
		} else if (status == 2 || status == 6) {
			mIvButton.setBackgroundResource(R.drawable.btn_follow_bg);
			mIvButton.setTextColor(Color.parseColor("#ffffff"));
			mIvButton.setText("已关注");
			mIvButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 取消关注
					startFollowUser(data, true);
				}
			});
		}
		mIvIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, UserIndexActivity.class);
				intent.putExtra(UserIndexActivity.KEY_UID, data.uid);
				mContext.startActivity(intent);
			}
		});
	}

	private VpHttpClient mClient;
	private Gson gson = new Gson();

	private void startFollowUser(final FollowData bean, final boolean isCancleFollow) {
		mIvButton.setEnabled(false);
		if (mClient == null) {
			mClient = new VpHttpClient(mContext);
			mClient.setShowProgressDialog(false);
		}
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		JsonObject obj = new JsonObject();
		obj.addProperty("from_uid", loginInfo.getUid());
		obj.addProperty("to_uid", bean.uid);
		mClient.post(isCancleFollow ? VpConstants.CANCLE_FOLLOW_USER : VpConstants.CHANNEL_USER_CREATE_FOLLOW, new RequestParams(),
				obj.toString(), new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						UserInfoBean fromJson = gson.fromJson(ResultParseUtil.deAesResult(responseBody), UserInfoBean.class);
						if (fromJson.code == 0) {
							// 取消成功
							if (isCancleFollow) {
								if (listener != null) {
									listener.cancleFollow(bean);
								}
							} else {
								// 关注成功
								ToastUtils.showTextToast(mContext, "关注成功");
								if (listener != null) {
									listener.addFollow(bean);
								}
							}
						} else {
							ToastUtils.showTextToast(mContext, fromJson.msg);
						}
						mIvButton.setEnabled(true);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						ToastUtils.showTextToast(mContext, mContext.getResources().getString(R.string.network_error));
						mIvButton.setEnabled(true);
					}
				});
	}
}
