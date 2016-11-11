package com.vp.loveu.index.holder;

import java.io.File;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.nineoldandroids.animation.ObjectAnimator;
import com.vp.loveu.R;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.activity.FellHelpActivity;
import com.vp.loveu.index.activity.FreeHelpActivity;
import com.vp.loveu.index.bean.FellHelpBean.FellHelpBeanData;
import com.vp.loveu.index.bean.FellHelpVipPlayBean;
import com.vp.loveu.index.bean.FileBean;
import com.vp.loveu.index.bean.UpdateFellHelpBean;
import com.vp.loveu.index.bean.UpdateFellHelpBean.UpdateDataBean;
import com.vp.loveu.index.bean.UpdateFellHelpBean.UpdateFellHelp;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.utils.WxUtil;
import com.vp.loveu.pay.bean.PayBindViewBean.PayType;
import com.vp.loveu.pay.ui.PayActivity;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.widget.CustomProgressDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月19日上午9:26:57
 * @功能 情感求助上部分的holder
 * @作者 mi
 */

public class FellHelpTopHolder extends BaseHolder<FellHelpBeanData> implements OnClickListener {

	private static final String TAG = "FellHelpTopHolder";
	public static final String KEY_FLAG_BUNDLE = "key_bundle";
	public static final String KEY_FLAG_CONTENT = "key_content";
	public static final String KEY_FLAG_PHOTO = "key_photo";
	public static final String KEY_MAX_FLAG = "key_max";
	public static final String KEY_SRC_ID = "key_srcuid";
	public static final String KEY_USER_NUM = "key_user_num";
	private FellHelpActivity mActivity;
	private EditText mFellEditText;
	public ImageView mFellImageButton;
	private RelativeLayout mFellBtOne;// relativelayout
	private RelativeLayout mFellBtTwo;
	private RelativeLayout mFellBtThree;
	private ImageView mIvOne;
	private ImageView mIvTwo;
	private ImageView mIvThree;
	private TextView mTvOne;
	private TextView mTvTwo;
	private TextView mTvThree;
	private TextView mTvOneDesc;
	private TextView mFellTvMoney;
	private TextView mFellBtCommit;
	private TextView mFellTvNumber;
	private TextView mFellTvCurrentNumber;
	private TextView mFellTvCount;
	private LinearLayout mFellIvContainer;
	public ArrayList<String> mSelectPath;// 已选择图片
	final ArrayList<String> pictureList = new ArrayList<String>();
	private View mLine;
	private Gson gson = new Gson();
	private double mTalent_price;// 情感达人价格
	private double mExpert_price;// 专业导师价格
	private int mHelpCount;// 帮助过多少人
	private int mMax_answer_num;// 最多可免费在线解答的人数
	private int mCurrentPosition = 1;// 默认选中为1
	private VpHttpClient mClient;
	private CustomProgressDialog createDialog;

	public FellHelpTopHolder(Context context){
		super(context);
		mActivity = (FellHelpActivity) context;
	}

	@Override
	protected View initView() {
		return View.inflate(mContext, R.layout.fell_help_top_holder, null);
	}
	
	@Override
	protected void findView() {
		handler = new Handler();
		mFellEditText = (EditText) mRootView.findViewById(R.id.fell_help_top_ed_content);
		mFellImageButton = (ImageView) mRootView.findViewById(R.id.fell_help_top_bt_select_photo);
		mFellBtOne = (RelativeLayout) mRootView.findViewById(R.id.fell_help_top_btn_one);
		mFellBtTwo = (RelativeLayout) mRootView.findViewById(R.id.fell_help_top_btn_two);
		mFellBtThree = (RelativeLayout) mRootView.findViewById(R.id.fell_help_top_btn_three);
		mIvOne = (ImageView) mRootView.findViewById(R.id.fell_help_top_btn_one_iv_one);
		mIvTwo = (ImageView) mRootView.findViewById(R.id.fell_help_top_btn_two_iv_two);
		mIvThree = (ImageView) mRootView.findViewById(R.id.fell_help_top_btn_three_iv_three);
		mTvOneDesc = (TextView) mRootView.findViewById(R.id.fell_help_top_btn_one_tv_one);
		mTvOne = (TextView) mRootView.findViewById(R.id.fell_help_top_btn_one_tv_money);
		mTvTwo = (TextView) mRootView.findViewById(R.id.fell_help_top_btn_two_tv_money);
		mTvThree = (TextView) mRootView.findViewById(R.id.fell_help_top_btn_three_tv_money);
		mFellTvNumber = (TextView) mRootView.findViewById(R.id.fell_help_top_tv_number);
		mFellBtCommit = (TextView) mRootView.findViewById(R.id.fell_help_top_bt_commit);
		mFellTvCurrentNumber = (TextView) mRootView.findViewById(R.id.fell_help_top_tv_current_number);
		mFellIvContainer = (LinearLayout) mRootView.findViewById(R.id.fell_help_top_iv_container);
		mFellTvCount = (TextView) mRootView.findViewById(R.id.fell_help_top_tv_target_number);
		initOnClick();
	}

