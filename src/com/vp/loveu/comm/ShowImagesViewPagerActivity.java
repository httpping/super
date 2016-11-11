package com.vp.loveu.comm;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.viewpagerindicator.UnderlinePageIndicator;
import com.vp.loveu.R;
import com.vp.loveu.message.utils.ToastUtil;

/**
 * show images 传入图片列表 和 当前的位置， 用viewpager 的方式来滑动图片展示
 * 
 * @author tanping 2015-11-19
 */
public class ShowImagesViewPagerActivity extends Activity implements OnClickListener {

	public static final String IMAGES = "images";
	public static final String POSITION = "position";
	public static final String SHOW_DELETE = "showDelete";
	private boolean isShowDelete = false;

	private ViewPager mViewPager;
	private ImageView mDelete;
	public ArrayList<String> images;
	int position;
	private  int count ;
	DisplayImageOptions options;
	private LayoutInflater inflater;
	private SamplePagerAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

		setContentView(R.layout.public_show_images_activity);
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mDelete = (ImageView) findViewById(R.id.page_delete_btn);
		inflater = LayoutInflater.from(this);

		images = getIntent().getStringArrayListExtra(IMAGES);
		isShowDelete = getIntent().getBooleanExtra(SHOW_DELETE, false);
		if (isShowDelete) {
			mDelete.setVisibility(View.VISIBLE);
			mDelete.setOnClickListener(this);
		}
		position = getIntent().getIntExtra(POSITION, 0);
		if (images == null || images.size() <= 0) {
			ToastUtil.show(this, "参数错误");
			finish();
		}

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image_loading) // resource
																											// or
				.showImageForEmptyUri(R.drawable.default_image_loading_fail) // resource
																				// or
				.showImageOnFail(R.drawable.default_image_loading_fail) // resource
																		// or
				.resetViewBeforeLoading(false) // default
				.delayBeforeLoading(50).cacheInMemory(true) // default
				.cacheOnDisk(true) // default
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(false)// default
				.build();

		if (position < 0 || position >= images.size()) {
			position = 0;
		}
		mAdapter = new SamplePagerAdapter();
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(position);

		UnderlinePageIndicator mIndicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mViewPager);
	}

	class SamplePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {

			String uri = images.get(position) + "";

			if (!TextUtils.isEmpty(uri)) {
				if (uri.startsWith("http")) {
					// non
				} else {
					uri = "file://" + uri; // 本地
				}
			}

			View imageLayout = inflater.inflate(R.layout.public_show_image_item, null, false);
			assert imageLayout != null;
			PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			photoView.setOnViewTapListener(new OnViewTapListener() {
				@Override
				public void onViewTap(View view, float x, float y) {
					Intent intent = new Intent();
					intent.putStringArrayListExtra("result", images);
					setResult(Activity.RESULT_OK, intent);
					ShowImagesViewPagerActivity.this.finish();
				}
			});

			imageLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ShowImagesViewPagerActivity.this.finish();
				}
			});
			ImageLoader.getInstance().displayImage(uri, photoView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			container.addView(imageLayout, 0);

			/*
			 * PhotoView photoView = new PhotoView(container.getContext());
			 * 
			 * 
			 * ImageLoader.getInstance().displayImage(uri, photoView);
			 * photoView.setMinimumScale(1f); photoView.setZoomable(true);
			 * container.addView(photoView, LayoutParams.MATCH_PARENT,
			 * LayoutParams.MATCH_PARENT); photoView.setOnViewTapListener(new
			 * OnViewTapListener() {
			 * 
			 * @Override public void onViewTap(View view, float x, float y) {
			 * ShowImagesViewPagerActivity.this.finish();
			 * 
			 * } });
			 */

			return imageLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
		@Override
		public int getItemPosition(Object object) {
			return  POSITION_NONE;
		}

	}

	@Override
	public void onClick(View v) {
		if (v.equals(mDelete)) {
			deleteSelectedPhoto();
		}
	}

	Toast makeText;
	private void deleteSelectedPhoto() {
		if (images != null && mViewPager != null) {
			int currentItem = mViewPager.getCurrentItem();
			images.remove(currentItem);
			mAdapter.notifyDataSetChanged();
			if (makeText == null) {
				makeText = Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT);
			}
			makeText.show();
			if (images.size() == 0) {
				Intent intent = new Intent();
				intent.putStringArrayListExtra("result", images);
				setResult(Activity.RESULT_OK, intent);
				ShowImagesViewPagerActivity.this.finish();
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() ==  KeyEvent.ACTION_DOWN) {
			Intent intent = new Intent();
			intent.putStringArrayListExtra("result", images);
			setResult(Activity.RESULT_OK, intent);
			ShowImagesViewPagerActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
