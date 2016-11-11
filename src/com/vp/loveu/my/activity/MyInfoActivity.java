package com.vp.loveu.my.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.me.nereo.multi_image_selector.MultiImageSelectorActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.ShowImagesViewPagerActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.bean.FileBean;
import com.vp.loveu.index.myutils.ArtUtils;
import com.vp.loveu.index.myutils.ArtUtils.OnArtFindCompleteListener;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.login.ui.ClipImageActivity;
import com.vp.loveu.message.utils.WxUtil;
import com.vp.loveu.my.bean.MyInfoBean;
import com.vp.loveu.my.bean.MyInfoBean.MyInfoDataBean;
import com.vp.loveu.my.dialog.EditNameDialog;
import com.vp.loveu.my.dialog.EditNameDialog.OnChickedDialogListener;
import com.vp.loveu.my.widget.CustomGridView;
import com.vp.loveu.my.widget.WalletBottomItemRelativeLayout;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.widget.CustomProgressDialog;
import com.vp.loveu.widget.IOSActionSheetDialog;
import com.vp.loveu.widget.IOSActionSheetDialog.OnSheetItemClickListener;
import com.vp.loveu.widget.IOSActionSheetDialog.SheetItemColor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月1日下午3:18:31
 * @功能 我的资料的页面
 * @作者 mi
 */

public class MyInfoActivity extends VpActivity implements OnItemClickListener, OnClickListener, OnItemLongClickListener {

