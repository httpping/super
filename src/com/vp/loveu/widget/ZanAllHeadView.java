package com.vp.loveu.widget;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.vp.loveu.R;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.login.bean.UserBaseInfoBean;
import com.vp.loveu.util.ScreenBean;
import com.vp.loveu.util.ScreenUtils;
import com.vp.loveu.util.UIUtils;

/**
 * 赞头像的view
 * 
 * @author tanping
 *
 */
public class ZanAllHeadView extends LinearLayout {
	/**
	 * 是否显示数字
	 */
	private boolean mIsShowNumView;

	/**
	 * 数量文字颜色
	 */
	private int mNumTextColor;

	/**
	 * 数量文字背景颜色
	 */
	private int mNumBgColor;

	/**
	 * 头像view是否平分空间
	 */
	private boolean mIsAverage;

	/**
	 * 头像大小
	 */
	private float mImageSize;

	/**
	 * 头像间距
	 */
	private float mImagePadding;

	/**
	 * 控件背景颜色
	 */
	private int mBgColor;

	/**
	 * 是否显示分割线
	 */
	private boolean mIsShowCutline;

	// <attr name="number_boder_color" format="color" />
	// <attr name="number_boder_size" format="dimension" />
	public LinearLayout mContentLayout;
	public int maxSize;

	public int imageWH;
	public int imgMargin;

	private List<UserBaseInfoBean> userInfos;
	// private boolean mIsShowCount=false;
	private DisplayImageOptions options;
	private Context mContext;
	private LinearLayout mPortraitCountContainer;
	public TextView mPortraitCount;
	private View mCutLine;

	private boolean isMakeAnnimation;// 是否第一个头像需要做动画效果

	// private int mPading;

	// private int mCustomWidth=0;

	public ZanAllHeadView(Context context) {
		this(context, null);
		this.mContext = context;
	}

