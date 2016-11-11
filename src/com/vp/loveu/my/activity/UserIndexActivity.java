package com.vp.loveu.my.activity;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.onekeyshare.custom.ShareDialogFragment;

import com.ecloud.pultozoomview.PullToZoomScrollViewEx;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.channel.utils.ToastUtils;
import com.vp.loveu.comm.ShowImagesViewPagerActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.myutils.ArtUtils;
import com.vp.loveu.index.myutils.ArtUtils.OnArtFindCompleteListener;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.bean.UserInfo;
import com.vp.loveu.message.db.UserInfoDao;
import com.vp.loveu.message.ui.PersonSettingActivity;
import com.vp.loveu.message.ui.PrivateChatActivity;
import com.vp.loveu.message.utils.DensityUtil;
import com.vp.loveu.my.bean.SelectUserFollow;
import com.vp.loveu.my.bean.UserInfoBean;
import com.vp.loveu.my.bean.UserInfoBean.UserDataBean;
import com.vp.loveu.util.BitmapBlur;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.widget.CircleImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月30日下午6:02:11
 * @功能 用户个人主页O
 * @作者 mi
 */

public class UserIndexActivity extends VpActivity implements OnClickListener {

	public static final String KEY_UID = "key_uid";
	public final static String ADDRESS_KEY = "art_code_key";
	private CircleImageView mIvIcon;
	private String FILE_NAME = "userIndexactivity";
	private TextView mTvNickName;// 姓名
	private ImageView mIvBack;// 返回
	private ImageView mIvMore;// 更多
	private ImageView mIvBackground;
	private TextView mTvAlias;// 别名
	private TextView mTvSex;// 性别
	private TextView mTvHome;// 归属地
	private TextView mTvRule;// 择偶对象
	private Button mBtFollow;// 关注
	private TextView mBtMSN;// 私聊
	private View mLeftLine;
	private FrameLayout mFrameLayouMSG;
	private TextView mTvGrade;
	private RelativeLayout mRlDLL;// 她的动态
	private LinearLayout mPhotoContainer;
	private TextView mTvFlag;// 她的动态 ----他的动态
	private RelativeLayout mRlChanged;// 互换资料
	private LinearLayout mIvContainer;// 相片的容器
	private UserDataBean mDatas;
	private Gson gson = new Gson();
	private int status;
	private int uid;
	private SharedPreferencesHelper sp;
	private PullToZoomScrollViewEx mNewScrollview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uid = getIntent().getIntExtra(KEY_UID, -1);
		if (uid == -1) {
			Toast.makeText(this, "uid错误", Toast.LENGTH_SHORT).show();
			return;
		}
		// setContentView(R.layout.my_user_index_activity); // version 1.0