	private final String FILE_NAME = "myinfo.text";
	private LinearLayout mContainerOne;
	private LinearLayout mContainerTwo;
	private CustomGridView mGridView;
	private MyAdapter mAdapter;
	private PopupWindow pop;
	private View popView;
	private RelativeLayout mRlIcon;
	private RelativeLayout mRlNickName;
	private ImageView mIvIcon;
	private TextView mTvNickName;
	private Button mBtCamera;
	private Button mBtPhoto;
	private Button mBtCancle;
	private Gson gson = new Gson();
	private LinearLayout mPopuLayout;
	public ArrayList<String> mPhotoData = new ArrayList<String>();// 本地相册集合
	private ArrayList<String> mIconPhotoData = new ArrayList<String>();// 头像相册集合
	private ArrayList<String> newList = new ArrayList<String>();// 新的url集合
	private ArrayList<Integer> indexList = new ArrayList<Integer>();// 错误的角标集合
	public File file;
	private int CAMERA_CODE = 100;
	private int PHOTO_CODE = 200;
	private int ICON_CAMERA_CODE = 300;
	private int ICON_PHOTO_CODE = 400;
	private int ICON_PHOTO_CLIPIMAGEVIEW = 500;
	private int ICON_CAMERA_CLIPIMAGEVIEW = 600;
	public static final String[] namesOne = new String[] { "性别", "手机号码" };
	public static final String[] valuesOne = new String[] { "男", "132****4561" };
	public static final String[] namesTwo = new String[] { "所在地", "交友状态", "婚恋资料" };
	public static final String[] valuesTwo = new String[] { "广东", "找老公", ">" };
	public static final String[] colors = new String[] { "#999999", "#000000" };
	private MyInfoDataBean mDatas;
	private CustomProgressDialog createDialog;
	private AlertDialog dialogs;
	private IOSActionSheetDialog addSheetItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myinfo_activity);
		initPublicTitle();
		initView();
		initData();
	}

	private void initData() {
		createDialog = CustomProgressDialog.createDialog(this);
		createDialog.show();
		String readCache = CacheFileUtils.readCache(FILE_NAME);

		if (readCache != null && !TextUtils.isEmpty(readCache)) {
			setData(readCache);
		} else {
			// 走本地缓存
			String cacheJsonFromLocal = CacheFileUtils.getCacheJsonFromLocal(this, "myinfocache.txt");
			setData(cacheJsonFromLocal);
		}
		if (MyUtils.isNetword(this)) {
			startHttp();
		}
	}

	private void startHttp() {
		mClient = new VpHttpClient(this);
		mClient.setShowProgressDialog(false);
		RequestParams params = new RequestParams();
		params.put("id", LoginStatus.getLoginInfo().getUid());
		mClient.get(VpConstants.My_INFO, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				MyInfoBean fromJson = gson.fromJson(result, MyInfoBean.class);
				if (fromJson.code == 0) {
					MyInfoDataBean fromJson2 = gson.fromJson(fromJson.data, MyInfoDataBean.class);
					if (fromJson2 != null) {
						CacheFileUtils.writeCache(FILE_NAME, result);
						setData(result);
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
	 * 保存用户信息 void
	 */
	protected void saveUserInfo() {
		// 判断网络是否可用
		if (!com.vp.loveu.index.myutils.MyUtils.isNetword(this)) {
			Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!createDialog.isShowing()) {
			createDialog.show();
		}
		// 先上传图片---图片上传完成后再上传json
		if (mPhotoData.size() == 0) {
			// 没有图片上传
			upLoadJson(false);

		} else {
			// 有图片可上传
			new Thread() {
				public void run() {
					Looper.prepare();
					myLooper = Looper.myLooper();
					upLoadPicture(mPhotoData);
					Looper.loop();
				};
			}.start();
		}
	}

	private Looper myLooper;

	/**
	 * 开始保存数据 void
	 */
	private void upLoadJson(final boolean isImageList) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mIconPhotoData.size() == 0) {
					try {
						WalletBottomItemRelativeLayout.infoObj.put("portrait", mDatas.portrait);
						startHttpUpLoad(isImageList);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					// 有头像可上传
					upLoadIcon(mIconPhotoData, isImageList);
				}
			}
		});
	}

	private void startHttpUpLoad(boolean isImageList) {
		try {
			WalletBottomItemRelativeLayout.infoObj.put("nickname", mTvNickName.getText().toString().trim());// 昵称
			LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
			if (loginInfo == null) {
				loginInfo = new LoginUserInfoBean(this);
			}
			WalletBottomItemRelativeLayout.infoObj.put("uid", loginInfo.getUid());// uid
			if (isImageList) {
				JSONArray jsonArray = new JSONArray(newList);
				WalletBottomItemRelativeLayout.infoObj.put("photos", jsonArray);// 相册
			} else {
				ArrayList<Object> arrayList = new ArrayList<>();
				JSONArray jsonArray = new JSONArray(arrayList);
				WalletBottomItemRelativeLayout.infoObj.put("photos", jsonArray);// 相册
			}
			String json = WalletBottomItemRelativeLayout.infoObj.toString();
			if (mClient == null) {
				mClient = new VpHttpClient(this);
			}
			mClient.setShowProgressDialog(false);
			Log.d("aaa", "json" + json.toString());
			mClient.post(VpConstants.MY_SAVE_USER_INFO_RUL, new RequestParams(), json, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					MyInfoBean fromJson = gson.fromJson(new String(responseBody), MyInfoBean.class);
					if (fromJson.code == 0) {
						/*
						 * if (newList != null && newList.size() > 0) {
						 * mPhotoData.clear(); for (int i = 0; i <
						 * newList.size(); i++) {
						 * mPhotoData.add(newList.get(i)); }
						 * mAdapter.notifyDataSetChanged(); }
						 */
						Toast.makeText(getApplicationContext(), "资料保存成功", Toast.LENGTH_SHORT).show();
						mPubTitleView.mBtnRight.setEnabled(false);
					} else {
						Toast.makeText(getApplicationContext(), fromJson.msg, Toast.LENGTH_SHORT).show();
					}
					clearTempData();
					// 释放dialog
					if (createDialog.isShowing()) {
						createDialog.dismiss();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
					clearTempData();
					// 释放dialog
					if (createDialog.isShowing()) {
						createDialog.dismiss();
					}
				}
			});

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void clearTempData() {
		// 记得清除数据
		indexList.clear();// 错误的角标集合
		newList.clear();// url集合
		mIconPhotoData.clear();// 头像
	}

	/**
	 * 上传图片---得到上传后的url 然后再保存到服务器
	 * 
	 * @param data
	 * @return ArrayList<String>
	 */
	private void upLoadPicture(final ArrayList<String> data) {
		for (int i = 0; i < data.size(); i++) {
			String path = data.get(i);
			final int position = i;
			if (path.startsWith("http://") || path.startsWith("https://")) {
				// 说明是服务器传回来的数据 无需重复上传
				newList.add(path);
				if (i == data.size() - 1) {
					upLoadJson(true);
				}
			} else {
				// 是从本地选取的图片 ，需要上传给服务器
				String newPath = System.currentTimeMillis() + ".jpg";
				final File files = new File(getCacheDir(), newPath); // 临时解压路径
				if (mClient == null) {
					mClient = new VpHttpClient(MyInfoActivity.this);
				}
				boolean flag = WxUtil.transImage(path, files.getAbsolutePath(), 400, 800, 70);
				// 开始上传图片
				mClient.setShowProgressDialog(false);
				mClient.postFile(VpConstants.FILE_UPLOAD, VpConstants.FILE_UPLOAD_PATH_PIC_FILE, flag ? files.getAbsolutePath() : path,
						true, true, true, new AsyncHttpResponseHandler(myLooper) {

							@Override
							public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
								String result = ResultParseUtil.deAesResult(responseBody);
								FileBean fromJson = gson.fromJson(result, FileBean.class);
								if (fromJson.state == 1) {
									// 说明上传成功
									if (files.exists()) {
										// 删除临时文件
										files.delete();
									}
									newList.add(fromJson.url);// 上传后的url保存起来
								} else {
									// 说明上传失败
									indexList.add(position);
								}
								startOut(data);
							}

							@Override
							public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
								indexList.add(position);
								startOut(data);
							}
						});
			}
		}
	}

	/**
	 * 解决异步回掉
	 * 
	 * @param data
	 *            void
	 */
	private void startOut(final ArrayList<String> data) {
		if (newList.size() + indexList.size() == data.size()) {
			// 全部执行完毕
			if (indexList.size() == 0) {
				// 全部上传成功
				upLoadJson(true);
			} else {
				// 有上传失败的
				Collections.sort(indexList);
				StringBuilder count = new StringBuilder();
				for (int j = 0; j < indexList.size(); j++) {
					if (j == indexList.size() - 1) {
						count.append(indexList.get(j) + 1);
					} else {
						count.append(indexList.get(j) + 1).append(", ");
					}
					// 删除上传失败的图片
					data.remove(indexList.get(j));
				}
				Toast.makeText(MyInfoActivity.this, "第 " + count.toString() + " 张图片上传失败", Toast.LENGTH_SHORT).show();
				upLoadJson(true);
			}
		}
	}

	private String value;

	protected void setData(String data) {
		MyInfoBean fromJson = gson.fromJson(data, MyInfoBean.class);
		mDatas = gson.fromJson(fromJson.data, MyInfoDataBean.class);
		if (mDatas == null) {
			return;
		}

		mPhotoData.clear();
		List<String> photos = mDatas.photos;

		if (photos != null) {
			for (int i = 0; i < photos.size(); i++) {
				mPhotoData.add(photos.get(i));
			}
		}

		mAdapter = new MyAdapter();
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(MyInfoActivity.this);
		mGridView.setOnItemLongClickListener(MyInfoActivity.this);

		mTvNickName.setText(mDatas.nickname);
		ImageLoader.getInstance().displayImage(mDatas.portrait, mIvIcon, DisplayOptionsUtils.getOptionsConfig());
		valuesOne[0] = mDatas.sex == 1 ? "男" : "女";
		valuesOne[1] = mDatas.mt;
		mContainerOne.removeAllViews(); // 性别手机号码
		for (int i = 0; i < namesOne.length; i++) {
			WalletBottomItemRelativeLayout item = new WalletBottomItemRelativeLayout(this);
			item.setTvTitle(namesOne[i]);
			item.setIsShowTvTime(false);
			item.setTvValuesColor(colors[0]);
			item.setTvTitleColor(colors[0]);
			item.setTvIntergral(valuesOne[i]);
			if (i == namesOne.length - 1) {
				item.setIsShowLine(false);
			}
			mContainerOne.addView(item);
		}

		if (TextUtils.isEmpty(mDatas.area_code)) {
			valuesTwo[0] = "未设置";
			value = "";
			loadAddressName();
		} else {
			new ArtUtils(this).startFindArtCode(mDatas.area_code, new OnArtFindCompleteListener() {
				@Override
				public void complete(final String provinceName, final String cityName) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (TextUtils.isEmpty(cityName)) {
								valuesTwo[0] = "未设置";
							} else {
								valuesTwo[0] = cityName;
							}
							value = provinceName + cityName;
							loadAddressName();
						}
					});
				}
			});
		}
	}

	private void loadAddressName() {
		try {
			WalletBottomItemRelativeLayout.infoObj.put("area_code", mDatas.area_code);
			WalletBottomItemRelativeLayout.infoObj.put("dating_status", mDatas.dating_status);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (mDatas.dating_status == 0) {
			valuesTwo[1] = "未设置";
		} else {
			valuesTwo[1] = getResources().getStringArray(R.array.ChooseState)[mDatas.dating_status - 1];
		}
		mContainerTwo.removeAllViews();
		for (int i = 0; i < namesTwo.length; i++) {
			WalletBottomItemRelativeLayout item = new WalletBottomItemRelativeLayout(MyInfoActivity.this);
			item.setTvTitle(namesTwo[i]);
			item.setStringPosition(value);
			item.setIsShowTvTime(false);
			item.setTvValuesColor(colors[1]);
			item.setTvIntergral(valuesTwo[i]);
			if (i == namesTwo.length - 1) {
				item.setIsShowLine(false);
				item.setTvBackground();
			}
			if (i == 1) {
				item.setPosition(mDatas.dating_status == 0 ? 0 : mDatas.dating_status - 1);
			}
			mContainerTwo.addView(item);
		}

		if (createDialog.isShowing()) {
			createDialog.dismiss();
		}
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mBtnRight.setText("保存");
		// mPubTitleView.mBtnRight.setTextColor(Color.parseColor("#10BB7D"));
		mPubTitleView.mBtnRight.setTextColor(Color.parseColor("#D8D8D8"));
		mPubTitleView.mBtnRight.setEnabled(false);
		mPubTitleView.mTvTitle.setText("我的资料");
		mPubTitleView.mBtnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPubTitleView.mBtnRight.isEnabled()) {
					// 需要弹出提示，提示用户进行保存资料
					showTipDialog();
				} else {
					finish();
				}
			}
		});
		mPubTitleView.mBtnRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addSheetItem = new IOSActionSheetDialog(MyInfoActivity.this).builder().setCancelable(false).setCanceledOnTouchOutside(false)
						.setTitle("是否保存资料").setTitleColor(SheetItemColor.Gray)
						.addSheetItem("保存", SheetItemColor.Green, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						saveUserInfo();
					}
				});
				addSheetItem.show();
			}
		});

		mContainerOne = (LinearLayout) findViewById(R.id.myinfo_container_one);
		mContainerTwo = (LinearLayout) findViewById(R.id.myinfo_container_two);
		mGridView = (CustomGridView) findViewById(R.id.myinfo_gridview);
		mRlIcon = (RelativeLayout) findViewById(R.id.myinfo_rl_icon);
		mRlNickName = (RelativeLayout) findViewById(R.id.myinfo_rl_nickname);
		mIvIcon = (ImageView) findViewById(R.id.myinfo_iv_icon);
		mTvNickName = (TextView) findViewById(R.id.myinfo_tv_nickname);
		mRlIcon.setOnClickListener(this);
		mRlNickName.setOnClickListener(this);
		// defaultBitmap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.btn_photo);

	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			/*
			 * if (mPhotoData != null && mPhotoData.size() >= 5) { return 6; }
			 * return mPhotoData.size() + 1;
			 */

			if (mPhotoData != null && mPhotoData.size() >= 6) {
				return 6;
			}
			return mPhotoData.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			if (mPhotoData != null && mPhotoData.size() > 0) {
				return mPhotoData.get(position);
			}
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.myinfo_gridview_item, null);
				holder.mIvIcon = (ImageView) convertView.findViewById(R.id.myinfo_girdview_item_iv_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 加载数据
			if (mPhotoData.size() >= 6) {
				if (mPhotoData.get(position).startsWith("http://")) {
					ImageLoader.getInstance().displayImage(mPhotoData.get(position), holder.mIvIcon,
							DisplayOptionsUtils.getOptionsConfig());
				} else {
					Bitmap compressBitmap = MyUtils.compressBitmap(mPhotoData.get(position));
					holder.mIvIcon.setImageBitmap(compressBitmap);
				}
			} else {
				if (getCount() - 1 == position) {
					holder.mIvIcon.setImageDrawable(getResources().getDrawable(R.drawable.btn_photo));
				} else {
					if (mPhotoData.get(position).startsWith("http://")) {

						ImageLoader.getInstance().displayImage(mPhotoData.get(position), holder.mIvIcon,
								DisplayOptionsUtils.getOptionsConfig());
					} else {
						Bitmap compressBitmap = MyUtils.compressBitmap(mPhotoData.get(position));
						holder.mIvIcon.setImageBitmap(compressBitmap);
					}
				}
			}

			/*
			 * if (getCount() - 1 == position) {
			 * holder.mIvIcon.setImageResource(R.drawable.btn_photo); } else {
			 * if (mPhotoData.get(position).startsWith("http://")) {
			 * ImageLoader.getInstance().displayImage(mPhotoData.get(position),
			 * holder.mIvIcon, DisplayOptionsUtils.getOptionsConfig()); } else {
			 * Bitmap compressBitmap =
			 * MyUtils.compressBitmap(mPhotoData.get(position));
			 * holder.mIvIcon.setImageBitmap(compressBitmap); } }
			 */
			return convertView;
		}
	}

	private static class ViewHolder {
		public ImageView mIvIcon;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mPhotoData.size() >= 6) {
			// 浏览相片
			// mPubTitleView.mBtnRight.setEnabled(false);

			Intent intent = new Intent(this, ShowImagesViewPagerActivity.class);
			intent.putExtra(ShowImagesViewPagerActivity.IMAGES, mPhotoData);
			intent.putExtra(ShowImagesViewPagerActivity.POSITION, position);
			intent.putExtra(ShowImagesViewPagerActivity.SHOW_DELETE, true);
			startActivityForResult(intent, 2000);
		} else {
			if (mAdapter.getCount() - 1 == position) {
				// 弹出popuwindow 选择相片
				// showPopuWindow();
				showDialogs();
			} else {
				// 浏览相片
				// mPubTitleView.mBtnRight.setEnabled(false);

				Intent intent = new Intent(this, ShowImagesViewPagerActivity.class);
				intent.putExtra(ShowImagesViewPagerActivity.IMAGES, mPhotoData);
				intent.putExtra(ShowImagesViewPagerActivity.POSITION, position);
				intent.putExtra(ShowImagesViewPagerActivity.SHOW_DELETE, true);
				startActivityForResult(intent, 2000);
			}
		}

		/*
		 * if (mAdapter.getCount() - 1 == position) { // 弹出popuwindow 选择相片 //
		 * showPopuWindow(); showDialogs(); } else { // 浏览相片 Intent intent = new
		 * Intent(this, ShowImagesViewPagerActivity.class);
		 * intent.putExtra(ShowImagesViewPagerActivity.IMAGES, mPhotoData);
		 * intent.putExtra(ShowImagesViewPagerActivity.POSITION, position);
		 * startActivity(intent); }
		 */
	}

	private void showDialogs() {
		IOSActionSheetDialog dialog = new IOSActionSheetDialog(this).builder().setCancelable(false).setCanceledOnTouchOutside(false)
				.setTitle("选取相片").setTitleColor(SheetItemColor.Green)
				.addSheetItem("相机", SheetItemColor.Black, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						file = MyUtils.createTmpFile(MyInfoActivity.this);
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
						startActivityForResult(intent, CAMERA_CODE);
					}
				}).addSheetItem("相册", SheetItemColor.Black, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						Intent intent = new Intent(MyInfoActivity.this, MultiImageSelectorActivity.class);
						intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 6);
						intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
						intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mPhotoData);
						startActivityForResult(intent, PHOTO_CODE);
					}
				});
		dialog.show();
	}

	private void showPopuWindow() {
		if (pop == null) {
			createPopWindow();
		} else {
			if (pop.isShowing()) {
				return;
			}
		}

		Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.popuwindow_animation);
		mPopuLayout.startAnimation(loadAnimation);
		pop.showAtLocation(mGridView, Gravity.BOTTOM, 0, 0);
	}

	private void createPopWindow() {
		pop = new PopupWindow(this);
		pop.setFocusable(false);
		pop.setOutsideTouchable(false);
		pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		popView = LayoutInflater.from(this).inflate(R.layout.custom_popuwindow, null);
		pop.setContentView(popView);
		pop.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				pop = null;
			}
		});
		initPopView();
	}

	private void initPopView() {
		mBtCamera = (Button) popView.findViewById(R.id.custom_popuwindow_bt_camera);
		mBtPhoto = (Button) popView.findViewById(R.id.custom_popuwindow_bt_photo);
		mBtCancle = (Button) popView.findViewById(R.id.custom_popuwindow_bt_cancle);
		mPopuLayout = (LinearLayout) popView.findViewById(R.id.custom_popuwindow_ly);
		mBtCancle.setOnClickListener(this);
		mBtCamera.setOnClickListener(this);
		mBtPhoto.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v.equals(mBtCancle)) {
			if (pop.isShowing()) {
				pop.dismiss();
			}
		} else if (v.equals(mBtCamera)) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (intent.resolveActivity(getPackageManager()) != null) {
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				startActivityForResult(intent, CAMERA_CODE);
			} else {
				Toast.makeText(this, "没有系统相机", Toast.LENGTH_SHORT).show();
			}
		} else if (v.equals(mBtPhoto)) {
			// 相册选择
			Intent intent = new Intent(this, MultiImageSelectorActivity.class);
			intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 5);
			intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
			intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mPhotoData);
			startActivityForResult(intent, PHOTO_CODE = 200);
		} else if (v.equals(mRlIcon)) {
			// 选择头像
			selectIconPhoto();
		} else if (v.equals(mRlNickName)) {
			EditNameDialog editNameDialog = new EditNameDialog(MyInfoActivity.this, "编辑昵称", mTvNickName.getText().toString().trim(),
					new OnChickedDialogListener() {
						@Override
						public void onChicked(String content) {
							try {
								WalletBottomItemRelativeLayout.infoObj.put("nickname", content);
								mTvNickName.setText(content);
								LoginStatus.saveUserNickName(content);
								notifyClick();
							} catch (JSONException e) {
							}
						}
					});
			editNameDialog.show();
		}
	}

	private void selectIconPhoto() {
		IOSActionSheetDialog dialog = new IOSActionSheetDialog(this).builder().setCancelable(false).setCanceledOnTouchOutside(false)
				.setTitle("选择头像").setTitleColor(SheetItemColor.Green)
				.addSheetItem("相机", SheetItemColor.Black, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						file = MyUtils.createTmpFile(MyInfoActivity.this);
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
						startActivityForResult(intent, ICON_CAMERA_CODE);
					}
				}).addSheetItem("相册", SheetItemColor.Black, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						Intent intent = new Intent(MyInfoActivity.this, MultiImageSelectorActivity.class);
						intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
						intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
						// intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST,
						// mIconPhotoData);
						startActivityForResult(intent, ICON_PHOTO_CODE);
					}
				});
		dialog.show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == CAMERA_CODE) {
				// 相机返回
				/*
				 * if (pop.isShowing()) { pop.dismiss(); }
				 */
				mPhotoData.add(file.getAbsolutePath());
				mAdapter.notifyDataSetChanged();
				Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				Uri uri = Uri.fromFile(file);
				intent.setData(uri);
				sendBroadcast(intent);
				notifyClick();
			} else if (requestCode == PHOTO_CODE) {
				// 相册返回
				/*
				 * if (pop.isShowing()) { pop.dismiss(); }
				 */
				ArrayList<String> list = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
				if (list != null && list.size() > 0) {
					mPhotoData.clear();
					mPhotoData.addAll(list);
					mAdapter.notifyDataSetChanged();
				}

				notifyClick();
			} else if (requestCode == ICON_PHOTO_CODE) {
				ArrayList<String> list = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
				if (list != null && list.size() > 0) {
					Intent intent = new Intent(MyInfoActivity.this, ClipImageActivity.class);
					intent.putExtra("path", list.get(0));
					startActivityForResult(intent, ICON_PHOTO_CLIPIMAGEVIEW);
				}

				/*
				 * ArrayList<String> list =
				 * data.getStringArrayListExtra(MultiImageSelectorActivity.
				 * EXTRA_RESULT); if (list != null && list.size() > 0) { String
				 * path = list.get(0); Bitmap compressBitmap =
				 * MyUtils.compressBitmap(path);
				 * mIvIcon.setImageBitmap(compressBitmap);
				 * mIconPhotoData.clear(); mIconPhotoData.add(path); }
				 */

				notifyClick();
			} else if (requestCode == ICON_CAMERA_CODE) {

				Intent intents = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				Uri uris = Uri.fromFile(file);
				intents.setData(uris);
				sendBroadcast(intents);

				Intent intent = new Intent(MyInfoActivity.this, ClipImageActivity.class);
				intent.putExtra("path", file.getAbsolutePath());
				startActivityForResult(intent, ICON_PHOTO_CLIPIMAGEVIEW);

				/*
				 * Bitmap compressBitmap =
				 * MyUtils.compressBitmap(file.getAbsolutePath());
				 * mIvIcon.setImageBitmap(compressBitmap);
				 * mIconPhotoData.clear();
				 * mIconPhotoData.add(file.getAbsolutePath());
				 */

				notifyClick();
			} else if (requestCode == ICON_PHOTO_CLIPIMAGEVIEW) {
				// 裁剪后的头像
				String iconPath = data.getStringExtra("path");
				Bitmap decodeFile = BitmapFactory.decodeFile(iconPath);
				mIvIcon.setImageBitmap(decodeFile);
				mIconPhotoData.clear();
				mIconPhotoData.add(iconPath);
				Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				Uri parse = Uri.parse("file://" + iconPath);
				intent.setData(parse);
				sendBroadcast(intent);
				notifyClick();
			} else if (requestCode == 2000) {
				ArrayList<String> stringArrayListExtra = data.getStringArrayListExtra("result");
				if (stringArrayListExtra.size() != mPhotoData.size()) {
					notifyClick();
					mPhotoData.clear();
					for (int i = 0; i < stringArrayListExtra.size(); i++) {
						String string = stringArrayListExtra.get(i);
						mPhotoData.add(string);
					}
				}
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		/*
		 * if (mPhotoData.size() >= 6) { deleteDialog(position); return true;
		 * }else{ if (position != mAdapter.getCount() - 1) {
		 * deleteDialog(position); return true; } }
		 */

		// deleteDialog(position);

		/*
		 * if (position != mAdapter.getCount() - 1) { AlertDialog.Builder
		 * builder = new AlertDialog.Builder(this); final int index = position;
		 * builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		 * {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * mPhotoData.remove(index); mAdapter.notifyDataSetChanged();
		 * dialog.dismiss(); notifyClick(); } });
		 * builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		 * {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * dialog.dismiss(); } }); dialogs = builder.create();
		 * dialogs.setOnDismissListener(new DialogInterface.OnDismissListener()
		 * {
		 * 
		 * @Override public void onDismiss(DialogInterface dialog) { dialogs =
		 * null; dialog = null; } });
		 * dialogs.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		 * dialogs.setMessage("删除对话框"); dialogs.show(); return true; }
		 */
		return true;
	}

	private void deleteDialog(int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final int index = position;
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mPhotoData.remove(index);
				mAdapter.notifyDataSetChanged();
				dialog.dismiss();
				notifyClick();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialogs = builder.create();
		dialogs.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				dialogs = null;
				dialog = null;
			}
		});
		dialogs.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialogs.setMessage("删除对话框");
		dialogs.show();
	}

	public void upLoadIcon(final List<String> list, final boolean isImageList) {
		if (list == null || list.size() < 0) {
			return;
		}
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		final File createTmpFile = MyUtils.createTmpFile(this);
		boolean transImage = WxUtil.transImage(list.get(0), createTmpFile.getPath(), 360, 360, 100);
		mClient.setShowProgressDialog(false);
		mClient.postFile(VpConstants.FILE_UPLOAD, VpConstants.FILE_UPLOAD_PATH_PORTRAIT, transImage ? createTmpFile.getPath() : list.get(0),
				true, true, true, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						String result = ResultParseUtil.deAesResult(responseBody);
						FileBean fromJson = gson.fromJson(result, FileBean.class);
						if (fromJson.state == 1) {
							// 说明上传成功
							try {
								LoginStatus.saveUserPortrait(fromJson.url, list.get(0));
								WalletBottomItemRelativeLayout.infoObj.put("portrait", fromJson.url);
							} catch (JSONException e) {
								Toast.makeText(getApplicationContext(), "头像上传失败", Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(getApplicationContext(), "头像上传失败", Toast.LENGTH_SHORT).show();
						}
						startHttpUpLoad(isImageList);

						try {
							if (createTmpFile.exists()) {
								createTmpFile.delete();
							}
						} catch (Exception e) {
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						Toast.makeText(getApplicationContext(), "头像上传失败", Toast.LENGTH_SHORT).show();
						startHttpUpLoad(isImageList);
						try {
							if (createTmpFile.exists()) {
								createTmpFile.delete();
							}
						} catch (Exception e) {
						}
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mPubTitleView.mBtnRight.isEnabled()) {
				// 需要弹出提示，提示用户进行保存资料
				showTipDialog();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private IOSActionSheetDialog addSheetItem2;

	private void showTipDialog() {
		addSheetItem2 = new IOSActionSheetDialog(MyInfoActivity.this).builder().setCancelable(false).setCanceledOnTouchOutside(false)
				.setTitle("有资料未保存，是否退出").setTitleColor(SheetItemColor.Gray)
				.addSheetItem("退出", SheetItemColor.Green, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						finish();
					}
				});
		addSheetItem2.show();
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
		System.gc();
	}
}