	@Override
	protected void initData(FellHelpBeanData data) {
		if (data == null) {
			return;
		}
		mTalent_price = data.talent_price;
		mExpert_price = data.expert_price;
		mHelpCount = data.asked_num;
		mMax_answer_num = data.max_answer_num;
		mTvOneDesc.setText("邀请" + mMax_answer_num + "位用户");
		mTvOne.setText("免费");
		mTvTwo.setText(mTalent_price + "元");
		mTvThree.setText(mExpert_price + "元");
		mFellTvNumber.setText(mHelpCount + "");
	}

	private boolean isFirstShow = true;

	private void initOnClick() {
		mFellBtOne.setOnClickListener(this);
		mFellBtTwo.setOnClickListener(this);
		mFellBtThree.setOnClickListener(this);
		mFellBtCommit.setOnClickListener(this);
		mFellEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				int length = s.length();
				mFellTvCurrentNumber.setText("" + length);
				if (length >= 400 && isFirstShow) {
					Toast.makeText(mContext, "文本最多输入400个哦", Toast.LENGTH_SHORT).show();
					isFirstShow = !isFirstShow;
				}
				if (length >= 390) {
					mFellTvCurrentNumber.setTextColor(mContext.getResources().getColor(R.color.pay_ok_bg));
					mFellTvCount.setTextColor(mContext.getResources().getColor(R.color.pay_ok_bg));
				} else {
					mFellTvCurrentNumber.setTextColor(Color.parseColor("#CCCCCC"));
					mFellTvCount.setTextColor(Color.parseColor("#CCCCCC"));
				}
				if (length < 400) {
					isFirstShow = true;
				}
			}
		});

		mFellEditText.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ViewParent parent = mFellEditText.getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(true);
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.fell_help_top_btn_one:
			mCurrentPosition = 1;
			mFellBtOne.setBackgroundColor(Color.parseColor("#15CCCCCC"));
			mIvOne.setImageResource(R.drawable.fell_icon_btn_selecter);
			mTvOne.setTextColor(Color.parseColor("#FF8000"));
			mTvOne.setVisibility(View.VISIBLE);
			mFellBtTwo.setBackgroundColor(Color.parseColor("#ffffff"));
			mIvTwo.setImageResource(R.drawable.fell_icon_btn_two);
			mTvTwo.setVisibility(View.INVISIBLE);
			mFellBtThree.setBackgroundColor(Color.parseColor("#ffffff"));
			mIvThree.setImageResource(R.drawable.fell_icon_btn_three);
			mTvThree.setVisibility(View.INVISIBLE);

			// oneAnimation();
			break;
		case R.id.fell_help_top_btn_two:
			mCurrentPosition = 2;
			mFellBtTwo.setBackgroundColor(Color.parseColor("#15CCCCCC"));
			mIvTwo.setImageResource(R.drawable.fell_icon_btn_two_selecter);
			mTvTwo.setTextColor(Color.parseColor("#FF8000"));
			mTvTwo.setVisibility(View.VISIBLE);
			mFellBtOne.setBackgroundColor(Color.parseColor("#ffffff"));
			mIvOne.setImageResource(R.drawable.fell_icon_btn_one);
			mTvOne.setVisibility(View.INVISIBLE);
			mFellBtThree.setBackgroundColor(Color.parseColor("#ffffff"));
			mIvThree.setImageResource(R.drawable.fell_icon_btn_three);
			mTvThree.setVisibility(View.INVISIBLE);

			// twoAnimation();
			break;
		case R.id.fell_help_top_btn_three:
			mCurrentPosition = 3;
			mFellBtThree.setBackgroundColor(Color.parseColor("#15CCCCCC"));
			mIvThree.setImageResource(R.drawable.fell_icon_btn_three_selecter);
			mTvThree.setTextColor(Color.parseColor("#FF8000"));
			mTvThree.setVisibility(View.VISIBLE);
			mFellBtOne.setBackgroundColor(Color.parseColor("#ffffff"));
			mIvOne.setImageResource(R.drawable.fell_icon_btn_one);
			mTvOne.setVisibility(View.INVISIBLE);
			mFellBtTwo.setBackgroundColor(Color.parseColor("#ffffff"));
			mIvTwo.setImageResource(R.drawable.fell_icon_btn_two);
			mTvTwo.setVisibility(View.INVISIBLE);

			// threeAnimation();
			break;
		case R.id.fell_help_top_bt_commit:
			if (mFellEditText.getText().toString().trim().length() > 400) {
				Toast.makeText(mContext, "文本最多输入400个哦", Toast.LENGTH_SHORT).show();
				break;
			} else if (mFellEditText.getText().toString().trim().length() <= 0) {
				Toast.makeText(mContext, "内容不能为空,请重新输入", Toast.LENGTH_SHORT).show();
				break;
			}
			if (!MyUtils.isNetword(mContext)) {
				Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
				break;
			}

			commit();
			break;
		default:
			break;
		}
	}

	private void commit() {
		if (mClient == null) {
			mClient = new VpHttpClient(mContext);
			mClient.setShowProgressDialog(false);
		}

		if (createDialog == null) {
			createDialog = CustomProgressDialog.createDialog(mActivity);
			createDialog.setCancelable(false);
			createDialog.setCanceledOnTouchOutside(false);
		}
		if (!createDialog.isShowing()) {
			createDialog.show();
		}
		// 先上传图片---成功后走数据----跳转下个页面
		if (mSelectPath != null && mSelectPath.size() > 0) {
			new Thread(){
				public void run() {
					Looper.prepare();
					looper = Looper.myLooper();
					uploadFilePicture(mClient);// 开始上传图片
					Looper.loop();
				};
			}.start();
		} else {
			// 图片为空直接上传内容数据
			startUpdateExtra(mClient, null);
		}
	}
	
	private Handler handler;
	private Looper looper;
	

	public void clearData() {
		mFellEditText.getText().clear();
		if (mSelectPath != null && mSelectPath.size() > 0) {
			mSelectPath.clear();
			mFellIvContainer.removeAllViews();
		}
		if (pictureList != null && pictureList.size() > 0) {
			pictureList.clear();
		}
	}

	/**
	 * 图片上传完以后 ，开始上传其余的数据
	 * 
	 * @param mClient
	 *            void
	 */
	private void startUpdateExtra(final VpHttpClient mClient, final ArrayList<String> pictures) {
		
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				
				if (createDialog.isShowing()) {
					createDialog.dismiss();
				}
				
				if (mCurrentPosition != 1) {
					// 跳转到收费页面
					startVip(mClient, pictures, false);
				} else {
					startVip(mClient, pictures, true);
				}
				
			}
		});
	}

	private void startVip(VpHttpClient mClient, final ArrayList<String> pictures, boolean isFree) {
		UpdateFellHelpBean bean = new UpdateFellHelpBean();
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		bean.uid = loginInfo.getUid();// 用户id
		if (isFree) {
			bean.type = 1;// 求助类型
			bean.price = 0; // 金额
		} else {
			bean.type = mCurrentPosition == 2 ? 2 : 3;// 求助类型
			bean.price = mCurrentPosition == 2 ? mTalent_price : mExpert_price;// 金额
		}
		bean.pay_type = 0;// 支付方式
		bean.cont = getEdittext();// 求助的内容
		if (pictures != null && pictures.size() > 0) {
			bean.pics = pictures;// 图片列表 --- 可选
		}
		bean.audio = "";// 求助的音频 --- 可选
		bean.audio_title = "";// 音频的标题 ---- 可选
		String json = gson.toJson(bean);
		if (!isFree) {
			// 如果不是免费的 吧数据带过去 这边不负责上传
			startVipActivity(mCurrentPosition == 2 ? mTalent_price : mExpert_price, json);
			return;
		}
		// 否则就是免费求助 需要自己上传数据
		mClient.post(VpConstants.SEND_HELP, new RequestParams(), json, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				UpdateFellHelp fromJson = gson.fromJson(result, UpdateFellHelp.class);
				if (fromJson.code == 0) {
					// 说明成功
					UpdateDataBean data = gson.fromJson(fromJson.data, UpdateDataBean.class);
					startFreeActivity(data);
				} else {
					Toast.makeText(mActivity, fromJson.msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(mActivity, R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 跳转到VIP求助的界面
	 * @param money
	 * @param pictures
	 * @param json
	 *            void
	 */
	
	FellHelpVipPlayBean vipPlayBean;
	public void startVipActivity(double money, String json) {
		Intent intent = new Intent(mContext, PayActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (vipPlayBean == null) {
			vipPlayBean = new FellHelpVipPlayBean();
		}
		vipPlayBean.content = getEdittext();
		ArrayList<String> arrayList = new ArrayList<String>();
		if (mSelectPath != null && mSelectPath.size() > 0) {
			for (int i = 0; i < mSelectPath.size(); i++) {
				arrayList.add(mSelectPath.get(i));
			}
		}
		vipPlayBean.mSelectPath = arrayList;
		vipPlayBean.payTitle = "塞红包";
		vipPlayBean.json = json;
		vipPlayBean.payMoney = money;
		vipPlayBean.payType = PayType.flee_help;
		intent.putExtra(PayActivity.PAY_PARAMS, vipPlayBean);
		mActivity.startActivityForResult(intent, 200);
		// 支付结果 TODO：

	}

	/**
	 * 跳转到免费求助的界面
	 * @param bean
	 * 
	 * @param money
	 *            void
	 */
	public void startFreeActivity(UpdateDataBean bean) {
		Intent intent = new Intent(mContext, FreeHelpActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putString(FellHelpTopHolder.KEY_FLAG_CONTENT, getEdittext());
		ArrayList<String> arrayList = new ArrayList<String>();
		if (mSelectPath != null && mSelectPath.size() > 0) {
			for (int i = 0; i < mSelectPath.size(); i++) {
				arrayList.add(mSelectPath.get(i));
			}
			bundle.putStringArrayList(FellHelpTopHolder.KEY_FLAG_PHOTO, arrayList);
		}
		bundle.putInt(FellHelpTopHolder.KEY_MAX_FLAG, mMax_answer_num);
		bundle.putInt(FellHelpTopHolder.KEY_SRC_ID, bean.src_id);// 求助id
		bundle.putInt(FellHelpTopHolder.KEY_USER_NUM, bean.user_num);//最多求助的人数
		intent.putExtra(FellHelpTopHolder.KEY_FLAG_BUNDLE, bundle);
		mActivity.startActivity(intent);
		clearData();
	}

	/**
	 * 图片上传
	 * 
	 * @param mClient
	 *            void
	 */
	private void uploadFilePicture(final VpHttpClient mClient) {
		final ArrayList<Integer> list = new ArrayList<Integer>();// 上传错误角标集合
		for (int i = 0; i < mSelectPath.size(); i++) {
			String path = mSelectPath.get(i);
			final long currentTimeMillis = System.currentTimeMillis();
			final File file = new File(mActivity.getCacheDir(), currentTimeMillis + ".jpg");// 临时压缩路径
			final int position = i;
			boolean transImage = WxUtil.transImage(path, file.getAbsolutePath(), 400, 800, 70);
			// 开始上传图片
			mClient.setShowProgressDialog(false);
			mClient.postFile(VpConstants.FILE_UPLOAD, VpConstants.FILE_UPLOAD_PATH_PIC_FILE, transImage ? file.getAbsolutePath() : path,
					true, true, true, new AsyncHttpResponseHandler(looper) {
						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							String result = ResultParseUtil.deAesResult(responseBody);
							FileBean fromJson = gson.fromJson(result, FileBean.class);
							if (fromJson.state == 1) {
								if (file.exists()) {
									file.delete();
								}
								// 成功
								pictureList.add(fromJson.url);
								// 解决异步回调 保证数据正确
							} else {
								// 失败
								list.add(position);
							}
							startOut(mClient, list);
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
							list.add(position);
							startOut(mClient, list);
						}

						/**
						 * 解决异步回掉
						 * 
						 * @param mClient
						 * @param list
						 *            void
						 */
						private void startOut(final VpHttpClient mClient, final ArrayList<Integer> list) {
							if (pictureList.size() + list.size() == mSelectPath.size()) {
								if (list.size() <= 0) {
									// 说明全部上传成功
									startUpdateExtra(mClient, pictureList);
								} else {
									// 说明有上传失败的
									StringBuilder count = new StringBuilder();
									for (int j = 0; j < list.size(); j++) {
										if (j == list.size() - 1) {
											count.append(list.get(j));
										} else {
											count.append(list.get(j)).append(",");
										}
										mSelectPath.remove(list.get(j));
									}
									Toast.makeText(mActivity, "第" + count.toString() + "张图片上传失败", Toast.LENGTH_SHORT).show();
									startUpdateExtra(mClient, pictureList);
								}
							}
						}
					});
		}
	}

	@SuppressLint("NewApi")
	private int getScreenWidth() {
		WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getRealMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}

	public void notifySelectImageView() {
		if (mSelectPath == null) {
			return;
		}
		mFellIvContainer.removeAllViews();
		for (int i = 0; i < mSelectPath.size(); i++) {
			String path = mSelectPath.get(i);
			ImageView iv = new ImageView(mContext);
			Bitmap bitmap = WxUtil.extractThumbNail(path, 200, 300, true);
			iv.setScaleType(ScaleType.CENTER_CROP);
			iv.setImageBitmap(bitmap);
			android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(UIUtils.dp2px(30), UIUtils.dp2px(30));
			layoutParams.leftMargin = UIUtils.dp2px(10);
			mFellIvContainer.addView(iv, layoutParams);
			final int position = i;
			iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActivity.showPhoto(mSelectPath, position);
				}
			});
		}
	}

	private void threeAnimation() {
		if (mCurrentPosition == 3) {
			// 说明点击的是当前的，不做任何处理
		} else if (mCurrentPosition == 1) {
			// 从最左边平移到最右边
			ObjectAnimator.ofFloat(mLine, "translationX", 0, getScreenWidth() - getScreenWidth() / 3).setDuration(300).start();
		} else if (mCurrentPosition == 2) {
			// 从中间平移到右边
			ObjectAnimator.ofFloat(mLine, "translationX", getScreenWidth() / 3, getScreenWidth() - getScreenWidth() / 3).setDuration(300)
					.start();
		}
		mFellTvMoney.setText("需支付" + mExpert_price + "元");
		mCurrentPosition = 3;
	}

	private void twoAnimation() {
		if (mCurrentPosition == 2) {
			// 说明点击的是当前的，不做任何处理
		} else if (mCurrentPosition == 1) {
			// 从左边平移到中间
			ObjectAnimator.ofFloat(mLine, "translationX", 0, getScreenWidth() / 3).setDuration(300).start();
		} else if (mCurrentPosition == 3) {
			// 从最右边平移到中间
			ObjectAnimator.ofFloat(mLine, "translationX", getScreenWidth() - getScreenWidth() / 3, getScreenWidth() / 3).setDuration(300)
					.start();
		}
		mFellTvMoney.setText("需支付" + mTalent_price + "元");
		mCurrentPosition = 2;
	}

	private void oneAnimation() {
		if (mCurrentPosition == 1) {
			// 说明点击的是当前的，不做任何处理
		} else if (mCurrentPosition == 2) {
			// 从中间移动到左边
			ObjectAnimator.ofFloat(mLine, "translationX", getScreenWidth() / 3, 0).setDuration(300).start();
		} else if (mCurrentPosition == 3) {
			// 从最右边移动到最左边
			ObjectAnimator.ofFloat(mLine, "translationX", getScreenWidth() - getScreenWidth() / 3, 0).setDuration(300).start();
		}
		mFellTvMoney.setText("免费");
		mCurrentPosition = 1;
	}

	public String getEdittext() {
		return mFellEditText.getText().toString().trim();
	}

	public boolean getContentIsEmpty() {
		return TextUtils.isEmpty(mFellEditText.getText().toString().trim());
	}
}