		setContentView(R.layout.my_new_userindex2);
		mNewScrollview = (PullToZoomScrollViewEx) findViewById(R.id.my_new_scrollview);
		initPullToScrollView();
		LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, UIUtils.dp2px(255));
		mNewScrollview.setHeaderLayoutParams(localObject);

		// initView();
		// initOnclick();
		// initData();
	}

	private ImageView zoomViewImageView;

	private void initPullToScrollView() {
		View headView = LayoutInflater.from(this).inflate(R.layout.you_center_show_user_info, null, false);
		zoomViewImageView = new ImageView(this);
		zoomViewImageView.setScaleType(ScaleType.CENTER_CROP);
		View zoomView = zoomViewImageView;
		View contentView = LayoutInflater.from(this).inflate(R.layout.my_new_scrollview_content, null, false);
		mNewScrollview.setHeaderView(headView);
		mNewScrollview.setZoomView(zoomView);
		mNewScrollview.setScrollContentView(contentView);
		mNewScrollview.setPressed(true);
		zoomViewImageView.getLayoutParams().height = UIUtils.dp2px(255);
		initView();
		initOnclick();
		initData();
	}

	private void initOnclick() {
		mIvBack.setOnClickListener(this);
		mIvMore.setOnClickListener(this);
		mRlChanged.setOnClickListener(this);
		mBtFollow.setOnClickListener(this);
		mFrameLayouMSG.setOnClickListener(this);
		mRlDLL.setOnClickListener(this);
		mTvAlias.setOnClickListener(this);
		ScrollView pullRootView = mNewScrollview.getPullRootView();
	}

	private void initView() {
		mIvIcon = (CircleImageView) mNewScrollview.getPullRootView().findViewById(R.id.user_index_iv_icon);
		mTvNickName = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.user_index_tv_name);
		mIvBack = (ImageView) findViewById(R.id.user_index_iv_back);
		mIvMore = (ImageView) findViewById(R.id.user_index_iv_more);
		mTvAlias = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.user_index_tv_alias);
		mTvSex = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.user_index_tv_sex);
		mTvHome = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.user_index_tv_home);
		mTvRule = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.user_index_tv_rule);
		mBtFollow = (Button) mNewScrollview.getPullRootView().findViewById(R.id.user_index_bt_follow);
		mBtMSN = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.user_index_bt_msn);
		mRlDLL = (RelativeLayout) mNewScrollview.getPullRootView().findViewById(R.id.user_index_rl_dll);
		mTvFlag = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.user_index_tv_flag);
		mRlChanged = (RelativeLayout) mNewScrollview.getPullRootView().findViewById(R.id.user_index_rl_changed);
		mIvBackground = (ImageView) mNewScrollview.getPullRootView().findViewById(R.id.user_index_big_background);
		mIvContainer = (LinearLayout) mNewScrollview.getPullRootView().findViewById(R.id.user_index_iv_container);
		mPhotoContainer = (LinearLayout) mNewScrollview.getPullRootView().findViewById(R.id.my_index_photo_container);
		mTvGrade = (TextView) mNewScrollview.getPullRootView().findViewById(R.id.user_index_tv_grade);
		mFrameLayouMSG = (FrameLayout) mNewScrollview.getPullRootView().findViewById(R.id.user_info_msg);
		mLeftLine = mNewScrollview.getPullRootView().findViewById(R.id.center_flag);
	}

	private void initData() {
		sp = SharedPreferencesHelper.getInstance(this);
		status = sp.getIntegerValue(FILE_NAME + uid, -1);
		String readCache = CacheFileUtils.readCache(FILE_NAME + uid);// 保证文件名称唯一
																		// 用户的uid加文件名
		if (!TextUtils.isEmpty(readCache) && status != -1) {
			setData(readCache);
		}

		if (MyUtils.isNetword(this)) {
			startHttp();
		}
	}

	private void startHttp() {
		mClient = new VpHttpClient(this);
		RequestParams params = new RequestParams();
		params.put("id", uid);
		params.put("from_uid", LoginStatus.getLoginInfo().getUid());
		mClient.get(VpConstants.USER_INDEX_INFO_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				UserInfoBean fromJson = gson.fromJson(result, UserInfoBean.class);
				if (fromJson.code == 0) {
					CacheFileUtils.writeCache(FILE_NAME + uid, result);
					// 检测用户是否被关注
					selectUserIsFollow(result);
				} else if (fromJson.code == 303) {// 禁止用户
					finish();
					Toast.makeText(getApplicationContext(), fromJson.msg, Toast.LENGTH_SHORT).show();
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

	protected void selectUserIsFollow(final String result) {
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		JsonObject obj = new JsonObject();
		obj.addProperty("from_uid", loginInfo.getUid());
		obj.addProperty("to_uid", uid);
		mClient.post(VpConstants.SELECT_FOLLOW_USER, new RequestParams(), obj.toString(), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				SelectUserFollow fromJson = gson.fromJson(new String(responseBody), SelectUserFollow.class);
				if (fromJson.code == 0) {
					status = fromJson.data.status;
					sp.putIntegerValue(FILE_NAME + uid, status);
					setData(result);
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

	private void setData(String readCache) {
		mDatas = gson.fromJson(gson.fromJson(readCache, UserInfoBean.class).data, UserDataBean.class);
		// 是否已关注，0=未关注, 2=已关注,4=被关注，6=相互关注
		if (status == 0 || status == 4) {
			mBtFollow.setText("+关注");
			mBtFollow.setTextColor(Color.parseColor("#10BB7D"));
			mBtFollow.setBackgroundResource(R.drawable.user_info_follow_bt_background);
		} else if (status == 2 || status == 6) {
			mBtFollow.setText("已关注");
			mBtFollow.setTextColor(Color.parseColor("#ffffff"));
			mBtFollow.setBackgroundResource(R.drawable.user_info_msg_bt_background);
		}

		// 0=未设置
		// 1=想找一个男朋友
		// 2=想找一个女朋友
		mTvRule.setText(mDatas.sex == 1 ? MyUtils.getStringForResID(this, R.string.dating_status_two)
				: MyUtils.getStringForResID(this, R.string.dating_status_one));

		int type = mDatas.type;
		if (type == 4 || type == 8) {// 专业导师 红娘 显示等级
			//version  1.2 
			mTvRule.setText("情感导师/资深红娘");
			
			mTvGrade.setVisibility(View.VISIBLE);
			mTvGrade.setText("V" + mDatas.mentor_grade);
		} else {
			mTvGrade.setVisibility(View.GONE);
		}
		
		
		mTvNickName.setText(mDatas.nickname);
		mTvFlag.setText(mDatas.sex == 1 ? "他的动态" : "她的动态");
		ImageLoader.getInstance().displayImage(mDatas.portrait, mIvIcon, DisplayOptionsUtils.getOptionsConfig());
		// ImageLoader.getInstance().displayImage(mDatas.portrait,zoomViewImageView,DisplayOptionsUtils.getOptionsConfig());
		// 毛玻璃效果
		ImageLoader.getInstance().loadImage(mDatas.portrait, DisplayOptionsUtils.getOptionsConfig(), new ImageLoadingListener() {
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
							return BitmapBlur.doBlur(params[0], 12, false); // TODO
																			// OOM
																			// 待优化
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
									MyUtils.getScreenWidthAndHeight(UserIndexActivity.this)[0], UIUtils.dp2px(255), true);

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

		mTvAlias.setText(mDatas.grade);
		mTvHome.setText(mDatas.sex == 1 ? "男" : "女");
		// 交友状态
		setAddressName();

		final List<String> photos = mDatas.photos;
		mIvContainer.removeAllViews();
		if (photos != null && photos.size() > 0) {
			mPhotoContainer.setVisibility(View.VISIBLE);
			for (int i = 0; i < photos.size(); i++) {
				LayoutParams params = new LinearLayout.LayoutParams(UIUtils.dp2px(65), UIUtils.dp2px(65));
				params.leftMargin = UIUtils.dp2px(10);
				ImageView iv = new ImageView(getApplicationContext());
				iv.setScaleType(ScaleType.CENTER_CROP);
				ImageLoader.getInstance().displayImage(photos.get(i), iv, DisplayOptionsUtils.getOptionsConfig());
				mIvContainer.addView(iv, params);
				final int position = i;
				iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(UserIndexActivity.this, ShowImagesViewPagerActivity.class);
						intent.putStringArrayListExtra(ShowImagesViewPagerActivity.IMAGES, (ArrayList<String>) photos);
						intent.putExtra(ShowImagesViewPagerActivity.POSITION, position);
						startActivity(intent);
					}
				});
			}
		} else {
			// 没有相册的情况下
			TextView tv = new TextView(this);
			tv.setText("暂无相片");
			LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			tv.setGravity(Gravity.CENTER);
			mIvContainer.addView(tv, params);
		}
	}

	/**
	 * 设置归属地信息 void
	 */
	private void setAddressName() {
		// 归属地
		if (TextUtils.isEmpty(mDatas.area_code)) {
			mTvSex.setVisibility(View.GONE);
			mLeftLine.setVisibility(View.GONE);
		} else {
			// 查询归属地
			new ArtUtils(this).startFindArtCode(mDatas.area_code, new OnArtFindCompleteListener() {
				@Override
				public void complete(final String provinceName, final String cityName) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (TextUtils.isEmpty(provinceName) && TextUtils.isEmpty(cityName)) {
								mTvSex.setVisibility(View.GONE);
								mLeftLine.setVisibility(View.GONE);
							} else {
								mTvSex.setVisibility(View.VISIBLE);
								mLeftLine.setVisibility(View.VISIBLE);
								mTvSex.setText(provinceName + cityName);
							}
						}
					});
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (mDatas == null) {
			return;
		}
		if (TextUtils.isEmpty(mDatas.nickname)) {
			return;
		}
		switch (v.getId()) {
		case R.id.user_index_iv_back:
			finish();
			break;
		case R.id.user_index_iv_more:
			// 更多
			UserInfo info = new UserInfo();
			info.userId = mDatas.uid + "";
			info.userName = mDatas.nickname;
			info.headImage = mDatas.portrait;
			info.xmppUser = mDatas.xmpp_user;
			UserInfoDao.getInstance(this).saveOrUpdate(info);
			Intent intent2 = new Intent(this, PersonSettingActivity.class);
			intent2.putExtra(PersonSettingActivity.PARAMS_KEY, info);
			intent2.putExtra(PersonSettingActivity.PARAMS_KEY_FROM, "my");
			startActivity(intent2);
			break;
		case R.id.user_index_bt_follow:
			// 是否已关注，0=未关注, 2=已关注,4=被关注，6=相互关注
			if (status == 0 || status == 4) {
				followUser(true);
			} else if (status == 2 || status == 6) {
				followUser(false);
			}

			break;
		case R.id.user_info_msg:
			// 私聊
			Intent chatIntent = new Intent(this, PrivateChatActivity.class);
			chatIntent.putExtra(PrivateChatActivity.CHAT_USER_ID, uid);
			chatIntent.putExtra(PrivateChatActivity.CHAT_USER_NAME, mDatas.nickname);
			chatIntent.putExtra(PrivateChatActivity.CHAT_USER_HEAD_IMAGE, mDatas.portrait);
			chatIntent.putExtra(PrivateChatActivity.CHAT_XMPP_USER, mDatas.xmpp_user);
			startActivity(chatIntent);
			break;
		case R.id.user_index_rl_dll:
			// 他的动态
			Intent dll = new Intent(this, MyDLLActivity.class);
			dll.putExtra(MyDLLActivity.UID, uid);
			dll.putExtra(MyDLLActivity.NICKNAME, mDatas.nickname);
			startActivity(dll);
			break;
		case R.id.user_index_rl_changed:
			// 婚恋资料
			Intent intent = new Intent(this, LoveInfoActivity.class);
			intent.putExtra(LoveInfoActivity.KEY_UID, uid);
			startActivity(intent);
			break;
		case R.id.user_index_tv_alias:
			// 积分规则
			Intent integral = new Intent(this, AboutActivity.class);
			integral.putExtra(AboutActivity.KEY, AboutActivity.INTERGRAL);
			startActivity(integral);
			break;
		default:
			break;
		}
	}

	private void followUser(final boolean isFollow) {// 是否关注
		mBtFollow.setEnabled(false);
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		JsonObject obj = new JsonObject();
		obj.addProperty("from_uid", LoginStatus.getLoginInfo().getUid());
		obj.addProperty("to_uid", uid);
		mClient.post(isFollow ? VpConstants.CHANNEL_USER_CREATE_FOLLOW : VpConstants.CANCLE_FOLLOW_USER, new RequestParams(),
				obj.toString(), new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						UserInfoBean fromJson = gson.fromJson(ResultParseUtil.deAesResult(responseBody), UserInfoBean.class);
						if (fromJson.code == 0) {
							if (isFollow) {
								mBtFollow.setText("已关注");
								// Toast.makeText(getApplicationContext(),
								// "关注成功", Toast.LENGTH_SHORT).show();
								mBtFollow.setTextColor(Color.parseColor("#ffffff"));
								mBtFollow.setBackgroundResource(R.drawable.user_info_msg_bt_background);
								ToastUtils.showTextToast(getApplicationContext(), "关注成功");
								status = 6;
							} else {
								mBtFollow.setText("+关注");
								mBtFollow.setTextColor(Color.parseColor("#10BB7D"));
								mBtFollow.setBackgroundResource(R.drawable.user_info_follow_bt_background);
								// Toast.makeText(getApplicationContext(),
								// "取消关注成功", Toast.LENGTH_SHORT).show();
								ToastUtils.showTextToast(getApplicationContext(), "取消关注成功");
								status = 0;
							}
						} else {
							ToastUtils.showTextToast(getApplicationContext(), "关注失败");
						}
						mBtFollow.setEnabled(true);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						ToastUtils.showTextToast(getApplicationContext(), "关注失败");
						mBtFollow.setEnabled(true);
					}
				});
	}
}