	public ZanAllHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView(attrs);
	}

	private void initView(AttributeSet attrs) {
		setOrientation(LinearLayout.HORIZONTAL);
		inflate(getContext(), R.layout.view_portrait_item, this);
		mPortraitCount = (TextView) findViewById(R.id.portrait_count);
		mPortraitCountContainer = (LinearLayout) findViewById(R.id.portrait_count_container);
		mCutLine = findViewById(R.id.portrait_cut_line);

		TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.ZanAllHeadView);
		mIsShowNumView = ta.getBoolean(R.styleable.ZanAllHeadView_is_showNum, true);
		mNumTextColor = ta.getColor(R.styleable.ZanAllHeadView_number_text_color,
				getResources().getColor(R.color.portrait_view_border));
		mNumBgColor = R.color.white;
		// mNumBgColor=ta.getColor(R.styleable.ZanAllHeadView_number_bg_color,
		// R.color.white);
		mIsAverage = ta.getBoolean(R.styleable.ZanAllHeadView_is_average, false);
		mImageSize = ta.getDimension(R.styleable.ZanAllHeadView_image_size,
				getResources().getDimension(R.dimen.portrait_view_width));
		mImagePadding = ta.getDimension(R.styleable.ZanAllHeadView_image_padding,
				getResources().getDimension(R.dimen.portrait_view_padding));
		mBgColor = ta.getColor(R.styleable.ZanAllHeadView_bg_color, 0xffffff);
		mIsShowCutline = ta.getBoolean(R.styleable.ZanAllHeadView_is_showCutLine, true);
		ta.recycle();

		mPortraitCount.setTextColor(mNumTextColor);
		android.view.ViewGroup.LayoutParams params = mPortraitCount.getLayoutParams();
		params.width = (int) mImageSize;
		params.height = (int) mImageSize;

		params = mCutLine.getLayoutParams();
		params.height = (int) mImageSize;
		mCutLine.setPadding((int) mImagePadding, 0, 0, 0);
		mCutLine.setVisibility(mIsShowCutline ? View.VISIBLE : View.GONE);

		if (isInEditMode()) {// 编辑模式不需要在往下走
			return;
		}

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.color.frenchgrey) // resource
																							// or
				.showImageForEmptyUri(R.drawable.default_portrait) // resource
																	// or
				.showImageOnFail(R.drawable.default_portrait) // resource or
				.resetViewBeforeLoading(false) // default
				.cacheInMemory(true) // default
				.cacheOnDisk(true) // default
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(false) // default
				.displayer(new SimpleBitmapDisplayer()).build();

		ScreenUtils.initScreen((Activity) getContext());

		setPadding((int) mImagePadding, (int) mImagePadding, (int) mImagePadding, (int) mImagePadding);// 设置padiing

		// setBackgroundResource(mBgColor);
		setBackgroundColor(mBgColor);

		mContentLayout = (LinearLayout) findViewById(R.id.head_content);

		// computer();//计算最多放多少
	}
	
	public void setDatas(List<UserBaseInfoBean> lists) {
		computer();// 计算最多放多少
		if (lists == null || lists.size() == 0) {
			setVisibility(View.GONE);
			return;
		}
		this.userInfos = lists;
		this.mPortraitCountContainer.setVisibility(this.mIsShowNumView ? View.VISIBLE : View.GONE);
		if (this.userInfos != null && this.userInfos.size() > 0) {
			createView();
		}
		if (lists != null && lists.size() < 100) {
			int m = (int) getResources().getDimension(R.dimen.portrait_view_text_size14);
			int s = UIUtils.px2dp(m);
			mPortraitCount.setTextSize(s);
		} else if (lists != null && lists.size() >= 100) {
			int m = (int) getResources().getDimension(R.dimen.portrait_view_text_size12);
			int s = UIUtils.px2dp(m);
			mPortraitCount.setTextSize(s);
		}
		mPortraitCount.setText(lists.size() + "");
	}

	/**
	 * 计算
	 */
	public void computer() {
		imageWH = (int) this.mImageSize;
		imgMargin = (int) this.mImagePadding;
		maxSize = (ScreenBean.screenWidth - (int) this.mImagePadding) / (imgMargin + imageWH);

	}

	// 设置图片大小
	public void setIvWidth(int width) {
		imageWH = width;
	}

	public void createView() {

		if (this.mIsShowNumView) {
			maxSize = maxSize - 1;
		}

		if (this.userInfos.size() < maxSize)
			maxSize = this.userInfos.size();

		for (int i = 0; i < maxSize; i++) {
			Log.d("tag", "size:" + maxSize);
			final CircleImageView imageView = new CircleImageView(getContext());
			String url = userInfos.get(i).getPortrait();

			if (isMakeAnnimation && i == 0) {
				imageView.setVisibility(View.GONE);
				ImageLoader.getInstance().displayImage(url, imageView, options, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
						arg1.setVisibility(View.VISIBLE);
//						 ScaleAnimation animation =new ScaleAnimation(0.0f, 1f, 0.0f, 1f,   
//									Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);   
//									animation.setDuration(2000);//设置动画持续时间 \
//									animation.setFillAfter(true);
//									arg1.startAnimation(animation);


					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}
				});
			} else {
				ImageLoader.getInstance().displayImage(url, imageView, options);
			}
			mContentLayout.addView(imageView);
			LayoutParams params = (LayoutParams) imageView.getLayoutParams();
			params.width = imageWH;
			params.height = imageWH;
			if (mIsAverage)
				params.weight = 1;
			if (i == 0 && !mIsShowNumView)
				params.leftMargin = 0;
			else
				params.leftMargin = imgMargin;
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setBorderWidth(0);

		}

	}
	
	public boolean ismIsShowNumView() {
		return mIsShowNumView;
	}

	public void setmIsShowNumView(boolean mIsShowNumView) {
		this.mIsShowNumView = mIsShowNumView;
	}

	public int getmNumTextColor() {
		return mNumTextColor;
	}

	public void setmNumTextColor(int mNumTextColor) {
		this.mNumTextColor = mNumTextColor;
	}

	public boolean ismIsAverage() {
		return mIsAverage;
	}

	public void setmIsAverage(boolean mIsAverage) {
		this.mIsAverage = mIsAverage;
	}

	public float getmImageSize() {
		return mImageSize;
	}

	public void setmImageSize(float mImageSize) {
		this.mImageSize = mImageSize;
	}

	public float getmImagePadding() {
		return mImagePadding;
	}

	public void setmImagePadding(float mImagePadding) {
		this.mImagePadding = mImagePadding;
	}

	public int getmBgColor() {
		return mBgColor;
	}

	public void setmBgColor(int mBgColor) {
		this.mBgColor = mBgColor;
	}

	public boolean ismIsShowCutline() {
		return mIsShowCutline;
	}

	public void setmIsShowCutline(boolean mIsShowCutline) {
		this.mIsShowCutline = mIsShowCutline;
		mCutLine.setVisibility(mIsShowCutline ? View.VISIBLE : View.GONE);
	}

	public boolean isMakeAnnimation() {
		return isMakeAnnimation;
	}

	public void setMakeAnnimation(boolean isMakeAnnimation) {
		this.isMakeAnnimation = isMakeAnnimation;
	}

}
