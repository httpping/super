package com.vp.loveu.my.activity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.myutils.ArtUtils;
import com.vp.loveu.index.myutils.ArtUtils.OnArtFindCompleteListener;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.my.bean.LoveInfoBean;
import com.vp.loveu.my.bean.LoveInfoBean.LoveDataBean;
import com.vp.loveu.my.widget.WalletBottomItemRelativeLayout;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.widget.CustomProgressDialog;
import com.vp.loveu.widget.IOSActionSheetDialog;
import com.vp.loveu.widget.IOSActionSheetDialog.OnSheetItemClickListener;
import com.vp.loveu.widget.IOSActionSheetDialog.SheetItemColor;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月2日上午9:16:21
 * @功能 婚恋资料的界面
 * @作者 mi
 */

public class LoveInfoActivity extends VpActivity implements Runnable {
	public final static String KEY_UID = "key_uid";
	public final static String SHARE_KEY = "love_info";
	private Gson gson = new Gson();
	public LinearLayout mContainer;
	private int uid;
	private  boolean isEnable;
	private LoveDataBean mDatas;
	private IOSActionSheetDialog dialog;
	private String normalValue = "未设置";
	private SharedPreferencesHelper sp;
	private static String[] mKeyDatas;
	public static final String[] colors = new String[] { "#999999", "#000000" };
	private static final int[] valuesKey = new int[] { 0, 0, R.array.Constellation, R.array.ZodiacSign, R.array.Education, R.array.Salary,
			0, 0, R.array.Kid, R.array.Married, R.array.HasCar, R.array.House, R.array.BloodType, R.array.National_copy, R.array.Job, 2,
			0 };
	private static String[] jsonKey;
	private CustomProgressDialog createDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		uid = getIntent().getIntExtra(KEY_UID, -1);
		if (uid == -1) {
			Toast.makeText(this, "uid出错了", Toast.LENGTH_SHORT).show();
			return;
		}
		sp = SharedPreferencesHelper.getInstance(this);
		setContentView(R.layout.loveinfo_activity);
		initPublicTitle();
		initView();

