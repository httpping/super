package com.vp.loveu.my.activity;

import com.ecloud.pultozoomview.PullToZoomScrollViewEx;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.bean.InwardAction;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.coupon.ui.MyCouponActivity;
import com.vp.loveu.index.activity.ArticleActivity;
import com.vp.loveu.index.activity.HotUserActivity;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.utils.DensityUtil;
import com.vp.loveu.my.bean.UserInfoBean;
import com.vp.loveu.my.bean.NewIntergralBean.NewIntergralDataBean;
import com.vp.loveu.my.bean.UserInfoBean.UserDataBean;
import com.vp.loveu.util.BitmapBlur;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.UIUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.onekeyshare.custom.ShareDialogFragment;
import cn.sharesdk.onekeyshare.custom.ShareModel;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月18日上午10:55:54
 * @功能 我的中心页面
 * @作者 mi
 */

public class MyCenterActivity extends VpActivity implements OnClickListener {

	private static final String FILE_NAME = "my_center.text";
	private ImageView mIvIcon;
	private TextView mTvGrade; // 等级头像右下角的标识
	private TextView mTvName;
	private TextView mTvAlias;
	private TextView mTvInteGral;
	private TextView mTvRank;// 活跃排名
	private LinearLayout mTvCollect;// 收藏
	private TextView mTvFlow;// 关注粉丝
	private TextView mTvEditInfo;// 修改资料
	private TextView mTvActiveNumber;
	private LinearLayout mLyIntergral;
	private LinearLayout mLyRanking;
	private View mPromoCodeLine;
	private ImageView mIvBack;
	private ImageView mIvShare;
	private ImageView mIvSetting;
	private ImageView mIvBackground;
	private Gson gson = new Gson();
	private LinearLayout mLyMyActive;// 我的活动
	private LinearLayout mLyMyDll;// 我的动态
	private LinearLayout mLyMyPlayer;// 我的支付
	private LinearLayout mLyMyMoney;// 我的钱包
	private LinearLayout mLyPromoManager;// 我的优惠码管理
	private LinearLayout mLyActiveSign;// 活动签到
	private UserDataBean mDatas;
	private SharedPreferencesHelper preferences;
	private PullToZoomScrollViewEx mNewScrollview;
	private ImageView zoomViewImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setContentView(R.layout.my_center_activity); //version 1.0
		setContentView(R.layout.my_new_userindex);
		mNewScrollview = (PullToZoomScrollViewEx) findViewById(R.id.my_new_scrollview);
		mNewScrollview.setBackgroundColor(getResources().getColor(R.color.bg_gray));
		mNewScrollview.setParallax(false);
		initNewScrollView();
		LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				DensityUtil.dip2px(this, 255));
		mNewScrollview.setHeaderLayoutParams(localObject);
		// initPublicTitle();
		// initView();
		// initClick();
		// initData();

		// 注册广播
		IntentFilter filter = new IntentFilter(IntergralActivity.ACTION);
		registerReceiver(receiver, filter);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			NewIntergralDataBean bean = (NewIntergralDataBean) intent.getSerializableExtra("obj");
			if (bean == null) {
				return;
			}
			mDatas.user_exp += bean.exp;
			mTvInteGral.setText(mDatas.user_exp + "");
		}
	};

	private void initNewScrollView() {
		View headView = LayoutInflater.from(this).inflate(R.layout.my_center_show_user_info, null, false);
		zoomViewImageView = new ImageView(this);
		zoomViewImageView.setScaleType(ScaleType.CENTER_CROP);
		View zoomView = zoomViewImageView;
		View contentView = LayoutInflater.from(this).inflate(R.layout.my_center_scrollview_content, null);
		mNewScrollview.setHeaderView(headView);
		mNewScrollview.setZoomView(zoomView);
		mNewScrollview.setScrollContentView(contentView);
		mNewScrollview.setPressed(true);
		initView();
		initClick();
		initData();
	}

	private void initData() {
		sp = SharedPreferencesHelper.getInstance(this);
		String readCache = CacheFileUtils.readCache(FILE_NAME);
		if (readCache != null && !TextUtils.isEmpty(readCache)) {
			// 走缓存
			setData(readCache);
		} else {
			String cacheJsonFromLocal = CacheFileUtils.getCacheJsonFromLocal(this, "mycentercache.txt");
			setData(cacheJsonFromLocal);
		}
		if (MyUtils.isNetword(this)) {
			// 网络畅通走网络
			startHttp();
		}
	}

	private void startHttp() {
		mClient = new VpHttpClient(this);
		RequestParams params = new RequestParams();
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		params.put("id", loginInfo.getUid());
		mClient.get(VpConstants.USER_INDEX_INFO_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				UserInfoBean fromJson = gson.fromJson(result, UserInfoBean.class);
				Log.d(tag, "success" + result);
				if (fromJson.code == 0) {
					// 写入缓存
					CacheFileUtils.writeCache(FILE_NAME, fromJson.data);
					setData(fromJson.data);
				} else {
					Toast.makeText(getApplicationContext(), fromJson.msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected void setData(String json) {
		mDatas = gson.fromJson(json, UserDataBean.class);
		if (mDatas == null) {
			return;
		}
		// ImageLoader.getInstance().displayImage(mDatas.portrait,
		// mIvBackground, DisplayOptionsUtils.getOptionsConfig());
		ImageLoader.getInstance().displayImage(mDatas.portrait, mIvIcon, DisplayOptionsUtils.getOptionsConfig());
		ImageLoader.getInstance().loadImage(mDatas.portrait, DisplayOptionsUtils.getOptionsConfig(), new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String arg0, View arg1) {

			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

			}

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				/*
				 * try { mIvBackground.setImageBitmap(BitmapBlur.doBlur(arg2,
				 * 12, false)); } catch (Exception e) { e.printStackTrace(); }
				 */
				new AsyncTask<Bitmap, String, Bitmap>() {

					@Override
					protected Bitmap doInBackground(Bitmap... params) {
						try {
							return BitmapBlur.doBlur(params[0], 12, false);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Bitmap result) {
						try {
							// zoomViewImageView.setImageBitmap(result);
							// result = null;

							Bitmap createScaledBitmap = Bitmap.createScaledBitmap(result,
									MyUtils.getScreenWidthAndHeight(MyCenterActivity.this)[0], UIUtils.dp2px(255), true);

							zoomViewImageView.setImageBitmap(createScaledBitmap);
							result.recycle();
							result = null;

						} catch (Exception e) {
						}
					}
				}.execute(arg2);
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {

			}
		});
		mTvName.setText(mDatas.nickname);
		mTvAlias.setText(mDatas.grade);
		mTvInteGral.setText(mDatas.user_exp + "");
		mTvRank.setText(mDatas.rank + "");
		mTvActiveNumber.setText("我的活动 (" + mDatas.activities_num + ")");
		int type = mDatas.type;
		sp.putIntegerValue("UserType", type);

		if (type == 4 || type == 8) {
			mTvGrade.setVisibility(View.VISIBLE);
			mTvGrade.setText("V" + mDatas.mentor_grade);
		} else {
			mTvGrade.setVisibility(View.GONE);
		}

		if ((type & 6) > 0) { //我的钱包 优惠码管理  特殊用户可见
			mLyMyMoney.setVisibility(View.VISIBLE);
			mPromoCodeLine.setVisibility(View.VISIBLE);
		} else {
			mLyMyMoney.setVisibility(View.GONE);
			mPromoCodeLine.setVisibility(View.GONE);
		}
		if ((type & 30) > 0) {
			mLyPromoManager.setVisibility(View.VISIBLE);
		} else {
			mLyPromoManager.setVisibility(View.GONE);
		}

//		 mLyMyMoney.setVisibility(View.VISIBLE);  
//		 mPromoCodeLine.setVisibility(View.VISIBLE);
//		 mLyPromoManager.setVisibility(View.VISIBLE);
	}

	public static final String ICON_KEY = "icon_key";
	public static final String NICKNAME_KEY = "nickname_key";
	private SharedPreferencesHelper sp;

	@Override
	protected void onStart() {
		super.onStart();
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo != null && !TextUtils.isEmpty(loginInfo.getPortrait())) {
			String iconPath = loginInfo.getPortrait();
			String nickname = loginInfo.getNickname();
			mTvName.setText(TextUtils.isEmpty(nickname) ? "" : nickname);
			ImageLoader.getInstance().displayImage(iconPath, mIvIcon, DisplayOptionsUtils.getOptionsConfig());
			ImageLoader.getInstance().loadImage(iconPath, DisplayOptionsUtils.getOptionsConfig(), new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
				}

				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				}

				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					new AsyncTask<Bitmap, String, Bitmap>() {
						@Override
						protected Bitmap doInBackground(Bitmap... params) {
							try {
								return BitmapBlur.doBlur(params[0], 12, false);
							} catch (Exception e) {
								e.printStackTrace();
							}
							return null;
						}

						@Override
						protected void onPostExecute(Bitmap result) {
							try {
								// ImageLoader.getInstance().cancelDisplayTask(mIvBackground);
								// mIvBackground.setImageBitmap(result);
								Bitmap createScaledBitmap = Bitmap.createScaledBitmap(result,
										MyUtils.getScreenWidthAndHeight(MyCenterActivity.this)[0], UIUtils.dp2px(255), true);

								zoomViewImageView.setImageBitmap(createScaledBitmap);
								result.recycle();
								result = null;
							} catch (Exception e) {
							}
						}
					}.execute(arg2);
				}

				@Override
				public void onLoadingCancelled(String arg0, View arg1) {

				}
			});
		}
	}

	private void initClick() {
		mLyMyPlayer.setOnClickListener(this);
		mTvFlow.setOnClickListener(this);
		mLyMyMoney.setOnClickListener(this);
		mIvBack.setOnClickListener(this);
		mIvShare.setOnClickListener(this);
		mIvSetting.setOnClickListener(this);
		mLyIntergral.setOnClickListener(this);
		mLyPromoManager.setOnClickListener(this);
		mLyActiveSign.setOnClickListener(this);
		mIvIcon.setOnClickListener(this);
		mLyRanking.setOnClickListener(this);
		mTvEditInfo.setOnClickListener(this);
		mTvCollect.setOnClickListener(this);
		mLyMyActive.setOnClickListener(this);
		mLyMyDll.setOnClickListener(this);
		mIvShare.setOnClickListener(this);
		mTvAlias.setOnClickListener(this);
	}

	private void initView() {
		mIvIcon = (ImageView) mNewScrollview.getPullRootView().findViewById(R.id.my_center_iv_logo);
		mTvGrade = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.my_center_tv_grade);
		mTvName = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.my_center_tv_name);
		mTvAlias = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.my_center_tv_alias);
		mTvInteGral = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.my_center_tv_integral);
		mTvRank = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.my_center_tv_hot_rank);
		mTvCollect = (LinearLayout) mNewScrollview.getPullRootView().findViewById(R.id.center);
		mTvFlow = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.my_center_bt_flow);
		mTvEditInfo = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.my_center_bt_editinfos);
		mLyMyActive = (LinearLayout) mNewScrollview.getPullRootView().findViewById(R.id.my_center_my_active);
		mLyMyDll = (LinearLayout) mNewScrollview.getPullRootView().findViewById(R.id.my_center_my_dll);
		mLyMyPlayer = (LinearLayout) mNewScrollview.getPullRootView().findViewById(R.id.my_center_my_palyer);
		mLyMyMoney = (LinearLayout) mNewScrollview.getPullRootView().findViewById(R.id.my_center_my_money);
		mLyActiveSign = (LinearLayout) mNewScrollview.getPullRootView().findViewById(R.id.my_center_active_sing);//活动签到
		mIvBack = (ImageView) findViewById(R.id.my_center_iv_back);
		mIvShare = (ImageView) findViewById(R.id.my_center_iv_share);
		mIvSetting = (ImageView) findViewById(R.id.my_center_iv_setting);
		mIvBackground = (ImageView) mNewScrollview.getPullRootView().findViewById(R.id.my_center_big_background);
		mTvActiveNumber = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.my_center_active_num);
		mPromoCodeLine = mNewScrollview.getPullRootView().findViewById(R.id.promo_code_line);
		mLyIntergral = (LinearLayout) mNewScrollview.getPullRootView().findViewById(R.id.my_center_integral);
		mLyRanking = (LinearLayout) mNewScrollview.getPullRootView().findViewById(R.id.my_center_ranking);
		mLyPromoManager = (LinearLayout) mNewScrollview.getPullRootView().findViewById(R.id.my_center_my_promo_code);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.my_center_my_palyer:
			// 我的支付
			startActivity(new Intent(this, MyCenterPlayListActivity.class));
			break;
		case R.id.my_center_bt_flow:
			// 关注/粉丝
			startActivity(new Intent(this, FansActivity.class));
			break;
		case R.id.my_center_my_money:
			// 我的钱包
			startActivity(new Intent(this, WalletActivity.class));
			break;
		case R.id.my_center_iv_back:
			// 返回
			finish();
			break;
		case R.id.my_center_iv_share:
			// 分享
			openShare();
			break;
		case R.id.my_center_iv_setting:
			// 设置
			startActivity(new Intent(this, SettingActivity.class));
			break;
		case R.id.my_center_active_sing:
			// 活动签到
			startActivity(new Intent(this, ActiveSignActivity.class));
			break;
		case R.id.my_center_integral:
			// 积分
			Intent intent = new Intent(this, IntergralActivity.class);
			intent.putExtra(IntergralActivity.KEY_GRADE, mDatas.grade);
			intent.putExtra(IntergralActivity.KEY_INTERGRAL, mDatas.user_exp);
			intent.putExtra(IntergralActivity.KEY_RANKING, mDatas.rank);
			startActivityForResult(intent, 100);
			break;
		case R.id.my_center_ranking:
			// 活跃排名
			startActivity(new Intent(this, HotUserActivity.class));
			break;
		case R.id.my_center_my_promo_code:
			// 优惠码管理
			startActivity(new Intent(this, MyCouponActivity.class));
			break;
		case R.id.my_center_bt_editinfos:
			// 修改资料
			startActivity(new Intent(this, MyInfoActivity.class));
			break;
		case R.id.center:
			// 收藏
			startActivity(new Intent(this, CollectActivity.class));
			break;
		case R.id.my_center_my_active:
			// 我的活动
			startActivity(new Intent(this, MyActiveActivity.class));
			break;
		case R.id.my_center_my_dll:
			// 动态
			startActivity(new Intent(this, MyDLLActivity.class));
			break;
		case R.id.my_center_tv_alias:
			// 积分规则
			/*
			 * Intent integral = new Intent(this,AboutActivity.class);
			 * integral.putExtra(AboutActivity.KEY, AboutActivity.INTERGRAL);
			 * startActivity(integral);
			 */
			InwardAction.parseAction(VpConstants.INTERGRAL).toStartActivity(this);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == 100 && arg1 == -1) {
			int intExtra = arg2.getIntExtra("integral", 0);
			if (intExtra > 0) {
				// 刷新ui
				mTvInteGral.setText(mDatas.user_exp + intExtra + "");
			}
		}
	}

	private void openShare() {
		ShareModel model = new ShareModel();
		int sex = mDatas.sex;
		String title = sex == 1 ? "想找一个女朋友" : "想找一个男朋友";
		model.setTitle(mDatas.nickname + "的个人主页," + title);
		model.setUrl(VpConstants.USER_WEB + mDatas.uid + "&app_is_installed=1");
		model.setImageUrl(mDatas.portrait);
		ShareDialogFragment dialog = new ShareDialogFragment();
		dialog.show(getSupportFragmentManager(), ArticleActivity.KEY_FLAG, model);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}