		mPubTitleView.mBtnRight.setEnabled(false);
		if (uid == LoginStatus.getLoginInfo().getUid()) {// 如果等于自己的可以编辑 否则只能展示
			// 显示保存按钮并且可以点击
			isEnable = true;
			mPubTitleView.mBtnRight.setVisibility(View.VISIBLE);
			mPubTitleView.mBtnRight.setTextColor(Color.parseColor("#D8D8D8"));
		} else {
			isEnable = false;
			mPubTitleView.mBtnRight.setVisibility(View.GONE);
		}
		initData();
	}

	private void initData() {
		createDialog = CustomProgressDialog.createDialog(this);
		mKeyDatas = getResources().getStringArray(R.array.basic_info_name);// key
		jsonKey = getResources().getStringArray(R.array.basic_info_name_json_key);// json
		String readCache = SharedPreferencesHelper.getInstance(this).getStringValue(uid + "");
		
		createDialog.show();
		
		if (!TextUtils.isEmpty(readCache)) {
			setData(readCache);
		} else {
			// 本地缓存
			String cacheJsonFromLocal = CacheFileUtils.getCacheJsonFromLocal(this, "loveinfocache.txt");
			setData(cacheJsonFromLocal);
		}
		if (MyUtils.isNetword(this)) {
			startHttp();
		}
	}

	private void startHttp() {
		mClient = new VpHttpClient(this);
		mClient.setShowProgressDialog(false);
		mClient.get(VpConstants.LOVE_INFO_URL + uid, null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				LoveInfoBean fromJson = gson.fromJson(result, LoveInfoBean.class);
				LoveDataBean fromJson2 = gson.fromJson(fromJson.data, LoveDataBean.class);
				if (fromJson.code == 0) {
					if (fromJson2 != null && !fromJson2.toString().equals(new LoveInfoBean().new LoveDataBean().toString())) {
						SharedPreferencesHelper.getInstance(LoveInfoActivity.this).putStringValue(uid + "", fromJson.data);// 写入缓存
						setData(fromJson.data);
					}
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

	/**
	 * 根据index 找value
	 * 
	 * @param rsid
	 * @param id
	 */
	public String selectValue(int rsid, String typeId) {
		if (TextUtils.isEmpty(typeId)) {
			// 如果为空 绝对是未设置
			return normalValue;
		}
		try {
			Integer valueOf = Integer.valueOf(typeId);// 如果可以转换成功，说明是一个数据类型
			if (valueOf == 0) {
				// 如果为0 也绝对是未设置
				return normalValue;
			}

			if (rsid == 0) {
				// 说明是一个假象 直接返回typeId
				return typeId;
			}
			// 这里就是一种数据类型
			String[] stringArray = getResources().getStringArray(rsid);
			if (rsid == R.array.Salary) {
				return stringArray[valueOf - 10];
			} else {
				return stringArray[valueOf - 1];
			}
		} catch (Exception e) {
			// 如果出现异常 说明本来就是string
			return typeId;
		}
	}

	private void setData(String readCache) {
		mDatas = gson.fromJson(readCache, LoveDataBean.class);
		if (mDatas == null) {
			return;
		}
		new Thread(this).start();
	}

	/**
	 * 获取定位角标
	 * @param i
	 * @param string
	 * @return int
	 */

	private int getPosition(int resId, String value) {
		if (TextUtils.isEmpty(value)) {
			// 如果为空 绝对是未设置 不需要定位
			return 0;
		}
		try {
			Integer valueOf = Integer.valueOf(value);// 如果可以转换成功，说明是一个数据类型
			if (valueOf == 0) {
				// 如果为0 也绝对是未设置 不需要定位
				return 0;
			}
			if (resId != 0 && resId !=2) {
				// 有几个特殊的
				if (resId == R.array.Education) {// 学历
					if (valueOf == 5) {
						return 1;
					} else if (valueOf == 6) {
						return 2;
					} else if (valueOf == 9) {
						return 3;
					} else if (valueOf == 3) {
						return 0;
					}
				} else if (resId == R.array.Salary) {// 收入
					return valueOf - 10;
				}
				return valueOf - 1;
			} else {
				// 身高 体重 --- 其余的字符串
				if (resId == 2) {
					//归属地
					return valueOf;
				}
				return valueOf;
			}
		} catch (Exception e) {
			// 如果出现异常 说明本来就是string

			return 0;
		}
	}

	private String[] getValuesFromObject(LoveDataBean data) {
		return new String[] { data.uid + "", data.birthday, data.constellations + "", data.zodiak + "", data.edu + "", data.income + "",
				data.height + "", data.weight + "", data.child_status + "", data.marital_status + "", data.car_status + "",
				data.house_status + "", data.blood_type + "", data.nation + "", data.job + "", data.home_area_code, data.signature };
	}

	@Override
	protected void onStart() {
		super.onStart();
		String stringValue = sp.getStringValue(HeartShowActivity.KEY);
		if (!TextUtils.isEmpty(stringValue)) {
			WalletBottomItemRelativeLayout childAt = (WalletBottomItemRelativeLayout) mContainer.getChildAt(mContainer.getChildCount() - 1);
			childAt.setTvIntergral(stringValue);
		}
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText("婚恋资料");
		mPubTitleView.mBtnRight.setText("保存");
		mPubTitleView.mBtnRight.setTextColor(Color.parseColor("#10BB7D"));
		mPubTitleView.mBtnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPubTitleView.mBtnRight.isEnabled()) {
					showTipDialog();
				} else {
					finish();
				}
			}
		});
		mPubTitleView.mBtnRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!MyUtils.isNetword(getApplicationContext())) {
					// 如果没有联网，直接提示
					Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
					return;
				}
				dialog = new IOSActionSheetDialog(LoveInfoActivity.this).builder().setCancelable(false).setCanceledOnTouchOutside(false)
						.setTitle("是否保存设置的资料").setTitleColor(SheetItemColor.Gray)
						.addSheetItem("保存", SheetItemColor.Green, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						// 查看数据有没有修改过 如果有则开始保存 否则提示
						/*int childCount = mContainer.getChildCount();
						StringBuffer stringBuffer = new StringBuffer();
						for (int i = 0; i < childCount; i++) {
							WalletBottomItemRelativeLayout childAt = (WalletBottomItemRelativeLayout) mContainer.getChildAt(i);
							stringBuffer.append(childAt.getTvValues());
						}*/
						/*
						 * if
						 * (stringBuffer.toString().equals(buffer.toString())) {
						 * Toast.makeText(getApplicationContext(),
						 * "资料没有被修改过,无需重复保存", Toast.LENGTH_SHORT).show();
						 * return; }
						 */

						saveLoveInof();
					}
				});
				dialog.show();
			}
		});
		mContainer = (LinearLayout) findViewById(R.id.loveinfo_ly_container);
	}

	private IOSActionSheetDialog addSheetItem2;

	private void showTipDialog() {
		addSheetItem2 = new IOSActionSheetDialog(LoveInfoActivity.this).builder().setCancelable(false).setCanceledOnTouchOutside(false)
				.setTitle("有资料未保存，是否退出").setTitleColor(SheetItemColor.Gray)
				.addSheetItem("退出", SheetItemColor.Green, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						finish();
					}
				});
		addSheetItem2.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mPubTitleView.mBtnRight.isEnabled()) {
				showTipDialog();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void saveLoveInof() {
		JsonObject obj = WalletBottomItemRelativeLayout.obj;
		obj.addProperty("uid", LoginStatus.getLoginInfo().getUid());
		mClient.setShowProgressDialog(true);
		mClient.post(VpConstants.LOVE_INFO_SAVE_URL, new RequestParams(), obj.toString(), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				LoveInfoBean fromJson = gson.fromJson(ResultParseUtil.deAesResult(responseBody), LoveInfoBean.class);
				if (fromJson.code == 0) {
					Toast.makeText(getApplicationContext(), "资料保存成功", Toast.LENGTH_SHORT).show();
					mPubTitleView.mBtnRight.setEnabled(false);
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

	public void notifyClick() {
		if (mPubTitleView.mBtnRight.isEnabled()) {
			return;
		}
		mPubTitleView.mBtnRight.setTextColor(Color.parseColor("#10BB7D"));
		mPubTitleView.mBtnRight.setEnabled(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		sp.removeKey(HeartShowActivity.KEY);
		System.gc();
	}

	@Override
	public void run() {
		
		synchronized (LoveInfoActivity.this) {
			
			syncRemoveViewContainer();
			
			String[] values = getValuesFromObject(mDatas);// values
			// id 出身日期 星座 生效 学历 薪水 身高 体重 有无子女 婚姻状况 购车情况 购房情况 血型 民族 职业 guanji
			// 内心独白

			for (int i = 0; i < mKeyDatas.length; i++) {
				final WalletBottomItemRelativeLayout item = new WalletBottomItemRelativeLayout(LoveInfoActivity.this);
				item.setPosition(getPosition(valuesKey[i], values[i]));
				item.setStringPosition(values[i]);
				item.setIsEnable(isEnable);// 设置是否可点击
				item.setIsShowTvTime(false);// 设置是否显示时间
				item.setTvTitle(mKeyDatas[i]);// 设置key
				item.setTvTitleColor(colors[1]);
				item.setTvIntergral(selectValue(valuesKey[i], values[i]));// 设置value
				if (i == 0) {
					item.setTvTitleColor(colors[0]);
					item.setTvValuesColor(colors[0]);// 设置第一个为不可修改
				} else {
					item.setTvValuesColor(colors[1]);
				}
				item.addJsonPreferences(jsonKey[i], values[i]);

				if (i == mKeyDatas.length - 2) {
					// guanji
					new ArtUtils(LoveInfoActivity.this).startFindArtCode(values[i], new OnArtFindCompleteListener() {
						@Override
						public void complete(final String provinceName, final String cityName) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									item.setTvIntergral(TextUtils.isEmpty(cityName) ? "广东" : cityName);
								}
							});
						}
					});

				}
				if (i == mKeyDatas.length - 1) {
					if (isEnable) {
						item.setTvBackground(); // 最后一个
						
						syncAddViewContainer(item);
					} else {
						final View view = View.inflate(LoveInfoActivity.this, R.layout.love_info_last_item_layout, null);
						TextView name = (TextView) view.findViewById(R.id.loveinfo_name);
						TextView value = (TextView) view.findViewById(R.id.loveinfo_value);
						name.setText(mKeyDatas[i]);
						value.setText(TextUtils.isEmpty(mDatas.signature) ? "暂无内心独白" : mDatas.signature);
						syncAddViewContainer(view);
					}

					if (createDialog.isShowing()) {
						createDialog.dismiss();
					}

				} else {
					syncAddViewContainer(item);
				}
			}
			;
		}
	}
	
	private void syncAddViewContainer(final View view){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mContainer.addView(view);
			}
		});
	}
	
	private void syncRemoveViewContainer(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mContainer.removeAllViews();
			}
		});
	}
}
